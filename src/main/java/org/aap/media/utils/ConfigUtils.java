package org.aap.media.utils;

import java.util.Properties;

public class ConfigUtils {
	
	
	public static int getConfigValueAsInt(Properties props, String key, int defaultValue) {
		String s = props.getProperty(key);
		if (s != null) {
			return Integer.parseInt(s);
		}
		return defaultValue;
		
	}
	
	public static String getConfigValueAsString(Properties props, String key, String defaultValue) {
		String s = props.getProperty(key);
		if (s != null) {
			return s;
		}
		return defaultValue;
		
		
	}

}
