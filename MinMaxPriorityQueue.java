/* ======= ALGORITHM =========== */
// Maintain a max heap and min heap internally. This allows for us to always 
// have the max and min available in constant time. This requires two different 
// implementations of sink and swim since they are inverted for min and max,
// respectively. Whenever we add a new item to the data structure we add it to
// the minPQ and maxPQ internally; where the min and max heap maintain their
// own ordering. The issue we encounter is deleting an item from one heap requires
// us to delete it from the other heap, however we must iterate linearly to find 
// the value in the other heap. We must also then shift the values in the other
// heap to ensure no lingering null values in the center of the array which will
// affect the array index arithmetic for representing children.

/* ============ COMPLEXITIES ============= */
// Time complexity of getting the max or the min = O(1)
// Time complexity of insert = O(log N)
// Time complexity of deleteMax or deleteMin = O(log N) 
// deleteMax and deleteMin are both done in roughly ~ 2 log N
// Space Complexity of all operations = O(N) 
// Since we are using arrays our space grows with the size of our arrays

import java.lang.reflect.Array;
import java.util.HashSet;

/* Min/max priority queue. Design a data type that supports the following
 * operations:
 * insert, delete the maximum, and delete the minimum (all in logarithmic time)
 * and find the maximum and find the minimum (both in constant time).
 * Hint: Use two heaps.
 */
public class MinMaxPriorityQueue<Key extends Comparable<Key>> {
  // Use array as underlying implementation
  // two arrays to represent 2 heaps
  // resizing array when capcacity is surpassed
  // use HashSet to handle the collision of deleting from both heaps
  // can't have duplicate values inside of our Set so it allows us to
  // compare against when we need to delete a value from the opposing heap
  Key[] minPQ;
  Key[] maxPQ;
  HashSet<Key> deleted;
  int Nmin;
  int Nmax;
  final boolean MAX = false;
  final boolean MIN = true;

  // Constructs a new MinMaxPriorityQueue
  public MinMaxPriorityQueue(int initCapacity) {
    // Not needed but good practice for generic checking
    //@SuppressWarnings("unchecked")
    final Key[] minPQ = (Key[]) Array.newInstance(Comparable.class, initCapacity + 1);
    this.minPQ = minPQ;
    final Key[] maxPQ = (Key[]) Array.newInstance(Comparable.class, initCapacity + 1);
    this.maxPQ = maxPQ;
    this.deleted = new HashSet<Key>();
    this.Nmin = 0;
    this.Nmax = 0;
  }

  // Check if empty, takes parameter for specific heap
  public boolean isEmpty(boolean which) {
    if (which == MIN) return Nmin == 0;
    return Nmax == 0;
  }

  // Return the max in the heap
  public Key max() {
    if (isEmpty(MAX)) return null;
    return maxPQ[1];
  }

  // Return the min in the heap
  public Key min() {
    if (isEmpty(MIN)) return null;
    return minPQ[1];
  }

  // Inserts a key into the PriorityQueue
  public void insert(Key key) {
    if (Nmin >= minPQ.length - 1 && Nmax > maxPQ.length - 1) return;
    // insert into minPQ
    minPQ[++Nmin] = key;
    //swim min into correct place
    swim(MIN, Nmin);
    // n has already been incremented to correct index
    maxPQ[++Nmax] = key;
    //swim max into correct place
    swim(MAX, Nmax);
  }

  // Delete max from maxPQ and return value
  public void deleteMax() {
    if (isEmpty(MAX)) return;
    // Get value at index 1, Max always at index 1
    Key max = maxPQ[1];
    // add the deleted max to the HashSet
    deleted.add(max);
    // swap
    swap(maxPQ, 1, Nmax--);
    sink(MAX, 1);
    // prevent loitering
    maxPQ[Nmax+1] = null;
    
    // Delete the max within the min heap
    if (minPQ[1] != null && deleted.contains(minPQ[1])) {
      deleted.remove(max);
      /// exch + sink
      swap(minPQ, 1, Nmin--);
      sink(MIN, 1);
      minPQ[Nmin+1] = null;
    }
    // check if set needs to be cleared
    if (isEmpty(MIN) && isEmpty(MAX)) {
      deleted.clear();
    }
  }

  // Delete min from minPQ and return value
  public void deleteMin() {
    if (isEmpty(MIN)) return; // Get value at index 1, min always at index 1
    Key min = minPQ[1];
    // Add the min to the deleted HashSet
    deleted.add(min);
    // swap
    swap(minPQ, 1, Nmin--);
    sink(MIN, 1);
    // prevent loitering
    minPQ[Nmin+1] = null;

    // Delete the max within the min heap
    if (maxPQ[1] != null && deleted.contains(maxPQ[1])) {
      deleted.remove(min);
      /// exch + sink
      swap(maxPQ, 1, Nmax--);
      sink(MAX, 1);
      maxPQ[Nmax+1] = null;
    }
    // check if set needs to be cleared
    if (isEmpty(MIN) && isEmpty(MAX)) {
      deleted.clear();
    }
  }

