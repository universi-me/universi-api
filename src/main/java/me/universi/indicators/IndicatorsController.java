package me.universi.indicators;

import me.universi.indicators.entities.Indicators;
import me.universi.indicators.services.GetIndicatorsService;
import me.universi.indicators.services.GetRakingScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
public class IndicatorsController {

    private final GetIndicatorsService indicatorsService;
    private final GetRakingScoreService getRakingScoreService;

    public IndicatorsController(GetIndicatorsService indicatorsService, GetRakingScoreService getRakingScoreService) {
        this.indicatorsService = indicatorsService;
        this.getRakingScoreService = getRakingScoreService;
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping(value = "/indicators")
    public Indicators getIndicators() {

        return indicatorsService.getIndicators();
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping(value = "/ranking")
    public List<Indicators> getRanking(){
        return this.getRakingScoreService.getRankingIndicators();
    }
}
