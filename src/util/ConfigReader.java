package util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Abstract class for managing the application configuration.
 *
 * @author Joris MASSON
 */
public abstract class ConfigReader {
    /**
     * Name of the config file
     */
    private static final String CONFIG_FILE_NAME = "config.properties";

    /* Noms des configs */
    /**
     * Number of line in the RT.
     */
    private static final String CONFIG_NAME_NB_LINE = "rt.line.nb";

    /**
     * Length of all passwords.
     */
    private static final String CONFIG_NAME_PASSWORDS_LENGTH = "rt.password.length";

    /**
     * Hash algorithm.
     */
    private static final String CONFIG_NAME_HASH_ALGORITHM = "rt.hash.algo";

    /**
     * Reduction name to use.
     */
    private static final String CONFIG_NAME_REDUCTION_CLASS = "rt.reduction.class";

    /**
     * Number of color in the RT.
     */
    private static final String CONFIG_NAME_NB_COLOR = "rt.color.count";

    /**
     * Password generator name to use.
     */
    private static final String CONFIG_NAME_PASSWORD_GENERATOR = "rt.password.generator";

    /**
     * Start character in the domain of passwords.
     */
    private static final String CONFIG_NAME_CHAR_START = "rt.char.start";

    /**
     * End character in the domain of passwords.
     */
    private static final String CONFIG_NAME_CHAR_END = "rt.char.end";

    /**
     * Optimization, deleting duplicate hashes in the RT.
     */
    private static final String CONFIG_NAME_DELETE_DUPLICATE_HASHES = "rt.optimization.delete.hashes";

    /**
     * Optimization, deleting duplicate passwords in the RT.
     */
    private static final String CONFIG_NAME_DELETE_DUPLICATE_PASSWORDS = "rt.optimization.delete.passwords";

    /**
     * Optimization, put all the RT in RAM.
     */
    private static final String CONFIG_NAME_CACHE_TABLE = "rt.optimization.cache";

    /**
     * Training file.
     */
    private static final String CONFIG_NAME_TRAINING_FILE = "rt.training.file";

    /**
     * Random seed.
     */
    private static final String CONFIG_NAME_RANDOM_SEED = "rt.random.seed";

    /**
     * Save result if is unknown?
     */
    private static final String CONFIG_NAME_SAVE_NOT_FOUND = "rt.save_unkown";

    /**
     * Do the RT tries to regenerate a line with a collision?
     */
    private static final String CONFIG_NAME_NO_COLLISIONS = "rt.optimization.no_collisions";


    /* Valeurs par défaut des configs */
    /**
     * Number of line in the RT.
     */
    private static final int DEFAULT_CONFIG_VALUE_NB_LINE = 100000;

    /**
     * Length of all passwords.
     */
    private static final int DEFAULT_CONFIG_VALUE_PASSWORDS_LENGTH = 5;

    /**
     * Hash algorithm.
     */
    private static final String DEFAULT_CONFIG_VALUE_HASH_ALGORITHM = "SHA-1";

    /**
     * Reduction name to use.
     */
    private static final String DEFAULT_CONFIG_VALUE_REDUCTION_CLASS = "ColorReduction";

    /**
     * Number of color in the RT.
     */
    private static final int DEFAULT_CONFIG_VALUE_NB_COLOR = 5;

    /**
     * Password generator name to use.
     */
    private static final String DEFAULT_CONFIG_VALUE_PASSWORD_GENERATOR = "IncrementalPasswordGenerator";

    /**
     * Start character in the domain of passwords.
     */
    private static final char DEFAULT_CONFIG_VALUE_CHAR_START = 'a';

    /**
     * End character in the domain of passwords.
     */
    private static final char DEFAULT_CONFIG_VALUE_CHAR_END = 'z';

    /**
     * Optimization, deleting duplicate hashes in the RT.
     */
    private static final boolean DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_HASHES = false;

    /**
     * Optimization, deleting duplicate passwords in the RT.
     */
    private static final boolean DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_PASSWORDS = false;

