/*
 * MIT License
 * 
 * Copyright (c) 2022 Kawtious
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.kaw.dradacorus.online.mods.kobold;

import java.io.IOException;
import java.net.Socket;
import net.kaw.dradacorus.online.ExtendableKoboldSocket;
import net.kaw.dradacorus.online.IDragonServer;
import net.kaw.dradacorus.online.ILairActions;
import net.kaw.dradacorus.online.mods.dragon.Lair;

public final class KoboldSocket extends ExtendableKoboldSocket {

    public KoboldSocket(IDragonServer dragon, ILairActions actions, Socket socket)
            throws IOException {
        super(dragon, actions, socket);
    }

    @Override
    public Lair getLair() {
        return (Lair) super.getLair();
    }

    @Override
    public LairActions getActions() {
        return (LairActions) actions;
    }

}
