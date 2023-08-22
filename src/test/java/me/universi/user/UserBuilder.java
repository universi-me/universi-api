package me.universi.user;

import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
import profile.builder.ProfileBuilder;

import java.util.UUID;

public class UserBuilder {

    public static User createUser(){
        User user = new User("Fulano de tal","fulando@email.com", "40028922");
        user.setProfile(ProfileBuilder.createProfile());
        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        user.setId(uuid_1);
        return user;
    }
    public static User createUserSecondary(){
        User user = new User("Beltrano de tal","beltrano@email.com", "40028922");
        Profile profile = ProfileBuilder.createProfile();
        UUID uuid_2 = UUID.fromString("626370e9-b1ff-4b2d-baf8-b6b8ba04f603");
        profile.setId(uuid_2);
        user.setProfile(profile);
        user.setId(uuid_2);
        return user;
    }

}
