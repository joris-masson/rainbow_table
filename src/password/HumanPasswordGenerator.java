package password;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * <code>PasswordGenerator</code> that uses a source file with passwords, and returns them when generating.
 *
 * @author Joris MASSON
 */
public class HumanPasswordGenerator extends AbstractPasswordGenerator {
    /**
     * A list of all passwords.
     */
    private final List<String> passwords = new ArrayList<>();

    /**
     * Random object.
     */
    private final Random random;

    /**
     * Domain of passwords
     */
    private final Set<Character> domain;

    /**
     * Base constructor.
     *
     * @param passwordsLength Length of generated passwords.
     * @param charDebut       Start character for passwords.
     * @param charFin         End character for passwords.
     * @param passwordsFile   file containing passwords
     * @param seed            random seed
     */
    public HumanPasswordGenerator(int passwordsLength, int charDebut, int charFin, String passwordsFile, long seed) {
        super(passwordsLength, charDebut, charFin);

        this.domain = new HashSet<>(charFin - charDebut);
        for (int i = charDebut; i < charFin; i++) {
            domain.add((char) i);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(passwordsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {  // pour parcourir toutes les lignes
                if (line.length() == passwordsLength && this.allInDomain(line)) {
                    this.passwords.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        this.random = new Random(seed);
    }

    /**
     * Check if all characters in the password are in the domain.
     *
     * @param password a password
     * @return <code>true</code> if all characters in the password are in the domain.
     */
    private boolean allInDomain(String password) {
        for (char c : password.toCharArray()) {
            if (!this.domain.contains(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String generatePassword() {
        return this.passwords.get(this.random.nextInt(0, this.passwords.size() - 1));
    }
}
