import password.HumanPasswordGenerator;
import password.IncrementalPasswordGenerator;
import password.PasswordGenerator;
import password.SimplePasswordGenerator;
import rainbowTable.SimpleRainbowTable;
import reduction.*;
import util.ConfigReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Main class of the project.
 *
 * @author Abdellah BENTEBIBEL
 * @author Joris MASSON
 * @author Mikail SARI
 * @author William MIERE
 */
public abstract class Main {
    /**
     * Argument for the creation of the RT.
     */
    private final static String ARG_CREATION = "--createTable";

    /**
     * Argument for cracking with the RT.
     */
    private final static String ARG_CRACK = "--crack";


    /**
     * Number of line in the RT.
     */
    private static final int nbLine = ConfigReader.getNbLine();

    /**
     * Length of all passwords.
     */
    private static final int passwordsLength = ConfigReader.getPasswordsLength();

    /**
     * Hash algorithm.
     */
    private static final String hashAlgo = ConfigReader.getHashAlgorithm();

    /**
     * Number of color in the RT.
     */
    private static final int nbColor = ConfigReader.getNbColor();

    /**
     * Start character in the domain of passwords.
     */
    private static final char startChar = ConfigReader.getCharStart();

    /**
     * End character in the domain of passwords.
     */
    private static final char endChar = ConfigReader.getCharEnd();

    /**
     * Reduction name to use.
     */
    private static final String reductionClass = ConfigReader.getReductionClass();

    /**
     * Password generator name to use.
     */
    private static final String passwordGenerator = ConfigReader.getPasswordGenerator();

    /**
     * Optimization, deleting duplicate hashes in the RT.
     */
    private static final boolean deleteDuplicateHashes = ConfigReader.getDeleteDuplicateHashes();

    /**
     * Optimization, deleting duplicate passwords in the RT.
     */
    private static final boolean deleteDuplicatePasswords = ConfigReader.getDeleteDuplicatePasswords();

    /**
     * Optimization, put all the RT in RAM.
     */
    private static final boolean cacheTable = ConfigReader.getCacheTable();

    /**
     * Save result if is unknown?
     */
    private static final boolean saveUnknown = ConfigReader.getSaveNotFound();

    /**
     * Main method.
     *
     * @param args args.
     */
    public static void main(String[] args) {
        System.out.print("Initialisation de la table arc-en-ciel... ");
        SimpleRainbowTable rainbowTable = new SimpleRainbowTable(nbLine, passwordsLength, hashAlgo, getReduction(), nbColor, getPasswordGenerator(), startChar, endChar, deleteDuplicatePasswords, deleteDuplicateHashes, cacheTable, ConfigReader.getNoCollisions());
        System.out.println("Initialisation terminée !");

        // on regarde si l'argument demandant la création de la table est présent, si oui, la table est créée
        if (containArg(args, ARG_CREATION)) {
            System.out.print("Création de la table arc-en-ciel... ");
            rainbowTable.createTable();
            System.out.println("Table créée !");
        }

        if (containArg(args, ARG_CRACK)) {  // idem pour la cassage d'un hash
            int crackArgIndex = indexOfArg(args, ARG_CRACK);  // prends l'index de l'argument dans l'array des arguments

            if (crackArgIndex <= args.length && !args[crackArgIndex + 1].equals(ARG_CREATION)) {  // vérifie si l'argument suivant est présent et n'est pas celui demandant la création de la table
                String crackArgValue = args[crackArgIndex + 1];

                if (!crackArgValue.contains("/") && !crackArgValue.contains(".")) {  // on regarde si l'argument donné n'est pas potentiellement un chemin
                    String res = rainbowTable.crackHash(crackArgValue);  // casse le hash et stocke le résultat

                    if (res == null) {  // si null, alors pas de résultat trouvé
                        System.out.printf("Le hash \"%s\" n'a pas pu être cassé.\n", args[crackArgIndex + 1]);
                    } else {
                        System.out.printf("Le hash a été cassé: %s -> %s.\n", args[crackArgIndex + 1], res);
                    }
                } else {  // cas où l'argument contient un '/' ou un '.' -> c'est un fichier
                    Set<String> allHashes = getAllHashesFromFile(crackArgValue);  // récupération des hashes
                    int nbOfHashesCracked = crackAllHashes(rainbowTable, allHashes, crackArgValue);  // cassage des hashes

                    System.out.println("----- RÉSULTATS -----");
                    System.out.printf("\n%d hashes ont été cassés sur %d, ce qui représente %d%% !\n\n", nbOfHashesCracked, allHashes.size(), (nbOfHashesCracked * 100) / allHashes.size());
                }
            } else {
                System.out.printf("Utilisation: %s [hash à casser | chemin/d/un/fichier/contenant/des/hashes]\n", ARG_CRACK);
            }
        }
    }

