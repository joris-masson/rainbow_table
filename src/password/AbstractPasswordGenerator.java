package password;

/**
 * Abstract class for password generators.
 *
 * @author William MIERE
 * @see PasswordGenerator
 */
public abstract class AbstractPasswordGenerator implements PasswordGenerator {
    /**
     * Length of generated passwords.
     */
    protected final int passwordsLength;

    /**
     * Start character for passwords.
     */
    protected final int charDebut;

    /**
     * End character for passwords.
     */
    protected final int charFin;

    /**
     * Base constructor.
     *
     * @param passwordsLength Length of generated passwords.
     * @param charDebut       Start character for passwords.
     * @param charFin         End character for passwords.
     */
    public AbstractPasswordGenerator(int passwordsLength, int charDebut, int charFin) {
        this.passwordsLength = passwordsLength;
        this.charDebut = charDebut;
        this.charFin = charFin;
    }
}
