package me.universi.api.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import me.universi.util.ConvertUtil;
import java.util.HashMap;

/**
    Class with default structure for API responses
 */

public class Response {

    /** Represents if the operation was a success */
    public boolean success;

    /** Warning message to show on the web page */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String message;

    /** URL path to redirect to after receiving this response */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String redirectTo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String token;

    /** Body of the response with any data */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public HashMap<String, Object> body;

    public Response() {
        // Allocate map
        body = new HashMap<>();
    }

    @Override
    public String toString() {
        return ConvertUtil.serializeToJsonString(this);
    }
}