    /**
     * Utility method for initializing the <code>Reduction</code> class.
     *
     * @return A <code>Reduction</code>.
     */
    private static Reduction getReduction() {
        // initialisation du domaine
        Set<Character> domain = new HashSet<>();
        for (int i = startChar; i < endChar; i++) {
            domain.add((char) i);
        }

        return switch (reductionClass) {
            case "AdvancedHashReducer" -> new AdvancedHashReducer(nbColor, passwordsLength);
            case "ColorReduction" -> new ColorReduction(nbColor, passwordsLength, domain);
            case "CustomReduction" -> new CustomReduction(passwordsLength);
            case "HashcodeReduction" -> new HashcodeReduction(passwordsLength);
            case "MarkovReduction" ->
                    new MarkovReduction(nbColor, passwordsLength, domain, Paths.get(ConfigReader.getTrainingFile()));
            case "SimpleReduction" -> new SimpleReduction(passwordsLength, domain);
            default -> {
                System.err.print("Problème de chargement de la classe de réduction, voici les valeurs autorisées:\n\t- AdvancedHashReducer\n\t- ColorReduction\n\t- CustomReduction\n\t- HashcodeReduction\n\t- MarkovReduction\n\t- SimpleReduction\nChargement de ColorReduction\n");
                yield new ColorReduction(nbColor, passwordsLength, domain);
            }
        };
    }

    /**
     * Utility method for initializing the <code>PasswordGenerator</code> class.
     *
     * @return A <code>PasswordGenerator</code>.
     */
    private static PasswordGenerator getPasswordGenerator() {
        return switch (passwordGenerator) {
            case "HumanPasswordGenerator" ->
                    new HumanPasswordGenerator(passwordsLength, startChar, endChar, ConfigReader.getTrainingFile(), ConfigReader.getRandomSeed());
            case "IncrementalPasswordGenerator" ->
                    new IncrementalPasswordGenerator(passwordsLength, startChar, endChar, nbColor);
            case "SimplePasswordGenerator" ->
                    new SimplePasswordGenerator(passwordsLength, startChar, endChar, ConfigReader.getRandomSeed());
            default -> {
                System.err.print("Problème de chargement du générateur de mot de passe, voici les valeurs autorisées:\n\t- HumanPasswordGenerator\n\t- IncrementalPasswordGenerator\n\t- SimplePasswordGenerator\nChargement de IncrementalPasswordGenerator\n");
                yield new IncrementalPasswordGenerator(passwordsLength, startChar, endChar, nbColor);
            }
        };
    }

    /**
     * Utility method that checks index of a specified argument.
     *
     * @param args  agrs.
     * @param zeArg The argument to search for.
     * @return Index of the specified argument. Or <code>-1</code> if it does not exists.
     */
    private static int indexOfArg(String[] args, String zeArg) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(zeArg)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Utility method that checks if args contains a specific argument.
     *
     * @param args  args.
     * @param zeArg The argument to search.
     * @return <code>true</code> if the argument is in args array.
     */
    private static boolean containArg(String[] args, String zeArg) {
        return indexOfArg(args, zeArg) != -1;
    }

    /**
     * Retrieves all hashes within a specified file.
     *
     * @param path Path of the file.
     * @return A <code>Set</code> of all hashes.
     */
    private static Set<String> getAllHashesFromFile(String path) {
        Set<String> res = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {  // pour parcourir toutes les lignes
                res.add(line);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return res;
    }

    /**
     * Initialize the writer for results.
     *
     * @param path Path of the output file.
     * @return The initialized <code>FileWriter</code>.
     */
    private static FileWriter initResWriter(String path) {
        try {
            return new FileWriter(path);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Attempt to crack all hashes in the given <code>Set</code>, and save results in a file.
     *
     * @param rainbowTable The rainbow table to use.
     * @param allHashes    The <code>Set</code> of all hashes to crack.
     * @param hashesFile   The file containing the hashes(for naming the output file).
     * @return Number of hashes that the rainbow table cracked.
     */
    private static int crackAllHashes(SimpleRainbowTable rainbowTable, Set<String> allHashes, String hashesFile) {
        FileWriter resWriter = initResWriter(String.format("%s_res.csv", hashesFile));
        int crackedCounter = 0;

        if (resWriter == null) {
            System.err.println("Le fichier de stockage des résultats n'a pas pu être ouvert.");
            System.exit(1);
        }

        try {
            for (String hash : allHashes) {
                System.out.printf("%s -> ", hash);
                String crackRes = rainbowTable.crackHash(hash);  // tentative de cassage

                // analyse du résultat
                if (crackRes == null) {  // si null, alors pas de résultat trouvé
                    System.out.print("Non trouvé \n");
                    if (saveUnknown) {
                        resWriter.append(String.format("%s, \n", hash));
                    }
                } else {  // si non null, alors c'est trouvé !
                    crackedCounter++;
                    System.out.printf("%s\n", crackRes);
                    resWriter.append(String.format("%s, %s\n", hash, crackRes));
                }
            }
            resWriter.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return crackedCounter;
    }
}