  // Swim method that takes in a boolean to determine
  // if we want to do max / min operations
  private void swim(boolean which, int k) {
    // min version of swim
    if (which == MIN) {
      while (k > 1 && !(less(minPQ[k/2], minPQ[k]))) {
        // swap
        swap(minPQ, k/2, k);
        k /= 2;
      }
    } else {
      // Max version of swim
      while (k > 1 && less(maxPQ[k/2], maxPQ[k])) {
        // swap
        swap(maxPQ, k/2, k);
        k /= 2;
      }
    }
  }

  // More general sink method to be used with
  // both min and max heaps
  private void sink(boolean which, int k) {
    // min version of sink
    if (which == MIN) {
      while ((2*k) <= Nmin) {
        int j = 2*k;
        if (j < Nmin && !(less(minPQ[j], minPQ[j+1]))) j++;
        if ((less(minPQ[k], minPQ[j]))) break;
        // swap
        swap(minPQ, j, k);
        k = j;
      }
    } else {
      // max version of sink
      while ((2*k) <= Nmax) {
        int j = 2*k;
        if (j < Nmax && (less(maxPQ[j], maxPQ[j+1]))) j++;
        if (!(less(maxPQ[k], maxPQ[j]))) break;
        // swap
        swap(maxPQ, j, k);
        k = j;
      }
    }
  }

  // Swap method to work for both min and max heaps
  private void swap(Key[] arr, int min, int index) {
    Key tmp = arr[index];
    arr[index] = arr[min];
    arr[min] = tmp;
  }

  // Method to compare two objects
  private boolean less(Key a, Key b) {
    return a.compareTo(b) < 0;
  }

  // Output the min heap
  public void printMin() {
    System.out.print("Min Heap: ");
    for (int i = 0; i < minPQ.length; i++) {
      if (minPQ[i] == null) continue;
      System.out.print(minPQ[i] + " | ");
    }
    System.out.println();
  }

  // OUtput the max heap
  public void printMax() {
    System.out.print("Max Heap: ");
    for (int i = 0; i < maxPQ.length; i++) {
      if (maxPQ[i] == null) continue;
      System.out.print(maxPQ[i] + " | ");
    }
    System.out.println();
  }

  // Output the set values
  public void printSet() {
    System.out.print("HashSet: " + this.deleted);
    System.out.println();
  }

  /* ======= MAIN METHOD FOR TESTING ======= */
  public static void main(String[] args) {
    /* TESTING WITH INT */
    System.out.println("=======INT TEST=========");
    MinMaxPriorityQueue<Integer> test = new MinMaxPriorityQueue<Integer>(10);
    test.insert(20);
    test.insert(5);
    test.insert(10);
    test.insert(3);
    test.insert(4);
    test.insert(21);

    System.out.println("Printing out Heaps...");
    test.printMin();
    test.printMax();
    test.printSet();
    System.out.println();

    test.deleteMax();
    test.deleteMin();

    test.deleteMax();
    test.deleteMin();

    test.deleteMax();
    test.deleteMin();

    test.deleteMax();
    test.deleteMin();

    test.deleteMax();
    test.deleteMin();

    test.printSet();

    System.out.println("Printing out Heaps...");
    test.printMin();
    test.printMax();
    System.out.println();

    // ======= TEST 2 ========
    System.out.println("=======STRING TEST=========");
    MinMaxPriorityQueue<String> other = new MinMaxPriorityQueue<String>(10);
    other.insert("S");
    other.insert("E");
    other.insert("A");
    other.insert("R");
    other.insert("C");
    other.insert("H");
    other.insert("M");

    System.out.println("Printing out Heaps...");
    other.printMin();
    other.printMax();
    other.printSet();
    System.out.println();

    other.deleteMax();
    other.deleteMin();
    other.printMin();
    other.printMax();
    other.printSet();

    other.deleteMax();
    other.deleteMin();
    other.printMin();
    other.printMax();
    other.printSet();

    other.deleteMax();
    other.deleteMin();
    other.printMin();
    other.printMax();
    other.printSet();

    other.deleteMax();
    other.deleteMin();
    other.printMin();
    other.printMax();
    other.printSet();

    System.out.println("Printing out Heaps...");
    other.printMin();
    other.printMax();
  }
}
