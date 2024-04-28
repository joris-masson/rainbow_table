package rainbowTable;

import hashage.SimpleHashFunction;
import reduction.*;
import util.xml.PerfGenXMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedWriter;

/**
 * Classe de contrôle pour la gestion du CSV
 * Permet de modifier ce dernier
 */
public class GestionCSV {

	private static final String RESULTS_OUTPUT_DIR = "output_data/testing/generation/perf";
	private SimpleHashFunction simpleHashFunction;
	private String cheminMdp;
	private FileWriter fichierEcriture;
	private BufferedWriter writer;
	private int tailleMdp;
	private String fctHashage;
	private int charDebut;
	private int charFin;
	private Set<Character> domain;
	private String reduction;
	private int chainLength; // longueur chaine

	/**
	 * Créer une nouvelle instance avec un cheminMdp spécifié
	 *
	 * @param cheminMdp Chemin du fichier d'une base de mots de passe (brute) d'une
	 *                  taille donnée
	 */
	public GestionCSV(String cheminMdp, String fctHashage, int charD, int charF, int taille, int chainLength, String reduction) {
		this.simpleHashFunction = new SimpleHashFunction();
		this.cheminMdp          = cheminMdp;
		this.fctHashage         = fctHashage;
		this.tailleMdp          = taille;
		this.charDebut          = charD;
		this.charFin            = charF;
		this.chainLength        = chainLength;
		this.reduction 			= reduction;
		this.domain = new HashSet<Character>(charFin-charDebut);
		for (int i = 0; i < charFin-charDebut; i++){
            domain.add((char)i);
		}

		createCSV();
	}

