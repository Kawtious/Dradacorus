/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.online.client;

import dradacorus.online.utils.SocketHelper;
import dradacorus.utils.DragonConsole;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class DragonClient {

    private byte[] key = "$31$".getBytes();

    private DataInputStream dis;

    private DataOutputStream dos;

    private String ip;

    private int port;

    private volatile boolean connected = false;

    private String[] args;

    public DragonClient() {
    }

    public boolean run() {
        if (ip.isEmpty()) {
            return false;
        }

        if (port < 0 || port > 65535) {
            return false;
        }

        return connect(ip, port);
    }

    private boolean connect(String ip, int port) {
        try (final Socket socket = new Socket(ip, port)) {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            receive();

            if (connected) {
                Thread listen = listen();
                listen.setDaemon(true);
                listen.start();

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

            DragonConsole.Error.WriteLine("Client", "Disconnected from server");

            dis.close();
            dos.close();
            return true;
        } catch (IOException ex) {
            DragonConsole.Error.WriteLine("Client", "Disconnected: " + ex.getMessage());
        }

        return false;
    }

    private void receive() throws IOException {
        byte[] message = SocketHelper.readBytes(dis, key);

        if (!connected) {
            setup(message);
            connected = true;

            DragonConsole.WriteLine("Client", "Connected to " + ip + ":" + port);
        } else {
            DragonConsole.WriteLine("Client", new String(message));
        }
    }

    private void setup(byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
        send(key);
    }

    private Thread listen() {
        return new Thread(() -> {
            while (connected) {
                try {
                    receive();
                } catch (IOException ex) {
                    disconnect();
                }
            }
        });
    }

    private Thread write() {
        return new Thread(() -> {
            try (final Scanner scanner = new Scanner(System.in)) {
                while (connected) {
                    byte[] message = scanner.nextLine().getBytes();
                    if (message != null && message.length > 0) {
                        send(message);
                    }
                }
            }
        });
    }

    private void send(byte[] message) {
        try {
            SocketHelper.sendBytes(dos, message, key);
        } catch (IOException ex) {
        }
    }

    private void disconnect() {
        connected = false;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }

    public void setArgs(String[] args) {
        this.args = Arrays.copyOf(args, args.length);
    }

}
