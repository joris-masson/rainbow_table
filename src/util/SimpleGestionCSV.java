package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class that manage a CSV file, representing the rainbow table.
 *
 * @author Mikail SARI
 */
public class SimpleGestionCSV {
    /**
     * Directory of all tables.
     */
    private final String cheminMdp;


    /**
     * File of the RT
     */
    private FileWriter fichierEcriture;

    /**
     * Writer of the RT.
     */
    private BufferedWriter writer;


    /**
     * Filename of the RT.
     */
    private String filename;

    /**
     * Main constructor.
     *
     * @param cheminMdp   Directory of all tables.
     * @param chainLength Number of colors.
     * @param tailleMdp   Length of passwords.
     * @param fctHashage  Hash algorithm used.
     * @param reduction   Used reduction function.
     */
    public SimpleGestionCSV(String cheminMdp, int chainLength, int tailleMdp, String fctHashage, String reduction) {
        this.cheminMdp = cheminMdp;
        this.initFilename(chainLength, tailleMdp, fctHashage, reduction);
    }

    /**
     * Initialize the name of the RT.
     * @param chainLength Number of colors.
     * @param tailleMdp Length of passwords.
     * @param fctHashage Hash algorithm used.
     * @param reduction Used reduction function.
     */
    public void initFilename(int chainLength, int tailleMdp, String fctHashage, String reduction) {
        if (chainLength == 1) {
            this.filename = String.format("%s/mdp_taille_%d_%s_%d_reduction_%s.csv", cheminMdp, tailleMdp, fctHashage, chainLength, reduction);
        } else {
            this.filename = String.format("%s/mdp_taille_%d_%s_%d_reductions_%s.csv", cheminMdp, tailleMdp, fctHashage, chainLength, reduction);
        }
    }

    /**
     * Write a line.
     * @param basePassword A base password.
     * @param finalHash The final hash of the chain.
     */
    public void writeLine(String basePassword, String finalHash) {
        try {
            this.fichierEcriture.append(basePassword).append(", ");
            this.fichierEcriture.append(finalHash);
            this.fichierEcriture.append("\n");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Initialize the file of the RT.
     */
    public void initCSV() {
        // si jamais le chemin de destination n'existe pas
        if (Files.notExists((Path.of(this.cheminMdp)))) {
            new File(this.cheminMdp).mkdirs();
        }

        try {
            File fichierCSV = new File(this.filename);
            this.writer = new BufferedWriter(new FileWriter(this.filename));

            if (fichierCSV.exists()) {
                fichierCSV.delete();
            }

            this.fichierEcriture = new FileWriter(this.filename);
            this.fichierEcriture.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Close the file, and stop writing.
     */
    public void close() {
        try {
            this.fichierEcriture.close();
            this.writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Getter for the filename.
     * @return The filename.
     */
    public String getFilename() {
        return this.filename;
    }
}
