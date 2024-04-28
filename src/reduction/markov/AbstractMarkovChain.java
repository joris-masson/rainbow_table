package reduction.markov;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Abstract class for MarkovChain.
 *
 * @author Joris MASSON
 */
public abstract class AbstractMarkovChain implements MarkovChain {
    /**
     * Transition matrix of the chain.
     */
    protected final Map<Object, Map<Object, Float>> matrix;

    /**
     * Seed used by the chain.
     */
    protected long seed;

    /**
     * Random generator
     */
    protected Random random;

    /**
     * Current state of the chain
     */
    protected Object state;

    /**
     * Constructor.
     *
     * @param seed   Seed used by the chain.
     * @param matrix Transition matrix of the chain.
     */
    public AbstractMarkovChain(long seed, Map<Object, Map<Object, Float>> matrix) {
        this.seed = seed;
        this.random = new Random(seed);
        this.matrix = matrix;

        Set<Object> states = matrix.keySet();
        this.state = new ArrayList<>(states).get(this.random.nextInt(0, states.size()));  // prends un état de départ aléatoire parmi les états possibles
    }

    @Override
    public Object getState() {
        return this.state;
    }

    @Override
    public void setState(Object newState) {
        this.state = newState;
    }

    @Override
    public void setSeed(long newSeed) {
        this.seed = newSeed;
        this.resetRandom();
    }

    @Override
    public Object nextState() {
        Map<Object, Float> transitions = this.matrix.get(this.state);

        if (transitions != null && !transitions.isEmpty()) {
            float randomValue = this.random.nextFloat();
            float cumulativeProbability = 0;

            for (Map.Entry<Object, Float> entry : transitions.entrySet()) {
                cumulativeProbability += entry.getValue();
                if (randomValue <= cumulativeProbability) {
                    this.state = entry.getKey();
                    break;
                }
            }
        }
        return this.state;
    }

    @Override
    public void resetRandom() {
        this.random = new Random(this.seed);
    }
}
