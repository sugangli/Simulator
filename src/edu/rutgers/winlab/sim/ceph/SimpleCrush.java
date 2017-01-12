package edu.rutgers.winlab.sim.ceph;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SimpleCrush {


	private static HashMap<Integer, List<String>> DefaultMap = new HashMap<Integer, List<String>>();
	private static int[] PGs;
	private int PG_Size;
	private HashMap<String, OSD> OSDs;

	public static HashMap<Integer, List<String>> getDefaultMap() {
		return DefaultMap;
	}

	public static void setDefaultMap(HashMap<Integer, List<String>> defaultMap) {
		DefaultMap = defaultMap;
	}

	public int[] getPGs() {
		return PGs;
	}

	public void setPGs(int[] pGs) {
		PGs = pGs;
	}
	public ArrayList<OSD> getPGByObjectId(long object_id){
		List<OSD> group = new ArrayList<OSD>();
		int pg_id = (int) (object_id % PG_Size);
		List<String> osd_ids = DefaultMap.get(pg_id);
		for(String s: osd_ids){
			group.add(OSDs.get(s));
		}
		return null;	
	}

	public SimpleCrush() {

	}
	public static List<List<String>> Crush(HashMap<String, OSD> osds, int numofPG, int groupsize){

		PGs = new int[numofPG];
		DefaultMap = new HashMap<Integer, List<String>>();

		List<String> input = new ArrayList<String>();
		for(Map.Entry<String, OSD> entry : osds.entrySet() ){
			input.add(entry.getKey());
		}


		List<List<String>> all_result = Permutation(input, groupsize);
		return all_result;
	}

	public static List<List<String>> ParallelCrush(HashMap<String, ParallelOSD> osds, int numofPG, int groupsize){

		PGs = new int[numofPG];
		DefaultMap = new HashMap<Integer, List<String>>();

		List<String> input = new ArrayList<String>();
		for(Map.Entry<String, ParallelOSD> entry : osds.entrySet() ){
			input.add(entry.getKey());
		}


		List<List<String>> all_result = Permutation(input, groupsize);
		return all_result;
	}

	public static List<String[]> Combination(List<String> input, int k){
		List<String[]> subsets = new ArrayList<>();

		int[] s = new int[k];                  // here we'll keep indices 
		// pointing to elements in input array

		if (k <= input.size()) {
			// first index sequence: 0, 1, 2, ...
			for (int i = 0; (s[i] = i) < k - 1; i++);  
			subsets.add(getSubset(input, s));
			for(;;) {
				int i;
				// find position of item that can be incremented
				for (i = k - 1; i >= 0 && s[i] == input.size() - k + i; i--); 
				if (i < 0) {
					break;
				} else {
					s[i]++;                    // increment this item
					for (++i; i < k; i++) {    // fill up remaining items
						s[i] = s[i - 1] + 1; 
					}
					subsets.add(getSubset(input, s));
				}
			}
		}
		return subsets;

	}
	// generate actual subset by index sequence
	public static String[] getSubset(List<String> input, int[] subset) {
		String[] result = new String[subset.length]; 
		for (int i = 0; i < subset.length; i++) 
			result[i] = input.get(subset[i]);
		return result;
	}


	public static void shuffleList(List<List<String>> a, int numofPG) {
		int n = a.size();
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		random.nextInt();
		for (int i = 0; i < n; i++) {
			int change = i + random.nextInt(n - i);
			swap(a, i, change);
		}
		for(int i = 0; i < numofPG; i++){
			DefaultMap.put(i, a.get(i));
			PGs[i] = i;
		}
	}

	private static void swap(List<List<String>> a, int i, int change) {
		List<String> helper = a.get(i);
		a.set(i, a.get(change));
		a.set(change, helper);
	}


	public static List<List<String>> Permutation(List<String> original, int group_size){

		List<List<String>> ret = new ArrayList<>();
		innerPermutation(original, new ArrayList<>(), ret, group_size);
		return ret;

	}

	private static void innerPermutation(List<String> original, List<String> target, List<List<String>> result, int remaining) {
		if (remaining == 0) {
			List<String> tmp = new ArrayList<>(target);
			result.add(tmp);
			return;
		} else {
			for (String str : original) {
				if (target.contains(str))
					continue;
				target.add(str);
				innerPermutation(original, target, result, remaining - 1);
				target.remove(str);
			}
		}
	}

	public static void  main(String[] args){



		//		List<String> ls  = new ArrayList<String>();
		//		ls.add("1");
		//		ls.add("2");
		//		ls.add("3");
		//		ls.add("4");
		//		
		//		List<List<String>> result = Permutation(ls, 3);

		int num_of_osd = 10;
		int num_of_PG = 100;
		HashMap<String, OSD> osds = new HashMap<String, OSD>();
		for (int i = 0; i < num_of_osd; i++){
			osds.put(Integer.toString(i), new OSD("" + i + ""));
		}	
		List<List<String>> finallist  = SimpleCrush.Crush(osds, num_of_PG , 3);
		SimpleCrush.shuffleList(finallist, num_of_PG);
		Iterator it = SimpleCrush.getDefaultMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			List<String> item = (ArrayList<String>) pair.getValue();
			System.out.println(pair.getKey() + " = " +item.toString());
			it.remove(); // avoids a ConcurrentModificationException
		}



	}





}
