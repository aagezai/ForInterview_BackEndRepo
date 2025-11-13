package org.example.JavaDataStructureSummary;

import java.util.*;
import java.util.stream.Collectors;

public class Most_Asked_Java_Interview_Questions {
        public void main(String[] args) {
            // 1) Convert list of Strings to a sorted list of Characters
            System.out.println("convert list of Strings to a sorted list of Characters");
            List.of("AB $C", "DEF", "ABC")
                    .stream()
                    .flatMapToInt(String::chars)
                    .mapToObj(ch -> (char) ch)
                    .sorted()
                    .collect(Collectors.toList())
                    .forEach(System.out::print);

            // 2) Convert list of Strings to Characters and print in reverse order
            System.out.println("\n+++ convert list of Strings to Characters and print in reverse order");
            List.of("ABC", "DEFF", "GHIJ")
                    .stream()
                    .flatMap(str -> Arrays.stream(str.split("")))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList())
                    .forEach(System.out::print);

            // 3) Find non-repeated numbers (appear exactly once)
            System.out.println("\n+++++++ find non-repeated numbers (appear exactly once)");
            List<Integer> numbers = List.of(5, 3, 6, 6, 2, 7, 7, 4, 1);
            List<Integer> nonRepeated = numbers.stream()
                    .collect(Collectors.groupingBy(el -> el, Collectors.counting()))
                    .entrySet().stream()
                    .filter(e -> e.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            System.out.println("using distinct() (unique values in reverse order):");
            numbers.stream().distinct().sorted(Comparator.reverseOrder()).forEach(System.out::print);

            System.out.println("\n++++++++++");
            System.out.println("Original List: " + numbers);
            System.out.println("List with only non-repeated numbers: " + nonRepeated);

            // 4) Keep only alphabetic characters from list of words
            System.out.println("\n+++++ keep only alphabetic characters from each word");
            List.of("A@B", "CD#", "2E")
                    .stream()
                    .map(s -> s.replaceAll("[^a-zA-Z]", ""))
                    .collect(Collectors.toList())
                    .forEach(System.out::print);

            // 5) Employees demo: second and third by salary, sorting, grouping, max-per-dept
            System.out.println("\n++++++++ find the 2nd and 3rd employees by salary (descending)");
            List<EmployeeUser> employees = List.of(
                    new EmployeeUser(121212L, "E1", 100_000, "D1"),
                    new EmployeeUser(121212L, "E2", 110_000, "D2"),
                    new EmployeeUser(121212L, "E0", 8_000, "D3"),
                    new EmployeeUser(233334L, "E5", 110_000, "D2"),
                    new EmployeeUser(12_144_212L, "E6", 1_000_000, "D1")
            );

            employees.stream()
                    .sorted(Comparator.comparing(EmployeeUser::getSalary).reversed()
                            .thenComparing(EmployeeUser::getDepartment)
                            .thenComparing(EmployeeUser::getName))
                    .skip(1)   // skip top (1st)
                    .limit(2)  // take 2nd and 3rd
                    .forEach(System.out::println);

            System.out.println("++++ sort employees by salary desc, then by department, then by name");
            employees.stream()
                    .sorted(Comparator.comparing(EmployeeUser::getSalary).reversed()
                            .thenComparing(EmployeeUser::getDepartment)
                            .thenComparing(EmployeeUser::getName))
                    .forEach(System.out::println);

            System.out.println("++++ group employees by department");
            employees.stream()
                    .collect(Collectors.groupingBy(EmployeeUser::getDepartment))
                    .entrySet()
                    .forEach(System.out::println);

            System.out.println("++++ group by department and get employee with MAX salary in each department (sorted by max salary desc)");
            Map<String, Optional<EmployeeUser>> maxSalaryByDept = employees.stream()
                    .collect(Collectors.groupingBy(
                            EmployeeUser::getDepartment,
                            Collectors.maxBy(Comparator.comparingDouble(EmployeeUser::getSalary))
                    ));

            maxSalaryByDept.entrySet().stream()
                    .sorted((a, b) ->
                            b.getValue().map(EmployeeUser::getSalary).orElse(Double.NEGATIVE_INFINITY)
                                    .compareTo(a.getValue().map(EmployeeUser::getSalary).orElse(Double.NEGATIVE_INFINITY)))
                    .forEach(System.out::println);

            System.out.println("---------------------------------");

            // 6) Comparator demonstrations
            System.out.println("++++ sort employees by salary using different comparator styles (ascending)");
            Comparator<EmployeeUser> cmpVerbose = (x, y) -> {
                if (x.getSalary() < y.getSalary()) return -1;
                if (x.getSalary() > y.getSalary()) return 1;
                return 0;
            };
            Comparator<EmployeeUser> cmpDouble = Comparator.comparingDouble(EmployeeUser::getSalary);

            employees.stream().sorted(cmpVerbose).forEach(System.out::println);
            employees.stream().sorted(cmpDouble).forEach(System.out::println);

            System.out.println("---------------------------------");

            // 7) Reverse array of Integer without sorting
            System.out.println("-------------------- reverse array of Integer without sorting --------------------");
            Integer[] arr = {4, 1, 9, 3, 2, 9};
            for (int i = arr.length - 1; i >= 0; i--) {
                System.out.print(arr[i]);
            }
            System.out.println();

            System.out.println("----------- after converting to List, reverse the array ----------------------");
            Integer[] arr1 = {4, 1, 9, 3, 2, 9};
            List<Integer> reversed = new ArrayList<>(Arrays.asList(arr1));
            Collections.reverse(reversed);
            System.out.println(reversed);

            // 8) First duplicate element
            System.out.println("----------- first duplicate element ----------------------");
            List<Integer> withDuplicates = List.of(2, 3, 4, 2, 6, 8, 9, 3, 6);
            LinkedHashSet<Integer> seen = new LinkedHashSet<>();
            for (Integer x : withDuplicates) {
                if (!seen.add(x)) {
                    System.out.println("first duplicate: " + x);
                    break;
                }
            }

            System.out.println("#################################");

            // 9) Iterating and modifying a Map while traversing
            System.out.println("_________ Can you traverse/iterate a Map and modify it? _______");
            System.out.println("Using iterator.remove() on a MUTABLE map is allowed; modifying the map directly during iteration causes ConcurrentModificationException.");
            Map<Integer, String> mutableMap = new LinkedHashMap<>();
            mutableMap.put(1, "d");
            mutableMap.put(3, "b");

            System.out.println("Original map entries:");
            Iterator<Map.Entry<Integer, String>> it = mutableMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, String> e = it.next();
                System.out.println(e.getKey() + " -> " + e.getValue());
                // legal removal while iterating:
                if (e.getKey() == 1) it.remove(); // OK for mutable maps
                // ILLEGAL direct modification while iterating (will be caught):
                try {
                    if (e.getKey() == 3) {
                        mutableMap.put(4, "x"); // will cause CME on next iteration
                    }
                } catch (ConcurrentModificationException cme) {
                    System.out.println("Caught ConcurrentModificationException due to direct map modification during iteration");
                }
            }
            System.out.println("Map after safe iterator.remove(): " + mutableMap);

