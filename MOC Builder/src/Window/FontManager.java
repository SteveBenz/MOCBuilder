package Window;

import java.util.HashMap;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import Builder.BuilderConfigurationManager;

public class FontManager {
	private static FontManager _instance = null;

	public synchronized static FontManager getInstance() {
		if (_instance == null)
			_instance = new FontManager();
		return _instance;
	}

	private FontManager() {
		display = Display.getDefault();
		fontMap = new HashMap<String, Font>();
	}

	private Display display = null;

	private HashMap<String, Font> fontMap;

	public Font getFont(String type, int size, int style) {
		Font ret = null;
		
		switch (BuilderConfigurationManager.getOSType()) {
		case Mac:
			type = "Arial";
			break;
		default:
			type = display.getSystemFont().getFontData()[0].getName();
			if(size>10)size=10;
			break;
		}
		
		String key = type + "_" + size + "_" + style;
		if (fontMap.containsKey(key)) {
			ret = fontMap.get(key);
		} else {
			ret = new Font(display, type, size, style);
			fontMap.put(key, ret);
		}

		return ret;
	}
}
