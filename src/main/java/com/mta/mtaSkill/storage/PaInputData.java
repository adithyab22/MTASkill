package com.mta.mtaSkill.storage;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.Session;

import com.mta.mtaSkill.util.Location;
import com.mta.mtaSkill.util.Stop;

/**
 * 
 */
public class PaInputData {
	private static Logger log = LoggerFactory.getLogger(PaInputData.class);
    
	

    /* -----data fields---- */
	private String id; //The user ID of this input object
	
    private String locationName;
	private String locationAddress;
    private String locationLat;
    private String locationLong;
    
    private String stopID;//Stop ID
    private String stopName;//Description of stop
    private double stopLat; //latitude
    private double stopLon; //longitude
    
    private String routeID;
    private String routeName;
    
    private String direction;
    
    /* ------------------- */
    private PaInputData() {
    }
    
    public static PaInputData create(Object o){
    	PaInputData data = null;
    	if (o instanceof LinkedHashMap){
    		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)o;
    		data = PaInputData.newInstance((String)map.get("id"));
    		data.setLocationName((String)map.get("locationName"));
    		data.setLocationAddress((String)map.get("locationAddress"));
    		data.setLocationLat((String)map.get("locationLat"));
    		data.setLocationLong((String)map.get("locationLong"));
    		
    		data.setStopID((String)map.get("stopID"));
    		data.setStopName((String)map.get("stopName"));
    		
    		try{
    			data.setStopLat((Double)map.get("stopLat"));
    		}
    		catch (Exception e){
    			data.setStopLat((Integer)map.get("stopLat"));
    		}
    		try{
    			data.setStopLon((Double)map.get("stopLon"));
    		} catch (Exception e){
    			data.setStopLon((Integer)map.get("stopLon"));
    		}
    		
    		data.setRouteID((String)map.get("routeID"));
    		data.setRouteName((String)map.get("routeName"));
    		
    		data.setDirection((String)map.get("direction"));
    	} else if (o instanceof PaInputData){
    		data = (PaInputData)o;
    	} else {
    		throw new ClassCastException("Cannot create a PaInputData object with " + o.getClass().toString());
    	}
    	return data;
    }
    
    /**
     * Creates a new instance of {@link PaInputData} with the provided {@link id}
     * @param id The user ID associated with this input data
     * @return
     */
    public static PaInputData newInstance(String id) {
        PaInputData input = new PaInputData();
        input.setID(id);
        return input;
    }
    
    public String getID(){
    	return id;
    }
    
    public void setID(String id){
    	this.id = id;
    }

    public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(String locationLat) {
		this.locationLat = locationLat;
	}

	public String getLocationLong() {
		return locationLong;
	}

	public void setLocationLong(String locationLong) {
		this.locationLong = locationLong;
	}

	public String getStopID() {
		return stopID;
	}

	public void setStopID(String stopID) {
		this.stopID = stopID;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public double getStopLat() {
		return stopLat;
	}

	public void setStopLat(double stopLat) {
		this.stopLat = stopLat;
	}

	public double getStopLon() {
		return stopLon;
	}

	public void setStopLon(double stopLon) {
		this.stopLon = stopLon;
	}

	public String getRouteID() {
		return routeID;
	}

	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

	public void setStop(Stop stop){
		setStopName(stop.getStopName());
		setStopID(stop.getStopID());
		setStopLat(stop.getLatitude());
		setStopLon(stop.getLongitude());
	}
	
	public void setLocation( Location c){
		setLocationAddress(c.getAddress());
		setLocationLat(c.getLat() + "");
		setLocationLong(c.getLng() + "");
	}
	
	public boolean hasAllData() {
        return (getStopName() != null && getDirection() != null && getRouteID() != null);
    }
	/*
	public String toString() {
		return "PaInputData [locationName=" + locationName + ", locationLat=" + locationLat + ", locationLong="
				+ locationLong + ", stopID=" + stopID + ", stopName=" + stopName + ", stopLat=" + stopLat + ", stopLon="
				+ stopLon + ", routeID=" + routeID + ", routeName=" + routeName + ", direction=" + direction +"]";
	}
	
	*/
	public String toString() {
		return "PaInputData [locationName=" + locationName + ", locationLat=" + locationLat + ", locationLong="
				+ locationLong + ", stopID=" + stopID + ", stopName=" + stopName + ", stopLat=" + stopLat + ", stopLon="
				+ stopLon + ", routeID=" + routeID + ", routeName=" + routeName + ", direction=" + direction 
				+ ", id=" + id +"]";
	}
    
}
