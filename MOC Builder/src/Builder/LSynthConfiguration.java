package Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Command.LDrawLSynth;
import Command.LDrawPart;
import Command.LSynthClassT;
import LDraw.Support.LDrawUtilities;
//
//LSynthConfiguration.m
//Bricksmith
//
//Created by Robin Macharg on 24/09/2012.
import LDraw.Support.Range;

public class LSynthConfiguration {
	private static LSynthConfiguration _instance = null;

	// ========== sharedInstance
	// ====================================================
	//
	// Purpose: Return the singleton LSynthConfiguration instance.
	//
	// ==============================================================================
	public synchronized static LSynthConfiguration sharedInstance() {
		if (_instance == null)
			_instance = new LSynthConfiguration();
		return _instance;
	}

	private static final String DEFAULT_HOSE_CONSTRAINT = "LS01.DAT";
	private static final String DEFAULT_BAND_CONSTRAINT = "3648a.dat";
	private static final String DEFAULT_HOSE_TYPE = "TECHNIC_PNEUMATIC_HOSE";
	private static final String DEFAULT_BAND_TYPE = "TECHNIC_CHAIN_LINK";

	private ArrayList<HashMap<String, Object>> parts;
	private ArrayList<HashMap<String, Object>> hose_constraints;
	private ArrayList<HashMap<String, Object>> hose_types;
	private ArrayList<HashMap<String, Object>> band_constraints;
	private ArrayList<HashMap<String, Object>> band_types;
	private ArrayList<String> quickRefBands;
	private ArrayList<String> quickRefHoses;
	private ArrayList<String> quickRefParts;
	private ArrayList<String> quickRefBandConstraints;
	private ArrayList<String> quickRefHoseConstraints;

	private LSynthConfiguration() {
		init();
	}

	// ========== defaultHoseConstraint
	// =============================================
	//
	// Purpose: Return the default hose constraint
	//
	// ==============================================================================
	public static String defaultHoseConstraint() {
		return DEFAULT_HOSE_CONSTRAINT;
	}// end defaultHoseConstraint

	// ========== defaultBandConstraint
	// =============================================
	//
	// Purpose: Return the default band constraint
	//
	// ==============================================================================
	public static String defaultBandConstraint() {
		return DEFAULT_BAND_CONSTRAINT;
	}// end defaultBandConstraint

	// ========== defaultHoseType
	// ===================================================
	//
	// Purpose: Return the default hose type
	//
	// ==============================================================================
	public static String defaultHoseType() {
		return DEFAULT_HOSE_TYPE;
	}// end defaultHoseType

	// ========== defaultBandCType
	// ==================================================
	//
	// Purpose: Return the default band type
	//
	// ==============================================================================
	public static String defaultBandType() {
		return DEFAULT_BAND_TYPE;
	}// end defaultBandType

	// ========== init
	// ==============================================================
	//
	// Purpose: initialize the LSynthConfiguration instance.
	//
	// ==============================================================================
	public void init() {
		initializeArrays();
	}

	// ========== initializeArrays
	// ==================================================
	//
	// Purpose: initialize the LSynthConfiguration arrays.
	//
	// ==============================================================================
	public void initializeArrays() {
		parts = new ArrayList<HashMap<String, Object>>();
		hose_constraints = new ArrayList<HashMap<String, Object>>();
		hose_types = new ArrayList<HashMap<String, Object>>();
		band_constraints = new ArrayList<HashMap<String, Object>>();
		band_types = new ArrayList<HashMap<String, Object>>();

		quickRefBands = new ArrayList<String>();
		quickRefHoses = new ArrayList<String>();
		quickRefParts = new ArrayList<String>();
		quickRefBandConstraints = new ArrayList<String>();
		quickRefHoseConstraints = new ArrayList<String>();
	} // end initializeArrays

