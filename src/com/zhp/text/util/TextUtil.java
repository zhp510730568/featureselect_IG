package com.zhp.text.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zhp.text.features.TextTokenizer;
import com.zhp.text.model.Document;

public class TextUtil {
    /**
     * Reads the all lines from a corpus file and places it a List<Document>.
     * Every document is in a line, and its format is "classID \t content", and the content is splitted by space. 
     * 
     * @param path corpus file path
     * @return
     * @throws IOException 
     */
    public static List<Document> readCorpusLines(String path) throws IOException {
    	
        Reader fileReader = new InputStreamReader(new FileInputStream(path), Charset.forName("UTF-8"));
        Map<String, List<String>> corpusMap = new HashMap<String, List<String>>();

        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
            	line = line.trim();
            	String[] arr = line.split("\t", 2);
            	if(arr.length == 2) {
            		String classID = arr[0];
            		String content = arr[1];
            		List<String> artiList = corpusMap.get(classID);
            		if(artiList == null) {
            			List<String> tempArtiList = new ArrayList<>();
            			tempArtiList.add(content);
            			corpusMap.put(classID, tempArtiList);
            		} else {
            			artiList.add(content);
            		}
            	}
            }
        }
        fileReader.close();
        
        //loading examples in memory
        Map<String, String[]> corpusData = new HashMap<>();
        String[] artiArr;
        for(Map.Entry<String, List<String>> entry : corpusMap.entrySet()) {
        	String classID = entry.getKey();
        	List<String> artiList = entry.getValue();
        	artiArr = artiList.toArray(new String[artiList.size()]);
        	corpusData.put(classID, artiArr);
        }
        return preprocessDataset(corpusData);
    }
    
    /**
     * Preprocesses the original dataset and converts it to a List of Documents.
     * 
     * @param trainingDataset
     * @return 
     */
    private static List<Document> preprocessDataset(Map<String, String[]> trainingDataset) {
        List<Document> dataset = new ArrayList<>();
                
        String category;
        String[] examples;
        
        Document doc;
        
        Iterator<Map.Entry<String, String[]>> it = trainingDataset.entrySet().iterator();
        
        while(it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            category = entry.getKey();
            examples = entry.getValue();
            
            for(int i=0;i<examples.length;++i) {
                doc = TextTokenizer.tokenize(examples[i]);
                doc.category = category;
                dataset.add(doc);
            }
        }
        
        return dataset;
    }
}
