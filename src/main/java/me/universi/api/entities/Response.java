package me.universi.api.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
    Class with default structure for API responses
 */

public class Response {

    /** Represents if the operation was a success */
    public boolean success;

    /** Warning message to show on the web page */
    public String message;

    /** URL path to redirect to after receiving this response */
    public String redirectTo;

    /** Body of the response with any data */
    public Map body;

    public Response() {
        // Allocate map
        body = new HashMap();
    }

    @Override
    public String toString() {
        try {
            // Parse this object to Json String
            ObjectMapper mapper = new ObjectMapper();
            return (String)mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
