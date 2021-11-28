package com.soundrecognition.repository;

import com.soundrecognition.model.DataSoundParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoundTypeParametersRepository extends JpaRepository<DataSoundParameters, String> {
    Optional<DataSoundParameters> findByTypeName(String type);
}