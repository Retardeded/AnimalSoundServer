package com.quiz.quizapp.sound;


import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Validated
@RestController
@RequestMapping("api/sounds")
public class DataSoundController {

    private final DataSoundService sounds;

    public DataSoundController(DataSoundService sounds) {
        this.sounds = sounds;
    }


    @GetMapping
    public ResponseEntity<List<DataSound>> get() {
        return ResponseEntity.ok(sounds.getAllDataSounds());
    }

    @GetMapping("{id}")
    public ResponseEntity<DataSound> get(@PathVariable String id) throws NotFoundException {
        var data = getDataSound(id);
        return data.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) throws NotFoundException {
        var data = getDataSound(id);
        if (data.isPresent()) {
            sounds.delete(data.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<DataSound> post(@RequestBody DataSound dataSound) {
        dataSound = sounds.save(dataSound);
        return ResponseEntity.ok(dataSound);
    }

    private Optional<DataSound> getDataSound(String id) throws NotFoundException {
        return sounds.getDataSound(Integer.parseInt(id));
    }

}