    /**
     * Optimization, put all the RT in RAM.
     */
    private static final boolean DEFAULT_CONFIG_VALUE_CACHE_TABLE = true;

    /**
     * Training file.
     */
    private static final String DEFAULT_CONFIG_VALUE_TRAINING_FILE = "texts/train.txt";

    /**
     * Random seed.
     */
    private static final long DEFAULT_CONFIG_VALUE_RANDOM_SEED = 177013;

    /**
     * Save result if is unknown?
     */
    private static final boolean DEFAULT_CONFIG_VALUE_SAVE_NOT_FOUND = false;

    /**
     * Do the RT tries to regenerate a line with a collision?
     */
    private static final boolean DEFAULT_CONFIG_VALUE_NO_COLLISIONS = false;

    /**
     * Get nbLine config value.
     *
     * @return nbLine config value.
     */
    public static int getNbLine() {
        try {
            return Integer.parseInt(getConf(CONFIG_NAME_NB_LINE));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %d\n", CONFIG_NAME_NB_LINE, DEFAULT_CONFIG_VALUE_NB_LINE);
            return DEFAULT_CONFIG_VALUE_NB_LINE;
        }
    }

    /**
     * Get passwordsLength config value.
     *
     * @return passwordsLength config value.
     */
    public static int getPasswordsLength() {
        try {
            return Integer.parseInt(getConf(CONFIG_NAME_PASSWORDS_LENGTH));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %d\n", CONFIG_NAME_PASSWORDS_LENGTH, DEFAULT_CONFIG_VALUE_PASSWORDS_LENGTH);
            return DEFAULT_CONFIG_VALUE_PASSWORDS_LENGTH;
        }
    }

    /**
     * Get hashAlgorithm config.
     *
     * @return hashAlgorithm config.
     */
    public static String getHashAlgorithm() {
        try {
            return getConf(CONFIG_NAME_HASH_ALGORITHM);
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %s\n", CONFIG_NAME_HASH_ALGORITHM, DEFAULT_CONFIG_VALUE_HASH_ALGORITHM);
            return DEFAULT_CONFIG_VALUE_HASH_ALGORITHM;
        }
    }

    /**
     * Get reductionClass value.
     *
     * @return reductionClass value.
     */
    public static String getReductionClass() {
        try {
            return getConf(CONFIG_NAME_REDUCTION_CLASS);
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %s\n", CONFIG_NAME_REDUCTION_CLASS, DEFAULT_CONFIG_VALUE_REDUCTION_CLASS);
            return DEFAULT_CONFIG_VALUE_REDUCTION_CLASS;
        }
    }

    /**
     * Get nbColor config value.
     *
     * @return nbColor config value.
     */
    public static int getNbColor() {
        try {
            return Integer.parseInt(getConf(CONFIG_NAME_NB_COLOR));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %d\n", CONFIG_NAME_NB_COLOR, DEFAULT_CONFIG_VALUE_NB_COLOR);
            return DEFAULT_CONFIG_VALUE_NB_COLOR;
        }
    }

    /**
     * Get passwordGenerator config value.
     *
     * @return passwordGenerator config value.
     */
    public static String getPasswordGenerator() {
        try {
            return getConf(CONFIG_NAME_PASSWORD_GENERATOR);
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %s\n", CONFIG_NAME_PASSWORD_GENERATOR, DEFAULT_CONFIG_VALUE_PASSWORD_GENERATOR);
            return DEFAULT_CONFIG_VALUE_PASSWORD_GENERATOR;
        }
    }

    /**
     * get charStart config value
     *
     * @return charStart config value
     */
    public static char getCharStart() {
        try {
            return getConf(CONFIG_NAME_CHAR_START).toCharArray()[0];
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %s\n", CONFIG_NAME_CHAR_START, DEFAULT_CONFIG_VALUE_CHAR_START);
            return DEFAULT_CONFIG_VALUE_CHAR_START;
        }
    }

    /**
     * get charEnd value
     *
     * @return charEnd value
     */
    public static char getCharEnd() {
        try {
            return getConf(CONFIG_NAME_CHAR_END).toCharArray()[0];
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %s\n", CONFIG_NAME_CHAR_END, DEFAULT_CONFIG_VALUE_CHAR_END);
            return DEFAULT_CONFIG_VALUE_CHAR_END;
        }
    }

