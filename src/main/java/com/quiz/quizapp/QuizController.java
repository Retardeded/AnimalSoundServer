package com.quiz.quizapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Validated
@RestController
@RequestMapping("api/quizzes")
public class QuizController {

    private final QuizRepository quizzes;

    public QuizController(QuizRepository quizzes) {
        this.quizzes = quizzes;
    }

    @GetMapping
    public ResponseEntity<Iterable<Quiz>> get() {
        return ResponseEntity.ok(quizzes.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Quiz> get(@PathVariable String id) {
        var quiz = getQuiz(id);
        return quiz.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        var quiz = getQuiz(id);
        if (quiz.isPresent()) {
            quizzes.delete(quiz.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Quiz> post(@Valid @RequestBody Quiz quiz) {
        quiz = quizzes.save(quiz);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("{id}/solve")
    public ResponseEntity<Feedback> post(@PathVariable String id,@Valid @RequestBody Answer answer) {
        var quiz = getQuiz(id);
        if (quiz.isPresent()) {
            var providedAnswers = answer.getAnswer();
            var quizAnswersList = quiz.get().getAnswer();

            HashSet<Integer> set1 = new HashSet<Integer>(Arrays.asList(providedAnswers));
            HashSet<Integer> set2 = new HashSet<Integer>(quizAnswersList);
            //

                if (set1.equals(set2)) {
                return ResponseEntity.ok(
                        new Feedback(true, "Congratulations, you're right!"));
            }
            return ResponseEntity.ok(
                    new Feedback(false, "Wrong answer! Please try again.")
            );
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Optional<Quiz> getQuiz(String id) {
        var idValue = Integer.parseInt(id);
        return quizzes.findById(idValue);
    }

}