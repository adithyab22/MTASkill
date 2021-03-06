package com.amazon.util;

import com.mta.mtaSkill.APIException;
import com.mta.mtaSkill.InvalidInputException;
import com.mta.mtaSkill.UnexpectedInputException;
import com.mta.mtaSkill.api.Message;
import com.mta.mtaSkill.api.TrueTime;
import com.mta.mtaSkill.googleMaps.GoogleMaps;
import com.mta.mtaSkill.storage.PaInputData;
import com.mta.mtaSkill.util.DirectionCorrector;
import com.mta.mtaSkill.util.Location;
import com.mta.mtaSkill.util.LocationCorrector;
import com.mta.mtaSkill.util.Route;
import com.mta.mtaSkill.util.RouteCorrector;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author brown
 *
 */
public class DataHelper {
	private static Logger log = LoggerFactory.getLogger(DataHelper.class);

	public static final String SESSION_OBJECT_NAME = "InputData";
	public static final String SKILL_CONTEXT_NAME = "SkillContext";
	
	// INTENTS
	public static final String ONE_SHOT_INTENT_NAME = "OneshotBusIntent";
	public static final String RESET_INTENT_NAME = "ResetBusIntent";
	public static final String ALL_ROUTES_INTENT_NAME = "AllBusRoutesIntent";
	public static final String ROUTE_INTENT_NAME = "RouteBusIntent";
	public static final String LOCATION_INTENT_NAME = "LocationBusIntent";
	public static final String DIRECTION_INTENT_NAME = "DirectionBusIntent";

	// SLOTS
	public static final String ROUTE_NAME = "Route_Name";
	public static final String ROUTE_ID = "Route";
	// public static final String NAME = "Route";
	public static final String LOCATION = "Location";
	public static final String LAT = "lat";
	public static final String LONG = "long";
	public static final String ADDRESS = "address";
	public static final String DIRECTION = "Direction";

	public static final String LAST_QUESTION = "LastQuestion";

	
	
	public static void addLocationToConversation(PaInputData data, SkillContext skillContext, String location) throws InvalidInputException {
		// Find address for location
		try {
			location=LocationCorrector.getLocation(location);
			log.info("putting value in session Slot Location:" + location);
			
			Location c = GoogleMaps.findSourceLocation(location);
			
			data.setLocation(c);
			data.setLocationName(c.getName());
			data.setLocationLat("" + c.getLat());
			data.setLocationLong("" + c.getLng());
			data.setLocationAddress(c.getAddress());
			
			if (!c.isAddress()) {
				skillContext.setFeedbackText("I found " + c.getName() + " at " + c.getStreetAddress() + ". ");
			}

		} catch (JSONException jsonE) {
			throw new InvalidInputException("No Location in Intent", jsonE,
					"Please repeat your location. " + OutputHelper.LOCATION_PROMPT);
		} catch (IOException ioE) {
			throw new InvalidInputException("Cannot reach Google Maps ", ioE,
					"Please repeat your location. " + OutputHelper.LOCATION_PROMPT);
		} catch (UnexpectedInputException e) {
			e.printStackTrace();
		}
	}
	
	public static void addRouteToConversation(PaInputData data, SkillContext skillContext, String routeID) throws InvalidInputException {
		Route route;
		try {
			routeID = RouteCorrector.getRoute(routeID);

			route = getMatchedRoute(routeID);
			log.info("putting value in session Slot " + DataHelper.ROUTE_ID+" : "+route.getId());
			
			data.setRouteID(route.getId());
			data.setRouteName(route.getName());

		} catch (UnexpectedInputException e) {
			//TODO: Rephrase if question different.
			//TODO: use skill context instead
			String lastQuestion=skillContext.getLastQuestion();
			log.error("UnexpectedInputException:Message={}:LastQuestion={}",e.getMessage(),lastQuestion);
			
			if ((lastQuestion!=null)&&(lastQuestion.equals(OutputHelper.LOCATION_PROMPT))){
				throw new InvalidInputException(e.getMessage(), e, OutputHelper.HELP_INTENT);
			}
			throw new InvalidInputException(e.getMessage(), e, "Please repeat your bus line. " + OutputHelper.ROUTE_PROMPT);
			
		} catch (APIException apiE) {
			throw new InvalidInputException("Route does not match API",
					"Could not find the bus line " + routeID + "." + OutputHelper.ROUTE_PROMPT);
		}

		skillContext.setFeedbackText(route.getId() + "," + route.getName() + ", ");

	}
	
	public static void addDirectionToConversation(PaInputData data, SkillContext skillContext, String direction) throws InvalidInputException {
		try {
			direction=DirectionCorrector.getDirection(direction);
			data.setDirection(direction);
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage(), e, "Please repeat your direction. " + OutputHelper.DIRECTION_PROMPT);
		}
	}
	
	
	public static Route getMatchedRoute(String routeID) throws APIException {
		Route output = null;
		//List<Message> routes = TrueTimeAPI.getRoutes();
		List<Message> routes = TrueTime.generateRoutes();
		
		Iterator<Message> iterator = routes.iterator();
		while (iterator.hasNext()) {
			Message element = (Message) iterator.next();
//			if (element.getMessageType().equalsIgnoreCase(Message.ERROR)) {
//				throw new APIException("Error from API:"+element.getError());
//			}
			//if (element.getMessageType().equalsIgnoreCase(Message.ROUTE)) {
				if (routeID.equalsIgnoreCase(element.getRouteID())) {
					output = Route.createRoute(element);
				}
			//}
		}
		if (output==null){
			throw new APIException("Route does not match API");
		}
		
		return output;
	}
	
}

