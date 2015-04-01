package com.zhp.text.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Document Object represents the texts that we use for training or 
 * prediction as a bag of words.
 * 
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 * @see <a href="http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/">http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/</a>
 */
public class Document {
    
    /**
     * List of token counts
     */
    public Map<String, Integer> tokens;
    
    /**
     * The class of the document
     */
    public String category;
    
    /**
     * Document constructor
     */
    public Document() {
        tokens = new HashMap<>();
    }
}
