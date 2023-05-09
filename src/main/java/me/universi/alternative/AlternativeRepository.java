package me.universi.alternative;

import me.universi.alternative.entities.Alternative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlternativeRepository extends JpaRepository<Alternative, Long> {
    Optional<Alternative> findAlternativeByIdAndQuestionIdAndQuestionUserCreateId(Long alternativeId, Long questionId, Long UserId);
    List<Alternative>  findAllByQuestionIdAndQuestionUserCreateId(Long questionId, Long UserId);

    @Query(value = "select * from alternative where question_id in ( ?1 ) order by question_id", nativeQuery = true)
    List<Alternative> findAllByQuestionWithAlternatives(List<Long> ids);

    @Query(value = "select * from alternative where question_id in (?1) and correct is true", nativeQuery = true)
    List<Alternative> findAllByQuestionIdAndCorrectIsTrue(List<Long> questionIds);

    Integer countAlternativeByQuestionId(Long questionId);

}