package Resource;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundEffectManager {
	private static SoundEffectManager _instance = null;

	private HashMap<SoundEffectT, Clip> soundEffectMap;

	private SoundEffectManager() {
		soundEffectMap = new HashMap<SoundEffectT, Clip>();
		loadAllSoundEffect();
	}

	private void loadAllSoundEffect() {
		for (SoundEffectT soundEffect : SoundEffectT.values()) {
			loadSoundEffect(soundEffect);
		}

	}

	private void loadSoundEffect(SoundEffectT soundEffect) {
		Clip clip;
		AudioInputStream audioInputStream;
		try {
			String audioFile = soundEffect.getFilePath();

			URL url = ResourceManager.getInstance().getURL(audioFile);
			if (url != null) {
				audioInputStream = AudioSystem.getAudioInputStream(url);
			} else {
				audioInputStream = AudioSystem
						.getAudioInputStream(new BufferedInputStream(
								new FileInputStream(System
										.getProperty("user.dir") + audioFile)));
			}

			clip = AudioSystem.getClip();
			clip.open(audioInputStream);

			soundEffectMap.put(soundEffect, clip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static SoundEffectManager getInstance() {
		if (_instance == null)
			_instance = new SoundEffectManager();

		return _instance;
	}

	public void playSoundEffect(SoundEffectT type) {
		if (soundEffectMap.containsKey(type) == false)
			loadSoundEffect(type);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Clip clip = soundEffectMap.get(type);
				clip.stop();
				clip.setFramePosition(0);
				clip.start();
				do {
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				} while (clip.isActive());
				clip.stop();
			}

			public Runnable init(SoundEffectT type) {
				this.type = type;
				return this;
			}

			private SoundEffectT type;

		}.init(type)).start();

	}
}
