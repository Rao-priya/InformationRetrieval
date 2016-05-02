package webcralwerproject1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class EvaluatePart2 {
	HashMap<String, Integer> termfrequency = new HashMap<String, Integer>();//wordcount

	public EvaluatePart2(){
		Document doc1 = null;
		Document doc2 = null;
		String file1 = "./crawler" + "1" + "content.html";
		String file2 = "./crawler" + "gold.html";
		File input1 = new File(file1);
		File input2 = new File(file2);
		String processed = "";
		String gold = "";
		try {
			doc1 = Jsoup.parse(input1, "UTF-8");
			doc2 = Jsoup.parse(input2, "UTF-8");
			processed = doc1.text();
			gold = doc2.text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		this.countFrequency(processed);
		this.writeTermfrequency("./crawler1output.txt");
		this.countFrequency(gold);
		this.writeTermfrequency("./crawlergoldoutput.txt");
	}
	public void evaluateContentProcessingStep(){
		String file1 = "./crawler" + "1" + "content.html";
		String file2 = "./crawler" + "gold.html";
		File input1 = new File(file1);
		File input2 = new File(file2);
		String processed = "";
		String gold = "";
		try {
			Document doc1 = Jsoup.parse(input1, "UTF-8");
			Document doc2 = Jsoup.parse(input2, "UTF-8");
			processed = doc1.text();
			gold = doc2.text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}

	public double calcPrecision(int lcs, int extractionLength){
		double precision = 0;
		precision = (lcs*1.0)/extractionLength;
		return precision;

	}

	public double calcRecall(int lcs, int goldLength){
		double recall = 0;
		recall = (lcs*1.0)/goldLength;
		return recall;
	}

	public double calcF1(double precision, double recall){
		double f1 = 0;
		f1 = (2*precision*recall)/(precision+recall);
		return f1;
	}

	public double calcScore(int lcs, int extractionLength,int goldLength){
		return (lcs*1.0)/(extractionLength + goldLength - lcs);
	}

	
	public void countFrequency(String cfile) {
		//open processed file tokenize and count word frequency
		// if (cfile.length() != 0) { //fi not empty
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		String r = "!@).(':_|,-?/<>* ";
		StringTokenizer st = new StringTokenizer(cfile, "!@).(':_|,-?/<>*$%^!\" ");
		while (st.hasMoreTokens()) {
			String word = st.nextToken().toLowerCase().trim();
			//   word.replaceAll("\"", "");word.trim();
			word = word.replaceAll("[^\\w\\s]", "");
			word = word.replace("\"", "");
			Matcher m = p.matcher(word);
			boolean b = m.find();
			//  System.out.print(" "+word);
			if (!b) {
				if (termfrequency.containsKey(word)) {
					termfrequency.put(word, termfrequency.get(word) + 1);
				} else {
					termfrequency.put(word, 1);
				}
			} else {

				//   System.out.print(" "+word);
			}
		}



	}

	public void writeTermfrequency(String filename) {
		try {
			FileWriter f_write = new FileWriter(filename);
			BufferedWriter writer = new BufferedWriter(f_write);

			TreeMap<String, Integer> sortedMap = sortTermFreq(termfrequency);
			Set set = sortedMap.entrySet();
			// Get an iterator
			Iterator i = set.iterator();
			// Display elements
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				writer.write(me.getKey() + " " + me.getValue());
				writer.newLine();
			}
			writer.close();
			termfrequency.clear();
		} catch (Exception e) {
			System.out.println("Inside writerTermFrequency : " + e);
		}
	}

	public TreeMap<String, Integer> sortTermFreq(HashMap<String, Integer> termfreq) {
		//The comparator is used to sort the TreeMap by keys. 
		Comparator<String> comparator = new ValueComparator(termfreq);
		//Creating a TreeMap to enable sorting by keys in our termfreq hashmap
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(comparator);
		sortedMap.putAll(termfreq);
		return sortedMap;
	}

	class ValueComparator implements Comparator<String> {

		HashMap<String, Integer> map = new HashMap<String, Integer>();

		public ValueComparator(HashMap<String, Integer> map) {
			this.map.putAll(map);
		}

		@Override
		public int compare(String s1, String s2) {
			if (map.get(s1) >= map.get(s2)) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
