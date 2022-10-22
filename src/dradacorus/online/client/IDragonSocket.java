/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.client;

import dradacorus.online.server.layers.ILayer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface IDragonSocket {

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

    public Socket getSocket();

    public DataInputStream getDis();

    public DataOutputStream getDos();

    public byte[] getKey();

    public void setKey(byte[] key);

    public ILayer getLayer();

    public void setLayer(ILayer layer);

    public void setConnected(boolean connected);

    public String getClientName();

    public void setClientName(String name);

}