            // 10) HashMap vs LinkedHashMap order
            System.out.println("_________ Does HashMap preserve insertion order? Answer: No (not guaranteed). LinkedHashMap does. ");
            Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put(10, "A");
            linkedHashMap.put(20, "B");
            linkedHashMap.put(30, "C");
            linkedHashMap.entrySet().forEach(System.out::println);

            // 11) Build a map from Employee (id -> name)
            System.out.println("_________ Build a Map from Employee (id -> name) ");
            EmployeeUser a1 = new EmployeeUser(11_110L, "namee1", 11_100.00, "d1");
            EmployeeUser a2 = new EmployeeUser(22_210L, "namee2", 20_000.00, "d2");
            List<EmployeeUser> employeeUserList = List.of(a2, a1);
            employeeUserList.stream()
                    .collect(Collectors.toMap(EmployeeUser::getId, EmployeeUser::getName))
                    .forEach((id, name) -> System.out.println(id + " " + name));
        }
    }

    // Minimal POJO for the demos
    class EmployeeUser {
        private final long id;
        private final String name;
        private final double salary;
        private final String department;

        public EmployeeUser(long id, String name, double salary, String department) {
            this.id = id;
            this.name = name;
            this.salary = salary;
            this.department = department;
        }
        public long getId() { return id; }
        public String getName() { return name; }
        public double getSalary() { return salary; }
        public String getDepartment() { return department; }

        @Override
        public String toString() {
            return "EmployeeUser{id=" + id + ", name='" + name + "', salary=" + salary + ", department='" + department + "'}";
        }
    }


