package de.tum.cit.fop.maze.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized audio management for music and sound effects.
 * Supports volume control and music switching.
 */
public class AudioManager implements Disposable {

    private static AudioManager instance;

    private Music currentMusic;
    private Music menuMusic;
    private Music gameplayMusic;

    private final Map<String, Sound> soundEffects;

    private float musicVolume;
    private float sfxVolume;
    private boolean musicMuted;
    private boolean sfxMuted;

    private float pausedMusicPosition = 0f;

    private static final String PREFS_NAME = "audio_settings";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_SFX_VOLUME = "sfx_volume";
    private static final String KEY_MUSIC_MUTED = "music_muted";
    private static final String KEY_SFX_MUTED = "sfx_muted";

    private static final String MENU_MUSIC_PATH = "audio/background.mp3";
    private static final String GAMEPLAY_MUSIC_PATH = "audio/gameBackgroundWastelandShowdown.mp3";

    private static final String SFX_PLAYER_HURT = "audio/player_hurt.mp3";
    private static final String SFX_PLAYER_DEATH = "audio/player_death.mp3";
    private static final String SFX_DOOR_UNLOCK = "audio/door_unlock.mp3";
    private static final String SFX_LEVEL_COMPLETE = "audio/level_complete.mp3";
    private static final String SFX_GAME_OVER = "audio/game_over.mp3";
    private static final String SFX_ENEMY_ALERT = "audio/enemy_alert.mp3";
    private static final String SFX_UI_CLICK = "audio/ui_click.mp3";

    // private static final String SFX_COLLECT_KEY = "audio/collect_key.mp3";
    // private static final String SFX_COLLECT_HEART = "audio/collect_heart.mp3";
    // private static final String SFX_LEVER_TOGGLE = "audio/lever_toggle.mp3";
    // private static final String SFX_COLLECT_COIN = "audio/collect_coin.mp3";
    // private static final String SFX_POWERUP_SPEED = "audio/powerup_speed.mp3";
    // private static final String SFX_POWERUP_SHIELD = "audio/powerup_shield.mp3";
    // private static final String SFX_SHIELD_BLOCK = "audio/shield_block.mp3";
    // private static final String SFX_ACHIEVEMENT = "audio/achievement.mp3";
    // private static final String SFX_TRAP_SPIKE = "audio/trap_spike.mp3";


    private AudioManager() {
        soundEffects = new HashMap<>();
        loadSettings();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }


