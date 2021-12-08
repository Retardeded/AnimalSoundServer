package com.soundrecognition.controller;


import com.soundrecognition.model.*;
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

    @GetMapping("/soundsInfo")
    public ResponseEntity<List<DataSound>> getSoundsInfoOnly() {
        return ResponseEntity.ok(sounds.getSoundInfoOnly());
    }

    @GetMapping("/soundTypes")
    public ResponseEntity<List<SoundType>> getSoundTypes() {
        return ResponseEntity.ok(sounds.getSoundTypes());
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

    @PostMapping("/checkTime")
    public List<Pair<SoundType, SoundsTimeCoefficients>> checkSoundTimeDomain(@RequestBody DataSound dataSound) {
        var list = sounds.getMostSimilarSoundsTimeDomain(dataSound);
        return list;
    }

    @PostMapping("/checkPowerSpectrum")
    public List<Pair<SoundType, PowerSpectrumCoefficient>> checkSoundPowerSpectrum(@RequestBody DataSound dataSound) {
        var list = sounds.getMostSimilarSoundsPowerSpectrum(dataSound);
        return list;
    }

    @PostMapping("/checkFrequency")
    public List<Pair<SoundType, SoundsFreqCoefficients>> checkSoundFrequencyDomain(@RequestBody DataSound dataSound) {
        var list = sounds.getMostSimilarSoundsFreqDomain(dataSound);
        return list;
    }

    private Optional<DataSound> getDataSound(String id) throws NotFoundException {
        return sounds.getDataSound(Integer.parseInt(id));
    }

}