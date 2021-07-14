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
  public int findFloorBinarySearchLogN(int[] floors) {
    // this uses regular binary search over the whole size of the floors
    // start lo at 0 and hi at max index
    int lo = 0;
    int hi = floors.length - 1;
    // call binary search on whole search space
    return binarySearch(floors, lo, hi);
  }
  
  // Binary search implementation
  private int binarySearch(int[] arr, int l, int r) {
    // if left is still below right in array
    if (l <= r) {
      // calculate mid value
      int mid = l + (r - l) / 2;
      // if we came across a broken egg
      // binary search right side of array if we haven't come across a 1 yet
      if (arr[mid] == 0) return binarySearch(arr, mid + 1, r);
      else {
        // we search the left side of the array if we've seen a 1
        int lowerIdx = binarySearch(arr, l, mid - 1);
        // if we got a -1 then we return mid
        if (lowerIdx == -1) return mid;
        // otherwise we return the lowerIdx with the floor
        else return lowerIdx;
      }
    }
    // return -1 for not found
    return -1;
  }
  
  // Method to use powers of two to cut the search space down before we binary search
  // leading us to binary search in roughly 2logF time rather than logN
  public int findFloorBinarySearchLogF(int[] floors) {
    int power2 = 0;
    // iterate over powers of 2 until we find a 1 or go past array size
    while (power2 < floors.length) {
      // if we find a 1 break out of loop
      if (floors[power2] == 1) break;
      // on first iteration power2 needs to be incremented so multiplication
      // will have an affect on the iteration
      else if (power2 == 0) power2++; 
      // double power2
      power2 *= 2;
    }
    // we need to store the previous power of 2 in a variable to use for our range
    int prevPower = power2 / 2;
    // now power2 will be > floors.length, so we take the smaller of the two values
    int minLength = Math.min(power2, floors.length - 1);
    
    // delegate work to binary search
    return binarySearch(floors, prevPower, minLength);
  }
  
  // method for running unit tests more easily than typing them out manually
  public void unitTest(ThrowingEggs eggs, int[] arr, int expected) {
    StringBuilder sb = new StringBuilder();
    sb.append("Expected: ")
      .append(expected)
      .append(" -> test: ");
    String output = eggs.findFloorBinarySearchLogN(arr) == eggs.findFloorBinarySearchLogF(arr) ? "PASS" : "FAIL";
    sb.append(output);
    System.out.println(sb.toString());
  }

  /* MAIN METHOD FOR TESTING */
  public static void main(String[] args) {
    ThrowingEggs eggs = new ThrowingEggs();

    System.out.println("===========Test 1==========");
    int[] test1 = {0,0,0,0,0,0,0,0,0,1,1,1,1,1};
    eggs.unitTest(eggs, test1, 9);

    System.out.println("===========Test 2==========");
    int[] test2 = {0,0,0,0,0,0,0,0};
    eggs.unitTest(eggs, test2, -1);

    System.out.println("===========Test 3==========");
    int[] test3 = {0};
    eggs.unitTest(eggs, test3, -1);

    System.out.println("===========Test 4==========");
    int[] test4 =
    {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1};
    eggs.unitTest(eggs, test4, 29);

    System.out.println("===========Test 5==========");
    int[] test5 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,1,1,1,1,1};
    eggs.unitTest(eggs, test5, 50);

    System.out.println("===========Test 6==========");
    int[] test6 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    eggs.unitTest(eggs, test6, -1);
  }
}
