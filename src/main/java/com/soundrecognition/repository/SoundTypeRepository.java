package com.soundrecognition.repository;

import com.soundrecognition.model.SoundType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoundTypeRepository extends JpaRepository<SoundType, String> {
    Optional<SoundType> findByName(String name);
}