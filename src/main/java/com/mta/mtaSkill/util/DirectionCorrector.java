
package com.mta.mtaSkill.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Adithya
 */
public class DirectionCorrector {

    private static final Map<String, String> expectedInputs = new HashMap<>(200);

    static {
        expectedInputs.put("EAST", "EAST");
        expectedInputs.put("WEST", "WEST");
        expectedInputs.put("NORTH", "NORTH");
        expectedInputs.put("SOUTH", "SOUTH");
        
    }

    public static String getDirection(String inputDirection) throws Exception {
    	String output=expectedInputs.get(inputDirection.toUpperCase());
        if (output != null) {
            return output;
        } else {
            throw new Exception ("Cannot understand the direction " + inputDirection);
        }
    }
}
