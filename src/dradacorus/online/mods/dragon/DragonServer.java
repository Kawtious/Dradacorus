/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.mods.dragon;

import dradacorus.online.ExtendableDragonServer;
import dradacorus.online.IKoboldSocket;
import dradacorus.online.mods.kobold.KoboldSocket;
import dradacorus.online.mods.kobold.LairActions;
import java.io.IOException;
import java.net.Socket;

public final class DragonServer extends ExtendableDragonServer {

    public DragonServer(int port) {
        super(port);
    }

    @Override
    public IKoboldSocket createKoboldSocket(Socket socket) throws IOException {
        return new KoboldSocket(this, new LairActions(this), socket);
    }

    @Override
    public void createLair(IKoboldSocket kobold, String name, String password) {
        addLair(new Lair(this, name, password), kobold);
    }

}
