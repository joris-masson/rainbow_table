package reduction.markov;

import java.util.Map;
import java.util.Set;

/**
 * Interface for the training of Markov.
 *
 * @author Joris MASSON
 */
public interface MarkovTextTraining {
    /**
     * Setter for domain
     *
     * @param newDomain the new domain
     */
    void setDomain(Set<Character> newDomain);

    /**
     * Getter for the transition matrix
     *
     * @return a transition matrix
     */
    Map<Object, Map<Object, Float>> getMatrix();
}
