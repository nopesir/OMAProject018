package Main;

import Simulated.Configuration;
import Simulated.Solution;

public class SimulatedThread implements Runnable {

	private Solution solution;
	private int stop_time;
	private boolean toSave;
	private String path;
	private Configuration [] array_configuration;

	public SimulatedThread(Solution s, int stp, boolean toSave, String path, Configuration [] array_configuration) {
		solution = s;
		stop_time = stp;
		this.toSave = toSave;
		this.path = path;
		this.array_configuration = array_configuration;
	}

	@Override
	public void run() {

		try {
			solution.calculate(stop_time, toSave, path, array_configuration);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
