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

public interface ILairActions {

    public void executeAction(IKoboldSocket kobold, String input);

    public String listActions();

    public void help(IKoboldSocket kobold);

    public void setKoboldName(IKoboldSocket kobold, String name);

    public void createLair(IKoboldSocket kobold, String name, String password);

    public void joinLair(IKoboldSocket kobold, String name, String password);

    public void leaveLair(IKoboldSocket kobold);

    public void invite(IKoboldSocket kobold, String name, String message);

    public void accept(IKoboldSocket kobold, String name);

    public void decline(IKoboldSocket kobold, String name);

    public void disconnect(IKoboldSocket kobold);

    public void setLairName(IKoboldSocket kobold, String name);

    public void setLairPassword(IKoboldSocket kobold, String password);

    public void kick(IKoboldSocket kobold, String name);

    public void ban(IKoboldSocket kobold, String name);

    public void op(IKoboldSocket kobold, String name);

    public void deop(IKoboldSocket kobold, String name);

    public void listKobolds(IKoboldSocket kobold);

    public void listLairs(IKoboldSocket kobold);

    public void unknown(IKoboldSocket kobold, String action);

    public void playSound(IKoboldSocket kobold, String soundfile, String volume, String cycleCount);

}
