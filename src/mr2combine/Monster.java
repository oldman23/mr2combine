package mr2combine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class Monster {
	
	public Breed breed;
	String name;
	
	int[] stats ;
	
	{
		init();
	}
	
	public void init() {
		int[] arr = {-1,-1,-1,-1,-1,-1};
		stats = arr;		
		name = "Undefined";
	}
	
	public int[] getCorrectedStatsOrder(int[] correctedStats) {
		ArrayList<Integer> statOrder = new ArrayList<Integer>();
		
		int[] tempcs = correctedStats.clone();
		int todo = 6;
		while (todo > 0) {
			int duplicate = 0;
			ArrayList<Integer> currentRound = new ArrayList<>();
			int max = -2;
			
			for (int i = 0; i < 6; i++) {
				int stat = tempcs[i];
				if (stat == -1) {
					continue;
				}
				if (max == -2) {
					max = i;
					continue;
				}
				if (stat > tempcs[max]) {
					max = i;					
				}
			}
			
			if (max == -2) {
				throw new NullPointerException("Not supposed to happen. See this " + Arrays.toString(correctedStats));
			}
			
			
			int maxstat = tempcs[max];
			for (int i = 0; i < 6; i++) {
				int stat = tempcs[i];
				if (stat == maxstat) {					
					currentRound.add(i);
					duplicate++;
					tempcs[i] = -1;
				}
			}
//			System.out.println("duplicate= " + duplicate);
//			System.out.println("tempcs = " + Arrays.toString(tempcs));
			
			todo = todo - duplicate;
			if (duplicate == 1) {
				statOrder.add(currentRound.get(0));
			} else if (duplicate > 1) {
				currentRound.sort(new Comparator<Integer>() {
					@Override
					public int compare(Integer arg0, Integer arg1) {
						int baseStatIndex0 = breed.baseStatOrder_inverse[arg0];
						int baseStatIndex1 = breed.baseStatOrder_inverse[arg1];
						return baseStatIndex0 - baseStatIndex1;
					}
				});
				statOrder.addAll(currentRound);
			} else {
				throw new NullPointerException("Weirdness. See this " + correctedStats);
			}
		}
		
		int[] ret = new int[6];
		Iterator<Integer> iter = statOrder.iterator();
		for (int i = 0; i < 6; i++) {
			ret[i] =  iter.next();
		}
		
		return ret;
	}
	
	public int[] correctStats() {
		int[] arr = new int[6];
		
		for (int i = 0; i < arr.length; i++) {
			arr[i] = correctStat(stats[i], breed.statGains[i]);
		}
		
		return arr;
	}
	
	public static int correctStat(int stat, int statgain) {
		double mul = -1;
		
		switch(statgain) {
		case 1:
			mul = 0;
			break;
		case 2:
			mul = 0.5;
			break;
		case 3:
			mul = 1;
			break;
		case 4:
			mul = 1.5;
			break;
		case 5:
			mul = 2;
			break;
		default:
			throw new NullPointerException("Weirdness with statgain " + statgain);
		}
		
		int corrected = (int) Math.floor(mul * stat);
		if (corrected > 999) {
			corrected = 999;		
		}
		
		return corrected;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
