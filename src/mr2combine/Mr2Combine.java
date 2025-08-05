package mr2combine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class Mr2Combine {
	
	public static ConcurrentHashMap<String, Breed> BREEDS = new ConcurrentHashMap<>();
	public static  ConcurrentHashMap<String, HashSet<Breed>> BREEDS_BY_TYPE = new ConcurrentHashMap<>();

	
	public static final DecimalFormat df = new DecimalFormat("#0.00");
	
	
	/**
	 * 
	 * 
	 * ORDER : LIF-INT-SKI-SPD-DEF-POW
       LIF : 933
       POW : 424
       INT : 770
       SKI : 735
       SPD : 704
       DEF : 437
	 * 
	 * 
	 */
	
	
	
	
	public static void main(String[] args) {
		processFiles();
		int a = 5;
		
		switch (a) {
		case 1:
//			int[] stats1  = {980, 990, 990, 990, 990, 990};
//			planOffspring(false, "joker/joker", stats1);
			int[] stats1  = {999,999,999,999,999,999};
//			DEF-POW-LIF-INT-SKI-SPD
//			POW-DEF-LIF-INT-SKI-SPD
			planOffspring(false, "Rockness","hopper/kato", stats1, 6);
			break;
		case 2:
			int[] statorder = {0,2,3,4,5,1};
			for (int i = 0; i < 5; i++) {
				int[] stats = {773,364,690,675,624,377};
				planStat("ape/gali", stats, statorder, 500, 20, false);
				int total = 0;
				for (int stat : stats) {
					total += stat;
				}
				double mean = total / 6d;
				double var = 0;
				for (int stat : stats) {
					double varling = (1 / 6d) * ((stat - mean) * (stat - mean)); 
//					o("stat = " + stat + ", mean = " + mean + ", varling = " + varling);
					var += varling ;
				}
				
				printstat(0, stats);
				o(1,"var : " + df.format(Math.sqrt(var)));
			}			
			break;
		case 3:
			freeStyle();
			break;
		case 4:
			fillStatFree("hopper/kato", 1400);
			break;
		case 5:
			findParents("zuum/joker");
			break;
		case 6:
			findTheOne();
			break;
		}
	}
	
	private static void findParents(String kidBreedString) {
		Breed kidbreed = BREEDS.get(kidBreedString);
		String kidmain = kidbreed.main;
		String kidsub  = kidbreed.sub;
		
		HashSet<Breed> pParLeft  = BREEDS_BY_TYPE.get(kidmain);
		HashSet<Breed> pParRight = BREEDS_BY_TYPE.get(kidsub);
		
		pParLeft.remove(kidbreed);
		pParRight.remove(kidbreed);
		o(0,"");
		o(0, "Kid name          : " + kidbreed);
		o(0, "Kid stat gains    : " + Arrays.toString(kidbreed.statGains));
		o(0, "Kid lifespan      : " + kidbreed.lifespan);
		
		
		for (int x = 0; x < 2; x++) {
			HashSet<Breed> potentialParent;
			if (x == 0) {
				o(0, "Potential parentA : ");
				potentialParent = pParLeft;
			} else {
				o(0, "\nPotential parentB : ");
				potentialParent = pParRight;
			}
			PrintTable pt = new PrintTable("pot. parent", "max pos. stats", "score", "stat gains", "lifespan", "mean", "var");
			int i = -1;
			String[] breedname  = new String[potentialParent.size()];
			int[]    breedScore = new int[potentialParent.size()];
			int[][]  maxstatss  = new int[potentialParent.size()][6];
			
			for (Breed pParBreed : potentialParent) {
				i++;
				breedname[i] = pParBreed.toString();
				int[] maxStats = maxStats(pParBreed, kidbreed.baseStatOrder);
				maxstatss[i]   = maxStats;
				if (maxStats == null) {
//					o(1, "Parent candidate " + pParBreed + " is not compatible ( " + ats(pParBreed.statGains) + " )");
					breedScore[i] = -1;					
					continue;
				}
				int score = 0;
				for (int stat : maxStats) {
					score += stat;
				}
				breedScore[i] = score;
				StringBuilder sb = new StringBuilder();
//				sb.append(pParBreed.toString()).setLength(20);
//				// sb.append((maxStats == null? "null" : Arrays.toString(maxStats)));
//				sb.append("   ").append(Arrays.toString(pParBreed.statGains)).append("   ");
//				sb.append(score).append("   ").append(pParBreed.lifespan).append(" weeks");
//				o(1, sb.toString());
			}
			int length = breedname.length;

			for (int j = 1; j < length; j++) {
				for (int k = length - 1; k >= j; k--) {
					if (breedScore[k] > breedScore[k - 1]) {
						int temp = breedScore[k];
						breedScore[k] = breedScore[k - 1];
						breedScore[k - 1] = temp;

						String stemp = breedname[k];
						breedname[k] = breedname[k - 1];
						breedname[k - 1] = stemp;
						
						int[] atemp = maxstatss[k];
						maxstatss[k] = maxstatss[k - 1];
						maxstatss[k - 1] = atemp;
					}
				}
			}

//			o(0, "Ordered Parent A Candidate : ");
			for (int j = 0; j < length; j++) {
				String bname = breedname[j];
				Breed pParBreed = BREEDS.get(bname);
				int score = breedScore[j];
				if (score < 0) {
					continue;
				}
				if (maxstatss[j] == null) {
					o(2, bname + " maxstats is null, score is " + score + " (statgains : " + ats(pParBreed.statGains) + " )");
				}
				pt.add(bname)
				  .add(ats(maxstatss[j]))
				  .add(score + "")
				  .add(ats(pParBreed.statGains))
				  .add(pParBreed.lifespan + " weeks")
				  .add(form2(pParBreed.getMean()))
				  .add(form2(pParBreed.getVar()))
				  .flush();
				  ;
			}
			pt.printNoLines("");
		}
	}
	
	private static void findTheOne() {
		
		double[]   kidScores   = new double[BREEDS.size()];
		Breed[]    kidBreeds   = new Breed[BREEDS.size()];
		String[][]  kidParents = new String[BREEDS.size()][2];
		
		int ee = -1; 
		for (Entry<String, Breed> e : BREEDS.entrySet()) {
			
			ee++;
			o(0, "Analyzing " + e.getValue() + "...");
			Breed kidbreed = e.getValue();
			String kidmain = kidbreed.main;
			String kidsub  = kidbreed.sub;

			kidBreeds[ee]  = kidbreed;
			kidScores[ee]  = 0;
			kidParents[ee] = new String[] {"undef/undef", "undef/undef"};
			
			if (kidbreed.main.equals(kidbreed.sub)) {
				continue;
			} 
			
			/**
			 * Check ??? sub
			 */
			HashSet<Breed> checksub = BREEDS_BY_TYPE.get(kidbreed.sub);
			if (checksub.size() == 1) {
				continue;
			}
			/**
			 * End check ??? sub
			 */
			
			HashSet<Breed> pParLeft = BREEDS_BY_TYPE.get(kidmain);
			HashSet<Breed> pParRight = BREEDS_BY_TYPE.get(kidsub);

			pParLeft.remove(kidbreed);
			pParRight.remove(kidbreed);

			for (int x = 0; x < 2; x++) {
				HashSet<Breed> potentialParent;
				if (x == 0) {
					potentialParent = pParLeft;
				} else {
					potentialParent = pParRight;
				}

				int i = -1;
				String[] breedname = new String[potentialParent.size()];
				int[] breedScore   = new int[potentialParent.size()];
				int[][] maxstatss  = new int[potentialParent.size()][6];

				for (Breed pParBreed : potentialParent) {
					i++;
					breedname[i] = pParBreed.toString();
					int[] maxStats = maxStats(pParBreed, kidbreed.baseStatOrder);
					maxstatss[i] = maxStats;
					if (maxStats == null) {
						breedScore[i] = -1;
						continue;
					}
					int score = 0;
					for (int stat : maxStats) {
						score += stat;
					}
					breedScore[i] = score;
				}
				int length = breedname.length;

				for (int j = 1; j < length; j++) {
					for (int k = length - 1; k >= j; k--) {
						if (breedScore[k] > breedScore[k - 1]) {
							int temp = breedScore[k];
							breedScore[k] = breedScore[k - 1];
							breedScore[k - 1] = temp;

							String stemp = breedname[k];
							breedname[k] = breedname[k - 1];
							breedname[k - 1] = stemp;

							int[] atemp = maxstatss[k];
							maxstatss[k] = maxstatss[k - 1];
							maxstatss[k - 1] = atemp;
						}
					}
				}
				kidParents[ee][x] = breedname[0]; 
				kidScores[ee] += breedScore[0];
			}
		}
		
		int length = BREEDS.size();
		for (int j = 1; j < length; j++) {
			for (int k = length -1; k >= j; k--) {
				if (kidScores[k] > kidScores[k - 1]) {
					double temp 	 = kidScores[k];
					kidScores[k] 	 = kidScores[k - 1];
					kidScores[k - 1] = temp;

					Breed btemp 	 = kidBreeds[k];
					kidBreeds[k] 	 = kidBreeds[k - 1];
					kidBreeds[k - 1] = btemp;
					
					String[] arrtemp  = kidParents[k];
					kidParents[k]     = kidParents[k - 1];
					kidParents[k - 1] = arrtemp;
				}
			}
		}
		
		o(0,"");
		o(0,"");
		PrintTable pt = new PrintTable("breed", "score", "parentA", "parentB", "meana", "meanb", "vara", "varb", "meant", "vart");
		for (int i = 0; i < length; i++) {
			Breed breed  = kidBreeds[i];
			double score = kidScores[i];
			if (score <= 0) {
				continue;
			}
			String[] parents = kidParents[i]; 
			o("adding for " + breed)		;
			Breed pabreed = BREEDS.get(parents[0]);
			Breed pbbreed = BREEDS.get(parents[1]);
			pt.add(breed)
			  .add(score + "")
			  .add(parents[0])
			  .add(parents[1])
			  .add(pabreed == null? "-1" : df.format(pabreed.getMean()))
			  .add(pbbreed == null? "-1" : df.format(pbbreed.getMean()))
			  .add(pabreed == null? "-1" : df.format(pabreed.getVar()))
			  .add(pbbreed == null? "-1" : df.format(pbbreed.getVar()))
			  .add(pbbreed == null? "-1" : df.format(pabreed.getMean() + pbbreed.getMean()))
			  .add(pbbreed == null? "-1" : df.format(pabreed.getVar() + pbbreed.getVar()))
			  .flush()
			;
			o("done");
		}
		
		pt.print("");
	}

	private static void fillStatFree(String breedString, int total) {
		o();
		o("Stats of monster : ");
		for (int i = 0; i < 10; i++) {
			Breed breed = BREEDS.get(breedString);
			Monster monster = new Monster();
			int[] stats = {0,0,0,0,0,0};
			monster.stats = stats;
			monster.breed = breed;

			fillStatFree(monster, total, 20);
			o(1, ">>>>>>>>>>>>" + ats(monster.stats));
			planOffspring(true, "Nameless", monster.breed.toString(), monster.stats, 3);
		}
	}

	private static void freeStyle() {
		int size = BREEDS.entrySet().size();
		
		Breed[] breedal    = new Breed[size];
		Double[] meanal    = new Double[size];
		Double[] varal     = new Double[size];
		Double[] score     = new Double[size];
		
		
		int i = 0;
		for (Entry<String, Breed> e : BREEDS.entrySet()) {
			Breed breed = e.getValue();
			int[] statGains = breed.statGains.clone();
			
			double mean = sum(statGains) / 6d;
			
			double var = 0;
			for (int sg : statGains) {
				var += ( mean - sg ) * (mean - sg);				
			}
			var = var/6;

			breedal[i] = breed;
			meanal[i]  = mean;
			varal[i]   = var;
			
			score[i] = (double) (statGains[4] + statGains[5]);
			
			for (int j = i; j > 0; j--) {
				if ((meanal[j] >= meanal[j-1])) {
					double temp;
					temp  		 = varal[j];
					varal[j]     = varal[j - 1];
					varal[j - 1] = temp;
					
					temp  		 = score[j];
					score[j]     = score[j - 1];
					score[j - 1] = temp;

					temp          = meanal[j];
					meanal[j]     = meanal[j - 1];
					meanal[j - 1] = temp;

					Breed stemp     = breedal[j];
					breedal[j]      = breedal[j - 1];
					breedal[j - 1]  = stemp;
					
				}
			}
			i++;
		}
		o("1 " + breedal.length);
		o("2 " + meanal.length);
		o("3 " + varal.length);
		o();
		StringBuilder sbp = new StringBuilder();
		sbp.append("BREED")
		  	.setLength(20);
		sbp .append("MEAN")
		    .setLength(30);
		sbp .append("VAR")
		    .setLength(40);
		sbp .append("LIFESPAN")
		    .setLength(60);
		sbp .append("STATGAIN")
	    .setLength(80);
		o(sbp);
		for (int j = 0; j < meanal.length; j++) {
			StringBuilder sb = new StringBuilder();
			sb.append(breedal[j]).setLength(20);
			sb.append(df.format(meanal[j]));
			sb.setLength(30);
			sb.append(df.format(varal[j]));
			sb.setLength(40);
			sb.append(breedal[j].lifespan + " weeks");
			sb.setLength(60);
			sb.append(ats(breedal[j].statGains));
			sb.setLength(80);
			o(sb);
		}
	}

	public static String form2(double d) {
		return df.format(d);
	}
	private static void printstat(int indent, int[] stats) {
		for (int i = 0; i < 6; i++) {
			o(indent, Constants.STAT_NAMES[i] + " : " + stats[i]);			
		}
	}
	
	private static void planStat(String breedstring, int[] stats, int[] statorder, int total, int unit, boolean reduce) {
		Breed breed = BREEDS.get(breedstring);
		Monster monster = new Monster();
		monster.stats = stats;
		monster.breed = breed;
		if (statorder == null) {
			statorder = monster.breed.baseStatOrder;
		}
		fillStat(monster, statorder, total, unit, reduce);
		o();
	}

	private static void fillStat(Monster monster, int[] statorder, int stattotal, int unit, boolean reduce) {
		ArrayList<Integer> al = new ArrayList<>();
		for(int i = 0; i < 6; i++) {
			int gain = monster.breed.statGains[i];
			int weight = 0;
			switch (gain) {
			case 1:
				weight = 2;
				break;
			case 2:
				weight = 5;
				break;
			case 3:
				weight = 10;
				break;
			case 4:
				weight = 15;
				break;
			case 5:
				weight = 20;
				break;
			}
			
			for (int j = 0; j < weight; j++) {
				al.add(i);
			}
		}
		int failsafe_base = 200;
		int iter = 0;
		int failsafe = failsafe_base;
		while(true && failsafe > 0) {
			iter++;
			if (stattotal > 0) {
				int ran = (int) (Math.floor(Math.random() * al.size()));
				if (ran == al.size()) {
					throw new NullPointerException("Something weird here...");
				}
				int stat = al.get(ran);
				
				if (!reduce) {
					monster.stats[stat] += unit;
				} else {
					monster.stats[stat] -= unit;
				}
				
				if (fulfill(statorder, monster, 6) && !(monster.stats[stat] > 999)) {
					failsafe = failsafe_base;
					stattotal -= unit;
				} else {
					failsafe--;
					if (reduce) {
						monster.stats[stat] += unit;
					} else {
						monster.stats[stat] -= unit;
					}
					continue;
				}
			} else {
				break;
			}
		}
		if (failsafe <= 0) {
			o("\nFillstat has failed");		
		}
	}
	
	private static void fillStatFree(Monster monster, int stattotal, int unit) {
		ArrayList<Integer> al = new ArrayList<>();
		for(int i = 0; i < 6; i++) {
			int gain = monster.breed.statGains[i];
			int weight = 0;
			switch (gain) {
			case 1:
				weight = 2;
				break;
			case 2:
				weight = 5;
				break;
			case 3:
				weight = 10;
				break;
			case 4:
				weight = 15;
				break;
			case 5:
				weight = 20;
				break;
			}
			
			for (int j = 0; j < weight; j++) {
				al.add(i);
			}
		}
		
		int iter = 0;
		while(true) {
			iter++;
			if (stattotal > 0) {
				int ran = (int) (Math.floor(Math.random() * al.size()));
				if (ran == al.size()) {
					throw new NullPointerException("Something weird here...");
				}
				int stat = al.get(ran);
				
				monster.stats[stat] += unit;
				
				if (!(monster.stats[stat] > 999)) {
					stattotal -= unit;
				} else {
					monster.stats[stat] -= unit;
					continue;
				}
			} else {
				break;
			}
		}
		
		
	}
	public static void planOffspring(boolean cutshort, String name, String breedstring, int[] stats, int n) {

		System.out.print("\n=======================================================================\n\nMONSTERS LABORATORY\n\n-----------------------------------------------------------------------\n");
		Monster parent = new Monster();
//		int[] stats  = {688, 930, 971, 613, 675, 778};
//		int[] stats  = {991,939,999,999,875,995};
		
		
		parent.stats = stats;
		parent.name  = name;
		parent.breed = BREEDS.get(breedstring);
		
		o("Parent");
		o(1, "Name     : " + parent);
		o(1, "Main     : " + parent.breed.main);
		o(1, "Sub      : "  + parent.breed.sub);
		o(1, "Stats    :");
		displayStats(stats, 2);
		o(1, "Stats    : " + ats(stats));
		o(1, "Gains    : " + ats(parent.breed.statGains));
		o(1, "B.Order  : " + ats(parent.breed.baseStatOrder));
		o(1, "Lifespan : " + parent.breed.lifespan + " weeks");
		
		int[] correctedStats = parent.correctStats();
		int[] statorder      = parent.getCorrectedStatsOrder(correctedStats);

		o(1, "Corrected Stats : ");
		displayStats(correctedStats, 2);
		
		System.out.format("  Corrected Stat Order : %s-%s-%s-%s-%s-%s (%s, %s, %s, %s, %s, %s)%n"
				, Constants.STAT_NAMES[statorder[0]]
				, Constants.STAT_NAMES[statorder[1]]
				, Constants.STAT_NAMES[statorder[2]]
				, Constants.STAT_NAMES[statorder[3]]
				, Constants.STAT_NAMES[statorder[4]]
				, Constants.STAT_NAMES[statorder[5]]
				, statorder[0], statorder[1], statorder[2], statorder[3], statorder[4], statorder[5]);
		
		
		ArrayList<OffspringScore> offspringscores = new ArrayList<>();
		HashSet<Breed> potOffsprings = getPotentialOffsprings(parent.breed);		
//		o(1, "Potential Offsprings :");
		for (Breed potentialOS : potOffsprings) {
			int[] matches = matches(potentialOS, statorder);
			int matchScore = countNonNegative(matches);
//			o(2, potentialOS.toString() + ".  Match " + matchScore  + " " + getMatchesString(matches));
			offspringscores.add(new OffspringScore(potentialOS, matchScore));
		}
		
		/**
		 * FIND TOP N POTENTIAL OFFSPRING
		 */
		Collections.sort(offspringscores);
		o(1, "TOP " + n + " Potential Offsprings: ");
		int setsize = Math.min(offspringscores.size(), n);
		for (int i = 0; i < setsize; i++) {
			OffspringScore oss = offspringscores.get(i);
			o(2, oss.breed + " score =  " + oss.score);
		}
		
		if (cutshort)  {
			return;
		}
		for (int i = 0; i < setsize; i++) {
			OffspringScore oss = offspringscores.get(i);
			Breed osbreed = oss.breed;
			int[] matches = matches(osbreed, statorder);
			o(1, "C >>> " + osbreed + " " + getMatchesString2(matches));
			
			
			/**
			 * FIND PARENT 2
			 */
			String type = null;
			if (!(osbreed.main.equals(parent.breed.main) || osbreed.main.equals(parent.breed.sub))) {
				type = osbreed.main;
			} 
			else if (!(osbreed.sub.equals(parent.breed.main) || osbreed.sub.equals(parent.breed.sub))) {
				type = osbreed.sub;
			}
			else {
				o(2,"Breed same as parent " + osbreed);
				continue;
			}
			o(2,"P2 >>> osbreed = ");
			HashSet<Breed> pp2set = BREEDS_BY_TYPE.get(type);
			pp2set.remove(osbreed);
			ArrayList<HelperMaxStat> al = new ArrayList<>();
			for (Breed pp2 : pp2set) {
				/**
				 * P2 STAT SIMULATION
				 */
				int[] maxStats  = maxStats(pp2, matches);
				if (maxStats == null) {
					continue;
				}
				int totalstat   = sum(maxStats);
				int score =  0;
				for (int j = 0; j < 6; j++) {
					if (matches[j] >=  0) {
						score += maxStats[j];
					}
				}
				HelperMaxStat x = new HelperMaxStat(score, maxStats, pp2);
				al.add(x); 
			}
			Collections.sort(al);
			
			for (HelperMaxStat a : al) {
				Monster mon = new Monster();
				mon.breed = a.breed;
				mon.stats = a.maxstats;
				int[] so = mon.getCorrectedStatsOrder(mon.correctStats());
				o(4, pad(a.breed,20) + "| Score: " + a.totalstat + " |  " + Arrays.toString(a.maxstats) + " |  " + ats(a.breed.statGains) + " |  " + sum(a.breed.statGains) + " | " + a.breed.lifespan + " weeks");
				
//				o(4, a.breed + ".  Score: " + a.totalstat + " " + Arrays.toString(a.maxstats) + "");
				
			}
		}
		
		Breed b = BREEDS.get("gali/gali");
		o();
		o("gali statorder " + ats(b.baseStatOrder));
	}
	
	private static String pad(Breed breed, int i) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(breed);
		sb.setLength(20);
		return sb.toString();
	}

	private static String ats(int[] statGains) {
		return Arrays.toString(statGains);
	}

	private static class HelperMaxStat implements Comparable<HelperMaxStat>{
		public int totalstat;
		public int[] maxstats;
		public Breed breed;
		
		public HelperMaxStat (int a1, int[] a2, Breed a3) {
			totalstat = a1;
			maxstats = a2;
			breed = a3;
		}
		
		@Override
		public int compareTo(HelperMaxStat arg0) {
			return - (this.totalstat - arg0.totalstat);
		}
		
	}
	
	private static String getMatchesString(int[] matchesArr) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		boolean b = false;
		for (int match : matchesArr) {
			if (match == -1 ) {
				continue;
			}
			b = true;
			sb.append(Constants.STAT_NAMES[match]);
			sb.append(", ");
		}
		if (b)
			sb.setLength(sb.length() - 2);
		sb.append(")");
		return sb.toString();
	}

	private static String getMatchesString2(int[] matchesArr) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int match : matchesArr) {
			if (match == -1 ) {
				sb.append("*");
				sb.append(", ");
			} else {
				sb.append(Constants.STAT_NAMES[match]);
				sb.append(", ");
			}
		}
		sb.setLength(sb.length() - 2);
		sb.append(")");
		
		sb.append(" / ");
		for(int i = 0; i < 6; i++) {
			sb.append(matchesArr[i] == -1?"x":matchesArr[i]);
			sb.append("-");
		}
		sb.setLength(sb.length() - 1);
		
		return sb.toString();
	}
	
	private static int countNonNegative(int[] matches) {
		int ret = 0;
		for (int i : matches) {
			if (i >= 0) {
				ret++;
			}
		}
		return ret;
	}

	private static int[] maxStats(Breed p_breed, int[] p_matches) {			
		int[] ret = { 0, 0, 0, 0, 0, 0 };		
		Monster monster = new Monster();
		monster.stats = ret;
		monster.breed = p_breed;
		
		int[] optimumOrder = p_matches.clone();
		
		/**
		 * Optimum stat order
		 */
		int[] gainorder = p_breed.getStatGainOrder();
		for (int statType : gainorder) {
			 if (contains(optimumOrder, statType)) {
				 continue;
			 } else {
				 for (int i = 0; i < 6; i++) {
					 if (optimumOrder[i] == -1) {
						 optimumOrder[i] = statType;
						 break;
					 }
				 }
			 }
		}
		int[] orderedgains = new int[6];
		
		for (int i = 0; i < 6; i++) {
			orderedgains[i] = monster.breed.statGains[optimumOrder[i]];
		}
		
		
		for (int i = 1; i < 6; i++) {
			if ((orderedgains[i - 1] == 1) && (orderedgains[i] != 1)) {
				return null;
			} else if ((orderedgains[i - 1] == 1) && (orderedgains[i] == 1)) {
				if (p_breed.baseStatOrder_inverse[optimumOrder[i - 1]] > p_breed.baseStatOrder_inverse[optimumOrder[i]]) {
					return null;
				} else {
					
				}
			}
		}
////
//		o(3, "Optimum order = " + Arrays.toString(optimumOrder) + " | " + monster.breed);
		
		for (int i = 0; i < 6; i++) {
			int statindex = optimumOrder[i];
			while(true) {
				ret[statindex] += 20;
				if (fulfill(optimumOrder, monster, i + 1)) {
					/**
					 * IF statgains is 2 AND its not SKI, POW, or DEF
					 * THEN maximal is 500					 
					 */
					if (p_breed.statGains[statindex] == 2) {
						if((statindex != 1) && (statindex != 3) && (statindex != 5) ) {
							if (ret[statindex] > 500) {
								ret[statindex] = 499;
								break;
							}
						}
					}
					/**
					 * IF statgains is 1 AND its not SKI, POW, or DEF
					 * THEN maximal is 250					 
					 */
					if (p_breed.statGains[statindex] == 1) {
						if((statindex != 1) && (statindex != 3) && (statindex != 5) ) {
							if (ret[statindex] > 500) {
								ret[statindex] = 249;
								break;
							}
						}
					}
					if (ret[statindex] == 1000) {
						ret[statindex] = 999;
						break;
					}
					continue;
				} else {
					ret[statindex] -= 20;
					break;
				}
			}
		}
		
		return ret;
	}

	private static boolean contains(int[] optimumOrder, int statType) {
		for (int i : optimumOrder) {
			if (i == statType) {
				return true;
			}
		}
		return false;
	}

	private static boolean fulfill(int[] statOrder, Monster monster, int step) {
		int[] so = monster.getCorrectedStatsOrder(monster.correctStats());
		for (int i = 0; i < step; i++) {
			if(so[i] != statOrder[i]) {
				return false;
			}
		}
		return true;
	}

	private static int sum(int[] maxStats) {
		int ret = 0;
		for (int i :maxStats) {
			ret += i;
		}
		return ret;
	}

	private static void displayStats(int[] stats, int indent) {
		for (int i = 0; i < 6; i++) {
			o(indent, Constants.STAT_NAMES[i] + " = " + stats[i]);
		}
	}

	private static HashSet<Breed> getPotentialOffsprings(Breed parentBreed) {
		HashSet<Breed> ret = new HashSet<>();
		ret.addAll(BREEDS_BY_TYPE.get(parentBreed.main));
		ret.addAll(BREEDS_BY_TYPE.get(parentBreed.sub));
		
		ret.remove(parentBreed);
		
		Breed parentLeft = BREEDS.get(breedKey(parentBreed.main, parentBreed.main));
		if (parentLeft != null) {
			BREEDS.remove(parentLeft);
		}
		
		Breed parentRight = BREEDS.get(breedKey(parentBreed.sub, parentBreed.sub));
		if (parentRight != null) {
			BREEDS.remove(parentLeft);
		}

		Breed parentReverse = BREEDS.get(breedKey(parentBreed.sub, parentBreed.main));
		if (parentReverse != null) {
			BREEDS.remove(parentReverse);
		}
		
		return ret;
	}

	static class OffspringScore implements Comparable<OffspringScore>{
		Breed breed;
		int score;
		public OffspringScore(Breed a1, int a2) {
			breed  = a1;
			score = a2;
		}
		@Override
		public int compareTo(OffspringScore o) {
			return - this.score + o.score;
		}
	}
	
