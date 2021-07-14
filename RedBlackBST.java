/* ============== ALGORITHM ================= */
/* The main purpose of what has been implemented in this RedBlackBST
 implementation is to remove the use of a single bit from each node
 in order to optimize the storage of the program. In doing this we now
 have to determine if a node is red based on a comparison between the
 children of the given node. We run into issues when we only have 1
 child and have nothing to compare with. That was the trickiest part
 of this problem. To signify that a node is red we swap the left and
 right child of the node so that the greater node's key is the left child.
 This is the important takeaway that is what all other code depends on in this
 problem. The only methods that really needed modification were put, isRed,
 rotateLeft, rotateRight, and flipColors.
*/
/* ============== COMPLEXITIES ================= */
// Time complexity of methods:
// put -> O(logN)
// delete -> O(logN)
// contains -> O(logN)
// isEmpty -> O(1)
// size -> O(logN)
// keys -> O(logN)
// Space complexity for all methods is O(N)
// the size is dependent on the number of nodes


// reference: https://github.com/reneargento/algorithms-sedgewick-wayne/blob/master/src/chapter1/section4/Exercise24_ThrowingEggs.java

import java.util.LinkedList;
import java.util.Queue;
import java.util.NoSuchElementException;

public class RedBlackBST<Key extends Comparable<Key>, Value> {

  Node root; // root of the BST

  // BST helper node data type
  private class Node {
    private Key key; // key
    private Value val; // associated data
    private Node left, right; // links to left and right subtrees
    private int size; // subtree count

    public Node(Key key, Value val, int size) {
      this.key = key;
      this.val = val;
      this.size = size;
    }
  }

  // Method to flip the left and right children of a Node, this will
  // later lead to signifying that the node is red.
  private void flipChildren(Node x) {
    if (x == null)
      return;

    // Only want to swap L & R if we have two children to compare
    if (x.left != null && x.right != null) {
      Node temp = x.left;
      x.left = x.right;
      x.right = temp;
    }
  }

  // is node x red; false if x is null ?
  private boolean isRed(Node x) {
    // basic null check
    if (x == null)
      return false;
    // If it's the only node in our BST it's black
    if (x == root)
      return false;

    // Case for 1 or 0 children
    // Iterate from our root to the parent of the passed in node
    // This will allow us to check if passed in node has a sibling
    Node parent = root;
    while (parent != null) {
      // case when we find the node and it has no siblings
      // we know it's red in this case
      if (parent.left == x) {
        if (parent.right == null)
          return true;
        // else we have a sibling, so we break to check comparison
        else
          break;
      }
      // inverted case
      if (parent.right == x) {
        if (parent.left == null)
          return true;
        // else we have a sibling, so we break to check comparison
        else
          break;
      }
      // Determine how to continue while loop iteration
      // store the cmp between parent/child (for traversal direction)
      // we need to store the cmp before we start updating the parent
      // in case we change the parent around
      int cmp = x.key.compareTo(parent.key);
      // Case when we haven't come across the 'x' node during traversal
      // and the parent only has one child aka no sibling
      if (parent.left == null || parent.right == null) {
        // if left null, go right
        if (parent.left == null) {
          parent = parent.right;
          // continue in our loop again to check for x
          continue;
        } else {
          parent = parent.right;
          // continue in our loop again to check for x
          continue;
        }
      } else {
        // Case when we have two children to compare compare left and right
        // child to determine red / black this will determine traversal order,
        // red means we go left when greater and right when less store the
        // value that determines red or not (L > R -> RED)
        if (parent.left != null && parent.right != null) {
          // If current parent node is black...
          // we traversal normally
          if (parent.left.key.compareTo(parent.right.key) < 0) {
            // go left if less than
            if (cmp < 0)
              parent = parent.left;
            // go right if greater than
            else if (cmp > 0)
              parent = parent.right;
            // break if they are equal
            else
              break;
          } else {
            // if we have a red parent traversal is inverted
            if (cmp < 0)
              parent = parent.right;
            else if (cmp > 0)
              parent = parent.left;
            else
              break;
          }
        }
      }
    }

    // Case for two node with two children
    if (x.left != null && x.right != null) {
      if (x.left.key.compareTo(x.right.key) > 0)
        return true;
      // if left is less than right (natural order so black)
      return false;
    }
    // return false by default if we don't pass any other cases
    return false;
  }

