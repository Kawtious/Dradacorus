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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.kaw.dradacorus.online.sound.SoundData;
import net.kaw.dradacorus.online.utils.Dradacorus;
import net.kaw.dradacorus.online.utils.LairUtils;
import net.kaw.dradacorus.online.utils.SocketHelper;
import net.kaw.dradacorus.utils.ColorUtils;
import net.kaw.dradacorus.utils.DragonConsole;

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

        Dradacorus.verifyVersion();

        run(port);

        return true;
    }

    @Override
    public void run(int port) {
        try (final ServerSocket server = new ServerSocket(port)) {
            running = true;

            DragonConsole.writeLine(name + " is now flying!");
            DragonConsole.writeLine("Listening on port " + port);

            while (running) {
                listen(server);
            }
        } catch (IOException ex) {
            DragonConsole.Error.writeLine(ex.getMessage());
        }
    }

    @Override
    public void listen(ServerSocket server) {
        try {
            Socket socket = server.accept();
            IKoboldSocket kobold = createKoboldSocket(socket);

            byte[] key = UUID.randomUUID().toString().getBytes("ISO-8859-1");

            if (validate(kobold, key)) {
                add(kobold);
            }
        } catch (IOException ex) {
            DragonConsole.Error.writeLine(ex.getMessage());
        }
    }

    @Override
    public boolean validate(IKoboldSocket kobold, byte[] key) {
        SocketHelper.Output.send(kobold, key);
        kobold.setKey(key);

        if (Arrays.equals(kobold.listen(), kobold.getKey())) {
            DragonConsole.writeLine(kobold.getKoboldName() + " has connected");
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

            File bellAudio = new File("Audio/bell.wav");
            if (bellAudio.exists()) {
                SoundData sndData = new SoundData(new File("Audio/bell.wav"), 0.15, 1);
                SocketHelper.Output.sendSoundPlayRequest(kobold, sndData);
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
