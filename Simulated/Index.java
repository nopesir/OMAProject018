package Simulated;

public class Index {
	private int cost;
	private int memory;

	public Index(int value, int memory) {
		cost = value;
		this.memory = memory;
	}

	public int get_cost() {
		return cost;
	}

	public int get_memory() {
		return memory;
	}
}