//	ape/ape                    | 150 | 160 | 20  | 120 | 100 | 140 | p l d sk sp i
//	ape/gali                   | 120 | 140 | 110 | 100 | 90  | 130 | p d l i sk sp
//	ape/golem                  | 150 | 160 | 70  | 100 | 90  | 140 | p l d sk sp i
//	ape/hare                   | 160 | 140 | 60  | 110 | 150 | 120 | l sp p d sk i
//	ape/plant                  | 140 | 120 | 90  | 110 | 100 | 130 | l d p sk sp i
//	arrowhead/arrowhead        | 120 | 80  | 70  | 30  | 40  | 170 | d l p i sp sk
//	arrowhead/durahan          | 130 | 120 | 80  | 110 | 100 | 170 | d l p sk sp i
//	arrowhead/golem            | 120 | 150 | 70  | 50  | 60  | 190 | d p l i sp sk
	
	private static void processFiles() {
		File bsfile = new File(System.getProperty("user.home") + "\\OneDrive\\Mr2Combine\\basestats.txt");
		File sgfile = new File(System.getProperty("user.home") + "\\OneDrive\\Mr2Combine\\statgains.txt");
		
		try (BufferedReader br = new BufferedReader(new FileReader(bsfile));
				BufferedReader br2 = new BufferedReader(new FileReader(sgfile));) {
			/**
			 * Base stats file
			 * 
			 */
			String line;
			while ((line = br.readLine()) != null) {
				String[] lineling = line.split("\\|");
				String[] breeds   = lineling[0].trim().split("\\/");
				
				Breed breed = new Breed();
				breed.main  = breeds[0].trim();
				breed.sub   = breeds[1].trim();
				
				String[] statorder = lineling[7].trim().split("\\h");
				
				for (int i = 0; i < 6; i++) {
					int statindex = Constants.STATS_BY_ABBR.get(statorder[i]);
					breed.baseStatOrder[i] = statindex;
					breed.baseStatOrder_inverse[statindex] = i;
				}
				
				
				BREEDS.put(breedKey(breed.main, breed.sub), breed);
				
				// put main
				HashSet<Breed> mainset = BREEDS_BY_TYPE.get(breed.main);
				if (mainset == null) {
					mainset = new HashSet<Breed>();
					BREEDS_BY_TYPE.put(breed.main, mainset);
				}
				mainset.add(breed);
				
				// put sub
				HashSet<Breed> subset = BREEDS_BY_TYPE.get(breed.sub);
				if (subset == null) {
					subset = new HashSet<Breed>();
					BREEDS_BY_TYPE.put(breed.sub, subset);
				}
				subset.add(breed);
			}
			
//			ape/ape              | 4 | 4 | 1 | 3 | 3 | 4 |19 | 500 
//			ape/gali             | 4 | 4 | 3 | 3 | 3 | 4 |21 | 440 
//			ape/golem            | 4 | 4 | 2 | 2 | 2 | 4 |18 | 480 
//			ape/hare             | 4 | 4 | 1 | 3 | 4 | 3 |19 | 460 
//			ape/plant            | 4 | 3 | 2 | 3 | 3 | 3 |18 | 520 
//			arrowhead/arrowhead  | 3 | 3 | 2 | 3 | 2 | 5 |18 | 500 
//			arrowhead/durahan    | 3 | 3 | 2 | 3 | 2 | 5 |18 | 500 
//			arrowhead/golem      | 3 | 4 | 2 | 2 | 2 | 5 |18 | 480 
//			arrowhead/henger     | 3 | 3 | 2 | 3 | 3 | 4 |18 | 460 
//			arrowhead/joker      | 3 | 3 | 3 | 4 | 2 | 4 |19 | 440 
//			arrowhead/mock       | 2 | 3 | 3 | 3 | 2 | 4 |17 | 520 
			
			/**
			 * Stat gains file
			 * 
			 */
			while ((line = br2.readLine()) != null) {
				String[] lineling = line.split("\\|");
				
				String[] mainsub = lineling[0].trim().split("\\/");
				String main = mainsub[0];
				String sub =  mainsub[1];
				
				Breed breed = BREEDS.get(breedKey(main, sub));
				if (breed == null) {
					System.out.format("Warning : Breed %s/%s not found in basestats file%n", main, sub);
					continue;
				}
				
				for (int i = 0; i < 6; i++) {
					breed.statGains[i] = Integer.parseInt(lineling[i + 1].trim());
				}
				
				breed.lifespan = Integer.parseInt(lineling[8].trim());
			}
			
			/**
			 * Clearing up...
			 * 
			 */
			
			System.out.print("\n");
			ArrayList<Breed> deletes = new ArrayList<>();
			for (Entry<String, Breed> e : BREEDS.entrySet()) {
				Breed breed = e.getValue();
				if (breed.statGains[0] == 0) {
					System.out.format("Warning : Breed %s still doesn't have statgains%n", breed.toString());
					deletes.add(breed);
				}
			}
			
			System.out.format("%ndeleting incomplete datas, current size = %s%n", BREEDS.size());
			for (Breed delete : deletes) {
				BREEDS.remove(breedKey(delete.main, delete.sub));
				
				HashSet<Breed> mainset = BREEDS_BY_TYPE.get(delete.main);
				mainset.remove(delete);
				if (mainset.size() == 0) {
					BREEDS_BY_TYPE.remove(delete.main);
				}

				HashSet<Breed> subset = BREEDS_BY_TYPE.get(delete.sub);
				subset.remove(delete);
				if (subset.size() == 0) {
					BREEDS_BY_TYPE.remove(delete.sub);
				}
				
			}
			System.out.format("deleting incomplete datas done, current size = %s%n", BREEDS.size());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String breedKey(String main, String sub) {
		return main + "/" + sub;
	}
	
	public static int[] matches(Breed b, int[] statOrder) {
		int[] al = new int[6];
		for (int i = 0; i < 6; i++) {
			if (b.baseStatOrder[i] == statOrder[i]) {
				al[i] = b.baseStatOrder[i];
			} else {
				al[i] = -1;
			}
		}
		
		return al;
	}
	
	public static void o(Object o) {
		System.out.println(o);
	}
	
	public static void o(int i, Object o) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < i; j++) {
			sb.append("  ");
		}
		sb.append(o);
		System.out.println(sb);
	}
	
	public static void o() {
		System.out.println();
	}
}
