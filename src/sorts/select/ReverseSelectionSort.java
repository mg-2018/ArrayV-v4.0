/**
 * 
 */
package sorts.select;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/*
 * 
MIT License

Copyright (c) 2021 mingyue12

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

/**
 * @author mingyue12
 *
 */
public final class ReverseSelectionSort extends Sort {

    /**
     * @param arrayVisualizer
     */
    public ReverseSelectionSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        // TODO Auto-generated constructor stub
        this.setSortListName("Reverse Selection");
        this.setRunAllSortsName("Reverse Selection Sort");
        this.setRunSortName("Reverse Selection Sort");
        this.setCategory("Selection Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
        // TODO Auto-generated method stub
        for (int i = sortLength - 1; i >= 0; i--) {
            int highestindex = 0;
            
            for (int j = 1; j < i + 1; j++) {
                Highlights.markArray(2, j);
                Delays.sleep(0.01);
                
                if (Reads.compareValues(array[j], array[highestindex]) == 1){
                    highestindex = j;
                    Highlights.markArray(1, highestindex);
                    Delays.sleep(0.01);
                }
            }
            Writes.swap(array, i, highestindex, 0.02, true, false);
        }

    }

}
