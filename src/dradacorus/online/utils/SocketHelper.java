/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.utils;

import dradacorus.online.client.IDragonSocket;
import dradacorus.online.server.IDragonServer;
import dradacorus.online.server.layers.ILayer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SocketHelper {

    public static byte[] readBytes(DataInputStream dis, byte[] key) throws IOException {
        int len = dis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        return TinkHelper.decryptBytes(data, key);
    }

    public static void sendBytes(DataOutputStream dos, byte[] data, byte[] key) throws IOException {
        byte[] bytes = TinkHelper.encryptBytes(data, key);
        sendBytes(dos, bytes, 0, bytes.length);
    }

    public static void sendBytes(DataOutputStream dos, byte[] data, int start, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("Negative length not allowed");
        }
        if (start < 0 || start >= data.length) {
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        }
        // Other checks if needed.

        dos.writeInt(len);
        if (len > 0) {
            dos.write(data, start, len);
        }
    }

    public static Object readObject(byte[] bytes) throws IOException, ClassNotFoundException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis);
        return in.readObject();
    }

    public static byte[] readObjectBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(object);
        out.flush();

        return bos.toByteArray();
    }

    public static void sendObject(IDragonSocket client, Object object) throws IOException {
        send(client, readObjectBytes(object));
    }

    public static void send(IDragonSocket client, byte[] message) {
        try {
            sendBytes(client.getDos(), message, client.getKey());
        } catch (IOException ex) {
        }
    }

    public static void sendTo(ILayer layer, byte[] message) {
        for (IDragonSocket client : layer.getClients()) {
            send(client, message);
        }
    }

    public static void sendTo(ILayer layer, String message) {
        sendTo(layer, message.getBytes());
    }

    public static void sendTo(IDragonServer server, byte[] message) {
        for (IDragonSocket client : server.getClients()) {
            if (client.getLayer() == null) { // if not in a layer
                send(client, message);
            }
        }
    }

    public static void sendTo(IDragonServer server, String message) {
        sendTo(server, message.getBytes());
    }

    public static void send(IDragonSocket client, String message) {
        send(client, message.getBytes());
    }

    private SocketHelper() {
    }

}
