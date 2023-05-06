package me.universi.indicators.services;

import me.universi.indicators.IndicatorsRepository;
import me.universi.indicators.entities.Indicators;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserNotFoundException;
import me.universi.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetIndicatorsServiceImpl implements GetIndicatorsService {

    private final UserRepository userRepository;
    private final IndicatorsRepository indicatorsRepository;

    @Autowired
    public GetIndicatorsServiceImpl(UserRepository userRepository, IndicatorsRepository indicatorsRepository) {
        this.userRepository = userRepository;
        this.indicatorsRepository = indicatorsRepository;
    }

    @Override
    public Indicators getIndicators(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return this.indicatorsRepository.findByUserId(userId);
    }
}
