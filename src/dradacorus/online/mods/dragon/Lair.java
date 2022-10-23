/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.mods.dragon;

import dradacorus.online.ExtendableLair;
import dradacorus.online.IDragonServer;

public final class Lair extends ExtendableLair {

    public Lair(IDragonServer dragon, String name) {
        super(dragon, name);
    }

    public Lair(IDragonServer dragon, String name, String password) {
        super(dragon, name, password);
    }

}
