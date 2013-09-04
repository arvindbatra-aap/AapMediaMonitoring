package org.aap.monitoring.pagecleaner;

import java.net.URL;
import de.l3s.boilerpipe.extractors.DefaultExtractor;

public class ContentExtractor 
{
    public static void main( String[] args ) throws Exception
    {
    	final URL url = new URL(args[0]);
    	System.out.println(DefaultExtractor.INSTANCE.getText(url));
    }
}
