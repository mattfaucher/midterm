/*
   Throwing eggs from a building. Suppose that you have an N-story building and
   plenty of eggs. Suppose also that an egg is broken if it is thrown off floor F
   or higher, and unhurt otherwise. First, devise a strategy to determine the
   value of F such that the number of broken eggs is ~lg N when using ~lg N
   throws, then find a way to reduce the cost to ~2lg F.
*/

/* 
 * This problem requires that we use binary search to find the first occurrence
 * of an egg breaking. However in order to optimize our solution to be ~2logF
 * we must use power's of 2 traversal of an array to find the first occurrence
 * of a breaking egg, then use binary search on the smaller space rather than
 * binary searching over the entire array of size N.
 */

/* ======= COMPLEXITIES ============ */
// Time complexity of Binary search = O(log N)
// Space complexity of Binary Search = N
// Time complexity of findFloorLogF = O(~2 log F)
// Space complexity of findFloorLogF = N

public class ThrowingEggs {
  /*
   * Algorithm for solution is binary search
   * for 2 lg F -> start at floor 1->2->4->8 etc
   * until first egg breaks
   * Once we find where first egg breaks use binary
   * search in the smaller space (range < F)
   */

  // Find the floor in Log time using Binary Search
  public int findFloorBinarySearchN(int[] floors) {
    // this uses regular binary search over the whole size of the floors
    // start lo at 0 and hi at max index
    int lo = 0;
    int hi = floors.length - 1;
    // call binary search on whole search space
    return binarySearch(floors, lo, hi);
  }

  // Find the floor in LogF time using power of 2 increments to decrease
  // search space, then using Binary search within that space
  public int findFloorBinarySearchLogF(int[] floors) {
    // broken is 1
    int broken = 1;
    int search = 0;

    // Iterate over powers of 2 to decrease search space
    while (search < floors.length) {
      // if we found the broken floor, break out
      if (broken == floors[search]) break;
      // use this to be able to increment by powers of 2 (0 can't multiply alone)
      if (search == 0) search = 2;
      else search *= 2;
    }

    // set prevFloor to previous power of 2 so we have a reduced range
    // searching in between 2^k and 2^k+1
    int prevPowerOf2 = search / 2;
    // Find and use the minimum range (array size vs search value)
    search = Math.min(floors.length - 1, search);
    // use the value of search to binary search the smaller space
    int floor = binarySearch(floors, prevPowerOf2 + 1, search - 1);
    // if we didn't get a -1, return the floor
    if (floor != -1) return floor;
    // otherwise return the search value
    else return search;
  }

  // Essentially default binary search implementation
  // except we only need to check if we've come across a 1 yet
  private int binarySearch(int[] arr, int L, int R) {
    // broken is 1, 1 signifies we have a broken egg
    int broken = 1;

    // if lo > hi we want to stop
    if (R >= L) {
      // calculate new mid
      int mid = L + (R - L) / 2;
      // check if we are at an index where no broken eggs yet
      if (arr[mid] < broken) {
        // if we haven't come across a 1 yet, search right half
        return binarySearch(arr, mid + 1, R);
      } else {
        // return binary search call on right half of array
        int brokenFloor = binarySearch(arr, L, mid - 1);
        // check for not found
        if (brokenFloor == -1) {
          // return prev mid (index) if it's -1
          return mid;
        } else {
          // return found lowest egg broken floor
          return brokenFloor;
        }
      }
    }
    // default return -1
    return -1;
  }

  /* MAIN METHOD FOR TESTING */
  public static void main(String[] args) {
    ThrowingEggs eggs = new ThrowingEggs();
    System.out.println("===========Test 1 ==========");
    int[] floors = {0,0,0,0,0,0,0,0,0,1,1,1,1,1};
    if (eggs.findFloorBinarySearchN(floors) == eggs.findFloorBinarySearchLogF(floors)) {
      System.out.println("Test 1 passed");
    }
  }
}
