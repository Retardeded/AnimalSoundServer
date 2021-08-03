package com.soundrecognition.server.quiz;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    /**
     * Returns all quizzes.
     */
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }


    public Optional<Quiz> getQuiz(Integer id) throws NotFoundException {
        Optional<Quiz> quiz = quizRepository.findById(id);

        if (quiz == null)
            throw new NotFoundException("Product not found");

        return quiz;
    }

    public Quiz save(Quiz quiz) {
        quizRepository.save(quiz);
        return quiz;
    }

    public void delete(Quiz quiz) {
        quizRepository.delete(quiz);
    }
}