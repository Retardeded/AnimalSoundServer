package com.soundrecognition.service;
;
import ca.uol.aig.fftpack.RealDoubleFFT;
import com.soundrecognition.model.*;
import com.soundrecognition.repository.DataSoundParametersRepository;
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
    private final DataSoundParametersRepository dataSoundParametersRepository;
    private final SoundTypeParametersRepository soundTypeParametersRepository;
    final int topListSize = 3;
    final int minSignalEnvelope = 200;
    final int minRootMeanSquareEnergy = 90;
    final String noiseTitle = "It's background noise";

    DataSoundService(DataSoundRepository dataSoundRepository, SoundTypeRepository soundTypeRepository, DataSoundParametersRepository dataSoundParametersRepository, SoundTypeParametersRepository soundTypeParametersRepository) {
        this.dataSoundRepository = dataSoundRepository;
        this.soundTypeRepository = soundTypeRepository;
        this.dataSoundParametersRepository = dataSoundParametersRepository;
        this.soundTypeParametersRepository = soundTypeParametersRepository;
    }

    public List<DataSound> getDataSounds()
    {
       return  dataSoundRepository.findAll();
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
            data.add(soundType);
        }
        return data;
    }

    private void cleanSoundTypeData(SoundType soundType) {
        for (var sound:soundType.getDataSounds())
        {
            sound.setFreqDomainPoints(new ArrayList<>());
            sound.setTimeDomainPoints(new ArrayList<>());
        }
    }

    private void cleanSoundData(DataSound sound) {
        sound.setFreqDomainPoints(new ArrayList<>());
        sound.setTimeDomainPoints(new ArrayList<>());
    }

    private DataSoundParameters getDataSoundParameters(DataSound dataSound) {
        var zeroCrossingDensity = CalculateSoundSimilarity.zeroCrossingDensity(dataSound);
        var rootMeanSquareEnergy = CalculateSoundSimilarity.rootMeanSquareEnergy(dataSound);
        var signalEnvelope = CalculateSoundSimilarity.signalEnvelope(dataSound);
        var parameters = new DataSoundParameters("", Arrays.asList(signalEnvelope), Arrays.asList(rootMeanSquareEnergy), zeroCrossingDensity);
        return parameters;
    }

    public DataSound save(DataSound dataSoundRaw) {
        DataSound dataSound = cutSoundNoise(dataSoundRaw);
        if(dataSound.getTimeDomainPoints().size() == 0)
            return dataSound;

        dataSound = calculateFullSignalFrequencyDomain(dataSound);
        DataSoundParameters newSoundParams = getDataSoundParameters(dataSound);
        dataSound.setDataSoundParameters(newSoundParams);
        SoundTypeParameters soundTypeParameters = new SoundTypeParameters(dataSound.getType(), newSoundParams.signalEnvelope, newSoundParams.rootMeanSquareEnergy, newSoundParams.zeroCrossingDensity);
        dataSoundParametersRepository.save(newSoundParams);
        dataSoundRepository.save(dataSound);
        if(!dataSound.getType().equals("")) {
            var soundTypeOptional = soundTypeRepository.findByName(dataSound.getType());
            if(soundTypeOptional.isPresent()) {
                var soundType = soundTypeOptional.get();
                var paramsOptional = soundTypeParametersRepository.findByTypeName(soundType.getName());
                if(paramsOptional.isPresent()) {
                    var soundTypeParametersPresent = paramsOptional.get();
                    var currentZeroCrossingDensity = soundTypeParametersPresent.getZeroCrossingDensityRaw();
                    soundTypeParametersPresent.UpdateZeroCrossingDensity(currentZeroCrossingDensity, soundTypeParameters.getZeroCrossingDensity(), true);
                    soundTypeParametersPresent.calculateNewParamAverageAdd(soundTypeParametersPresent.getSignalEnvelopeRaw(), soundTypeParameters.getSignalEnvelopeRaw(), soundTypeParametersPresent.signalEnvelopeCount);
                    soundTypeParametersPresent.calculateNewParamAverageAdd(soundTypeParametersPresent.getRootMeanSquareEnergyRaw(), soundTypeParameters.getRootMeanSquareEnergyRaw(), soundTypeParametersPresent.rootMeanSquareEnergyCount);

                    var list = soundType.getDataSounds();
                    cleanSoundData(dataSound);
                    list.add(dataSound);
                    soundTypeParametersRepository.save(soundTypeParametersPresent);
                }
            }  else {
                System.out.println("tuuu1");
                soundTypeParameters.typeName = dataSound.getType();
                var sounds = Arrays.asList(dataSound);
                SoundType newSoundType = new SoundType(dataSound.getType(), sounds, soundTypeParameters);
                soundTypeParametersRepository.save(soundTypeParameters);
                soundTypeRepository.save(newSoundType);
                System.out.println("tuuu1");
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


    private DataSound calculateFullSignalFrequencyDomain(DataSound dataSound) {
        int n = Math.toIntExact(dataSound.getPointsInGraphs());
        int m = Math.toIntExact(dataSound.getNumOfGraphs());
        var audioData = dataSound.getTimeDomainPoints();
        var dataAmplitudeFullSignal = new double[n/2];
        var transformer = new RealDoubleFFT(n);
        double[] toTransform = new double[n];
        var dataFreqDomain = new ArrayList<DataPoint>();
        for(int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                toTransform[i] = audioData.get(i+j*n).getY() / n;
                //toTransform[i] = audioData.get(i+j*n).getY();
            }
            transformer.ft(toTransform);
            for (int i = 0; i < n/2; i++) {
                dataAmplitudeFullSignal[i] += toTransform[i*2+1] * toTransform[i*2+1] + toTransform[i*2] * toTransform[i*2];
                //dataFreqDomain.add(new DataPoint(i, toTransform[i*2+1] * toTransform[i*2+1] + toTransform[i*2] * toTransform[i*2]));
            }
            //System.out.println(dataFreqDomain.size());
        }
        for (int i = 0; i < n/2; i++) {
            dataFreqDomain.add(new DataPoint(i, dataAmplitudeFullSignal[i]/m));
        }
        dataSound.setFreqDomainPoints(dataFreqDomain);
        return dataSound;
    }

    public List<Pair<DataSound, SoundsFreqCoefficients>> getMostSimilarSoundsFreqDomain(DataSound newSound) {
        var sounds = dataSoundRepository.findAll();
        var newSoundWithFreq = calculateFullSignalFrequencyDomain(newSound);
        var newSoundFreqValues = getFreqValues(newSoundWithFreq);

        ArrayList<Pair<DataSound, SoundsFreqCoefficients>> mostSimilar = new ArrayList<>();


        for (int i =0; i < sounds.size(); i++) {
            var freqValues = getFreqValues(sounds.get(i));
            System.out.println(freqValues);
            var cor = CalculateSoundSimilarity.calculateCoefficient(newSoundFreqValues, freqValues, Math.min(newSoundFreqValues.size(),freqValues.size()));
            System.out.println(cor);
            //parameters.setFreqDomainPoints(new ArrayList<>());
            mostSimilar.add(Pair.of(sounds.get(i), new SoundsFreqCoefficients(cor)));
        }

        Collections.sort(mostSimilar, new Comparator<Pair<DataSound, SoundsFreqCoefficients>>() {
            @Override
            public int compare(final Pair<DataSound, SoundsFreqCoefficients> o1, final Pair<DataSound, SoundsFreqCoefficients> o2) {
                if (o1.getSecond().mergedCoefficient > o2.getSecond().mergedCoefficient) {
                    return -1;
                } else if (o1.getSecond().mergedCoefficient == (o2.getSecond().mergedCoefficient)) {
                    return 0; // You can change this to make it then look at the
                    //words alphabetical order
                } else {
                    return 1;
                }
            }
        });
        return mostSimilar.subList(0,Math.min(mostSimilar.size(),3));
    }


    private List<Integer> getFreqValues(DataSound newSoundWithFreq) {
        var freqDomain = newSoundWithFreq.getFreqDomainPoints();
        int n = Math.toIntExact(freqDomain.size());
        Integer[] freqValues = new Integer[n];
        for(int i = 0; i < n; i++) {
            freqValues[i] = (int) freqDomain.get(i).getY();
        }
        return Arrays.asList(freqValues);
    }

    public List<Pair<SoundType, SoundsTimeCoefficients>> getMostSimilarSoundsTimeDomain(DataSound newSound) {
        var typesParams = soundTypeRepository.findAll();
        var newSoundParam = getDataSoundParameters(newSound);
        ArrayList<Pair<SoundType, SoundsTimeCoefficients>> mostSimilar = new ArrayList<>();

        System.out.println("TUUUU");
        System.out.println(typesParams.size());
        System.out.println("TUUUU");

        for (int i =0; i < typesParams.size(); i++) {
            var params = typesParams.get(i).soundTypeParameters;
            System.out.println(params);
            var cor = CalculateSoundSimilarity.correlationParamsCoefficient(params, newSoundParam);
            System.out.println(cor);
            //parameters.setFreqDomainPoints(new ArrayList<>());
            cleanSoundTypeData(typesParams.get(i));
            mostSimilar.add(Pair.of(typesParams.get(i), cor));
        }

        Collections.sort(mostSimilar, new Comparator<Pair<SoundType, SoundsTimeCoefficients>>() {
            @Override
            public int compare(final Pair<SoundType, SoundsTimeCoefficients> o1, final Pair<SoundType, SoundsTimeCoefficients> o2) {
                if (o1.getSecond().mergedCoefficient > o2.getSecond().mergedCoefficient) {
                    return -1;
                } else if (o1.getSecond().mergedCoefficient == (o2.getSecond().mergedCoefficient)) {
                    return 0; // You can change this to make it then look at the
                    //words alphabetical order
                } else {
                    return 1;
                }
            }
        });
        var topList = mostSimilar.subList(0,Math.min(mostSimilar.size(), topListSize));
        for (var listItem:topList
             ) {
            var soundType = listItem.getFirst();
            makeSoundTypeRightForSendingThroughNetwork(soundType);
        }
        return mostSimilar.subList(0,Math.min(mostSimilar.size(), topListSize));
    }

    private void makeSoundTypeRightForSendingThroughNetwork(SoundType soundType) {
        cleanSoundTypeData(soundType);
        var typeParams = soundType.soundTypeParameters;
        typeParams.setRootMeanSquareEnergy(typeParams.getRootMeanSquareEnergyWeighted());
        typeParams.setSignalEnvelope(typeParams.getSignalEnvelopeWeighted());
        typeParams.setZeroCrossingDensity(typeParams.getZeroCrossingDensityWeighted());
    }

    public Optional<DataSound> getDataSound(Integer id) throws NotFoundException {
        Optional<DataSound> data = dataSoundRepository.findById(id);

        if (data == null)
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

                int n = sounds.size();
                paramsType.UpdateZeroCrossingDensity(paramsType.getZeroCrossingDensityRaw(), soundParams.getZeroCrossingDensity(), false);
                paramsType.calculateNewParamAverageDelete(paramsType.getSignalEnvelopeRaw(), soundParams.signalEnvelope, paramsType.signalEnvelopeCount);
                paramsType.calculateNewParamAverageDelete(paramsType.getRootMeanSquareEnergyRaw(), soundParams.rootMeanSquareEnergy, paramsType.rootMeanSquareEnergyCount);

                sounds.remove(dataSound);
               soundTypeRepository.save(soundType);
                soundTypeParametersRepository.delete(paramsType);
                dataSoundRepository.delete(dataSound);
            } else {
                soundTypeParametersRepository.delete(paramsType);
                soundTypeRepository.delete(soundType);
                }
            }
        else {
            dataSoundRepository.delete(dataSound);
        }
        //dataSoundParametersRepository.delete(dataSound.getDataSoundParameters());
    }

    private ArrayList<DataGraph> loadDataSound(List<DataPoint> soundData, Integer pointsInGraphs) {
        var dataGraphs = new ArrayList<DataGraph>();
        var numberOfGraphs = (soundData.size() / pointsInGraphs-1);
        for (int i = 0; i < numberOfGraphs; i++) {
            var graph = new DataGraph(
                    soundData.subList(
                            ((i * pointsInGraphs)),
                            ((i + 1) * pointsInGraphs)
                    )
            );
            dataGraphs.add(graph);
        }
        return dataGraphs;
    }

}