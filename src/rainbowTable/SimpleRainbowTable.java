package rainbowTable;

import cracker.MdpCracker;
import hashage.SimpleHashFunction;
import password.PasswordGenerator;
import reduction.Reduction;
import util.SimpleGestionCSV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Main rainbow table class, can create a RT, and crack a hash that is within it.
 *
 * @author Mikail SARI
 * @author Joris MASSON
 */
public class SimpleRainbowTable {
    /**
     * Output directory for RT.
     */
    private static final String DEFAULT_OUTPUT_DIR = "fichierCSV/rainbowTable";

    /**
     * Max allowed iteration in the creation loop.
     */
    private static final int MAX_ITERATION_IN_CREATION = 500000;


    /**
     * Fixed number of lines of the RT.
     */
    private final int nbLines;

    /**
     * Length of all passwords.
     */
    private final int passwordsLength;

    /**
     * Hash algorithm that will be used.
     */
    private final String hashAlgo;

    /**
     * Reduction function that will be used.
     */
    private final Reduction reduction;

    /**
     * Total number of colors of the RT.
     */
    private final int nbColor;

    /**
     * Class that will be used for generating base passwords.
     */
    private final PasswordGenerator passwordGenerator;

    /**
     * If a base password already exists in the RT, ignore it. Incase the password generator is random for example.
     */
    private final boolean deleteDuplicatePasswords;

    /**
     * If a hash already exists in the RT, ignore it. Great speedup, but can remove useful chains. Butcher RT size.
     */
    private final boolean deleteDuplicateHashes;

    /**
     * Do the RT cache all table(passwords and hashes) in memory. Great speedup.
     */
    private final boolean cacheAllTable;

    /**
     * Do the RT tries to regenerate a line with a collision? Extreme speedup in search time.
     */
    private final boolean noCollisions;


    /**
     * Class used for managing the CSV representing the table.
     */
    private final SimpleGestionCSV gestionCSV;

    /**
     * Logger class
     */
    private final Logger logger = Logger.getLogger(MdpCracker.class.getPackage().getName());

    /**
     * Map used for associating a duplicate hash, with all of the passwords that are associated with it.
     */
    private final Map<String, Set<String>> duplicateHashesPasswords;

    /**
     * Used for storing all the RT in RAM.
     */
    private Map<String, String> allTable;

    /**
     * Main constructor.
     *
     * @param nbLines                  Fixed number of lines of the RT.
     * @param passwordsLength          Length of all passwords.
     * @param hashAlgo                 Hash algorithm that will be used.
     * @param reduction                Reduction function that will be used.
     * @param nbColor                  Total number of colors of the RT.
     * @param passwordGenerator        Class that will be used for generating base passwords.
     * @param charDebut                Start character for passwords.
     * @param charFin                  End character for passwords.
     * @param deleteDuplicatePasswords If a base password already exists in the RT, ignore it. Incase the password generator is random for example.
     * @param deleteDuplicateHashes    If a hash already exists in the RT, ignore it. Great speedup, but can remove useful chains. Butcher RT size.
     * @param cacheAllTable            Do the RT cache all table(passwords and hashes) in memory. Great speedup.
     * @param noCollisions             Do the RT tries to regenerate a line with a collision? Extreme speedup in search time.
     */
    public SimpleRainbowTable(int nbLines, int passwordsLength, String hashAlgo, Reduction reduction, int nbColor, PasswordGenerator passwordGenerator, int charDebut, int charFin, boolean deleteDuplicatePasswords, boolean deleteDuplicateHashes, boolean cacheAllTable, boolean noCollisions) {
        this.nbLines = nbLines;
        this.passwordsLength = passwordsLength;
        this.hashAlgo = hashAlgo;
        this.reduction = reduction;
        this.nbColor = nbColor;
        this.passwordGenerator = passwordGenerator;

        this.noCollisions = noCollisions;
        if (this.noCollisions) {
            this.deleteDuplicatePasswords = true;
            this.deleteDuplicateHashes = true;
        } else {
            this.deleteDuplicatePasswords = deleteDuplicatePasswords;
            this.deleteDuplicateHashes = deleteDuplicateHashes;
        }

        this.cacheAllTable = cacheAllTable;
        this.duplicateHashesPasswords = new HashMap<>();

        this.gestionCSV = new SimpleGestionCSV(DEFAULT_OUTPUT_DIR, nbColor, passwordsLength, hashAlgo, reduction.getClass().getSimpleName());
        if (cacheAllTable) {
            this.allTable = this.makeTableMap();
        } else {
            this.allTable = null;
        }
    }

