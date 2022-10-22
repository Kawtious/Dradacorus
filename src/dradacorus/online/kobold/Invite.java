/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.kobold;

import dradacorus.online.server.lairs.ILair;

public class Invite {

    private final IKoboldSocket sender;

    private final ILair lair;

    public Invite(IKoboldSocket sender, ILair layer) {
        this.sender = sender;
        this.lair = layer;
    }

    public IKoboldSocket getSender() {
        return sender;
    }

    public ILair getLair() {
        return lair;
    }

}
