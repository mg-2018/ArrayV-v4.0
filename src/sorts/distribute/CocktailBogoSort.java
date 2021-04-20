package sorts.distribute;

import main.ArrayVisualizer;
import sorts.templates.BogoSorting;

public final class CocktailBogoSort extends BogoSorting {
    public CocktailBogoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Cocktail Bogo");
        this.setRunAllSortsName("Cocktail Bogo Sort");
        this.setRunSortName("Cocktail Bogosort");
        this.setCategory("Impractical Sorts");
        this.setComparisonBased(false);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(512);
        this.setBogoSort(true);
    }

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        int min = 0;
        int max = length;

        while (min < max-1) {
            if (this.isMinSorted(array, min, max)) {
                Highlights.markArray(3, min);
                ++min;
            }
            if (this.isMaxSorted(array, min, max)) {
                Highlights.markArray(4, max-1);
                --max;
            }

            this.bogoSwap(array, min, max, false);
        }
    }
}
