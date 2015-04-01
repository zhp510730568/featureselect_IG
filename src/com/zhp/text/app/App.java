package com.zhp.text.app;

import java.io.IOException;
import java.util.List;

import com.zhp.text.features.FeatureExtraction_IG;
import com.zhp.text.features.FeatureStats;
import com.zhp.text.model.Document;
import com.zhp.text.util.TextUtil;

public class App {
	public static void main(String[] args) throws IOException {
		List<Document> corpusData = TextUtil.readCorpusLines("D:/corpus.txt");
		FeatureExtraction_IG ig = new FeatureExtraction_IG(corpusData);
		FeatureStats stats = ig.selectFeature(0.001);
		System.out.println(stats.featureCategoryJointCount.size());
	}
}
