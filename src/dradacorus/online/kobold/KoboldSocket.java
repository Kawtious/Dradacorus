/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.kobold;

import dradacorus.online.dragon.IDragonServer;
import dradacorus.online.server.lairs.ILair;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KoboldSocket extends Thread implements IKoboldSocket {

    private final IDragonServer dragon;

    private final Socket socket;

    private final DataInputStream dis;

    private final DataOutputStream dos;

    private byte[] key = "$31$".getBytes();

    private ILair lair;

    private volatile boolean connected = false;

    private final List<Invite> invites = new ArrayList<>();

    private final Commands commands;

    public KoboldSocket(IDragonServer dragon, Socket socket) throws IOException {
        this.dragon = dragon;
        this.socket = socket;
        this.commands = new Commands(dragon, this);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        setName("Kobold-" + ThreadLocalRandom.current().nextInt(1000, 9999));
    }

    @Override
    public void run() {
        try (socket; dos; dis) {
            while (connected) {
                listen();
            }

            DragonConsole.WriteLine("DragonSocket", getKoboldName() + " disconnected from server");
        } catch (IOException ex) {
            Logger.getLogger(IKoboldSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] listen() {
        try {
            byte[] input = SocketHelper.readBytes(dis, key);

            if (connected) {
                execute(input);

                //Console.WriteLine("KoboldSocket", getPlayer().getName() + ": " + new String(TinkHelper.encryptBytes(input, key), StandardCharsets.US_ASCII));
                DragonConsole.WriteLine("DragonSocket", getKoboldName() + ": " + new String(input));
            }

            SocketHelper.sendDiscordUpdate(this);

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
            if (lair != null) {
                SocketHelper.sendTo(lair, getKoboldName() + ": " + input);
            } else {
                SocketHelper.sendTo(dragon, getKoboldName() + ": " + input);
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
                commands.setKoboldName(getArgument(arguments, 1));
            }
            case "/createlair" -> {
                commands.createLair(getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/joinlair" -> {
                commands.joinLair(getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/leavelair" -> {
                commands.leaveLair();
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
            case "/setlairname" -> {
                commands.setLairName(getArgument(arguments, 1));
            }
            case "/setlairpassword" -> {
                commands.setLairPassword(getArgument(arguments, 1));
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
            case "/listkobolds" -> {
                commands.listKobolds();
            }
            case "/listlairs" -> {
                commands.listLairs();
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
        if (lair != null) {
            lair.kick(this);
        }

        this.connected = false;
    }

    @Override
    public IDragonServer getDragon() {
        return dragon;
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
    public ILair getLair() {
        return lair;
    }

    @Override
    public void setLair(ILair lair) {
        this.lair = lair;
    }

    @Override
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public String getKoboldName() {
        return getName();
    }

    @Override
    public void setKoboldName(String name) {
        setName(name);
    }

}
