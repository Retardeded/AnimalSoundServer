package com.soundrecognition.controller;


import com.soundrecognition.model.DataSound;
import com.soundrecognition.model.SoundType;
import com.soundrecognition.model.SoundsFreqCoefficients;
import com.soundrecognition.model.SoundsTimeCoefficients;
import com.soundrecognition.service.DataSoundService;
import javassist.NotFoundException;
import org.springframework.data.util.Pair;
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
        return ResponseEntity.ok(sounds.getDataSounds());
    }

    @GetMapping("/soundInfo")
    public ResponseEntity<List<DataSound>> getSoundInfoOnly() {
        return ResponseEntity.ok(sounds.getSoundInfoOnly());
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
    public DataSound post(@RequestBody DataSound dataSound) {
        dataSound = sounds.save(dataSound);
        return (dataSound);
    }

    @PostMapping("/check")
    public List<Pair<SoundType, SoundsTimeCoefficients>> checkSound(@RequestBody DataSound dataSound) {
        var list = sounds.getMostSimilarSoundsTimeDomain(dataSound);
        return list;
    }

    @PostMapping("/checkFreq")
    public List<Pair<DataSound, SoundsFreqCoefficients>> checkSoundFrequencyDomain(@RequestBody DataSound dataSound) {
        var list = sounds.getMostSimilarSoundsFreqDomain(dataSound);
        return list;
    }



    private Optional<DataSound> getDataSound(String id) throws NotFoundException {
        return sounds.getDataSound(Integer.parseInt(id));
    }

}