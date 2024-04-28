package reduction;

import testing.Util;

import java.nio.file.Paths;
import java.util.Set;

public class Demo {
    public static void main(String[] args) {
        Set<String> hashes = Util.generateAllHash(100000, 6, "MD5");

        // Reduction markovReduction = new MarkovReduction(10, 6, "azertyuiopqsdfghjklmwxcvbn", Paths.get("texts/train.txt"));
        Reduction colorReduction = new ColorReduction(10, 6, "azertyuiopqsdfghjklmwxcvbn");

        for (String hash : hashes) {
            String reduc = colorReduction.reduce(hash);
            System.out.printf("%s -> %s\n", hash, reduc);
            if (reduc.equals("amalia")) {
                break;
            }
        }
    }
}
