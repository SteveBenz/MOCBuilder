package Command;

public enum LSynthParserStateT {
	PARSER_READY_TO_PARSE, // Idle state - we've not found a SYNTH BEGIN <TYPE>
							// <COLOR> line
	PARSER_PARSING_BEGUN, // SYNTH BEGIN has been found
	PARSER_PARSING_CONSTRAINTS, // Parsing constraints
	PARSER_PARSING_SYNTHESIZED, // Parsing synthesized parts
	PARSER_SYNTHESIZED_FINISHED, // Looking for SYNTH END
	PARSER_FINISHED, // All finished.
	PARSER_STATE_COUNT;

}
