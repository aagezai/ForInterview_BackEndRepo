package org.example.JavaDataStructureSummary;

import java.util.*;
import java.util.concurrent.*;

public class MapAllInOne {
    public static void main(String[] a) {
        Map<String,Integer> hash = new HashMap<>(Map.of("A",1,"B",2,"A",3));
        Map<String,Integer> linked = new LinkedHashMap<>(Map.of("A",1,"B",2,"C",3));
        Map<String,Integer> tree = new TreeMap<>(Map.of("B",2,"A",1,"C",3));
        EnumMap<Day,Integer> en = new EnumMap<>(Day.class); en.put(Day.MON,1); en.put(Day.TUE,2);
        ConcurrentMap<String,Integer> conc = new ConcurrentHashMap<>(Map.of("A",1,"B",2));
        Map<String,Integer> imm = Map.of("X",10,"Y",20,"Z",30);
        System.out.println("HashMap: " + hash);
        System.out.println("LinkedHashMap: " + linked);
        System.out.println("TreeMap: " + tree);
        System.out.println("EnumMap: " + en);
        System.out.println("ConcurrentHashMap: " + conc);
        System.out.println("Immutable: " + imm);
        Map<String,Integer> m1 = new HashMap<>(Map.of("A",1,"B",2,"C",3));
        Map<String,Integer> m2 = new HashMap<>(Map.of("B",5,"D",4));
        Map<String,Integer> union = new HashMap<>(m1); union.putAll(m2);
        m1.keySet().retainAll(m2.keySet());
        System.out.println("Union: " + union + " | Inter: " + m1);
    }
    enum Day {MON,TUE,WED}
}
