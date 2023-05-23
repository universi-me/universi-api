package me.universi.indicators.services;

import me.universi.indicators.IndicatorsRepository;
import me.universi.indicators.entities.Indicators;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetIndicatorsServiceImpl implements GetIndicatorsService {

    private final UserService userService;
    private final IndicatorsRepository indicatorsRepository;

    @Autowired
    public GetIndicatorsServiceImpl(UserService userService, IndicatorsRepository indicatorsRepository) {
        this.userService = userService;
        this.indicatorsRepository = indicatorsRepository;
    }

    @Override
    public Indicators getIndicators() {
        User user = this.userService.obterUsuarioNaSessao();
        return this.indicatorsRepository.findByUserId(user.getId());
    }
}
