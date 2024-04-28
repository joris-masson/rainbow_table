package reduction;

/**
 * Interface for reduction related code.
 *
 * @author Joris MASSON
 */
public interface Reduction {
    /**
     * Take a <code>String</code> that represents a hash, and reduce it.
     * Reduction MUST return a valid password and avoid collisions.
     *
     * @param hash The hash to reduce.
     * @return Reduced hash.
     */
    String reduce(String hash);

    /**
     * Setter for passwordSize.
     *
     * @param newPasswordSize new passwordSize.
     */
    void setPasswordSize(int newPasswordSize);

    /**
     * Setter for colorNb
     *
     * @param colorNb new colorNb.
     */
    void setColor(int colorNb);

    /**
     * Reset the color.
     */
    void resetColor();
}
