package testing;

import reduction.*;

import java.nio.file.Paths;
import java.security.Security;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for testing.
 *
 * @author Joris MASSON
 */
public abstract class Testing {
    /**
     * A domain which contains all aphanumerics.
     */
    private static final Set<Character> DOMAIN = "abcdefghijklmnopqrstuvwxyz".chars().mapToObj(ch -> (char) ch).collect(Collectors.toSet());

    /**
     * Main testing method.
     *
     * @param args args
     */
    public static void main(String[] args) {
        Reduction hashcodeReduction = new HashcodeReduction(1);
        Reduction simpleReduction = new SimpleReduction(1, DOMAIN);
        Reduction customReduction = new CustomReduction(1);
        Reduction advancedHashReducer = new AdvancedHashReducer(0, 1);
        Reduction colorReduction = new ColorReduction(0, 1, DOMAIN);
        Reduction markovReduction = new MarkovReduction(0, 1, DOMAIN, Paths.get("texts/train.txt"));

        Set<Reduction> reductionsSet = new HashSet<>();
        reductionsSet.add(hashcodeReduction);
        reductionsSet.add(simpleReduction);
        reductionsSet.add(customReduction);
        reductionsSet.add(advancedHashReducer);
        reductionsSet.add(colorReduction);
        reductionsSet.add(markovReduction);

        Set<String> hashAlgoSet = new HashSet<>(Arrays.asList("MD2", "MD5", "SHA-1", "SHA-256", "SHA-512"));
        Set<String> allAlgos = Security.getAlgorithms("MessageDigest");

        for (String hashAlgo: hashAlgoSet) {
            System.out.printf("\n[TEST] - Démarrage du test pour le hash %s\n", hashAlgo);
            ReductionCollisionTest test = new ReductionCollisionTest(reductionsSet, 100000, 8, hashAlgo);
            test.test();
        }
    }
}
