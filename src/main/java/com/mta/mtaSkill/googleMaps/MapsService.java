/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mta.mtaSkill.googleMaps;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;

import com.mta.mtaSkill.InvalidInputException;
import com.mta.mtaSkill.util.Location;
import com.mta.mtaSkill.util.Stop;

/**
 *
 * @author Adithya
 */
@SuppressWarnings("serial")
public class MapsService extends HttpServlet {

    /**
     * Web application to determine nearest bus stop from a source location for a given route# and 
     * direction of transit.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {   
        //NearestStopLocator p = new NearestStopLocator();
      
        //Inputs fom UI:
        String source = request.getParameter("source").replaceAll("\\s","+");
        String route = request.getParameter("route").trim();
        String direction = request.getParameter("direction").trim();
        
        try {
        	Location c = NearestStopLocator.getSourceLocation(source);
        	Stop stop = NearestStopLocator.process(c, route, direction);
        	request.setAttribute("nearestStopName", stop.getStopName());
        } catch (JSONException ex) {
        	Logger.getLogger(MapsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidInputException ex) {
        	Logger.getLogger(MapsService.class.getName()).log(Level.SEVERE, null, ex);
        }

    	//direct to the results page
        RequestDispatcher view = request.getRequestDispatcher("result.jsp");
        view.forward(request, response);
        
    }
}
