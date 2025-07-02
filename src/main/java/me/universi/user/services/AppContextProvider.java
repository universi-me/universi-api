package me.universi.user.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AppContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext ctx) {
        context = ctx;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
