/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online;

import dradacorus.online.utils.SocketHelper;
import dradacorus.online.utils.ValidationUtils;
import dradacorus.utils.ColorUtils;
import dradacorus.utils.DragonConsole;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ExtendableKoboldSocket extends Thread implements IKoboldSocket {

    private final IDragonServer dragon;

    private final Socket socket;

    private final DataInputStream dis;

    private final DataOutputStream dos;

    private byte[] key = "$31$".getBytes();

    private ILair lair;

    private volatile boolean connected = false;

    private final List<Invite> invites = new ArrayList<>();

    public final ILairActions actions;

    public ExtendableKoboldSocket(IDragonServer dragon, ILairActions actions, Socket socket) throws IOException {
        this.dragon = dragon;
        this.socket = socket;
        this.actions = actions;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        String koboldColor = ColorUtils.getRandomColorName();
        setName(koboldColor + " Kobold");
    }

    @Override
    public void run() {
        try {
            while (connected) {
                listen();
            }

            DragonConsole.WriteLine(this.getClass(), getKoboldName() + " has disconnected");
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(IKoboldSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] listen() {
        try {
            byte[] input = SocketHelper.Input.readBytes(dis, key);

            if (connected) {
                execute(input);

                //Console.WriteLine(this.getClass(), getPlayer().getName() + ": " + new String(TinkHelper.encryptBytes(input, key), StandardCharsets.US_ASCII));
                DragonConsole.WriteLine(this.getClass(), getKoboldName() + ": " + new String(input));
            }

            SocketHelper.Output.sendDiscordUpdate(this);

            return input;
        } catch (IOException ex) {
            disconnect();
        }

        return new byte[1];
    }

    @Override
    public void execute(byte[] msg) {
        String input = new String(msg);

        if (ValidationUtils.validateAction(input)) {
            actions.executeAction(this, input);
        } else {
            if (!input.substring(0, 1).equals("!")) {
                if (getLair() != null) {
                    SocketHelper.Output.sendTo(getLair(), getKoboldName() + ": " + input);
                } else {
                    SocketHelper.Output.sendTo(dragon, getKoboldName() + ": " + input);
                }
            }
        }
    }

    @Override
    public Invite createInvite() {
        return new Invite(this, this.getLair());
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
        if (getLair() != null) {
            getLair().kick(this);
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
        return key != null ? key.clone() : null;
    }

    @Override
    public void setKey(byte[] key) {
        this.key = key.clone();
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
    public abstract ILairActions getActions();

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

    public class Invite {

        private final IKoboldSocket sender;

        private final ILair lair;

        public Invite(IKoboldSocket sender, ILair layer) {
            this.sender = sender;
            this.lair = layer;
        }

        public IKoboldSocket getSender() {
            return sender;
        }

        public ILair getLair() {
            return lair;
        }

    }

}
