package com.mta.mtaSkill.api;

//import GetNextBusSpeechlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class MTAHerokuApi extends BaseAPIParser {

    private static Logger log = LoggerFactory.getLogger(MTAHerokuApi.class);

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    private static final String MILWAUKEE_HEROKU_API_URL_START = "https://mke-bus.herokuapp.com/get/all/predictions/where?route_id=";
    private static final String HEROKU_APP_ROUTES_API = "https://mke-bus.herokuapp.com/get/all/routes";

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

    public static List<Message> getPredictions(String routeId, String stopId) {
        List<Message> messageList = new ArrayList<>();

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
                String zone = arr.getJSONObject(i).getString("zone");
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

               
                System.out.println(individualMessage.toString());
                
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    		   DateTime predTime = formatter.parseDateTime(arrivalTime);
                  // formatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
    		  // DateTime nowTime = formatter.parseDateTime(System.c);
                    Date today = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    df.setTimeZone(TimeZone.getTimeZone("EST"));
                    String time = df.format(today);
                    
                    DateTime nowTime = formatter.parseDateTime(time);
                   // System.out.println(time);
                   
    		   int ETA=predTime.getMinuteOfDay()-nowTime.getMinuteOfDay();
    		   log.debug("estimate:"+ETA);
    		   individualMessage.setEstimate(ETA);
    		   // add it to the list
    		    messageList.add(individualMessage);
            }

        } catch (IOException ex) {
          log.info("IO Exception for getPredictions");
       } catch (JSONException ex) {
           log.info("JSON Exception in getPredictions");
       }

        return messageList;
    }


    protected static List<Message> parse(String apiString)
            throws IOException, SAXException, ParserConfigurationException {

        //Create a "parser factory" for creating SAX parsers
        SAXParserFactory spfac = SAXParserFactory.newInstance();

        //Now use the parser factory to create a SAXParser object
        SAXParser sp = spfac.newSAXParser();

        //Create an instance of this class; it defines all the handler methods
        TrueTimeHandler handler = new TrueTimeHandler();

        //Finally, tell the parser to parse the input and notify the handler
        sp.parse(apiString, handler);

        return handler.getMessages();

    }
    
    public static List<Message> getRoutes() {
       List<Message> messageList = new ArrayList<>();

       String JSON_URL = HEROKU_APP_ROUTES_API;
       JSONObject obj;
       try {
           obj = readJsonFromUrl(JSON_URL);

           JSONArray arr = obj.getJSONArray("with");

           for (int i = 0; i < arr.length(); i++) {
               Message individualMessage = new Message();
               String routeId = arr.getJSONObject(i).getString("id");

               individualMessage.setRouteID(routeId);
               String routeName = arr.getJSONObject(i).getString("name");
               individualMessage.setRouteName(routeName);
               String routeColor = arr.getJSONObject(i).getString("color");
               individualMessage.setRouteColor(routeColor);
               messageList.add(individualMessage);

           }

       } catch (IOException ex) {
          log.info("IO Exception for getRoutes");
       } catch (JSONException ex) {
           log.info("JSON Exception in getRoutes");
       }

       return messageList;
   }
}
