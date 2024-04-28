package reduction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reduction class that takes into account a color number.
 *
 * @author Joris MASSON
 */
public class ColorReduction extends AbstractReduction {
    /**
     * The default color
     */
    protected static final int DEFAULT_BASE_COLOR = 1;

    /**
     * Current color
     */
    protected int currentColor = DEFAULT_BASE_COLOR;

    /**
     * Numbr of colors
     */
    protected final int nbColor;

    /**
     * Constructor
     *
     * @param nbColor Number of colors of the rainbow table.
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public ColorReduction(int nbColor, int maxSize, Set<Character> domain) {
        super(maxSize, domain);
        this.nbColor = nbColor;
    }

    /**
     * Constructor
     *
     * @param nbColor Number of colors of the rainbow table.
     * @param maxSize Maximum size allowed after a reduction.
     * @param domain  Domain of the password characters.
     */
    public ColorReduction(int nbColor, int maxSize, String domain) {
        super(maxSize, domain);
        this.nbColor = nbColor;
    }

    @Override
    public String reduce(String hash) {
        StringBuilder res = new StringBuilder();
        List<Character> zeList = new ArrayList<>(this.domain);
        for (int i = 1; i < hash.length(); i++) {
            res.append(zeList.get(Math.abs((currentColor * i * hash.charAt(i)) % domain.size())));
        }
        this.updateColor();
        return this.cutToMaxSize(res.toString());
    }

    /**
     * Update the current color based on number of times this class reduced a hash.
     */
    protected void updateColor() {
        if (this.currentColor >= DEFAULT_BASE_COLOR + this.nbColor - 1) {
            this.currentColor = DEFAULT_BASE_COLOR;
        } else {
            this.currentColor++;
        }
    }

    @Override
    public void setColor(int colorNb) {
        this.currentColor = colorNb;
    }

    @Override
    public void resetColor() {
        this.setColor(DEFAULT_BASE_COLOR);
    }
}
