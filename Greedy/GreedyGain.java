package Greedy;

import java.util.Arrays;
import Main.*;

public class GreedyGain {
	Data data = Data.getInstance();
	private int[] tempSelectedIndexes = new int[data.getN_INDEXES()];
	private int[] selectedQueries = new int[data.getN_QUERIES()]; // which configuration for each query

	// public int positionj = 0;
	// type 0,1,2
	public void implement(int type) {

		Configurations configuration = new Configurations();
		// initialize GainPosition objects
		GainPosition[] gp_array = new GainPosition[data.getN_CONFIGURATIONS()];

		for (int i = 0; i < data.getN_INDEXES(); i++) {
			tempSelectedIndexes[i] = 0;
		}
		for (int i = 0; i < data.getN_CONFIGURATIONS(); i++) {
			gp_array[i] = new GainPosition(i);
		}

		// initialize with -1 bcz configurations starts from 0...n
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			selectedQueries[i] = -1;
		}


		boolean nextConfig;

		// select configuration one by open
		do {
			// calculate gain for each configuration
			if (type == 0) {
				calculateGain(gp_array);
				Arrays.sort(gp_array, (a, b) -> Double.compare(b.gain, a.gain)); // descending
				nextConfig = selectNextConfiguration(gp_array);
			} else if (type == 1) {
				calculateGain1(gp_array);
				Arrays.sort(gp_array, (a, b) -> Double.compare(b.gain, a.gain)); // descending
				nextConfig = selectNextConfiguration(gp_array);
			} else {
				calculateGain2(gp_array); // order ascending
				Arrays.sort(gp_array, (a, b) -> Double.compare(a.gain, b.gain)); // ascending
				nextConfig = selectNextConfigurationModified(gp_array);
			}
		} while (nextConfig);

		configuration.getConfigurationGain(Data.configurationsSelected);

