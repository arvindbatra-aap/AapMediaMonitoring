package org.aap.monitoring.pagecleaner;


import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.net.MalformedURLException;
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
	public String getContentFromUrl(@Context UriInfo ui) {
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

	@Path("/content")
	@POST
	@Consumes("text/plain")
	@Produces(MediaType.APPLICATION_JSON)
	public String getContentFromHTML(String jsonInputStr) {
		logger.info("Serving POST request for input: " + jsonInputStr);

		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead

		String rawHTML = null;
		String url = null;

		try {
			JsonParser jp = factory.createJsonParser(jsonInputStr);
			JsonNode inputJSON = mapper.readTree(jp);
			if(inputJSON.has("content")) {
				rawHTML = inputJSON.get("content").asText();
			}
			if(inputJSON.has("url")) {
				url = inputJSON.get("url").asText();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			String status = "error: unable to parse input json";
			return getJsonWithStatus(status).toString();
		}

		String content = null;

		if(rawHTML != null) {
			try {
				content = ArticleExtractor.INSTANCE.getText(rawHTML);
			}		
			catch(Exception e) {
				e.printStackTrace();
				String status = "error: boilerpipe exception";
				return getJsonWithStatus(status).toString();
			}
		}
		else if (url != null) {
			URL urlObject;
			try {
				urlObject = new URL(url);
				content = ArticleExtractor.INSTANCE.getText(urlObject);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				String status = "error: url parsing or boilerpipe exception";
				return getJsonWithStatus(status).toString();
			}	
		}
		
		if(content != null) {
			ObjectNode jsonNode = getJsonWithStatus("success");	
			jsonNode.put("content", content);
			return jsonNode.toString();
		}
		else {
			return getJsonWithStatus("error: no content found").toString();
		}
	}
	
	public ObjectNode getJsonWithStatus(String status) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonNode = mapper.createObjectNode();
		jsonNode.put("status",status);
		return jsonNode;
	}
}
