package com.quiz.quizapp.sound;
;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataSoundService {

    private final DataSoundRepository dataSoundRepository;

    DataSoundService(DataSoundRepository dataSoundRepository) {
        this.dataSoundRepository = dataSoundRepository;
    }

    /**
     * Returns all quizzes.
     */
    public List<DataSound> getAllDataSounds() {
        return dataSoundRepository.findAll();
    }


    public Optional<DataSound> getDataSound(Integer id) throws NotFoundException {
        Optional<DataSound> data = dataSoundRepository.findById(id);

        if (data == null)
            throw new NotFoundException("Product not found");

        return data;
    }

    public DataSound save(DataSound dataSound) {
        dataSoundRepository.save(dataSound);
        return dataSound;
    }

    public void delete(DataSound dataSound) {
        dataSoundRepository.delete(dataSound);
    }
}