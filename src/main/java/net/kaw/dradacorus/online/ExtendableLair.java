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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class ExtendableLair implements ILair {

    private final IDragonServer dragon;

    private UUID id = UUID.randomUUID();

    private String name = "";

    private String password = "";

    private final List<IKoboldSocket> kobolds = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> operators = Collections.synchronizedList(new ArrayList<>());

    private final List<IKoboldSocket> blacklist = Collections.synchronizedList(new ArrayList<>());

    public ExtendableLair(IDragonServer dragon, String name) {
        this.dragon = dragon;
        this.name = name;
    }

    public ExtendableLair(IDragonServer dragon, String name, String password) {
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
