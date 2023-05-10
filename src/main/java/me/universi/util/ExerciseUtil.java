package me.universi.util;

import me.universi.group.entities.Group;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UnauthorizedException;

public abstract  class ExerciseUtil {
    public static void checkPermissionExercise(User user, Group group){
        if (!group.getAdmin().getId().equals(user.getProfile().getId())){
            throw  new UnauthorizedException();
        }
    }
}
