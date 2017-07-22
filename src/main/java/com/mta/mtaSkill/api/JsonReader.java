
package com.mta.mtaSkill.api;



import com.mta.mtaSkill.api.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author shparash
 */
public class JsonReader {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    private static String MILWAUKEE_HEROKU_API_URL_START = "https://mke-bus.herokuapp.com/get/all/predictions/where?route_id=";
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
    
    
    
    public static List<Message> getETA(String routeId, String stopId) {
        List<Message> messageList= new ArrayList<>();
        
        String JSON_URL = MILWAUKEE_HEROKU_API_URL_START + routeId + "&stop_id="
                + stopId;
        //JSONObject obj = readJsonFromUrl("https://mke-bus.herokuapp.com/get/all/predictions/where?route_id=GOL&stop_id=6486");
        JSONObject obj;
        try {
            obj = readJsonFromUrl(JSON_URL);
            
       
        JSONArray arr = obj.getJSONArray("with");
        
        for (int i = 0; i < arr.length(); i++) {
            Message individualMessage = new Message();
            String distanceAway = arr.getJSONObject(i).getString("distance_away");
            individualMessage.setDestination(distanceAway);
            String ta_block_id = arr.getJSONObject(i).getString("ta_block_id");
            individualMessage.setTaBLockID(ta_block_id);
            String arrivalTime = arr.getJSONObject(i).getString("arrival_time"); 
            individualMessage.setPredictionTime(arrivalTime);
            String route_id = arr.getJSONObject(i).getString("route_id"); 
            individualMessage.setRouteID(route_id);
            String zone =   arr.getJSONObject(i).getString("zone");
            individualMessage.setZone(zone);
            String destination = arr.getJSONObject(i).getString("destination");
            individualMessage.setDestination(destination);
            String is_delayed = arr.getJSONObject(i).getString("is_delayed");
            if ("false".equalsIgnoreCase(is_delayed)) { 
                individualMessage.setDelayed(false);
            } else {
              individualMessage.setDelayed(true);    
            }
                   
            String vehicle_id = arr.getJSONObject(i).getString("vehicle_id");
           // individualMessage.setVehicleID(vehicle_id);
            String direction = arr.getJSONObject(i).getString("direction");
            individualMessage.setDirection(direction);
            String ta_trip_id = arr.getJSONObject(i).getString("ta_trip_id");
            individualMessage.setTaTripID(ta_trip_id);
            
            messageList.add(individualMessage);
            System.out.println(individualMessage.toString());
        }
        
        } catch (IOException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       return  messageList;
    }
    public static void main(String[] args) throws IOException, JSONException {
        getETA("GOL","6486");
    }
}
