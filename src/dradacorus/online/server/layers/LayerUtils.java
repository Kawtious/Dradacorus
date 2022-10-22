/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.server.layers;

import dradacorus.online.client.IDragonSocket;
import dradacorus.online.client.Invite;
import dradacorus.online.server.IDragonServer;
import java.util.List;

public class LayerUtils {

    public static boolean isBanned(List<IDragonSocket> list, IDragonSocket baddie) {
        return list.contains(baddie);
    }

    public static boolean isIPBanned(List<IDragonSocket> list, IDragonSocket baddie) {
        for (IDragonSocket client : list) {
            if (client.getSocket().getInetAddress().toString().equals(baddie.getSocket().getInetAddress().toString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOperator(List<IDragonSocket> list, IDragonSocket client) {
        return list.contains(client);
    }

    public static int findLayerByName(IDragonServer server, String name) {
        for (ILayer layer : server.getLayers()) {
            if (layer.getName().equals(name)) {
                return server.getLayers().indexOf(layer);
            }
        }
        return -1;
    }

    public static int findClientByName(List<IDragonSocket> clients, String name) {
        for (IDragonSocket client : clients) {
            if (client.getClientName().equals(name)) {
                return clients.indexOf(client);
            }
        }
        return -1;
    }

    public static int findInviteByClientName(List<Invite> invites, String name) {
        for (Invite invite : invites) {
            if (invite.getSender().getClientName().equals(name)) {
                return invites.indexOf(invite);
            }
        }
        return -1;
    }

    private LayerUtils() {
    }

}
