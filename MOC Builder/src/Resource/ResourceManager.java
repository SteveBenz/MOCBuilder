package Resource;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class ResourceManager {
	HashMap<String, Image> imageCache;
	static ResourceManager instance;

	public static ResourceManager getInstance() {
		if (instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}

	private ResourceManager() {
		imageCache = new HashMap<String, Image>();
	}

	public Image getImage(Device device, String path) {
		Image image = imageCache.get(path);
		if (image == null) {
			InputStream is = getInputStream(path);
			if (is != null) {
				image = new Image(device, is);
			} else {
				image = new Image(device, System.getProperty("user.dir")
						+ path);
			}
			imageCache.put(path, image);
		}
		return image;
	}

	public InputStream getInputStream(String path) {
		return getClass().getResourceAsStream(path);
	}
	public URL getURL (String path){
		return getClass().getResource(path);
	}
}
