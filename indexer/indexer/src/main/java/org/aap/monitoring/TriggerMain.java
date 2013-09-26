package org.aap.monitoring;


public class TriggerMain {
	
	
	
	public static void main(String [] args) throws Exception {
		String date = args[0];
		System.out.println("Triggering from date " + date);
		SQLManager sqlManager = new SQLManager();
		sqlManager.triggerIndexer(date);
	}

}
