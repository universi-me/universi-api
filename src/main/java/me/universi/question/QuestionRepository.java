package me.universi.question;

import me.universi.question.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "select * from Question order by random() limit ?1 ",nativeQuery = true)
    List<Question> findAllRandonAndLimited(int amount);

    void deleteById(Long questionId);

    Question findById(long id);

    Optional<Question> findByIdAndExercisesId(Long questionId, Long exercisesId);
}