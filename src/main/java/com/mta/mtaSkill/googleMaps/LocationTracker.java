/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mta.mtaSkill.googleMaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.util.OutputHelper;

import com.mta.mtaSkill.InvalidInputException;
import com.mta.mtaSkill.api.TrueTimeAPI;
import com.mta.mtaSkill.util.JsonUtils;
import com.mta.mtaSkill.util.Location;
import com.mta.mtaSkill.util.Stop;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 *
 * @author Adithya
 */
public class LocationTracker {

	private static Logger log = LoggerFactory.getLogger(LocationTracker.class);

    /**
     * Case 1: Request returns list of Coordinates
     * Proceed to Step 2
     * 
     * Case 2: Unable to understand source location
     * Ask user to try again.

     * 
     * @param json returned by striking the Google maps API
     * @param limit set limit to the number of places returned by the API
     * @return
     * @throws JSONException 
     */
	protected static List<Location> getLatLngDetails(JSONObject json, int limit) throws JSONException, InvalidInputException {
    	List<Location> output = new ArrayList<>();
    	
        JSONArray results = json.getJSONArray("results");
        log.debug("JSON Results Size={}",results.length());
        if (results.length() == 0) {
            throw new InvalidInputException("No results from JSON","I did not understand the source location, " + OutputHelper.LOCATION_PROMPT);
        }
        int numResultsToReturn=Math.min(limit, results.length());
        
        
        JSONObject result;
       	JSONObject location;

        for (int i = 0; i < numResultsToReturn; i++) {
        	result = results.getJSONObject(i);
        	
        	location = result.getJSONObject("geometry").getJSONObject("location");
        	Location c = new Location(
        			result.getString("name"),
        			location.getDouble("lat"),
        			location.getDouble("lng"),
        			result.getString("formatted_address"),
        			makeList(result.getJSONArray("types")));

        	output.add(c);
        }
        return output;
    }
    
	protected static List<String> makeList(JSONArray array) throws JSONException{
    	List<String>  output = new ArrayList<String>();
    	for (int i=0;i<array.length();i++){
    		output.add(array.getString(i));
    	}
    	return output;
    }
    
	/* MOVED FROM TRUETIMEAPI
	 * Note: this makes this method dependent on existence of TrueTimeAPI.
	 * Someday if needed, the URL constants can be passed in as parameters to workaround this*/
	
	/**
     * Gets list of stops for a route#
     * @param route
     * @param direction
     * @return
     * @throws IOException
     * @throws JSONException 
     */
       private static final String HEROKU_APP_STOPS_API = "https://mke-bus.herokuapp.com/get/all/stops/where?route_id=";
	protected static List<Stop> getStopsAsJson(String route, String direction) throws IOException, JSONException{
    	log.trace("getStopsAsJson: route={}, direction={}", route, direction);
    	List<Stop> messageList = new ArrayList<>();
            

       String JSON_URL = HEROKU_APP_STOPS_API+ route+"&direction="+direction.toUpperCase();
       
       JSONObject obj;
       try {
           obj = readJsonFromUrl(JSON_URL);

           JSONArray arr = obj.getJSONArray("with");

           for (int i = 0; i < arr.length(); i++) {
               String stopId = arr.getJSONObject(i).getString("id");
               System.out.println("stopId"+stopId);
               String stopName = arr.getJSONObject(i).getString("name");
               System.out.println("stopName"+stopName);
               double latitude = arr.getJSONObject(i).getDouble("latitude");
               
                System.out.println("latitude"+latitude);
               double longitude = arr.getJSONObject(i).getDouble("longitude");
               
                System.out.println("longitude"+longitude);
                Stop s = new Stop(stopId, stopName, latitude, longitude);
               messageList.add(s);

           }

       } catch (IOException ex) {
           log.info("IOException in getStopsAsJson");
       } catch (JSONException ex) {
          log.info("JSONException in  getStopsAsJson");
       }

       return messageList;
    }
        
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
   
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}