  // number of node in subtree rooted at x; 0 if x is null
  private int size(Node x) {
    if (x == null) return 0;
    return x.size;
  }

  /**
   * Returns the number of key-value pairs in this symbol table.
   * 
   * @return the number of key-value pairs in this symbol table
   */
  public int size() {
    return size(root);
  }

  /**
   * Is this symbol table empty?
   * 
   * @return {@code true} if this symbol table is empty and {@code false}
   *         otherwise
   */
  public boolean isEmpty() {
    // simple enough, check if the root is null, that's your answer
    return root == null;
  }

  /**
   * Returns the value associated with the given key.
   * 
   * @param key the key
   * @return the value associated with the given key if the key is in the symbol
   *         table and {@code null} if the key is not in the symbol table
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public Value get(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to get() is null");
    return get(root, key);
  }

  // value associated with the given key in subtree rooted at x; null if no such
  // key
  private Value get(Node x, Key key) {
    while (x != null) {
      int cmp = key.compareTo(x.key);
      if (cmp < 0) x = x.left;
      else if (cmp > 0) x = x.right;
      else return x.val;
    }
    return null;
  }

  /**
   * Does this symbol table contain the given key?
   * 
   * @param key the key
   * @return {@code true} if this symbol table contains {@code key} and
   *         {@code false} otherwise
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public boolean contains(Key key) {
    return get(key) != null;
  }

  /***************************************************************************
   * Red-black tree insertion.
   ***************************************************************************/

  /**
   * Inserts the specified key-value pair into the symbol table, overwriting the
   * old value with the new value if the symbol table already contains the
   * specified key. Deletes the specified key (and its associated value) from this
   * symbol table if the specified value is {@code null}.
   *
   * @param key the key
   * @param val the value
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public void put(Key key, Value val) {
    if (key == null)
      throw new IllegalArgumentException("first argument to put() is null");
    if (val == null) {
      // if we are passed a null value, delete the key
      delete(key);
      return;
    }

    root = put(root, key, val);
    // assert check();
  }

  // insert the key-value pair in the subtree rooted at h
  private Node put(Node h, Key key, Value val) {
    // Base case, when we take in a null, we return a new Node
    if (h == null) return new Node(key, val, 1);
    // store the key comparison for tree traversal
    int cmp = key.compareTo(h.key);
    // black traversal for putting new node
    if (!isRed(h)) {
      // if less, go left
      if (cmp < 0) h.left = put(h.left, key, val);
      // if greater, go right
      else if (cmp > 0) h.right = put(h.right, key, val);
      // overwrite the value, equal keys
      else h.val = val;
    }
    // if h is red, invert the traversal/recursion
    if (isRed(h)) {
      // if h is red
      // if less, go right
      if (cmp < 0) h.right = put(h.right, key, val);
      // if
      else if (cmp > 0) h.left = put(h.left, key, val);
      // overwrite the value, equal keys
      else h.val = val;
    }
    // Check to fix up any balance issues
    // initial operations on black node
    // inverted operations for red nodes
    // ROTATE LEFT CASES
    if (!isRed(h)) {
      if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
    }
    // inverted case for a red node
    // just need to check opposite children
    if (isRed(h)) {
      if (isRed(h.left) && !isRed(h.right)) h = rotateLeft(h);
    }
    // ROTATE RIGHT CASES only 2
    // if h is black, then check left child == red
    // and check left child right child red
    // this is the case where we have two consecutive
    // red nodes so we must rotate right
    if (!isRed(h)) {
      // this rotation gets us back to left leaning
      if (isRed(h.left) && isRed(h.left.right)) h = rotateRight(h);
    }
    // if h is red node then do the inverse of what we did for black
    // we check if h right child is red and if the right childs right child
    // is red as well, this means we have two consecutive red nodes
    // so we must rotate right to get left leaning again
    // but left leaning in this case is inverted since the children of h are
    // flipped 
    if (isRed(h)) {
      if (isRed(h.right) && isRed(h.right.right)) h = rotateRight(h);
    }
    // if both children are red, flip children and root's colors
    if (isRed(h.left) && isRed(h.right)) flipColors(h);
    // increment the size, recursively
    h.size = size(h.left) + size(h.right) + 1;
    return h;
  }

  /***************************************************************************
   * Red-black tree deletion.
   ***************************************************************************/

