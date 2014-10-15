/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.data;

/**
 * {@link CompletenessT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public enum CompletenessT {

    COMPLETE('C'),
    INCOMPLETE('B'),
    SEALED('S');

    private final char identifier;

    CompletenessT(char identifier) {
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return identifier;
    }

    public static CompletenessT byId(char identifier) throws Exception {
        for (CompletenessT completeness : values()) {
            if (completeness.getIdentifier() == identifier) {
                return completeness;
            }
        }
        throw new Exception("Unknown completeness identifier " + identifier + '.');
    }
}
