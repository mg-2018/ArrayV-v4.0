package main;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Random;
import java.util.function.IntConsumer;

import panes.JErrorPane;
import utils.Delays;
import utils.Highlights;
import utils.Shuffles;
import utils.Distributions;
import utils.Statistics;
import utils.Writes;

/*
 *
MIT License

Copyright (c) 2019 w0rthy
Copyright (c) 2020 ArrayV 4.0 Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

final public class ArrayManager {
    private utils.Shuffles[] shuffleTypes;
    private utils.Distributions[] distributionTypes;
    private String[] shuffleIDs;
    private String[] distributionIDs;

    private boolean hadDistributionAllocationError;

    private volatile boolean MUTABLE;

    private ArrayVisualizer ArrayVisualizer;
    private Delays Delays;
    private Highlights Highlights;
    private Shuffles Shuffles;
    private Distributions Distributions;
    private Writes Writes;

    public ArrayManager(ArrayVisualizer arrayVisualizer) {
        this.ArrayVisualizer = arrayVisualizer;

        this.Shuffles = utils.Shuffles.RANDOM;
        this.Distributions = utils.Distributions.LINEAR;
        this.shuffleTypes = utils.Shuffles.values();
        this.distributionTypes = utils.Distributions.values();

        hadDistributionAllocationError = false;

        this.Delays = ArrayVisualizer.getDelays();
        this.Highlights = ArrayVisualizer.getHighlights();
        this.Writes = ArrayVisualizer.getWrites();

        this.shuffleIDs = new String[this.shuffleTypes.length];
        for (int i = 0; i < this.shuffleTypes.length; i++)
            this.shuffleIDs[i] = this.shuffleTypes[i].getName();

        this.distributionIDs = new String[this.distributionTypes.length];
        for (int i = 0; i < this.distributionTypes.length; i++)
            this.distributionIDs[i] = this.distributionTypes[i].getName();

        this.MUTABLE = true;
    }

    public boolean isLengthMutable() {
        return this.MUTABLE;
    }
    public void toggleMutableLength(boolean Bool) {
        this.MUTABLE = Bool;
    }

    //TODO: Fix minimum to zero
    public void initializeArray(int[] array) {
        if(ArrayVisualizer.doingStabilityCheck()) {
            ArrayVisualizer.resetStabilityTable();
            ArrayVisualizer.resetIndexTable();
        }

        int currentLen = ArrayVisualizer.getCurrentLength();

        int[] temp;
        try {
            temp = new int[currentLen];
        } catch (OutOfMemoryError e) {
            if (!hadDistributionAllocationError)
                JErrorPane.invokeCustomErrorMessage("Failed to allocate temporary array for distribution. (will use main array, which may have side-effects.)");
            hadDistributionAllocationError = true;
            temp = array;
        }
        Distributions.initializeArray(temp, this.ArrayVisualizer);

        double uniqueFactor = (double)currentLen/ArrayVisualizer.getUniqueItems();
        for(int i = 0; i < currentLen; i++)
            temp[i] = (int)(uniqueFactor*(int)(temp[i]/uniqueFactor))+(int)uniqueFactor/2;

        System.arraycopy(temp, 0, array, 0, currentLen);
        ArrayVisualizer.updateNow();
    }

    public String[] getShuffleIDs() {
        return this.shuffleIDs;
    }
    public Shuffles[] getShuffles() {
        return this.shuffleTypes;
    }
    public Shuffles getShuffle() {
        return this.Shuffles;
    }
    public void setShuffle(Shuffles choice) {
        this.Shuffles = choice;
    }

    public String[] getDistributionIDs() {
        return this.distributionIDs;
    }
    public Distributions[] getDistributions() {
        return this.distributionTypes;
    }
    public Distributions getDistribution() {
        return this.Distributions;
    }
    public void setDistribution(Distributions choice) {
        this.Distributions = choice;
        this.Distributions.selectDistribution(ArrayVisualizer.getArray(), ArrayVisualizer);
        if (!ArrayVisualizer.isActive())
            this.initializeArray(ArrayVisualizer.getArray());
    }

    public void shuffleArray(int[] array, int currentLen, ArrayVisualizer ArrayVisualizer) {
        this.initializeArray(array);

        String tmp = ArrayVisualizer.getHeading();
        ArrayVisualizer.setHeading("Shuffling...");

        double speed = Delays.getSleepRatio();

        if(ArrayVisualizer.isActive()) {
            double sleepRatio = ArrayVisualizer.getCurrentLength()/1024d;
            Delays.setSleepRatio(sleepRatio);
        }

        Shuffles tempShuffle = this.Shuffles;
        if(Distributions == Distributions.RANDOM || Distributions == Distributions.EQUAL)
            this.Shuffles = Shuffles.ALREADY;
        Shuffles.shuffleArray(array, this.ArrayVisualizer, Delays, Highlights, Writes);
        this.Shuffles = tempShuffle;

        Delays.setSleepRatio(speed);

        Highlights.clearAllMarks();
        ArrayVisualizer.setHeading(tmp);
    }

    private void stableShuffle(int[] array, int length) {
        boolean delay = ArrayVisualizer.shuffleEnabled();
        double sleep = delay ? 1 : 0;

        double speed = Delays.getSleepRatio();

        if(ArrayVisualizer.isActive()) {
            double sleepRatio = ArrayVisualizer.getCurrentLength()/1024d;
            Delays.setSleepRatio(sleepRatio);
        }

        int[] counts    = new int[length];
        int[] prefixSum = new int[length];
        int[] table     = ArrayVisualizer.getStabilityTable();

        for(int i = 0; i < length; i++)
            counts[array[i]]++;

        prefixSum[0] = counts[0];
        for(int i = 1; i < length; i++)
            prefixSum[i] = counts[i] + prefixSum[i-1];

        for(int i = 0, j = 0; j < length; i++) {
            while(counts[i] > 0) {
                table[j++] = i;
                counts[i]--;
            }
        }

        for(int i = length-1; i >= 0; i--)
            Writes.write(array, i, --prefixSum[array[i]], 0.5, true, false);

        ArrayVisualizer.setIndexTable();

        Delays.setSleepRatio(speed);
    }

    public void refreshArray(int[] array, int currentLen, ArrayVisualizer ArrayVisualizer) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            JErrorPane.invokeErrorMessage(e);
        }

        ArrayVisualizer.resetAllStatistics();
        Highlights.clearAllMarks();

        ArrayVisualizer.setHeading("");
        if (!ArrayVisualizer.useAntiQSort()) {
            this.shuffleArray(array, currentLen, ArrayVisualizer);

            if(ArrayVisualizer.doingStabilityCheck())
                this.stableShuffle(array, currentLen);

            int[] validateArray = ArrayVisualizer.getValidationArray();
            if (validateArray != null) {
                System.arraycopy(array, 0, validateArray, 0, currentLen);
                Arrays.sort(validateArray, 0, currentLen);
                if (ArrayVisualizer.reversedComparator()) {
                    for (int i = 0, j = currentLen - 1; i < j; i++, j--) {
                        int temp = validateArray[i];
                        validateArray[i] = validateArray[j];
                        validateArray[j] = temp;
                    }
                }
            }
        }

        Highlights.clearAllMarks();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            JErrorPane.invokeErrorMessage(e);
        }

        ArrayVisualizer.resetAllStatistics();
    }
}