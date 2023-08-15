package me.universi.user.services;

import me.universi.Sys;
import me.universi.user.entities.User;

/**
 * This class is a filter for JsonInclude, Serialization 'email' that checks if the user is owner of session or not for show or not.
 * Only see u email.
 * Follows privacy rules.
 */
public class JsonEmailOwnerSessionFilter {
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof String)) {
            return true;
        }
        try {

            // UserService bean instance via context
            UserService userService = Sys.context.getBean("userService", UserService.class);

            User user = userService.getUserInSession();

            String emailInSession = user.getEmail();
            String emailValue = obj.toString();

            return !emailValue.equals(emailInSession);

        } catch (Exception e) {
            return true;
        }
    }
}