	/**
	 * Créer et initialise un csv avec le nom correspondant qui a 2 colonnes (mdp,
	 * hash)
	 */
	public void createCSV() {
		try {
			String nomFichier;
			if(this.chainLength==1){
				nomFichier = cheminMdp + "/mdp_taille_" + tailleMdp + "_" + fctHashage + "_" + chainLength + "_reduction_" + reduction + ".csv";
			}else{
				nomFichier = cheminMdp + "/mdp_taille_" + tailleMdp + "_" + fctHashage + "_" + chainLength + "_reductions" + ".csv";
			}
			File fichierCSV = new File(nomFichier);
			System.out.println(nomFichier);
			this.writer = new BufferedWriter(new FileWriter(nomFichier));
			
			if (fichierCSV.exists()) {
				fichierCSV.delete();
			}

			this.fichierEcriture = new FileWriter(nomFichier);
			this.fichierEcriture.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ajoute dans le csv à chaque ligne, le mdp et son hash
	 */
	public void insertCSV() {
		try {

			StringBuilder mdp = new StringBuilder(); // mot de passe courant
			StringBuilder stop = new StringBuilder(); // Sert juste à l'arrêt de la boucle, c'est de dernier mot de passe
			mdp.setLength(this.tailleMdp);
			stop.setLength(this.tailleMdp);

			for (int i = 0; i < this.tailleMdp; i++) { // Construction des mots de passes de tailleMDP caractères
				mdp.setCharAt(i, (char) (this.charDebut));
				stop.setCharAt(i, (char) this.charFin);
			}

			for (int i = 0; i < (Math.pow(charFin - charDebut, tailleMdp)) / (chainLength+1); i++) { // Boucle principale créant les mots de passe à la chaine
				int j = 1;

				if (mdp.charAt(this.tailleMdp - j) > (char) this.charFin) {
					mdp.setCharAt(this.tailleMdp - j, (char) this.charDebut);
					mdp.setCharAt(this.tailleMdp - j - 1, (char) (mdp.charAt(this.tailleMdp - j - 1) + 1));
					j++;
					while (mdp.charAt(this.tailleMdp - j) > (char) this.charFin && j<this.tailleMdp) {
						mdp.setCharAt(this.tailleMdp - j, (char) this.charDebut);
						mdp.setCharAt(this.tailleMdp - j - 1, (char) (mdp.charAt(this.tailleMdp - j - 1) + 1));
						j++;
					}
				}

				String currentPassword = String.valueOf(mdp);
				String currentHash = "";

				for (int l = 0; l < chainLength; l++) { // Hash & Réduit un certain nb de fois (couleurs)
					currentHash = simpleHashFunction.hashString(currentPassword, fctHashage);
					Reduction hashcodeReduction = createReduction(l, this.reduction, currentPassword);
					currentPassword = hashcodeReduction.reduce(currentHash);
				}

				currentHash = simpleHashFunction.hashString(currentPassword, fctHashage); // Une dernière fois pour avoir un hash

				this.fichierEcriture.append(mdp + ", ");
				this.fichierEcriture.append(currentHash);
				this.fichierEcriture.append("\n");
				mdp.setCharAt(this.tailleMdp - 1, (char) (mdp.charAt(this.tailleMdp - 1) + 1));
			}
			this.fichierEcriture.close();
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void generationNaive() {
		try {
			StringBuilder mdp = new StringBuilder(); // mot de passe courant
			StringBuilder stop = new StringBuilder(); // Sert juste à l'arrêt de la boucle, c'est de dernier mot de passe
			mdp.setLength(this.tailleMdp);
			stop.setLength(this.tailleMdp);

			for (int i = 0; i < this.tailleMdp; i++) { // Construction des mots de passes de tailleMDP caractères
				mdp.setCharAt(i, (char) (this.charDebut));
				stop.setCharAt(i, (char) this.charFin);
			}
			while (!(String.valueOf(mdp).equals(String.valueOf(stop)))) { // Boucle principale créant les mots de passe à la chaine
				int i = 1;

				if (mdp.charAt(this.tailleMdp - i) > (char) this.charFin) {
					mdp.setCharAt(this.tailleMdp - i, (char) this.charDebut);
					mdp.setCharAt(this.tailleMdp - i - 1, (char) (mdp.charAt(this.tailleMdp - i - 1) + 1));
					i++;
					while (mdp.charAt(this.tailleMdp - i) > (char) this.charFin && i<this.tailleMdp) {
						mdp.setCharAt(this.tailleMdp - i, (char) this.charDebut);
						mdp.setCharAt(this.tailleMdp - i - 1, (char) (mdp.charAt(this.tailleMdp - i - 1) + 1));
						i++;
					}
				}

				String currentPassword = String.valueOf(mdp);
				String currentHash = "";

				currentHash = simpleHashFunction.hashString(currentPassword, fctHashage); // Une dernière fois pour avoir un hash
				this.fichierEcriture.append(mdp + ", ");
				this.fichierEcriture.append(currentHash);
				this.fichierEcriture.append("\n");
				mdp.setCharAt(this.tailleMdp - 1, (char) (mdp.charAt(this.tailleMdp - 1) + 1));
			}
			String currentPassword = String.valueOf(mdp);
			String currentHash = "";

			currentHash = simpleHashFunction.hashString(currentPassword, fctHashage); // Une dernière fois pour avoir un hash
			this.fichierEcriture.append(mdp + ", ");
			this.fichierEcriture.append(currentHash);
			this.fichierEcriture.close();
			this.writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Reduction createReduction(int i, String red, String currentPassword){
		if(i%(chainLength+1)<=0){
			switch (red) {
				case "simple":
					return new SimpleReduction(currentPassword.length(), this.domain);
				
				case "hashcode":
					return new HashcodeReduction(currentPassword.length());

				case "markov":
					return new MarkovReduction(this.chainLength, currentPassword.length(), this.domain);
				
				case "color":
					return new ColorReduction(this.chainLength, currentPassword.length(), this.domain);

				case "custom":
					return new CustomReduction(currentPassword.length());
				
				case "advanced":
					return new AdvancedHashReducer(this.chainLength, currentPassword.length());
			
				default:
					return null;
			}
		}
		switch (i) {
			case 1:
				return new SimpleReduction(currentPassword.length(), this.domain);
		
			case 2:
				return new HashcodeReduction(currentPassword.length());

			case 3:
				return new AdvancedHashReducer(this.chainLength, currentPassword.length());
		
			case 4:
				return new ColorReduction(this.chainLength, currentPassword.length(), this.domain);

			case 5:
				return new CustomReduction(currentPassword.length());
		
			case 6:
				return new MarkovReduction(this.chainLength, currentPassword.length(), this.domain);
	
		default:
			return null;
		}
	}

	/**
	 * Point d'entré principal pour la gestion du csv
	 * Permet la gestion avec un argument spécifié (qui est le chemin d'une base de
	 * mdp)
	 */
	public static void main(String[] args) {
		String fctHashage = "SHA-512";
		int tailleMdp   = 3;
		int chainLength = 1;
		String reduction = "simple";
		GestionCSV gestionCSV;

		long startExec=0;
		long endExec=0;
		long execTime=0;

		for (int i=1; i<=chainLength; i++) {
			startExec = System.currentTimeMillis(); // Mesure du temps d'éxécution
			gestionCSV = new GestionCSV("fichierCSV/rainbowTable", fctHashage, Integer.valueOf('a'), Integer.valueOf('z'), tailleMdp, i, reduction);
			endExec = System.currentTimeMillis();
			execTime=endExec-startExec;
			System.out.println("Temps de création de la table "+fctHashage+" = "+execTime+" ms");
			startExec = System.currentTimeMillis();
			gestionCSV.insertCSV();
			endExec = System.currentTimeMillis();
			execTime=endExec-startExec;
			System.out.println("Temps du remplissage de la table "+fctHashage+" taille "+tailleMdp+" longueur "+i+" pour des caractères allants de a à z = "+execTime+" ms\n");
			PerfGenXMLWriter.writeExecTime(execTime, tailleMdp, i, fctHashage, RESULTS_OUTPUT_DIR+".xml");
		}

		startExec = System.currentTimeMillis();
		gestionCSV = new GestionCSV("fichierCSV/table", fctHashage, Integer.valueOf('a'), Integer.valueOf('z'), tailleMdp, 0, reduction);
		endExec = System.currentTimeMillis();
		System.out.println("Temps de création de la table "+fctHashage+" = "+String.valueOf(endExec-startExec)+" ms");
		startExec = System.currentTimeMillis();
		gestionCSV.generationNaive();
		endExec = System.currentTimeMillis();
		execTime=endExec-startExec;
		System.out.println("Temps du remplissage de la table de référence "+fctHashage+" taille "+tailleMdp+" pour des caractères allants de a à z = "+String.valueOf(endExec-startExec)+" ms\n");
		PerfGenXMLWriter.writeExecTime(execTime, tailleMdp, 0, fctHashage, RESULTS_OUTPUT_DIR+".xml");
	}
}
