import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

res_logs = pd.read_csv('resultats_logs.csv', sep=';')

data_taille_mdp_3 = res_logs[res_logs[' tailleMdp'] == 3]
data_taille_mdp_4 = res_logs[res_logs[' tailleMdp'] == 4]

# Question scientifique : Quelle est l'efficacité de la table en fct de son nb de couleurs, de sa taille et de la taille des mdps ?

# ######################################################
# Graphe 1 : nb mdp trouvé en fct de son nb de reduction
grouped_data = res_logs.groupby(' nbReduction')[' nbMdpTrouver'].mean().reset_index()
x = np.array(grouped_data[' nbReduction'])
y = np.array(grouped_data[' nbMdpTrouver'])

plt.figure(figsize=(12, 7))
plt.bar(x, y, color='teal', label='Nb moyen de mdp trouvés')

plt.xlabel('Nb de réductions')
plt.ylabel('Nb moyen de mdp trouvés sur 1000 hashs inconnu')
# plt.title('Efficacité de la table en fct du nb de réductions sur des mdp de taille 3')

plt.xticks(np.arange(min(x), max(x)+1, 1.0), rotation=0)
plt.ylim(0, 400)
plt.tight_layout()

plt.savefig('graphe/nb_mdp_trouves_en_fct_nb_reduction.png')
# plt.show()

# ######################################################
# Graphe 2 : nb mdp trouvé en fct du nb de couleurs et de la taille du mdp
grouped = res_logs.groupby([' tailleMdp', ' nbReduction'])[' nbMdpTrouver'].mean().unstack()
grouped.plot(kind='bar', figsize=(12, 7))

plt.xlabel('Taille du mdp')
plt.ylabel('Nb moyen de mdp trouvés sur 1k hashs')
# plt.title('Efficacité de la table en fct du nb de réductions et de la taille du mdp')

plt.xticks(rotation=0)
plt.legend(title='Réduction')

# plt.xticks(np.arange(min(x), max(x)+1, 1.0), rotation=0)
# plt.ylim(0, 400)
plt.tight_layout()

plt.savefig('graphe/nb_mdp_trouves_en_fct_nb_reduction_et_taille_mdp.png')
# plt.show()
