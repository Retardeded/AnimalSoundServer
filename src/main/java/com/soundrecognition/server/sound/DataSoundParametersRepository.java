package com.soundrecognition.server.sound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSoundParametersRepository extends JpaRepository<DataSoundParameters, Integer> {
    Optional<DataSoundParameters> findById(Integer id);
}