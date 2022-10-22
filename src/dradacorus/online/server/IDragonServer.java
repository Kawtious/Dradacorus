/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.server;

import dradacorus.online.client.IDragonSocket;
import dradacorus.online.server.layers.ILayer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public interface IDragonServer {

    public boolean start();

    public void run(int port);

    public Thread listen(ServerSocket server);

    public IDragonSocket createDragonSocket(Socket socket) throws IOException;

    public boolean validate(IDragonSocket client, byte[] key) throws IOException;

    public void add(IDragonSocket client) throws IOException;

    public void addClientToLayer(ILayer layer, IDragonSocket client);

    public void createLayer(IDragonSocket client, String name);

    public void createLayer(IDragonSocket client, String name, String password);

    public void addLayer(ILayer layer, IDragonSocket client);

    public void removeLayer(ILayer layer);

    public List<ILayer> getLayers();

    public void setPort(int port);

    public List<IDragonSocket> getClients();

}
