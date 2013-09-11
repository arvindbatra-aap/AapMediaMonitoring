package org.aap.monitoring;

import java.util.Map;

public class ArticleCount {
	Map<String, Map<String, Integer>> countByDate;
	Map<String, Map<String, Integer>> countBySrc;
	
	public Map<String, Map<String, Integer>> getCountByDate() {
		return countByDate;
	}
	public void setCountByDate(Map<String, Map<String, Integer>> countByDate) {
		this.countByDate = countByDate;
	}
	public Map<String, Map<String, Integer>> getCountBySrc() {
		return countBySrc;
	}
	public void setCountBySrc(Map<String, Map<String, Integer>> countBySrc) {
		this.countBySrc = countBySrc;
	}
	
}
