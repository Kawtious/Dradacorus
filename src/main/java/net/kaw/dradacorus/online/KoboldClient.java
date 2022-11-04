/*
 * MIT License
 * 
 * Copyright (c) 2022 Kawtious
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.kaw.dradacorus.online;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kaw.dradacorus.discord.DiscordContainer;
import net.kaw.dradacorus.discord.DiscordHandler;
import net.kaw.dradacorus.online.sound.SoundData;
import net.kaw.dradacorus.online.sound.SoundTrack;
import net.kaw.dradacorus.online.utils.Dradacorus;
import net.kaw.dradacorus.online.utils.SocketHelper;
import net.kaw.dradacorus.utils.DragonConsole;

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
        DragonConsole.writeLine("Connected to " + ip + ":" + port);
    }

    private void playTrack(SoundData sndData) {
        try {
            File tempFile = File.createTempFile(sndData.getFileName(), "." + sndData.getFileExt());
            try (final FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(sndData.getStreamData());
            }
            tempFile.deleteOnExit();

            SoundTrack track =
                    new SoundTrack(tempFile, sndData.getVolume(), sndData.getCycleCount());

            track.setOnEndOfMedia(() -> {
                track.dispose();
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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

            // we have to authenticate this client, for that we setup the key and give a response to
            // the server
            setup(receive());

            Thread write = new Thread(() -> {
                while (connected) {
                    write();
                }
            });
            write.setDaemon(true);
            write.start();

            if (args != null && args.length > 0) {
                for (String arg : args) {
                    send(arg.getBytes());
                }
            }

            while (connected) {
                listen();
            }

            DragonConsole.writeLine("Disconnected from server");

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
        return new byte[0];
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
                DragonConsole.writeLine(str);
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
