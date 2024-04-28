package reduction;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleReductionTest {
    @Test
    void reduce() {
        Set<Character> domain1 = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd'));
        Set<Character> domain2 = new HashSet<>(Arrays.asList('e', 'f', 'g', 'h'));

        Reduction simpleReduce = new SimpleReduction(4, domain1);
        String res1 = simpleReduce.reduce("8747e564eb53cb2f1dcb9aae0779c2aa");
        String res2 = simpleReduce.reduce("c802e1bd9b5f2b0d244bbc982f5082b3");

        assertTrue(res1.length() <= 4);
        assertTrue(res1.length() <= 4);
        assertTrue(allInDomain(res1, domain1));
        assertTrue(allInDomain(res2, domain1));
        assertFalse(allInDomain(res1, domain2));
        assertFalse(allInDomain(res2, domain2));
    }

    private boolean allInDomain(String str, Set<Character> domain) {
        for (char c : str.toCharArray()) {
            if (!domain.contains(c)) {
                return false;
            }
        }
        return true;
    }
}