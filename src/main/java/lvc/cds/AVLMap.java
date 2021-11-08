package lvc.cds;

import java.util.Comparator;

public class AVLMap<K extends Comparable<K>, V> implements Map<K, V> {

    private Node root;
    private Comparator<K> comp;
    private int size;

    public AVLMap() {
        this(Comparator.<K>naturalOrder());
    }

    public AVLMap(Comparator<K> c) {
        clear();
        comp = c;
    }

    /**
     * if the tree is empty, make the new entry the root and we are done, otherwise
     * call the private put
     */
    @Override
    public V put(K key, V value) {
        Node newNode = new Node(key, value);
        if (root == null) {
            root = newNode;
            size++;
            return null;
        } else {
            V ret = put(newNode, root);
            if (ret == null)
                size++;
            return ret;
        }
    }

    /**
     * implements the AVL Tree's algorithm for adding new entries, and rebalancing where needed
     */
    private V put(Node newNode, Node cur) {
        int c = comp.compare(newNode.key, cur.key);
        if (c == 0) {
            // this item already in the tree, swap them
            V val = cur.value;
            cur.value = newNode.value;
            return val;
        } else if (c < 0) {
            // this item is smaller, try adding to the left
            if (cur.left == null) {
                // nothing in the left position, add newNode
                cur.left = newNode;
                newNode.parent = cur;
                fixHeight(cur);
                return null;
            } else{
                // something in the left spot, call put again with cur.left
                V val = put(newNode, cur.left);
                // coming back out of recursion, fix the height and check balancing
                if(isBalanced(cur)) {
                    // we are balanced, fix the height and continue
                    fixHeight(cur);
                    return val;
                } else {
                    // cur is not balanced, rotate to balance it
                    rebalance(cur);
                    return val;
                }
            }
        } else {
            // this item is bigger, try adding to the right
            if (cur.right == null) {
                // nothing to the right, add newNode 
                cur.right = newNode;
                newNode.parent = cur;
                fixHeight(cur);
                return null;
            } else{
                // something in the right spot, we call put agin with cur.right
                V val = put(newNode, cur.right);
                // coming back out of recursion, check for balancing
                if(isBalanced(cur)) {
                    // we are balanced, fix the height and continue
                    fixHeight(cur);
                    return val;
                } else {
                    // cur is not balanced, rotate to balance it
                    rebalance(cur);
                    return val;
                }
            }
        }
    }


    @Override
    public V remove(K key) {
        return remove(key, root);
    }

    // Rewritten to trim the method down, using a separate method to avoid duplicate
    // code.
    // The method I called "removeTwig" is for removing a leaf or a node with 1 child
    private V remove(K key, Node cur) {
        if (cur == null) {
            return null;
        }

        int c = comp.compare(key, cur.key);
        if (c < 0) {
            return remove(key, cur.left);
        } else if (c > 0) {
            return remove(key, cur.right);
        } else {
            // we found it! get it outta here.
            var val = cur.value;
            Node start;
            // do we have 2 children?
            if (cur.left != null && cur.right != null) {
                // find the replacement
                var least = cur.right;
                while (least.left != null) {
                    least = least.left;
                }
                // move payload into this spot
                cur.key = least.key;
                cur.value = least.value;
                // keep a mark of the last element reached for rebalancing
                if(least.parent != null) {
                    start = least.parent;
                } else {
                    start = null;
                }
                // remove the stale node
                removeTwig(least);
            } else {
                // at most one child
                // keep a mark of the last element reached for rebalancing
                if (cur.parent != null) {
                    start = cur.parent;
                } else {
                    start = null;
                }
                removeTwig(cur);
            }
            
            while (start != null) {
                // fix the height since something from the bottom has been removed.
                // check if the node is balanced, and balance it if it is not
                if (!isBalanced(start)) {
                    // start is not balanced, rotate to balance it
                    rebalance(start);
                } else {
                    // we are balanced, just fix the height and move on
                    fixHeight(start);
                }
                start = start.parent;
            }
            size--;
            return val;
        }
    }

    /**
     * rebalances a node
     */ 
    public void rebalance(Node start) {
        if(getHeight(start.left) > getHeight(start.right)) {
            // left side is heavy, left rotation of sorts
            Node temp = start.left;
            if (getHeight(temp.left) >= getHeight(temp.right)) {
                // Left Left rotation
                rotateLL(start);
            } else {
                // Left Right rotation
                rotateLR(start);
            }
        } else {
            // right is heavy, right rotation of sorts
            Node temp = start.right;
            if (getHeight(temp.right) >= getHeight(temp.left)) {
                // Right Right rotation
                rotateRR(start);
            } else {
                // Right Left rotation
                rotateRL(start);
            }
        }
    }

