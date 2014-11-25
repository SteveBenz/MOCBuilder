package Command;

//
//LDrawLSynth.h
//Bricksmith
//
//Created by Robin Macharg on 16/11/2012.
//
//
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.media.opengl.GL2;

import Builder.BuilderConfigurationManager;
import Builder.LSynthConfiguration;
import Builder.OSType;
import Common.Box2;
import Common.Box3;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;
import Common.Vector3f;
import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawFile;
import LDraw.Files.LDrawMPDModel;
import LDraw.Support.ColorLibrary;
import LDraw.Support.DispatchGroup;
import LDraw.Support.ILDrawObservable;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.MatrixMath;
import LDraw.Support.Range;
import LDraw.Support.TransformComponents;
import LDraw.Support.type.CacheFlagsT;
import LDraw.Support.type.LDrawGridTypeT;
import LDraw.Support.type.MessageT;
import Notification.LDrawDirectiveDidAdded;
//The LSynth LDraw format extensions have several mandatory and several optional directives.
//The following state diagram illustrates the order that directives could occur.
//The initWithLines: parser in this class implements this state machine.
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Renderer.ILDrawRenderer;
import Window.MOCBuilder;

//TODO: need a transition between PARSER_PARSING_BEGUN and PARSER_FINISHED on 0 SYNTH END

//
//  State                                         Transitions
//  ---------------------------------------------------------------------------------------
//
//  PARSER_READY_TO_PARSE                         o
//                                                |
//                                                |    0 SYNTH BEGIN X X
//                                                V
//  PARSER_PARSING_BEGUN                          o
//                                                |    0 SYNTH SHOW or
//                                                |    1 X X X ...
//                                                V
//  PARSER_PARSING_CONSTRAINTS                  /\o
//                                 1 X X X ... |_/|
//                                                |    0 SYNTH SYNTHESIZED BEGIN
//                                                V
//  PARSER_PARSING_SYNTHESIZED                  /\o
//                                 1 X X X ... |_/|
//                                                |    0 SYNTH SYNTHESIZED END
//                                                V
//  PARSER_SYNTHESIZED_FINISHED                   o
//                                                |    0 SYNTH END
//                                                |
//                                                V
//  PARSER_FINISHED                               o
//

public class LDrawLSynth extends LDrawContainer{
	private ArrayList<LDrawPart> synthesizedParts;
	String synthType;
	LSynthClassT lsynthClass;
	LDrawColor color;
	float glTransformation[];
	boolean hidden;
	boolean subdirectiveSelected;
	Box3 cachedBounds; // cached bounds of the enclosed directives

	private LDrawMPDModel synthesizedModel = null;

	public LDrawLSynth() {
		synthesizedParts = new ArrayList<LDrawPart>();
		synthType = null;
		color = new LDrawColor();

		cachedBounds = Box3.getInvalidBox();
		invalCache(CacheFlagsT.CacheFlagBounds);

		glTransformation = new float[16];
		// Observe changes in selection display options
		// NotificationCenter.getInstance().addSubscriber(this,
		// NotificationMessageT.LSynthSelectionDisplayDidChangeNotification);
		// NotificationCenter.getInstance().addSubscriber(this,
		// NotificationMessageT.LSynthResynthesisRequiredNotification);
	}

	public static LDrawLSynth create() {
		return new LDrawLSynth();
	}// end init

