package com.soundrecognition.service;
;
import com.soundrecognition.model.coefficients.CorrelationCoefficient;
import com.soundrecognition.model.coefficients.PowerSpectrumCoefficient;
import com.soundrecognition.model.coefficients.SoundsFreqCoefficients;
import com.soundrecognition.model.coefficients.SoundsTimeCoefficients;
import com.soundrecognition.model.entities.DataSound;
import com.soundrecognition.model.entities.DataSoundParameters;
import com.soundrecognition.model.entities.SoundType;
import com.soundrecognition.model.entities.typeparameters.SoundTypeParameters;
import com.soundrecognition.repository.DataSoundRepository;
import com.soundrecognition.repository.SoundTypeParametersRepository;
import com.soundrecognition.repository.SoundTypeRepository;
import com.soundrecognition.soundprocessing.CalculateSoundSimilarity;
import javassist.NotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataSoundService {

    private final DataSoundRepository dataSoundRepository;
    private final SoundTypeRepository soundTypeRepository;
    private final SoundTypeParametersRepository soundTypeParametersRepository;
    final int topListSize = 3;
    final int minSignalEnvelope = 200;
    final int minRootMeanSquareEnergy = 90;
    final String noiseTitle = "It's background noise";

    DataSoundService(DataSoundRepository dataSoundRepository, SoundTypeRepository soundTypeRepository, SoundTypeParametersRepository soundTypeParametersRepository) {
        this.dataSoundRepository = dataSoundRepository;
        this.soundTypeRepository = soundTypeRepository;
        this.soundTypeParametersRepository = soundTypeParametersRepository;
    }

    public List<DataSound> getSoundInfoOnly()
    {
        ArrayList<DataSound> data = new ArrayList<>();
        for (var sound: dataSoundRepository.findAll() ) {
            cleanSoundData(sound);
            data.add(sound);
        }
        return data;
    }

    public List<SoundType> getSoundTypes()
    {
        ArrayList<SoundType> data = new ArrayList<>();
        for (var soundType: soundTypeRepository.findAll() ) {
            makeSoundTypeRightForSendingThroughNetwork(soundType);
            System.out.println(soundType.soundTypeParameters.parametersListInt.get(0).getParameterValuesRaw());
            data.add(soundType);
        }
        return data;
    }

    private void cleanSoundTypeData(SoundType soundType) {
        for (var sound:soundType.getDataSounds())
        {
            sound.setTimeDomainPoints(new ArrayList<>());
        }
    }

    private void cleanSoundData(DataSound sound) {
        sound.setTimeDomainPoints(new ArrayList<>());
    }

    private DataSoundParameters getDataSoundParameters(DataSound dataSound, String mode) {
        var parameters = new DataSoundParameters();
        parameters.typeName = "";
        if(mode.charAt(0) == '1') {
            var zeroCrossingDensity = CalculateSoundSimilarity.zeroCrossingDensity(dataSound);
            var rootMeanSquareEnergy = CalculateSoundSimilarity.rootMeanSquareEnergy(dataSound);
            var signalEnvelope = CalculateSoundSimilarity.signalEnvelope(dataSound);
            parameters.zeroCrossingDensity = zeroCrossingDensity;
            parameters.rootMeanSquareEnergy = Arrays.asList(rootMeanSquareEnergy);
            parameters.signalEnvelope = Arrays.asList(signalEnvelope);
        }
        if(mode.charAt(1) == '1'|| mode.charAt(2) == '1') {
            var powerSpectres = CalculateSoundSimilarity.calculatePowerSpectres(dataSound);

            int n = Math.toIntExact(dataSound.getPointsInGraphs());
            int m = Math.toIntExact(dataSound.getNumOfGraphs());
           if(mode.charAt(1) == '1') {
               parameters.powerSpectrum = CalculateSoundSimilarity.calculateAccumulatedPowerSpectrum(powerSpectres, n, m);
           }
            if(mode.charAt(2) == '1') {
               parameters.spectralCentroids = CalculateSoundSimilarity.spectralCentroids(powerSpectres, n, m);
               parameters.spectralFluxes = CalculateSoundSimilarity.spectralFluxes(powerSpectres, n, m);
               parameters.spectralRollOffPoints = CalculateSoundSimilarity.spectralRollOffPoints(powerSpectres, n, m);
           }
        }

        return parameters;
    }

    public DataSound save(DataSound dataSoundRaw) {
        DataSound dataSound = cutSoundNoise(dataSoundRaw);
        if(dataSound.getTimeDomainPoints().size() == 0)
            return dataSound;

        DataSoundParameters newSoundParams = getDataSoundParameters(dataSound, "111");
        System.out.println("enveolope:::" + newSoundParams.signalEnvelope);
        dataSound.setDataSoundParameters(newSoundParams);
        SoundTypeParameters newSoundTypeParams = new SoundTypeParameters(dataSound.getType(), newSoundParams.signalEnvelope,
                newSoundParams.rootMeanSquareEnergy,newSoundParams.zeroCrossingDensity, newSoundParams.powerSpectrum,
                newSoundParams.spectralCentroids, newSoundParams.spectralFluxes, newSoundParams.spectralRollOffPoints);
        dataSoundRepository.saveAndFlush(dataSound);
        if(!dataSound.getType().equals("")) {
            var soundTypeOptional = soundTypeRepository.findByName(dataSound.getType());
            if(soundTypeOptional.isPresent()) {
                var soundType = soundTypeOptional.get();
                var presentSoundTypeParams = soundType.soundTypeParameters;
                var currentZeroCrossingDensity = presentSoundTypeParams.getZeroCrossingDensityRaw();
                presentSoundTypeParams.UpdateZeroCrossingDensity(currentZeroCrossingDensity, newSoundTypeParams.getZeroCrossingDensity(), true);

                for (int i = 0; i < presentSoundTypeParams.parametersListInt.size();i++) {
                    var param = presentSoundTypeParams.parametersListInt.get(i);
                    var value = newSoundTypeParams.parametersListInt.get(i).getParameterValuesRaw();
                    presentSoundTypeParams.updateParameterValueAdd(param.name, value);
                }
                for (int i = 0; i < presentSoundTypeParams.parametersListDouble.size();i++) {
                    var param = presentSoundTypeParams.parametersListDouble.get(i);
                    var value = newSoundTypeParams.parametersListDouble.get(i).getParameterValuesRaw();
                    presentSoundTypeParams.updateParameterValueAdd(param.name, value);
                }

                cleanSoundData(dataSound);
                soundType.getDataSounds().add(dataSound);
                soundTypeParametersRepository.saveAndFlush(presentSoundTypeParams);
            }  else {
                newSoundTypeParams.typeName = dataSound.getType();
                var sounds = Arrays.asList(dataSound);
                SoundType newSoundType = new SoundType(dataSound.getType(), sounds, newSoundTypeParams);
                soundTypeParametersRepository.saveAndFlush(newSoundTypeParams);
                soundTypeRepository.saveAndFlush(newSoundType);
            }
        }


        return dataSound;
    }

    private DataSound cutSoundNoise(DataSound dataSoundRaw) {
        var rootMeanSquareEnergy = CalculateSoundSimilarity.rootMeanSquareEnergy(dataSoundRaw);
        int minIndex = -1;
        int maxIndex = -1;

        System.out.println("Initial:" + dataSoundRaw.getNumOfGraphs());
        System.out.println("Initial:" + dataSoundRaw.getTimeDomainPoints().size());

        for(int i = 0; i < rootMeanSquareEnergy.length; i++) {
            if(rootMeanSquareEnergy[i] > minRootMeanSquareEnergy && minIndex == -1) {
                minIndex = i;
            }
            if(minIndex != -1 && rootMeanSquareEnergy[i] < minRootMeanSquareEnergy) {
                maxIndex = i;
                break;
            }

        }

        if(maxIndex == -1) maxIndex = rootMeanSquareEnergy.length;

        if(maxIndex == -1) {
            cleanSoundData(dataSoundRaw);
            dataSoundRaw.setTitle(noiseTitle);
            return dataSoundRaw;
        }

        dataSoundRaw.setNumOfGraphs((long) (maxIndex-minIndex));
        int pointsInGraphs = Math.toIntExact(dataSoundRaw.getPointsInGraphs());
        dataSoundRaw.setTimeDomainPoints(dataSoundRaw.getTimeDomainPoints().subList(minIndex*pointsInGraphs, maxIndex*pointsInGraphs));

        System.out.println("After:" + dataSoundRaw.getNumOfGraphs());
        System.out.println("After:" + dataSoundRaw.getTimeDomainPoints().size());
        return dataSoundRaw;
    }


    public List<Pair<SoundType, SoundsFreqCoefficients>> getMostSimilarSoundsFreqDomain(DataSound newSound) {
        String mode = "001";
        var typesParams = soundTypeRepository.findAll();
        var newSoundParam = getDataSoundParameters(newSound, mode);
        ArrayList<Pair<SoundType, CorrelationCoefficient>> mostSimilar = new ArrayList<>();
        ArrayList<Pair<SoundType, SoundsFreqCoefficients>> mostSimilarType = new ArrayList<>();

        makeCorrelationList(typesParams, newSoundParam, mostSimilar, mode);
        sort(mostSimilar);
        var topList = mostSimilar.subList(0,Math.min(mostSimilar.size(), topListSize));
        for (var listItem:topList) {
            var soundType = listItem.getFirst();
            makeSoundTypeRightForSendingThroughNetwork(soundType);
            mostSimilarType.add(Pair.of(soundType, (SoundsFreqCoefficients) listItem.getSecond()));
        }
        return mostSimilarType.subList(0,Math.min(mostSimilar.size(), topListSize));
    }



    public List<Pair<SoundType, PowerSpectrumCoefficient>> getMostSimilarSoundsPowerSpectrum(DataSound newSound) {
        String mode = "010";
        var typesParams = soundTypeRepository.findAll();
        var newSoundParam = getDataSoundParameters(newSound, mode);
        ArrayList<Pair<SoundType, CorrelationCoefficient>> mostSimilar = new ArrayList<>();
        ArrayList<Pair<SoundType, PowerSpectrumCoefficient>> mostSimilarType = new ArrayList<>();

        makeCorrelationList(typesParams, newSoundParam, mostSimilar, mode);
        sort(mostSimilar);
        var topList = mostSimilar.subList(0,Math.min(mostSimilar.size(), topListSize));
        for (var listItem:topList) {
            var soundType = listItem.getFirst();
            makeSoundTypeRightForSendingThroughNetwork(soundType);
            mostSimilarType.add(Pair.of(soundType, (PowerSpectrumCoefficient) listItem.getSecond()));

        }
        return mostSimilarType.subList(0,Math.min(mostSimilar.size(), topListSize));
    }

    private void makeCorrelationList(List<SoundType> typesParams, DataSoundParameters newSoundParam, ArrayList<Pair<SoundType, CorrelationCoefficient>> mostSimilar, String mode) {
        for (int i = 0; i < typesParams.size(); i++) {
            var params = typesParams.get(i).soundTypeParameters;
            CorrelationCoefficient cor = null;
            if(mode.charAt(0) == '1') {
                cor = CalculateSoundSimilarity.correlationTimeParamsCoefficient(params, newSoundParam);
            }
            if(mode.charAt(1) == '1') {
                cor = CalculateSoundSimilarity.correlationPowerSpectrumCoefficient(params, newSoundParam);
            }
            if(mode.charAt(2) == '1') {
                cor = CalculateSoundSimilarity.correlationFreqParamsCoefficient(params, newSoundParam);
            }
            cleanSoundTypeData(typesParams.get(i));
            mostSimilar.add(Pair.of(typesParams.get(i), cor));
        }
    }

    public List<Pair<SoundType, SoundsTimeCoefficients>> getMostSimilarSoundsTimeDomain(DataSound newSound) {
        String mode = "100";
        var typesParams = soundTypeRepository.findAll();
        var newSoundParam = getDataSoundParameters(newSound, mode);
        ArrayList<Pair<SoundType, CorrelationCoefficient>> mostSimilar = new ArrayList<>();
        ArrayList<Pair<SoundType, SoundsTimeCoefficients>> mostSimilarType = new ArrayList<>();

        makeCorrelationList(typesParams, newSoundParam, mostSimilar, mode);
        sort(mostSimilar);
        var topList = mostSimilar.subList(0,Math.min(mostSimilar.size(), topListSize));
        for (var listItem:topList) {
            var soundType = listItem.getFirst();
            makeSoundTypeRightForSendingThroughNetwork(soundType);
            mostSimilarType.add(Pair.of(soundType, (SoundsTimeCoefficients) listItem.getSecond()));
        }
        return mostSimilarType.subList(0,Math.min(mostSimilar.size(), topListSize));
    }


    private void sort(ArrayList<Pair<SoundType, CorrelationCoefficient>> mostSimilar) {
        Collections.sort(mostSimilar, (o1, o2) -> {
            if (o1.getSecond().getMergedCoefficient() > o2.getSecond().getMergedCoefficient()) {
                return -1;
            } else if (o1.getSecond().getMergedCoefficient() == (o2.getSecond().getMergedCoefficient())) {
                return 0;
            } else {
                return 1;
            }
        });
    }

    private void makeSoundTypeRightForSendingThroughNetwork(SoundType soundType) {
        cleanSoundTypeData(soundType);
        var typeParams = soundType.soundTypeParameters;

        typeParams.setZeroCrossingDensity(typeParams.getZeroCrossingDensityWeighted());

        for (var param:typeParams.parametersListInt) {
            typeParams.setParameter(param.name);
        }
        for (var param:typeParams.parametersListDouble) {
            typeParams.setParameter(param.name);
        }
    }

    public Optional<DataSound> getDataSound(Integer id) throws NotFoundException {
        Optional<DataSound> data = dataSoundRepository.findById(id);

        if (data.isEmpty())
            throw new NotFoundException("Product not found");

        return data;
    }

    public void delete(DataSound dataSound) {
        var soundTypeOptional = soundTypeRepository.findByName(dataSound.getType());
        if(soundTypeOptional.isPresent()) {
            var soundType = soundTypeOptional.get();
            var paramsType = soundType.soundTypeParameters;
            if(soundType.getDataSounds().size() > 1) {
                var sounds = soundType.getDataSounds();
                var soundParams = dataSound.getDataSoundParameters();

                paramsType.UpdateZeroCrossingDensity(paramsType.getZeroCrossingDensityRaw(), soundParams.getZeroCrossingDensity(), false);

                for (var param:paramsType.parametersListInt) {
                    paramsType.updateParameterValueDelete(param.name);
                }
                for (var param:paramsType.parametersListDouble) {
                    paramsType.updateParameterValueDelete(param.name);
                }
                sounds.remove(dataSound);
                soundTypeRepository.save(soundType);
                //soundTypeParametersRepository.delete(paramsType);
            } else {
                soundTypeParametersRepository.delete(paramsType);
                //soundTypeRepository.delete(soundType);
                }
            }
        dataSoundRepository.delete(dataSound);
        //dataSoundParametersRepository.delete(dataSound.getDataSoundParameters());
    }

}