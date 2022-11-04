/*
    MIT License

    Copyright (c) 2022 Kawtious

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package net.kaw.dradacorus.online;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import net.kaw.dradacorus.online.ExtendableKoboldSocket.Invite;

public interface IKoboldSocket {

    public void start();

    public void run();

    public byte[] listen();

    public void execute(byte[] msg);

    public Invite createInvite();

    public void addInvite(Invite invite);

    public void removeInvite(Invite invite);

    public List<Invite> getInvites();

    public void disconnect();

    public IDragonServer getDragon();

    public Socket getSocket();

    public DataInputStream getDis();

    public DataOutputStream getDos();

    public byte[] getKey();

    public void setKey(byte[] key);

    public abstract ILair getLair();

    public void setLair(ILair lair);

    public abstract ILairActions getActions();

    public void setConnected(boolean connected);

    public String getKoboldName();

    public void setKoboldName(String name);

}
