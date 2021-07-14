/* ==== ALGORITHM ======= */
// Use a stack that stores the hi and lo indices of subarrays rather than using
// recursion. pop from the stack and call partition (same as regular qsort
// partition) so long as the array length is > 1. Split the partitioned array
// into a left and right subarray and push the values onto the stack and iterate.
// Push the larger subarray onto the stack first so it will get split up first
// on the next iteration. Keep looping until our stack is empty.

/* ======= Complexities ========== */
// Time complexity of NRQSort = O(Nlog N)
// Space complexity of NRQSort = N
// we do add a stack data structure to be used but the memory added isn't
// a greater complexity than what already exists.

import java.util.Stack;

public class NonRecursiveQSort {

  // Client method that calls qsort internally
  public static void NRQSort(Comparable[] array) {
    // call qsort, kickoff the iterative call using stack
    qsort(array, 0, array.length - 1);
  }

  // Internal qsort method, uses iteration instead of recursion
  private static void qsort(Comparable[] array, int lo, int hi) {
    // Create a stack of int[] that stores lo and hi (0, 1 indices)
    Stack<int[]> stack = new Stack<>();
    // Create new range based on hi and lo
    int[] range = {lo, hi};
    // push to stack
    stack.push(range);

    // Loop until stack empty
    while (stack.size() > 0) {
      // save current range as the popped value
      int[] currRange = stack.pop();
      // calculate partition from partition with lo and hi
      int partition = partition(array, currRange[0], currRange[1]);
      // create new ranges for left and right based on partition
      // basically same as binary search
      int[] leftRange = {currRange[0], partition -1};
      int[] rightRange = {partition + 1, currRange[1]};

      // calculate sizes of each array
      int leftArrSize = partition - currRange[0];
      int rightArrSize = currRange[1] - partition;

      // pushing the larger sub array will guarantee we iterate the
      // least number of times (log N);
      if (leftArrSize > rightArrSize) {
        // push as long as size > 1
        if (leftArrSize > 1) {
          stack.push(leftRange);
        }
        if (rightArrSize > 1) {
          stack.push(rightRange);
        }
      } else {
        // case when right arr is bigger than left
        // push right first to guarantee log n iterations
        if (rightArrSize > 1) {
          stack.push(rightRange);
        }
        if (leftArrSize > 1) {
          stack.push(leftRange);
        }
      }
    }
  }

  // Just like normal partition for quicksort
  // Internal partition method that returns partition index
  private static int partition(Comparable[] array, int lo, int hi) {
    // start pivot as lo index
    Comparable pivot = array[lo];
    // set pointers
    int i = lo;
    int j = hi + 1;

    // Loop until manual break
    while(true) {
      // loop while array[i] < pivot
      while (less(array[++i], pivot)) {
        // if i pointer reaches end, break
        if (i == hi) {
          break;
        }
      }
      // loop while pivot < array[j]
      while (less(pivot, array[--j])) {
        // if j pointer at lo break
        if (j == lo) {
          break;
        }
      }
      // when pointers cross break
      if (i >= j) {
        break;
      }
      // swap i -> j
      exch(array, i, j);
    }
    // swap lo -> j
    // places j's value in correct index
    exch(array, lo, j);
    // return correct index for partition value
    return j;
  }

  // Method to check less
  private static boolean less(Comparable a, Comparable b) {
    return a.compareTo(b) <= 0;
  }

  // Method to swap values
  private static void exch(Comparable[] array, int i, int j) {
    Comparable temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  /* ======== MAIN METHOD FOR TESTING ========= */
  public static void main(String[] args) {
    System.out.println("TEST CASE 1: String type");
    String[] array = {"X", "C", "A", "H", "Y", "Z", "T", "B"};

    System.out.println("Shuffled Array");
    for (int i = 0; i < array.length; i++) {
      System.out.print(array[i] + ", ");
    }
    System.out.println();

    // Call sort
    NonRecursiveQSort.NRQSort(array);

    System.out.println("QuickSorted array");
    for (int i = 0; i < array.length; i++) {
      System.out.print(array[i] + ", ");
    }
    System.out.println();

    System.out.println("TEST CASE 2: Integer type");

    Integer[] intArr = {101, 150, 99, 0, 69, 420, 1000};

    System.out.println("Shuffled Array");
    for (int i = 0; i < intArr.length; i++) {
      System.out.print(intArr[i] + ", ");
    }
    System.out.println();

    // Call sort
    NonRecursiveQSort.NRQSort(intArr);

    System.out.println("QuickSorted array");
    for (int i = 0; i < intArr.length; i++) {
      System.out.print(intArr[i] + ", ");
    }
    System.out.println();
  }
}