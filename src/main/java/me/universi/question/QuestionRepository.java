package me.universi.question;

import me.universi.question.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query(value = "select q.* from question q " +
            "join exercise_question eq on q.id = eq.question_id and eq.exercise_id = ?1 " +
            "order by random() limit ?2 ",nativeQuery = true)
    List<Question> findAllRandonAndLimited(UUID exerciseId, int amount);

    void deleteById(UUID questionId);

    Optional<Question> findFirstById(UUID id);

    Optional<Question> findFirstByIdAndExercisesId(UUID questionId, UUID exercisesId);

    List<Question> findAllByExercisesIdAndExercisesInactivateIsFalse(UUID exerciseId);
}