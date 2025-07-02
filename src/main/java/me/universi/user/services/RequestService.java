package me.universi.user.services;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URL;
import java.util.regex.Pattern;
import me.universi.Sys;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class RequestService {
    private final EnvironmentService environmentService;

    public RequestService(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    // bean instance via context
    public static RequestService getInstance() {
        return Sys.context().getBean("requestService", RequestService.class);
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }

    public String getActiveUrl() {
        return  getRequest().getRequestURI();
    }

    // get host from request
    public String getDomainFromRequest() {
        try {
            return getRequest().getServerName();
        } catch (Exception e) {
            return null;
        }
    }

    private static Pattern SUBDOMAIN_PATTERN = Pattern.compile( "^([a-zA-Z0-9-]+)\\." );
    public @Nullable String getSubdomainFromRequest() {
        try {
            var domain = getDomainFromRequest();
            if ( domain == null ) return null;

            var matcher = SUBDOMAIN_PATTERN.matcher( domain );
            return matcher.find()
                    ? matcher.group( 1 )
                    : null;
        }
        catch( Exception e ) {
            return null;
        }
    }

    public String getRefererUrlBase() {
        try {
            String refererHeader = getRequest().getHeader("Referer");
            if(refererHeader != null && !refererHeader.isEmpty()) {
                URL requestUrl = new URL(refererHeader);
                return getUrlDomainFromURL(requestUrl);
            }
            return environmentService.getPublicUrl();
        } catch (Exception e) {
            return null;
        }
    }

    // get url with host only from url with path
    public String getUrlDomainFromURL(URL requestUrl) {
        String port = requestUrl.getPort() > 0 && requestUrl.getPort() != 80 && requestUrl.getPort() != 443
                ? ":" + requestUrl.getPort()
                : "";
        return requestUrl.getProtocol() + "://" + requestUrl.getHost() + port;
    }

    // get full public url domain api
    public String getPublicUrlApi() {
        try {
            return environmentService.getPublicUrl() + environmentService.getContextPath();
        } catch (Exception e) {
            return null;
        }
    }

    // get full public url domain web client
    public String getPublicUrlWebClient() {
        try {
            return environmentService.getPublicUrl();
        } catch (Exception e) {
            return null;
        }
    }

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public String getClientIpAddress() {
        HttpServletRequest request = getRequest();
        for (String header: IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0];
                return ip;
            }
        }
        return getRequest().getRemoteAddr();
    }

    public void clearSession() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }

    public String getCookieValue(String cookieName) {
        HttpServletRequest request = getRequest();
        if (request != null && request.getCookies() != null && cookieName != null) {
            for (var cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean hasCookie(String cookieName) {
        return getCookieValue(cookieName) != null;
    }

    public HttpServletResponse getResponse() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getResponse();
    }

    public void removeCookie(String cookieName) {
        if(hasCookie(cookieName)) {
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            HttpServletResponse response = getResponse();
            response.addCookie(cookie);
        }
    }
}