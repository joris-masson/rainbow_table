package reduction;

import reduction.markov.CharMarkovChain;
import reduction.markov.CharMarkovTextTraining;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reduction class that uses Markov chains for generating passwords.
 *
 * @author Joris MASSON
 * @see Reduction
 * @see reduction.markov.MarkovChain
 */
public class MarkovReduction extends ColorReduction {
    /**
     * Markov chain that will we used for generating passwords
     */
    private final CharMarkovChain charMarkovChain;

    /**
     * Constructor
     *
     * @param nbColor      Number of colors of the rainbow table.
     * @param maxSize      Maximum size allowed after a reduction.
     * @param domain       Domain of the password characters.
     * @param trainingFile path to a training file.
     */
    public MarkovReduction(int nbColor, int maxSize, Set<Character> domain, Path trainingFile) {
        super(nbColor, maxSize, domain);
        this.charMarkovChain = new CharMarkovChain(DEFAULT_BASE_COLOR, this.getTraining(trainingFile).getMatrix());
    }

    /**
     * Constructor
     *
     * @param nbColor      Number of colors of the rainbow table.
     * @param maxSize      Maximum size allowed after a reduction.
     * @param domain       Domain of the password characters.
     * @param trainingFile path to a training file.
     */
    public MarkovReduction(int nbColor, int maxSize, String domain, Path trainingFile) {
        super(nbColor, maxSize, domain);
        this.charMarkovChain = new CharMarkovChain(DEFAULT_BASE_COLOR, this.getTraining(trainingFile).getMatrix());
    }

    /**
     * Constructor
     *
     * @param nbColor Number of colors of the rainbow table.
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public MarkovReduction(int nbColor, int maxSize, Set<Character> domain) {
        this(nbColor, maxSize, domain, FileSystems.getDefault().getPath("texts/train.txt"));
    }

    /**
     * Constructor
     *
     * @param nbColor Number of colors of the rainbow table.
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public MarkovReduction(int nbColor, int maxSize, String domain) {
        this(nbColor, maxSize, domain, FileSystems.getDefault().getPath("texts/train.txt"));
    }

    @Override
    public String reduce(String hash) {
        int hashHashcode = hash.hashCode() * 177013 * this.currentColor;
        StringBuilder res = new StringBuilder();
        this.charMarkovChain.setState(this.findFirstState(hash));
        this.charMarkovChain.setSeed((long) hashHashcode * this.currentColor);
        for (int i = 0; i < this.currentColor; i++) {
            this.charMarkovChain.nextState();
        }
        for (int i = 0; i < this.maxSize; i++) {
            res.append(this.charMarkovChain.nextState());
        }
        this.updateColor();
        return res.toString();
    }

    /**
     * Utility method for finding the first state of the reduced hash.
     *
     * @param hash hash.
     * @return the first <code>char</code> of the reduction.
     */
    private char findFirstState(String hash) {
        int hashHashcode = hash.hashCode() * this.currentColor;
        List<Character> zeList = new ArrayList<>(this.domain);

        return zeList.get(Math.abs(hashHashcode * this.currentColor) % zeList.size());
    }

    /**
     * Get the <code>MarkovTextTraining</code> with the training file <code>Path</code>.
     *
     * @param trainingFile Path of the training file to use.
     * @return The <code>MarkovTextTraining</code> trained with data from the training file
     */
    private CharMarkovTextTraining getTraining(Path trainingFile) {
        CharMarkovTextTraining markovTextTraining;
        try {
            markovTextTraining = new CharMarkovTextTraining(Files.readString(trainingFile, StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            markovTextTraining = new CharMarkovTextTraining("String de base en cas d'erreur");
        }
        markovTextTraining.setDomain(this.domain);
        return markovTextTraining;
    }
}
