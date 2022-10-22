/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.dragon;

import dradacorus.online.kobold.IKoboldSocket;
import dradacorus.online.server.lairs.ILair;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public interface IDragonServer {

    public boolean start();

    public void run(int port);

    public Thread listen(ServerSocket server);

    public IKoboldSocket createKoboldSocket(Socket socket) throws IOException;

    public boolean validate(IKoboldSocket kobold, byte[] key) throws IOException;

    public void add(IKoboldSocket kobold) throws IOException;

    public void addKoboldToLair(ILair lair, IKoboldSocket kobold);

    public void createLair(IKoboldSocket kobold, String name);

    public void createLair(IKoboldSocket kobold, String name, String password);

    public void addLair(ILair lair, IKoboldSocket kobold);

    public void removeLair(ILair lair);

    public String getName();

    public void setName(String name);

    public List<ILair> getLairs();

    public void setPort(int port);

    public List<IKoboldSocket> getKobolds();

}
