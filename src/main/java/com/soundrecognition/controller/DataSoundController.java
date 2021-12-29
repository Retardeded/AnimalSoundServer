package com.soundrecognition.controller;


import com.soundrecognition.model.ServerMessage;
import com.soundrecognition.model.coefficients.PowerSpectrumCoefficient;
import com.soundrecognition.model.coefficients.SoundsFreqCoefficients;
import com.soundrecognition.model.coefficients.SoundsTimeCoefficients;
import com.soundrecognition.model.entities.DataSound;
import com.soundrecognition.model.entities.SoundType;
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
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(null));
        //return data.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ServerMessage> delete(@PathVariable String id) throws NotFoundException {
        var data = getDataSound(id);
        if (data.isPresent()) {
            sounds.delete(data.get());
            return  ResponseEntity.ok(new ServerMessage("Sound deleted"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }

    @PostMapping
    public ResponseEntity<DataSound> post(@RequestBody DataSound dataSound) throws NotFoundException {
        var data = getDataSound(dataSound.getTitle());
        if (data.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        } else {
            sounds.save(dataSound);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(dataSound);
        }
    }

    @PostMapping("/checkTime")
    public List<Pair<SoundType, SoundsTimeCoefficients>> checkSoundTimeDomain(@RequestBody DataSound dataSound) {
        DataSound dataSoundCut = sounds.cutSoundNoise(dataSound);
        var list = sounds.getMostSimilarSoundsTimeDomain(dataSoundCut);
        return list;
    }

    @PostMapping("/checkPowerSpectrum")
    public List<Pair<SoundType, PowerSpectrumCoefficient>> checkSoundPowerSpectrum(@RequestBody DataSound dataSound) {
        DataSound dataSoundCut = sounds.cutSoundNoise(dataSound);
        var list = sounds.getMostSimilarSoundsPowerSpectrum(dataSoundCut);
        return list;
    }

    @PostMapping("/checkFrequency")
    public List<Pair<SoundType, SoundsFreqCoefficients>> checkSoundFrequencyDomain(@RequestBody DataSound dataSound) {
        DataSound dataSoundCut = sounds.cutSoundNoise(dataSound);
        var list = sounds.getMostSimilarSoundsFreqDomain(dataSoundCut);
        return list;
    }

    private Optional<DataSound> getDataSound(String title) throws NotFoundException {
        return sounds.getDataSound(title);
    }

}