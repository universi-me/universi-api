package me.universi.alternative;

import me.universi.alternative.entities.Alternative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlternativeRepository extends JpaRepository<Alternative, UUID> {
    Optional<Alternative> findAlternativeByIdAndQuestionIdAndQuestionProfileCreateId(UUID alternativeId, UUID questionId, UUID profileId);
    List<Alternative>  findAllByQuestionIdAndQuestionProfileCreateId(UUID questionId, UUID profileId);

    @Query(value = "select * from alternative where question_id in ( ?1 ) order by question_id", nativeQuery = true)
    List<Alternative> findAllByQuestionWithAlternatives(List<UUID> ids);

    @Query(value = "select * from alternative where question_id in (?1) and correct is true", nativeQuery = true)
    List<Alternative> findAllByQuestionIdAndCorrectIsTrue(List<UUID> questionIds);

    Integer countAlternativeByQuestionId(UUID questionId);

}