/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.server.lairs;

import dradacorus.online.dragon.IDragonServer;
import dradacorus.online.kobold.IKoboldSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lair implements ILair {

    private final IDragonServer dragon;

    private UUID id = UUID.randomUUID();

    private String name = "";

    private String password = "";

    private final List<IKoboldSocket> kobolds = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> operators = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> blacklist = Collections.synchronizedList(new ArrayList<>());

    public Lair(IDragonServer dragon, String name) {
        this.dragon = dragon;
        this.name = name;
    }

    public Lair(IDragonServer dragon, String name, String password) {
        this.dragon = dragon;
        this.name = name;
        this.password = password;
    }

    @Override
    public void destroy() {
        if (!kobolds.isEmpty()) {
            for (IKoboldSocket kobold : kobolds) {
                kick(kobold);
            }
        }
    }

    @Override
    public void addKobold(IKoboldSocket kobold) {
        kobolds.add(kobold);
    }

    @Override
    public void kick(IKoboldSocket kobold) {
        kobold.setLair(null);
        kobolds.remove(kobold);

        if (kobolds.size() < 1) {
            dragon.removeLair(this);
        }
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
