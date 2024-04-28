import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import numpy as np


def translate_collision_test_data_to_dict(filename: str) -> dict[int, list[tuple[str, float]]]:
    res = {}
    tree = ET.parse(filename)
    root = tree.getroot()
    for test in root:
        data = []
        for test_data in test:
            data.append((test_data.attrib["algorithm"], float(test_data.text)))
        res[int(test.attrib["passwordSize"])] = data
    return res


def plot_graph(data: dict, filename: str, title: str, xlabel: str, ylabel: str, percentage: bool, xlim: int, algo: str):
    data_array = np.array([(x, label, y) for x, data_list in data.items() for label, y in data_list])

    unique_labels = np.unique(data_array[:, 1])

    for label in unique_labels:
        label_data = data_array[data_array[:, 1] == label]
        plt.plot(label_data[:, 0].astype(int), label_data[:, 2].astype(float), marker='.', label=label)

    plt.suptitle(title)
    plt.title(f"([{algo}] - Sur 100 000 mots de passe)")
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.legend()
    plt.grid(axis='both')
    if percentage:
        plt.xlim(min(data.keys()), xlim)
        plt.ylim(0, 100)
    else:
        plt.xlim(min(data.keys()), max(data.keys()))  # pour
    plt.savefig(f"../output_data/graphs/{filename}.png", dpi=300)
    plt.show()


def generate_collisions_test_data_graph(filename: str, algo: str) -> None:
    data = translate_collision_test_data_to_dict(f"../output_data/testing/reduction/collisions/{filename}_collisions.xml")
    title = "Pourcentage de collisions en fonction de la taille du mot de passe"
    xlabel = "Taille du mot de passe"
    ylabel = "Pourcentage de collisions"
    plot_graph(data, f"{filename}_collisions", title, xlabel, ylabel, True, 10, algo)


def generate_collisions_test_exec_time_graph(filename: str, algo: str) -> None:
    data = translate_collision_test_data_to_dict(f"../output_data/testing/reduction/collisions/{filename}_execTime.xml")
    title = "Temps d'exécution en fonction de la taille du mot de passe"
    xlabel = "Taille du mot de passe"
    ylabel = "Temps d'exécution (ms)"
    plot_graph(data, f"{filename}_execTime", title, xlabel, ylabel, False, 0, algo)


def generate_collision_test_graphs(filename: str, algo: str):
    generate_collisions_test_data_graph(filename, algo)
    generate_collisions_test_exec_time_graph(filename, algo)

#def generate_perf_generation_time(filename: str):
#    data = translate_collision_test_data_to_dict(f"../output_data/testing/generation/{filename}")
#    title = "Temps de génération des tables"
#    xlabel = "Taille du mot de passe"
#    ylabel = "Nombre de réductions"
#    zlabel = "Algo utilisé"
#    plot_graph(data, f"{filename}_collisions", title, xlabel, ylabel, True, 10, algo)


if __name__ == '__main__':
    generate_collision_test_graphs("SHA-1_6algorithms", "SHA-1")
    generate_collision_test_graphs("SHA-256_6algorithms", "SHA-256")
    generate_collision_test_graphs("SHA-512_6algorithms", "SHA-512")
    generate_collision_test_graphs("MD2_6algorithms", "MD2")
    generate_collision_test_graphs("MD5_6algorithms", "MD5")
    generate_perf_generation_time("perf.xml")
