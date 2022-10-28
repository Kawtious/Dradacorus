/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.sound;

import dradacorus.online.utils.SocketHelper;
import java.io.File;
import java.io.Serializable;
import java.util.Optional;

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
        if (fileName.indexOf("\\.") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("\\."));
        } else {
            return fileName;
        }
    }

    public String getFileExt() {
        String filename = file.getName();
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf("\\.") + 1)).get();
    }

    @Override
    public String toString() {
        return "SoundData{" + "file=" + file.getName() + ", fileName=" + getFileName() + ", fileExt=" + getFileExt() + ", volume=" + volume + ", cycleCount=" + cycleCount + '}';
    }

}
