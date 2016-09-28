/*
 * Bao Lab 2016
 */

import wormguides.models.LineageTree;

public class Foo {
    public static void main(String[] args) {
        System.out.println(LineageTree.isAncestor("p1", "p1"));
        System.out.println(LineageTree.isDescendant("p1", "p1"));
    }
}
