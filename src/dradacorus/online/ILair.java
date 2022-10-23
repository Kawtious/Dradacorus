/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online;

import java.util.List;
import java.util.UUID;

public interface ILair {

    public void destroy();

    public void addKobold(IKoboldSocket client);

    public void kick(IKoboldSocket client);

    public void ban(IKoboldSocket client);

    public void op(IKoboldSocket client);

    public void deop(IKoboldSocket client);

    public UUID getId();

    public void setId(UUID id);

    public String getName();

    public void setName(String name);

    public boolean hasPassword();

    public String getPassword();

    public void setPassword(String password);

    public List<IKoboldSocket> getKobolds();

    public List<IKoboldSocket> getOperators();

    public List<IKoboldSocket> getBlacklist();

}
