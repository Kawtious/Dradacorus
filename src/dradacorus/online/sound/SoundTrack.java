/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundTrack {

    public static enum TrackState {
        NOTHING, RESUME, UNMUTE_PLAY, CONTINUE, STOP, PAUSE, MUTE_PAUSE
    }

    private MediaPlayer mediaPlayer = null;

    private static double maxVolume = 0.15;

    private static double minVolume = 0;

    public SoundTrack(File file) {
        createMediaPlayer(file, maxVolume, 1);
    }

    public SoundTrack(File file, double volume, int cycleCount) {
        createMediaPlayer(file, volume, cycleCount);
    }

    public final void createMediaPlayer(File file, double volume, int cycleCount) {
        final JFXPanel fxPanel = new JFXPanel();
        try {
            this.mediaPlayer = new MediaPlayer(new Media(file.toURI().toURL().toString()));
            this.mediaPlayer.setVolume(volume);
            this.mediaPlayer.setCycleCount(cycleCount);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SoundTrack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void play() {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.play();
    }

    public final void pause() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    public final void stop() {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.stop();
        mediaPlayer.dispose();
    }

    public final void dispose() {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.dispose();
    }

    public final void setCycleCount(int cycleCount) {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.setCycleCount(cycleCount);
    }

    public final void setOnStopped(Runnable r) {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.setOnStopped(r);
    }

    public final void setOnEndOfMedia(Runnable r) {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.setOnEndOfMedia(r);
    }

    public final void setMute(boolean value) {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.setMute(value);
    }

    public final void setMaxVolume(double volume) {
        if (mediaPlayer == null) {
            return;
        }

        if (volume > minVolume) {
            if (volume < 0) {
                maxVolume = 0;
            } else {
                maxVolume = volume;
            }
        }
    }

    public final void setMinVolume(double volume) {
        if (mediaPlayer == null) {
            return;
        }

        if (volume < maxVolume) {
            if (volume < 0) {
                minVolume = 0;
            } else {
                minVolume = volume;
            }
        }
    }

    public final void setVolume(double volume) {
        if (mediaPlayer == null) {
            return;
        }

        if (volume > maxVolume) {
            mediaPlayer.setVolume(maxVolume);
        } else if (volume < minVolume) {
            mediaPlayer.setVolume(minVolume);
        } else {
            mediaPlayer.setVolume(volume);
        }
    }

    public final void increaseVolume(double volume) {
        if (mediaPlayer == null) {
            return;
        }

        double volumeSlider = getVolume() + volume;
        setVolume(volumeSlider);
    }

    public final void decreaseVolume(double volume) {
        increaseVolume(-volume);
    }

    public final double getVolume() {
        if (mediaPlayer == null) {
            return -1;
        }

        return mediaPlayer.getVolume();
    }

    public static final double getMaxVolume() {
        return maxVolume;
    }

    public static final double getMinVolume() {
        return minVolume;
    }

    public final double getCurrentTime() {
        if (mediaPlayer == null) {
            return -1;
        }

        return mediaPlayer.getCurrentTime().toMillis();
    }

    public final void seek(double playbackTime) {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.seek(new Duration(playbackTime));
    }

    public final int getCurrentCount() {
        if (mediaPlayer == null) {
            return 0;
        }

        return mediaPlayer.getCurrentCount();
    }

    public final int getCycleCount() {
        if (mediaPlayer == null) {
            return 0;
        }

        return mediaPlayer.getCycleCount();
    }

    public final MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public final void shiftVolume(double targetVolume, double fadeSpeed, TrackState init, TrackState event) {
        if (mediaPlayer == null) {
            return;
        }

        new Thread(() -> {
            switch (init) {
                case RESUME:
                    play();
                    break;
                case UNMUTE_PLAY:
                    setMute(false);
                    play();
                    break;
            }
            //

            try {
                if (getVolume() > targetVolume) {
                    while (getVolume() > targetVolume) {
                        decreaseVolume(fadeSpeed);
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                } else if (getVolume() < targetVolume) {
                    while (getVolume() < targetVolume) {
                        increaseVolume(fadeSpeed);
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            } catch (InterruptedException ex) {
            }

            setVolume(targetVolume);  // now sound is at this volume level

            switch (event) {
                case PAUSE:
                    pause();
                    break;
                case MUTE_PAUSE:
                    setMute(true);
                    pause();
                    break;
                case STOP:
                    stop();
                    break;
            }
        }).start();
    }

}
