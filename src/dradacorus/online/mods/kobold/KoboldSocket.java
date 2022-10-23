/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.mods.kobold;

import dradacorus.online.ExtendableKoboldSocket;
import dradacorus.online.IDragonServer;
import dradacorus.online.ILairActions;
import dradacorus.online.mods.dragon.Lair;
import java.io.IOException;
import java.net.Socket;

public final class KoboldSocket extends ExtendableKoboldSocket {

    public KoboldSocket(IDragonServer dragon, ILairActions actions, Socket socket) throws IOException {
        super(dragon, actions, socket);
    }

    @Override
    public Lair getLair() {
        return (Lair) super.getLair();
    }

    @Override
    public LairActions getActions() {
        return (LairActions) actions;
    }

}
