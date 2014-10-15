package Exports;

import java.util.ArrayList;
import java.util.HashMap;

public class PartIds {
	private HashMap<PartDomainT, ArrayList<String>> idMap;

	public PartIds() {
		idMap = new HashMap<PartDomainT, ArrayList<String>>();
	}

	public ArrayList<String> getId(PartDomainT domain) {
		return idMap.get(domain);
	}

	public ArrayList<String> setId(PartDomainT domain, ArrayList<String> id) {
		return idMap.put(domain, id);
	}
	
	public ArrayList<String> setId(PartDomainT domain, String id) {
		ArrayList<String> newList = new ArrayList<String>();
		newList.add(id);
		return idMap.put(domain, newList);
	}
}
