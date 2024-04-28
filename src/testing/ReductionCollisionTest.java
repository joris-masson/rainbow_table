package testing;

import reduction.Reduction;
import util.xml.ReductionCollisionTestDataXMLWriter;

import java.util.*;

/**
 * Class for running collisions tests for reduction functions.
 *
 * @author Joris MASSON
 * @see Reduction
 */
public class ReductionCollisionTest {
    /**
     * Default output directory for results of tests.
     */
    private static final String RESULTS_OUTPUT_DIR = "output_data/testing/reduction/collisions/";


    /**
     * All algorithms that will be tested.
     */
    private final Set<Reduction> algos;

    /**
     * Number of passwords generated for all tests.
     */
    private final int nbPasswords;

    /**
     * Maximum passwords size.
     */
    private final int passwordsSize;

    /**
     * The hash algorithme that will be used.
     */
    private final String hashAlgo;

    /**
     * Test for collisions.
     *
     * @param algos         reduction classes to test
     * @param nbPasswords   number of passwords to test
     * @param passwordsSize size of passwords
     * @param hashAlgo      hash algorithm to use
     */
    public ReductionCollisionTest(Set<Reduction> algos, int nbPasswords, int passwordsSize, String hashAlgo) {
        this.algos = algos;
        this.nbPasswords = nbPasswords;
        this.passwordsSize = passwordsSize;
        this.hashAlgo = hashAlgo;
    }

    /**
     * Main method, run all tests. For all passwords size, a reduction algorithm and a percentage is linked.
     */
    public void test() {
        Map<Integer, Map<String, Double>> res = new HashMap<>();  // for collisions data
        Map<Integer, Map<String, Long>> execTimes = new HashMap<>();  // for execution time data

        for (int tailleMdp = 4; tailleMdp < this.passwordsSize; tailleMdp++) {  // commence à 4, par rapport au nombre max de mdp possibles
            System.out.printf("\n[TEST - %s] - Taille du mot de passe: %d/%d(%d%%).\n", this.hashAlgo, tailleMdp, this.passwordsSize, (100 * tailleMdp) / this.passwordsSize);
            Map<String, Double> currentRes = new HashMap<>();
            Map<String, Long> currentExecTime = new HashMap<>();
            Set<String> hashs = Util.generateAllHash(this.nbPasswords, tailleMdp, this.hashAlgo);  // génération de tous les hashs à réduire

            /* Parcours de tous les algos de réduction */
            for (Reduction algoReduction : this.algos) {
                algoReduction.setPasswordSize(tailleMdp);  // pour que le mot de passe généré soit le même que la taille de celui en entrée
                List<String> reductions = new ArrayList<>();  // liste toutes les réduction faites, pour comparer à la fin

                long startTime = System.currentTimeMillis();
                /* Parcours de tous les hash */
                for (String hash : hashs) {
                    reductions.add(algoReduction.reduce(hash));  // réduction
                }
                long execTime = System.currentTimeMillis() - startTime;  // calcul du temps de... calcul

                int nbCollisions = this.countCollisions(reductions);  // on compte le nb de collisions au total

                /* enregistrement des résultats dans le map pour une taille précise de mdp */
                currentRes.put(algoReduction.getClass().getSimpleName(), (100.0 * nbCollisions) / this.nbPasswords);
                currentExecTime.put(algoReduction.getClass().getSimpleName(), execTime);
                System.out.printf("[TEST - %s] - Temps de calcul %s: %dms, soit %ds\n", this.hashAlgo, algoReduction.getClass().getSimpleName(), execTime, execTime / 1000);
            }

            /* Enregistrement des résultat finaux pour cette taille de mdp */
            res.put(tailleMdp, currentRes);
            execTimes.put(tailleMdp, currentExecTime);
        }

        /* Sauvegarde des résultats au format XML */
        ReductionCollisionTestDataXMLWriter.writeCollisionTestData(res, this.getFileName("collisions"));
        ReductionCollisionTestDataXMLWriter.writeCollisionTestExecTime(execTimes, this.getFileName("execTime"));
    }

    /**
     * Count all collisions that occured.
     *
     * @param reductions A <code>List</code> of reductions.
     * @return Number of collisions in the <code>List</code>.
     */
    private int countCollisions(List<String> reductions) {
        int count = 0;
        HashMap<String, Integer> countMap = new HashMap<>();
        for (String reduction : reductions) {
            if (countMap.containsKey(reduction)) {
                countMap.put(reduction, countMap.get(reduction) + 1);
            } else {
                /* 0 est utilisé à la place de 1 car c'est plus simple lors du comptage final, on additionne tout ce qui n'est pas à 0 */
                countMap.put(reduction, 0);
            }
        }

        for (int nbCollision : countMap.values()) {
            /* Si nbCollision est supérieur à 0, alors il a été vu plus d'une fois */
            if (nbCollision > 0) {
                count += nbCollision;
            }
        }

        return count;
    }

    /**
     * Get the output filename.
     *
     * @param type test data or execution time?
     * @return filename
     */
    public String getFileName(String type) {
        return RESULTS_OUTPUT_DIR + this.hashAlgo + "_" + algos.size() + "algorithms_" + type + ".xml";
    }
}
