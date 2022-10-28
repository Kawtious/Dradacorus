/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dradacorus.online;

public interface IKoboldClient {

    public boolean run(String[] args);

    public boolean connect(String ip, int port, String[] args);

    public void listen();

    public byte[] receive();

    public void processMessage(byte[] data);

    public void write();

    public void send(byte[] message);

    public void disconnect();

}
