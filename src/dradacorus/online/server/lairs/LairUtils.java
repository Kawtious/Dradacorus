/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.server.lairs;

import dradacorus.online.dragon.IDragonServer;
import dradacorus.online.kobold.IKoboldSocket;
import dradacorus.online.kobold.Invite;
import java.util.List;

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
