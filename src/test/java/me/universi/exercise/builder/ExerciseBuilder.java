package me.universi.exercise.builder;

import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.entities.Exercise;

public class ExerciseBuilder {
    public static Exercise createExercise(){
        return new Exercise();
    }

    public static ExerciseCreateDTO createExerciseCreateDTO(){
        ExerciseCreateDTO exerciseCreateDTO = new ExerciseCreateDTO();
        exerciseCreateDTO.setTitle("Exercise");
        return exerciseCreateDTO;
    }
}
