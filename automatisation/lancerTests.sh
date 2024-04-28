#!/usr/bin/bash

# Se mettre à la racine du projet (plus simple pour la compréhension et l'execution)
cd ..

echantillonTests="automatisation/echantillon_1000_tests.csv"

# On boucle pour nos tests
for tailleMdp in {3..4}; do
  for fctHash in "MD2" "MD5" "SHA-1" "SHA-256" "SHA-512"; do
    for nbReduction in {1..5}; do
      for i in {1..1}; do # Nombre de fois que nous souhaitons avoir des résultats pour des mêmes paramètres (sauf les 1000 hashs tester)
        ####################################
        # Nos paramètres
        filenameTable="fichierCSV/table/mdp_taille_${tailleMdp}_${fctHash}_0_reductions.csv"
        shuf "$filenameTable" | head -n 1000 > "$echantillonTests" # On prend que 1000 éléments pour chaque test, pour rester cohérent avec les résultats
        fctHashage="$fctHash"
        if [ $nbReduction -eq 1 ]; then # Quand c' 1 réduction, on a des fichiers spéficique
          # for fctReduction in "simple" "hashcode" "advanced" "color" "custom" "markov"; do # dès que y'a markov, remettre ça
          for fctReduction in "simple" "hashcode" "advanced" "color" "custom"; do
            # Nos paramètres
            filenameRainbowTables="fichierCSV/rainbowTable/mdp_taille_${tailleMdp}_${fctHash}_${nbReduction}_reduction_${fctReduction}.csv"
            # On execute
            echo "DEBUT TEST POUR -> mdp taille ${tailleMdp}, ${fctHash}, nb reduction ${nbReduction}, fctReduction ${fctReduction}"
            java -cp build cracker.MdpCracker "$echantillonTests" "$filenameRainbowTables" "$tailleMdp" "$fctHashage" "$nbReduction" "$fctReduction"
            echo "TEST FINIE POUR -> mdp taille ${tailleMdp}, ${fctHash}, nb reduction ${nbReduction}, fctReduction ${fctReduction}. RES dans logs"
          done
        else
          # Nos paramètres
          filenameRainbowTables="fichierCSV/rainbowTable/mdp_taille_${tailleMdp}_${fctHash}_${nbReduction}_reductions.csv"
          # On execute
          echo "DEBUT TEST POUR -> mdp taille ${tailleMdp}, ${fctHash}, nb reduction ${nbReduction}"
          java -cp build cracker.MdpCracker "$echantillonTests" "$filenameRainbowTables" "$tailleMdp" "$fctHashage" "$nbReduction" "NO_IMPORTANCE"
          echo "TEST FINIE POUR -> mdp taille ${tailleMdp}, ${fctHash}, nb reduction ${nbReduction}. RES dans logs"
        fi
        ####################################
      done
    done
  done
done

echo "TOUT LES TESTS ONT ETE FAITS AVEC SUCCES !!!"
