package org.example.JavaDataStructureSummary;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class All_Java_InOne {
    public static void main(String[] args) {
        //===========array basic
        int[] arrayS = new int[1];
        int[] arrayB= new int[]{1,2,3};
        int[] arrayC = {1,2,3};
        int[] arrayD = new int[arrayC.length] ;
        System.arraycopy(arrayC,0,arrayD,0,3);
        int[] arrayE = arrayD.clone();
        int[] arrayF = Arrays.copyOf(arrayD,arrayD.length);
        //=========== array to list
        String[] arrayStr = {"a","b"};
        List<String> list = Arrays.asList(arrayStr);
        list.add("e");
        list.set(2,"D");
        List<String> list3 = Arrays.stream(arrayStr).toList();
        List<Integer> boxedInt = Arrays.stream(arrayD).boxed().collect(Collectors.toList());
        List<Integer> intStreamToList = IntStream.of(arrayD).boxed().collect(Collectors.toList());
        //===========List to array
        List<String> listOfStr = List.of("ab","cd");
        Object[] objectsListArray = listOfStr.toArray();
        String[] arrayOfStrings = listOfStr.toArray(String[]::new);
        String[] arrayOfStrings1 = listOfStr.toArray(new String[0]);
        int[] wrapperList = intStreamToList.stream().mapToInt(Integer::intValue).toArray();//if no null
        int[] wrapperListIfElmHasNull = intStreamToList.stream().mapToInt(x -> x == null ? 0 : x).toArray();//if ele has null
        //Linked list--- all step for array list the same but performance insertion and removing in linked is fast and retrieving  in array list fast
        List<String> linkedList = new LinkedList<>(Arrays.asList(arrayOfStrings1));
        List<String> linked_List2 = Arrays.stream(arrayOfStrings)
                .collect(Collectors.toCollection(LinkedList::new));
        // safe update and remove for both linkied list and array list
        listOfStr.removeIf(a -> a.startsWith("a"));
        ListIterator<String> listItra = listOfStr.listIterator();
        if(listItra.hasNext()){listItra.remove();}
        if(listItra.hasNext()){String el = listItra.next();
            if (el.startsWith("a")) {
                listItra.set("b");
                listItra.add("d");
            }
            }
        listOfStr.replaceAll(el ->el.startsWith("a")?"k":el);
        //=========== Set hashset and linked and tree set
        Set<String> setOfString = new HashSet<String>(List.of("A","B"));
        Set<String> linkedHashSet = new HashSet<String>(List.of("A","B"));
        setOfString.addAll(linkedHashSet);// union
        setOfString.retainAll(linkedHashSet);// intersection
        setOfString.removeAll(setOfString); // difference
        Set<String> copy = new CopyOnWriteArraySet<>((List.of("A","B")));// thread safe and read heavy tasks
        Set<String> skip = new ConcurrentSkipListSet<>(List.of("A","B","A","D","C"));// ABCD sorted and thread safe
        //===================Map
        Map<String,Integer> hash = new HashMap<>(Map.of("A",1,"B",2,"A",3));
        Map<String,Integer> linked = new LinkedHashMap<>(Map.of("A",1,"B",2,"C",3));
        Map<String,Integer> tree = new TreeMap<>(Map.of("B",2,"A",1,"C",3));
        ConcurrentMap<String,Integer> conc = new ConcurrentHashMap<>(Map.of("A",1,"B",2));
        //Queue and Deque
        ArrayDeque<String> arrayDeque = new ArrayDeque<>(List.of("a","b","c"));
        arrayDeque.addFirst("add at first removeFirst Gives us this one");
        arrayDeque.addLast("adds at last removes by removeLast");
        PriorityQueue<String> pq = new PriorityQueue<>(List.of("a","b","c"));
        pq.poll();// it returns First element from sorted queue
        Queue<String> queueString = new ConcurrentLinkedQueue<>(List.of("a","b","c"));// thread-safe
        // =================== Stream API
        List<String> names = List.of("Milyon","Yakob","Aaron","Merhawi","Aaron","Milyon");
        // 1) Basics: filter + map + toList / toSet Match & find Reduce (sum/concat/custom)
        List<String> aUpper = names.stream().filter(a -> a.startsWith("Y"))
                .sorted().limit(2).map(String::toUpperCase).skip(1).collect(Collectors.toList());
        boolean anyMatch = names.stream().anyMatch(a->a.startsWith("M"));// allMatch the same boolean
        Optional<String> findFirst = names.parallelStream().findFirst();//findAny() the same return optional
        String joined = names.stream().reduce("",(a,b)->a.isEmpty()?b:a+","+b);
        int sum = IntStream.rangeClosed(1,5).reduce(0,Integer::sum);
        // 5) Collectors: groupingBy / partitioningBy / counting / mapping / joining
        Map<Character,Long> countByFirstChar = names.stream().collect(Collectors.groupingBy(a->a.charAt(0),Collectors.counting()));//{A:1,M:2..} groupingByFirstCar
        Map<Boolean,List<String>> byLength= names.stream().collect(Collectors.partitioningBy(a ->a.length()>5));//{false:[Yakob],ture:[Merhawi,Milyon,Aaron]}
        Map<Boolean,Long> byLengthCount= names.stream().collect(Collectors.partitioningBy(a ->a.length()>5,Collectors.counting()));//{false:1,ture:3}
        String uniqueSortedJoin = names.stream().distinct().sorted().collect(Collectors.joining("|"));
        Map<Character,Set<String>> mappedSet = names.stream().collect(Collectors.groupingBy(a ->a.charAt(0),Collectors.mapping(String::toUpperCase,Collectors.toSet())));
        List<List<String>> listOfLists = List.of(List.of("TX","WA"),List.of("CA","US"));
        List<String> flatedList = listOfLists.stream().flatMap(a->a.stream()).collect(Collectors.toList());
        //Map streams (entries, sort by value, toMap with merge)
        Map<String,Integer> scores = Map.of("A",3,"B",1,"C",2);
        List<String> keyScores = scores.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).toList();
       // takeWhile / dropWhile (Java 9+)
        List<Integer> nums = List.of(1,2,3,2,1);
        List<Integer> upToSecondIndex = nums.stream().takeWhile(a -> a<=2).collect(Collectors.toList());
        List<Integer> tailAfterGt2  = nums.stream().dropWhile(x -> x <= 2).toList();                                      // [3,2,1]
        // Custom comparator (multi-key)
        List<String> byLenThenAlpha = names.stream()
                .distinct()
                .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
                .toList();
        // generate / iterate (bounded!)
        List<Double> randoms = Stream.generate(Math::random).limit(3).toList();                                          // e.g. [0.12, 0.87, 0.43]
        List<Integer> seq = Stream.iterate(1, x -> x + 2).limit(5).toList();

    }








    }

