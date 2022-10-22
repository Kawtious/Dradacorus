/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.client;

import dradacorus.online.server.IDragonServer;
import dradacorus.online.server.layers.ILayer;
import dradacorus.online.utils.SocketHelper;
import dradacorus.online.utils.ValidationUtils;
import dradacorus.utils.DragonConsole;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DragonSocket extends Thread implements IDragonSocket {

    private final IDragonServer server;

    private final Socket socket;

    private final DataInputStream dis;

    private final DataOutputStream dos;

    private byte[] key = "$31$".getBytes();

    private ILayer layer;

    private volatile boolean connected = false;

    private final List<Invite> invites = new ArrayList<>();

    private final Commands commands;

    public DragonSocket(IDragonServer server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.commands = new Commands(server, this);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try (socket; dos; dis) {
            while (connected) {
                listen();
            }

            DragonConsole.WriteLine("DragonSocket", getClientName() + " disconnected from server");
        } catch (IOException ex) {
            Logger.getLogger(IDragonSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] listen() {
        try {
            byte[] input = SocketHelper.readBytes(dis, key);

            if (connected) {
                execute(input);
                //Console.WriteLine("DragonSocket", getPlayer().getName() + ": " + new String(TinkHelper.encryptBytes(input, key), StandardCharsets.US_ASCII));
                DragonConsole.WriteLine("DragonSocket", getClientName() + ": " + new String(input));
            }

            return input;
        } catch (IOException ex) {
            disconnect();
        }

        return new byte[1];
    }

    @Override
    public void execute(byte[] msg) throws IOException {
        String input = new String(msg);

        if (ValidationUtils.validateCommand(input)) {
            executeCommand(getArguments(input));
        } else {
            if (layer != null) {
                SocketHelper.sendTo(layer, getClientName() + ": " + input);
            } else {
                SocketHelper.sendTo(server, getClientName() + ": " + input);
            }
        }
    }

    @Override
    public void executeCommand(List<String> arguments) {
        String execute = getArgument(arguments, 0);

        switch (execute) {
            case "/help", "/?" -> {
                commands.help();
            }
            case "/setname", "/nickname", "/name" -> {
                commands.setClientName(getArgument(arguments, 1));
            }
            case "/createlayer" -> {
                commands.createLayer(getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/joinlayer" -> {
                commands.joinLayer(getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/leavelayer" -> {
                commands.leaveLayer();
            }
            case "/invite" -> {
                commands.invite(getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/accept" -> {
                commands.accept(getArgument(arguments, 1));
            }
            case "/decline" -> {
                commands.decline(getArgument(arguments, 1));
            }
            case "/disconnect" -> {
                commands.disconnect();
            }
            case "/setlayername" -> {
                commands.setLayerName(getArgument(arguments, 1));
            }
            case "/setlayerpassword" -> {
                commands.setLayerPassword(getArgument(arguments, 1));
            }
            case "/kick" -> {
                commands.kick(getArgument(arguments, 1));
            }
            case "/ban" -> {
                commands.ban(getArgument(arguments, 1));
            }
            case "/op" -> {
                commands.op(getArgument(arguments, 1));
            }
            case "/deop" -> {
                commands.deop(getArgument(arguments, 1));
            }
            case "/listclients" -> {
                commands.listClients();
            }
            case "/listlayers" -> {
                commands.listLayers();
            }

            // Unknown
            default -> {
                commands.unknown(getArgument(arguments, 0));
            }
        }
    }

    @Override
    public List<String> getArguments(String str) {
        List<String> arguments = new ArrayList<>();

        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
        while (m.find()) {
            arguments.add(m.group(1).replace("\"", ""));
        }

        return arguments;
    }

    @Override
    public String getArgument(List<String> arguments, int index) {
        if (index < 0 || arguments.isEmpty() || index >= arguments.size()) {
            return "";
        }

        return arguments.get(index);
    }

    @Override
    public void addInvite(Invite invite) {
        invites.add(invite);
    }

    @Override
    public void removeInvite(Invite invite) {
        invites.remove(invite);
    }

    @Override
    public List<Invite> getInvites() {
        return Collections.unmodifiableList(invites);
    }

    @Override
    public void disconnect() {
        if (layer != null) {
            layer.kick(this);
        }

        this.connected = false;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public DataInputStream getDis() {
        return dis;
    }

    @Override
    public DataOutputStream getDos() {
        return dos;
    }

    @Override
    public byte[] getKey() {
        return key != null ? Arrays.copyOf(key, key.length) : null;
    }

    @Override
    public void setKey(byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
    }

    @Override
    public ILayer getLayer() {
        return layer;
    }

    @Override
    public void setLayer(ILayer layer) {
        this.layer = layer;
    }

    @Override
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public String getClientName() {
        return getName();
    }

    @Override
    public void setClientName(String name) {
        setName(name);
    }

}
