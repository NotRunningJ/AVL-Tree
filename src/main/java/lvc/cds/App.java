package lvc.cds;

import java.util.Random;

public final class App {
    private static Random r = new Random();


    public static void main(String[] args) {
        testRuntimeAVL();

        /**
         * outfput follows that an AVL tree adds in O(nlog(n)) 
         * for a series of n adds
         */
    }


    /**
     * adds a specified number of elements to a tree
     */
    public static void testRandomAdds(AVLTree<Integer, Integer> map, int num) {
        for (int i = 0; i < num; i++) {
            int rand = r.nextInt();
            map.put(rand, rand);
        }
    }


    /**
     * tests the time it takes to add elements to the tree
     */
    public static void testRuntimeAVL() {
        var time = 0.0;
        double CONVERT = 1_000_000.0;
        for (int m = 100; m <=1_000_000; m*=10) {
            for (int i = 0; i < 10; i++) {
                AVLTree<Integer, Integer> avlm = new AVLTree<>();
                var start = System.nanoTime();
                testRandomAdds(avlm, m);
                var elapsed = System.nanoTime() - start;
                time += elapsed/CONVERT;
            }
            time = time/10;
            System.out.println(time + "ms to add " + m + " random items to a tree");
            time = 0;
        }
    }
}
