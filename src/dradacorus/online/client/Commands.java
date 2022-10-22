/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.client;

import dradacorus.online.server.IDragonServer;
import dradacorus.online.server.layers.ILayer;
import dradacorus.online.server.layers.LayerUtils;
import dradacorus.online.utils.SocketHelper;
import java.util.List;

public class Commands {

    private final IDragonServer server;

    private final IDragonSocket client;

    public Commands(IDragonServer server, IDragonSocket client) {
        this.server = server;
        this.client = client;
    }

    public void help() {
        SocketHelper.send(client, listCommands());
    }

    public String listCommands() {
        StringBuilder sb = new StringBuilder();

        sb.append("List of commands:\n");

        String[] commandList = {
            "/help",
            "/setname",
            "/createlayer", "/createroom",
            "/joinlayer", "/joinroom",
            "/leavelayer", "/leaveroom",
            "/invite",
            "/accept",
            "/decline",
            "/disconnect",
            "/setlayername", "/setroomname",
            "/setlayerpassword", "/setroompassword",
            "/kick", "/ban",
            "/op", "/deop",
            "/listclients", "/listlayers", "/listlobbies", "/listlayers", "/listrooms"
        };

        for (String command : commandList) {
            sb.append(command).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        sb.append("\n");
        return sb.toString();
    }

    public void setClientName(String name) {
        client.setClientName(name);
        SocketHelper.send(client, "Name set to " + client.getClientName());
    }

    public void createLayer(String name, String password) {
        if (name.isEmpty()) {
            SocketHelper.send(client, "A name is required for the layer");
            return;
        }

        if (LayerUtils.findLayerByName(server, name) != -1) {
            SocketHelper.send(client, "A layer with that name already exists");
            return;
        }

        if (!password.isEmpty()) {
            server.createLayer(client, name, password);
        } else {
            server.createLayer(client, name);
        }

        SocketHelper.send(client, "Created layer " + client.getLayer().getName());
    }

    public void joinLayer(String name, String password) {
        if (client.getLayer() != null) {
            SocketHelper.send(client, "You are already in a layer");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.send(client, "You didn't select a valid layer");
            return;
        }

        if (server.getLayers().isEmpty()) {
            SocketHelper.send(client, "No layers are available");
            return;
        }

        int layerIdx = LayerUtils.findLayerByName(server, name);

        if (layerIdx == -1) {
            SocketHelper.send(client, "Layer not found");
            return;
        }

        ILayer layer = server.getLayers().get(layerIdx);

        if (LayerUtils.isBanned(layer.getBlacklist(), client)) {
            SocketHelper.send(client, "You are banned from the layer");
            return;
        }

        if (layer.hasPassword()) {
            if (password.isEmpty()) {
                SocketHelper.send(client, "A password is required to join the layer");
                return;
            }

            if (!password.equals(layer.getPassword())) {
                SocketHelper.send(client, "Wrong password");
                return;
            }
        }

        server.addClientToLayer(layer, client);

        SocketHelper.send(client, "Joined layer " + client.getLayer().getName());
        SocketHelper.sendTo(layer, client.getClientName() + " joined the layer");
    }

    public void leaveLayer() {
        if (client.getLayer() == null) {
            SocketHelper.send(client, "You are not currently in a layer");
            return;
        }

        SocketHelper.sendTo(client.getLayer(), client.getClientName() + " left the layer");

        client.getLayer().kick(client);
    }

    public void invite(String name, String message) {
        if (client.getLayer() == null) {
            SocketHelper.send(client, "You are not currently in a layer");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.send(client, "You need to specify the client you want to invite");
            return;
        }

        int clientIdx = LayerUtils.findClientByName(server.getClients(), name);

        if (clientIdx == -1) {
            SocketHelper.send(client, "Client not found");
            return;
        }

        IDragonSocket client1 = server.getClients().get(clientIdx);

        if (client1.equals(client)) {
            SocketHelper.send(client, "You cannot send an invite to yourself");
            return;
        }

        Invite invite = new Invite(client, client.getLayer());

        client1.addInvite(invite);
        SocketHelper.send(client, "Sent invite to client " + client1.getClientName());
        SocketHelper.send(client1, "Client " + client.getClientName() + " invited you to layer " + client.getLayer().getName() + " (/accept or /decline <name of the inviter>)" + (!message.isEmpty() ? ": " + message : ""));
    }

    public void accept(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(client, "Argument not valid");
            return;
        }

        int inviteIdx = LayerUtils.findInviteByClientName(client.getInvites(), name);

        if (inviteIdx == -1) {
            SocketHelper.send(client, "Invite not found");
            return;
        }

        Invite invite = client.getInvites().get(inviteIdx);

        if (client.getLayer() != null) { //if client is in a layer
            if (!client.getLayer().equals(invite.getLayer())) { //if it's not the same layer as the invite's
                leaveLayer();
                joinLayer(invite.getLayer().getName(), invite.getLayer().getPassword());
            }
        } else {
            joinLayer(invite.getLayer().getName(), invite.getLayer().getPassword());
        }

        IDragonSocket inviteSender = invite.getSender();
        SocketHelper.send(client, "Accepted invite of client " + inviteSender.getClientName());

        client.removeInvite(invite);
    }

