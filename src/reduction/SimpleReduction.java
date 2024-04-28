package reduction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Simple reduction class.
 * Takes into account the domain by taking hashcode of the hash, then use it for getting characters from the domain by
 * index.
 *
 * @author Joris MASSON
 * @see AbstractReduction
 */
public class SimpleReduction extends AbstractReduction {
    /**
     * Constructor
     *
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public SimpleReduction(int maxSize, Set<Character> domain) {
        super(maxSize, domain);
    }

    /**
     * Constructor that takes a <code>String</code> for domain.
     *
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public SimpleReduction(int maxSize, String domain) {
        super(maxSize, domain);
    }

    @Override
    public String reduce(String hash) {
        int[] indexes = Integer.toString(Math.abs(hash.hashCode())).chars().map(c -> c - '0').toArray();  // https://stackoverflow.com/a/30022561

        StringBuilder res = new StringBuilder();  // le résultat qui sera renvoyé
        List<Character> zeList = new ArrayList<>(this.domain);  // on transforme le set en une liste, où on pourra récupérer les éléments par index
        for (int i : indexes) {
            if (i < zeList.size()) {
                res.append(zeList.get(i));
            } else {
                res.append(zeList.get(0));  // prends le premier élément si un i est supérieur à la taille de la liste
            }
        }

        return this.cutToMaxSize(res.toString());  // on coupe puis on renvoit
    }
}
