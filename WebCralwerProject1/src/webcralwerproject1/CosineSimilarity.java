package webcralwerproject1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Measures the Cosine similarity of two vectors of an inner product space and
 * compares the angle between them.
 */
public class CosineSimilarity {

    /**
     * Calculates the cosine similarity for two given vectors.
     *
     * @param leftVector left vector
     * @param rightVector right vector
     * @return cosine similarity between the two vectors
     */
    public Double cosineSimilarity(Map<CharSequence, Integer> leftVector, Map<CharSequence, Integer> rightVector) {
        if (leftVector == null || rightVector == null) {
            throw new IllegalArgumentException("Vectors must not be null");
        }

        Set<CharSequence> intersection = getIntersection(leftVector, rightVector);
        double dotProduct = dot(leftVector, rightVector, intersection);
        
        double d1 = 0.0d;
        for (Integer value : leftVector.values()) {
            d1 += Math.pow(value, 2);
        }
        double d2 = 0.0d;
        for (Integer value : rightVector.values()) {
            d2 += Math.pow(value, 2);
        }
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
        } else {
            cosineSimilarity = (double) (dotProduct / (double) (Math.sqrt(d1) * Math.sqrt(d2)));
        }
        return cosineSimilarity;
    }

    /**
     * Returns a set with strings common to the two given maps.
     *
     * @param leftVector left vector map
     * @param rightVector right vector map
     * @return common strings
     */
    private Set<CharSequence> getIntersection(Map<CharSequence, Integer> leftVector,
            Map<CharSequence, Integer> rightVector) {
        Set<CharSequence> intersection = new HashSet<CharSequence>(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }

    /**
     * Computes the dot product of two vectors. It ignores remaining elements. It means
     * that if a vector is longer than other, then a smaller part of it will be used to compute
     * the dot product.
     *
     * @param leftVector left vector
     * @param rightVector right vector
     * @param intersection common elements
     * @return the dot product
     */
    private double dot(Map<CharSequence, Integer> leftVector, Map<CharSequence, Integer> rightVector,
            Set<CharSequence> intersection) {
        long dotProduct = 0;
        for (CharSequence key : intersection) {
            dotProduct += leftVector.get(key) * rightVector.get(key);
        }
        return dotProduct;
    }
    
    private Map<CharSequence, Integer> createMapfromText (String filepath){
    	Map<CharSequence, Integer> map = new HashMap<CharSequence, Integer>();
    	String line;
        BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filepath));
			while ((line = reader.readLine()) != null){
	        	String[] parts = line.split(" ", 2);
	            if (parts.length >= 2)
	            {
	                String key = parts[0];
	                int value = Integer.parseInt(parts[1]);
	                map.put(key, value);
	            } else {
	                System.out.println("ignoring line: " + line);
	            }
	        
	        }
		} catch (FileNotFoundException e) {
			System.out.println("Inside CreateMapfromText: " + e);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("Inside CreateMapfromText: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    	return map;
    	
    }
    
    public static void main (String [] args){
    	
    	CosineSimilarity cos = new CosineSimilarity();
    	//calling the constructor of EvaluatePart2 will write the output txt files for content and gold
    	EvaluatePart2 ev = new EvaluatePart2();
    	
    	//Now we set up the base work for reading the output txt word frequencies and computing cosine similarity for them
    	String file1 = "./crawler1output.txt";
    	String file2 = "./crawlergoldoutput.txt";
    	Map<CharSequence, Integer> content = new HashMap<CharSequence, Integer>();
    	Map<CharSequence, Integer> gold = new HashMap<CharSequence, Integer>();
    	content = cos.createMapfromText(file1);
    	gold = cos.createMapfromText(file2);
    	//Printing Cosine similarity for content and gold word frequencies
    	System.out.println("Cosine Similarity between " + file1 + " and " + file2 + "is: " + cos.cosineSimilarity(content, gold) );

    	//Now calling other Evaluation metrics like precision, recall, F1 and score based on LCS
    	ev.evaluateContentProcessingStep();//may give memory heap error
    }

}