	// ========== defaultConfigPath
	// =================================================
	//
	// Purpose: Return the default config path in the main bundle
	//
	// ==============================================================================
	public String defaultConfigPath() {
		// todo
		// return pathForResource:@"lsynth" ofType:@"mpd"];;
		return "";
	} // end defaultConfigPath

	// ========== parseLsynthConfig:
	// ================================================
	//
	// Purpose: Parse an LSynth lsynth.mpd configuration file in order that we
	// can a) validate incoming ldraw files if required and b) populate
	// parts menus appropriately. We only need to parse the file enough
	// to satisfy these requirements; LSynth has a better understanding
	// of this config file.
	//
	// TODO: do we need to parse in as much detail? Surely we just need
	// description + part + type
	// ==============================================================================
	public void parseLsynthConfig(String lsynthConfigurationPath) {
		// Initialise all arrays, since we may be called after a config file
		// change
		initializeArrays();

		// Read the file in
		String fileContents = LDrawUtilities
				.stringFromFile(lsynthConfigurationPath);
		ArrayList<String> lines = new ArrayList<String>(
				Arrays.asList(fileContents.split("\r\n")));

		// General parsing variables
		int lineIndex = 0;
		Range range = new Range(0, lines.size());
		String currentLine = null;
		String previousLine = null;

		// LSynth sscanf()-specific line scanning variables
		String product, title, stretch, fill, type = "";

		int d, // diameter
		st; // stiffness
		float t, // twist
		scale, thresh;
		ArrayList<HashMap<String, Object>> tmp_parts = new ArrayList<HashMap<String, Object>>();

		while (lineIndex < range.getMaxRange()) {
			currentLine = lines.get(lineIndex);
			if (currentLine.length() > 0) {

				// HOSE CONSTRAINTS, e.g.
				//
				// 0 // LSynth Constraint Part - Type 1 - "Hose"
				// 1 0 0 0 0 1 0 0 0 1 0 0 0 1 LS01.dat

				if (lines.get(lineIndex).equals(
						"0 SYNTH BEGIN DEFINE HOSE CONSTRAINTS")) {
					lineIndex++;
					// Local block line-parsing variables. TODO: move to top
					int flip;
					float offset[] = new float[3];
					float orient[][] = new float[3][3];

					while (!lines.get(lineIndex).equals("0 SYNTH END")) {
						String token[] = lines.get(lineIndex).split(" ");
						if (token.length == 15) {
							try {
								flip = Integer.parseInt(token[1]);
								offset[0] = Float.parseFloat(token[2]);
								offset[1] = Float.parseFloat(token[3]);
								offset[2] = Float.parseFloat(token[4]);
								orient[0][0] = Float.parseFloat(token[5]);
								orient[0][1] = Float.parseFloat(token[6]);
								orient[0][2] = Float.parseFloat(token[7]);
								orient[1][0] = Float.parseFloat(token[8]);
								orient[1][1] = Float.parseFloat(token[9]);
								orient[1][2] = Float.parseFloat(token[10]);
								orient[2][0] = Float.parseFloat(token[11]);
								orient[2][1] = Float.parseFloat(token[12]);
								orient[2][2] = Float.parseFloat(token[13]);
								type = token[14];

								// Extract description
								// Big assumption: that we have useful contents
								// in the previous line
								// TODO: harden
								String desc = "";
								if (previousLine != null)
									desc = previousLine.split("- Type ")[1];

								HashMap<String, Object> hose_constraint = new HashMap<String, Object>();
								hose_constraint.put("flip", flip);
								hose_constraint.put(
										"offset",
										new ArrayList<Float>(Arrays
												.asList(new Float[] {
														offset[0], offset[1],
														offset[2] })));
								ArrayList<ArrayList<Float>> orientList = new ArrayList<ArrayList<Float>>();
								orientList.add(new ArrayList<Float>(Arrays
										.asList(new Float[] { orient[0][0],
												orient[0][1], orient[0][2] })));
								orientList.add(new ArrayList<Float>(Arrays
										.asList(new Float[] { orient[1][0],
												orient[1][1], orient[1][2] })));
								orientList.add(new ArrayList<Float>(Arrays
										.asList(new Float[] { orient[2][0],
												orient[2][1], orient[2][2] })));
								hose_constraint.put("orient", orientList);
								hose_constraint.put("partName", type);
								hose_constraint.put("description", desc);
								hose_constraint.put("LSYNTH_CONSTRAINT_CLASS",
										LSynthClassT.LSYNTH_HOSE);

								// A little post-processing
								String description = (String) hose_constraint
										.get("description");
								description = description.replaceAll("\"", "");
								ArrayList<String> descriptionParts = new ArrayList<String>(
										Arrays.asList(description.split("-")));

								// Skip constraints without a description. It
								// would be better to rely on the lsynth.mpd for
								// correct constraints rather than enshrine it
								// in code. Hopefully these lines are
								// short-lived
								if (descriptionParts.size() == 1) {
									lineIndex++;
									continue;
								}

								// Use our processed description
								hose_constraint.put("description", description);

								hose_constraints.add(hose_constraint);
								quickRefHoseConstraints.add(type.toLowerCase());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}// The description precedes the constraint definition
							// so save it for the next time round
						else if (lines.get(lineIndex).length() > 0) {
							previousLine = lines.get(lineIndex);
						}

						lineIndex++;
					}
				} // END HOSE CONSTRAINTS

				// BAND CONSTRAINTS, e.g.
				//
				// 0 // Technic Axle 2 Notched
				// 1 8 0 0 0 0 0 1 0 1 0 -1 0 0 32062.DAT

				else if (lines.get(lineIndex).equals(
						"0 SYNTH BEGIN DEFINE BAND CONSTRAINTS")) {
					lineIndex++;

					// Local block line-parsing variables. TODO: move to top
					int radius;
					float offset[] = new float[3];
					float orient[][] = new float[3][3];

					while (!lines.get(lineIndex).equals("0 SYNTH END")) {
						String token[] = lines.get(lineIndex).split(" ");
						if (token.length == 15) {
							try {
								radius = Integer.parseInt(token[1]);
								offset[0] = Float.parseFloat(token[2]);
								offset[1] = Float.parseFloat(token[3]);
								offset[2] = Float.parseFloat(token[4]);
								orient[0][0] = Float.parseFloat(token[5]);
								orient[0][1] = Float.parseFloat(token[6]);
								orient[0][2] = Float.parseFloat(token[7]);
								orient[1][0] = Float.parseFloat(token[8]);
								orient[1][1] = Float.parseFloat(token[9]);
								orient[1][2] = Float.parseFloat(token[10]);
								orient[2][0] = Float.parseFloat(token[11]);
								orient[2][1] = Float.parseFloat(token[12]);
								orient[2][2] = Float.parseFloat(token[13]);
								type = token[14];

								// Extract description
								// Big assumption: that we have useful contents
								// in the previous line
								// TODO: harden
								String desc = "";
								if (previousLine != null
										&& previousLine.split(" ").length > 1)
									desc = previousLine.split(" ")[1];

								HashMap<String, Object> band_constraint = new HashMap<String, Object>();
								band_constraint.put("radius", radius);
								band_constraint.put(
										"offset",
										new ArrayList<Float>(Arrays
												.asList(new Float[] {
														offset[0], offset[1],
														offset[2] })));
								ArrayList<ArrayList<Float>> orientList = new ArrayList<ArrayList<Float>>();
								orientList.add(new ArrayList<Float>(Arrays
										.asList(new Float[] { orient[0][0],
												orient[0][1], orient[0][2] })));
								orientList.add(new ArrayList<Float>(Arrays
										.asList(new Float[] { orient[1][0],
												orient[1][1], orient[1][2] })));
								orientList.add(new ArrayList<Float>(Arrays
										.asList(new Float[] { orient[2][0],
												orient[2][1], orient[2][2] })));
								band_constraint.put("orient", orientList);
								band_constraint.put("partName", type);
								band_constraint.put("description", desc);
								band_constraint.put("LSYNTH_CONSTRAINT_CLASS",
										LSynthClassT.LSYNTH_BAND);

								band_constraints.add(band_constraint);
								quickRefBandConstraints.add(type.toLowerCase());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} // The description precedes the constraint definition
							// so save it for the next time round
						else if (lines.get(lineIndex).length() > 0) {
							previousLine = lines.get(lineIndex);
						}
						lineIndex++;
					}
				} // END BAND CONSTRAINTS

				// SYNTH PART lines, e.g.
				//
				// 0 SYNTH PART 4297187.dat PLI_ELECTRIC_NXT_CABLE_20CM
				// ELECTRIC_NXT_CABLE
				else if (currentLine.startsWith("0 SYNTH PART ")
						&& currentLine.split(" ").length == 6) {
					String token[] = currentLine.split(" ");

					product = token[3];
					title = token[4];
					type = token[5];

					HashMap<String, Object> part = new HashMap<String, Object>();
					part.put("product", product);
					part.put("title", title.replaceAll("_", " "));
					part.put("method", type);
					part.put("LSYNTH_TYPE", title);
					part.put("LSYNTH_CLASS", "");

					tmp_parts.add(part);
					// This (& the two below) feel a little hacky. Better to
					// have them as class methods on the config.
					quickRefParts.add(title);

				} // END PART

				// HOSE DEFINITIONS, e.g.
				//
				// 0 SYNTH BEGIN DEFINE BRICK_ARC HOSE FIXED 1 100 0
				//
				// We don't care about the rest of the definition (LSynth
				// does)
				//
				else if (currentLine.startsWith("0 SYNTH BEGIN DEFINE ")
						&& currentLine.split(" ").length == 10
						&& currentLine.split(" ")[5].equals("HOSE")) {
					String token[] = currentLine.split(" ");
					type = token[4];
					stretch = token[6];
					try {
						d = Integer.parseInt(token[7]);
						st = Integer.parseInt(token[8]);
						t = Float.parseFloat(token[9]);

						HashMap<String, Object> hose_def = new HashMap<String, Object>();
						hose_def.put("title", type.replaceAll("_", " ")
								.toUpperCase());
						hose_def.put("LSYNTH_TYPE", type);
						hose_def.put("LSYNTH_CLASS", LSynthClassT.LSYNTH_HOSE);

						hose_types.add(hose_def);
						quickRefHoses.add(type);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// BAND DEFINITIONS, e.g.
				//
				// 0 SYNTH BEGIN DEFINE CHAIN BAND FIXED 0.0625 8
				//
				// We don't care about the rest of the definition (LSynth does)
				else if (currentLine.startsWith("0 SYNTH BEGIN DEFINE ")
						&& currentLine.split(" ").length == 10
						&& currentLine.split(" ")[5].equals("BAND")) {
					String token[] = currentLine.split(" ");
					type = token[4];
					fill = token[6];
					try {
						scale = Float.parseFloat(token[7]);
						thresh = Float.parseFloat(token[8]);

						HashMap<String, Object> band_def = new HashMap<String, Object>();
						band_def.put("title", type.replaceAll("_", " ")
								.toUpperCase());
						band_def.put("LSYNTH_TYPE", type);
						band_def.put("LSYNTH_CLASS", LSynthClassT.LSYNTH_HOSE);
						band_types.add(band_def);
						quickRefBands.add(type);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			lineIndex++;
		}

		// Now we've read in all the config we can go back over our SYNTH PARTs
		// and apply the correct class to them,
		// based on a matching band or hose type. Not performant, but run only
		// once at startup.

		for (HashMap<String, Object> part : tmp_parts) {
			if (getQuickRefBands().contains(part.get("method"))) {
				part.put("LSYNTH_CLASS", LSynthClassT.LSYNTH_BAND);
			} else if (getQuickRefHoses().contains(part.get("method"))) {
				part.put("LSYNTH_CLASS", LSynthClassT.LSYNTH_HOSE);
			}
			parts.add(part);
		}
	}

	// ========== isLSynthConstraint:
	// ===============================================
	//
	// Purpose: Determine if a given part is an "official" LSynth constraint,
	// i.e. defined in lsynth.ldr, and parsed into the LSynthConfiguration
	// object.
	//
	// ==============================================================================
	public boolean isLSynthConstraint(LDrawPart part) {
		if (quickRefBandConstraints.contains(part.referenceName())
				|| quickRefHoseConstraints.contains(part.referenceName())) {
			return true;
		}
		return false;
	}// end isLSynthConstraint:

	// TODO: move to properties

	public ArrayList<HashMap<String, Object>> getParts() {
		return parts;
	}

	public ArrayList<HashMap<String, Object>> getHoseTypes() {
		return hose_types;
	}

	public ArrayList<HashMap<String, Object>> getHoseConstraints() {
		return hose_constraints;
	}

	public ArrayList<HashMap<String, Object>> getBandTypes() {
		return band_types;
	}

	public ArrayList<HashMap<String, Object>> getBandConstraints() {
		return band_constraints;
	}

	public ArrayList<String> getQuickRefBands() {
		return quickRefBands;
	}

	public ArrayList<String> getQuickRefHoses() {
		return quickRefHoses;
	}

	public ArrayList<String> getQuickRefParts() {
		return quickRefParts;
	}

	public ArrayList<String> getQuickRefBandContstraints() {
		return quickRefBandConstraints;
	}

	public ArrayList<String> getQuickRefHoseConstraints() {
		return quickRefHoseConstraints;
	}

	// ========== constraintDefinitionForPart:
	// ======================================
	//
	// Purpose: Look up a constraint by part type. Not especially performant.
	// Consider adding a dictionary for lookup?
	//
	// ==============================================================================
	public HashMap<String, Object> constraintDefinitionForPart(
			LDrawPart directive) {
		for (HashMap<String, Object> constraint : hose_constraints) {
			if (((String) constraint.get("partName")).toLowerCase().equals(
					directive.referenceName())) {
				return constraint;
			}
		}

		for (HashMap<String, Object> constraint : band_constraints) {
			if (((String) constraint.get("partName")).toLowerCase().equals(
					directive.referenceName())) {
				return constraint;
			}
		}
		return null;
	} // end constraintDefinitionForPart:

	// ========== typeForTypeName:
	// ==================================================
	//
	// Purpose: Look up a band or hose definition by name. Used when the class
	// is changed. Not especially performant.
	//
	// ==============================================================================
	public HashMap<String, Object> typeForTypeName(String typeName) {
		for (HashMap<String, Object> type : band_types) {
			if (type.get("LSYNTH_TYPE").equals(typeName)) {
				return type;
			}
		}

		for (HashMap<String, Object> type : hose_types) {
			if (type.get("LSYNTH_TYPE").equals(typeName)) {
				return type;
			}
		}
		return null;
	}// end typeForTypeName:

	// ========== setLSynthClassForDirective:withType:
	// ==============================
	//
	// Purpose: Set the class of an LSynthDirective based on the part type name
	//
	// ==============================================================================
	public void setLSynthClassForDirective(LDrawLSynth directive, String type) {
		// Determine the class - hose, band or part
		if (getQuickRefHoses().contains(type)) {
			directive.setLsynthClass(LSynthClassT.LSYNTH_HOSE);
		} else if (getQuickRefBands().contains(type)) {
			directive.setLsynthClass(LSynthClassT.LSYNTH_BAND);
		} else if (getQuickRefParts().contains(type)) {
			directive.setLsynthClass(LSynthClassT.LSYNTH_PART);
		} else {
			System.out.println("Unknown LSynth type");
		}
	}
}
