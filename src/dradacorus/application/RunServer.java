/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.application;

import dradacorus.online.dragon.DragonServer;

public class RunServer {

    private static final int PORT = 1001;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DragonServer dragon = new DragonServer();

        dragon.setPort(PORT);
        dragon.setName("Wyvern");
        dragon.setPassword("");

        if (!dragon.start()) {
            System.exit(1);
        }
    }

}
