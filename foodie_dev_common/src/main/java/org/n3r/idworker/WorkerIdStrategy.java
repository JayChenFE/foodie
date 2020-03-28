package org.n3r.idworker;

/**
 * @author jaychenfe
 */
public interface WorkerIdStrategy {
    /**
     * initialize
     */
    void initialize();

    /**
     * availableWorkerId
     *
     * @return availableWorkerId
     */
    long availableWorkerId();

    /**
     * release
     */
    void release();
}
