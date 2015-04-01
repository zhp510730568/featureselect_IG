package com.zhp.text.features;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhp.text.model.Document;

public class FeatureExtraction_IG {
	// document list
	private List<Document> dataset;
	
	// save IG value of feature
	private Map<String, Double> featureValueMap = new  HashMap<>();

	// save selected feature by order
	private List<String> featureList = new LinkedList<>();
	
    /**
     * structure function
     * 
     * @param dataset document list
     * @return 
     */
	public FeatureExtraction_IG(List<Document> dataset) {
		this.dataset = dataset;
	}
	
    /**
     * select feature by the value of feature IG value by order
     * 
     * @param retainPercent
     * @return 
     */
	public FeatureStats selectFeature(double retainPercent) {
		FeatureStats stats = extractFeatureStats(this.dataset);
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;
		int docCount = stats.n;
		int totalFeatureCount = stats.featureCategoryJointCount.size();
		int criticalCount = (int)(totalFeatureCount * retainPercent);
		int progress = 0;
		for(Map.Entry<String, Map<String, Integer>> entry1: stats.featureCategoryJointCount.entrySet()) {
			String feature = entry1.getKey();
			Map<String, Integer> cateCounts = entry1.getValue();
			double featureCount = stats.featureCounts.get(feature) * 1.0;
			// P(W)的概率
			double P_W = featureCount / docCount;
			// P(/W)的概率
			double P__W = 1.0 - P_W;
			// feature IG value
			double IG = 0.0;
			double IG1 = 0.0;
			double IG2 = 0.0;
			for(Map.Entry<String, Integer> entry2: stats.categoryCounts.entrySet()) {
				String cate = entry2.getKey();
				int cateCount = entry2.getValue();
				double featureCateCount = 0.0;
				if(cateCounts.containsKey(cate)) {
					featureCateCount = cateCounts.get(cate) * 1.0;
				}
				double P_Ct = cateCount * 1.0 / docCount;
				
				double IG_W1 =  featureCateCount / featureCount;
				if(IG_W1 > 0.0) {
					IG1 += P_W * IG_W1 * Math.log(IG_W1 / P_Ct);
				}
				double IG_W2 =  (cateCount - featureCateCount) / (docCount - featureCount);
				if(IG_W2 > 0.0) {
					IG2 += P__W * IG_W2 * Math.log(IG_W2 / P_Ct);
				}
			}
			IG = IG1 + IG2;
			if(IG > maxValue) {
				maxValue = IG;
			}
			if(IG < minValue) {
				minValue = IG;
			}

			featureValueMap.put(feature, IG);
			insertListByOrder(criticalCount, feature);
			if(progress % 10000 == 0) {
				System.out.println("当前处理数量:" + progress);
			}
			++progress;
		}
		// 把特征转到集合里，方便下面过滤
		Set<String> featureBag = new HashSet<>();
		for(String tmp: featureList) {
			featureBag.add(tmp);
		}
		// release Object
		featureList = null;
		// remove feature
        Iterator<Map.Entry<String, Map<String, Integer>>> it = stats.featureCategoryJointCount.entrySet().iterator();
        while(it.hasNext()) {
            String feature = it.next().getKey();
            if(featureBag.contains(feature)==false) {
                //if the feature is not in the selectedFeatures list remove it
                it.remove();
            }
        }
		System.out.println("特征选择数:" + criticalCount);
		System.out.println("最大值:" + maxValue);
		System.out.println("最小值:" + minValue);
		return stats;
	}
	
    /**
     * use binary search method to find inserting index, and then insert the feature in the position
     * 
     * @param criticalCount
     * @param feature
     * @return 
     */
	private void insertListByOrder(int criticalCount, String feature) {
		if(featureList == null) {
			return;
		}
		int listLen = featureList.size();
		if(listLen <= 0) {
			featureList.add(feature);
			return;
		}
		int insertIndex = -1;
		double IG = featureValueMap.get(feature);
		double header_IG = featureValueMap.get(featureList.get(0));
		double tail_IG = featureValueMap.get(featureList.get(listLen - 1));
		if(listLen >= criticalCount) {
			if(IG > tail_IG) {
				featureList.remove(listLen - 1);
			} else {
				return;
			}
		}
		if(IG > header_IG) {
			featureList.add(0, feature);
			return;
		}
		if(IG < tail_IG) {
			featureList.add(listLen, feature);
			return;
		}
		
		insertIndex = insertIndex(0, listLen - 1, feature);

		featureList.add(insertIndex, feature);
	}
	
    /**
     * use recursion to find the insertion position
     * 
     * @param header 
     * @param tail
     * @param feature
     * @return 
     */
	private int insertIndex(int header, int tail, String feature) {
		int middle = (header + tail) / 2;
		double IG = featureValueMap.get(feature);
		double middle_IG = featureValueMap.get(featureList.get(middle));
		if(middle == header) {
			return header + 1;
		}
		if(IG > middle_IG) {
			return insertIndex(header, middle, feature);
		} else {
			return insertIndex(middle, tail, feature);
		}
	}
	
    /**
     * Generates a FeatureStats Object with metrics about he occurrences of the
     * keywords in categories, the number of category counts and the total number 
     * of observations. These stats are used by the feature selection algorithm.
     * 
     * @param dataset
     * @return 
     */
	public FeatureStats extractFeatureStats(List<Document> dataset) {
        FeatureStats stats = new FeatureStats();
        // 文档总数
        int docCount = 0;
        // 保存特征所出现的各类别文章数量
		Map<String, Map<String, Integer>> featureToCategMap = new HashMap<String, Map<String, Integer>>();
		// 保存文档中的词
		Set<String> tokenBag = new HashSet<>();
		// 保存各类别文章数量
		Map<String, Integer> categoryCounts = new HashMap<String, Integer>();
		// 保存各特征文章数量
		Map<String, Integer> featureCounts = new HashMap<String, Integer>();
		
		for(Document doc: dataset) {
			String cate = doc.category;
			docCount++;
			// 统计各类别文章数量
			Integer categDocCount = categoryCounts.get(cate);
			if(categDocCount == null) {
				categoryCounts.put(cate, 1);
			} else {
				categoryCounts.put(cate, ++categDocCount);
			}
			tokenBag.clear();
			// 统计各文档特征
			for(Map.Entry<String, Integer> entry : doc.tokens.entrySet()) {
				String tokenName = entry.getKey();
				if(!tokenBag.contains(tokenName)) {
					tokenBag.add(tokenName);
					// 统计各特征对应文章数
					Integer featureCount = featureCounts.get(tokenName);
					if(featureCount == null) {
						featureCounts.put(tokenName, 1);
					} else {
						featureCounts.put(tokenName, ++featureCount);
					}
					// 统计特征对应的各类别文章数
					Map<String, Integer> categCountMap = featureToCategMap.get(tokenName);
					if(categCountMap == null) {
						categCountMap = new HashMap<String, Integer>();
						categCountMap.put(cate, 1);
						featureToCategMap.put(tokenName, categCountMap);
					} else {
						Integer categCount = categCountMap.get(cate);
						if(categCount != null) {
							categCountMap.put(cate, ++categCount);
						} else {
							categCountMap.put(cate, 1);
						}
					}
				}
			}
		}
		
		stats.n = docCount;
		stats.categoryCounts = categoryCounts;
		stats.featureCounts = featureCounts;
		stats.featureCategoryJointCount = featureToCategMap;
		System.out.println(featureToCategMap.size());
		
		return stats;
	}
}
