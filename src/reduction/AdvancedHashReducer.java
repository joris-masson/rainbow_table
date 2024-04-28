package reduction;

import java.math.BigInteger;
import java.util.HashSet;

/**
 * La classe {@code AdvancedHashReducer} est une implémentation spécialisée de {@code ColorReduction}
 * pour la réduction de hash. Elle utilise une table alphanumérique pour convertir un hash en une chaîne
 * de caractères alphanumériques de longueur fixe, basée sur le principe de la table arc-en-ciel.
 * Cette classe est conçue pour être utilisée dans le contexte de la cryptographie et de la sécurité informatique,
 * spécifiquement pour la génération et la réduction de hash dans le cadre de la récupération de mots de passe.
 * @author Abdellah BENTEBIBEL
 */
public class AdvancedHashReducer extends ColorReduction {

    private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ALPHANUM_LEN = ALPHANUM.length();

    /**
     * Constructeur de {@code AdvancedHashReducer}.
     * Initialise une nouvelle instance avec le nombre de couleurs spécifié pour la table arc-en-ciel et la taille maximale autorisée après réduction.
     * 
     * @param nbColor Nombre de couleurs de la table arc-en-ciel.
     * @param maxSize Taille maximale autorisée après une réduction.
     */
    public AdvancedHashReducer(int nbColor, int maxSize) {
        super(nbColor, maxSize, new HashSet<>());
    }

    /**
     * Réduit un hash en une chaîne de caractères alphanumériques en utilisant une table de correspondance alphanumérique.
     * Cette méthode convertit le hash donné en un nombre, l'ajuste en fonction de la couleur actuelle de la table arc-en-ciel,
     * puis le convertit en une chaîne de caractères alphanumériques de taille définie par {@code maxSize}.
     * Après chaque réduction, la couleur courante de la table arc-en-ciel est mise à jour pour la prochaine itération.
     * 
     * @param hash Le hash à réduire.
     * @return Une chaîne de caractères alphanumériques résultante de la réduction du hash.
     */
    @Override
    public String reduce(String hash) {
        StringBuilder result = new StringBuilder();
        long value = new BigInteger(hash, 16).add(BigInteger.valueOf(this.currentColor)).mod(BigInteger.valueOf(ALPHANUM_LEN)).longValue();

        for (int i = 0; i < this.maxSize; i++) {
            result.append(ALPHANUM.charAt((int) (value % ALPHANUM_LEN)));
            value = value / ALPHANUM_LEN;
        }

        this.updateColor();
        return result.toString();
    }
}

