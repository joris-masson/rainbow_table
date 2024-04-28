package reduction;

import java.util.HashSet;

/**
 * Simple reduction class.
 * It just takes hashcode of the hash and cut it to match maximum size.
 *
 * @author Joris MASSON
 * @see AbstractReduction
 */
public class HashcodeReduction extends AbstractReduction {
    /**
     * Base constructor, it just takes a maximum size, as the domain will be [0-9] anyway.
     *
     * @param maxSize maximum size of the reduction result.
     */
    public HashcodeReduction(int maxSize) {
        super(maxSize, new HashSet<>());
    }

    @Override
    public String reduce(String hash) {
        String res = String.valueOf(hash.hashCode());
        return this.cutToMaxSize(res);
    }
}
