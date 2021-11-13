package com.soundrecognition.server.sound;
;
import ca.uol.aig.fftpack.RealDoubleFFT;
import javassist.NotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataSoundService {

    private final DataSoundRepository dataSoundRepository;

    DataSoundService(DataSoundRepository dataSoundRepository) {
        this.dataSoundRepository = dataSoundRepository;
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

    public List<Pair<DataSound,Double>> getMostSimilarSounds(DataSound newSound) {
        var sounds = dataSoundRepository.findAll();
        ArrayList<Pair<DataSound,Double>> mostSimilar = new ArrayList<>();
        for (var sound:sounds
             ) {
            Double cor = CalculateSoundSimilarity.correlationCoefficient(sound, newSound);
            System.out.println(cor);
            if(cor.isNaN()) continue;
            sound.setFreqDomainPoints(new ArrayList<>());
            mostSimilar.add(Pair.of(sound, cor));
        }
        Collections.sort(mostSimilar, new Comparator<Pair<DataSound, Double>>() {
            @Override
            public int compare(final Pair<DataSound, Double> o1, final Pair<DataSound, Double> o2) {
                if (o1.getSecond() > o2.getSecond()) {
                    return -1;
                } else if (o1.getSecond().equals(o2.getSecond())) {
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
        dataSoundRepository.save(dataSound);
        return dataSound;
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