	// ========== initWithLines:inRange:parentGroup:
	// ================================
	//
	// Purpose: Initializes the synthesized part with the supplied range of
	// lines
	//
	// LSynth format is
	//
	// 0 SYNTH BEGIN <SYNTH_TYPE> <COLOR_CODE>
	// 0 SYNTH SHOW
	// 1 <CONSTRAINT PART>
	// 0 SYNTH INSIDE/OUTSIDE
	// ...
	//
	// <OPTIONALLY:>
	// 0 SYNTH SYNTHESIZED BEGIN
	// 1 <SYNTHESIZED PART SPEC>
	// ...
	// 0 SYNTH SYNTHESIZED END
	// 0 SYNTH END
	//
	// ==============================================================================
	public LDrawDirective initWithLines(ArrayList<String> lines, Range range,
			DispatchGroup parentGroup) throws Exception {
		String currentLine = null;
		LDrawDirective commandClass = null;
		Range commandRange = range;
		int lineIndex = 0;
		LSynthParserStateT parserState = LSynthParserStateT.PARSER_READY_TO_PARSE;

		super.initWithLines(lines, range, parentGroup);

		cachedBounds = Box3.getInvalidBox();

		DispatchGroup stepDispatchGroup = null;
		stepDispatchGroup = new DispatchGroup();

		if (parentGroup != null)
			stepDispatchGroup.extendsFromParent(parentGroup);

		lineIndex = range.getLocation();
		while (lineIndex <= range.getMaxRange()) {
			currentLine = lines.get(lineIndex);
			//
			// '0 SYNTH' directives
			//

			// 0 SYNTH BEGIN <SYNTH_TYPE> <COLOR>
			if (currentLine.startsWith("0 SYNTH BEGIN ")
					&& parserState == LSynthParserStateT.PARSER_READY_TO_PARSE) {
				String type;
				int synthColor = -1;
				String token[] = currentLine.split(" ");
				if (token.length == 5) {
					type = token[3];
					try {
						synthColor = Integer.parseInt(token[4]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					this.synthType = type;
					this.color = ColorLibrary.sharedColorLibrary()
							.colorForCode(LDrawColorT.byValue(synthColor));
					parserState = LSynthParserStateT.PARSER_PARSING_BEGUN;
				}
			}
			// 0 SYNTH END - Synthesized parts may or may not be present
			else if (currentLine.equals("0 SYNTH END")
					&& (parserState == LSynthParserStateT.PARSER_PARSING_CONSTRAINTS || parserState == LSynthParserStateT.PARSER_SYNTHESIZED_FINISHED)) {
				parserState = LSynthParserStateT.PARSER_FINISHED;
			}

			// 0 SYNTH SHOW or
			// 0 SYNTH HIDE
			else if ((currentLine.equals("0 SYNTH SHOW") || currentLine
					.equals("0 SYNTH HIDE"))
					&& parserState == LSynthParserStateT.PARSER_PARSING_BEGUN) {
				parserState = LSynthParserStateT.PARSER_PARSING_CONSTRAINTS;
			}

			// 0 SYNTH SYNTHESIZED BEGIN - start of synthesized constraints
			else if (currentLine.equals("0 SYNTH SYNTHESIZED BEGIN")
					&& parserState == LSynthParserStateT.PARSER_PARSING_CONSTRAINTS) {
				parserState = LSynthParserStateT.PARSER_PARSING_SYNTHESIZED;
			}

			// 0 SYNTH SYNTHESIZED END - end of synthesized constraints
			else if (currentLine.equals("0 SYNTH SYNTHESIZED END")
					&& parserState == LSynthParserStateT.PARSER_PARSING_SYNTHESIZED) {
				parserState = LSynthParserStateT.PARSER_SYNTHESIZED_FINISHED;
			}

			// 0 SYNTH INSIDE or
			// 0 SYNTH OUTSIDE or
			// 0 SYNTH CROSS
			else if (parserState == LSynthParserStateT.PARSER_PARSING_CONSTRAINTS
					&& (currentLine.equals("0 SYNTH INSIDE")
							|| currentLine.equals("0 SYNTH OUTSIDE") || currentLine
								.equals("0 SYNTH CROSS"))) {
				String token[] = currentLine.split(" ");
				if (token.length == 3) {
					String direction = token[2];
					LDrawLSynthDirective directive = new LDrawLSynthDirective();

					directive.setStringValue(direction);
					addDirective(directive);
					directive.setEnclosingDirective(this);
					directive.addObserver(this);
				}
			}

			//
			// '1 XXX' Part directives - constraints or synthesized parts
			//

			else if (currentLine.startsWith("1 ")
					&& (parserState == LSynthParserStateT.PARSER_PARSING_BEGUN
							|| parserState == LSynthParserStateT.PARSER_PARSING_CONSTRAINTS || parserState == LSynthParserStateT.PARSER_PARSING_SYNTHESIZED)) {

				// Either way, create a part

				commandClass = LDrawUtilities
						.classForDirectiveBeginningWithLine(currentLine);

				commandRange = commandClass.rangeOfDirectiveBeginningAtIndex(
						lineIndex, lines, range.getMaxRange() - 1);

				LDrawDirective newDirective = commandClass.initWithLines(lines,
						commandRange, parentGroup);
				newDirective.setEnclosingDirective(this);
				newDirective.addObserver(this);

				// Add our part in the correct place
				if (parserState == LSynthParserStateT.PARSER_PARSING_CONSTRAINTS) {
					newDirective.setIconName(determineIconName(newDirective));
					addDirective(newDirective);
				}

				else if (parserState == LSynthParserStateT.PARSER_PARSING_SYNTHESIZED) {
					if (newDirective instanceof LDrawPart)
						synthesizedParts.add((LDrawPart) newDirective);
				}
			}

			//
			// Unrecognized or inappropriate directive at this point
			//

			else {
				System.out
						.println("Unexpected line in LSynth definition at line "
								+ (lineIndex + 1) + "(state: " + currentLine);
			}

			lineIndex += 1;
		}

		// If we've read in synthesized parts or don't have any constraints then
		// don't initially synthesize
		if (synthesizedParts.size() == 0 && subdirectives().size() > 0) {
			invalCache(CacheFlagsT.ContainerInvalid);
		}

		return this;
	}// end initWithLines:inRange:
		// ========== lineIsLSynthBeginning:
		// ===========================================

	//
	// Purpose: Returns if line is a 0 SYNTH BEGIN
	//
	// ==============================================================================

	public static boolean lineIsLSynthBeginning(String line) {
		if (line.startsWith("0 SYNTH BEGIN "))
			return true;
		return false;
	} // end lineIsLSynthBeginning:

	// ========== lineIsLSynthTerminator:
	// ==========================================
	//
	// Purpose: Returns if line is a 0 SYNTH END or 0 SYNTH PART (which
	// are
	// single
	// line directives)
	//
	// ==============================================================================
	public static boolean lineIsLSynthTerminator(String line) {
		if (line.equals("0 SYNTH END"))
			return true;
		return false;
	} // end lineIsLSynthTerminator:

	// ---------- rangeOfDirectiveBeginningAtIndex:inLines:maxIndex:
	// ------[static]--
	//
	// Purpose: Returns the range from the beginning to the end of the
	// step.
	// i.e. 0 SYNTH END
	//
	// ------------------------------------------------------------------------------
	@Override
	public Range rangeOfDirectiveBeginningAtIndex(int index,
			ArrayList<String> lines, int maxIndex) {

		String currentLine;
		int counter = 0;
		Range testRange = new Range(index, maxIndex - index + 1);
		int synthLength = 0;
		Range synthRange;

		currentLine = lines.get(index);

		if (currentLine.startsWith("0 SYNTH BEGIN ")) {
			// Find the last line in the synth definition: 0 SYNTH END
			for (counter = testRange.getLocation() + 1; counter <= testRange
					.getMaxRange(); counter++) {
				currentLine = lines.get(counter);
				synthLength += 1;

				if (lineIsLSynthTerminator(currentLine)) {
					// Nothing more to parse. Stop.
					synthLength += 1;
					break;
				}
			}
		}

		synthRange = new Range(index, synthLength);

		return synthRange;
	}// end rangeOfDirectiveBeginningAtIndex:inLines:maxIndex:
		//

	// ========== inspectorClassName
	// ================================================
	//
	// Purpose: Returns the name of the class used to inspect this one.
	//
	// ==============================================================================
	public String inspectorClassName() {
		return "InspectionLSynth";
	}// end inspectorClassName
		//
		// #pragma mark -
		// #pragma mark DIRECTIVES
		// #pragma mark -
		//
		// ========== insertDirective:atIndex:
		// ==========================================

	//
	// Purpose: Inserts the new directive into the step.
	//
	// ==============================================================================

	public void insertDirective(LDrawDirective directive, int index) {
		setSelected(false); // Explicitly deselect ourselves. The newly added
							// constraint gets selected.

		// Pick a badge icon depending on the LSynth class (band or hose)
		directive.setIconName(determineIconName(directive));

		super.insertDirective(directive, index);
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);
		invalCache(CacheFlagsT.ContainerInvalid);
	}// end insertDirective:atIndex:

	// ========== removeDirective:
	// ==================================================
	//
	// Purpose: Remove a contained directive (constraint etc.) and
	// resynthesize
	//
	// ==============================================================================
	public void removeDirective(LDrawDirective doomedDirective) {
		// We can leave removal to the base class
		super.removeDirective(doomedDirective);

		// TODO: Should we select the constraint at the previous index?
		setSubdirectiveSelected(false);
		setSelected(false);// remove ourselves from the selection so that we can
							// be selected by the user.
		invalCache(CacheFlagsT.ContainerInvalid);
	}

	//
	// ========== draw:viewScale:parentColor:
	// =======================================
	//
	// Purpose: Draw the synthesized part.
	//
	// ==============================================================================
	public void drawSelf(GL2 gl2, ILDrawRenderer renderer) {
		ArrayList<LDrawDirective> constraints = subdirectives();

		if (hidden == false) {
			// Draw each constraint, if:
			if (isSelected() == true || // We're selected
					subdirectiveSelected != false || // A subdirective
														// (constraint) is
														// selected
					lsynthClass == LSynthClassT.LSYNTH_BAND || // We're a Band,
																// so show
																// constraints
																// regardless
					lsynthClass == LSynthClassT.LSYNTH_PART && // We're a Band
																// PART
					partClass() == LSynthClassT.LSYNTH_BAND) {
				for (LDrawDirective currentDirective : constraints) {
					currentDirective.drawSelf(gl2, renderer);
				}
			}

			// Resynthesize if we've been invalidated (by e.g. any of our
			// constraints moving)
			// This is the only place we invoke synthesis. While it may incur a
			// small delay in drawing
			// it's lazy (in a good way), and means resynthesis only occurs when
			// we actually need it.
			if (revalCache(CacheFlagsT.ContainerInvalid) == CacheFlagsT.ContainerInvalid) {
				synthesize();
				colorSelectedSynthesizedParts(isSelected()
						|| subdirectiveSelected);
			}

			// Draw any synthesized parts as well
			for (LDrawDirective currentDirective : synthesizedParts) {
				currentDirective.drawSelf(gl2, renderer);
			}
		}

	}// end drawSelf:

	// ==========
	// hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Hit-test the geometry.
	//
	// ==============================================================================
	public void hitTest(Ray3 pickRay, Matrix4 transform,
			LDrawDirective creditObject, HashMap<LDrawDirective, Float> hits) {
		if (hidden == false) {
			ArrayList<LDrawDirective> steps = subdirectives();
			// i.e. constraints

			int counter = 0;
			LDrawDirective currentDirective;

			// Hit test the constraints first since this will be the quicker
			// test
			for (counter = 0; counter < steps.size(); counter++) {
				currentDirective = steps.get(counter);
				currentDirective
						.hitTest(pickRay, transform, creditObject, hits);
			}

			// Now do the synthesized pieces. We take the credit.
			// Thangyouverehmuch.
			// for (LDrawPart part : synthesizedParts) {
			// part.hitTest(pickRay, transform, creditObject, hits);
			// }
		}
	}// end hitTest:transform:viewScale:boundsOnly:creditObject:hits:

	// ==========
	// boxTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Check for intersections with screen-space geometry.
	//
	// ==============================================================================
	public boolean boxTest(Box2 bounds, Matrix4 transform, boolean boundsOnly,
			LDrawDirective creditObject, TreeSet<LDrawDirective> hits) {

		ArrayList<LDrawDirective> commands = subdirectives();
		int commandCount = commands.size();
		LDrawPart currentDirective = null;
		int counter = 0;

		// Do constraints
		for (counter = 0; counter < commandCount; counter++) {
			if (commands.get(counter) instanceof LDrawPart == false)
				continue;
			currentDirective = (LDrawPart) commands.get(counter);
			if (currentDirective.boxTest(bounds, transform, boundsOnly,
					creditObject, hits)) {
				if (creditObject != null) {
					return true;
				}
			}
			;
		}

		// Also check synthesized parts
		for (LDrawPart part : synthesizedParts) {
			if (part.boxTest(bounds, transform, boundsOnly, creditObject, hits)) {
				if (creditObject != null) {
					return true;
				}
			}
			;
		}

		return false;
	}// end boxTest:transform:viewScale:boundsOnly:creditObject:hits:

	// ==========
	// depthTest:inBox:transform:creditObject:bestObject:bestDepth:=======
	//
	// Purpose: depthTest finds the closest primitive (in screen space)
	// overlapping a given point, as well as its device coordinate
	// depth.
	// ==============================================================================
	public void depthTest(Vector2f testPt, Box2 bounds, Matrix4 transform,
			LDrawDirective creditObject, ArrayList<LDrawDirective> bestObject,
			FloatBuffer bestDepth) {
		ArrayList<LDrawDirective> commands = subdirectives();
		int commandCount = commands.size();
		LDrawPart currentDirective = null;
		int counter = 0;

		for (counter = 0; counter < commandCount; counter++) {
			if (commands.get(counter) instanceof LDrawPart == false)
				continue;
			currentDirective = (LDrawPart) commands.get(counter);

			currentDirective.depthTest(testPt, bounds, transform, creditObject,
					bestObject, bestDepth);
		}

		// Now do the synthesized pieces. We take the credit.
		for (LDrawPart part : synthesizedParts) {
			part.depthTest(testPt, bounds, transform, creditObject, bestObject,
					bestDepth);
		}

	}// end depthTest:inBox:transform:creditObject:bestObject:bestDepth:

	// ========== write
	// =============================================================
	//
	// Purpose: Write out all the commands in the part
	//
	// ==============================================================================
	public String write() {
		StringBuilder written = new StringBuilder();
		String CRLF = "\r\n";

		String lsynthVisibility = "SHOW";
		ArrayList<LDrawDirective> constraints = subdirectives();
		LDrawDirective currentCommand = null;
		String commandString = null;
		int numberCommands = 0;
		int counter = 0;

		// Start
		written.append("0 SYNTH BEGIN " + lsynthType() + " "
				+ color.colorCode().getValue() + CRLF);
		written.append("0 SYNTH " + lsynthVisibility + CRLF);

		numberCommands = constraints.size();
		for (counter = 0; counter < numberCommands; counter++) {
			currentCommand = constraints.get(counter);
			commandString = currentCommand.write();
			;
			written.append(commandString);
			written.append(CRLF);
		}

		// Write out synthesized parts, if there are any to write out
		if (synthesizedParts.size() > 0) {
			written.append("0 SYNTH SYNTHESIZED BEGIN");
			written.append(CRLF);
			for (LDrawPart part : synthesizedParts) {
				// Parts aren't smart enough to know that they're temporarily
				// coloured differently
				// during Synth part selection, so we force the parent color in.
				// It's reset when the
				// part selection changes, anyway.
				part.setLDrawColor(color);
				written.append(part.write());
				written.append(CRLF);
			}
			written.append("0 SYNTH SYNTHESIZED END");
			written.append(CRLF);
		}
		// End
		written.append("0 SYNTH END");
		written.append(CRLF);

		return written.toString();
	}// end write
		//
		// #pragma mark -
		// #pragma mark DISPLAY
		// #pragma mark -

	// ========== browsingDescription
	// ===============================================
	//
	// Purpose: Returns a representation of the directive as a short
	// string
	// which can be presented to the user.
	//
	// ==============================================================================
	public String browsingDescription() {
		LSynthConfiguration config = LSynthConfiguration.sharedInstance();
		HashMap<String, Object> entry = config.typeForTypeName(synthType);
		String description = null;

		if (entry != null)
			description = (String) entry.get("title");

		if (description == null)
			description = synthType;

		// Show the number of parts
		// TODO: allow for single-piece objects like tubes/string etc. Meanwhile
		// just show the part type
		// The part type's <fill> param (FIXED or STRETCH( should serve in
		// deciding this.
		// return [String stringWithFormat:@"%@ (%i pieces)", synthType,
		// [synthesizedParts count]];
		return description;

	}// end browsingDescription

	// ========== iconName
	// ==========================================================
	//
	// Purpose: Returns the name of image file used to display this kind
	// of
	// object, or nil if there is no icon.
	//
	// ==============================================================================
	public String iconName() {
		return "LSynthPart";

	}// end iconName
		//
		// #pragma mark -
		// #pragma mark ACCESSORS
		// #pragma mark -
		//
		// ========== boundingBox3
		// ======================================================

	//
	// Purpose: Returns the minimum and maximum points of the box which
	// perfectly contains this object.
	//
	// ==============================================================================

	public Box3 boundingBox3() {
		if (revalCache(CacheFlagsT.CacheFlagBounds) == CacheFlagsT.CacheFlagBounds) {
			cachedBounds = LDrawUtilities
					.boundingBox3ForDirectives(subdirectives());
		}
		return cachedBounds;
	}

	// ========== setLsynthClass:
	// ====================================================
	//
	// Purpose: Sets the class of the Synthesized part, Pneumatic tube,
	// or Technic chain etc.
	//
	// ==============================================================================
	public void setLsynthClass(LSynthClassT classT) {
		lsynthClass = classT;
	}// end setLsynthClass:

	// ========== lsynthClass:
	// ====================================================
	//
	// Purpose: Return the class of the Synthesized part.
	//
	// ==============================================================================

	public LSynthClassT lsynthClass() {
		return lsynthClass;
	}// end lsynthClass:

	// ========== setLsynthType:
	// ====================================================
	//
	// Purpose: Sets the type of the Synthesized part, band, chain or part
	//
	// ==============================================================================
	public void setLsynthType(String type) {
		synthType = type;
	}// end setLsynthType:

	// ========== lsynthClass:
	// ====================================================
	//
	// Purpose: Return the type of the Synthesized part.
	//
	// ============================================================================
	public String lsynthType() {
		return synthType;
	}// end

	// ========== setHidden:
	// ========================================================
	//
	// Purpose: Sets whether this part will be drawn, or whether it will be
	// skipped during drawing. This setting only affects drawing;
	// hidden parts will always be written out. Also, note that
	// hiddenness is a temporary state; it is not saved and restored.
	//
	// ==============================================================================
	public void setHidden(boolean flag) {
		if (hidden != flag) {
			hidden = flag;
			enclosingDirective().setVertexesNeedRebuilding();
			invalCache(CacheFlagsT.CacheFlagBounds);
			invalCache(CacheFlagsT.DisplayList);
		}

	}// end setHidden:

	// ========== isHidden
	// ==========================================================
	//
	// Purpose: Returns whether this element will be drawn or not.
	//
	// ==============================================================================
	public boolean isHidden() {
		return hidden;

	}// end isHidden

	// ========== transformComponents
	// ===============================================
	//
	// Purpose: Returns the individual components of the transformation matrix
	// applied to this part.
	//
	// ==============================================================================
	public TransformComponents transformComponents() {
		Matrix4 transformation = transformationMatrix();
		TransformComponents components = TransformComponents
				.getIdentityComponents();

		// This is a pretty darn neat little function. I wish I could say I
		// wrote it.
		// It will extract all the user-friendly components out of this nasty
		// matrix.
		MatrixMath.Matrix4DecomposeTransformation(transformation, components);

		return components;

	}// end transformComponents

	// ========== setSelected:
	// ======================================================
	//
	// Purpose: Custom (de)selection action. We want to make our part
	// transparent
	// when selected.
	//
	// ==============================================================================
	public void setSelected(boolean flag) {
		super.setSelected(flag);
		// We don't need to resynthesize just to change the colours
		colorSelectedSynthesizedParts(flag);
		for(LDrawDirective directive : subdirectives())
			directive.setSelected(flag);
	}// end setSelected:

	// ========== setSubdirectiveSelected:
	// =========================================
	//
	// Purpose: Set the flag denoting whether a subdirective is selected
	// Also colors the synthesized parts translucent
	//
	// ==============================================================================
	public void setSubdirectiveSelected(boolean flag) {
		subdirectiveSelected = flag;
	}

	// #pragma mark <LDrawColorable> protocol methods

	// ========== setLDrawColor:
	// ====================================================
	//
	// Purpose: Sets the color of the synthesized tube. This may be temporarily
	// overridden for certain operations but WILL be the one saved out.
	//
	// ==============================================================================
	public void setLDrawColor(LDrawColor newColor) {
		// Store the color
		color = newColor;
		
		for(LDrawDirective directive : subdirectives()){
			if(directive instanceof LDrawPart)
				((LDrawPart)directive).setLDrawColor(newColor);			
		}
		
		synthesize();
	}// end setLDrawColor:

	// ========== LDrawColor
	// ========================================================
	//
	// Purpose: Returns the LDraw color code of the receiver.
	//
	// ==============================================================================
	public LDrawColor getLDrawColor() {
		return color;
	}// end LDrawColor

	// #pragma mark -
	// #pragma mark <LDrawMovableElement> protocol methods
	// #pragma mark -
	//
	// ========== displacementForNudge:
	// =============================================
	//
	// Purpose: Determine the displacement for a specific nudge. We rely on the
	// first subdirective that can help; all subdirectives will move
	// same amount.
	//
	// ==============================================================================
	public Vector3f displacementForNudge(Vector3f nudgeVector) {
		for (LDrawDirective directive : subdirectives()) {
			if (directive instanceof LDrawDrawableElement
					&& directive instanceof LDrawMovableDirective) {
				return ((LDrawMovableDirective) directive)
						.displacementForNudge(nudgeVector);
			}
		}
		Vector3f v = new Vector3f(0, 0, 0);
		return v;

	}

	// ========== moveBy:
	// ===========================================================
	//
	// Purpose: Passes a movement request down to its subdirectives
	//
	// Optimisation: move all synthesized elements as well to save a resynth
	//
	// ==============================================================================
	public void moveBy(Vector3f moveVector) {
		// pass on the nudge to drawable subdirectives
		for (LDrawDirective constraint : subdirectives()) {
			if (constraint instanceof LDrawMovableDirective) {
				((LDrawPart) constraint).moveBy(moveVector);
			}
		}
	}// end moveBy:
		//
		//
		// #pragma mark -
		// #pragma mark UTILITY FUNCTIONS
		// #pragma mark -
		//
		// ========== synthesize
		// ========================================================

	//
	// Purpose: Synthesizes the part using LSynth
	//
	// TODO: multithread/background
	//
	// ==============================================================================
	private static HashMap<LDrawLSynth, String> synthesizeModelNameMap = null;
	private static LDrawFile previousFile = null;

	private boolean isSynthesizeCompleted = true;
	private boolean needOneMoreSynthesize = false;

	public void synthesize() {
		if (isSynthesizeCompleted) {
			isSynthesizeCompleted = false;
			new Thread(new Runnable() {
				@Override
				public void run() {
					synthesize_thread();
					while (needOneMoreSynthesize) {
						needOneMoreSynthesize = false;
						synthesize_thread();
					}
					isSynthesizeCompleted = true;
					
					NotificationCenter.getInstance().postNotification(
							NotificationMessageT.NeedRedraw);
				}
			}).start();
		} else
			needOneMoreSynthesize = true;
	}

	public void synthesize_thread() {
		if (size() <= 1)
			return;
		if (synthesizeModelNameMap == null
				|| previousFile == null
				|| previousFile != MOCBuilder.getInstance()
						.getWorkingLDrawFile()) {
			synthesizeModelNameMap = new HashMap<LDrawLSynth, String>();
		}

		if (previousFile == null
				|| MOCBuilder.getInstance().getWorkingLDrawFile() != previousFile)
			previousFile = MOCBuilder.getInstance().getWorkingLDrawFile();

		String modelName = null;
		if (synthesizeModelNameMap.containsKey(this))
			modelName = synthesizeModelNameMap.get(this);
		else {
			int counter = 1;
			modelName = synthType + " " + getLDrawColor().colorCode() + "_"
					+ counter;
			while (synthesizeModelNameMap.containsValue(modelName) == true) {
				counter++;
				modelName = synthType + " " + getLDrawColor().colorCode() + "_"
						+ counter;
			}
			synthesizeModelNameMap.put(this, modelName);
		}

		if (synthesizedModel == null
				&& MOCBuilder.getInstance().getWorkingLDrawFile()
						.modelWithName(modelName) != null) {
			synthesizedModel = MOCBuilder.getInstance().getWorkingLDrawFile()
					.modelWithName(modelName);
			synthesizedModel.addObserver(this);

			LDrawPart part = new LDrawPart();
			part.initWithPartName(modelName, new Vector3f());
			part.setDisplayName(modelName);
			part.resolvePart();
			part.setEnclosingDirective(this);
			synthesizedParts.clear();
			synthesizedParts.add(part);
			
			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.LDrawDirectiveDidAdded, new LDrawDirectiveDidAdded(this, part));
		}

		// NSLog(@"SYNTHESIZE");

		// Modifies the constraints to provide automatic OUTSIDE/INSIDE
		// determination for
		// constraints inside the convex hull. Dig down for more details.
		boolean doAutoHull = true; // Placeholder until we make it a
									// configurable setting
		if (doAutoHull == true && lsynthClass == LSynthClassT.LSYNTH_BAND) {
			// TODO: Turned off while the Inspector code is fleshed out
			// [self doAutoHullOnBand];
		}

		// Clean up first
		// synthesizedParts.clear();

		String input = "";
		// Path to lsynth. If it's unset or whitespace use the built-in default
		String executablePath = null;
		if(BuilderConfigurationManager.getOSType() == OSType.Window)
		executablePath = BuilderConfigurationManager.getInstance()
				.getLSynthDirectory() + "lsynthcp.exe";
		else if(BuilderConfigurationManager.getOSType() == OSType.Mac)
			executablePath = BuilderConfigurationManager.getInstance()
			.getLSynthDirectory() + "lsynthcp";
		else
			executablePath = "";

		// We run LSynth as follows:
		// - Create an LDraw file in memory
		// - Setup the STDIN/OUT pipes and NSTask
		// - Launch task
		// - Write to LSynth's STDIN, read from its STDOUT
		// - Process the output (using LDrawDirective's parser) into synthesized
		// parts

		// Create an LDraw file in memory
		LDrawColorT code = getLDrawColor().colorCode();
		input = input + "0 SYNTH BEGIN " + synthType + " " + code.getValue()
				+ "\r\n";
		input = input + "0 SYNTH SHOW\r\n";
		for (LDrawDirective part : subdirectives()) {
			input = input + part.write() + "\r\n";
		}
		input = input + "0 SYNTH END\r\n";
		input = input + "0 STEP\r\n";

		try {
			String inputFilePath = BuilderConfigurationManager
					.getDefaultDataDirectoryPath()
					+ "lsynthTemp_constraint_"
					+ modelName + ".dat";
			File file = new File(inputFilePath);
			FileWriter fw = new FileWriter(file);
			fw.write(input);
			fw.close();
			String outputFilePath = BuilderConfigurationManager
					.getDefaultDataDirectoryPath()
					+ "lsynthTemp_synthesis_"
					+ modelName + ".dat";
			Process oProcess = new ProcessBuilder(executablePath, "-l",
					inputFilePath, outputFilePath).start();
			oProcess.waitFor();

			LDrawFile ldrawFile = LDrawFile
					.fileFromContentsAtPath(outputFilePath);
			ldrawFile.activeModel().setModelName(modelName);

			LDrawMPDModel newModel = ldrawFile.activeModel();

			if (synthesizedModel == null || synthesizedModel.modelName().equals(modelName)==false) {
				synthesizedModel = newModel;
				newModel.addObserver(this);
				MOCBuilder.getInstance().getWorkingLDrawFile()
						.addSubmodel(newModel);
				LDrawPart part = new LDrawPart();
				part.initWithPartName(modelName, new Vector3f());
				part.setDisplayName(modelName);
				part.resolvePart();
				part.setEnclosingDirective(this);
				synthesizedParts.clear();
				synthesizedParts.add(part);
				
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.LDrawModelDidChanged);
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.LDrawDirectiveDidAdded, new LDrawDirectiveDidAdded(this, part));
			} else {
				synthesizedModel.clear();
				for (LDrawDirective directive : newModel.subdirectives())
					synthesizedModel.addDirective(directive);

				for (LDrawPart part : synthesizedParts)
					part.moveTo(new Vector3f(), LDrawGridTypeT.Coarse);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// //========== doAutoHullOnBand
	// ==================================================
	// //
	// // Purpose: Calculate the INSIDE/OUTSIDE directives automatically.
	// //
	// // Based on calculating the convex hull of the constraints and
	// inserting
	// // INSIDE/OUTSIDE directives appropriately.
	// //
	// //==============================================================================
	// - (void)doAutoHullOnBand
	// {
	// // clean out INSIDE/OUTSIDE directives
	// int i;
	// for (i = [[self subdirectives] count] - 1; i >= 0; i--) {
	// if ([[[self subdirectives] objectAtIndex:i]
	// isKindOfClass:[LDrawLSynthDirective class]]) {
	// [[self subdirectives] removeObjectAtIndex:i];
	// }
	// }
	// //NSLog(@"Cleaned subdirs: %@", [self subdirectives]);
	//
	// // Prepare the constraints for calculating the convex hull.
	// NSMutableArray *preparedData = [self prepareAutoHullData];
	//
	// // Determine the Convex Hull. This is the meat. After this we know
	// // which constraints are really on the hull. We respect their radii..
	// [ComputationalGeometry doJarvisMarch:preparedData];
	//
	// //NSLog(@"Prepared Data After: %@", preparedData);
	//
	// // Reintegrate our hull-determined data. We'll likely have multiple
	// // points all on the hull, each associated with a single constraint.
	// // This boils them down to a set of constraints on the hull.
	// NSMutableSet *hullConstraints = [[[NSMutableSet alloc] init]
	// autorelease];
	// for (NSMutableDictionary *point in preparedData) {
	// if ([[point objectForKey:@"inHull"] integerValue] == YES) {
	// [hullConstraints addObject:[point objectForKey:@"directive"]];
	// }
	// }
	// //NSLog(@"hullConstraints: %@", hullConstraints);
	//
	// // Knowing which constraints are on the hull allows us to add
	// // INSIDE/OUTSIDE constraints as we iterate over them.
	// // We could modify the constraints in-place but recreating the
	// // subdirectives array is simpler.
	// NSMutableArray *newConstraints = [[[NSMutableArray alloc] init]
	// autorelease];
	//
	// for (i=0; i<[[self subdirectives] count]; i++) {
	// LDrawPart *part = [[self subdirectives] objectAtIndex:i];
	// LDrawPart *nextPart = [[self subdirectives] objectAtIndex:((i+1) %
	// [[self
	// subdirectives] count])];
	//
	// // The first point is potentially a special case. Handle it.
	// // Not on the hull? Then prepend an OUTSIDE
	// if (i == 0 && ![hullConstraints containsObject:part]) {
	// LDrawLSynthDirective *OUTSIDE = [[LDrawLSynthDirective alloc] init];
	// [OUTSIDE setStringValue:@"OUTSIDE"];
	// [newConstraints addObject:OUTSIDE];
	// [OUTSIDE release];
	// }
	//
	// // This part is on the hull (i.e. INSIDE the band) and the next part
	// is
	// // NOT on hull (i.e. OUTSIDE)
	// if ( [hullConstraints containsObject:part] &&
	// ![hullConstraints containsObject:nextPart]) {
	// // generate OUTSIDE
	// LDrawLSynthDirective *OUTSIDE = [[LDrawLSynthDirective alloc] init];
	// [OUTSIDE setStringValue:@"OUTSIDE"];
	// [newConstraints addObject:part];
	// [newConstraints addObject:OUTSIDE];
	// [OUTSIDE release];
	// }
	//
	// // This part is not on the hull (i.e. OUTSIDE the band) and the next
	// part
	// IS
	// // on hull (i.e. INSIDE)
	// else if ( ![hullConstraints containsObject:part] &&
	// [hullConstraints containsObject:nextPart]){
	// // generate INSIDE
	// LDrawLSynthDirective *INSIDE = [[LDrawLSynthDirective alloc] init];
	// [INSIDE setStringValue:@"INSIDE"];
	// [newConstraints addObject:part];
	// [newConstraints addObject:INSIDE];
	// [INSIDE release];
	// }
	//
	// // The constraint has the same hull membership as the next one so
	// // no change of direction. Just add it.
	// else {
	// [newConstraints addObject:part];
	// }
	//
	// //NSLog(@"New Constraints: %@", newConstraints);
	// }
	//
	// // Finally, update the constraints
	// [[self subdirectives] removeAllObjects];
	// [[self subdirectives] addObjectsFromArray:newConstraints];
	// }
	//
	// //========== prepareAutoHullData
	// ===============================================
	// //
	// // Purpose: Prepare a datastructure containing points, directives
	// etc.,
	// ready for
	// // calculating the convex hull.
	// //
	// //==============================================================================
	// - (NSMutableArray *)prepareAutoHullData {
	//
	// // Used for looking up constraint radii
	// LSynthConfiguration *config = [LSynthConfiguration sharedInstance];
	//
	// // Map each constraint to XY plane, based on the orientation of the
	// first
	// constraint
	// // We build up details for each constraint in mappedPoints as we
	// progress.
	// // The inverse of the first constraint's transformation moves it back
	// to
	// (0,0,0).
	// // The same inverse transform will do similar for the other
	// constraints
	// NSMutableArray *mappedPoints = [[[NSMutableArray alloc] init]
	// autorelease];
	// Matrix4 transform = [[[self subdirectives] objectAtIndex:0]
	// transformationMatrix];
	// Matrix4 inverseTransform = Matrix4Invert(transform);
	// for (LDrawPart *part in [self subdirectives]) {
	// Matrix4 transformed;
	// transformed = Matrix4Multiply([part transformationMatrix],
	// inverseTransform);
	// TransformComponents t;
	// Matrix4DecomposeTransformation(transformed, &t);
	// NSMutableDictionary *point = [NSMutableDictionary
	// dictionaryWithObjects:[NSArray
	// arrayWithObjects:part,
	// [NSNumber numberWithFloat:t.translate.x],
	// [NSNumber numberWithFloat:t.translate.y],
	// [NSNumber numberWithInt:[[[config constraintDefinitionForPart:part]
	// valueForKey:@"radius"] integerValue]],
	// [NSMutableArray array],
	// nil]
	// forKeys:[NSArray arrayWithObjects:@"directive", @"x", @"y", @"r",
	// @"hullPoints", nil]];
	// [mappedPoints addObject:point];
	// }
	// //NSLog(@"Mapped Points: %@", mappedPoints);
	//
	// // Generate hull points by calculating "outside" tangents for each
	// pair
	// of
	// // constraint-derived circles, e.g. ((0,1), (1,2), ..., (N,0)
	// // Some of these will be on the inside of the convex hull but that's
	// OK
	// since the
	// // convex hull calculation will discard them.
	// // TODO: can we take advantage of CCW ordering of constraints?
	// int i;
	// for (i=0; i<[mappedPoints count]; i++) {
	// int j = (i+1) % [mappedPoints count]; // next constraint, cyclical
	// (N+1
	// -> 0)
	//
	// NSArray *tangents = [ComputationalGeometry
	// tangentBetweenCircle:[mappedPoints objectAtIndex:i]
	// andCircle:[mappedPoints objectAtIndex:j]];
	// if (tangents != nil) {
	// // Tangents are between two circles (i.e. constraints)
	// // add both outside tangent points for the current constraint
	// [[[mappedPoints objectAtIndex:i] objectForKey:@"hullPoints"]
	// addObject:
	// [NSDictionary dictionaryWithObjects:[NSArray
	// arrayWithObjects:[[tangents
	// objectAtIndex:0] objectAtIndex:0],
	// [[tangents objectAtIndex:0] objectAtIndex:1], nil]
	// forKeys:[NSArray arrayWithObjects:@"x", @"y", nil]]];
	//
	// [[[mappedPoints objectAtIndex:i] objectForKey:@"hullPoints"]
	// addObject:
	// [NSDictionary dictionaryWithObjects:[NSArray
	// arrayWithObjects:[[tangents
	// objectAtIndex:1] objectAtIndex:0],
	// [[tangents objectAtIndex:1] objectAtIndex:1], nil]
	// forKeys:[NSArray arrayWithObjects:@"x", @"y", nil]]];
	//
	// // add both outside tangent points for the next constraint
	// [[[mappedPoints objectAtIndex:j] objectForKey:@"hullPoints"]
	// addObject:
	// [NSDictionary dictionaryWithObjects:[NSArray
	// arrayWithObjects:[[tangents
	// objectAtIndex:0] objectAtIndex:2],
	// [[tangents objectAtIndex:0] objectAtIndex:3], nil]
	// forKeys:[NSArray arrayWithObjects:@"x", @"y", nil]]];
	//
	// [[[mappedPoints objectAtIndex:j] objectForKey:@"hullPoints"]
	// addObject:
	// [NSDictionary dictionaryWithObjects:[NSArray
	// arrayWithObjects:[[tangents
	// objectAtIndex:1] objectAtIndex:2],
	// [[tangents objectAtIndex:1] objectAtIndex:3], nil]
	// forKeys:[NSArray arrayWithObjects:@"x", @"y", nil]]];
	// }
	//
	// //NSLog(@"Tangents: %@", tangents);
	// }
	// //NSLog(@"Mapped Points after tangent calc: %@", mappedPoints);
	//
	// // Prepare the mappedPoints for the Convex Hull algorithm
	// // We create a dictionary for each hull point for each mappedPoint
	// // We'll reintegrate later to decide which constraints are in or out
	// // (in doAutoHullOnBand)
	// NSMutableArray *preparedData = [[[NSMutableArray alloc] init]
	// autorelease];
	// for (NSMutableDictionary *point in mappedPoints) {
	// for (NSMutableDictionary *coords in [point
	// objectForKey:@"hullPoints"]) {
	// //NSLog(@"Point: %@", coords);
	// // TODO: check that int values are OK. Prob. should use float?
	//
	// [preparedData addObject:[NSMutableDictionary
	// dictionaryWithObjects:[NSArray arrayWithObjects:[point
	// objectForKey:@"directive"],
	// [NSNumber numberWithInt:[[coords objectForKey:@"x"] integerValue]],
	// [NSNumber numberWithInt:[[coords objectForKey:@"y"] integerValue]],
	// [NSNumber numberWithBool:false],
	// nil]
	// forKeys:[NSArray arrayWithObjects:@"directive", @"x", @"y",
	// @"inHull",
	// nil]]];
	// }
	// }
	// //NSLog(@"Prepared Data: %@", preparedData);
	// return preparedData;
	// }//end prepareAutoHullData

	// ========== determineIconName:
	// ================================================
	//
	// Purpose: Determine the name of the constraint icon depending on the type
	// of synthesized part.
	//
	// ==============================================================================

	public String determineIconName(LDrawDirective directive) {
		// Hose
		if (lsynthClass == LSynthClassT.LSYNTH_HOSE) {
			return "LSynthHoseConstraint";
		}

		// Band
		else if (lsynthClass == LSynthClassT.LSYNTH_BAND) {
			return "LSynthBandConstraint";
		}

		// Part
		else if (lsynthClass == LSynthClassT.LSYNTH_PART) {
			if (partClass() == LSynthClassT.LSYNTH_HOSE) {
				return "LSynthHoseConstraint";
			} else if (partClass() == LSynthClassT.LSYNTH_BAND) {
				return "LSynthBandConstraint";
			}
		}

		// Other?
		return "Brick";
	}// end determineIconName:

	// ========== colorSelectedSynthesizedParts:
	// ====================================
	//
	// Purpose: Change the appearance of selected synthesized parts according
	// to user preferences so that they can better see the object and
	// its constraints.
	//
	// ==============================================================================
	public void colorSelectedSynthesizedParts(boolean yesNo) {
		// LSynthSelectionModeT selectionMode = [userDefaults
		// integerForKey:LSYNTH_SELECTION_MODE_KEY];
		// float rgba[] = new float[4]; // a temporary RGBA color we create and
		// manipulate
		// LDrawColor theColor = new LDrawColor(); // an LDrawColor to set the
		// part's color with
		//
		// // Is the part selected?
		// if (yesNo == true) {
		// // Modify the transparency, but use the object's existing color
		// if (selectionMode == TransparentSelection) {
		// color.getColorRGBA(rgba);
		// rgba[3] = ((float)[userDefaults
		// integerForKey:LSYNTH_SELECTION_TRANSPARENCY_KEY]) / 100;
		// }
		//
		// // Modify the color, with full opacity/no transparency
		// else if (selectionMode == ColoredSelection) {
		// NSColor *selectionColor = [userDefaults
		// colorForKey:LSYNTH_SELECTION_COLOR_KEY];
		// rgba[0] = [selectionColor redComponent];
		// rgba[1] = [selectionColor greenComponent];
		// rgba[2] = [selectionColor blueComponent];
		// rgba[3] = 1.0; // fully opaque
		// }
		//
		// // Modify both color and transparency
		// else if (selectionMode == TransparentColoredSelection) {
		// NSColor *selectionColor = [userDefaults
		// colorForKey:LSYNTH_SELECTION_COLOR_KEY];
		// rgba[0] = [selectionColor redComponent];
		// rgba[1] = [selectionColor greenComponent];
		// rgba[2] = [selectionColor blueComponent];
		// rgba[3] = ((float)[userDefaults
		// integerForKey:LSYNTH_SELECTION_TRANSPARENCY_KEY]) / 100;
		// }
		//
		// [theColor setColorRGBA:rgba];
		// }
		//
		// // The part's not selected so use its actual color
		// else {
		// theColor = color;
		// }
		//
		// // Recolor the synthesized parts
		// for (LDrawPart *part in synthesizedParts) {
		// [part setLDrawColor:theColor];
		// }

	} // end colorSelectedSynthesizedParts:

	// ========== transformationMatrix
	// ==============================================
	//
	// Purpose: Returns a two-dimensional (row matrix) representation of the
	// part's transformation matrix.
	//
	// +- -+
	// +- -+ +- -+| a d g 0 |
	// |a d g 0 b e h c f i 0 x y z 1| --> |x y z 1|| b e h 0 |
	// +- -+ +- -+| c f i 0 |
	// | x y z 1 |
	// +- -+
	// OpenGL Matrix Format LDraw Matrix
	// (flat column-major of transpose) Format
	//
	// ==============================================================================
	public Matrix4 transformationMatrix() {
		return MatrixMath.Matrix4CreateFromGLMatrix4(glTransformation);
	}// end transformationMatrix

	// ========== acceptsDroppedDirective:
	// ==========================================
	//
	// Purpose: Returns YES if this container will accept a directive dropped on
	// it.
	//
	// ==============================================================================
	public boolean acceptsDroppedDirective(LDrawDirective directive) {
		// Only add valid parts as constraints; invalid ones are passed to our
		// container
		// This arises if a synth part is selected and a part is dragged from
		// the
		// part chooser to the view.
		if ((directive instanceof LDrawPart && LSynthConfiguration
				.sharedInstance().isLSynthConstraint((LDrawPart) directive))
				|| directive instanceof LDrawLSynthDirective) {
			return true;
		}
		return false;
	}

	// ========== cleanupAfterDrop
	// ====================================================
	//
	// Purpose: Called as part of a drag and drop operation.
	// The argument indicates whether we were a donating parent or not.
	// this in turn affect whether we should be selected.
	//
	// TODO: Should be a protocol
	//
	// ==============================================================================
	public void cleanupAfterDropIsDonor(boolean isDonor) {
		setSelected(false);

		if (isDonor == false) {
			setSubdirectiveSelected(true);
		}
	} // end cleanupAfterDrop

	// ========== synthesizedPartsCount
	// =============================================
	//
	// Purpose: Returns the number of parts synthesized to create the shape.
	//
	// ==============================================================================
	public int synthesizedPartsCount() {
		return synthesizedParts.size();
	}

	public ArrayList<LDrawPart> synthesizedParts() {
		return new ArrayList<LDrawPart>(synthesizedParts);
	}

	// ========== partClass
	// =========================================================
	//
	// Purpose: Returns the class of a part
	// TODO: Optimise part class determination into a config dict and
	// remove the loops.
	//
	// ==============================================================================
	public LSynthClassT partClass() {
		LSynthClassT classType = lsynthClass;

		if (lsynthClass == LSynthClassT.LSYNTH_PART) {
			ArrayList<HashMap<String, Object>> partTypes = LSynthConfiguration
					.sharedInstance().getParts();

			// Loop over the parts from config, and when we find one matching
			// ourselves
			// use that part's class.
			for (HashMap<String, Object> part : partTypes) {
				if (lsynthType().equals(part.get("LSYNTH_TYPE").toString())) {
					classType = (LSynthClassT) part.get("LSYNTH_CLASS");
					break;
				}
			}
		} else {
			// For some reason we've been called when we're not a Part so trust
			// that
			// we have the correct class.
		}

		return classType;
	}

	//
	//
	// #pragma mark -
	// #pragma mark NOTIFICATIONS
	// #pragma mark -
	//
	// ========== receiveMessage:who:
	// ===============================================
	//
	// Purpose: The things we observe call this when something one-time and
	// eventful happens - we can respond if desired.
	//
	// ==============================================================================
	public void receiveMessage(MessageT msg, ILDrawObservable observable) {
		// Typically if one of our child constraints changed we need to
		// resynthesize
		switch (msg) {
		case MessageObservedChanged:
			if (observable != synthesizedModel)
				invalCache(CacheFlagsT.ContainerInvalid);
			break;
		case MessageSayGoodbye:
			synthesizedModel = null;
			break;
		}
	}

	// ========== selectionDisplayOptionsDidChange:
	// =================================
	//
	// Purpose: The selection style has changed, so we may need to redraw.
	// We get here via a LSynthSelectionDisplayDidChangeNotification
	// probably sent from the preferences controller.
	//
	// ==============================================================================
	public void selectionDisplayOptionsDidChange(LDrawDirective sender) {
		colorSelectedSynthesizedParts(isSelected() || subdirectiveSelected);
		noteNeedsDisplay();
	} // end selectionDisplayOptionsDidChange:

	// ========== requiresResynthesis:
	// ==============================================
	//
	// Purpose: LSynth parts need resynthesized explicitly for some reason.
	// We get here via a LSynthResynthesisRequiredNotification
	// probably sent from the preferences controller in response to an
	// executable or config file change.
	//
	// ==============================================================================
	public void requiresResynthesis(LDrawDirective sender) {
		synthesize();
		colorSelectedSynthesizedParts(isSelected() || subdirectiveSelected);
		noteNeedsDisplay();
	} // end requiresResynthesis:
}
