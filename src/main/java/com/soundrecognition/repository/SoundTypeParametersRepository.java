package com.soundrecognition.repository;

import com.soundrecognition.model.entities.typeparameters.SoundTypeParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoundTypeParametersRepository extends JpaRepository<SoundTypeParameters, String> {
    Optional<SoundTypeParameters> findByTypeName(String type);
}