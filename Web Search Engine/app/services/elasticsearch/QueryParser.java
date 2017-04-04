package services.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * Created By: Nisha Narayanaswamy on 5/25/2016
 * 
 * Class will parse query string for keywords
 */

public class QueryParser {

	public static Map<String, String> parseQuery(String myQuery) {

		// insert keywords-value pairs for cities
		Map<String, ArrayList<String>> queryKeywords = new HashMap<String, ArrayList<String>>();
		ArrayList<String> sfoAlias = new ArrayList<String>();
		sfoAlias.addAll(Arrays.asList("san francisco", "sfo", "sf", "bay area",
				"north bay", "silicon valley", "san fran", "sanfrancisco","the golden city"));

		ArrayList<String> tahoeAlias = new ArrayList<String>();
		tahoeAlias.addAll(Arrays.asList("lake tahoe", "tahoe", "tahoe city",
				"north lake tahoe", "south lake tahoe", "squaw valley",
				"tahoe towns", "truckee", "squaw"));

		ArrayList<String> sdAlias = new ArrayList<String>();
		sdAlias.addAll(Arrays.asList("san diego", "sd", "north county island",
				"gaslamp quarter", "north county coastal"));

		ArrayList<String> laAlias = new ArrayList<String>();
		laAlias.addAll(Arrays.asList("los angeles", "la", "l.a"));
		ArrayList<String> montereyAlias = new ArrayList<String>();
		montereyAlias.addAll(Arrays.asList("monterey"));
		ArrayList<String> scAlias = new ArrayList<String>();
		scAlias.addAll(Arrays.asList("santa cruz"));
		ArrayList<String> oakAlias = new ArrayList<String>();
		oakAlias.addAll(Arrays.asList("oakland"));
		ArrayList<String> sbAlias = new ArrayList<String>();
		sbAlias.addAll(Arrays.asList("santa barbara"));
		ArrayList<String> sacAlias = new ArrayList<String>();
		sacAlias.addAll(Arrays.asList("sacramento"));
		ArrayList<String> lbAlias = new ArrayList<String>();
		lbAlias.addAll(Arrays.asList("long beach"));
		ArrayList<String> fresnoAlias = new ArrayList<String>();
		fresnoAlias.addAll(Arrays.asList("fresno"));

		queryKeywords.put("san francisco", sfoAlias);
		queryKeywords.put("tahoe", tahoeAlias);
		queryKeywords.put("san diego", sdAlias);
		queryKeywords.put("los angeles", laAlias);
		queryKeywords.put("monterey", montereyAlias);
		queryKeywords.put("santa cruz", scAlias);
		queryKeywords.put("oakland", oakAlias);
		queryKeywords.put("santa barbara", sbAlias);
		queryKeywords.put("sacramento", sacAlias);
		queryKeywords.put("long beach", lbAlias);
		queryKeywords.put("fresno", fresnoAlias);

		// insert key-value pairs for geo-distance
		ArrayList<String> metricTypes = new ArrayList<String>();
		metricTypes.addAll(Arrays.asList("miles", "kilometers", "mi", "km"));

		// Split at one or more spaces OR spaces followed
		// by comma and/or spaces OR non-word chars followed by spaces
		String[] splitQuery = myQuery.trim().split("\\s+|\\s*,\\s*|\\W\\s*");

		// data structure to store results
		// fixed key values - "cities", "distance"
		Map<String, String> parsedResults = new HashMap<String, String>();

		// results for cities
		String listCity = "";
		for (Map.Entry<String, ArrayList<String>> entry : queryKeywords
				.entrySet()) {
			// compare query with each alias value in Map entry
			String key = entry.getKey();
			for (String aliasValue : entry.getValue()) {
				if (myQuery.toLowerCase().contains(aliasValue)) {
					listCity = listCity + "," + key; // store key if query contains alias
					break;
				}
			}
		}
		if(listCity!=null && !listCity.equals(""))
			parsedResults.put("cities", listCity.substring(1));	//start from index 1 to ignore initial null value

		// results for distance
		for (int i = 0; i < splitQuery.length; i++) {
			if ((splitQuery[i].matches("\\d+"))
					&& (metricTypes.contains(splitQuery[i + 1]))) { // if number followed by distance metric
				if(splitQuery[i+1].equals("mi"))
					parsedResults.put("distance", splitQuery[i] + " " + "miles");
				else if(splitQuery[i+1].equals("km"))
					parsedResults.put("distance", splitQuery[i] + " " + "kilometers");
				else
					parsedResults.put("distance", splitQuery[i] + " " + splitQuery[i+1]);
				i++;
			}
		}
		return parsedResults;
	}

}
