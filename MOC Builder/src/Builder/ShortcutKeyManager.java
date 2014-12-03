package Builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ShortcutKeyManager {
	public enum ShortcutKeyT {
		CancelSelection("Cancel Selection"), Save("Save"), Copy("Copy"), Cut(
				"Cut"), Paste("Paste"), Undo("Undo"), Redo("Redo"), Delete(
				"Delete"), MoveCamera("Move Camera Pos"), RotateX90C(
				"Rotate X 90' C"), RotateX90CC("Rotate X 90' CC"), RotateY90C(
				"Rotate Y 90' C"), RotateY90CC("Rotate Y 90' CC"), RotateZ90C(
				"Rotate Z 90' C"), RotateZ90CC("Rotate Z 90' CC"), RotateX45C(
				"Rotate X 45' C"), RotateX45CC("Rotate X 45' CC"), RotateY45C(
				"Rotate Y 45' C"), RotateY45CC("Rotate Y 45' CC"), RotateZ45C(
				"Rotate Z 45' C"), RotateZ45CC("Rotate Z 45' CC"), RotateX15C(
				"Rotate X 15' C"), RotateX15CC("Rotate X 15' CC"), RotateY15C(
				"Rotate Y 15' C"), RotateY15CC("Rotate Y 15' CC"), RotateZ15C(
				"Rotate Z 15' C"), RotateZ15CC("Rotate Z 15' CC"), RotateX5C(
				"Rotate X 5' C"), RotateX5CC("Rotate X 5' CC"), RotateY5C(
				"Rotate Y 5' C"), RotateY5CC("Rotate Y 5' CC"), RotateZ5C(
				"Rotate Z 5' C"), RotateZ5CC("Rotate Z 5' CC"), MemorizeCameraPos1(
				"Memorize Camera Pos1"), MemorizeCameraPos2(
				"Memorize Camera Pos2"), MemorizeCameraPos3(
				"Memorize Camera Pos3"), MemorizeCameraPos4(
				"Memorize Camera Pos4"), MoveCameraToPos1(
				"Move Camera to Memorized Pos1"), MoveCameraToPos2(
				"Move Camera to Memorized Pos2"), MoveCameraToPos3(
				"Move Camera to Memorized Pos3"), MoveCameraToPos4(
				"Move Camera to Memorized Pos4"), HideSelectedParts(
				"Hide Selected Parts"), HideAll("Hide All Bricks"), ShowAll(
				"Show All Bricks"), ShowSequencially("Show bricks sequencially"), FindNReplace(
				"Find/Replace"), OpenConnectivityEditor(
				"Open Connectivity Editor"), MoveXF("Move Bricks X Forward"), MoveXB(
				"Move Bricks X Backward"), MoveYF("Move Bricks Y Forward"), MoveYB(
				"Move Bricks Y Backward"), MoveZF("Move Bricks Z Forward"), MoveZB(
				"Move Bricks Z Backward"),
		// Group
		NewGroup("Add a New Group"), RemoveGroup("Remove the Current Group"), GroupSelectedParts(
				"Group Selected Parts"), GenerateGropusBastedOnConnection(
				"Separate the Current Group into new Groups based on Connection"), PutAllPartsIntoASingleGroup(
				"Put All Parts Into a Single Group"), RemoveEmptyGroups(
				"Remove Empty Groups"),
		// Submodel
		NewSubmodel("Add a New Submodel"), RemoveSubmodel(
				"Remove the Active Submodel"), MakeSelectedPartsIntoSubmodel(
				"Make Selected Parts Into a Submodel"), ExtractSubmodel(
				"Extract Parts from the Selected Part"),
		// LSynth
		LSynthAddCommand("LSynth-Add Command"), LSynthSynthesize(
				"LSynth-Synthesize"),
		//Comment
		CommentAdd("Add a New Comment")
				
				;

		private String description;

		private ShortcutKeyT(String desc) {
			this.description = desc;
		}

		public String getDescription() {
			return description;
		}

		public static ShortcutKeyT byValue(String desc) {
			for (ShortcutKeyT t : ShortcutKeyT.values())
				if (t.getDescription().toLowerCase().equals(desc.toLowerCase()))
					return t;
			return null;
		}
	}

	private static ShortcutKeyManager _instance = null;

	private ShortcutKeyManager() {
		keyMap = new LinkedHashMap<ShortcutKeyT, String>();
		loadKeyMapFromFile();
	}

	public synchronized static ShortcutKeyManager getInstance() {
		if (_instance == null)
			_instance = new ShortcutKeyManager();
		return _instance;
	}

	private LinkedHashMap<ShortcutKeyT, String> keyMap;
	final static String ShortcutPath = BuilderConfigurationManager
			.getDefaultDataDirectoryPath() + "shortcutkeyMap.list";

	public LinkedHashMap<ShortcutKeyT, String> getKeyMap() {
		return keyMap;
	}

	private void loadKeyMapFromCode() {
		switch (BuilderConfigurationManager.getOSType()) {
		case Mac:
			loadKeyMapFromCode_Mac();
			break;
		default:
			loadKeyMapFromCode_Window();
		}
	}

	private void loadKeyMapFromCode_Window() {
		keyMap.put(ShortcutKeyT.CancelSelection, "Esc");

		keyMap.put(ShortcutKeyT.Save, "Ctrl+S");

		keyMap.put(ShortcutKeyT.Copy, "Ctrl+C");
		keyMap.put(ShortcutKeyT.Cut, "Ctrl+X");
		keyMap.put(ShortcutKeyT.Paste, "Ctrl+V");

		keyMap.put(ShortcutKeyT.Undo, "Ctrl+Z");
		keyMap.put(ShortcutKeyT.Redo, "Ctrl+Y");

		keyMap.put(ShortcutKeyT.Delete, "Del");

		keyMap.put(ShortcutKeyT.MoveCamera, "Space");

		keyMap.put(ShortcutKeyT.RotateX90C, "Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX90CC, "Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY90C, "Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY90CC, "Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ90C, "Page_Up");
		keyMap.put(ShortcutKeyT.RotateZ90CC, "Page_Down");

		keyMap.put(ShortcutKeyT.RotateX45C, "Shift+Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX45CC, "Shift+Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY45C, "Shift+Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY45CC, "Shift+Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ45C, "Shift+Page_Up");
		keyMap.put(ShortcutKeyT.RotateZ45CC, "Shift+Page_Down");

		keyMap.put(ShortcutKeyT.RotateX15C, "Alt+Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX15CC, "Alt+Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY15C, "Alt+Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY15CC, "Alt+Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ15C, "Alt+Page_Up");
		keyMap.put(ShortcutKeyT.RotateZ15CC, "Alt+Page_Down");

		keyMap.put(ShortcutKeyT.RotateX5C, "Alt+Shift+Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX5CC, "Alt+Shift+Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY5C, "Alt+Shift+Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY5CC, "Alt+Shift+Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ5C, "Alt+Shift+Page_Up");
		keyMap.put(ShortcutKeyT.RotateZ5CC, "Alt+Shift+Page_Down");

		keyMap.put(ShortcutKeyT.MoveXF, "Ctrl+Arrow_Right");
		keyMap.put(ShortcutKeyT.MoveXB, "Ctrl+Arrow_Left");
		keyMap.put(ShortcutKeyT.MoveYF, "Ctrl+Page_Up");
		keyMap.put(ShortcutKeyT.MoveYB, "Ctrl+Page_Down");
		keyMap.put(ShortcutKeyT.MoveZF, "Ctrl+Arrow_Up");
		keyMap.put(ShortcutKeyT.MoveZB, "Ctrl+Arrow_Down");

		keyMap.put(ShortcutKeyT.MemorizeCameraPos1, "Ctrl+1");
		keyMap.put(ShortcutKeyT.MemorizeCameraPos2, "Ctrl+2");
		keyMap.put(ShortcutKeyT.MemorizeCameraPos3, "Ctrl+3");
		keyMap.put(ShortcutKeyT.MemorizeCameraPos4, "Ctrl+4");

		keyMap.put(ShortcutKeyT.MoveCameraToPos1, "1");
		keyMap.put(ShortcutKeyT.MoveCameraToPos2, "2");
		keyMap.put(ShortcutKeyT.MoveCameraToPos3, "3");
		keyMap.put(ShortcutKeyT.MoveCameraToPos4, "4");

		keyMap.put(ShortcutKeyT.HideSelectedParts, "");
		keyMap.put(ShortcutKeyT.HideAll, "0");
		keyMap.put(ShortcutKeyT.ShowAll, "-");
		keyMap.put(ShortcutKeyT.ShowSequencially, "=");

		keyMap.put(ShortcutKeyT.FindNReplace, "Ctrl+h");

		keyMap.put(ShortcutKeyT.OpenConnectivityEditor, "F12");

		for (ShortcutKeyT key : ShortcutKeyT.values()) {
			if (keyMap.containsKey(key) == false)
				keyMap.put(key, "");
		}
	}

	private void loadKeyMapFromCode_Mac() {
		keyMap.put(ShortcutKeyT.CancelSelection, "Esc");

		keyMap.put(ShortcutKeyT.Save, "Command+S");

		keyMap.put(ShortcutKeyT.Copy, "Command+C");
		keyMap.put(ShortcutKeyT.Cut, "Command+X");
		keyMap.put(ShortcutKeyT.Paste, "Command+V");

		keyMap.put(ShortcutKeyT.Undo, "Command+Z");
		keyMap.put(ShortcutKeyT.Redo, "Shift+Command+Z");

		keyMap.put(ShortcutKeyT.Delete, "BS");

		keyMap.put(ShortcutKeyT.MoveCamera, "Space");

		keyMap.put(ShortcutKeyT.RotateX90C, "Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX90CC, "Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY90C, "Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY90CC, "Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ90C, "[");
		keyMap.put(ShortcutKeyT.RotateZ90CC, "]");

		keyMap.put(ShortcutKeyT.RotateX45C, "Shift+Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX45CC, "Shift+Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY45C, "Shift+Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY45CC, "Shift+Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ45C, "Shift+[");
		keyMap.put(ShortcutKeyT.RotateZ45CC, "Shift+]");

		keyMap.put(ShortcutKeyT.RotateX15C, "Alt+Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX15CC, "Alt+Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY15C, "Alt+Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY15CC, "Alt+Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ15C, "Alt+[");
		keyMap.put(ShortcutKeyT.RotateZ15CC, "Alt+]");

		keyMap.put(ShortcutKeyT.RotateX5C, "Alt+Shift+Arrow_UP");
		keyMap.put(ShortcutKeyT.RotateX5CC, "Alt+Shift+Arrow_Down");
		keyMap.put(ShortcutKeyT.RotateY5C, "Alt+Shift+Arrow_Left");
		keyMap.put(ShortcutKeyT.RotateY5CC, "Alt+Shift+Arrow_Right");
		keyMap.put(ShortcutKeyT.RotateZ5C, "Alt+Shift+[");
		keyMap.put(ShortcutKeyT.RotateZ5CC, "Alt+Shift+]");

		keyMap.put(ShortcutKeyT.MoveXF, "Command+Arrow_Right");
		keyMap.put(ShortcutKeyT.MoveXB, "Command+Arrow_Left");
		keyMap.put(ShortcutKeyT.MoveYF, "Command+[");
		keyMap.put(ShortcutKeyT.MoveYB, "Command+]");
		keyMap.put(ShortcutKeyT.MoveZF, "Command+Arrow_Up");
		keyMap.put(ShortcutKeyT.MoveZB, "Command+Arrow_Down");

		keyMap.put(ShortcutKeyT.MemorizeCameraPos1, "Command+1");
		keyMap.put(ShortcutKeyT.MemorizeCameraPos2, "Command+2");
		keyMap.put(ShortcutKeyT.MemorizeCameraPos3, "Command+3");
		keyMap.put(ShortcutKeyT.MemorizeCameraPos4, "Command+4");

		keyMap.put(ShortcutKeyT.MoveCameraToPos1, "1");
		keyMap.put(ShortcutKeyT.MoveCameraToPos2, "2");
		keyMap.put(ShortcutKeyT.MoveCameraToPos3, "3");
		keyMap.put(ShortcutKeyT.MoveCameraToPos4, "4");

		keyMap.put(ShortcutKeyT.HideSelectedParts, "");
		keyMap.put(ShortcutKeyT.HideAll, "0");
		keyMap.put(ShortcutKeyT.ShowAll, "-");
		keyMap.put(ShortcutKeyT.ShowSequencially, "=");

		keyMap.put(ShortcutKeyT.FindNReplace, "Command+f");

		keyMap.put(ShortcutKeyT.OpenConnectivityEditor, "Command+0");

		for (ShortcutKeyT key : ShortcutKeyT.values()) {
			if (keyMap.containsKey(key) == false)
				keyMap.put(key, "");
		}
	}

	private void loadKeyMapFromFile() {
		keyMap.clear();

		File categoryFile = new File(ShortcutPath);
		if (categoryFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						categoryFile));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.trim().equals(""))
						continue;

					String description = line.split(":")[0];
					String keys;
					if (line.split(":").length > 1)
						keys = line.split(":")[1];
					else
						keys = "No Keys";
					keyMap.put(ShortcutKeyT.byValue(description), keys);
				}

				for (ShortcutKeyT key : ShortcutKeyT.values()) {
					if (keyMap.containsKey(key) == false)
						keyMap.put(key, "");
				}
				reader.close();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				categoryFile.delete();
			}
		}
		loadKeyMapFromCode();
		writeKeyMapToFile();
	}

	public void writeKeyMapToFile() {
		File categoryFile = new File(ShortcutPath);
		String contents = "";
		for (Entry<ShortcutKeyT, String> entry : keyMap.entrySet())
			contents += entry.getKey().getDescription() + ":"
					+ entry.getValue() + "\r\n";

		try {
			FileWriter fw = new FileWriter(categoryFile);
			fw.write(contents);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reset() {
		keyMap.clear();
		loadKeyMapFromCode();
	}
}
