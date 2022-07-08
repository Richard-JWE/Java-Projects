
/******************************************************************************
 *  Compilation:  javac Percolation.java
 *  Execution:    java Percolation < input.txt
 *  Dependencies: StdArrayIO.java StdDraw.java StdOut.java
 *  Data files:   https://introcs.cs.princeton.edu/java/24percolation/test5.txt
 *                https://introcs.cs.princeton.edu/java/24percolation/test8.txt
 *                https://introcs.cs.princeton.edu/java/24percolation/testD.txt
 *                https://introcs.cs.princeton.edu/java/24percolation/testV.txt
 *                https://introcs.cs.princeton.edu/java/24percolation/testT.txt
 *                https://introcs.cs.princeton.edu/java/24percolation/testF.txt
 *                https://introcs.cs.princeton.edu/java/24percolation/testTiny.txt
 *
 *  % more test5.txt 
 *  5 5
 *  0 1 1 0 1
 *  0 0 1 1 1
 *  1 1 0 1 1
 *  1 0 0 0 1
 *  0 1 1 1 1
 *
 *  % java Percolation < test5.txt
 *  5 5
 *  0 1 1 0 1 
 *  0 0 1 1 1 
 *  0 0 0 1 1 
 *  0 0 0 0 1 
 *  0 1 1 1 1 
 *  true
 *
 *  % more testD.txt
 *  8 8
 *  0 0 0 1 1 1 0 1
 *  1 1 1 0 0 1 1 1
 *  1 0 1 0 0 1 0 0
 *  1 0 1 1 1 1 0 1
 *  1 0 0 1 0 1 0 0
 *  1 1 0 1 0 0 1 0
 *  0 1 1 0 0 1 1 1
 *  0 0 1 0 0 0 0 0
 *
 *  % java Percolation < testD.txt
 *  8 8
 *  0 0 0 1 1 1 0 1
 *  1 1 1 0 0 1 1 1
 *  1 0 1 0 0 1 0 0
 *  1 0 1 1 1 1 0 0
 *  1 0 0 1 0 1 0 0
 *  1 1 0 1 0 0 0 0
 *  0 1 1 0 0 0 0 0
 *  0 0 1 0 0 0 0 0
 *  true
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[] site;
    private boolean[] connectTop;
    private boolean[] connectBottom;
    private final int size;
    private int numberOfOpenSites = 0;
    private final WeightedQuickUnionUF uf;
    private boolean percolateFlag = false;

    public Percolation(int n) {
        validate(n);
        this.size = n;
        this.uf = new WeightedQuickUnionUF(n * n);
        this.site = new boolean[n * n];
        this.connectTop = new boolean[n * n];
        this.connectBottom = new boolean[n * n];
    }

    public void open(int row, int col) {
        validate(row, col);
        int index = xyTo1D(row, col);
        if (!site[index]) {
            site[index] = true;
            numberOfOpenSites++;
        }
        boolean topFlag = false;
        boolean bottomFlag = false;

        if (row == 1) {
            topFlag = true;
        }
        if (row == size) {
            bottomFlag = true;
        }
        // Optimize if already connected to top/bottom with short-circuit evaluation
        // connect with down site
        if (row < size && site[index + size]) {
            if (!topFlag && connectTop[uf.find(index + size)] || connectTop[uf.find(index)]) {
                topFlag = true;
            }
            if (connectBottom[uf.find(index + size)] || connectBottom[uf.find(index)]) {
                bottomFlag = true;
            }
            uf.union(index, index + size);
        }
        // connect with up site
        if (row > 1 && site[index - size]) {
            if (!topFlag && connectTop[uf.find(index - size)] || connectTop[uf.find(index)]) {
                topFlag = true;
            }
            if (!bottomFlag && connectBottom[uf.find(index - size)] || connectBottom[uf.find(index)]) {
                bottomFlag = true;
            }
            uf.union(index, index - size);
        }
        // connect with right site
        if (col < size && site[index + 1]) {
            if (!topFlag && connectTop[uf.find(index + 1)] || connectTop[uf.find(index)]) {
                topFlag = true;
            }
            if (!bottomFlag && connectBottom[uf.find(index + 1)] || connectBottom[uf.find(index)]) {
                bottomFlag = true;
            }
            uf.union(index, index + 1);
        }
        // connect with left site
        if (col > 1 && site[index - 1]) {
            if (!topFlag && connectTop[uf.find(index - 1)] || connectTop[uf.find(index)]) {
                topFlag = true;
            }
            if (!bottomFlag && connectBottom[uf.find(index - 1)] || connectBottom[uf.find(index)]) {
                bottomFlag = true;
            }
            uf.union(index, index - 1);
        }

        connectTop[uf.find(index)] = topFlag;
        connectBottom[uf.find(index)] = bottomFlag;
        if (!percolateFlag && connectTop[uf.find(index)] && connectBottom[uf.find(index)]) {
            percolateFlag = true;
        }
    }

    public boolean isOpen(int row, int col) {
        validate(row, col);
        return site[xyTo1D(row, col)];
    }

    /**
     * A full site is an open site that can be connected to an open site in the
     * top row via a chain of neighboring (left, right, up, down) open sites.
     */
    public boolean isFull(int row, int col) {
        validate(row, col);
        return connectTop[uf.find(xyTo1D(row, col))];
    }

    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    /**
     * Introduce 2 virtual sites (and connections to top and bottom).
     * Percolates if virtual top site is connected to virtual bottom site.
     */
    public boolean percolates() {
        return percolateFlag;
    }

    private int xyTo1D(int row, int col) {
        validate(row, col);
        return size * (row - 1) + col - 1;
    }

    private void validate(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
    }

    private void validate(int row, int col) {
        if (row <= 0 || row > size || col <= 0 || col > size) {
            throw new IllegalArgumentException();
        }
    }

    // public static void main(String[] args) {
    // // test client (optional)
    // }
}