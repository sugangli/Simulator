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


	private static HashMap<Integer, List<String>> DefaultMap;
	private int[] PGs;
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

	public SimpleCrush(HashMap<String, OSD> osds, int numofPG, int groupsize) {// place all OSDs in the exp into the PGs
		OSDs = osds;
		PGs = new int[numofPG];
		PG_Size = numofPG;
		DefaultMap = new HashMap<Integer, List<String>>();
		//		String[] input = new String[osds.size()];
		//		 Iterator it = OSDs.entrySet().iterator();

		List<String> input = new ArrayList<String>();
		for(Map.Entry<String, OSD> entry : OSDs.entrySet() ){
			input.add(entry.getKey());
		}

		List<List<String>> all_result = generatePerm(input);
		List<List<String>> final_result = getnPr(all_result, groupsize);
//		shuffleList(final_result);
		for(int i = 0; i < numofPG; i++){
			DefaultMap.put(i, final_result.get(i));
			PGs[i] = i;
		}
		//		
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


	public static void shuffleList(List<List<String>> a) {
		int n = a.size();
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		random.nextInt();
		for (int i = 0; i < n; i++) {
			int change = i + random.nextInt(n - i);
			swap(a, i, change);
		}
	}

	private static void swap(List<List<String>> a, int i, int change) {
		List<String> helper = a.get(i);
		a.set(i, a.get(change));
		a.set(change, helper);
	}

	public static List<List<String>> generatePerm(List<String> original) {
		if (original.size() == 0) { 
			List<List<String>> result = new ArrayList<List<String>>();
			result.add(new ArrayList<String>());
			return result;
		}
		String firstElement = original.remove(0);
		List<List<String>> returnValue = new ArrayList<List<String>>();
		List<List<String>> permutations = generatePerm(original);
		for (List<String> smallerPermutated : permutations) {
			for (int index=0; index <= smallerPermutated.size(); index++) {
				List<String> temp = new ArrayList<String>(smallerPermutated);
				temp.add(index, firstElement);
				returnValue.add(temp);
			}
		}
		return returnValue;
	}

	public static List<List<String>> getnPr(List<List<String>> allpermutation, int r){
		List<List<String>> new_list = new ArrayList<List<String>>();
		for (List<String> item : allpermutation){
			List<String> tl = new ArrayList<String>();
			for (int i = 0; i< r; i++){
				tl.add(item.get(i));
			}
			new_list.add(tl);
		}

		Set<List<String>> s = new LinkedHashSet<>(new_list);
		new_list.clear();
		new_list.addAll(s);
		return new_list;
	}

	public static void  main(String[] args){

//		int num_of_osd = 10;
//		int num_of_PG = 100;
//		HashMap<String, OSD> osds = new HashMap<String, OSD>();
//		for (int i = 0; i < num_of_osd; i++){
//			osds.put(Integer.toString(i), new OSD("" + i + ""));
//		}	
//		SimpleCrush sc = new SimpleCrush(osds, num_of_PG , 3);
//		Iterator it = sc.getDefaultMap().entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry pair = (Map.Entry)it.next();
//			List<String> item = (ArrayList<String>) pair.getValue();
//			System.out.println(pair.getKey() + " = " +item.toString());
//			it.remove(); // avoids a ConcurrentModificationException
//		}

		List<String> sl = new ArrayList<String>();
		sl.add("12");
		sl.add("22");
		sl.add("33");
		sl.add("44");
		
		List<String[]> result = SimpleCrush.Combination(sl, 3);
		for (String[] item: result){
			String[] arr = (String[]) item;
			System.out.println(Arrays.toString(arr));
		}
		
	}





}
