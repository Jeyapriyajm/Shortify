import java.util.Arrays;

public class PerformanceEvaluator {

    public static long evaluateSortingPerformance(int[] array, String algorithm) {
        int[] tempArray = Arrays.copyOf(array, array.length);
        long startTime = System.nanoTime();

        switch (algorithm) {
            case "Insertion Sort":
                SortingAlgorithms.insertionSort(tempArray);
                break;
            case "Shell Sort":
                SortingAlgorithms.shellSort(tempArray);
                break;
            case "Merge Sort":
                SortingAlgorithms.mergeSort(tempArray, 0, tempArray.length - 1);
                break;
            case "Quick Sort":
                SortingAlgorithms.quickSort(tempArray, 0, tempArray.length - 1);
                break;
            case "Heap Sort":
                SortingAlgorithms.heapSort(tempArray);
                break;
            default:
                throw new IllegalArgumentException("Unknown sorting algorithm: " + algorithm);
        }

        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}
