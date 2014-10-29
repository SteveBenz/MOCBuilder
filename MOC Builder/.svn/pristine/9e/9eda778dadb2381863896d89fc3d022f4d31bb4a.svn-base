package Command;

import java.util.StringTokenizer;

import LDraw.Support.LDrawDirective;

public class LDrawBFCCommand extends LDrawDirective {
	String command;

	public void finishParsing(StringTokenizer strTokenizer) {
		try {
			String token = strTokenizer.nextToken();
			if(token.toUpperCase().equals("CERTIFY"))
				command = strTokenizer.nextToken().toUpperCase();
			else
				command = token.toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCommand(){
		return command;
	}
}
