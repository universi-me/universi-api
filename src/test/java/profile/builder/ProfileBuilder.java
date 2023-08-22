package profile.builder;

import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;

import java.util.UUID;

public class ProfileBuilder {
    public static Profile createProfile(){
        Profile profile = new Profile();
        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        profile.setId(uuid_1);
        profile.setGender(Gender.M);
        return profile;
    }
}
