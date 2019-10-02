package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Simulated.*;
import Greedy.*;

public class OMA {

	public static void main(String[] args) {
		String name;
		int time = 30 * 60 * 1000; // 30 minutes of default time

		// Check args
		if (args.length == 3) {
			if (args[1].toLowerCase().equals("-t")) {
				try {
					time = Integer.parseInt(args[2]) * 1000;
				} catch (NumberFormatException e) {
					System.out.println("err: ODBDPsolver_OMAAL_group03.exe <instancefilename> -t <timelimit>");
					return;
				}
			} else {
				System.out.println("err: ODBDPsolver_OMAAL_group03.exe <instancefilename> -t <timelimit>");
				return;
			}
			name = args[0];
		} else {
			System.out.println("err: ODBDPsolver_OMAAL_group03.exe <instancefilename> -t <timelimit>");
			return;
		}

		Timer t = new Timer();

		final int final_time = time;

		t.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				// set timeout to true after timer
				Data.timeout = true;
				System.out.println("END OF TIME (" + final_time / 1000 + "s)");
				t.cancel();
				t.purge();
			}
		}, final_time);

		// instantiating Data class

		Data.getInstance();

		Data data;

		try {
			data = Data.getInstance(name);
		} catch (FileNotFoundException e) {
			t.cancel();
			t.purge();
			System.out.println("err: File " + args[0] + " not found. Aborting...");
			return;
		}

		// creating parameters of backup to restore data after a run
		int[] indexesBackup = data.getIndexesSelected().clone();
		boolean[] configurationsbackup = data.get_Configuration_selected().clone();
		int[][] configurationsQuerieBackup = Data.getConfigurationsQueriesSelected().clone();
		int gainBackup = data.get_GainSelected();

		GreedyGain gg = new GreedyGain();
		gg.implement(2);

		int best_gain = 0;
		int cost = 0;
		int memory = 0;
		int[] indices_chosen = new int[data.getN_INDEXES()];
		// array of indexes
		Index[] array_index = new Index[data.getN_INDEXES()];

		// array of indexes cost
		int index_cost[] = data.getINDEXES_FIXED_COST();

		// array of indexes memory cost
		int memory_cost[] = data.getINDEXES_MEMORY_OCCUPATION();

		// matrix of configuration and query gain
		int[][] query_gain = data.getCONFIGURATIONS_QUERIES_GAIN();

		// insert in the array of indexes the object Index that has its own cost and
		// memory cost
		for (int i = 0; i < data.getN_INDEXES(); i++) {
			array_index[i] = new Index(index_cost[i], memory_cost[i]);
		}

		// matrix of configuration and indexes
		int[][] c_i_matrix = data.getCONFIGURATIONS_INDEXES_MATRIX();

		// array of Configurations
		Configuration[] array_configuration = new Configuration[data.getN_CONFIGURATIONS() + 1];

		// insert in the array of configurations the object Configuration that has its
		// own array of indexes
		for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {
			array_configuration[j] = new Configuration(data.getN_INDEXES());

			for (int i = 0; i < data.getN_INDEXES(); i++) {
				if (c_i_matrix[j][i] != 0)
					array_configuration[j].insert_index(i, array_index[i]);

			}
		}

		// array of gain different to 0 for each query
		int[] count = new int[data.getN_QUERIES()];

		// insert in the array count how many Configuration for that query have gain
		// different from 0
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {
				if (data.getCONFIGURATIONS_QUERIES_GAIN()[j][i] > 0)
					count[i]++;

			}

		}

		// array of Query 1 different per solution

		Query[] array_query0 = new Query[data.getN_QUERIES()];
		Query[] array_query1 = new Query[data.getN_QUERIES()];
		Query[] array_query2 = new Query[data.getN_QUERIES()];
		Query[] array_query3 = new Query[data.getN_QUERIES()];

		// insert in the array of Querys the objects Query with their own Configuration
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			int counter = 0;
			Configuration[] configuratino_query = new Configuration[count[i] + 1];
			for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {
				if (data.getCONFIGURATIONS_QUERIES_GAIN()[j][i] > 0) {
					configuratino_query[counter] = array_configuration[j];
					counter++;

				}
			}

			array_query0[i] = new Query(configuratino_query, count[i]);
			array_query1[i] = new Query(configuratino_query, count[i]);
			array_query2[i] = new Query(configuratino_query, count[i]);
			array_query3[i] = new Query(configuratino_query, count[i]);

		}

		// insert for each object Query in the array of Query the gain for each
		// configuration
		for (int j = 0; j < data.getN_QUERIES(); j++) {
			int counter = 0;

			for (int i = 0; i < data.getN_CONFIGURATIONS(); i++) {

				if (data.getCONFIGURATIONS_QUERIES_GAIN()[i][j] > 0) {

					array_query0[j].set_gain(counter, query_gain[i][j]);
					array_query1[j].set_gain(counter, query_gain[i][j]);
					array_query2[j].set_gain(counter, query_gain[i][j]);
					array_query3[j].set_gain(counter, query_gain[i][j]);

					counter++;
				}
			}

		}

		// create solution 2
		Solution solution2 = createSolution(array_query2, array_configuration, array_index, data);
		// reset data for run gg 1
		Data.indexesSelected = indexesBackup.clone();
		Data.configurationsQueriesSelected = configurationsQuerieBackup.clone();
		Data.configurationsSelected = configurationsbackup.clone();
		Data.gainSelected = gainBackup;

		// run gg2
		gg.implement(2);
		// create solution1
		Solution solution1 = createSolution(array_query1, array_configuration, array_index, data);
		// reset data to run gg1
		Data.indexesSelected = indexesBackup.clone();
		Data.configurationsQueriesSelected = configurationsQuerieBackup.clone();
		Data.configurationsSelected = configurationsbackup.clone();
		Data.gainSelected = gainBackup;
		// run gg1
		gg.implement(1);

		Solution solution0 = createSolution(array_query0, array_configuration, array_index, data);

		Solution solution3 = new Solution(array_query3, indices_chosen, best_gain, cost, memory, array_index);

		System.out.println("Greedy0  gain: " + solution3.get_gain());
		System.out.println("Greedy1  gain: " + solution0.get_gain());
		System.out.println("Greedy2A gain: " + solution1.get_gain());
		System.out.println("Greedy2B gain: " + solution2.get_gain());
		System.out.println("----------------------");

		String path = name + "_OMAAL_group03.sol";

		for (int i = 0; i < data.getN_INDEXES(); i++) {
			if (solution1.get_indices_chosen()[i] > 0) {
				Data.indexesSelected[i] = 1;
			}

		}

		for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {
			for (int i = 0; i < data.getN_QUERIES(); i++) {

				if (array_configuration[j] == solution1.get_array_query()[i].get_configuration()) {
					Data.configurationsQueriesSelected[j][i] = 1;

				} else
					Data.configurationsQueriesSelected[j][i] = 0;
			}
		}

		saveFile(path, data);

		// create array of solutions to run thread
		Solution[] array_solutions = new Solution[4];
		array_solutions[0] = solution0;
		array_solutions[1] = solution1;
		array_solutions[2] = solution2;
		array_solutions[3] = solution3;

		// thread executors
		ExecutorService executor = Executors.newCachedThreadPool();

		// execute threads
		for (int i = 0; i < 3; i++) {
			executor.execute(new SimulatedThread(array_solutions[i], final_time, false, path, array_configuration));
		}

		executor.execute(new SimulatedThread(array_solutions[3], final_time, true, path, array_configuration));

		// wait for the end
		executor.shutdown();
		try {
			executor.awaitTermination(final_time, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}

		System.out.println("\nSolutions found:" + "\n 0: " + solution3.get_gain() + "\n 1: " + solution0.get_gain()
				+ "\n2A: " + solution1.get_gain() + "\n2B: " + solution2.get_gain());

		// take best solution of 3
		Solution solution = solution0;

		if (solution.get_gain() < solution1.get_gain())
			solution = solution1;

		if (solution.get_gain() < solution2.get_gain())
			solution = solution2;

		if (solution.get_gain() < solution3.get_gain())
			solution = solution3;

		System.out.println("|-> Best solution's gain: " + solution.get_gain());

		for (int i = 0; i < data.getN_INDEXES(); i++) {
			if (solution.get_indices_chosen()[i] > 0) {
				Data.indexesSelected[i] = 1;
			}

		}

		for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {
			for (int i = 0; i < data.getN_QUERIES(); i++) {

				if (array_configuration[j] == solution.get_array_query()[i].get_configuration()) {
					Data.configurationsQueriesSelected[j][i] = 1;

				} else
					Data.configurationsQueriesSelected[j][i] = 0;
			}
		}

		saveFile(path, Data.getInstance());

		t.cancel();
		t.purge();

	}

	public static void saveFile(String path, Data data) {

		// Configuration[] array_configuration = new
		// Configuration[data.getN_CONFIGURATIONS() + 1];

		File matrixFile = new File(path);

		try {
			matrixFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {

			PrintWriter printMatrix = new PrintWriter(matrixFile);
			for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {
				String formattedString = Arrays.toString(Data.getConfigurationsQueriesSelected()[j]).replace(",", "") // remove
																														// commas
						.replace("[", "") // remove the right bracket
						.replace("]", "") // remove the left bracket
						.trim(); // remove trailing spaces from partially initialized arrays
				printMatrix.println(formattedString);

			}

			printMatrix.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private static Solution createSolution(Query[] array_query, Configuration[] array_configuration,
			Index[] array_index, Data data) {
		int best_gain = 0;
		int cost = 0;
		int memory = 0;
		int[] indices_chosen = new int[data.getN_INDEXES()];

		// insert for each Query in the array of Query the configuration chosen by the
		// greedy algorithm
		for (int j = 0; j < data.getN_CONFIGURATIONS(); j++) {

			for (int i = 0; i < data.getN_QUERIES(); i++) {

				if (Data.getConfigurationsQueriesSelected()[j][i] == 1) {
					array_query[i].set_configuration(array_configuration[j]);
					array_query[i].set_gain(data.getCONFIGURATIONS_QUERIES_GAIN()[j][i]);
				}
			}
		}

		// increment the array of indexes build for every indexes belonging to the
		// configuration chose for every Query
		for (int i = 0; i < data.getN_QUERIES(); i++) {

			if (array_query[i].get_configuration() != null) {
				Index[] i_matrix = array_query[i].get_configuration().get_indext();

				for (int j = 0; j < data.getN_INDEXES(); j++) {

					if (i_matrix[j] != null)
						indices_chosen[j] += 1;
				}
			}
		}

		// calculate the cost and the memory to build every index chosen
		for (int i = 0; i < data.getN_INDEXES(); i++) {

			if (indices_chosen[i] > 0) {
				cost += array_index[i].get_cost();
				memory += array_index[i].get_memory();
			}
		}

		// calculate the total gain
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			best_gain += array_query[i].get_gain();
		}

		// calculate the effective gain
		best_gain -= cost;

		Solution solution = new Solution(array_query, indices_chosen, best_gain, cost, memory, array_index);

		return solution;

	}

}
