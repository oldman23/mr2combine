package mr2combine;

import java.util.concurrent.ConcurrentHashMap;

public class Constants {
	public final static int 
	LIF = 0, 
	POW = 1, 
	INT = 2, 
	SKI = 3, 
	SPD = 4, 
	DEF = 5;
	
	public final static ConcurrentHashMap<String, Integer> STATS_BY_ABBR = new ConcurrentHashMap<>();
	
	public final static String[] STAT_NAMES = {
			"LIF", "POW", "INT", "SKI", "SPD", "DEF"
	};
	
	static {
		STATS_BY_ABBR.put("l" , LIF);
		STATS_BY_ABBR.put("p" , POW);
		STATS_BY_ABBR.put("i" , INT);
		STATS_BY_ABBR.put("sk", SKI);
		STATS_BY_ABBR.put("sp", SPD);
		STATS_BY_ABBR.put("d" , DEF);
	}
}
