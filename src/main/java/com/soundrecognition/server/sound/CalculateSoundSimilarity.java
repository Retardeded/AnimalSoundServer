package com.soundrecognition.server.sound;

public class CalculateSoundSimilarity {

    public static double correlationCoefficient(DataSound newSound, DataSound compareSound)
    {
        var newSoundData = newSound.getFreqDomainPoints();
        var compareSoundData = compareSound.getFreqDomainPoints();
        int n = Math.min(newSoundData.size(),compareSoundData.size());

        int sum_X = 0, sum_Y = 0, sum_XY = 0;
        int squareSum_X = 0, squareSum_Y = 0;

        for (int i = 0; i < n; i++)
        {
            // sum of elements of array X.
            int X = (int)newSoundData.get(i).getY();
            int Y = (int)compareSoundData.get(i).getY();
            sum_X = sum_X + X;

            // sum of elements of array Y.
            sum_Y = sum_Y + Y;

            // sum of X[i] * Y[i].
            sum_XY = sum_XY + X * Y;

            // sum of square of array elements.
            squareSum_X = squareSum_X + X * X;
            squareSum_Y = squareSum_Y + Y * Y;
        }

        // use formula for calculating correlation coefficient.
        double corr = (n * sum_XY - sum_X * sum_Y)
                / Math.sqrt((n * squareSum_X - sum_X * sum_X)
                * (n * squareSum_Y - sum_Y * sum_Y));

        return corr;
    }
}
