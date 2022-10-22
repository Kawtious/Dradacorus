/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.online.server;

import dradacorus.online.client.DragonSocket;
import dradacorus.online.client.IDragonSocket;
import dradacorus.online.server.layers.ILayer;
import dradacorus.online.server.layers.Layer;
import dradacorus.online.server.layers.LayerUtils;
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

public class DragonServer implements IDragonServer, ILayer {

    private UUID id = UUID.randomUUID();

    private String name = "";

    private String password = "";

    private final List<IDragonSocket> clients = Collections.synchronizedList(new ArrayList<>());

    private final List<IDragonSocket> operators = Collections.synchronizedList(new ArrayList<>());

    private final List<IDragonSocket> blacklist = Collections.synchronizedList(new ArrayList<>());

    private final List<ILayer> layers = Collections.synchronizedList(new ArrayList<>());

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

            DragonConsole.WriteLine("Server", "Server is now running!");
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
                    IDragonSocket client = createDragonSocket(socket);

                    byte[] key = UUID.randomUUID().toString().getBytes();

                    if (validate(client, key)) {
                        add(client);
                    }
                } catch (IOException ex) {
                }
            }
        });
    }

    @Override
    public IDragonSocket createDragonSocket(Socket socket) throws IOException {
        return new DragonSocket(this, socket);
    }

    @Override
    public boolean validate(IDragonSocket client, byte[] key) throws IOException {
        SocketHelper.send(client, key);
        client.setKey(key);

        if (Arrays.equals(client.listen(), client.getKey())) {
            DragonConsole.WriteLine("Server", client.getSocket().getInetAddress() + " has connected");

            if (LayerUtils.isIPBanned(blacklist, client)) {
                SocketHelper.send(client, "You are banned from this server");
                return false;
            }

            if (hasPassword()) {
                SocketHelper.send(client, "Input password to join");

                if (!Arrays.equals(client.listen(), password.getBytes())) {
                    SocketHelper.send(client, "Wrong password");
                    return false;
                }
            }

            SocketHelper.send(client, "Type /? or /help for a list of commands");
            return true;
        }

        return false;
    }

    @Override
    public void add(IDragonSocket client) throws IOException {
        clients.add(client);
        client.setConnected(true);
        client.start();
    }

    @Override
    public void addClientToLayer(ILayer layer, IDragonSocket client) {
        client.setLayer(layer);
        layer.addClient(client);
    }

    @Override
    public void createLayer(IDragonSocket client, String name) {
        createLayer(client, name, "");
    }

    @Override
    public void createLayer(IDragonSocket client, String name, String password) {
        addLayer(new Layer(this, name, password), client);
    }

    @Override
    public void addLayer(ILayer layer, IDragonSocket client) {
        layers.add(layer);

        client.setLayer(layer);

        layer.addClient(client);
        layer.op(client);
    }

    @Override
    public void removeLayer(ILayer layer) {
        layer.destroy();
        layers.remove(layer);
    }

    @Override
    public List<ILayer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void destroy() {
        if (!clients.isEmpty()) {
            for (IDragonSocket client : clients) {
                kick(client);
            }
        }
    }

    @Override
    public void addClient(IDragonSocket client) {
        clients.add(client);
    }

    @Override
    public void kick(IDragonSocket client) {
        client.disconnect();
    }

    @Override
    public void ban(IDragonSocket client) {
        kick(client);
        blacklist.add(client);
    }

    @Override
    public void op(IDragonSocket client) {
        operators.add(client);
    }

    @Override
    public void deop(IDragonSocket client) {
        operators.remove(client);
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
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public List<IDragonSocket> getClients() {
        return Collections.unmodifiableList(clients);
    }

    @Override
    public List<IDragonSocket> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    @Override
    public List<IDragonSocket> getBlacklist() {
        return Collections.unmodifiableList(blacklist);
    }

}
