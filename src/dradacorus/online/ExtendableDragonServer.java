/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.online;

import dradacorus.online.utils.Dradacorus;
import dradacorus.online.utils.LairUtils;
import dradacorus.online.utils.SocketHelper;
import dradacorus.utils.ColorUtils;
import dradacorus.utils.DragonConsole;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class ExtendableDragonServer implements IDragonServer {

    private UUID id = UUID.randomUUID();

    private String name = ColorUtils.getRandomColorName() + " Dragon";

    private String password = "";

    private final List<IKoboldSocket> kobolds = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> operators = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> blacklist = Collections.synchronizedList(new ArrayList<>());

    private final List<ILair> lairs = Collections.synchronizedList(new ArrayList<>());

    private final int port;

    private volatile boolean running = false;

    public ExtendableDragonServer(int port) {
        this.port = port;
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
            Dradacorus.verifyVersion();

            running = true;

            listen(server).start();

            DragonConsole.WriteLine(this.getClass(), name + " is now flying!");
            DragonConsole.WriteLine(this.getClass(), "Listening on port " + port);

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
    public abstract IKoboldSocket createKoboldSocket(Socket socket) throws IOException;

    @Override
    public boolean validate(IKoboldSocket kobold, byte[] key) throws IOException {
        SocketHelper.Output.send(kobold, key);
        kobold.setKey(key);

        if (Arrays.equals(kobold.listen(), kobold.getKey())) {
            DragonConsole.WriteLine(this.getClass(), kobold.getKoboldName() + " has connected");
            SocketHelper.Output.sendTo(this, kobold.getKoboldName() + " has connected");

            if (LairUtils.isIPBanned(blacklist, kobold)) {
                SocketHelper.Output.send(kobold, "You are banned from this server");
                return false;
            }

            if (hasPassword()) {
                SocketHelper.Output.send(kobold, "Input password to join");

                if (!Arrays.equals(kobold.listen(), password.getBytes())) {
                    SocketHelper.Output.send(kobold, "Wrong password");
                    return false;
                }
            }

            SocketHelper.Output.send(kobold, "Type /? or /help for a list of actions");
            return true;
        }

        return false;
    }

    @Override
    public void add(IKoboldSocket kobold) throws IOException {
        kobolds.add(kobold);
        kobold.setConnected(true);
        kobold.start();

        SocketHelper.Output.sendDiscordUpdate(kobold);
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
    public abstract void createLair(IKoboldSocket kobold, String name, String password);

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
    public boolean hasPassword() {
        return !password.isEmpty();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<ILair> getLairs() {
        return Collections.unmodifiableList(lairs);
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
