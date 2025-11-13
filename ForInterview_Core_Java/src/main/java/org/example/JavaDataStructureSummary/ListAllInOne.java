package org.example.JavaDataStructureSummary;

import java.util.*;

public class ListAllInOne {
    public static void main(String[] a) {
        System.out.println("ArrayList: " + new ArrayList<>(List.of("A","B","A","C")));
        System.out.println("LinkedList: " + new LinkedList<>(List.of("A","B","A","C")));
        List<String> arr = new ArrayList<>(List.of("A","B","C"));
        arr.add("D"); arr.remove("B"); arr.set(1,"Z");
        System.out.println("Modified ArrayList: " + arr);
        List<String> link = new LinkedList<>(List.of("A","B","C"));
        link.addFirst("X"); link.addLast("Y"); link.remove("B");
        System.out.println("Modified LinkedList: " + link);
        List<String> imm = List.of("X","Y","Z");
        System.out.println("Immutable: " + imm);
        List<String> sorted = new ArrayList<>(Set.copyOf(List.of("B","A","C","A")));
        Collections.sort(sorted);
        System.out.println("Sorted Unique: " + sorted);
        List<String> merged = new ArrayList<>(arr); merged.addAll(link);
        System.out.println("Merged: " + merged);
        merged.retainAll(List.of("A","Z","Y"));
        System.out.println("Common: " + merged);
    }
}
