package com.quiz.quizapp.sound;

import com.quiz.quizapp.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSoundRepository extends JpaRepository<DataSound, Integer> {
    Optional<DataSound> findById(Integer id);
}