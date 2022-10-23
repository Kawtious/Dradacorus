/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online;

import dradacorus.online.ExtendableKoboldSocket.Invite;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface IKoboldSocket {

    public void start();

    public void run();

    public byte[] listen();

    public void execute(byte[] msg) throws IOException;

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
