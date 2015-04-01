package com.zhp.text.features;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.zhp.text.model.Document;

/**
 * TextTokenizer class used to tokenize the texts and store them as Document
 * objects.
 * 
 */
public class TextTokenizer {
    
    /**
     * Preprocess the text by removing punctuation, duplicate spaces and 
     * lowercasing it.
     * 
     * @param text
     * @return 
     */
    public static String preprocess(String text) {
        return text.replaceAll("\\p{P}", " ").replaceAll("\\s+", " ").toLowerCase(Locale.getDefault());
    }
    
    /**
     * A simple method to extract the keywords from the text. For real world 
     * applications it is necessary to extract also keyword combinations.
     * 
     * @param text
     * @return 
     */
    public static String[] extractKeywords(String text) {
        return text.split(" ");
    }
    
    /**
     * Counts the number of occurrences of the keywords inside the text.
     * 
     * @param keywordArray
     * @return 
     */
    public static Map<String, Integer> getKeywordCounts(String[] keywordArray) {
        Map<String, Integer> counts = new HashMap<>();
        
        Integer counter;
        for(int i=0;i<keywordArray.length;++i) {
            counter = counts.get(keywordArray[i]);
            if(keywordArray[i].equals("")) {
            	continue;
            }
            if(counter==null) {
                counter=0;
            }
            counts.put(keywordArray[i], ++counter); //increase counter for the keyword
        }
        
        return counts;
    }
    
    /**
     * Tokenizes the document and returns a Document Object.
     * 
     * @param text
     * @return 
     */
    public static Document tokenize(String text) {
        String preprocessedText = preprocess(text);
        String[] keywordArray = extractKeywords(preprocessedText);
        
        Document doc = new Document();
        doc.tokens = getKeywordCounts(keywordArray);
        return doc;
    }
}