  /**
   * Removes the smallest key and associated value from the symbol table.
   * 
   * @throws NoSuchElementException if the symbol table is empty
   */
  public void deleteMin() {
    if (isEmpty())
      throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      flipChildren(root);

    root = deleteMin(root);
    if (!isEmpty())
      flipChildren(root);
    // assert check();
  }

  // delete the key-value pair with the minimum key rooted at h
  private Node deleteMin(Node h) {
    if (h.left == null)
      return null;

    if (!isRed(h.left) && !isRed(h.left.left))
      h = moveRedLeft(h);

    h.left = deleteMin(h.left);
    return balance(h);
  }

  /**
   * Removes the largest key and associated value from the symbol table.
   * 
   * @throws NoSuchElementException if the symbol table is empty
   */
  public void deleteMax() {
    if (isEmpty())
      throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      flipChildren(root);

    root = deleteMax(root);
    if (!isEmpty())
      flipChildren(root);
    // assert check();
  }

  // delete the key-value pair with the maximum key rooted at h
  private Node deleteMax(Node h) {
    if (isRed(h.left))
      h = rotateRight(h);

    if (h.right == null)
      return null;

    if (!isRed(h.right) && !isRed(h.right.left))
      h = moveRedRight(h);

    h.right = deleteMax(h.right);

    return balance(h);
  }