    /**
     * Alternative constructor, without the use of the deleteDuplicatePasswords option. Default to <code>false</code>.
     *
     * @param nbLines           Fixed number of lines of the RT.
     * @param passwordsLength   Length of all passwords.
     * @param hashAlgo          Hash algorithm that will be used.
     * @param reduction         Reduction function that will be used.
     * @param nbColor           Total number of colors of the RT.
     * @param passwordGenerator Class that will be used for generating base passwords.
     * @param charDebut         Start character for passwords.
     * @param charFin           End character for passwords.
     */
    public SimpleRainbowTable(int nbLines, int passwordsLength, String hashAlgo, Reduction reduction, int nbColor, PasswordGenerator passwordGenerator, int charDebut, int charFin) {
        this(nbLines, passwordsLength, hashAlgo, reduction, nbColor, passwordGenerator, charDebut, charFin, false, false, true, false);
    }

    /**
     * Initialize and create the RT.
     */
    public void createTable() {
        Set<String> allPasswords = new HashSet<>();
        Set<String> allHashes = new HashSet<>();

        this.gestionCSV.initCSV();
        this.reduction.resetColor();

        int currentMaxIterations = 0;
        int lastLineNb = 0;
        for (int lineNb = 0; lineNb < this.nbLines; lineNb++) {
            String basePassword = this.passwordGenerator.generatePassword();

            /* Vérification d'un mdp dupliqué */
            if (this.deleteDuplicatePasswords && allPasswords.contains(basePassword)) {
                if (this.noCollisions) {
                    lineNb--;
                    if (currentMaxIterations > MAX_ITERATION_IN_CREATION) {
                        break;
                    }
                    if (lineNb == lastLineNb) {
                        currentMaxIterations++;
                    } else {
                        currentMaxIterations = 0;
                    }
                }
                continue;
            } else if (this.deleteDuplicatePasswords) {
                allPasswords.add(basePassword);
            }

            String finalHash = this.getLastHash(basePassword);  // génère le dernier hash

            /* Vérification d'un hash dupliqué */
            if (this.deleteDuplicateHashes && allHashes.contains(finalHash)) {
                lineNb--;
                if (currentMaxIterations > MAX_ITERATION_IN_CREATION) {
                    break;
                }
                if (lineNb == lastLineNb) {
                    currentMaxIterations++;
                } else {
                    currentMaxIterations = 0;
                }
                continue;
            } else if (this.deleteDuplicateHashes) {
                allHashes.add(finalHash);
            }
            this.gestionCSV.writeLine(basePassword, finalHash);
            lastLineNb = lineNb;
        }
        this.gestionCSV.close();

        if (this.cacheAllTable) {
            this.allTable = this.makeTableMap();
        }
    }

    /**
     * Find a password with a hash in the RT.
     *
     * @param hash Hash of the password to search.
     * @return The password, or <code>null</code> if not found.
     */
    public String crackHash(String hash) {
        this.reduction.resetColor();

        String firstVerification = this.checkAllLastHashes(hash);
        if (firstVerification != null) {
            return firstVerification;
        } else {
            return checkAllTable(hash);
        }
    }

