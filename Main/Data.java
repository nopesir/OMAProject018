package Main;

// all input data are static and can be used from any class
import java.io.*;

// Java program implementing Data class
// with getInstance() method
public class Data {
	// static variable single_instance of type Data
	private static Data single_instance = null;

	// variable of type String
	public String s;

	/*
	 * ----------------------------- GLOBAL VARIABLES ------------------------------
	 */
	private static int N_QUERIES;
	private static int N_INDEXES;
	private static int N_CONFIGURATIONS;
	private static int MEMORY;
	private static int[][] CONFIGURATIONS_INDEXES_MATRIX;
	private static int[] INDEXES_FIXED_COST;
	private static int[] INDEXES_MEMORY_OCCUPATION;
	private static int[][] CONFIGURATIONS_QUERIES_GAIN;

	public static int[] indexesSelected;
	public static boolean[] configurationsSelected;
	public static int[][] configurationsQueriesSelected;
	public static int gainSelected;

	public static int totalMemory;

	// for timeout check
	public static boolean timeout;

	// private constructor restricted to this class itself
	private Data(String file) throws FileNotFoundException {
		try {
			timeout = false;
			String line;
			String[] arr;
			// The file in that location is opened
			FileReader f = new FileReader(file);
			BufferedReader b = new BufferedReader(f);

			// Every value of each row is read and stored
			line = b.readLine();
			arr = line.split("\\s+");
			N_QUERIES = Integer.parseInt(arr[1]);

			line = b.readLine();
			arr = line.split("\\s+");
			N_INDEXES = Integer.parseInt(arr[1]);

			line = b.readLine();
			arr = line.split("\\s+");
			N_CONFIGURATIONS = Integer.parseInt(arr[1]);

			line = b.readLine();
			arr = line.split("\\s+");
			MEMORY = Integer.parseInt(arr[1]);

			CONFIGURATIONS_INDEXES_MATRIX = new int[N_CONFIGURATIONS][N_INDEXES];
			INDEXES_FIXED_COST = new int[N_INDEXES];
			INDEXES_MEMORY_OCCUPATION = new int[N_INDEXES];
			indexesSelected = new int[N_INDEXES];
			configurationsSelected = new boolean[N_CONFIGURATIONS];
			CONFIGURATIONS_QUERIES_GAIN = new int[N_CONFIGURATIONS][N_QUERIES];
			configurationsQueriesSelected = new int[N_CONFIGURATIONS][N_QUERIES];
			gainSelected = 0;
			line = b.readLine();
			// reading CONFIGURATIONS_INDEXES_MATRIX
			for (int row = 0; row < N_CONFIGURATIONS; row++) {
				// Every value of each row is read and stored
				line = b.readLine();
				String[] values = line.trim().split("\\s+");
				for (int col = 0; col < values.length; col++) {
					CONFIGURATIONS_INDEXES_MATRIX[row][col] = Integer.parseInt(values[col]);
				}
			}
			line = b.readLine();
			// reading INDEXES_FIXED_COST
			for (int row = 0; row < N_INDEXES; row++) {
				// Every value of each row is read and stored
				line = b.readLine();
				INDEXES_FIXED_COST[row] = Integer.parseInt(line);
			}
			totalMemory = 0;
			line = b.readLine();
			// reading INDEXES_MEMORY_OCCUPATION
			for (int row = 0; row < N_INDEXES; row++) {
				// Every value of each row is read and stored
				line = b.readLine();
				INDEXES_MEMORY_OCCUPATION[row] = Integer.parseInt(line);
				totalMemory += INDEXES_MEMORY_OCCUPATION[row];
			}
			line = b.readLine();
			// reading CONFIGURATIONS_QUERIES_GAIN
			for (int row = 0; row < N_CONFIGURATIONS; row++) {
				// Every value of each row is read and stored
				line = b.readLine();
				String[] values = line.trim().split("\\s+");
				for (int col = 0; col < values.length; col++) {
					CONFIGURATIONS_QUERIES_GAIN[row][col] = Integer.parseInt(values[col]);
				}
			}
			b.close();
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				throw new FileNotFoundException();
			}
			System.out.println(e);
			return; // Always must return something
		}
	}

	public static Data getInstance() {
		return single_instance;
	}

	// static method to create instance of Data class
	public static Data getInstance(String file) throws FileNotFoundException {
		try {
			single_instance = new Data(file);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		}
		return single_instance;
	}

	public int getN_QUERIES() {
		return N_QUERIES;
	}

	public int getN_INDEXES() {
		return N_INDEXES;
	}

	public int getN_CONFIGURATIONS() {
		return N_CONFIGURATIONS;
	}

	public int getMEMORY() {
		return MEMORY;
	}

	public int[][] getCONFIGURATIONS_INDEXES_MATRIX() {
		return CONFIGURATIONS_INDEXES_MATRIX;
	}

	public int[] getINDEXES_FIXED_COST() {
		return INDEXES_FIXED_COST;
	}

	public int[] getINDEXES_MEMORY_OCCUPATION() {
		return INDEXES_MEMORY_OCCUPATION;
	}

	public int[][] getCONFIGURATIONS_QUERIES_GAIN() {
		return CONFIGURATIONS_QUERIES_GAIN;
	}

	public int[] getIndexesSelected() {
		return indexesSelected;
	}

	public static int[][] getConfigurationsQueriesSelected() {
		return configurationsQueriesSelected;
	}

	public boolean[] get_Configuration_selected() {
		return configurationsSelected;
	}

	public int get_GainSelected() {
		return gainSelected;
	}
}
