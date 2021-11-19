package com.soundrecognition.service;
;
import ca.uol.aig.fftpack.RealDoubleFFT;
import com.soundrecognition.model.*;
import com.soundrecognition.repository.DataSoundParametersRepository;
import com.soundrecognition.repository.DataSoundRepository;
import com.soundrecognition.soundprocessing.CalculateSoundSimilarity;
import javassist.NotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataSoundService {

    private final DataSoundRepository dataSoundRepository;
    private final DataSoundParametersRepository dataSoundParametersRepository;

    DataSoundService(DataSoundRepository dataSoundRepository, DataSoundParametersRepository dataSoundParametersRepository) {
        this.dataSoundRepository = dataSoundRepository;
        this.dataSoundParametersRepository = dataSoundParametersRepository;
    }

    public List<DataSound> getDataSounds()
    {
       return  dataSoundRepository.findAll();
    }

    public List<DataSound> getSoundInfoOnly()
    {
        ArrayList<DataSound> data = new ArrayList<>();
        for (var sound: dataSoundRepository.findAll() ) {
            sound.setFreqDomainPoints(new ArrayList<>());
            sound.setTimeDomainPoints(new ArrayList<>());
            data.add(sound);
        }
        return data;
    }

    private DataSoundParameters getDataSoundParameters(DataSound dataSound) {
        var zeroCrossingDensity = CalculateSoundSimilarity.zeroCrossingDensity(dataSound);
        var rootMeanSquareEnergy = CalculateSoundSimilarity.rootMeanSquareEnergy(dataSound);
        var signalEnvelope = CalculateSoundSimilarity.signalEnvelope(dataSound);
        var parameters = new DataSoundParameters(Arrays.asList(signalEnvelope), Arrays.asList(rootMeanSquareEnergy), zeroCrossingDensity);
        return parameters;
    }

    public DataSound save(DataSound dataSound) {

        int n = (int) (Math.toIntExact(dataSound.getPointsInGraphs())*dataSound.getNumOfGraphs());
        var audioData = dataSound.getTimeDomainPoints();
        var transformer = new RealDoubleFFT(n);
        double[] toTransform = new double[n];
        var dataFreqDomain = new ArrayList<DataPoint>();
        for(int j = 0; j < 1; j++) {
            for (int i = 0; i < n; i++) {
                //toTransform[i] = audioData[i].toDouble() / Short.MAX_VALUE
                toTransform[i] = audioData.get(i+j*n).getY();
            }
            transformer.ft(toTransform);
            for (int i = 0; i < n; i++) {
                dataFreqDomain.add(new DataPoint(i+j*n, toTransform[i]));
            }
            //System.out.println(dataFreqDomain.size());
        }
        dataSound.setFreqDomainPoints(dataFreqDomain);
        DataSoundParameters parameters = getDataSoundParameters(dataSound);

        dataSoundRepository.save(dataSound);
        dataSoundParametersRepository.save(parameters);
        return dataSound;
    }

    public List<Pair<DataSound, SoundsCoefficients>> getMostSimilarSounds(DataSound newSound) {
        var sounds = dataSoundRepository.findAll();
        var soundsParams = dataSoundParametersRepository.findAll();
        var newSoundParam = getDataSoundParameters(newSound);
        ArrayList<Pair<DataSound,SoundsCoefficients>> mostSimilar = new ArrayList<>();


        for (int i =0; i < soundsParams.size(); i++) {
            var params = soundsParams.get(i);
            System.out.println(params);
            var cor = CalculateSoundSimilarity.correlationParamsCoefficient(params, newSoundParam);
            System.out.println(cor);
            //parameters.setFreqDomainPoints(new ArrayList<>());
            mostSimilar.add(Pair.of(sounds.get(i), cor));

        }

        Collections.sort(mostSimilar, new Comparator<Pair<DataSound, SoundsCoefficients>>() {
            @Override
            public int compare(final Pair<DataSound, SoundsCoefficients> o1, final Pair<DataSound, SoundsCoefficients> o2) {
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

    public Optional<DataSound> getDataSound(Integer id) throws NotFoundException {
        Optional<DataSound> data = dataSoundRepository.findById(id);

        if (data == null)
            throw new NotFoundException("Product not found");

        return data;
    }

    public void delete(DataSound dataSound) {
        dataSoundRepository.delete(dataSound);
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