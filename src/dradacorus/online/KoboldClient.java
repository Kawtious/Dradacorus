/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.online;

import dradacorus.discord.DiscordContainer;
import dradacorus.discord.DiscordHandler;
import dradacorus.online.utils.Dradacorus;
import dradacorus.online.utils.SocketHelper;
import dradacorus.utils.DragonConsole;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KoboldClient {

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

    public boolean run(String[] args) {
        if (ip.isEmpty()) {
            return false;
        }

        if (port < 0 || port > 65535) {
            return false;
        }

        return connect(ip, port, args);
    }

    private boolean connect(String ip, int port, String[] args) {
        try (final Socket socket = new Socket(ip, port)) {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            Dradacorus.verifyVersion();

            setup(receive());

            connected = true;

            DragonConsole.WriteLine(this.getClass(), "Connected to " + ip + ":" + port);

            Thread listen = listen();
            listen.setDaemon(true);
            listen.start();

            if (connected) {
                Thread write = write();
                write.setDaemon(true);
                write.start();

                if (args != null) {
                    if (args.length > 0) {
                        for (String arg : args) {
                            send(arg.getBytes());
                        }
                    }
                }
            }

            while (connected) {
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

    private Thread listen() {
        return new Thread(() -> {
            while (connected) {
                processMessage(receive());
            }
        });
    }

    private Thread write() {
        return new Thread(() -> {
            try (final Scanner scanner = new Scanner(System.in)) {
                while (connected) {
                    send(scanner.nextLine().getBytes());
                }
            }
        });
    }

    private void processMessage(byte[] message) {
        String str = new String(message);

        if (str.equals(SocketHelper.OBJ_INCOMING_HEADER)) {
            Object obj = SocketHelper.Input.getObjectFromBytes(receive());

            if (obj instanceof DiscordContainer) {
                if (DiscordHandler.isEnabled()) {
                    DiscordHandler.update((DiscordContainer) obj);
                }
            }
        } else {
            DragonConsole.WriteLine(this.getClass(), str);
        }
    }

    private byte[] receive() {
        try {
            return SocketHelper.Input.readBytes(dis, key);
        } catch (IOException ex) {
            Logger.getLogger(KoboldClient.class.getName()).log(Level.SEVERE, null, ex);
            disconnect();
        }
        return null;
    }

    private void send(byte[] message) {
        try {
            SocketHelper.Output.sendBytes(dos, message, key);
        } catch (IOException ex) {
            Logger.getLogger(KoboldClient.class.getName()).log(Level.SEVERE, null, ex);
            disconnect();
        }
    }

    private void setup(byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
        send(key);
    }

    private void disconnect() {
        connected = false;
    }

    public boolean connectDiscord() {
        return DiscordHandler.init("1033353477935599746");
    }

}
