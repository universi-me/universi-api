package me.universi.feedback.services;

import me.universi.feedback.FeedbackRepository;
import me.universi.feedback.entities.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateFeedbackServiceImpl implements CreateFeedbackService {
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public CreateFeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback createFeedback(Feedback feedback){

        return feedbackRepository.save(feedback);
    }
}
