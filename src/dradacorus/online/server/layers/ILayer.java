/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.server.layers;

import dradacorus.online.client.IDragonSocket;
import java.util.List;
import java.util.UUID;

public interface ILayer {

    public void destroy();

    public void addClient(IDragonSocket client);

    public void kick(IDragonSocket client);

    public void ban(IDragonSocket client);

    public void op(IDragonSocket client);

    public void deop(IDragonSocket client);

    public UUID getId();

    public void setId(UUID id);

    public String getName();

    public void setName(String name);

    public boolean hasPassword();

    public String getPassword();

    public void setPassword(String password);

    public List<IDragonSocket> getClients();

    public List<IDragonSocket> getOperators();

    public List<IDragonSocket> getBlacklist();

}
