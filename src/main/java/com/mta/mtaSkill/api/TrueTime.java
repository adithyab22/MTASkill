package com.mta.mtaSkill.api;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.mta.mtaSkill.util.Stop;

/**
 * Exposes methods of classes that use the True Time API
 * @author adambarson
 *
 */
public class TrueTime {
	
	/**
	 * Given a route and stationID, returns a list of all predicted arrival information for buses arriving at this station within the next 30 minutes.
	 * Message encapsulates information returned by the TrueTimeAPI and may be an error message if no prediction information.
	 * @param route The route
	 * @param stationID The station ID
	 * @return A list of predictions for a specific bus route arriving at a specific stop
	 */
	public static List<Message> generatePredictions(String route, String stationID ){
		return MTAHerokuApi.getPredictions(route, stationID);
	}
	
	/**
	 * Calls the TrueTimeAPI and gets all routes, or returns an error message if there was an issue connecting to the API.
	 * @return All True Time routes
	 */
	public static List<Message> generateRoutes() {
		return MTAHerokuApi.getRoutes();
	}
	
	
}
