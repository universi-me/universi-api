package me.universi;

import me.universi.indicators.entities.Indicators;
import me.universi.user.UserBuilder;

import java.util.UUID;

public class IndicatorsBuilder {

    public static Indicators createIndicators(){
        Indicators indicators = new Indicators();
        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        indicators.setId(uuid_1);
        indicators.setScore(0L);
        indicators.setProfile(UserBuilder.createUser().getProfile());

        return indicators;
    }
}
