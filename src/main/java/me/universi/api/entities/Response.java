package me.universi.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletResponse;
import me.universi.user.exceptions.ExceptionResponse;
import me.universi.user.services.EnvironmentService;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
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

    /** SweetAlert2 alert options */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public HashMap<String, Object> alertOptions;

    private Response() {
        // Allocate map
        body = new HashMap<>();
        alertOptions = new HashMap<>();
        status = null;
    }

    public void setStatus(Integer statusCode) {
        this.status = statusCode;
    }

    public void setStatus(HttpStatus httpStatus) {
        if (httpStatus != null)
            this.status = httpStatus.value();
    }

    /**
        Build a new api response for execute action and return optional message for client
     */
    public static Response buildResponse(ThrowingConsumer<Response> completionHandler)  {
        Response response = new Response();
        try {
            response.success = true;
            completionHandler.accept(response);
        } catch (Exception e) {
            // log error
            e.printStackTrace();
            response.success = false;
            if(e.getClass().getPackageName().startsWith("me.universi")) { // is exception from this project
                response.message = e.getMessage();
                if(e instanceof ExceptionResponse expResp) {
                    if(expResp.redirectTo != null) {
                        response.redirectTo = expResp.redirectTo;
                    }
                    if(expResp.status != null) {
                        response.status = expResp.status;
                    }
                }
            } else {
                // unknown exception occurred
                String messageExp = "";
                if(EnvironmentService.getInstance().isProduction()) {
                    if(UserService.getInstance().isUserDevSession()) { // user is developer
                        messageExp = "\n (" + e.getMessage() + ")";
                    }
                } else {
                    // not in production
                    messageExp = "\n (" + e.getMessage() + ")";
                }
                response.message = "Ocorreu um erro interno por parte do servidor." + messageExp;
                response.alertOptions.put("html", response.message + "<br/><br/><img src=\"https://i.imgur.com/OWV6eKo.gif\" width='70%'/>");
            }

            if(response.status == null) {
                response.status = 500; // default status code error
            }
        }
        if(response.status != null) {
            // set http status to current response
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if(requestAttributes != null) {
                HttpServletResponse actualResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
                if(actualResponse != null) {
                    actualResponse.setStatus(response.status);
                }
            }
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