    /**
     * Check all the last hashes of the RT, it is the first step.
     *
     * @param hash Hash of the password to search.
     * @return The password, if the hash exists. <code>null</code> otherwise.
     */
    private String checkAllLastHashes(String hash) {
        this.reduction.resetColor();
        /* Première étape, la recherche dans tous les hash disponibles */
        if (this.cacheAllTable) {
            if (this.allTable.containsKey(hash)) {
                String potentialPassword = this.findPasswordWithColorNb(this.allTable.get(hash), this.nbColor - 1);
                if (Objects.equals(hash, SimpleHashFunction.hashString(potentialPassword, this.hashAlgo))) {
                    return potentialPassword;
                }
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(this.gestionCSV.getFilename()))) {
                String line;
                while ((line = reader.readLine()) != null) {  // pour parcourir toutes les lignes
                    String[] parts = line.split(", ");
                    if (Objects.equals(hash, parts[1])) {  // check si le dernier hash est celui recherché
                        String potentialPassword = this.findPasswordWithColorNb(parts[0], this.nbColor - 1);
                        if (Objects.equals(hash, SimpleHashFunction.hashString(potentialPassword, this.hashAlgo))) {
                            return potentialPassword;
                        }
                    }
                }
            } catch (IOException e) {
                logger.severe("ERREUR survenue : " + e);
            }
        }
        return null;  // si rien n'est trouvé
    }

    /**
     * Check in all RT, second step of the RT.
     *
     * @param baseHash Hash of the password to search.
     * @return The password, if the hash exists. <code>null</code> otherwise.
     */
    private String checkAllTable(String baseHash) {
        this.reduction.resetColor();

        Map<Integer, Map<String, String>> res = new HashMap<>();  // pour stocker toutes les chaînes possibles à partir de telle couleur.

        String currentReduction;  // la réduction courante
        String currentHash;  // le hash courant

        /* Parcours de toutes les couleurs possibles(comme on ne peut pas savoir à l'avance dans quelle couleur est stocké le mdp */
        for (int colorNb = 1; colorNb < this.nbColor; colorNb++) {
            currentHash = baseHash;  // on repart du hash de base
            this.reduction.setColor(colorNb);  // on set une nouvelle couleur de départ
            Map<String, String> currentCouple = new HashMap<>();  // création du Map pour le résultat pour telle couleur

            /* Création de la chaîne, pas d'ordre, juste le contenu est important */
            for (int i = colorNb; i < this.nbColor; i++) {
                currentReduction = this.reduction.reduce(currentHash);  // on fait la réduction
                currentHash = SimpleHashFunction.hashString(currentReduction, this.hashAlgo);  // puis le hash de la réduction
                currentCouple.put(currentReduction, currentHash);  // enregistrement du résultat dans la chaîne
            }
            res.put(colorNb, currentCouple);  // enregistrement du résultat complet de la chaîne
        }

        /* On check tout le map(chaînes), tous les derniers hash, voir si à un moment on en retrouve un dans la chaîne */
        for (Map.Entry<Integer, Map<String, String>> colorEntry : res.entrySet()) {
            int currentColor = colorEntry.getKey();  // le numéro de couleur actuel

            /* Parcours de tous les élément de la "chaîne" */
            for (Map.Entry<String, String> reductionEntry : colorEntry.getValue().entrySet()) {
                String hash = reductionEntry.getValue();  // le hash
                if (this.cacheAllTable) {
                    if (this.allTable.containsKey(hash)) {
                        String potentialPassword = this.findPasswordWithColorNb(this.allTable.get(hash), currentColor - 1);  // essaye de récupérer le mot de passe via le numéro de couleur
                        if (SimpleHashFunction.hashString(potentialPassword, this.hashAlgo).equals(baseHash)) {  // check si le hash du mdp potentiel est bien le hash recherché
                            return potentialPassword;  // si oui, c'est gagné!
                        }
                    } else if (!this.deleteDuplicateHashes && this.duplicateHashesPasswords.containsKey(hash)) {
                        for (Map.Entry<String, Set<String>> duplicate : this.duplicateHashesPasswords.entrySet()) {
                            for (String basePassword : duplicate.getValue()) {
                                String potentialPassword = this.findPasswordWithColorNb(basePassword, currentColor - 1);  // essaye de récupérer le mot de passe via le numéro de couleur
                                if (SimpleHashFunction.hashString(potentialPassword, this.hashAlgo).equals(baseHash)) {  // check si le hash du mdp potentiel est bien le hash recherché
                                    return potentialPassword;  // si oui, c'est gagné!
                                }
                            }
                        }
                    }
                } else {
                    // Ouverture de la RT
                    try (BufferedReader reader = new BufferedReader(new FileReader(this.gestionCSV.getFilename()))) {
                        String line;
                        // Puis vérification de toutes les lignes
                        while ((line = reader.readLine()) != null) {  // pour parcourir toutes les lignes
                            String[] parts = line.split(", ");
                            if (parts[1].equals(hash)) {  // check si le dernier hash est celui recherché
                                String potentialPassword = this.findPasswordWithColorNb(parts[0], currentColor - 1);  // essaye de récupérer le mot de passe via le numéro de couleur
                                if (SimpleHashFunction.hashString(potentialPassword, this.hashAlgo).equals(baseHash)) {  // check si le hash du mdp potentiel est bien le hash recherché
                                    return potentialPassword;  // si oui, c'est gagné!
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.severe("ERREUR survenue : " + e);
                    }
                }
            }
        }

        return null;  // si rien n'est trouvé
    }

    /**
     * Get the last hash of the chain of the RT, given a base password.
     *
     * @param basePassword Base password.
     * @return Last hash of the chain.
     */
    private String getLastHash(String basePassword) {
        String currentPassword = basePassword;
        String currentHash = "";
        /* Création de toute la chaîne */
        for (int colorNb = 0; colorNb < this.nbColor; colorNb++) {
            currentHash = SimpleHashFunction.hashString(currentPassword, this.hashAlgo);
            // System.out.printf("%s -> %s -> ", currentPassword, currentHash);
            currentPassword = this.reduction.reduce(currentHash);
        }
        // System.out.println();
        return currentHash;  // le dernier hash courant calculé est le dernier de la chaîne, on le retourne
    }

    /**
     * Get a password with a given color number, and the base password of the chain.
     *
     * @param basePassword Base password of the chain.
     * @param colorNb      Number of the color.
     * @return Corresponding password.
     */
    private String findPasswordWithColorNb(String basePassword, int colorNb) {
        this.reduction.resetColor();
        String currentPassword = basePassword;

        for (int color = 0; color < colorNb; color++) {
            String currentHash = SimpleHashFunction.hashString(currentPassword, this.hashAlgo);
            currentPassword = this.reduction.reduce(currentHash);
        }
        return currentPassword;
    }

    /**
     * If <code>cacheAllTable</code> option is activated, this method create the cache.
     *
     * @return The <code>Map</code> that contains the cache.
     */
    private Map<String, String> makeTableMap() {
        Map<String, String> res = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.gestionCSV.getFilename()))) {
            String line;
            while ((line = reader.readLine()) != null) {  // pour parcourir toutes les lignes
                String[] parts = line.split(", ");
                if (!res.containsKey(parts[1]) && !this.duplicateHashesPasswords.containsKey(parts[1])) {
                    res.put(parts[1], parts[0]);
                } else {
                    if (this.duplicateHashesPasswords.containsKey(parts[1])) {
                        this.duplicateHashesPasswords.get(parts[1]).add(parts[0]);
                    } else {
                        res.remove(parts[1]);
                        Set<String> duplicateHashPasswords = new HashSet<>();
                        duplicateHashPasswords.add(parts[0]);
                        this.duplicateHashesPasswords.put(parts[1], duplicateHashPasswords);
                    }
                }
            }
        } catch (IOException e) {
            logger.warning("Table non existante pour le chargement dans le Map.");
        }
        if (!this.deleteDuplicateHashes && res.keySet().size() != this.nbLines) {
            logger.warning(String.format("Il y a %d collisions dans la colonne des hashes. Un manque de précision est à prévoir.", this.duplicateHashesPasswords.keySet().size()));
        }
        return res;
    }

    @Override
    public String toString() {
        String reductionName = this.reduction.getClass().getSimpleName();
        String generatorName = this.passwordGenerator.getClass().getSimpleName();

        return String.format(
                "nbLines=%d; passwordsLength=%d; hashAlgo=%s; reduction=%s; nbColor=%d; passwordGenerator=%s; deleteDuplicatePasswords=%b; deleteDuplicateHashes=%b; cacheAllTable=%b",
                this.nbLines,
                this.passwordsLength,
                this.hashAlgo,
                reductionName,
                this.nbColor,
                generatorName,
                this.deleteDuplicatePasswords,
                this.deleteDuplicateHashes,
                this.cacheAllTable
        );
    }
}
