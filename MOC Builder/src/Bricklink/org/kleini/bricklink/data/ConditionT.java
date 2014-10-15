/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.data;

/**
 * {@link ConditionT} Indicates whether the price guide is for new or used
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public enum ConditionT {

    N,
    U;

    @Override
    public String toString() {
        switch (this) {
        case N:
            return "New";
        case U:
            return "Used";
        }
        return super.toString();
    }
}