  /**
   * Removes the specified key and its associated value from this symbol table (if
   * the key is in this symbol table).
   *
   * @param key the key
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public void delete(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to delete() is null");
    if (!contains(key))
      return;

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      flipChildren(root);

    root = delete(root, key);
    if (!isEmpty()) flipChildren(root);
    // assert check();
  }

  // delete the key-value pair with the given key rooted at h
  private Node delete(Node h, Key key) {
    // assert get(h, key) != null;
    // Null check here for node passed in
    if (h == null) return null;
    // if key we get passed in is less than key of h
    if (key.compareTo(h.key) < 0) {
      // Case when we have a black node
      if (!isRed(h)) {
        // if h's left child is a black node as well
        if (!isRed(h.left)) {
          // check child of left child for black
          // if this is the case we must move a red to the left
          if (!isRed(h.left.left)) h = moveRedLeft(h);
        } else {
          // if h's left child is RED
          // check h's left child's right child for BLACK
          // if it's black we need to move red to the left for balance
          if (!isRed(h.left.right)) h = moveRedLeft(h);
        }
      } else {
        // if the key passed in is GREATER than h's key
        // if h's right is black
        if (!isRed(h.right)) {
          // check h's right childs left child for black
          // if it's black we must rotate it left for balance
          if (!isRed(h.right.left)) h = moveRedLeft(h);
        } else {
          // case when h's right child is red
          // if h's right child's right child is black, move it left
          if (!isRed(h.right.right)) h = moveRedLeft(h);
        }
      }
      // base cases for deletion
      // TODO
      if (!isRed(h)) {
        h.left = delete(h.left, key);
      } else {
        h.right = delete(h.right, key);
      }
    } else {
      if (!isRed(h)) {
        if (isRed(h.left)) {
          h = rotateRight(h);
        }
      } else {
        if (isRed(h.right)) {
          h = rotateRight(h);
        }
      }
      if (key.compareTo(h.key) == 0 && h.right == null) {
        return null;
      }
      if (!isRed(h)) {
        if (!isRed(h.right)) {
          if (!isRed(h.right) && h.right != null && !isRed(h.right.left)) {
            h = moveRedRight(h);
          }
        } else {
          if (!isRed(h.right) && h.right != null && !isRed(h.right.right)) {
            h = moveRedRight(h);
          }
        }
      } else {
        if (!isRed(h.right)) {
          if (!isRed(h.left) && h.left != null && !isRed(h.left.left)) {
            h = moveRedRight(h);
          }
        } else {
          if (!isRed(h.left) && h.left != null && !isRed(h.left.right)) {
            h = moveRedRight(h);
          }
        }
      }
      if (key.compareTo(h.key) == 0) {
        Node aux;

        if (!isRed(h)) {
          aux = min(h.right);
        } else {
          aux = min(h.left);
        }

        h.key = aux.key;
        h.val = aux.val;

        if (!isRed(h)) {
          h.right = deleteMin(h.right);
        } else {
          h.left = deleteMin(h.left);
        }
      } else {
        if (!isRed(h)) {
          h.right = delete(h.right, key);
        } else {
          h.left = delete(h.left, key);
        }
      }
    }
    return balance(h);
  }

  // make a right-leaning link lean to the left
  // Using new Red/black Implementation
  private Node rotateLeft(Node h) {
    // null check
    if (h == null) return null;
    // Check if h is black
    // if h is black and its right child null
    // then we do no work
    if (!isRed(h)) {
      // nothing to rotate
      if (h.right == null) return h;
    }
    // Check if h is red
    // if h is red and its left child null
    // then we do no work
    if (isRed(h)) {
      // nothing to rotate
      if (h.left == null) return h;
    }
    
    // if h is a red node then we must flip its
    // children before pointer rearrangement in
    // order to avoid after affects of the pointer
    // swapping
    if (isRed(h)) flipChildren(h);
    // if the node's right child is red, then we must flip
    // its children because of the same reason above, this would
    // also be two consecutive red nodes which we want to avoid
    if (isRed(h.right)) flipChildren(h.right);
    // Pointer rearrangement same as original implementation
    // x will become our new root
    Node x = h.right;
    h.right = x.left;
    x.left = h;
    // == Original Code ==
    // x.color = h.color
    // after rotating pointers, flip children of x to match h
    if (isRed(h)) flipChildren(x);
    // h.color = RED, synonymous
    // if h is not red, make it red
    if (!isRed(h)) flipChildren(h);
    // Set x's size to previous root's size (h)
    x.size = h.size;
    // recalculate size for h
    h.size = size(h.left) + size(h.right) + 1;
    return x;
  }

  // make a left-leaning link lean to the right
  private Node rotateRight(Node h) {
    // basic null check, return null
    if (h == null) return null;
    // if the node is black, and it has no left child, then there's no work to be done
    if (!isRed(h) && h.left == null) return h;
    // if the node is red, and it has no right child, then there's no work to be done
    if (isRed(h) && h.right == null) return h;
    
    // flip children if we have a red node to prevent after effects
    // of rotating and pointer reassignment
    if (isRed(h)) flipChildren(h);
    // flip children if we have a red node to prevent after effects
    // of rotating and pointer reassignment for left child too
    if (isRed(h.left)) flipChildren(h.left);
    // Pointer reassignment for rotation
    Node x = h.left;
    h.left = x.right;
    x.right = h;
    // after we rotate, check if node is red, if it isn't
    // then make it red to balance
    if (!isRed(h)) flipChildren(h);
    // nodes without children are black
    // make the flipped root red and children black
    // since the children will have no children of their own in
    // this new orientation
    if (!isRed(x) && x.left != null && x.right != null) {
      flipChildren(x);
      flipChildren(x.left);
      flipChildren(x.right);
    }
    // reset the size
    x.size = h.size;
    h.size = size(h.left) + size(h.right) + 1;
    return x;
  }

  // flip the colors of a node and its two children
  // invert root and it's children if they are opposites
  private void flipColors(Node h) {
    // null check on parameter
    if (h == null) return;
    // null check on parameter's children
    // if there aren't 2 children we can't flip the colors
    if (h.left == null || h.right == null) return;
    // h must have opposite color of its two children
    // if h is red and children are black
    if (isRed(h) && !isRed(h.left) && !isRed(h.right)) {
      // flip everything
      flipChildren(h);
      flipChildren(h.left);
      flipChildren(h.right);
    }
    // if h is black and children are red
    if (!isRed(h) && isRed(h.left) && isRed(h.right)) {
      // flip everything
      flipChildren(h);
      flipChildren(h.left);
      flipChildren(h.right);
    }
  }

  // Assuming that h is red and both h.left and h.left.left
  // are black, make h.left or one of its children red.
  private Node moveRedLeft(Node h) {
    // assert (h != null);
    // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

    flipColors(h);
    if (isRed(h.right.left)) {
      h.right = rotateRight(h.right);
      h = rotateLeft(h);
      flipColors(h);
    }
    return h;
  }

  // Assuming that h is red and both h.right and h.right.left
  // are black, make h.right or one of its children red.
  private Node moveRedRight(Node h) {
    // assert (h != null);
    // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
    flipColors(h);
    if (isRed(h.left.left)) {
      h = rotateRight(h);
      flipColors(h);
    }
    return h;
  }

  // restore red-black tree invariant
  private Node balance(Node h) {
    // assert (h != null);

    if (isRed(h.right) && !isRed(h.left))
      h = rotateLeft(h);
    if (isRed(h.left) && isRed(h.left.left))
      h = rotateRight(h);
    if (isRed(h.left) && isRed(h.right))
      flipColors(h);

    h.size = size(h.left) + size(h.right) + 1;
    return h;
  }

  /***************************************************************************
   * Ordered symbol table methods.
   ***************************************************************************/

