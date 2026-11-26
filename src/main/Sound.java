package main;

import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

    Clip musicClip;
    Clip seClip;
    URL soundURL[] = new URL[30];

    public Sound() {

        // 🎵 Background Music
        soundURL[0] = getClass().getResource("/sound/NewBeginnings.wav");
        soundURL[1] = getClass().getResource("/sound/indoor.wav");
        soundURL[2] = getClass().getResource("/sound/Dungeon.wav");
        soundURL[17] = getClass().getResource("/sound/FinalBattle.wav");

        // 🔊 Sound Effects
        soundURL[10] = getClass().getResource("/sound/coin.wav");
        soundURL[11] = getClass().getResource("/sound/keypick.wav");
        soundURL[12] = getClass().getResource("/sound/powerup.wav");
        soundURL[13] = getClass().getResource("/sound/gameover.wav");
        soundURL[14] = getClass().getResource("/sound/unlock.wav");
        soundURL[15] = getClass().getResource("/sound/chipwall.wav");
        soundURL[16] = getClass().getResource("/sound/dmg.wav");
    }

    public void setMusicFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);

            AudioFormat baseFormat = ais.getFormat();

            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,                           // force 16-bit
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false                         
            );

            AudioInputStream decodedAIS = AudioSystem.getAudioInputStream(decodedFormat, ais);
            musicClip = AudioSystem.getClip();
            musicClip.open(decodedAIS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSEFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);

            AudioFormat baseFormat = ais.getFormat();

            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,                           // force 16-bit
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false                         // little-endian
            );

            AudioInputStream decodedAIS = AudioSystem.getAudioInputStream(decodedFormat, ais);
            seClip = AudioSystem.getClip();
            seClip.open(decodedAIS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (musicClip != null) musicClip.start();
    }

    public void loop() {
        if (musicClip != null) musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (musicClip != null) musicClip.stop();
    }

    public void playSE() {
        if (seClip != null) seClip.start();
    }
}