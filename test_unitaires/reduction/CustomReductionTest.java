package reduction;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomReductionTest {

    @Test
    public void testReduce() {
        // Taille maximale de la réduction
        int maxSize = 5;

        // Cas de test
        String hash1 = "md5hash1234567890";
        String expectedResult1 = "qazwsx";

        String hash2 = "0987654321fedcba";
        String expectedResult2 = "vbnmlk";

        // Créer une instance de CustomReduction
        CustomReduction reducer = new CustomReduction(maxSize);

        // Tester la méthode reduce()
        String actualResult1 = reducer.reduce(hash1);
        assertEquals(expectedResult1, actualResult1);

        String actualResult2 = reducer.reduce(hash2);
        assertEquals(expectedResult2, actualResult2);
    }

    // Ajoutez des cas de test supplémentaires pour couvrir différents scénarios et cas limites.
}

