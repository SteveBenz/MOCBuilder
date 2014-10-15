package LDraw.Support;

import java.util.ArrayList;

public interface IPartLibraryDelegate {
	void partLibrary(PartLibrary partLibrary, ArrayList<String> newFavorites);
	void partLibrary(PartLibrary partLibrary, int maxPartCount);
	void partLibraryIncrementLoadProgressCount(PartLibrary partLibrary);
}