  /**
   * Returns the smallest key in the symbol table.
   * 
   * @return the smallest key in the symbol table
   * @throws NoSuchElementException if the symbol table is empty
   */
  public Key min() {
    if (isEmpty())
      throw new NoSuchElementException("calls min() with empty symbol table");
    return min(root).key;
  }

  // the smallest key in subtree rooted at x; null if no such key
  private Node min(Node x) {
    // assert x != null;
    if (x.left == null)
      return x;
    else
      return min(x.left);
  }

  /**
   * Returns the largest key in the symbol table.
   * 
   * @return the largest key in the symbol table
   * @throws NoSuchElementException if the symbol table is empty
   */
  public Key max() {
    if (isEmpty())
      throw new NoSuchElementException("calls max() with empty symbol table");
    return max(root).key;
  }

  // the largest key in the subtree rooted at x; null if no such key
  private Node max(Node x) {
    // assert x != null;
    if (x.right == null)
      return x;
    else
      return max(x.right);
  }

  /**
   * Returns the largest key in the symbol table less than or equal to
   * {@code key}.
   * 
   * @param key the key
   * @return the largest key in the symbol table less than or equal to {@code key}
   * @throws NoSuchElementException   if there is no such key
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public Key floor(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to floor() is null");
    if (isEmpty())
      throw new NoSuchElementException("calls floor() with empty symbol table");
    Node x = floor(root, key);
    if (x == null)
      throw new NoSuchElementException("argument to floor() is too small");
    else
      return x.key;
  }

  // the largest key in the subtree rooted at x less than or equal to the given
  // key
  private Node floor(Node x, Key key) {
    if (x == null)
      return null;
    int cmp = key.compareTo(x.key);
    if (cmp == 0)
      return x;
    if (cmp < 0)
      return floor(x.left, key);
    Node t = floor(x.right, key);
    if (t != null)
      return t;
    else
      return x;
  }

  /**
   * Returns the smallest key in the symbol table greater than or equal to
   * {@code key}.
   * 
   * @param key the key
   * @return the smallest key in the symbol table greater than or equal to
   *         {@code key}
   * @throws NoSuchElementException   if there is no such key
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public Key ceiling(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to ceiling() is null");
    if (isEmpty())
      throw new NoSuchElementException("calls ceiling() with empty symbol table");
    Node x = ceiling(root, key);
    if (x == null)
      throw new NoSuchElementException("argument to ceiling() is too small");
    else
      return x.key;
  }

  // the smallest key in the subtree rooted at x greater than or equal to the
  // given key
  private Node ceiling(Node x, Key key) {
    if (x == null)
      return null;
    int cmp = key.compareTo(x.key);
    if (cmp == 0)
      return x;
    if (cmp > 0)
      return ceiling(x.right, key);
    Node t = ceiling(x.left, key);
    if (t != null)
      return t;
    else
      return x;
  }

  /**
   * Returns all keys in the symbol table as an {@code Iterable}. To iterate over
   * all of the keys in the symbol table named {@code st}, use the foreach
   * notation: {@code for (Key key : st.keys())}.
   * 
   * @return all keys in the symbol table as an {@code Iterable}
   */
  public Iterable<Key> keys() {
    if (isEmpty())
      return new LinkedList<Key>();
    return keys(min(), max());
  }

