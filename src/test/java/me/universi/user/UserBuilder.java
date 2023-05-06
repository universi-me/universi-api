package me.universi.user;

import me.universi.user.entities.User;
import profile.builder.ProfileBuilder;

public class UserBuilder {

    public static User createUser(){
        User user = new User("Fulano de tal","fulando@email.com", "40028922");
        user.setProfile(ProfileBuilder.createProfile());
        user.setId(1L);
        return user;
    }

}
