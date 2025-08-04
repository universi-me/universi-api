package me.universi.user.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import me.universi.Sys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
public class EnvironmentService {

    public String BUILD_HASH = "development";

    @Value("${BUILD_HASH}")
    public String BUILD_HASH_ENV;

    @Value("${PUBLIC_URL}")
    public String PUBLIC_URL;

    @Value("${spring.profiles.active}")
    public String activeProfile;

    @Value( "${server.servlet.context-path}" )
    private String contextPath;

    @Value("${RECOVERY_TOKEN_EXPIRATION_HOURS}")
    public int RECOVERY_TOKEN_EXPIRATION_HOURS;

    public EnvironmentService() {
    }

    // bean instance via context
    public static EnvironmentService getInstance() {
        return Sys.context().getBean("environmentService", EnvironmentService.class);
    }

    public String getPublicUrl() {
        try {
            if(PUBLIC_URL != null && !PUBLIC_URL.isEmpty()) {
                return PUBLIC_URL;
            }
            URL requestUrl = new URL(RequestService.getInstance().getRequest().getRequestURL().toString());
            return RequestService.getInstance().getUrlDomainFromURL(requestUrl);
        } catch (Exception e) {
            return null;
        }
    }

    public String getContextPath() {
        if(contextPath == null || contextPath.isEmpty()) {
            return "";
        }
        return contextPath;
    }

    public boolean isProduction() {
        return "prod".equals(activeProfile);
    }

    public String getBuildHash() {
        if(BUILD_HASH == null || BUILD_HASH.isEmpty() || "development".equals(BUILD_HASH)) {
            String jarPath = new File(".").getPath();
            String filePath = Paths.get(jarPath, "build.hash").toString();
            Resource resource = new FileSystemResource(filePath);
            try {
                if(resource.contentLength() == 0) {
                    return "development";
                }
                InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                BUILD_HASH = FileCopyUtils.copyToString(reader);
            } catch (IOException ignored) {
            }
        }
        if("development".equals(BUILD_HASH) && BUILD_HASH_ENV != null && !BUILD_HASH_ENV.isEmpty()) {
            return BUILD_HASH_ENV;
        }
        return BUILD_HASH;
    }

    public int getRecoveryTokenExpirationHours() {
        return RECOVERY_TOKEN_EXPIRATION_HOURS;
    }

}