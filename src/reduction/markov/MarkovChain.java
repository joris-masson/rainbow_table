package reduction.markov;

/**
 * Interface for markov chains
 *
 * @author Joris MASSON
 */
public interface MarkovChain {
    /**
     * Get the current state.
     *
     * @return current state
     */
    Object getState();

    /**
     * Setter for state.
     *
     * @param newState new state
     */
    void setState(Object newState);

    /**
     * Setter for random seed.
     *
     * @param seed new random seed.
     */
    void setSeed(long seed);

    /**
     * Calculate the next state.
     *
     * @return the new state.
     */
    Object nextState();

    /**
     * Reset the current random instance.
     */
    void resetRandom();
}
