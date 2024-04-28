package reduction.markov;

import java.util.Set;

/**
 * Class that generate a transition matrix based of a <code>String</code>. It counts the character that are after another.
 *
 * @author Joris MASSON
 */
public class CharMarkovTextTraining extends AbstractMarkovTextTraining {
    /**
     * Constructor
     *
     * @param text   training text
     * @param domain domain
     */
    public CharMarkovTextTraining(String text, Set<Character> domain) {
        super(text, domain);
    }

    /**
     * Constructor
     *
     * @param text training text
     */
    public CharMarkovTextTraining(String text) {
        this(text, null);
    }
}
