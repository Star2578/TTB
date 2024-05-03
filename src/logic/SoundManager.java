package logic;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.util.Random;

public class SoundManager {
    private static SoundManager instance;

    private static final float MIN_DECIBEL = -30.0f;
    private static final float MAX_DECIBEL = 6.0f;
    private static final float MID_DECIBEL = (MAX_DECIBEL + MIN_DECIBEL) / 2;

    private float soundEffectVolume = MID_DECIBEL;
    private float soundEffectSlider = 50.0f;
    private float backgroundMusicVolume = MID_DECIBEL;
    private float backgroundMusicSlider = 50.0f;

    private AudioInputStream audioInputStream;
    private Clip backgroundMusic;
    private Thread backgroundMusicThread;

    public SoundManager() {

    }


    // Public method to get the singleton instance
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    public void playSoundEffect(String filePath) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

            // Open an audio input stream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

            // Get a clip resource
            Clip clip = AudioSystem.getClip();

            // Open audio clip and load samples from the audio input stream
            clip.open(audioInputStream);

            adjustSoundEffectVolume(clip, getSoundEffectVolume());

            // Play the sound in a separate thread
            new Thread(() -> {
                clip.start();
                try {
                    // Sleep for a while to allow the sound to play
                    Thread.sleep(10000); // Adjust the duration as needed
                } catch (InterruptedException e) {
                    System.out.println("Error with playing sound in new Thread: " + e.getMessage());
                } finally {
                    // Stop the sound
                    clip.stop();
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Error with playing sound: " + e.getMessage());
        }
    }

    public void playBackgroundMusic(String filePath) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

            // Get a clip resource
            backgroundMusic = AudioSystem.getClip();

            // Open audio clip and load samples from the audio input stream
            backgroundMusic.open(audioInputStream);

            // Set the clip to loop indefinitely
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);

            adjustBackgroundMusicVolume(backgroundMusicVolume);

            // Start playing the background music in a new thread
            backgroundMusicThread = new Thread(() -> backgroundMusic.start());
            backgroundMusicThread.setDaemon(true);
            backgroundMusicThread.start();
        } catch (Exception e) {
            System.out.println("Error with playing background music: " + e.getMessage());
        }
    }

    // Mapping function to convert slider values to decibel range
    public float mapToDecibelRange(float sliderValue) {
        if (sliderValue == 0) return -80;

        // Assuming the slider value ranges from 0 to 100
        float normalizedValue = sliderValue / 100.0f;

        // Map the normalized value to the decibel range using a logarithmic scale
        return MIN_DECIBEL + normalizedValue * (MAX_DECIBEL - MIN_DECIBEL);
    }

    public void changeBackgroundMusic(String filePath) {
        // Check if the new background music is the same as the current one
        if (backgroundMusic != null && backgroundMusic.isOpen() && filePath.equals(audioInputStream.toString())) {
            // Same song is already playing, no need to change
            return;
        }
        // Stop the current background music
        stopBackgroundMusic();

        // Play the new background music
        playBackgroundMusic(filePath);
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }

        if (backgroundMusicThread != null && backgroundMusicThread.isAlive()) {
            try {
                backgroundMusicThread.join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                System.out.println("Error when stopping background music:" + e.getMessage());
            }
        }
    }

    public void adjustBackgroundMusicVolume(float volume) {
        if (backgroundMusic != null && backgroundMusic.isOpen()) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume);
        }
    }

    public void adjustSoundEffectVolume(Clip clip, float volume) {
        if (clip != null && clip.isOpen()) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume);
        }
    }

    public float getBackgroundMusicVolume() { return backgroundMusicVolume; }
    public void setBackgroundMusicVolume(float backgroundMusicVolume) { this.backgroundMusicVolume = backgroundMusicVolume; }

    public float getSoundEffectVolume() { return soundEffectVolume; }
    public void setSoundEffectVolume(float soundEffectVolume) { this.soundEffectVolume = soundEffectVolume; }

    public float getBackgroundMusicSlider() { return backgroundMusicSlider; }
    public void setBackgroundMusicSlider(float backgroundMusicSlider) { this.backgroundMusicSlider = backgroundMusicSlider; }

    public float getSoundEffectSlider() { return soundEffectSlider; }
    public void setSoundEffectSlider(float soundEffectSlider) { this.soundEffectSlider = soundEffectSlider; }

    public static float getMidDecibel() {
        return MID_DECIBEL;
    }
}