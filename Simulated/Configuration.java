package Simulated;

public class Configuration {

	private Index[] index;
	private int cost;

	public Configuration(int n_index) {

		this.index = new Index[n_index];

	}

	public void insert_index(int i, Index in) {
		index[i] = in;
		cost += index[i].get_cost();
	}

	public Index[] get_indext() {
		return index;

	}

	public int get_cost() {
		return cost;
	}

}
