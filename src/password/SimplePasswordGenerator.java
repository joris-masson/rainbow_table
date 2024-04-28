package password;

import java.util.Random;

/**
 * Simple generator that generate random <code>String</code>.
 *
 * @author Joris MASSON
 * @see PasswordGenerator
 * @see AbstractPasswordGenerator
 */
public class SimplePasswordGenerator extends AbstractPasswordGenerator {
    /**
     * Random object for generating passwords.
     */
    private final Random random;

    /**
     * Main constructor.
     *
     * @param passwordsLength Length of all passwords.
     * @param charDebut       Start character for passwords.
     * @param charFin         End character for passwords.
     * @param seed            Seed for the random generator.
     */
    public SimplePasswordGenerator(int passwordsLength, int charDebut, int charFin, long seed) {
        super(passwordsLength, charDebut, charFin);
        this.random = new Random(seed);
    }

    /**
     * Constructor with default seed.
     *
     * @param passwordsLength Length of all passwords.
     * @param charDebut       Start character for passwords.
     * @param charFin         End character for passwords.
     */
    public SimplePasswordGenerator(int passwordsLength, int charDebut, int charFin) {
        this(passwordsLength, charDebut, charFin, 0);
    }

    @Override
    public String generatePassword() {
        return this.random.ints(this.charDebut, this.charFin + 1)
                .limit(this.passwordsLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
