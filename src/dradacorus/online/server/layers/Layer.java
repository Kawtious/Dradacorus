/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.server.layers;

import dradacorus.online.client.IDragonSocket;
import dradacorus.online.server.IDragonServer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Layer implements ILayer {

    private final IDragonServer server;

    private UUID id = UUID.randomUUID();

    private String name = "";

    private String password = "";

    private final List<IDragonSocket> clients = Collections.synchronizedList(new ArrayList<>());

    private final List<IDragonSocket> operators = Collections.synchronizedList(new ArrayList<>());

    private final List<IDragonSocket> blacklist = Collections.synchronizedList(new ArrayList<>());

    public Layer(IDragonServer server, String name) {
        this.server = server;
        this.name = name;
    }

    public Layer(IDragonServer server, String name, String password) {
        this.server = server;
        this.name = name;
        this.password = password;
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
        client.setLayer(null);
        clients.remove(client);

        if (clients.size() < 1) {
            server.removeLayer(this);
        }
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
