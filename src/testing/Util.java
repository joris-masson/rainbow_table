package testing;

import hashage.SimpleHashFunction;

import java.util.*;

/**
 * Utility class for tests.
 *
 * @author Joris MASSON
 */
public abstract class Util {
    /**
     * <a href="https://www.baeldung.com/java-random-string">Source</a>
     *
     * @param targetStringLength Length of the generated <code>String</code>.
     * @return A random alpha <code>String</code>.
     */
    public static String generateRandomString(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Generate a <code>List</code> of random passwords.
     *
     * @param nbPasswords   number of passwords to generate.
     * @param passwordsSize the size of the passwords to generate.
     * @param checkDuplicates duplicates are stored if <code>false</code>
     * @return A <code>List</code> of random passwords.
     */
    public static List<String> generateAllPasswords(int nbPasswords, int passwordsSize, boolean checkDuplicates) {
        List<String> res = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < nbPasswords; i++) {
            String generatedPassword = generateRandomString(passwordsSize);
            if (checkDuplicates && res.contains(generatedPassword)) {
                continue;
            }

            res.add(generatedPassword);
        }
        long execTime = System.currentTimeMillis() - startTime;

        return res;
    }

    /**
     * Generate a <code>Set</code> of hashs.
     *
     * @param nbHash        Number of hash to generate.
     * @param passwordsSize the size of passwords used to generate those hash.
     * @param hashAlgo      the hash algorithm to use.
     * @return A <code>Set</code> of hashs.
     */
    public static Set<String> generateAllHash(int nbHash, int passwordsSize, String hashAlgo) {
        Set<String> res = new HashSet<>();
        for (String password : generateAllPasswords(nbHash, passwordsSize, true)) {
            res.add(SimpleHashFunction.hashString(password, hashAlgo));
        }
        return res;
    }
}
