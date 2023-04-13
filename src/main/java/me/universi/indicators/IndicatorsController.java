package me.universi.indicators;

import me.universi.indicators.entities.Indicators;
import me.universi.indicators.services.GetIndicatorsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user/{userId}/indicators")
public class IndicatorsController {

    private GetIndicatorsService indicatorsService;

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping
    public Indicators getIndicators(@PathVariable Long userId) {

        return indicatorsService.getIndicators(userId);
    }
}
