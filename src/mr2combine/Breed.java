package mr2combine;

public class Breed {
	
	
	public int[] baseStatOrder;
	public int[] baseStatOrder_inverse;
	public int[] statGains;
	String main;
	String sub;
	int lifespan;
	
	{
		baseStatOrder   	  = new int[6];
		baseStatOrder_inverse = new int[6];
		statGains	    	  = new int[6];
		main 		    	  = "";
		sub		 	    	  = "";
	}
	
	public boolean compareBaseStat(int a, int b) {
		for (int stat : baseStatOrder) {
			if (stat == a) {
				return true;
			}
			else if (stat == b){
				return false;
			}
		}
		System.out.println("ERROR : Stats comparison doesn't resolve.  " + toString());
		return false;
	}
	
	@Override
	public String toString() {
		return main + "/" + sub;
	}
	
	public int[] getStatGainOrder() {
		int[] ret = { 0, 1, 2, 3, 4, 5 };
		int[] gains = statGains.clone();
		for (int i = 0; i < 5; i++) {
			for (int j = 4; j >= i; j--) {
				if (gains[j + 1] >= gains[j]) {
					if (gains[j] == gains[j + 1]) {
						if (baseStatOrder_inverse[ret[j + 1]] > baseStatOrder_inverse[ret[j]]) {
							continue;
						}
					}
					int temp = gains[j];
					gains[j] = gains[j + 1];
					gains[j + 1] = temp;

					int temp2 = ret[j];
					ret[j] = ret[j + 1];
					ret[j + 1] = temp2;
				} else {
				}
			}
		}

		return ret;		
	}	
	
	private static class Helper implements Comparable<Helper>{
		int statType;
		int statGain;
		int orderIndex;
		
		@Override
		public int compareTo(Helper arg0) {
			int ret = -statGain + arg0.statGain;
			if (ret == 0) {
				ret = orderIndex - arg0.orderIndex;
			}
			return ret;
		}
	}
	
	double var = 0;
	double mean = 0;
	public double getVar() {
		if (var == 0) {
			mean = getMean();
			for (int sg : statGains) {
				var += ( mean - sg ) * (mean - sg);				
			}
			var = var / 6;
		}
		return var;
	}
	
	public double getMean() {
		if (mean == 0) {
			mean = sum(statGains) / 6d;
		}
		return mean;
	}
	private static int sum(int[] summand) {
		int ret = 0;
		for (int i : summand) {
			ret += i;
		}
		return ret;
	}
}
