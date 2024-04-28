package rainbowTable;

import hashage.SimpleHashFunction;
import password.IncrementalPasswordGenerator;
import password.PasswordGenerator;
import password.SimplePasswordGenerator;
import reduction.*;

import java.util.ArrayList;
import java.util.List;

public class Demo {
    private static final String HASH_ALGO = "MD5";
    private static final int NB_LINES = 1000000;
    private static final int NB_COLOR = 6;
    private static final int PASSWORD_SIZE = 5;
    private static final char START_CHAR = 'a';
    private static final char END_CHAR = 'z';
    private static final String DOMAIN = "azertyuiopqsdfghjklmwxcvbn";

    public static void main(String[] args) {
        PasswordGenerator simplePasswordGenerator = new SimplePasswordGenerator(PASSWORD_SIZE, START_CHAR, END_CHAR);
        PasswordGenerator incrementalPasswordGenerator = new IncrementalPasswordGenerator(PASSWORD_SIZE, START_CHAR, END_CHAR, NB_COLOR);
        // PasswordGenerator humanPasswordGenerator = new HumanPasswordGenerator(PASSWORD_SIZE, START_CHAR, END_CHAR, "texts/youporn.txt", 666);

        Reduction hashcodeReduction = new HashcodeReduction(PASSWORD_SIZE);
        Reduction simpleReduction = new SimpleReduction(PASSWORD_SIZE, DOMAIN);
        Reduction advancedHashReducer = new HashcodeReduction(PASSWORD_SIZE);
        Reduction customReduction = new CustomReduction(PASSWORD_SIZE);
        // Reduction markovReduction = new MarkovReduction(NB_COLOR, PASSWORD_SIZE, DOMAIN, Paths.get("texts/train.txt"));
        Reduction colorReduction = new ColorReduction(NB_COLOR, PASSWORD_SIZE, DOMAIN);

        SimpleRainbowTable rainbowTable = new SimpleRainbowTable(NB_LINES, PASSWORD_SIZE, HASH_ALGO, customReduction, NB_COLOR, simplePasswordGenerator, START_CHAR, END_CHAR, false, false, true, true);

        // rainbowTable.createTable();
        // System.out.println(rainbowTable.crackHash("eca387a74d2992c03b37e36dd2bb207685b3b41a"));

        // testCrackSinglePassword(rainbowTable, "amalia");

        fullTest(rainbowTable, 10000, true, simplePasswordGenerator);
    }

    private static String testCrackSinglePassword(SimpleRainbowTable rainbowTable, String password) {
        String hash = SimpleHashFunction.hashString(password, HASH_ALGO);

        System.out.printf("%s -> ", hash);
        long startTime = System.currentTimeMillis();
        String res = rainbowTable.crackHash(hash);
        long execTime = System.currentTimeMillis() - startTime;
        if (res != null) {
            System.out.printf("%s (Temps pour casser le hash: %dms, soit %ds)\n", res, execTime, execTime / 1000);
        } else {
            System.out.printf("Non trouvé (Temps: %dms, soit %ds)", execTime, execTime / 1000);
        }
        return res;
    }

    private static long testTableCreationTime(SimpleRainbowTable rainbowTable) {
        long startTime = System.currentTimeMillis();
        rainbowTable.createTable();
        long execTime = System.currentTimeMillis() - startTime;
        System.out.printf("Temps pour créer la table: %dms, soit %ds\n", execTime, execTime / 1000);
        return execTime;
    }

    private static List<List<Long>> testHashCrackingTime(SimpleRainbowTable rainbowTable, int nbHash, PasswordGenerator passwordGenerator) {
        List<List<Long>> res = new ArrayList<>();
        List<Long> foundRes = new ArrayList<>();
        List<Long> notFoundRes = new ArrayList<>();

        for (int i = 0; i < nbHash; i++) {
            String password = passwordGenerator.generatePassword();
            String hash = SimpleHashFunction.hashString(password, HASH_ALGO);
            System.out.printf("[%d/%d] - %s -> ", i, nbHash, hash);
            long startTime = System.currentTimeMillis();

            String crackResult = rainbowTable.crackHash(hash);
            long execTime = System.currentTimeMillis() - startTime;

            if (crackResult != null) {
                foundRes.add(execTime);
                System.out.printf("trouvé!(%s), en %dms, soit %ds\n", crackResult, execTime, execTime / 1000);
            } else {
                notFoundRes.add(execTime);
                System.out.printf("non trouvé(%s), en %dms, soit %ds\n", password, execTime, execTime / 1000);
            }
        }

        res.add(foundRes);
        res.add(notFoundRes);
        return res;
    }

    private static void fullTest(SimpleRainbowTable rainbowTable, int nbHash, boolean createTable, PasswordGenerator passwordGenerator) {
        long tableCreationTime = -1;
        if (createTable) {
            tableCreationTime = testTableCreationTime(rainbowTable);
        }
        List<List<Long>> hashCrackingTime = testHashCrackingTime(rainbowTable, nbHash, passwordGenerator);

        long totalCrackingTime = 0;
        for (long crackingTime : hashCrackingTime.get(0)) {
            totalCrackingTime += crackingTime;
        }
        for (long crackingTime : hashCrackingTime.get(1)) {
            totalCrackingTime += crackingTime;
        }

        double moyenneFound = 0;
        double ecartTypeFound = 0;
        double moyenneNotFound = 0;
        double ecartTypeNotFound = 0;

        if (!hashCrackingTime.get(0).isEmpty()) {
            moyenneFound = moyenne(hashCrackingTime.get(0));
            ecartTypeFound = ecartType(hashCrackingTime.get(0));
        }

        if (!hashCrackingTime.get(1).isEmpty()) {
            moyenneNotFound = moyenne(hashCrackingTime.get(1));
            ecartTypeNotFound = ecartType(hashCrackingTime.get(1));
        }

        String statsString = String.format("Moyenne de temps pour retrouver un seul hash:\n\t- %f (trouvé)\n\t- %f (non trouvé)\nÉcart-type:\n\t- %f (trouvé)\n\t- %f (non trouvé)\n",
                moyenneFound,
                moyenneNotFound,
                ecartTypeFound / 2,
                ecartTypeNotFound / 2);

        System.out.printf("\n\n----- Résumé de la session de test -----\nRainbowTable: %s\nTemps de création = %dms(%ds)\nNombre de hash à retrouver = %d\nTemps total de recherche = %dms(%ds)\n%sNombre de hash retrouvés = %d",
                rainbowTable,
                tableCreationTime,
                tableCreationTime / 1000,
                nbHash,
                totalCrackingTime,
                totalCrackingTime / 1000,
                statsString,
                hashCrackingTime.get(0).size());
    }

    private static double moyenne(List<Long> timeList) {
        long total = 0;
        for (long crackTime : timeList) {
            total += crackTime;
        }
        return (double) total / timeList.size();
    }

    private static double ecartType(List<Long> timeList) {
        double moyenne = moyenne(timeList);

        double total = 0;
        for (long crackTime : timeList) {
            total += Math.pow(crackTime - moyenne, 2);
        }
        return Math.pow(total / timeList.size(), 0.5);
    }
}
