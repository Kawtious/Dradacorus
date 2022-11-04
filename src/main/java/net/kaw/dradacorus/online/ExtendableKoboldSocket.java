/*
 * MIT License
 * 
 * Copyright (c) 2022 Kawtious
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.kaw.dradacorus.online;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kaw.dradacorus.online.utils.SocketHelper;
import net.kaw.dradacorus.online.utils.ValidationUtils;
import net.kaw.dradacorus.utils.ColorUtils;
import net.kaw.dradacorus.utils.DragonConsole;

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

    protected ExtendableKoboldSocket(IDragonServer dragon, ILairActions actions, Socket socket)
            throws IOException {
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

            DragonConsole.writeLine(getKoboldName() + " has disconnected");
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
                DragonConsole.writeLine(getKoboldName() + ": " + new String(input));
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
