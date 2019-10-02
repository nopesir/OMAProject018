package Greedy;

import java.util.Comparator;

class SortByGain implements Comparator<GainPosition> {
	public int compare(GainPosition a, GainPosition b) {
		// descending
		// if ( a.gain > b.gain ) return -1;
		// else if ( a.gain == b.gain ) return 0;
		// else return 1;
		// ascending
		if (a.gain < b.gain)
			return -1;
		else if (a.gain == b.gain)
			return 0;
		else
			return 1;
	}
}
