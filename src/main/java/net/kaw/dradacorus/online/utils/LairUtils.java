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
package net.kaw.dradacorus.online.utils;

import java.util.List;
import net.kaw.dradacorus.online.ExtendableKoboldSocket.Invite;
import net.kaw.dradacorus.online.IDragonServer;
import net.kaw.dradacorus.online.IKoboldSocket;
import net.kaw.dradacorus.online.ILair;

public class LairUtils {

    public static boolean isBanned(List<IKoboldSocket> list, IKoboldSocket baddie) {
        return list.contains(baddie);
    }

    public static boolean isIPBanned(List<IKoboldSocket> list, IKoboldSocket baddie) {
        for (IKoboldSocket kobold : list) {
            if (kobold.getSocket().getInetAddress().toString().equals(baddie.getSocket().getInetAddress().toString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOperator(List<IKoboldSocket> list, IKoboldSocket kobold) {
        return list.contains(kobold);
    }

    public static int findLairByName(IDragonServer dragon, String name) {
        for (ILair lair : dragon.getLairs()) {
            if (lair.getName().equals(name)) {
                return dragon.getLairs().indexOf(lair);
            }
        }
        return -1;
    }

    public static int findKoboldByName(List<IKoboldSocket> kobolds, String name) {
        for (IKoboldSocket kobold : kobolds) {
            if (kobold.getKoboldName().equals(name)) {
                return kobolds.indexOf(kobold);
            }
        }
        return -1;
    }

    public static int findInviteByKoboldName(List<Invite> invites, String name) {
        for (Invite invite : invites) {
            if (invite.getSender().getKoboldName().equals(name)) {
                return invites.indexOf(invite);
            }
        }
        return -1;
    }

    private LairUtils() {
    }

}