    /**
     * get deleteDuplicateHashes value.
     *
     * @return deleteDuplicateHashes value.
     */
    public static boolean getDeleteDuplicateHashes() {
        try {
            return Boolean.parseBoolean(getConf(CONFIG_NAME_DELETE_DUPLICATE_HASHES));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %b\n", CONFIG_NAME_DELETE_DUPLICATE_HASHES, DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_HASHES);
            return DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_HASHES;
        }
    }

    /**
     * Get deleteDuplicatePasswords value.
     *
     * @return deleteDuplicatePasswords value.
     */
    public static boolean getDeleteDuplicatePasswords() {
        try {
            return Boolean.parseBoolean(getConf(CONFIG_NAME_DELETE_DUPLICATE_PASSWORDS));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %b\n", CONFIG_NAME_DELETE_DUPLICATE_PASSWORDS, DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_PASSWORDS);
            return DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_PASSWORDS;
        }
    }

    /**
     * get cacheTable value.
     *
     * @return cacheTable.
     */
    public static boolean getCacheTable() {
        try {
            return Boolean.parseBoolean(getConf(CONFIG_NAME_CACHE_TABLE));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %b\n", CONFIG_NAME_CACHE_TABLE, DEFAULT_CONFIG_VALUE_CACHE_TABLE);
            return DEFAULT_CONFIG_VALUE_CACHE_TABLE;
        }
    }

    /**
     * Get trainingFile value.
     *
     * @return trainingFile value.
     */
    public static String getTrainingFile() {
        try {
            return getConf(CONFIG_NAME_TRAINING_FILE);
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %b\n", CONFIG_NAME_TRAINING_FILE, DEFAULT_CONFIG_VALUE_TRAINING_FILE);
            return DEFAULT_CONFIG_VALUE_TRAINING_FILE;
        }
    }

    /**
     * Get randomSeed value.
     *
     * @return randomSeed value.
     */
    public static Long getRandomSeed() {
        try {
            return Long.parseLong(getConf(CONFIG_NAME_RANDOM_SEED));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %b\n", CONFIG_NAME_RANDOM_SEED, DEFAULT_CONFIG_VALUE_RANDOM_SEED);
            return DEFAULT_CONFIG_VALUE_RANDOM_SEED;
        }
    }

    /**
     * Get saveNotFound config value.
     *
     * @return saveNotFoundValue.
     */
    public static boolean getSaveNotFound() {
        try {
            return Boolean.parseBoolean(getConf(CONFIG_NAME_SAVE_NOT_FOUND));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %b\n", CONFIG_NAME_SAVE_NOT_FOUND, DEFAULT_CONFIG_VALUE_SAVE_NOT_FOUND);
            return DEFAULT_CONFIG_VALUE_SAVE_NOT_FOUND;
        }
    }

    /**
     * Get noCollisions config value in the config file.
     *
     * @return noCollisions config value in the config file.
     */
    public static boolean getNoCollisions() {
        try {
            return Boolean.parseBoolean(getConf(CONFIG_NAME_NO_COLLISIONS));
        } catch (IOException e) {
            System.err.printf("Erreur lors de la récupération de %s, chargement de la valeur par défaut: %b\n", CONFIG_NAME_NO_COLLISIONS, DEFAULT_CONFIG_VALUE_NO_COLLISIONS);
            return DEFAULT_CONFIG_VALUE_NO_COLLISIONS;
        }
    }