    private void loadSettings() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        musicVolume = prefs.getFloat(KEY_MUSIC_VOLUME, 0.7f);
        sfxVolume = prefs.getFloat(KEY_SFX_VOLUME, 1.0f);
        musicMuted = prefs.getBoolean(KEY_MUSIC_MUTED, false);
        sfxMuted = prefs.getBoolean(KEY_SFX_MUTED, false);
    }

    /**
     * Saves volume settings to preferences.
     */
    public void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putFloat(KEY_MUSIC_VOLUME, musicVolume);
        prefs.putFloat(KEY_SFX_VOLUME, sfxVolume);
        prefs.putBoolean(KEY_MUSIC_MUTED, musicMuted);
        prefs.putBoolean(KEY_SFX_MUTED, sfxMuted);
        prefs.flush();
    }

    /**
     * Loads and sets up the menu music track.
     * 
     * @param filePath Internal path to the music file
     */
    public void loadMenuMusic(String filePath) {
        if (menuMusic != null) {
            menuMusic.dispose();
        }
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        menuMusic.setLooping(true);
    }

    /**
     * Loads and sets up the gameplay music track.
     * 
     * @param filePath Internal path to the music file
     */
    public void loadGameplayMusic(String filePath) {
        if (gameplayMusic != null) {
            gameplayMusic.dispose();
        }
        gameplayMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        gameplayMusic.setLooping(true);
    }

    /**
     * Loads all audio resources (music and sound effects).
     * This is the main entry point for initializing audio in the game.
     */
    public void loadAllAudio() {
        // Load music tracks
        loadMenuMusic(MENU_MUSIC_PATH);
        loadGameplayMusic(GAMEPLAY_MUSIC_PATH);

        // Load sound effects
        loadSoundEffects();
    }

    /**
     * Loads all sound effects for the game.
     * Sounds are cached for efficient playback.
     */
    private void loadSoundEffects() {
        // High Priority - Core feedback
        loadSound("player_hurt", SFX_PLAYER_HURT);
        loadSound("player_death", SFX_PLAYER_DEATH);
        loadSound("door_unlock", SFX_DOOR_UNLOCK);
        loadSound("level_complete", SFX_LEVEL_COMPLETE);
        loadSound("game_over", SFX_GAME_OVER);

        // Medium Priority - Enemy sounds
        loadSound("enemy_alert", SFX_ENEMY_ALERT);

        // Low Priority - UI sounds
        loadSound("ui_click", SFX_UI_CLICK);

        // loadSound("collect_key", SFX_COLLECT_KEY);
        // loadSound("collect_heart", SFX_COLLECT_HEART);
        // loadSound("lever_toggle", SFX_LEVER_TOGGLE);
        // loadSound("collect_coin", SFX_COLLECT_COIN);
        // loadSound("powerup_speed", SFX_POWERUP_SPEED);
        // loadSound("powerup_shield", SFX_POWERUP_SHIELD);
        // loadSound("shield_block", SFX_SHIELD_BLOCK);
        // loadSound("achievement", SFX_ACHIEVEMENT);
        // loadSound("trap_spike", SFX_TRAP_SPIKE);
    }


    public void loadSound(String name, String filePath) {
        try {
            if (soundEffects.containsKey(name)) {
                soundEffects.get(name).dispose();
            }
            if (!Gdx.files.internal(filePath).exists()) {
                Gdx.app.error("AudioManager", "Sound file not found: " + filePath);
                return;
            }
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundEffects.put(name, sound);
            Gdx.app.log("AudioManager", "Loaded sound: " + name + " from " + filePath);
        } catch (Exception e) {
            Gdx.app.error("AudioManager", "Failed to load sound " + name + ": " + e.getMessage());
        }
    }


    public void playMenuMusic() {
        if (menuMusic == null)
            return;

        if (currentMusic != null && currentMusic != menuMusic) {
            currentMusic.stop();
        }

        currentMusic = menuMusic;
        if (!musicMuted) {
            currentMusic.setVolume(musicVolume);
            if (!menuMusic.isPlaying()) {
                currentMusic.setPosition(0);
                currentMusic.play();
            }
        }
    }


    public void playGameplayMusic() {
        if (gameplayMusic == null)
            return;

        if (currentMusic != null && currentMusic != gameplayMusic) {
            currentMusic.stop();
        }

        currentMusic = gameplayMusic;
        if (!musicMuted) {
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
        }
    }


    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }


    public void resumeMusic() {
        if (currentMusic != null && !musicMuted) {
            currentMusic.play();
        }
    }


    public void playSound(String name) {
        if (sfxMuted) {
            Gdx.app.log("AudioManager", "SFX muted, not playing: " + name);
            return;
        }

        Sound sound = soundEffects.get(name);
        if (sound != null) {
            sound.play(sfxVolume);
            Gdx.app.log("AudioManager", "Playing sound: " + name + " at volume " + sfxVolume);
        } else {
            Gdx.app.error("AudioManager", "Sound not found: " + name);
        }
    }


    public void playSound(String name, float volume) {
        if (sfxMuted)
            return;

        Sound sound = soundEffects.get(name);
        if (sound != null) {
            sound.play(sfxVolume * volume);
        }
    }


    public void playSoundWithPitch(String name, float volume, float pitch) {
        if (sfxMuted)
            return;

        Sound sound = soundEffects.get(name);
        if (sound != null) {
            long id = sound.play(sfxVolume * volume);
            sound.setPitch(id, pitch);
        }
    }


    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    public boolean isMusicMuted() {
        return musicMuted;
    }

    public void setMusicMuted(boolean muted) {
        this.musicMuted = muted;
        if (currentMusic != null) {
            if (muted) {
                currentMusic.pause();
            } else {
                currentMusic.setVolume(musicVolume);
                currentMusic.play();
            }
        }
    }

    public boolean isSfxMuted() {
        return sfxMuted;
    }

    public void setSfxMuted(boolean muted) {
        this.sfxMuted = muted;
    }

    public void toggleMusicMute() {
        setMusicMuted(!musicMuted);
    }

    public void toggleSfxMute() {
        setSfxMuted(!sfxMuted);
    }


    public void pauseGameAndPlayMenu() {
        if (gameplayMusic != null && gameplayMusic.isPlaying()) {
            pausedMusicPosition = gameplayMusic.getPosition();
            gameplayMusic.pause();
            currentMusic = null;
        }
        playMenuMusic();
    }


    public void resumePausedGameMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.pause();
        }

        if (gameplayMusic != null) {
            currentMusic = gameplayMusic;
            if (!musicMuted) {
                gameplayMusic.setVolume(musicVolume);
                // Resume from the saved position
                gameplayMusic.setPosition(pausedMusicPosition);
                gameplayMusic.play();
            }
        }
    }

    @Override
    public void dispose() {
        if (menuMusic != null) {
            menuMusic.dispose();
            menuMusic = null;
        }
        if (gameplayMusic != null) {
            gameplayMusic.dispose();
            gameplayMusic = null;
        }
        for (Sound sound : soundEffects.values()) {
            sound.dispose();
        }
        soundEffects.clear();
        currentMusic = null;
    }


    public static void reset() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }
}
