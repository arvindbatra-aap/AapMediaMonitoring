package org.aap.monitoring.pagecleaner;


import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URL;
import de.l3s.boilerpipe.extractors.ArticleExtractor;


@Path("/api/")
public class APIResource {
	protected static final Logger logger = Logger.getLogger(APIResource.class.getName());
	
	public static Map<String, String> getQueryParams(UriInfo ui) {
		Map<String, String> qParams = new HashMap<String, String>();
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for (String key : queryParams.keySet()) {
			if (queryParams.get(key).size() == 1) {
				qParams.put(key, queryParams.getFirst(key));
			} else {
				qParams.put(key, queryParams.get(key).toArray().toString());
			}
		}
		return qParams;
	}
	
	@Path("/content")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getEventsForInterest(@Context UriInfo ui) {
		Map<String, String> queryParams = getQueryParams(ui);
		for (String key : queryParams.keySet()) {
			logger.info(key + "\t" +  queryParams.get(key));
		}
		
		String status = "success";
		String content = null;
		String url = null;
		
		if(queryParams.containsKey("url")) {
			url = queryParams.get("url");
		}
		
		try {
			final URL urlObject = new URL(url);
			content = ArticleExtractor.INSTANCE.getText(urlObject);
		}
		catch(Exception e) {
			e.printStackTrace();
			status = "error";
		}
    	
    	if (content == null) {
    		status = "error";
		}
	
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonNode = mapper.createObjectNode();
		
		jsonNode.put("url", url);
		jsonNode.put("status",status);
		if(content != null) {
			jsonNode.put("content", content);
		}
		
		return jsonNode.toString();
	}	
}