  /**
   * Returns all keys in the symbol table in the given range, as an
   * {@code Iterable}.
   *
   * @param lo minimum endpoint
   * @param hi maximum endpoint
   * @return all keys in the symbol table between {@code lo} (inclusive) and
   *         {@code hi} (inclusive) as an {@code Iterable}
   * @throws IllegalArgumentException if either {@code lo} or {@code hi} is
   *                                  {@code null}
   */
  public Iterable<Key> keys(Key lo, Key hi) {
    if (lo == null)
      throw new IllegalArgumentException("first argument to keys() is null");
    if (hi == null)
      throw new IllegalArgumentException("second argument to keys() is null");

    // Use a LinkedList to implement the Queue, since Java's Queue
    // is an interface we can't create an instance of it
    Queue<Key> queue = new LinkedList<Key>();
    // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
    keys(root, queue, lo, hi);
    return queue;
  }

  // add the keys between lo and hi in the subtree rooted at x
  // to the queue
  private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
    if (x == null) return;
    // store the lo and hi comparisons to current key
    int cmplo = lo.compareTo(x.key);
    int cmphi = hi.compareTo(x.key);
    // operations similar to Inorder traversal of Binary Tree if less, call
    // left side
    if (cmplo < 0) keys(x.left, queue, lo, hi);
    // add when they're the same comparison value
    if (cmplo <= 0 && cmphi >= 0) queue.add(x.key);
    // call right side when greater
    if (cmphi > 0) keys(x.right, queue, lo, hi);
  }

  // Method to print out the tree, uses recursive calls to traverseNodes for traversal
  // source: https://www.baeldung.com/java-print-binary-tree-diagram
  private String traversePreOrder(Node root) {
    if (root == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    sb.append(root.key);
    String pointerRight = "└──";
    String pointerLeft = (root.right != null) ? "├──" : "└──";
    traverseNodes(sb, "", pointerLeft, root.left, root.right != null);
    traverseNodes(sb, "", pointerRight, root.right, false);
    sb.append("\n");
    return sb.toString();
  }

  // Method to traverse child nodes and add to print string
  private void traverseNodes(StringBuilder sb, String padding, String pointer, Node node, boolean hasRightSibling) {
    if (node != null) {
      sb.append("\n");
      sb.append(padding);
      sb.append(pointer);
      sb.append(node.key);
      StringBuilder paddingBuilder = new StringBuilder(padding);
      if (hasRightSibling) {
        paddingBuilder.append("│  ");
      } else {
        paddingBuilder.append("   ");
      }
      String paddingForBoth = paddingBuilder.toString();
      String pointerRight = "└──";
      String pointerLeft = (node.right != null) ? "├──" : "└──";

      traverseNodes(sb, paddingForBoth, pointerLeft, node.left, node.right != null);
      traverseNodes(sb, paddingForBoth, pointerRight, node.right, false);
    }
  }

  // Method to print out the tree with nice output
  public void printTree() {
    System.out.print(traversePreOrder(this.root));
  }

  // ====== MAIN METHOD FOR TESTING ==========
  public static void main(String[] args) {
    RedBlackBST<Integer, String> rbt = new RedBlackBST<Integer, String>();
    rbt.put(1, "one");
    rbt.put(2, "two");
    rbt.put(3, "three");
    rbt.put(10, "ten");
    rbt.put(7, "seven");
    rbt.put(20, "twenty");
    rbt.put(5, "five");
    rbt.put(50, "fifty");
    rbt.printTree();
  }
}