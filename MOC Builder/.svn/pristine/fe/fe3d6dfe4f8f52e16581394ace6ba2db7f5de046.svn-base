package Exports;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Bricklink.BricklinkAPI;
import Bricklink.BrickBuilder.api.BrickBuilderClient;
import Bricklink.BrickBuilder.api.CompatibleInfo.CompatibleColorsRequest;
import Bricklink.BrickBuilder.api.CompatibleInfo.CompatibleColorsResponse;
import Bricklink.BrickBuilder.api.CompatibleInfo.CompatibleIDRequest;
import Bricklink.BrickBuilder.api.CompatibleInfo.CompatibleIDResponse;
import Bricklink.BrickBuilder.api.CompatibleInfo.UpdateCompatibleColorRequest;
import Bricklink.BrickBuilder.api.CompatibleInfo.UpdateCompatibleIDsRequest;
import Bricklink.BrickBuilder.api.Connectivity.ConnectivitiesRequest;
import Bricklink.BrickBuilder.api.Connectivity.ConnectivitiesResponse;
import Bricklink.BrickBuilder.api.Connectivity.UploadConnectivityRequest;
import Bricklink.BrickBuilder.api.LDrawPart.LDrawPartsRequest;
import Bricklink.BrickBuilder.api.LDrawPart.LDrawPartsResponse;
import Bricklink.BrickBuilder.api.LDrawPart.UploadLDrawPartRequest;
import Bricklink.BrickBuilder.data.ConnectivityDT;
import Bricklink.BrickBuilder.data.IDMappingDT;
import Bricklink.BrickBuilder.data.LDrawPartDT;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.api.Response;
import Builder.BuilderConfigurationManager;
import LDraw.Support.ConnectivityLibrary;
import LDraw.Support.LDrawPaths;
import LDraw.Support.PartCache;
import Window.BackgroundThreadManager;

public class UpdateManager {
	private static UpdateManager _instance = null;

	private HashMap<String, String> idMappingInfoMapFromLDraw;
	private HashMap<String, String> idMappingInfoMapFromBricklink;

	private HashMap<Integer, Integer> colorMappingInfoMapFromLDraw;
	private HashMap<Integer, Integer> colorMappingInfoMapFromBricklink;

	private HashMap<String, ConnectivityDT> connectivityInfo;

	private HashMap<String, LDrawPartDT> partInfo;

	private boolean isServerConnectible = true;

	private UpdateManager() {
		idMappingInfoMapFromLDraw = new HashMap<String, String>();
		idMappingInfoMapFromBricklink = new HashMap<String, String>();

		colorMappingInfoMapFromLDraw = new HashMap<Integer, Integer>();
		colorMappingInfoMapFromBricklink = new HashMap<Integer, Integer>();

		connectivityInfo = new HashMap<String, ConnectivityDT>();

		partInfo = new HashMap<String, LDrawPartDT>();

//		BackgroundThreadManager.getInstance().add(new Runnable() {
//			@Override
//			public void run() {
//				loadColorMappingInfo();
//				System.out.println("loadColorMappingInfo done");
//			}
//		});
//		BackgroundThreadManager.getInstance().add(new Runnable() {
//			@Override
//			public void run() {
//				loadConnectivityInfo();
//				System.out.println("loadConnectivityInfo done");
//			}
//		});
//		BackgroundThreadManager.getInstance().add(new Runnable() {
//			@Override
//			public void run() {
//				loadIdMappingInfo();
//				System.out.println("loadIdMappingInfo done");
//			}
//		});
//		BackgroundThreadManager.getInstance().add(new Runnable() {
//			@Override
//			public void run() {
//				loadLDrawPartInfo();
//				System.out.println("loadLDrawPartInfo done");
//			}
//		});
	}

	private void loadLDrawPartInfo() {
		partInfo.clear();
		if (!isServerConnectible)
			return;

		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();
		Request request = new LDrawPartsRequest(false);
		Response response = null;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isServerConnectible = false;
		}

