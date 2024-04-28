package password;

/**
 * Generator that generate all possible passwords.
 *
 * @author William MIERE
 * @see PasswordGenerator
 * @see AbstractPasswordGenerator
 */
public class IncrementalPasswordGenerator extends AbstractPasswordGenerator {
    /**
     * Maximum index of the generator.
     */
    private final int maxIndex;
    /**
     * Current password of the generator.
     */
    private StringBuilder currentPassword;
    /**
     * Current index of the generator.
     */
    private int index;

    /**
     * Main constructor.
     *
     * @param passwordsLength Length of all passwords.
     * @param charDebut       Start character for passwords.
     * @param charFin         End character for passwords.
     * @param nbColor         Total number of colors.
     */
    public IncrementalPasswordGenerator(int passwordsLength, int charDebut, int charFin, int nbColor) {
        super(passwordsLength, charDebut, charFin);
        /**
         * Total number of colors.
         */

        this.initCurrentPassword();
        this.maxIndex = (int) ((Math.pow(charFin - charDebut, passwordsLength)) / (nbColor + 1));
    }

    @Override
    public String generatePassword() {
        String password = this.currentPassword.toString();

        if (this.index < this.maxIndex) {
            this.currentPassword.setCharAt(this.passwordsLength - 1, (char) (this.currentPassword.charAt(this.passwordsLength - 1) + 1));
            this.generateNextPassword();
        } else {
            this.initCurrentPassword();
        }

        return password;
    }

    /**
     * Generate the next password.
     */
    private void generateNextPassword() {
        int j = 1;

        if (this.currentPassword.charAt(this.passwordsLength - j) > (char) this.charFin) {
            this.currentPassword.setCharAt(this.passwordsLength - j, (char) this.charDebut);
            this.currentPassword.setCharAt(this.passwordsLength - j - 1, (char) (this.currentPassword.charAt(this.passwordsLength - j - 1) + 1));
            j++;
            while (this.currentPassword.charAt(this.passwordsLength - j) > (char) this.charFin && j < this.passwordsLength) {
                this.currentPassword.setCharAt(this.passwordsLength - j, (char) this.charDebut);
                this.currentPassword.setCharAt(this.passwordsLength - j - 1, (char) (this.currentPassword.charAt(this.passwordsLength - j - 1) + 1));
                j++;
            }
        }

        String currentPassword = String.valueOf(this.currentPassword);

        this.index++;
    }

    /**
     * Initialization of the password.
     */
    private void initCurrentPassword() {
        this.currentPassword = new StringBuilder();
        this.currentPassword.setLength(this.passwordsLength);
        for (int i = 0; i < this.passwordsLength; i++) { // Construction des mots de passes de tailleMDP caractères
            this.currentPassword.setCharAt(i, (char) (this.charDebut));
        }

        this.index = 0;
    }
}
