package me.universi.user.services;

/**
 * This class is a filter for JsonInclude, Serialization that checks if the user is logged or not for show or not.
 * Follows privacy rules.
 */
public class JsonUserAdminFilter {
    @Override
    public boolean equals(Object obj) {
        try {
            return !UserService.getInstance().isUserAdminSession();
        } catch (Exception e) {
            return true;
        }
    }
}
