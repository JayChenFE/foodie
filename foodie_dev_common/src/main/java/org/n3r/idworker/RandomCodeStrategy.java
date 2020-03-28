package org.n3r.idworker;

/**
 * @author jaychenfe
 */
public interface RandomCodeStrategy {
    /**
     * init
     */
    void init();

    /**
     * prefix
     *
     * @return prefixIndex
     */
    int prefix();

    /**
     * next
     *
     * @return nextIndex
     */
    int next();

    /**
     * release
     */
    void release();
}
