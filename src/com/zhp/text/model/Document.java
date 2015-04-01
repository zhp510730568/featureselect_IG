package com.zhp.text.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Document Object represents the texts that we use for training or 
 * prediction as a bag of words.
 * 
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
