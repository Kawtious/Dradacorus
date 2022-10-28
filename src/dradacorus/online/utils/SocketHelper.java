/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.utils;

import dradacorus.discord.DiscordHandler;
import dradacorus.online.IDragonServer;
import dradacorus.online.IKoboldSocket;
import dradacorus.online.ILair;
import dradacorus.online.sound.SoundData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketHelper {

    public static final String OBJ_INCOMING_HEADER = "!obj_data_incoming::";

    public static final String DISCORD_UPDATE_HEADER = "!discord_update::";

    public static final String SOUND_REQUEST_HEADER = "!sound_request::";

    public static byte[] getFileBytes(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(SocketHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Object buildObject(byte[] bytes) {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            try (final ObjectInputStream in = new ObjectInputStream(bis)) {
                return in.readObject();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SocketHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] getObjectBytes(Object object) {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (final ObjectOutputStream out = new ObjectOutputStream(bos)) {
                out.writeObject(object);
                return bos.toByteArray();
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new byte[0];
    }

    private SocketHelper() {
    }

    public static class Input {

        public static byte[] readBytes(DataInputStream dis, byte[] key) throws IOException {
            try {
                int len = dis.readInt();
                byte[] data = new byte[len];
                if (len > 0) {
                    dis.readFully(data);
                }
                return TinkHelper.decryptBytes(data, key);
            } catch (EOFException e) {
                // ... this is fine
            }

            return new byte[0];
        }

        private Input() {
        }

    }

    public static class Output {

        public static void sendBytes(DataOutputStream dos, byte[] data, byte[] key) throws IOException {
            if (data != null && data.length > 0) {
                byte[] bytes = TinkHelper.encryptBytes(data, key);
                sendBytes(dos, bytes, 0, bytes.length);
            }
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

        public static void send(IKoboldSocket kobold, byte[] message) {
            try {
                sendBytes(kobold.getDos(), message, kobold.getKey());
            } catch (IOException ex) {
            }
        }

        public static void sendTo(ILair lair, byte[] message) {
            for (IKoboldSocket kobold : lair.getKobolds()) {
                if (lair instanceof IDragonServer) {
                    if (kobold.getLair() == null) { // if not in a lair
                        send(kobold, message);
                    }
                } else {
                    send(kobold, message);
                }
            }
        }

        public static void sendObject(IKoboldSocket kobold, Object object) {
            send(kobold, getObjectBytes(object));
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
            send(kobold, DISCORD_UPDATE_HEADER);
            sendObject(kobold, DiscordHandler.buildDiscordContainer(kobold));
        }

        public static void sendSoundPlayRequest(IKoboldSocket kobold, SoundData sndData) {
            send(kobold, SOUND_REQUEST_HEADER);
            sendObject(kobold, sndData);
        }

        private Output() {
        }

    }

}
