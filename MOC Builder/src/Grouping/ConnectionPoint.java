package Grouping;

import Command.LDrawPart;
import Connectivity.Direction6T;
import Connectivity.Hole;
import Connectivity.IConnectivity;
import Connectivity.Stud;

public class ConnectionPoint {
	private LDrawPart part;
	private Boolean isStudToHoleConnection;
	private IConnectivity connectivity_from;
	private IConnectivity connectivity_to;

	public ConnectionPoint(LDrawPart part, IConnectivity from, IConnectivity to) {
		if (from.getConnectivity() instanceof Stud
				&& to.getConnectivity() instanceof Hole)
			isStudToHoleConnection = true;
		else if (from.getConnectivity() instanceof Hole
				&& to.getConnectivity() instanceof Stud)
			isStudToHoleConnection = false;
		else
			isStudToHoleConnection = null;

		this.part = part;
		this.connectivity_from = from;
		this.connectivity_to = to;
	}

	public Boolean isStudToHoleConnection() {
		return isStudToHoleConnection;
	}

	public LDrawPart getPart() {
		return part;
	}

	public IConnectivity getFrom() {
		return connectivity_from;
	}

	public IConnectivity getTo() {
		return connectivity_to;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConnectionPoint == false)
			return false;

		if (((ConnectionPoint) obj).getPart() != this.part)
			return false;

		if (((ConnectionPoint) obj).getFrom() != this.connectivity_from)
			return false;

		if (((ConnectionPoint) obj).getTo() != this.connectivity_to)
			return false;

		return true;
	}
}
