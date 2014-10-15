/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api;

/**
 * List of possible property names in configuration file.
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public enum ConfigurationProperty {

    LOGIN("BrickLink.org.kleini.bricklink.login"),
    PASSWORD("BrickLink.org.kleini.bricklink.password"),
    CONSUMER_KEY("BrickLink.org.kleini.bricklink.consumerKey"),
    CONSUMER_SECRET("BrickLink.org.kleini.bricklink.consumerSecret"),
    TOKEN_VALUE("BrickLink.org.kleini.bricklink.tokenValue"),
    TOKEN_SECRET("BrickLink.org.kleini.bricklink.tokenSecret"),
    COMMENT_REGEX("BrickLink.org.kleini.bricklink.commentRegex");

    private final String propertyName;

    ConfigurationProperty(String propertName) {
        this.propertyName = propertName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
