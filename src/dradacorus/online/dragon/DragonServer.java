/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.online.dragon;

import dradacorus.online.kobold.IKoboldSocket;
import dradacorus.online.kobold.KoboldSocket;
import dradacorus.online.server.lairs.ILair;
import dradacorus.online.server.lairs.Lair;
import dradacorus.online.server.lairs.LairUtils;
import dradacorus.online.utils.SocketHelper;
import dradacorus.utils.DragonConsole;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DragonServer implements IDragonServer, ILair {

    private UUID id = UUID.randomUUID();

    private String name = "";

    private String password = "";

    private final List<IKoboldSocket> kobolds = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> operators = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> blacklist = Collections.synchronizedList(new ArrayList<>());

    private final List<ILair> lairs = Collections.synchronizedList(new ArrayList<>());

    private int port;

    private volatile boolean running = false;

    public DragonServer() {
    }

    @Override
    public boolean start() {
        if (port < 0 || port > 65535) {
            return false;
        }

        run(port);

        return true;
    }

    @Override
    public void run(int port) {
        try (final ServerSocket server = new ServerSocket(port)) {
            running = true;

            listen(server).start();

            DragonConsole.WriteLine("Server", "Dragon is now flying!");
            DragonConsole.WriteLine("Server", "Listening on port " + port);

            while (running) {
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public Thread listen(ServerSocket server) {
        return new Thread(() -> {
            while (running) {
                try {
                    Socket socket = server.accept();
                    IKoboldSocket kobold = createKoboldSocket(socket);

                    byte[] key = UUID.randomUUID().toString().getBytes("ISO-8859-1");

                    if (validate(kobold, key)) {
                        add(kobold);
                    }
                } catch (IOException ex) {
                }
            }
        });
    }

    @Override
    public IKoboldSocket createKoboldSocket(Socket socket) throws IOException {
        return new KoboldSocket(this, socket);
    }

    @Override
    public boolean validate(IKoboldSocket kobold, byte[] key) throws IOException {
        SocketHelper.send(kobold, key);
        kobold.setKey(key);

        if (Arrays.equals(kobold.listen(), kobold.getKey())) {
            DragonConsole.WriteLine("DragonServer", kobold.getSocket().getInetAddress() + " has connected");

            if (LairUtils.isIPBanned(blacklist, kobold)) {
                SocketHelper.send(kobold, "You are banned from this server");
                return false;
            }

            if (hasPassword()) {
                SocketHelper.send(kobold, "Input password to join");

                if (!Arrays.equals(kobold.listen(), password.getBytes())) {
                    SocketHelper.send(kobold, "Wrong password");
                    return false;
                }
            }

            SocketHelper.send(kobold, "Type /? or /help for a list of commands");
            return true;
        }

        return false;
    }

    @Override
    public void add(IKoboldSocket kobold) throws IOException {
        kobolds.add(kobold);
        kobold.setConnected(true);
        kobold.start();

        SocketHelper.sendDiscordUpdate(kobold);
    }

    @Override
    public void addKoboldToLair(ILair lair, IKoboldSocket kobold) {
        kobold.setLair(lair);
        lair.addKobold(kobold);
    }

    @Override
    public void createLair(IKoboldSocket kobold, String name) {
        createLair(kobold, name, "");
    }

    @Override
    public void createLair(IKoboldSocket kobold, String name, String password) {
        addLair(new Lair(this, name, password), kobold);
    }

    @Override
    public void addLair(ILair lair, IKoboldSocket kobold) {
        lairs.add(lair);

        kobold.setLair(lair);

        lair.addKobold(kobold);
        lair.op(kobold);
    }

    @Override
    public void removeLair(ILair lair) {
        lair.destroy();
        lairs.remove(lair);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<ILair> getLairs() {
        return Collections.unmodifiableList(lairs);
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void destroy() {
        System.exit(0);
    }

    @Override
    public void addKobold(IKoboldSocket kobold) {
        kobolds.add(kobold);
    }

    @Override
    public void kick(IKoboldSocket kobold) {
        kobold.disconnect();
    }

    @Override
    public void ban(IKoboldSocket kobold) {
        kick(kobold);
        blacklist.add(kobold);
    }

    @Override
    public void op(IKoboldSocket kobold) {
        operators.add(kobold);
    }

    @Override
    public void deop(IKoboldSocket kobold) {
        operators.remove(kobold);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean hasPassword() {
        return !password.isEmpty();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public List<IKoboldSocket> getKobolds() {
        return Collections.unmodifiableList(kobolds);
    }

    @Override
    public List<IKoboldSocket> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    @Override
    public List<IKoboldSocket> getBlacklist() {
        return Collections.unmodifiableList(blacklist);
    }

}
