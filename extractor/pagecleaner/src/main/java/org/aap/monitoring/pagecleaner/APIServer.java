package org.aap.monitoring.pagecleaner;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class APIServer {
    private static int getPort(int defaultPort) {
        String port = System.getProperty("jersey.test.port");
        if (null != port) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
            }
        }
        return defaultPort;        
    } 
    
    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://0.0.0.0/").port(getPort(2121)).build();
    }

    public static final URI BASE_URI = getBaseURI();

    protected static HttpServer startServer() throws IOException {
        System.out.println("Starting grizzly...");
        ResourceConfig rc = new PackagesResourceConfig("org.aap.monitoring.pagecleaner");
        JSONConfiguration.mapped().arrays("tags").build();
        return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
    }
    
    public static void main(String[] args) throws IOException {
        final HttpServer httpServer = startServer();
	
	// register shutdown hook
	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
	    public void run() {
            System.out.println("Stopping server..");
            httpServer.stop();
        }
	    }, "shutdownHook"));

	// run
	try {
	    httpServer.start();
	    System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nTry out %sapi\nHit CTRL^C to stop it...",
                BASE_URI, BASE_URI));

	    Thread.currentThread().join();
	} catch (Exception e) {
	    System.out.println(
			 "There was an error while starting Grizzly HTTP server. " + e);
	}
   }    
}
