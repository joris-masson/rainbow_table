package reduction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HashcodeReductionTest {
    @Test
    void reduce() {
        HashcodeReduction hashcodeReduction = new HashcodeReduction(6);
        /* hash MD5: https://fr.wikipedia.org/wiki/MD5 */
        assertTrue(hashcodeReduction.reduce("8747e564eb53cb2f1dcb9aae0779c2aa").length() <= 6);
        assertTrue(hashcodeReduction.reduce("c802e1bd9b5f2b0d244bbc982f5082b3").length() <= 6);
    }
}