package reduction;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AdvancedHashReducerTest {

    @Test
    public void testReduce() {
        // Parameters for AdvancedHashReducer
        int nbColor = 10;
        int maxSize = 5;

        // Test cases
        String hash1 = "abcdef1234567890";
        String expectedResult1 = "1z54a";

        String hash2 = "54321fedcba09876";
        String expectedResult2 = "p054a";

        // Create AdvancedHashReducer instance
        AdvancedHashReducer reducer = new AdvancedHashReducer(nbColor, maxSize);

        // Test reduce() method
        String actualResult1 = reducer.reduce(hash1);
        assertEquals(expectedResult1, actualResult1);

        String actualResult2 = reducer.reduce(hash2);
        assertEquals(expectedResult2, actualResult2);
    }


}

