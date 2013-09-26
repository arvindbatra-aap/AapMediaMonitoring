package org.aap.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggerMain {
	
	private static Logger LOG = LoggerFactory.getLogger(TriggerMain.class);
	
	public static void main(String [] args) throws Exception {
		String date = args[0];
		LOG.info("Triggering from date " + date);
		SQLManager sqlManager = new SQLManager();
		sqlManager.triggerIndexer(date);
	}

}
