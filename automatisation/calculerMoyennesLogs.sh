#!/usr/bin/bash

dossier_logs="logs";
fichier_resultats="resultats_logs.csv";

echo "nbHashTester; nbMdpTrouver; tempsMisEnMs; tempsMisEnMin; tailleMdp; nomFctHashage; nbReduction; nomFctReduction; posCouleurMoy; posCouleurEcartType; tailleFichierOctets;" > "$fichier_resultats" # Supprimer le contenu

echo "DEBUT du calcul de la moyenne des logs de chaque fichier csv respectifs"
for fichier_log in "$dossier_logs"/*.csv; do
  # RT octets
  log_nom_fichier=$(basename "$fichier_log")
  chemin_rainbowTable="./../fichierCSV/rainbowTable/${log_nom_fichier#resultat_}";
  tailleFichierOctets=$(stat -c %s $chemin_rainbowTable);
  # Valeurs où nous souhaitons pas la moyenne
  nbHashTester=0;
  tailleMdp=0;
  nomFctHashage="";
  nbReduction=0;
  nomFctReduction="";
  # Valeurs où nous souhaitons la moyenne
  total_nb_mdp_trouver=0;
  total_temps_mis_en_ms=0;
  total_temps_mis_en_min=0;
  total_pos_couleur_moy=0;
  total_pos_couleur_ecart_type=0;

  cpt_nb_logs=0;
  posCouleurMoyList=""

  while IFS=': ' read -r cle val; do
    if [[ $cle == "INFOS" ]]; then
      IFS='; ' read -r nbHashTester nbMdpTrouver tempsMisEnMs tempsMisEnMin tailleMdp nomFctHashage nbReduction nomFctReduction posCouleurMoy posCouleurEcartType <<< "$val"
      # Extraction infos
      nbHashTester=${nbHashTester#*=} # partie droite nbHashTester#*= -> supp tout caractère suivi d'un =, il nous reste donc que la valeur
      nbMdpTrouver=${nbMdpTrouver#*=}
      tempsMisEnMs=${tempsMisEnMs#*=}
      tempsMisEnMin=${tempsMisEnMin#*=}
      tailleMdp=${tailleMdp#*=}
      nomFctHashage=${nomFctHashage#*=}
      nbReduction=${nbReduction#*=}
      nomFctReduction=${nomFctReduction#*=}
      posCouleurMoy=${posCouleurMoy#*=}
      posCouleurEcartType=${posCouleurEcartType#*=}

      # Accumule seulement les infos pertinentes
      let total_nb_mdp_trouver+=nbMdpTrouver
      let total_temps_mis_en_ms+=tempsMisEnMs
      let total_temps_mis_en_min+=tempsMisEnMin
      posCouleurMoyFormat=$(echo "$posCouleurMoy" | sed 's/,/./g') # faut transformer les , en . pour le calcul
      total_pos_couleur_moy=$(echo "$total_pos_couleur_moy + $posCouleurMoyFormat" | bc)
      posCouleurEcartTypeFormat=$(echo "$posCouleurEcartType" | sed 's/,/./g')
      total_pos_couleur_ecart_type=$(echo "$total_pos_couleur_ecart_type + $posCouleurEcartTypeFormat" | bc)

      let cpt_nb_logs+=1
    fi
  done < "$fichier_log"

  if [ $cpt_nb_logs -ne 0 ]; then
    moy_nb_mdp_trouver=$(echo "$total_nb_mdp_trouver / $cpt_nb_logs" | bc)
    moy_temps_mis_en_ms=$(echo "$total_temps_mis_en_ms / $cpt_nb_logs" | bc)
    moy_temps_mis_en_min=$(echo "$total_temps_mis_en_min / $cpt_nb_logs" | bc)
    moy_pos_couleur_moy=$(echo "scale=3; $total_pos_couleur_moy / $cpt_nb_logs" | bc)
    moy_pos_couleur_ecart_type=$(echo "scale=3; $total_pos_couleur_ecart_type / $cpt_nb_logs" | bc)

    # bc supprime le 0 des entiers, on y remédie
    if [[ $moy_pos_couleur_moy == .* ]]; then
      moy_pos_couleur_moy="0$moy_pos_couleur_moy"
    fi
    if [[ $moy_pos_couleur_ecart_type == .* ]]; then
      moy_pos_couleur_ecart_type="0$moy_pos_couleur_ecart_type"
    fi

    echo "$nbHashTester; $moy_nb_mdp_trouver; $moy_temps_mis_en_ms; $moy_temps_mis_en_min; $tailleMdp; $nomFctHashage; $nbReduction; $nomFctReduction; $moy_pos_couleur_moy; $moy_pos_couleur_ecart_type; $tailleFichierOctets" >> "$fichier_resultats"
  else
    echo "AUCUN DONNE N'A ETE TRAITER POUR LE CALCUL DES MOYENNES DES LOGS RESPECTIFS"
  fi

done




echo "FIN du calcul de la moyenne des logs de chaque fichier csv respectifs. Résultat dans le fichier $fichier_resultats"
