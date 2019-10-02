package Simulated;

public class Query {
	private Configuration configuration;
	private Configuration[] array_configuration;
	private int[] gain_matrix;
	private int gain;

	public Query() {

	}

	public Query(Configuration[] value, int lenght) {
		array_configuration = value;
		gain_matrix = new int[lenght + 1];

	}

	public Query(Configuration[] value, int g, int[] m, Configuration c) {
		configuration = c;
		array_configuration = value;
		gain_matrix = m;
		gain = g;

	}

	public void set_gain(int i, int value) {
		gain_matrix[i] = value;
	}

	public void set_gain(int i) {
		gain = i;
	}

	public void set_array(int i, Configuration c) {
		array_configuration[i] = c;

	}

	public Index[] get_configuration_index() {
		return configuration.get_indext();
	}

	public Configuration get_configuration() {
		return configuration;
	}

	public void set_configuration(Configuration c) {

		configuration = c;
	}

	public int[] get_array_gain() {

		return gain_matrix;
	}

	public Configuration get_conf_matrix(int i) {
		return array_configuration[i];
	}

	public int get_gain_matrix(int i) {
		return gain_matrix[i];
	}

	public Configuration[] get_array_configuration_matrix() {
		return array_configuration;
	}

	public int get_gain() {
		return gain;
	}

	public int get_size() {
		return gain_matrix.length;
	}
}
