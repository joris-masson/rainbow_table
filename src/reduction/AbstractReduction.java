package reduction;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract class for reduction classes.
 *
 * @author Joris MASSON
 * @see Reduction
 */
public abstract class AbstractReduction implements Reduction {
    /**
     * Domain of the password characters.
     */
    protected final Set<Character> domain;
    /**
     * Maximum size allowed after a reduction.
     */
    protected int maxSize;

    /**
     * Constructor
     *
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public AbstractReduction(int maxSize, Set<Character> domain) {
        this.maxSize = maxSize;
        this.domain = domain;
    }

    /**
     * Another constructor, just taking a <code>String</code> for the domain.
     *
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public AbstractReduction(int maxSize, String domain) {
        this(maxSize, domain.chars().mapToObj(ch -> (char) ch).collect(Collectors.toSet()));
    }

    /**
     * Cut a <code>String</code> to the maximum allowed size
     *
     * @param s The <code>String</code> to cut.
     * @return Cutted <code>String</code>.
     */
    protected String cutToMaxSize(String s) {
        if (this.maxSize == 0) {
            return "";
        } else if (s.length() > this.maxSize) {
            return s.substring(0, this.maxSize);
        } else {
            return s;
        }
    }

    @Override
    public void setPasswordSize(int newPasswordSize) {
        this.maxSize = newPasswordSize;
    }

    @Override
    public void setColor(int colorNb) {
    }

    @Override
    public void resetColor() {
    }
}