    /**
     * Get a given config value as a <code>String</code>
     *
     * @param propName the name of the config to get
     * @return the config value as a <code>String</code>
     * @throws IOException yep.
     * @see String
     */
    private static String getConf(String propName) throws IOException {
        Properties prop = new Properties();

        /* essai chargement du fichier de configuration donné */
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_NAME)) {
            prop.load(fis);
            return prop.getProperty(propName);
        } catch (IOException e) {
            /* Le fichier n'a pas été trouvé, un par défaut est créé */
            System.err.println("Fichier de configuration non trouvé");
            writeDefaultConfig();
            prop.load(new FileInputStream(CONFIG_FILE_NAME));
            return prop.getProperty(propName);
        }
    }

    /**
     * Write the config file with default values.
     *
     * @throws IOException yep.
     */
    private static void writeDefaultConfig() throws IOException {
        System.err.println("Création du fichier de configuration par défaut");
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(CONFIG_FILE_NAME));

        writer.write("# ----- CONFIGURATION BASIQUE -----\n");
        writer.write(String.format("# Nombre de lignes de la table arc-en-ciel\n%s=%d\n", CONFIG_NAME_NB_LINE, DEFAULT_CONFIG_VALUE_NB_LINE));
        writer.write(String.format("# Longueur des mots de passe\n%s=%d\n", CONFIG_NAME_PASSWORDS_LENGTH, DEFAULT_CONFIG_VALUE_PASSWORDS_LENGTH));
        writer.write(String.format("# Nombre de couleurs\n%s=%d\n", CONFIG_NAME_NB_COLOR, DEFAULT_CONFIG_VALUE_NB_COLOR));
        writer.write(String.format("# Algorithme de hash\n%s=%s\n", CONFIG_NAME_HASH_ALGORITHM, DEFAULT_CONFIG_VALUE_HASH_ALGORITHM));
        writer.write(String.format("# Classe de reduction\n%s=%s\n", CONFIG_NAME_REDUCTION_CLASS, DEFAULT_CONFIG_VALUE_REDUCTION_CLASS));
        writer.write(String.format("# Generateur de mot de passe\n%s=%s\n", CONFIG_NAME_PASSWORD_GENERATOR, DEFAULT_CONFIG_VALUE_PASSWORD_GENERATOR));
        writer.write(String.format("# Est-ce que le fichier de sortie stocke les lignes qui n'ont aucun resultat?\n%s=%b\n", CONFIG_NAME_SAVE_NOT_FOUND, DEFAULT_CONFIG_VALUE_SAVE_NOT_FOUND));

        writer.write("\n# ----- CONFIGURATION AVANCEE -----\n");
        writer.write(String.format("# Caractere de debut\n%s=%s\n", CONFIG_NAME_CHAR_START, DEFAULT_CONFIG_VALUE_CHAR_START));
        writer.write(String.format("# Caractere de fin\n%s=%s\n", CONFIG_NAME_CHAR_END, DEFAULT_CONFIG_VALUE_CHAR_END));
        writer.write(String.format("# Fichier d'entrainement(liste de mots de passe) pour MarkovReduction et/ou HumanPasswordGenerator\n%s=%s\n", CONFIG_NAME_TRAINING_FILE, DEFAULT_CONFIG_VALUE_TRAINING_FILE));
        writer.write(String.format("# Graine de generation aleatoire\n%s=%d\n", CONFIG_NAME_RANDOM_SEED, DEFAULT_CONFIG_VALUE_RANDOM_SEED));

        writer.write("\n# ----- OPTIMISATIONS -----\n");
        writer.write(String.format("# Suppression des hashes dupliques?\n%s=%b\n", CONFIG_NAME_DELETE_DUPLICATE_HASHES, DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_HASHES));
        writer.write(String.format("# Suppression des mots de passe dupliques?\n%s=%b\n", CONFIG_NAME_DELETE_DUPLICATE_PASSWORDS, DEFAULT_CONFIG_VALUE_DELETE_DUPLICATE_PASSWORDS));
        writer.write(String.format("# Est-ce que la table arc-en-ciel est mise en cache dans la RAM?\n%s=%b\n", CONFIG_NAME_CACHE_TABLE, DEFAULT_CONFIG_VALUE_CACHE_TABLE));
        writer.write(String.format("# Est-ce que la table tente de recreer une chaine contenant une collision?\n%s=%b\n", CONFIG_NAME_NO_COLLISIONS, DEFAULT_CONFIG_VALUE_NO_COLLISIONS));

        writer.close();
    }
}
