package cracker;

import reduction.*;
import hashage.*;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors; // Pour la r() ColorReduction
import java.util.Set;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Cette classe représentent notre première méthode de recherche d'un mot de passe.
 */
public class MdpCracker {

  private static Logger      logger = Logger.getLogger(MdpCracker.class.getPackage().getName());
  private String             filenameTable;
  private String             filenameRainbowTable;
  private String             nomFctHashage;
  private int                tailleMdp;
  private int                nbReduction;
  private String             nomFctReduction;
  private SimpleHashFunction simpleHashFunction;
  private Reduction          fctReduction;
  private String             domain;
  private Path               trainingFile;
  private Reduction          fctReductionColor;
  private Reduction          fctReductionMarkov;
  private Reduction          fctReductionHashcode;
  private Reduction          fctReductionSimple;
  private Reduction          fctReductionCustom;
  private Reduction          fctReductionAdvanced;

  /**
   * Constructeur de la classe MdpCracker
   * Initialise une nouvelle instance de MdpCracker avec les paramètres fournies.
   * @param filenameTable        La table avec seulement 1000 hashs inconnus
   * @param filenameRainbowTable La table arc-en-ciel
   * @param nomFctHashage        Le nom de la fonction d'hashage
   * @param tailleMdp            La taille du mot de passe
   * @param nbReduction          Le nombre de réduction
   * @param nomFctReduction      Le nom de la fonction de réduction
   */
  public MdpCracker(String filenameTable, String filenameRainbowTable, String nomFctHashage, int tailleMdp, int nbReduction, String nomFctReduction) {
    this.filenameTable        = filenameTable;
    this.filenameRainbowTable = filenameRainbowTable;
    this.nomFctHashage        = nomFctHashage;
    this.tailleMdp            = tailleMdp;
    this.nbReduction          = nbReduction;
    this.nomFctReduction      = nomFctReduction;
    this.simpleHashFunction   = new SimpleHashFunction();
    // Elements nécessaire pour les fct de reduction
    this.domain       = "azertyuiopqsdfghjklmwxcvbn";
    this.trainingFile = Paths.get("texts", "train.txt");
    // On déclare une fois avant nos fct de réductions
    this.fctReductionColor    = new ColorReduction(nbReduction, tailleMdp, domain);
    this.fctReductionMarkov   = new MarkovReduction(nbReduction, tailleMdp, domain, trainingFile);
    this.fctReductionHashcode = new HashcodeReduction(tailleMdp);
    this.fctReductionSimple   = new SimpleReduction(tailleMdp, domain);
    this.fctReductionCustom   = new CustomReduction(tailleMdp);
    this.fctReductionAdvanced = new AdvancedHashReducer(nbReduction, tailleMdp);

    cracker();
  }

