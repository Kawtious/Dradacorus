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
package net.kaw.dradacorus.online.sound;

import java.io.File;
import java.io.Serializable;
import java.util.Optional;
import net.kaw.dradacorus.online.utils.SocketHelper;

public class SoundData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final File file;

    private final byte[] streamData;

    private final double volume;

    private final int cycleCount;

    public SoundData(File file, double volume, int cycleCount) {
        this.file = file;
        this.streamData = SocketHelper.getFileBytes(file);
        this.volume = volume;
        this.cycleCount = cycleCount;
    }

    public byte[] getStreamData() {
        return streamData.clone();
    }

    public double getVolume() {
        return volume;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public String getFileName() {
        String fileName = file.getName();
        if (fileName.indexOf("\\.") < 0) {
            return fileName;
        } else {
            return fileName.substring(0, fileName.lastIndexOf("\\."));
        }
    }

    public String getFileExt() {
        String filename = file.getName();
        Optional<String> ext = Optional.ofNullable(filename).filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf("\\.") + 1));

        if (!ext.isEmpty()) {
            return ext.get();
        }
        return "";
    }

    @Override
    public String toString() {
        return "SoundData{" + "file=" + file.getName() + ", fileName=" + getFileName()
                + ", fileExt=" + getFileExt() + ", volume=" + volume + ", cycleCount=" + cycleCount
                + '}';
    }

}
