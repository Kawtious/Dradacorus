/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.application;

import dradacorus.online.kobold.KoboldClient;

public class RunClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1001;
    private static final String[] ARGS = {};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        KoboldClient kobold = new KoboldClient();

        kobold.setIp(SERVER_IP);
        kobold.setPort(SERVER_PORT);
        kobold.setArgs(ARGS);

        if (!kobold.run()) {
            System.exit(1);
        }
    }

}