		if (response != null)
			for (LDrawPartDT dt : ((LDrawPartsResponse) response)
					.getLDrawPartDTList()) {
				partInfo.put(dt.getId().toLowerCase(), dt);
			}
	}

	private void loadConnectivityInfo() {
		connectivityInfo.clear();
		if (!isServerConnectible)
			return;

		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();
		Request request = new ConnectivitiesRequest(false);
		Response response = null;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isServerConnectible = false;
		}

		if (response != null)
			for (ConnectivityDT connectivityDT : ((ConnectivitiesResponse) response)
					.getConnectivityList()) {
				connectivityInfo.put(connectivityDT.getId(), connectivityDT);
			}
	}

	private void loadColorMappingInfo() {
		colorMappingInfoMapFromLDraw.clear();
		colorMappingInfoMapFromBricklink.clear();

		if (!isServerConnectible)
			return;

		// obtain all id mapping info from server
		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();
		Request request = new CompatibleColorsRequest(PartDomainT.LDRAW, false);
		Response response = null;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isServerConnectible = false;
		}

		if (response != null)
			for (IDMappingDT idMappingDT : ((CompatibleColorsResponse) response)
					.getMappingList()) {
				colorMappingInfoMapFromLDraw.put(
						Integer.parseInt(idMappingDT.getFromId().getId()),
						Integer.parseInt(idMappingDT.getToId().getId()));
			}

		request = new CompatibleColorsRequest(PartDomainT.BRICKLINK, false);
		response = null;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isServerConnectible = false;
		}

		if (response != null)
			for (IDMappingDT idMappingDT : ((CompatibleColorsResponse) response)
					.getMappingList()) {
				colorMappingInfoMapFromBricklink.put(
						Integer.parseInt(idMappingDT.getFromId().getId()),
						Integer.parseInt(idMappingDT.getToId().getId()));
			}

	}

	private void loadIdMappingInfo() {
		idMappingInfoMapFromLDraw.clear();
		idMappingInfoMapFromBricklink.clear();

		if (!isServerConnectible)
			return;

		// obtain all id mapping info from server
		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();
		Request request = new CompatibleIDRequest(PartDomainT.LDRAW, false);
		Response response = null;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isServerConnectible = false;
		}

		if (response != null)
			for (IDMappingDT idMappingDT : ((CompatibleIDResponse) response)
					.getMappingList()) {
				idMappingInfoMapFromLDraw.put(idMappingDT.getFromId().getId()
						.toLowerCase(), idMappingDT.getToId().getId()
						.toLowerCase());
			}

		request = new CompatibleIDRequest(PartDomainT.BRICKLINK, false);
		response = null;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isServerConnectible = false;
		}

		if (response != null)
			for (IDMappingDT idMappingDT : ((CompatibleIDResponse) response)
					.getMappingList()) {
				idMappingInfoMapFromBricklink.put(idMappingDT.getFromId()
						.getId().toLowerCase(), idMappingDT.getToId().getId()
						.toLowerCase());
			}

	}

	public synchronized static UpdateManager getInstance() {
		if (_instance == null)
			_instance = new UpdateManager();
		return _instance;
	}

	public HashMap<String, String> getIdMappingInfoMapFromLDraw() {
		if (idMappingInfoMapFromLDraw.size() == 0)
			loadIdMappingInfo();
		return new HashMap<String, String>(idMappingInfoMapFromLDraw);
	}

	public HashMap<String, String> getIdMappingInfoMapFromBricklink() {
		if (idMappingInfoMapFromBricklink.size() == 0)
			loadIdMappingInfo();
		return new HashMap<String, String>(idMappingInfoMapFromBricklink);
	}

	public HashMap<Integer, Integer> getColorMappingInfoMapFromLDraw() {
		if (colorMappingInfoMapFromLDraw.size() == 0)
			loadColorMappingInfo();
		return new HashMap<Integer, Integer>(colorMappingInfoMapFromLDraw);
	}

	public HashMap<Integer, Integer> getColorMappingInfoMapFromBricklink() {
		if (colorMappingInfoMapFromBricklink.size() == 0)
			loadColorMappingInfo();

		return new HashMap<Integer, Integer>(colorMappingInfoMapFromBricklink);
	}

	public ArrayList<String> getConnectivityInfoList() {
		if (connectivityInfo.size() == 0)
			loadConnectivityInfo();
		ArrayList<String> retList = new ArrayList<String>(
				connectivityInfo.keySet());
		return retList;
	}

	public ArrayList<String> getPartList() {
		if (partInfo.size() == 0)
			loadLDrawPartInfo();
		ArrayList<String> retList = new ArrayList<String>(partInfo.keySet());
		return retList;
	}

	public void downloadNewConnectivities() {
		if (!isServerConnectible)
			return;

		final ArrayList<String> connectivityInfoList_server = UpdateManager
				.getInstance().getConnectivityInfoList();
		final ArrayList<String> connectivityInfoList_local = ConnectivityLibrary
				.getInstance().getAllConnectiblePartIdList();

		for (final String partId : connectivityInfoList_server) {
			boolean isAlreadyExist = false;
			for (String partId_local : connectivityInfoList_local)
				if (partId.equals(partId_local)) {
					isAlreadyExist = true;
					break;
				}
			if (isAlreadyExist == true)
				continue;

			BackgroundThreadManager.getInstance().add(new Runnable() {
				@Override
				public void run() {
					ConnectivityDT dt = connectivityInfo.get(partId);
					String url = BricklinkAPI.getInstance()
							.getBrickBuilderClient().getBaseURL()
							+ dt.getFileURL();
					try {
						InputStream is = new URL(url).openStream();
						BufferedInputStream bis = new BufferedInputStream(is);

						FileWriter fw = new FileWriter(new File(
								"./Resource/Connectivity/" + partId + ".conn"));
						BufferedWriter bw = new BufferedWriter(fw);
						byte[] bytes = new byte[1024];
						while (bis.read(bytes) != -1)
							bw.write(new String(bytes));
						bw.close();
						fw.close();
						bis.close();
						is.close();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		BackgroundThreadManager.getInstance().add(new Runnable() {
			@Override
			public void run() {
				loadConnectivityInfo();
			}
		});
	}

	public void uploadCustomConnectivities() {
		if (!isServerConnectible)
			return;

		ArrayList<String> connectivityInfoList_server = UpdateManager
				.getInstance().getConnectivityInfoList();
		ArrayList<String> connectivityInfoList_local = ConnectivityLibrary
				.getInstance().getAllConnectiblePartIdList();

		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();

		for (String partId : connectivityInfoList_local) {
			boolean isAlreadyExist = false;
			for (String partId_server : connectivityInfoList_server)
				if (partId.equals(partId_server)) {
					isAlreadyExist = true;
					break;
				}
			if (isAlreadyExist == true)
				continue;

			Request request = new UploadConnectivityRequest(partId,
					"./Resource/connectivity/" + partId + ".conn");
			try {
				Response response = client.execute(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isServerConnectible = false;
				break;
			}
		}
		loadConnectivityInfo();
	}

	public void uploadCustomParts() {

		if (!isServerConnectible)
			return;

		ArrayList<String> partsList_server = getPartList();
		ArrayList<String> partsList_local = PartCache.getInstance()
				.getAllParts();

		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();

		for (String partId : partsList_local) {
			boolean isAlreadyExist = false;
			for (String partId_server : partsList_server)
				if (partId.equals(partId_server)) {
					isAlreadyExist = true;
					break;
				}
			if (isAlreadyExist == true)
				continue;
			String filePath = LDrawPaths.getInstance().pathForPartName(
					partId + ".dat");
			if (filePath == null)
				continue;

			Request request = new UploadLDrawPartRequest(partId, filePath);
			try {
				Response response = client.execute(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isServerConnectible = false;
				break;
			}
		}

		loadLDrawPartInfo();
	}

	public void downloadNewParts() {
		if (!isServerConnectible)
			return;

		final ArrayList<String> partsList_server = UpdateManager.getInstance()
				.getPartList();
		final ArrayList<String> partsList_local = PartCache.getInstance()
				.getAllParts();

		for (final String partId : partsList_server) {
			boolean isAlreadyExist = false;
			for (String partId_local : partsList_local)
				if (partId.equals(partId_local)) {
					isAlreadyExist = true;
					break;
				}
			if (isAlreadyExist == true)
				continue;

			BackgroundThreadManager.getInstance().add(new Runnable() {

				@Override
				public void run() {
					LDrawPartDT dt = partInfo.get(partId);
					String url = BricklinkAPI.getInstance()
							.getBrickBuilderClient().getBaseURL()
							+ dt.getFileURL();

					System.out.println(url);
					System.out.println(BuilderConfigurationManager
							.getInstance().getLDrawDirectory()
							+ "parts/"
							+ partId + ".dat");
					try {
						InputStream is = new URL(url).openStream();
						BufferedInputStream bis = new BufferedInputStream(is);

						FileWriter fw = new FileWriter(new File(
								BuilderConfigurationManager.getInstance()
										.getLDrawDirectory()
										+ "parts/"
										+ partId + ".dat"));
						BufferedWriter bw = new BufferedWriter(fw);
						byte[] bytes = new byte[1024];
						while (bis.read(bytes) != -1)
							bw.write(new String(bytes));
						bw.close();
						fw.close();
						bis.close();
						is.close();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		BackgroundThreadManager.getInstance().add(new Runnable() {
			@Override
			public void run() {
				loadLDrawPartInfo();
				PartCache.getInstance().reInit();
			}
		});
	}

	public void uploadIdMappingList() {
		if (!isServerConnectible)
			return;

		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();

		HashMap<String, PartIds> partIdsFromLDraw_local = CompatiblePartManager
				.getInstance().getAllPartsInDomain(PartDomainT.LDRAW);
		HashMap<String, String> partIdsFromLDraw_server = getIdMappingInfoMapFromLDraw();

		Request request;
		for (Entry<String, PartIds> entry : partIdsFromLDraw_local.entrySet()) {
			boolean isArleadyUploaded = false;
			if (entry.getValue().getId(PartDomainT.BRICKLINK) != null) {
				String id = null;
				for (String tempId : entry.getValue().getId(
						PartDomainT.BRICKLINK))
					if (id == null)
						id = tempId;
					else
						id += "+" + tempId;
				if (partIdsFromLDraw_server.containsKey(entry.getKey())
						&& partIdsFromLDraw_server.get(entry.getKey()).equals(
								id)) {
					isArleadyUploaded = true;
				}
			} else
				continue;

			if (isArleadyUploaded)
				continue;

			String bricklinkId = null;
			if (entry.getValue().getId(PartDomainT.BRICKLINK) != null)
				for (String id : entry.getValue().getId(PartDomainT.BRICKLINK))
					if (bricklinkId == null)
						bricklinkId = id;
					else
						bricklinkId += "+" + id;

			request = new UpdateCompatibleIDsRequest(entry.getKey(),
					PartDomainT.LDRAW, bricklinkId, PartDomainT.BRICKLINK);
			try {
				client.execute(request);
				idMappingInfoMapFromLDraw.put(entry.getKey(), bricklinkId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isServerConnectible = false;
				break;
			}
		}

		HashMap<String, PartIds> partIdsFromBricklink_local = CompatiblePartManager
				.getInstance().getAllPartsInDomain(PartDomainT.BRICKLINK);
		HashMap<String, String> partIdsFromBricklink_server = getIdMappingInfoMapFromBricklink();

		for (Entry<String, PartIds> entry : partIdsFromBricklink_local
				.entrySet()) {
			boolean isArleadyUploaded = false;
			if (entry.getValue().getId(PartDomainT.LDRAW) != null) {
				String id = null;
				for (String tempId : entry.getValue().getId(PartDomainT.LDRAW))
					if (id == null)
						id = tempId;
					else
						id += "+" + tempId;

				if (partIdsFromBricklink_server.containsKey(entry.getKey())
						&& partIdsFromBricklink_server.get(entry.getKey())
								.equals(id)) {
					isArleadyUploaded = true;
				}
			} else
				continue;

			if (isArleadyUploaded)
				continue;

			String ldrawId = null;
			if (entry.getValue().getId(PartDomainT.LDRAW) != null)
				for (String id : entry.getValue().getId(PartDomainT.LDRAW))
					if (ldrawId == null)
						ldrawId = id;
					else
						ldrawId += "+" + id;

			request = new UpdateCompatibleIDsRequest(entry.getKey(),
					PartDomainT.BRICKLINK, ldrawId, PartDomainT.LDRAW);
			try {
				client.execute(request);
				idMappingInfoMapFromBricklink.put(entry.getKey(), ldrawId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isServerConnectible = false;
				break;
			}

		}

		// loadIdMappingInfo();
	}

	public void uploadColorMappingList() {
		if (!isServerConnectible)
			return;

		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();

		HashMap<Integer, PartColors> partColorsFromLDraw_local = CompatiblePartManager
				.getInstance().getAllColorsInDomain(PartDomainT.LDRAW);
		HashMap<Integer, Integer> partColorsFromLDraw_server = getColorMappingInfoMapFromLDraw();

		Request request;
		for (Entry<Integer, PartColors> entry : partColorsFromLDraw_local
				.entrySet()) {
			boolean isArleadyUploaded = false;
			if (entry.getValue().getColorId(PartDomainT.BRICKLINK) != null) {
				if (partColorsFromLDraw_server.containsKey(entry.getKey())
						&& partColorsFromLDraw_server.get(entry.getKey())
								.equals(entry.getValue().getColorId(
										PartDomainT.BRICKLINK))) {
					isArleadyUploaded = true;
				}
			} else
				continue;

			if (isArleadyUploaded)
				continue;

			request = new UpdateCompatibleColorRequest("" + entry.getKey(),
					PartDomainT.LDRAW, ""
							+ entry.getValue()
									.getColorId(PartDomainT.BRICKLINK),
					PartDomainT.BRICKLINK);
			try {
				client.execute(request);
				colorMappingInfoMapFromLDraw.put(entry.getKey(), +entry
						.getValue().getColorId(PartDomainT.BRICKLINK));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isServerConnectible = false;
				break;
			}
		}

		HashMap<Integer, PartColors> partColorsFromBricklink_local = CompatiblePartManager
				.getInstance().getAllColorsInDomain(PartDomainT.BRICKLINK);
		HashMap<Integer, Integer> partColorsFromBricklink_server = getColorMappingInfoMapFromBricklink();

		for (Entry<Integer, PartColors> entry : partColorsFromBricklink_local
				.entrySet()) {
			boolean isArleadyUploaded = false;
			if (entry.getValue().getColorId(PartDomainT.LDRAW) != null) {
				if (partColorsFromBricklink_server.containsKey(entry.getKey())
						&& partColorsFromBricklink_server.get(entry.getKey())
								.equals(entry.getValue().getColorId(
										PartDomainT.LDRAW))) {
					isArleadyUploaded = true;
				}
			} else
				continue;

			if (isArleadyUploaded)
				continue;

			request = new UpdateCompatibleColorRequest("" + entry.getKey(),
					PartDomainT.BRICKLINK, ""
							+ entry.getValue().getColorId(PartDomainT.LDRAW),
					PartDomainT.LDRAW);
			try {
				client.execute(request);
				colorMappingInfoMapFromLDraw.put(entry.getKey(), +entry
						.getValue().getColorId(PartDomainT.LDRAW));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isServerConnectible = false;
				break;
			}

		}
		// loadColorMappingInfo();
	}
}
