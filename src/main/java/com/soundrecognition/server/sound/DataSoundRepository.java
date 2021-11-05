package com.soundrecognition.server.sound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSoundRepository extends JpaRepository<DataSound, Integer> {
    Optional<DataSound> findById(Integer id);
}