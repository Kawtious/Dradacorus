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
