package me.universi.user.services;

/**
 * This class is a filter for JsonInclude, Serialization that checks if the user is logged or not for show or not.
 * Follows privacy rules.
 */
public class JsonUserLoggedFilter {
    @Override
    public boolean equals(Object obj) {
        try {
            return !UserService.getInstance().userIsLoggedIn();
        } catch (Exception e) {
            return true;
        }
    }
}
