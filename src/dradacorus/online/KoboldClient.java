/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.online;

import dradacorus.discord.DiscordContainer;
import dradacorus.discord.DiscordHandler;
import dradacorus.online.sound.SoundData;
import dradacorus.online.sound.SoundTrack;
import dradacorus.online.utils.Dradacorus;
import dradacorus.online.utils.SocketHelper;
import dradacorus.utils.DragonConsole;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KoboldClient implements IKoboldClient {

    private byte[] key = "$31$".getBytes();

    private DataInputStream dis;

    private DataOutputStream dos;

    private final String ip;

    private final int port;

    private volatile boolean connected = false;

    public KoboldClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private void setup(byte[] key) {
        this.key = key.clone();
        send(key);
        connected = true;
        DragonConsole.WriteLine(this.getClass(), "Connected to " + ip + ":" + port);
    }

    private void playTrack(SoundData sndData) {
        try {
            File tempFile = File.createTempFile(sndData.getFileName(), "." + sndData.getFileExt());
            try (final FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(sndData.getStreamData());
            }
            tempFile.deleteOnExit();

            SoundTrack track = new SoundTrack(tempFile, sndData.getVolume(), sndData.getCycleCount());

            track.setOnEndOfMedia(() -> {
                track.dispose();
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    tempFile.delete();
                } catch (InterruptedException ex) {
                    Logger.getLogger(KoboldClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            track.play();
        } catch (IOException ex) {
            Logger.getLogger(KoboldClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean connectDiscord() {
        return DiscordHandler.init("1033353477935599746");
    }

    @Override
    public boolean run(String[] args) {
        // validate all parameters for a proper connection
        if (ip.isEmpty()) {
            return false;
        }

        if (port < 0 || port > 65535) {
            return false;
        }

        // retrieve current version from GitHub repository and compare it to this instance's version
        Dradacorus.verifyVersion();

        return connect(ip, port, args);
    }

    @Override
    public boolean connect(String ip, int port, String[] args) {
        try (final Socket socket = new Socket(ip, port)) {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // we have to authenticate this client, for that we setup the key and give a response to the server 
            setup(receive());

            Thread write = new Thread(() -> {
                while (connected) {
                    write();
                }
            });
            write.setDaemon(true);
            write.start();

            if (args != null) {
                if (args.length > 0) {
                    for (String arg : args) {
                        send(arg.getBytes());
                    }
                }
            }

            while (connected) {
                listen();
            }

            DragonConsole.WriteLine(this.getClass(), "Disconnected from server");

            dis.close();
            dos.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(KoboldClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public void listen() {
        byte[] message = receive();
        processMessage(message);
    }

    @Override
    public byte[] receive() {
        try {
            return SocketHelper.Input.readBytes(dis, key);
        } catch (IOException ex) {
            disconnect();
        }
        return null;
    }

    @Override
    public void processMessage(byte[] data) {
        if (data == null || data.length <= 0) {
            return;
        }

        String str = new String(data);

        switch (str) {
            case SocketHelper.DISCORD_UPDATE_HEADER:
                DiscordContainer container = (DiscordContainer) SocketHelper.buildObject(receive());
                if (DiscordHandler.isEnabled()) {
                    DiscordHandler.update(container);
                }
                break;

            case SocketHelper.SOUND_REQUEST_HEADER:
                SoundData sndData = (SoundData) SocketHelper.buildObject(receive());
                playTrack(sndData);
                break;

            default:
                DragonConsole.WriteLine(this.getClass(), str);
                break;
        }
    }

    @Override
    public void write() {
        try (final Scanner scanner = new Scanner(System.in)) {
            while (connected) {
                send(scanner.nextLine().getBytes());
            }
        }
    }

    @Override
    public void send(byte[] message) {
        try {
            SocketHelper.Output.sendBytes(dos, message, key);
        } catch (IOException ex) {
            disconnect();
        }
    }

    @Override
    public void disconnect() {
        this.connected = false;
    }

}
