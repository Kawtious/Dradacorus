/*
    MIT License

    Copyright (c) 2022 Kawtious

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package net.kaw.dradacorus.online.utils;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TinkHelper {

    private static KeysetHandle keysetHandle;

    private static Aead aead;

    private static void register() {
        try {
            AeadConfig.register();

            String keysetFilename = "./keyset.json";
            File keysetFile = new File(keysetFilename);

            if (!keysetFile.exists()) {
                generateKeysetFile(keysetFilename);
            }

            try {
                keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keysetFile));
            } catch (IOException ex) {
                generateKeysetFile(keysetFilename);
                keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keysetFile));
            }

            aead = keysetHandle.getPrimitive(Aead.class);
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(TinkHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void generateKeysetFile(String keysetFilename) throws IOException, GeneralSecurityException {
        // Generate the key material...
        KeysetHandle keysetHandle1 = KeysetHandle.generateNew(KeyTemplates.get("AES128_GCM"));
        CleartextKeysetHandle.write(keysetHandle1, JsonKeysetWriter.withFile(new File(keysetFilename)));
    }

    public static byte[] encryptBytes(byte[] plaintext, byte[] aad) {
        try {
            if (keysetHandle == null || aead == null) {
                register();
            }

            if (aad == null) {
                return plaintext;
            }

            return aead.encrypt(plaintext, aad);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(TinkHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new byte[1];
    }

    public static byte[] decryptBytes(byte[] plaintext, byte[] aad) {
        try {
            if (keysetHandle == null || aead == null) {
                register();
            }

            if (aad == null) {
                return plaintext;
            }

            return aead.decrypt(plaintext, aad);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(TinkHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new byte[1];
    }

    private TinkHelper() {
    }

}
