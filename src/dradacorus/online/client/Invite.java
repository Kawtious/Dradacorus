/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.client;

import dradacorus.online.server.layers.ILayer;

public class Invite {

    private final IDragonSocket sender;

    private final ILayer layer;

    public Invite(IDragonSocket sender, ILayer layer) {
        this.sender = sender;
        this.layer = layer;
    }

    public IDragonSocket getSender() {
        return sender;
    }

    public ILayer getLayer() {
        return layer;
    }

}
