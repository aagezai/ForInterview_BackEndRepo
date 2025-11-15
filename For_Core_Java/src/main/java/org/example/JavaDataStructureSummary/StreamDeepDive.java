package org.example.JavaDataStructureSummary;

import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

public class StreamDeepDive {
    public static void main(String[] args) {
        List<String> names = List.of("Milyon","Yakob","Aaron","Merhawi","Aaron","Milyon");

        // 1) Basics: filter + map + toList / toSet
        List<String> aUpper = names.stream().filter(n -> n.startsWith("A")).map(String::toUpperCase).toList();          // [AARON, AARON]
        Set<String> unique  = names.stream().collect(toSet());                                                           // [Yakob, Merhawi, Aaron, Milyon] (order not guaranteed)

        // 2) Distinct, sorted, limit/skip
        List<String> uniqSorted = names.stream().distinct().sorted().toList();                                           // [Aaron, Merhawi, Milyon, Yakob]
        List<String> top2       = names.stream().distinct().sorted().limit(2).toList();                                  // [Aaron, Merhawi]
        List<String> after1     = names.stream().skip(1).limit(3).toList();                                              // [Yakob, Aaron, Merhawi]

        // 3) Match & find
        boolean anyM = names.stream().anyMatch(n -> n.startsWith("M"));                                                  // true
        boolean allLenGt3 = names.stream().allMatch(n -> n.length() > 3);                                                // true
        Optional<String> first = names.stream().findFirst();                                                             // Optional[Milyon]
        Optional<String> anyA  = names.parallelStream().filter(n -> n.startsWith("A")).findAny();                        // Optional[Aaron]

        // 4) Reduce (sum/concat/custom)
        int sum = IntStream.rangeClosed(1, 5).reduce(0, Integer::sum);                                                   // 15
        String joined = names.stream().reduce("", (a,b) -> a.isEmpty()? b : a + ", " + b);                                // "Milyon, Yakob, Aaron, Merhawi, Aaron, Milyon"

        // 5) Collectors: groupingBy / partitioningBy / counting / mapping / joining
        Map<Character, Long> countByFirst =
                names.stream().collect(groupingBy(n -> n.charAt(0), counting()));                                        // {A=2, M=3, Y=1}
        Map<Character, List<String>> byFirst =
                names.stream().collect(groupingBy(n -> n.charAt(0)));                                                    // {A=[Aaron, Aaron], M=[Milyon, Merhawi, Milyon], Y=[Yakob]}
        Map<Boolean, List<String>> byLen =
                names.stream().collect(partitioningBy(n -> n.length() > 5));                                             // {false=[Yakob], true=[Milyon, Aaron, Merhawi, Aaron, Milyon]}
        String csvUniqueSorted =
                names.stream().distinct().sorted().collect(joining("|"));                                                // "Aaron|Merhawi|Milyon|Yakob"
        Map<Character, Set<String>> mappedSet =
                names.stream().collect(groupingBy(n -> n.charAt(0), mapping(String::toUpperCase, toSet())));             // {A=[AARON], M=[MERHAWI, MILYON], Y=[YAKOB]}

        // 6) Summaries
        IntSummaryStatistics stats =
                names.stream().mapToInt(String::length).summaryStatistics();                                             // count, sum, min, avg, max

        // 7) FlatMap (flatten nested)
        List<List<String>> nested = List.of(List.of("TX","WA"), List.of("CA","NY"));
        List<String> flat = nested.stream().flatMap(List::stream).toList();                                              // [TX, WA, CA, NY]

        // 8) Arrays & primitive streams
        int[] arr = {5,2,9,2};
        List<Integer> arrBoxed = Arrays.stream(arr).boxed().toList();                                                    // [5,2,9,2]
        int[] dedupSorted = Arrays.stream(arr).distinct().sorted().toArray();                                            // [2,5,9]

        // 9) Map streams (entries, sort by value, toMap with merge)
        Map<String,Integer> scores = Map.of("A",3,"B",1,"C",2);
        List<String> byValueAsc = scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).toList();                                                                        // [B, C, A]
        Map<Character,Integer> toMapMerge = names.stream()
                .collect(toMap(n -> n.charAt(0), n -> 1, Integer::sum));                                                 // {A=2, M=3, Y=1}

        // 10) collectingAndThen / toUnmodifiable
        List<String> unmodifiable = names.stream().distinct().sorted()
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));                                     // immutable list

        // 11) takeWhile / dropWhile (Java 9+)
        List<Integer> nums = List.of(1,2,3,2,1);
        List<Integer> headUntilGt2 = nums.stream().takeWhile(x -> x <= 2).toList();                                      // [1,2]
        List<Integer> tailAfterGt2  = nums.stream().dropWhile(x -> x <= 2).toList();                                      // [3,2,1]

        // 12) generate / iterate (bounded!)
        List<Double> randoms = Stream.generate(Math::random).limit(3).toList();                                          // e.g. [0.12, 0.87, 0.43]
        List<Integer> seq = Stream.iterate(1, x -> x + 2).limit(5).toList();                                             // [1,3,5,7,9]

        // 13) Parallel stream (use when heavy & order not required)
        int parSum = IntStream.range(1, 1_000_000).parallel().reduce(0, Integer::sum);                                   // 499999500000

        // 14) Custom comparator (multi-key)
        List<String> byLenThenAlpha = names.stream()
                .distinct()
                .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
                .toList();                                                                                                // [Aaron, Yakob, Merhawi, Milyon]

        // 15) Optional pipelines (map/filter/orElse)
        int lenIfA = names.stream().filter(n -> n.startsWith("A")).findFirst().map(String::length).orElse(0);            // 5
    }
}
