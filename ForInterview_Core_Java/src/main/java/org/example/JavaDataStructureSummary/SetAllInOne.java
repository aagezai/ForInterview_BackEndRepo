package org.example.JavaDataStructureSummary;

import java.util.*;
import java.util.concurrent.*;

public class SetAllInOne {
    enum S {NEW,SENT,DONE}
    public static void main(String[] a) {
        System.out.println("HashSet: " + new HashSet<>(List.of("A","B","A","C")));
        System.out.println("LinkedHashSet: " + new LinkedHashSet<>(List.of("A","B","A","C")));
        System.out.println("TreeSet: " + new TreeSet<>(List.of("B","A","C")));
        System.out.println("EnumSet: " + EnumSet.of(S.NEW,S.DONE));
        System.out.println("CopyOnWriteArraySet: " + new CopyOnWriteArraySet<>(List.of("A","A","B","C")));
        System.out.println("ConcurrentSkipListSet: " + new ConcurrentSkipListSet<>(List.of(5,3,1)));
        System.out.println("Immutable: " + Set.of("X","Y","Z"));
        System.out.println("Unique: " + new LinkedHashSet<>(List.of("A","B","A","C")));
        Set<String> x=new HashSet<>(List.of("A","B","C")),y=new HashSet<>(List.of("B","C","D"));
        System.out.println("Union: " + new HashSet<>(){{addAll(x);addAll(y);}} + 
                           " | Inter: " + new HashSet<>(){{addAll(x);retainAll(y);}} + 
                           " | Diff: " + new HashSet<>(){{addAll(x);removeAll(y);}});
    }
}
