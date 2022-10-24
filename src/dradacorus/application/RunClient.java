/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dradacorus.application;

import dradacorus.online.KoboldClient;

public class RunClient {

    private static final String SERVER_IP = "0.tcp.ngrok.io";
    private static final int SERVER_PORT = 10626;
    private static final String[] ARGS = {};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        KoboldClient kobold = new KoboldClient(SERVER_IP, SERVER_PORT, false);

        if (!kobold.run(ARGS)) {
            System.exit(1);
        }
    }

}