		configuration.updateResult();



	}

	// total Gain for each configuration
	private void calculateGain(GainPosition[] gp_array) {
		for (int i = 0; i < data.getN_CONFIGURATIONS(); i++) {
			int temp_gain = 0;
			int tempPosition = (gp_array[i].positioni >= 0) ? gp_array[i].positioni : i;
			for (int j = 0; j < data.getN_QUERIES(); j++) {
				int tempQ_gain = data.getCONFIGURATIONS_QUERIES_GAIN()[tempPosition][j];
				if (tempQ_gain > 0 && (selectedQueries[j] < 0
						|| tempQ_gain > data.getCONFIGURATIONS_QUERIES_GAIN()[selectedQueries[j]][j])) {
					temp_gain += data.getCONFIGURATIONS_QUERIES_GAIN()[tempPosition][j];
				}
			}
			gp_array[i].gain = temp_gain;
		}
	}

	// Gain-Cost of Configuration
	private void calculateGain1(GainPosition[] gp_array) {
		for (int i = 0; i < data.getN_CONFIGURATIONS(); i++) {
			int temp_gain = 0;
			int temp_cost = 0;
			int tempPosition = (gp_array[i].positioni >= 0) ? gp_array[i].positioni : i;
			int tempQ_gain = 0;
			for (int j = 0; j < data.getN_QUERIES(); j++) {
				tempQ_gain = data.getCONFIGURATIONS_QUERIES_GAIN()[tempPosition][j];
				if (tempQ_gain > 0 && (selectedQueries[j] < 0
						|| tempQ_gain > data.getCONFIGURATIONS_QUERIES_GAIN()[selectedQueries[j]][j])) {
					temp_gain += data.getCONFIGURATIONS_QUERIES_GAIN()[tempPosition][j];
				}
			}
			for (int j = 0; j < data.getN_INDEXES(); j++) {
				// check index is in configuration and if index is not already selected
				if (data.getCONFIGURATIONS_INDEXES_MATRIX()[tempPosition][j] == 1 && tempSelectedIndexes[j] == 0) {
					temp_cost += data.getINDEXES_FIXED_COST()[j];
				}
			}
			gp_array[i].gain = (temp_gain - temp_cost);
		}
	}

	// Memory/Gain of Configuration. gain per memory
	private void calculateGain2(GainPosition[] gp_array) {
		for (int i = 0; i < data.getN_CONFIGURATIONS(); i++) {
			int temp_gain = 0;
			int temp_mem = 0;
			int tempPosition = (gp_array[i].positioni >= 0) ? gp_array[i].positioni : i;
			for (int j = 0; j < data.getN_QUERIES(); j++) {
				int tempQ_gain = data.getCONFIGURATIONS_QUERIES_GAIN()[tempPosition][j];
				if (tempQ_gain > 0 && (selectedQueries[j] < 0
						|| tempQ_gain > data.getCONFIGURATIONS_QUERIES_GAIN()[selectedQueries[j]][j])) {
					temp_gain += data.getCONFIGURATIONS_QUERIES_GAIN()[tempPosition][j];
				}
			}
			for (int j = 0; j < data.getN_INDEXES(); j++) {
				if (data.getCONFIGURATIONS_INDEXES_MATRIX()[tempPosition][j] == 1 && tempSelectedIndexes[j] == 0) {
					temp_mem += data.getINDEXES_MEMORY_OCCUPATION()[j];
				}
			}
			if (temp_gain == 0) {
				temp_gain = 1;
			}
			gp_array[i].gain = temp_mem / temp_gain;
		}
	}

	// select next configuration for Greedy 0 and 1
	private boolean selectNextConfiguration(GainPosition[] gp_array) {
		for (int k = 0; k < data.getN_CONFIGURATIONS(); k++) {
			if (gp_array[k].selected == false) {
				int tempMemory = 0;
				int tempC = gp_array[k].positioni;
				// select indexed of the last selected configuration
				for (int j = 0; j < data.getN_INDEXES(); j++) {
					if (data.getCONFIGURATIONS_INDEXES_MATRIX()[tempC][j] == 1) {
						tempSelectedIndexes[j] += 1;
					}
				}
				// Calculate MEMORY
				for (int j = 0; j < data.getN_INDEXES(); j++) {
					if (tempSelectedIndexes[j] >= 1) {
						tempMemory += data.getINDEXES_MEMORY_OCCUPATION()[j];
					}
				}
				if (tempMemory <= data.getMEMORY()) {
					Data.configurationsSelected[tempC] = true;
					gp_array[k].selected = true;
					// selectedQueries update configuration
					for (int j = 0; j < data.getN_QUERIES(); j++) {
						int tempQ_gain = data.getCONFIGURATIONS_QUERIES_GAIN()[tempC][j];
						if (tempQ_gain > 0 && (selectedQueries[j] < 0
								|| tempQ_gain > data.getCONFIGURATIONS_QUERIES_GAIN()[selectedQueries[j]][j])) {
							selectedQueries[j] = tempC;
						}
					}
					return true;
				} else {
					return false;
				}
			}
			// check if this configuration[i] and query has gain greater than in selected
			// Queries than update
		}
		return true;
	}

	private boolean selectNextConfigurationModified(GainPosition[] gp_array) {

		for (int k = 0; k < data.getN_CONFIGURATIONS(); k++) {

			if (gp_array[k].selected == false) {

				int tempMemory = 0;

				int tempC = gp_array[k].positioni;

				// System.out.println(k +" : "+ tempC );

				// System.out.println(Arrays.toString(tempSelectedIndexes));

				// Calculate MEMORY

				for (int j = 0; j < data.getN_INDEXES(); j++) {

					if (tempSelectedIndexes[j] >= 1) {

						tempMemory += data.getINDEXES_MEMORY_OCCUPATION()[j];

					}

				}

				for (int j = 0; j < data.getN_INDEXES(); j++) {

					if (data.getCONFIGURATIONS_INDEXES_MATRIX()[tempC][j] == 1 && tempSelectedIndexes[j] == 0) {

						tempMemory += data.getINDEXES_MEMORY_OCCUPATION()[j];

					}

				}

				if (tempMemory <= data.getMEMORY()) {

					for (int j = 0; j < data.getN_INDEXES(); j++) {

						if (data.getCONFIGURATIONS_INDEXES_MATRIX()[tempC][j] == 1) {

							tempSelectedIndexes[j] += 1;

						}

					}

					Data.configurationsSelected[tempC] = true;

					gp_array[k].selected = true;

					// selectedQueries update configuration

					for (int j = 0; j < data.getN_QUERIES(); j++) {

						int tempQ_gain = data.getCONFIGURATIONS_QUERIES_GAIN()[tempC][j];

						if (tempQ_gain > 0 && (selectedQueries[j] < 0

								|| tempQ_gain > data.getCONFIGURATIONS_QUERIES_GAIN()[selectedQueries[j]][j])) {

							selectedQueries[j] = tempC;

						}

					}

					return true;

				}

			}

			// check if this configuration[i] and query has gain greater than in selected

			// Queries than update

			// System.out.println("Memory: " + tempMemory);

		}

		return false;
	}

}
