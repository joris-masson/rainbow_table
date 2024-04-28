package reduction;

import java.math.BigInteger;
import java.util.HashSet;

/**
 * La classe {@code CustomReduction} étend {@code AbstractReduction} pour offrir une implémentation personnalisée
 * de la réduction de hash. Elle est conçue pour réduire un hash MD5 en une chaîne de caractères de longueur fixe,
 * en minimisant les collisions. Cette approche est utile dans le cadre de la cryptanalyse, notamment pour les attaques
 * sur les tables arc-en-ciel ou pour toute autre application nécessitant une réduction efficace de l'espace des hash.
 * @author Abdellah BENTEBIBEL
 */
public class CustomReduction extends AbstractReduction {

    /**
     * Constructeur de {@code CustomReduction}.
     * Initialise une nouvelle instance avec la taille maximale spécifiée pour la chaîne après réduction.
     * 
     * @param maxSize Taille maximale autorisée après une réduction, définissant la longueur de la chaîne de sortie.
     */
    public CustomReduction(int maxSize) {
        super(maxSize, new HashSet<>());
    }

    /**
     * Réduit un hash MD5 en une chaîne de caractères de longueur spécifiée en générant une séquence
     * qui vise à minimiser les collisions. Cette méthode convertit le hash donné en un grand nombre entier
     * et construit la chaîne de sortie caractère par caractère en utilisant une simple transformation basée
     * sur le reste de la division, garantissant une diversité dans les résultats tout en respectant la taille maximale.
     * 
     * @param hash Le hash MD5 à réduire sous forme de chaîne hexadécimale.
     * @return Une chaîne de caractères résultante de la réduction du hash, de longueur {@code maxSize}.
     */
    @Override
    public String reduce(String hash) {
        // Convertir la chaîne hexadécimale en un nombre BigInteger
        BigInteger hashInt = new BigInteger(hash, 16);

        // Créer un StringBuilder pour construire la chaîne réduite
        StringBuilder result = new StringBuilder();

        // Boucler jusqu'à ce que la longueur désirée soit atteinte
        while (result.length() < this.maxSize) {
            // Convertir une partie du BigInteger en caractère et ajouter au résultat
            result.append((char) ('a' + hashInt.mod(BigInteger.valueOf(26)).intValue()));
            // Diviser 'hashInt' pour obtenir le prochain ensemble de valeurs
            hashInt = hashInt.divide(BigInteger.valueOf(26));
        }

        return result.toString();
    }
}

