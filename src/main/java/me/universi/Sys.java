package me.universi;

import me.universi.user.services.AppContextProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(proxyBeanMethods = false)
public class Sys {
    public static void main(String [] args) {
        SpringApplication.run(Sys.class, args);
    }

    public static ApplicationContext context() {
        return AppContextProvider.getContext();
    }
}