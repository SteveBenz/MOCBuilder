/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bricklink.org.kleini.bricklink.data.OrderDT;

/**
 * Helper methods for orders.
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class OrderHelper {

    private final Pattern commentPattern;

    public OrderHelper(String commentRegex) {
        super();
        this.commentPattern = Pattern.compile(commentRegex);
    }

    public boolean isNotBilled(OrderDT order) throws Exception {
        String remarks = order.getRemarks();
        if (null == remarks) {
            return true;
        }
        Matcher matcher = commentPattern.matcher(remarks);
        final boolean retval;
        if (matcher.matches()) {
            String billingNumber = matcher.group("billed");
            if (null == billingNumber) {
                throw new Exception("Can not extract billing number from order remarks \"" + remarks + "\".");
            }
            retval = 0 == billingNumber.length();
        } else {
            throw new Exception("Order remarks regular expression does not match on \"" + remarks + "\".");
        }
        return retval;
    }
}
