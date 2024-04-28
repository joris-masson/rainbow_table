package password;

/**
 * Interface for password generators.
 *
 * @author William MIERE
 */
public interface PasswordGenerator {
    /**
     * Generate a valid password.
     *
     * @return A valid password.
     */
    String generatePassword();
}
