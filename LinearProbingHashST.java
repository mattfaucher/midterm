/* =========== Final Array after Deleting C from the SymbolTable ============ */
/*
 * idx: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
 * key: P   M   -   -   A   H   S   L   -   -   E    -    -    -    R    X
 * val: 10  9   -   -   8   5   0   11  -   -   12   -    -    -    3    7
 */


/* ============ ALGORITHM / ANSWER TO QUESTION FROM MIDTERM ======== */
/* ======= delete() from page 471 explained in English ============= */
/* delete takes in a key as a parameter. We then check if our SymbolTable contains
 * that key, if this turns out to be false, we just return and exit. If we do
 * have that key in our SymbolTable, then we store the hash value of our key in
 * the int variable i. We then loop while they key passed in does NOT equal the
 * key located at index i. So long as we haven't found the matching key we
 * increment i and modulus by M so we can wrap back around to 0 if necessary.
 * Once we come across a matching key to matching i index key, we set the key +
 * val at [i] to be null (delete them). After we delete we increment i the same
 * as before with the modulus to handle wrap around. We then enter another
 * while loop that iterates so long as the value at index i isn't null. In here
 * we store the key + val at index i in separate variables, then we set key +
 * val at i to be null, so we basically delete them. following this we
 * decrement N since we are deleting again. Then we call put (this will
 * increment N within itself) and pass in the stored key + val. This put will
 * then reinsert our key + val back to their original hash location (if
 * possible) or will increment i until it gets to an open location. The purpose
 * of this is to reduce clustering and to also keep keys as close to their
 * original hash location as possible. We then increment i the same way we have
 * been and iterate again and repeat this process until we hit our first null
 * location.  Lastly we decrement to handle the initial deletion before the
 * second while loop and then resize if necessary.
 */

/* ========= COMPLEXITIES =========== */
// Time complexity of put = O(N) worst case, average case = O(1)
// Time complexity of get = O(N) worst case, average case = O(1)
// Time complexity of delete = O(N) worst case, average case = O(1)
// Space complexity = O(N) since we use associative arrays (2N)

/* Code from the book, Annotated */
public class LinearProbingHashST<Key, Value> {
  // N == number of pairs in symbol table
  private int N;
  // M is size of array to start
  private int M = 16;
  private Key[] keys;
  private Value[] vals;  // the values
  // number of key-value pairs in the table
  // size of linear-probing table
  // the keys
  public LinearProbingHashST() {
    // use associative arrays to represent the symbol table
    keys = (Key[])   new Object[M];
    vals = (Value[]) new Object[M];
  }

  // Method to determine index of a given key
  private int hash(Key key){
    return (key.hashCode() & 0x7fffffff) % M;
  }

  // will resize the array when needed
  private void resize(){};

  public void put(Key key, Value val) {
    // resize if needed
    if (N >= M/2) resize(2*M);  // double M (see text)
    int i;
    // iterate starting at index hash, iterate util keys == null
    for (i = hash(key); keys[i] != null; i = (i + 1) % M)
      // if you find a matching key, update the value to new value
      if (keys[i].equals(key)) { vals[i] = val; return; }
    // otherwise place new key/val at the index you stopped at
    keys[i] = key;
    vals[i] = val;
    // increment N since we put new pair in
    N++;
  }

  // get value from a key
  public Value get(Key key) {
    // iterate from the original hash value until we hit a null
    for (int i = hash(key); keys[i] != null; i = (i + 1) % M)
      // if we have a key match return the value
      if (keys[i].equals(key))
        return vals[i];
    // else return null (nothing)
    return null;
  }

  // Delete a key from SymbolTable
  public void delete(Key key) {
    // if we don't have the key then just return (doesn't exist)
    if (!contains(key)) return;
    // store the hash value of the passed in key
    int i = hash(key);
    // while the key doesn't equal the key at index i, increment i until we find
    // a match
    while (!key.equals(keys[i]))
      // increment and use modulus to wrap around back to zero
      i = (i + 1) % M;
    // remove the key and val at index i (delete it)
    keys[i] = null;
    vals[i] = null;
    // increment i + wraparound if needed
    i = (i + 1) % M;
    // while we aren't currently at a null key, iterate
    while (keys[i] != null) {
      // store key+val at index i
      Key   keyToRedo = keys[i];
      Value valToRedo = vals[i];
      // remove key and val and index i
      keys[i] = null;
      vals[i] = null;
      // decrement since we deleted a key/val pair
      N--;
      // call put and pass in the previously deleted key, val
      // put will then attempt to place they key at the original hash
      // index if possible, if not it will use linear probing to find next
      // available index
      // this will reduce clustering
      put(keyToRedo, valToRedo);
      // increment i and continue with next item until we hit a null
      i = (i + 1) % M;
    }
    // decrement for initial deletion
    N--;
    // resize if needed
    if (N > 0 N == M/8) resize(M/2);
  }
}
