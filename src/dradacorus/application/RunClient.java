/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.application;

import dradacorus.online.KoboldClient;

public class RunClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1001;
    private static final String[] ARGS = {"/setname caz"};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        KoboldClient kobold = new KoboldClient(SERVER_IP, SERVER_PORT);
        //kobold.connectDiscord();

        if (!kobold.run(ARGS)) {
            System.exit(1);
        }

        System.exit(0);
    }

}
