package reduction.markov;

import java.util.Map;

/**
 * Markov chain class, generate a char based on a transition matrix trained by <code>CharMarkovTextTraining</code> class.
 *
 * @author Joris MASSON
 * @see CharMarkovTextTraining
 */
public class CharMarkovChain extends AbstractMarkovChain {
    /**
     * Constructor.
     *
     * @param seed   Seed used by the chain.
     * @param matrix Transition matrix of the chain.
     */
    public CharMarkovChain(long seed, Map<Object, Map<Object, Float>> matrix) {
        super(seed, matrix);
    }
}
