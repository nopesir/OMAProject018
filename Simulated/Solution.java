package Simulated;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import Main.Data;
import Main.OMA;

public class Solution {

	private Query[] array_query;
	private int[] indices_chosen;
	private int gain;
	private int cost;
	private int memory;
	private Index[] array_index;

	public Solution(Query[] q, int[] i, int g, int c, int m, Index[] in) {
		array_query = q.clone();
		indices_chosen = i.clone();
		gain = g;
		cost = c;
		memory = m;
		array_index = in.clone();
	}

	public void calculate(int time, boolean toSave, String path, Configuration [] array_configuration) throws InterruptedException {
		Random random = new Random();
		Data data = Data.getInstance();
		// best solution found so far
		int very_best_gain = gain;
		int[] very_best_indexes = indices_chosen.clone();
		int very_cost = cost;
		int very_memory = memory;
		Query[] very_query = new Query[data.getN_QUERIES()];
		for (int i = 0; i < data.getN_QUERIES(); i++)
			very_query[i] = new Query(array_query[i].get_array_configuration_matrix(), array_query[i].get_gain(),
					array_query[i].get_array_gain(), array_query[i].get_configuration());

		// Parameters for simulated
		double temp = 100000;
		double alpha = 0.9999993;

		if (time > 40000 && time <= 80000)
			alpha = 0.9999995;
		else if (time > 80000 && time <= 180000)
			alpha = 0.9999999;
		else if (time > 180000 && time <= 300000)
			alpha = 0.99999993;
		else if (time > 300000)
			alpha = 0.99999997;

		// temporary variables
		int[] tempo = null;
		Index[] i_matrix;

		// Tabu List
		ArrayList<Query> TabuList = new ArrayList<Query>();

		// beginning of simulated, check if the timeout is reached
		while (!Data.timeout) {

			tempo = indices_chosen.clone();

			// chose a random query q and change the configuration to a random one h
			int q = random.nextInt(data.getN_QUERIES());
			int h = random.nextInt((array_query[q].get_size()));
			// if the query is in the tabu list , pick another query
			while (TabuList.contains(array_query[q])) {
				q = random.nextInt(data.getN_QUERIES());
				h = random.nextInt((array_query[q].get_size()));
			}

			// set a backup in case we have to restore the previous solution
			Configuration backup = array_query[q].get_configuration();
			int gain_backup = array_query[q].get_gain();

			// update the temporary indexes chosen array with new values
			if (array_query[q].get_configuration() == null) {
				array_query[q].set_configuration(array_query[q].get_conf_matrix(h));
				array_query[q].set_gain(array_query[q].get_gain_matrix(h));
			} else {
				i_matrix = array_query[q].get_configuration().get_indext();
				for (int j = 0; j < data.getN_INDEXES(); j++) {
					if (i_matrix[j] != null)
						tempo[j] -= 1;
				}
			}

			array_query[q].set_configuration(array_query[q].get_conf_matrix(h));
			array_query[q].set_gain(array_query[q].get_gain_matrix(h));
			if (array_query[q].get_configuration() != null) {
				i_matrix = array_query[q].get_configuration().get_indext();
				for (int j = 0; j < data.getN_INDEXES(); j++) {
					if (i_matrix[j] != null)
						tempo[j] += 1;
				}
			}

			// calculate the temporary gain cost and memory
			int temp_gain = 0;
			int temp_memory = 0;
			int temp_cost = 0;

			for (int i = 0; i < data.getN_INDEXES(); i++) {
				if (tempo[i] > 0) {
					temp_cost += array_index[i].get_cost();
					temp_memory += array_index[i].get_memory();
				}
			}

			for (int i = 0; i < data.getN_QUERIES(); i++) {
				temp_gain += array_query[i].get_gain();
			}

			temp_gain -= temp_cost;

			// Checking if the solution is feasible otherwise jump to else
			if (temp_memory <= data.getMEMORY()) {
				double p = Math.pow(Math.E, (double) ((temp_gain - gain) / temp));
				// checking if we accept or not the current solution
				if (temp_gain > gain || p > Math.random()) {

					// update if the temporary gain is greater than the best gain found
					if (very_best_gain < temp_gain) {
						very_best_gain = temp_gain;
						very_best_indexes = tempo.clone();
						very_cost = cost;
						very_memory = memory;

						for (int i = 0; i < data.getN_QUERIES(); i++) {
							very_query[i].set_configuration(array_query[i].get_configuration());
						}

						if (toSave) {

							for (int i = 0; i < data.getN_INDEXES(); i++) {
								if (very_best_indexes[i] > 0) {
									Data.indexesSelected[i] = 1;
								}

							}

							for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {
								for (int i = 0; i < data.getN_QUERIES(); i++) {

									if (array_configuration[j] == very_query[i].get_configuration()) {
										Data.configurationsQueriesSelected[j][i] = 1;

									} else
										Data.configurationsQueriesSelected[j][i] = 0;
								}
							}

							OMA.saveFile(path, data);

							TimeUnit.MILLISECONDS.sleep(300);

						}

						// Increment the array of indexes built for every index belonging to the conf
					}

					// add query modified to the tabu list
					TabuList.add(array_query[q]);

					// update parameters
					gain = temp_gain;
					cost = temp_cost;
					memory = temp_memory;
					indices_chosen = tempo.clone();

					int nTabu = 1;

					if ((data.getN_QUERIES() >= 10) && (data.getN_QUERIES() < 100))
						nTabu = 5;
					else if (data.getN_QUERIES() >= 100)
						nTabu = 10;

					// remove an item from tabu list
					if (TabuList.size() >= nTabu) {
						TabuList.remove(0);
					}
					// update temp
					temp = temp * alpha;

				}
				// jump here if the solution is feasible but not accepted; restore previous
				// solution
				else {
					temp = temp * alpha;
					array_query[q].set_configuration(backup);
					array_query[q].set_gain(gain_backup);

				}

			}

			// jump here if solution is infeasible; restore previous solution
			else {

				array_query[q].set_configuration(backup);
				array_query[q].set_gain(gain_backup);
				tempo = indices_chosen.clone();
				temp = temp * alpha;

			}

		}

		// check the best solution
		if (gain < very_best_gain) {
			gain = very_best_gain;
			indices_chosen = very_best_indexes;
			cost = very_cost;
			memory = very_memory;
			array_query = very_query;

		}

	}

	public Query[] get_array_query() {
		return array_query;
	}

	public int[] get_indices_chosen() {
		return indices_chosen;
	}

	public int get_gain() {
		return gain;
	}

}
