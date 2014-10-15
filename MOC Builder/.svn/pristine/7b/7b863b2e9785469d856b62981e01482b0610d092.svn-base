package Command;

import java.util.StringTokenizer;

import LDraw.Files.LDrawContainer;
import LDraw.Support.LDrawKeywords;

public class LDrawTexture extends LDrawContainer{

	public static boolean lineIsTextureBeginning(String line) {
		String parsedField = null;
		boolean isStart = false;

		StringTokenizer strTokenizer = new StringTokenizer(line);
		if (strTokenizer.hasMoreTokens() == false)
			return false;
		parsedField = strTokenizer.nextToken();
		if (parsedField.equals("0")) {
			if (strTokenizer.hasMoreTokens() == false)
				return false;
			parsedField = strTokenizer.nextToken();

			if (parsedField.equals(LDrawKeywords.LDRAW_TEXTURE)) {
				if (strTokenizer.hasMoreTokens() == false)
					return false;
				parsedField = strTokenizer.nextToken();
				if (parsedField.equals(LDrawKeywords.LDRAW_TEXTURE_START)
						|| parsedField.equals(LDrawKeywords.LDRAW_TEXTURE_NEXT)) {
					if (strTokenizer.hasMoreTokens() == false)
						return false;
					parsedField = strTokenizer.nextToken();
					if (parsedField
							.equals(LDrawKeywords.LDRAW_TEXTURE_METHOD_PLANAR)) {
						isStart = true;
					}
				}
			}
		}

		return isStart;

	}

	public String imageReferenceName() {
		// TODO Auto-generated method stub
		return null;
	}

}
