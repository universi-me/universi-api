package me.universi.indicators.services;

import me.universi.indicators.IndicatorsRepository;
import me.universi.indicators.entities.Indicators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRankingScoreServiceImpl implements GetRakingScoreService {

    private final IndicatorsRepository indicatorsRepository;

    @Autowired
    public GetRankingScoreServiceImpl(IndicatorsRepository indicatorsRepository) {
        this.indicatorsRepository = indicatorsRepository;
    }

    @Override
    public List<Indicators> getRankingIndicators() {
        List<Indicators> indicators = this.indicatorsRepository.findAllByOrderByScoreDesc();
        //Todo
        return indicators;
    }
}
