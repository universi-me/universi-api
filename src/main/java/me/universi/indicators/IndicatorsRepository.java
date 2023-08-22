package me.universi.indicators;

import me.universi.indicators.entities.Indicators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IndicatorsRepository extends JpaRepository<Indicators, UUID> {

    Indicators findByProfileId(UUID profileId);

    @Query(value = "select * from indicators order by score desc limit 10",nativeQuery = true)
    List<Indicators> findAllByOrderByScoreDesc();
}
