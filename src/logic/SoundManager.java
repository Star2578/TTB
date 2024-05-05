package logic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class SoundManager {
    private static SoundManager instance;

    private static final double MIN_VOLUME = 0.0;
    private static final double MAX_VOLUME = 1.0;
    private static final double MID_VOLUME = (MAX_VOLUME + MIN_VOLUME) / 2;

    private double soundEffectVolume = MID_VOLUME;
    private double soundEffectSlider = 50.0;
    private double backgroundMusicVolume = MID_VOLUME;
    private double backgroundMusicSlider = 50.0;

    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer soundEffectPlayer;

    public SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playSoundEffect(String filePath) {
        Media media = new Media(new File(filePath).toURI().toString());
        soundEffectPlayer = new MediaPlayer(media);
        soundEffectPlayer.setVolume(soundEffectVolume);
        soundEffectPlayer.play();
    }

    public void playBackgroundMusic(String filePath) {
        Media media = new Media(new File(filePath).toURI().toString());
        backgroundMusicPlayer = new MediaPlayer(media);
        backgroundMusicPlayer.setVolume(backgroundMusicVolume);
        backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusicPlayer.play();
    }

    public void changeBackgroundMusic(String filePath) {
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            backgroundMusicPlayer.stop();
        }
        playBackgroundMusic(filePath);
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            backgroundMusicPlayer.stop();
        }
    }

    public void adjustBackgroundMusicVolume(double volume) {
        if (backgroundMusicPlayer != null) {
            backgroundMusicVolume = clamp(volume);
            backgroundMusicPlayer.setVolume(backgroundMusicVolume);
        }
    }
    public void adjustSoundEffectVolume(double volume) {
        if (soundEffectPlayer != null) {
            soundEffectVolume = clamp(volume);
            soundEffectPlayer.setVolume(soundEffectVolume);
        }
    }
    public double mapToDecibelRange(double sliderValue) {
        if (sliderValue == 0) return -80;

        double normalizedValue = sliderValue / 100.0;
        return MIN_VOLUME + normalizedValue * (MAX_VOLUME - MIN_VOLUME);
    }
    private double clamp(double value) {
        return Math.max(MIN_VOLUME, Math.min(value, MAX_VOLUME));
    }

    // Getters and setters for volume levels and sliders
    public double getBackgroundMusicVolume() {
        return backgroundMusicVolume;
    }
    public void setBackgroundMusicVolume(double backgroundMusicVolume) {
        this.backgroundMusicVolume = backgroundMusicVolume;
    }
    public double getSoundEffectVolume() {
        return soundEffectVolume;
    }
    public void setSoundEffectVolume(double soundEffectVolume) {
        this.soundEffectVolume = soundEffectVolume;
    }
    public double getBackgroundMusicSlider() {
        return volumeToSliderValue(backgroundMusicVolume);
    }
    public void setBackgroundMusicSlider(double backgroundMusicSlider) {
        this.backgroundMusicSlider = backgroundMusicSlider;
    }
    public double getSoundEffectSlider() {
        return volumeToSliderValue(soundEffectVolume);
    }
    public void setSoundEffectSlider(double soundEffectSlider) {
        this.soundEffectSlider = soundEffectSlider;
    }
    private double volumeToSliderValue(double volume) {
        return (volume - MIN_VOLUME) / (MAX_VOLUME - MIN_VOLUME) * 100.0;
    }
    public static double getMidVolume() {
        return MID_VOLUME;
    }
}
