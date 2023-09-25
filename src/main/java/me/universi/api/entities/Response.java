package me.universi.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletResponse;
import me.universi.util.ConvertUtil;
import java.util.HashMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
    Class with default structure for API responses
 */

public class Response {

    /** Represents if the operation was a success */
    public boolean success;

    /** HTTP status code to return */
    @JsonIgnore
    public Integer status;

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
        status = null;
    }

    public static Response buildResponse(ThrowingConsumer<Response> completionHandler)  {
        Response response = new Response();
        try {
            response.success = true;
            completionHandler.accept(response);
        } catch (Exception e) {
            response.success = false;
            response.message = e.getMessage();
            if(response.status == null) {
                response.status = 500;
            }
        }
        if(response.status != null) {
            // set http status to current response
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletResponse actualResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
            actualResponse.setStatus(response.status);
        }
        return response;
    }


    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

    @Override
    public String toString() {
        return ConvertUtil.serializeToJsonString(this);
    }
}
