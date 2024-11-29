package me.universi.feedback.services;


import me.universi.feedback.entities.Feedback;

@FunctionalInterface
public interface CreateFeedbackService {
    Feedback createFeedback(Feedback feedback);
}