  /**
   * Tente de cracker un certain nombre de hash inconnu (1000 ici).
   * Cette méthode utilise la classe Logger pour enregistrer les informations.
   */
  public void cracker() {
    long nbHashTester    = 0;
    long nbMdpTrouver    = 0;
    long debutTimer      = System.currentTimeMillis();
    long controlTimer    = debutTimer;
    Map<Integer, Integer> mapCptCouleurPos = new HashMap<>();

    // ///////////////////////////////////////////
    // Début chargemet table mdp et hash, de nos 1000 hash d'entrée
    try (BufferedReader readerTable = new BufferedReader(new FileReader(filenameTable))) {
      String lineTable;

      while ((lineTable = readerTable.readLine()) != null) {
        String hashFound = "";
        String mdpFound  = "";

        String[] partsTable  = lineTable.split(", ");
        String   hashInconnu = partsTable[1];
        nbHashTester += 1;
        int posCouleur = 0;

        // ///////////////////////////////////////////
        // Début chargement RainbowTable
        String mdpCourant = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filenameRainbowTable))) {
          String line;
          while ((line = reader.readLine()) != null) {
            String[] parts = line.split(", ");

            // On test directement pour chaque mdp
            String currentPassword = parts[0];
            String currentHash     = "";
            mdpFound               = currentPassword;

            if (nbReduction == 1) { // Cas 1 seul réduction (fichier avec fctHashage spécifique)
              mapCptCouleurPos.put(1, mapCptCouleurPos.getOrDefault(1, 0) + 1);
              currentHash     = this.simpleHashFunction.hashString(currentPassword, nomFctHashage);
              switch (nomFctReduction) {
                case "color":
                  currentPassword = this.fctReductionColor.reduce(currentHash);
                  break;
                case "markov":
                  currentPassword = this.fctReductionMarkov.reduce(currentHash);
                  break;
                case "hashcode":
                  currentPassword = this.fctReductionHashcode.reduce(currentHash);
                  break;
                case "simple":
                  currentPassword = this.fctReductionSimple.reduce(currentHash);
                  break;
                case "custom":
                  currentPassword = this.fctReductionCustom.reduce(currentHash);
                  break;
                case "advanced":
                  currentPassword = this.fctReductionAdvanced.reduce(currentHash);
                  break;
                default: // On ne souhaite pas entrer dans default, on veut pouvoir tout gérer
                  currentPassword = this.fctReductionCustom.reduce(currentHash);
                  logger.severe("ATTENTION LA METHODE DE REDUCTION UTILISER EST CELLE DE DEFAULT. L'ENTRER EST SOIT PAS SPECIFIER, OU BIEN PRESENCE D'UNE ERREUR!");
                  break;
              }
              mdpFound        = currentPassword;
              if (hashInconnu.equals(currentHash)) { // A chaque fois qu'on fait un hash, on test ! Et quand égale c' Bingo
                hashFound = currentHash;
                break; // On sort, pas besoin d'aller jusqu'à la fin de la chaine
              }
            } else { // DEBUT cas nbReduction >= 2
              for (int l=1; l<=nbReduction; l++) { // Hash & Réduit un certain nb de fois (couleurs)
                currentHash     = this.simpleHashFunction.hashString(currentPassword, nomFctHashage);
                switch (l) {
                  case 1:
                    this.fctReduction = this.fctReductionSimple;
                    posCouleur = l;
                    break;
                  case 2:
                    this.fctReduction = this.fctReductionHashcode;
                    posCouleur = l;
                    break;
                  case 3:
                    this.fctReduction = this.fctReductionAdvanced;
                    posCouleur = l;
                    break;
                  case 4:
                    this.fctReduction = this.fctReductionColor;
                    posCouleur = l;
                    break;
                  case 5:
                    this.fctReduction = this.fctReductionCustom;
                    posCouleur = l;
                    break;
                  case 6:
                    this.fctReduction = this.fctReductionMarkov;
                    posCouleur = l;
                    break;
                  default: // On ne souhaite pas entrer dans default, on veut pouvoir tout gérer
                    this.fctReduction = this.fctReductionColor;
                    logger.severe("ATTENTION LA METHODE DE REDUCTION UTILISER DANS LA FONCTION DE CRACKAGE EST CELLE DE DEFAULT. L'ENTRER EST SOIT PAS SPECIFIER, OU BIEN PRESENCE D'UNE ERREUR!");
                    break;
                }
                currentPassword = this.fctReduction.reduce(currentHash);
                mdpFound        = currentPassword;
                if (hashInconnu.equals(currentHash)) { // A chaque fois qu'on fait un hash, on test ! Et quand égale c' Bingo
                  hashFound = currentHash;
                  break; // On sort, pas besoin d'aller jusqu'à la fin de la chaine
                }
              } // Fin for nbReduction
            } // FIN cas nbReduction >= 2


      			currentHash = this.simpleHashFunction.hashString(currentPassword, nomFctHashage); // Une dernière fois pour avoir un hash
            if (hashInconnu.equals(currentHash)) {
              hashFound = currentHash;
            }

            if (!hashFound.equals("")) { // Si hash trouver, on ajoute +1 au cpt
              nbMdpTrouver  += 1;
              mapCptCouleurPos.put(posCouleur, mapCptCouleurPos.getOrDefault(posCouleur, 0) + 1);
              break; // Car donne 2 fois le même affichage
            }
            hashFound = "";
          }

        } catch (IOException e) {
          //e.printStackTrace(); // avec logger, ne pas utiliser printStackTrace() !
          logger.severe("ERREUR survenue : " + e);
        }
        // Fin chargement RainbowTable
        // ///////////////////////////////////////////


      }
    } catch (IOException e) {
      logger.severe("ERREUR survenue : " + e);
    }
    // Fin chargemet table mdp et hash
    // ///////////////////////////////////////////

    long finTimer          = System.currentTimeMillis();
    long tempsMisEnMs      = finTimer - debutTimer;
    long tempsMisEnMin     = tempsMisEnMs / (1000*60);

    // Calcul moyenne pondérée des couleurs en fct de la fréquence d'app
    double sommePoids        = 0;
    double sommePoidsValeurs = 0;
    for (Map.Entry<Integer, Integer> entrer : mapCptCouleurPos.entrySet()) {
      int couleur = entrer.getKey();
      int poids   = entrer.getValue();
      sommePoids        += poids;
      sommePoidsValeurs += couleur * poids;
    }
    double posCouleurMoy = 0;
    if (nbMdpTrouver > 0) {
      posCouleurMoy = (double) sommePoidsValeurs / sommePoids;
    }

    // Calcul écart-type pondérée
    sommePoids        = 0;
    double sommePoidsEcartCarre = 0;
    for (Map.Entry<Integer, Integer> entrer : mapCptCouleurPos.entrySet()) {
      int couleur  = entrer.getKey();
      int poids    = entrer.getValue();
      double ecart = couleur - posCouleurMoy;
      sommePoids           += poids;
      sommePoidsEcartCarre += poids * ecart * ecart;
    }
    // Ajout de garde-fou, notamment pour mdp de taille 5
    if (sommePoids <= 0) {
      sommePoids = 1;
    }
    if (Double.isNaN(sommePoidsEcartCarre) || Double.isInfinite(sommePoidsEcartCarre) || sommePoidsEcartCarre < 0) {
      sommePoidsEcartCarre = 0;
    }
    double posCouleurEcartType = Math.sqrt(sommePoidsEcartCarre / sommePoids);

    String logMsg = String.format(
      "nbHashTester=%d; nbMdpTrouver=%d; tempsMisEnMs=%d; tempsMisEnMin=%d; tailleMdp=%d; nomFctHashage=%s; nbReduction=%d; nomFctReduction=%s; posCouleurMoy=%.3f; posCouleurEcartType=%.3f",
      nbHashTester, nbMdpTrouver, tempsMisEnMs, tempsMisEnMin, tailleMdp, nomFctHashage, nbReduction, nomFctReduction, posCouleurMoy, posCouleurEcartType
    );
    logger.info(logMsg);
  }


  /**
   * Méthode principale pour lancer le test.
   * Elle initialise l'objet MdpCracker avec les paramètres fournies.
   */
  public static void main(String[] args) {
    String filenameTable        = args[0];
    String filenameRainbowTable = args[1];
    int    tailleMdp            = Integer.parseInt(args[2]);
    String nomFctHashage        = args[3];
    int    nbReduction          = Integer.parseInt(args[4]); // nb de couleur
    String nomFctReduction      = args[5];

    String[] partsFilenameRainbowTable      = filenameRainbowTable.split("/");           // Car y'a tout le chemin qui est entrer
    String[] partsPartsFilenameRainbowTable = partsFilenameRainbowTable[2].split("\\."); // Pour avoir que son nom, et non l'extension. Faut mettre \\ car . est un caract spéciale en regex
    try {
      String cheminLogFile    = "automatisation/logs/resultat_" + partsPartsFilenameRainbowTable[0] + ".csv";
      File logFile = new File(cheminLogFile);
      FileHandler fileHandler;
      if (logFile.exists() && !logFile.isDirectory()) { // Si logFile existe, ajoute à la suite du fichier
        fileHandler = new FileHandler(cheminLogFile, true);
      } else { // sinon créer un nouveau fichier
        fileHandler = new FileHandler(cheminLogFile);
      }
      fileHandler.setFormatter(new SimpleFormatter());
      logger.addHandler(fileHandler);
      MdpCracker mdpCracker = new MdpCracker(filenameTable, filenameRainbowTable, nomFctHashage, tailleMdp, nbReduction, nomFctReduction);
      fileHandler.close();
    } catch (IOException e) {
      logger.severe("Erreur lors de la configuration du logger");
    }

  }

}
