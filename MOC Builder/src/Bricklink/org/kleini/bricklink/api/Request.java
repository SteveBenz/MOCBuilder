/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api;

/**
 * {@link Request}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public interface Request<T extends Response<?>> {

    String getPath();
    HttpRequestT getRequestType();

    Parameter[] getParameters();

    Parser<? extends T, ?> getParser();
}
