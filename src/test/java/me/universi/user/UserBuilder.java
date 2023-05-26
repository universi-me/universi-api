package me.universi.user;

import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
import me.universi.builder.ProfileBuilder;

public class UserBuilder {

    public static User createUser(){
        User user = new User("Fulano de tal","fulando@email.com", "40028922");
        user.setProfile(ProfileBuilder.createProfile());
        user.setId(1L);
        return user;
    }
    public static User createUserSecondary(){
        User user = new User("Beltrano de tal","beltrano@email.com", "40028922");
        Profile profile = ProfileBuilder.createProfile();
        profile.setId(2L);
        user.setProfile(profile);
        user.setId(2L);
        return user;
    }

}
