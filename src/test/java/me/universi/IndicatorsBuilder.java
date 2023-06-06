package me.universi;

import me.universi.indicators.entities.Indicators;
import me.universi.user.UserBuilder;

public class IndicatorsBuilder {

    public static Indicators createIndicators(){
        Indicators indicators = new Indicators();
        indicators.setId(1L);
        indicators.setScore(0L);
        indicators.setUser(UserBuilder.createUser());

        return indicators;
    }
}
