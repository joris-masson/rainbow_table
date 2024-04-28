package reduction.markov;

import java.util.*;

/**
 * Abstract class for MarkovTextTraining
 */
public abstract class AbstractMarkovTextTraining implements MarkovTextTraining {
    /**
     * <code>Character</code> that are forbidden and must not be counted.
     */
    protected static final Set<Character> FORBIDDEN_CHARS = new HashSet<>(Arrays.asList(
            '\n',
            '\r'
    ));

    /**
     * The text that will be used for generating the matrix
     */
    protected String text;

    /**
     * (Optionnal) A domain, characters in this <code>Set</code> are the only characters that will be taken into account.
     * Without a domain, all characters that are not forbidden will be used.
     */
    protected Set<Character> domain;

    /**
     * Main constructor.
     *
     * @param text   The text that will be used for generating the matrix.
     * @param domain A domain, characters in this <code>Set</code> are the only characters that will be taken into account.
     */
    public AbstractMarkovTextTraining(String text, Set<Character> domain) {
        this.domain = domain;
        this.text = this.cleanText(text);
    }

    /**
     * Constructor without a domain. It will be set to <code>null</code>.
     *
     * @param text The text that will be used for generating the matrix.
     */
    public AbstractMarkovTextTraining(String text) {
        this(text, null);
    }

    /**
     * Generate a "count matrix", which is just a <code>Map</code> with <code>char</code> counts in it.
     *
     * @return A count matrix.
     */
    protected Map<Object, Map<Object, Integer>> getCountMatrix() {
        /* un char est associé à un map des caractères qui le suivent, et du nombre de fois où il a été suivi par
        ce caractère. */
        Map<Object, Map<Object, Integer>> countMatrix = new HashMap<>();
        for (int i = 0; i < this.text.length(); i++) {
            if (i == this.text.length() - 1) {  // on ignore le dernier char
                break;
            }

            char currentLetter = this.text.charAt(i);  // la lettre actuelle
            char nextLetter = this.text.charAt(i + 1);  // la lettre qui la suit

            if (!countMatrix.containsKey(currentLetter)) {  // on vérifie si le hashmap ne contient pas déjà la lettre
                countMatrix.put(currentLetter, new HashMap<>());  // initialise un nouveau map avec la lettre en clé
            }
            if (!countMatrix.get(currentLetter).containsKey(nextLetter)) {  // vérifie si le map lié à la lettre actuelle ne contient pas la lettre qui la suit
                countMatrix.get(currentLetter).put(nextLetter, 1);  // la rajoute et initialise son compteur à 1
            } else {
                // sinon on incrémente ce compteur
                countMatrix.get(currentLetter).put(nextLetter, countMatrix.get(currentLetter).get(nextLetter) + 1);
            }
        }
        return countMatrix;
    }

    /**
     * Clean the entire text, removing forbidden chars and, if specified, chars that are not part of the domain.
     *
     * @param text The text to clean.
     * @return Clean text.
     */
    protected String cleanText(String text) {
        StringBuilder res = new StringBuilder();
        for (char c : text.toCharArray()) {
            if ((this.domain == null && !FORBIDDEN_CHARS.contains(c)) || (this.domain != null && this.domain.contains(c) && !FORBIDDEN_CHARS.contains(c))) {
                res.append(c);
            }
        }
        return res.toString();
    }

    @Override
    public void setDomain(Set<Character> newDomain) {
        this.domain = newDomain;
        this.text = this.cleanText(this.text);
    }

    @Override
    public Map<Object, Map<Object, Float>> getMatrix() {
        Map<Object, Map<Object, Integer>> countMatrix = this.getCountMatrix();
        Map<Object, Map<Object, Float>> transitionMatrix = new HashMap<>();

        for (Object character : countMatrix.keySet()) {
            Map<Object, Float> res = new HashMap<>();

            int total = 0;
            for (int nb : countMatrix.get(character).values()) {
                total += nb;
            }
            for (Object nextChar : countMatrix.get(character).keySet()) {
                float proba = (float) countMatrix.get(character).get(nextChar) / total;
                res.put(nextChar, proba);
            }

            transitionMatrix.put(character, res);
        }
        return transitionMatrix;
    }
}