    /**
     * removes a node with one or no children
     */
    private void removeTwig(Node twig) {
        // for our purposes, a twig is a node with 0 or 1 child(ren)

        // our parent. Might be null if we're root
        var p = twig.parent;

        // the child of twig we are going to promote. Will be null if
        // twig is a leaf. Saves some if-else branching.
        var promoteMe = twig.left != null ? twig.left : twig.right;

        if (p == null) {
            // twig is the root
            root = promoteMe;
        } else if (p.left == twig) {
            p.left = promoteMe;
        } else {
            p.right = promoteMe;
        }

        if (promoteMe != null) {
            promoteMe.parent = p;
        }
    }

    @Override
    public V get(K key) {
        Node cur = root;
        while (cur != null) {
            int c = comp.compare(key, cur.key);
            if (c == 0) {
                return cur.value;
            } else if (c < 0) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    private int getHeight(Node n) {
        return n == null ? -1 : n.height;
    }


    /**
     * unbalanced node gets passed in 
     * rotate because the Left Left is heavy
     */

    private void rotateLL(Node n) {
        Node left = n.left;
        if(left.right != null) {
            left.right.parent = n;
        }
        n.left = left.right;
        left.right = n;
        // connects n's parent to left, or makes left the root
        adoptAChild(left, n);
        left.parent = n.parent;
        n.parent = left;
        // fix their heights, do n's first because it is lower.
        fixHeight(n);
        fixHeight(left);
    }


    /**
     * unbalanced node gets passes in
     * rotate because the Left Right is heavy
     */
    private void rotateLR(Node n) {
        Node left = n.left;
        Node LR = left.right;
        if (LR.right != null) {
            LR.right.parent = n;
        }
        n.left = LR.right;
        if (LR.left != null) {
            LR.left.parent = left;
        }
        left.right = LR.left;
        LR.left = left;
        left.parent = LR;
        LR.right = n;
        // connects n's parent to LR, or makes LR the root
        adoptAChild(LR, n);
        LR.parent = n.parent;
        n.parent = LR;
        // fix the heights
        fixHeight(left);
        fixHeight(n);
        fixHeight(LR);
    }

    /**
     * unbalanced node gets passed in
     * heavy becasue of the right right tree
    */ 
    private void rotateRR(Node n) {
        Node right = n.right;
        if(right.left != null) {
            right.left.parent = n;
        }
        n.right = right.left;
        right.left = n;
        // connects n's parent to right, or sets right to the root
        adoptAChild(right, n);
        right.parent = n.parent;
        n.parent = right;
        // fix thier heights, n first
        fixHeight(n);
        fixHeight(right);
    }

    /**
     * unbalanced node gets passed in
     * heavy because of the right-left side
     */ 
    private void rotateRL(Node n) {
        Node right = n.right;
        Node RL = right.left;
        if (RL.left != null) {
            RL.left.parent = n;
        }
        n.right = RL.left;
        if (RL.right != null) {
            RL.right.parent = right;
        }
        right.left = RL.right;
        RL.right = right;
        right.parent = RL;
        RL.left = n;
        // connects n's parent to RL, or makes RL the root
        adoptAChild(RL, n);
        RL.parent = n.parent;
        n.parent = RL;
        // fix their heights
        fixHeight(right);
        fixHeight(n);
        fixHeight(RL);
    }


    /**
     * establishes the connection between a parent and the child
     * after a rotation
     */
    private void adoptAChild(Node newNode, Node cur) {
        if (cur.parent == null) {
            root = newNode;
        } else {
            // cur's parent must adopt a new child, find out which child n is
            if (cur.parent.left != null) {
                // check if cur is a left child
                if(comp.compare(cur.key, cur.parent.left.key) == 0) {
                    // cur is a left child
                    cur.parent.left = newNode;
                } else {
                    // cur is a right child
                    cur.parent.right = newNode;
                }
            } else {
                // cur is a right child
                cur.parent.right = newNode;
            }
        }
    }

    /**
     * check if the tree is balanced
     */
    private boolean isBalanced(Node n) {
        if (n == null) {
            return true;
        }
        int l = n.left == null ? -1 : n.left.height;
        int r = n.right == null ? -1 : n.right.height;
        if (Math.abs(l - r) <= 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * re-compute the height of n, assuming that the heights of n's
     * children are correct
     */
    private void fixHeight(Node n) {
        if (n == null)
            return;

        int l = n.left == null ? -1 : n.left.height;
        int r = n.right == null ? -1 : n.right.height;

        n.height = Math.max(l, r) + 1;
    }


    /**
     * prints the tree in order
     */
    public void printInOrder() {
        Node cur = root;
        while (cur.left != null) {
            cur = cur.left;
        }

        while (cur != null) {
            System.out.println(cur.key + ": " + cur.value);
            // find cur's successor
            if (cur.right != null) {
                cur = cur.right;
                while (cur.left != null) {
                    cur = cur.left;
                }
            }
            else {
                while (cur.parent != null && cur.parent.right == cur) {
                    cur = cur.parent;
                }
                cur = cur.parent;
            }
        }
    }


    /**
     * storing nodes in the tree
     * height allows for quick balance checking
     */
    private class Node {
        K key;
        V value;
        int height;

        Node parent;
        Node left;
        Node right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            height = 0;
            parent = left = right = null;
        }
    }
    
}