/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.utils;

import dradacorus.discord.DiscordHandler;
import dradacorus.online.dragon.IDragonServer;
import dradacorus.online.kobold.IKoboldSocket;
import dradacorus.online.server.lairs.ILair;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketHelper {

    public static final String OBJ_INCOMING_HEADER = "obj_data_incoming::";

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

    public static Object getObjectFromBytes(byte[] bytes) {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(bytes); final ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SocketHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] readObjectBytes(Object object) {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream(); final ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(SocketHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new byte[1];
    }

    public static void send(IKoboldSocket kobold, byte[] message) {
        try {
            sendBytes(kobold.getDos(), message, kobold.getKey());
        } catch (IOException ex) {
        }
    }

    public static void sendTo(ILair lair, byte[] message) {
        for (IKoboldSocket kobold : lair.getKobolds()) {
            send(kobold, message);
        }
    }

    public static void sendTo(IDragonServer server, byte[] message) {
        for (IKoboldSocket kobold : server.getKobolds()) {
            if (kobold.getLair() == null) { // if not in a lair
                send(kobold, message);
            }
        }
    }

    public static void sendObject(IKoboldSocket kobold, Object object) throws IOException {
        send(kobold, readObjectBytes(object));
    }

    public static void send(IKoboldSocket kobold, String message) {
        send(kobold, message.getBytes());
    }

    public static void sendTo(ILair lair, String message) {
        sendTo(lair, message.getBytes());
    }

    public static void sendTo(IDragonServer server, String message) {
        sendTo(server, message.getBytes());
    }

    public static void sendDiscordUpdate(IKoboldSocket kobold) {
        send(kobold, OBJ_INCOMING_HEADER);
        send(kobold, readObjectBytes(DiscordHandler.buildDiscordContainer(kobold)));
    }

    private SocketHelper() {
    }

}