    public void decline(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(client, "Argument not valid");
            return;
        }

        int inviteIdx = LayerUtils.findInviteByClientName(client.getInvites(), name);

        if (inviteIdx == -1) {
            SocketHelper.send(client, "Invite not found");
            return;
        }

        Invite invite = client.getInvites().get(inviteIdx);

        IDragonSocket client1 = invite.getSender();
        SocketHelper.send(client1, "Declined invite of client " + client1.getClientName());

        client1.removeInvite(invite);
    }

    public void disconnect() {
        client.disconnect();
    }

    public void setLayerName(String name) {
        if (client.getLayer() == null) {
            SocketHelper.send(client, "You need to be in a layer for that");
            return;
        }

        if (!LayerUtils.isOperator(client.getLayer().getOperators(), client)) {
            SocketHelper.send(client, "You are not an operator of this layer");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.send(client, "A name is required for the layer");
            return;
        }

        client.getLayer().setName(name);
    }

    public void setLayerPassword(String password) {
        if (client.getLayer() == null) {
            SocketHelper.send(client, "You need to be in a layer for that");
            return;
        }

        if (!LayerUtils.isOperator(client.getLayer().getOperators(), client)) {
            SocketHelper.send(client, "You are not an operator of this layer");
            return;
        }

        client.getLayer().setName(password);
    }

    public void kick(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(client, "You need to select a client");
            return;
        }

        if (!LayerUtils.isOperator(client.getLayer().getOperators(), client)) {
            SocketHelper.send(client, "You are not an operator of this layer");
            return;
        }

        int clientIdx = LayerUtils.findClientByName(client.getLayer().getClients(), name);

        if (clientIdx == -1) {
            SocketHelper.send(client, "Client not found");
            return;
        }

        IDragonSocket client1 = client.getLayer().getClients().get(clientIdx);
        ILayer clientLayer = client1.getLayer();

        clientLayer.kick(client1);

        SocketHelper.send(client1, "You have been kicked from the layer");

        SocketHelper.sendTo(clientLayer, client1.getClientName() + " was kicked from the layer");
    }

    public void ban(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(client, "You need to select a client");
            return;
        }

        if (!LayerUtils.isOperator(client.getLayer().getOperators(), client)) {
            SocketHelper.send(client, "You are not an operator of this layer");
            return;
        }

        int clientIdx = LayerUtils.findClientByName(client.getLayer().getClients(), name);

        if (clientIdx == -1) {
            SocketHelper.send(client, "Client not found");
            return;
        }

        IDragonSocket client1 = client.getLayer().getClients().get(clientIdx);
        ILayer clientLayer = client1.getLayer();

        clientLayer.ban(client1);

        SocketHelper.send(client1, "You have been banned from the layer");

        SocketHelper.sendTo(clientLayer, client1.getClientName() + " was banned from the layer");
    }

    public void op(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(client, "Argument is not valid");
            return;
        }

        if (!LayerUtils.isOperator(client.getLayer().getOperators(), client)) {
            SocketHelper.send(client, "You are not an operator of this layer");
            return;
        }

        int clientIdx = LayerUtils.findClientByName(client.getLayer().getClients(), name);

        if (clientIdx == -1) {
            SocketHelper.send(client, "Client not found");
            return;
        }

        IDragonSocket client1 = client.getLayer().getClients().get(clientIdx);

        SocketHelper.send(client1, client1.getClientName() + " is now an operator");

        client1.getLayer().op(client1);

        SocketHelper.send(client1, client1.getClientName() + " is now an operator");

        SocketHelper.send(client1, "You are now an operator");
    }

    public void deop(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(client, "Argument is not valid");
            return;
        }

        if (!LayerUtils.isOperator(client.getLayer().getOperators(), client)) {
            SocketHelper.send(client, "You are not an operator of this layer");
            return;
        }

        int clientIdx = LayerUtils.findClientByName(client.getLayer().getClients(), name);

        if (clientIdx == -1) {
            SocketHelper.send(client, "Client not found");
            return;
        }

        IDragonSocket client1 = client.getLayer().getClients().get(clientIdx);

        client1.getLayer().deop(client1);

        SocketHelper.send(client1, client1.getClientName() + " is no longer an operator");

        SocketHelper.send(client1, "You are no longer an operator");
    }

    public void listClients() {
        StringBuilder sb = new StringBuilder();

        List<IDragonSocket> clients = server.getClients();

        if (client.getLayer() != null) {
            clients = client.getLayer().getClients();
        }

        for (IDragonSocket client1 : clients) {
            sb.append(client1.getClientName()).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        SocketHelper.send(client, sb.toString());
    }

    public void listLayers() {
        if (client.getLayer() != null) {
            SocketHelper.send(client, "You cannot get a list of layers inside a layer");
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (server.getLayers().isEmpty()) {
            return;
        }

        for (ILayer layer : server.getLayers()) {
            sb.append(layer.getName()).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        SocketHelper.send(client, sb.toString());
    }

    public void unknown(String command) {
        SocketHelper.send(client, "Unknown command: " + command);
    }

    public IDragonSocket getClient() {
        return client;
    }

}
