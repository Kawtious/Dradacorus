/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.application;

import dradacorus.online.client.DragonClient;

public class RunClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1001;
    private final static String[] ARGS = {};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DragonClient client = new DragonClient();

        client.setIp(SERVER_IP);
        client.setPort(SERVER_PORT);
        client.setArgs(ARGS);

        if (!client.run()) {
            System.exit(1);
        }
    }

}
