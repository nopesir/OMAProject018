package Greedy;

import Main.*;

public class Configurations {
	Data data = Data.getInstance();
	private int[] selectedQueries = new int[data.getN_QUERIES()]; // which configuration for each query
	private int[] tempSelectedIndexes = new int[data.getN_INDEXES()];
	private int selectedGain;

	public Configurations() {
		// initialize with -1 bcz configurations starts from 0...n
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			selectedQueries[i] = -1;

		}
		return;
	}

	public int getConfigurationGain(boolean[] configurationsSelected) {
		int tempSelectedGain = 0;
		configurationToQuery(configurationsSelected);
		// calculate Gain
		int tempGain = selectedQueriesGain();
		// mark selectedIndexes for query selected configuration
		configurationToIndexes();

		// calculate cost
		int tempCost = selectedIndexesCost();
		tempSelectedGain = tempGain - tempCost;

		if (!checkMemory()) {
			tempSelectedGain = selectedGain / 2;
			return tempSelectedGain;
		} else {
			selectedGain = tempSelectedGain;
		}
		return selectedGain;
	}

	// markConfigurationQuery
	public void updateResult() {
		// mark on configurationsQueriesSelected
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			if (selectedQueries[i] >= 0) {
				Data.configurationsQueriesSelected[selectedQueries[i]][i] = 1;
			}
		}
		Data.gainSelected = selectedGain;
	}

	// Mark configuration to query
	private void configurationToQuery(boolean[] configurationsSelected) {
		for (int i = 0; i < data.getN_CONFIGURATIONS(); i++) {
			if (configurationsSelected[i] == true) {
				for (int j = 0; j < data.getN_QUERIES(); j++) {
					int tempQ_gain = data.getCONFIGURATIONS_QUERIES_GAIN()[i][j];
					if (tempQ_gain > 0 && (selectedQueries[j] < 0
							|| tempQ_gain > data.getCONFIGURATIONS_QUERIES_GAIN()[selectedQueries[j]][j])) {
						selectedQueries[j] = i;
					}
				}
			}
		}
		return;
	}

	// compute gain on selected Query
	private int selectedQueriesGain() {
		int tempGain = 0;
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			if (selectedQueries[i] >= 0) {
				tempGain += data.getCONFIGURATIONS_QUERIES_GAIN()[selectedQueries[i]][i];
			}
		}
		return tempGain;
	}

	// Mark configuration to Indexes
	private void configurationToIndexes() {
		for (int i = 0; i < data.getN_QUERIES(); i++) {
			int tempC = selectedQueries[i];
			if (tempC >= 0) {
				for (int j = 0; j < data.getN_INDEXES(); j++) {
					if (data.getCONFIGURATIONS_INDEXES_MATRIX()[tempC][j] == 1) {
						tempSelectedIndexes[j] = 1;
					}
				}
			}
		}
	}

	private int selectedIndexesCost() {
		int tempCost = 0;
		for (int i = 0; i < data.getN_INDEXES(); i++) {
			if (tempSelectedIndexes[i] >= 1) {
				tempCost += data.getINDEXES_FIXED_COST()[i];
			}
		}
		return tempCost;
	}

	// check configuration valid
	public boolean checkMemory() {
		int tempMemory = 0;
		// Calculate MEMORY
		for (int j = 0; j < data.getN_INDEXES(); j++) {
			if (tempSelectedIndexes[j] >= 1) {
				tempMemory += data.getINDEXES_MEMORY_OCCUPATION()[j];
			}
		}
		return tempMemory <= data.getMEMORY();
	}

}
