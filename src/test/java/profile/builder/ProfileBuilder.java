package profile.builder;

import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;

public class ProfileBuilder {
    public static Profile createProfile(){
        Profile profile = new Profile();
        profile.setId(1L);
        profile.setGender(Gender.M);
        return profile;
    }
}
