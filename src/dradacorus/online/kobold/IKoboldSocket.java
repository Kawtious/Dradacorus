/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.kobold;

import dradacorus.online.dragon.IDragonServer;
import dradacorus.online.server.lairs.ILair;
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

    public void executeCommand(List<String> arguments);

    public List<String> getArguments(String input);

    public String getArgument(List<String> arguments, int index);

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

    public ILair getLair();

    public void setLair(ILair lair);

    public void setConnected(boolean connected);

    public String getKoboldName();

    public void setKoboldName(String name);

}
