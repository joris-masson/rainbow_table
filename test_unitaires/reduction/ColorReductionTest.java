package reduction;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ColorReductionTest {
    @Test
    void reduce() {
        Set<Character> domain = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's'));
        ColorReduction colorReduction = new ColorReduction(2, 5, domain);

        String s1 = "8747e564eb53cb2f1dcb9aae0779c2aa";

        String firstReduce = colorReduction.reduce(s1);
        String secondReduce = colorReduction.reduce(s1);
        String thirdReduce = colorReduction.reduce(s1);

        assertTrue(firstReduce.length() <= 5);

        // première couleur, donc elle ne doit pas être la même que la deuxième
        assertNotEquals(firstReduce, secondReduce);

        // la troisième réduction fait en sorte d'atteindre le max de couleurs, donc le compteur est reset, on repart à
        // la première couleur
        assertEquals(firstReduce, thirdReduce);
    }
}