package com.example.employee.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class AllInOneThreadDemo {

    // ===== Shared monitor & buffer for wait/notify demo =====
    static final Object MONITOR = new Object();
    static final Queue<Integer> BUFFER = new LinkedList<>();
    static final int BUFFER_LIMIT = 3;

    public static void main(String[] args) throws Exception {
        System.out.println("=== All-in-One Thread Demo ===");

        // 1) Runnable + run() vs start()
        Runnable work = () -> {
            System.out.println("[Runnable] running on: " + Thread.currentThread().getName());
            sleep(200);
        };

        System.out.println("\n-- run() (NO new thread; runs on main) --");
        Thread tRunOnly = new Thread(work, "tRunOnly");
        // Calling run() directly executes on current thread (main)
        tRunOnly.run();
        System.out.println("Current thread after run(): " + Thread.currentThread().getName());

        System.out.println("\n-- start() (creates NEW thread) --");
        Thread tStart = new Thread(work, "tStart");
        tStart.start(); // actually spawns a new thread
        tStart.join();  // wait for it to finish (join)

        // 2) join(): main waits until another thread completes
        System.out.println("\n-- join() example --");
        Thread longTask = new Thread(() -> {
            System.out.println("[join] started on: " + Thread.currentThread().getName());
            sleep(400);
            System.out.println("[join] finished");
        }, "longTask");
        longTask.start();
        System.out.println("Main is waiting for longTask...");
        longTask.join();
        System.out.println("Main continues after join.");

        // 3) wait()/notify(): simple producer/consumer with single producer & single consumer
        System.out.println("\n-- wait()/notify() producer-consumer (single consumer) --");
        Thread producer = new Thread(() -> produce(5), "producer-1");
        Thread consumer = new Thread(AllInOneThreadDemo::consume, "consumer-1");

        producer.start();
        consumer.start();

        producer.join();
        // give consumer time to drain
        sleep(300);
        // Interrupt consumer loop for demo end
        consumer.interrupt();
        consumer.join();

        // 4) notifyAll(): two consumers waiting; wake them all
        System.out.println("\n-- notifyAll() with two waiting consumers --");
        Thread consumerA = new Thread(AllInOneThreadDemo::consumeOnceThenWait, "consumerA");
        Thread consumerB = new Thread(AllInOneThreadDemo::consumeOnceThenWait, "consumerB");
        consumerA.start();
        consumerB.start();
        // Let both hit wait()
        sleep(200);

        // Producer puts one item and notifies ALL
        new Thread(() -> {
            synchronized (MONITOR) {
                BUFFER.offer(999);
                System.out.println("[notifyAll demo] produced 999; calling notifyAll()");
                MONITOR.notifyAll();
            }
        }, "producer-notifyAll").start();

        consumerA.join();
        consumerB.join();

        // 5) Callable via FutureTask (thread that returns a value)
        System.out.println("\n-- Callable via FutureTask --");
        Callable<Integer> callable = () -> {
            System.out.println("[Callable] running on: " + Thread.currentThread().getName());
            sleep(150);
            return 42;
        };
        FutureTask<Integer> ft = new FutureTask<>(callable);
        Thread callableThread = new Thread(ft, "callable-thread");
        callableThread.start();
        Integer result = ft.get(); // blocks until callable finishes
        System.out.println("[Callable] result = " + result);

        System.out.println("\n=== Demo complete ===");
    }

    // ---------- Producer/Consumer helpers ----------

    // Produce n items; use wait() if buffer is full; notify() a single waiter each time
    static void produce(int nItems) {
        for (int i = 1; i <= nItems; i++) {
            synchronized (MONITOR) {
                while (BUFFER.size() == BUFFER_LIMIT) {
                    try {
                        System.out.println("[producer] buffer full; waiting...");
                        MONITOR.wait();  // release lock, wait
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                }
                BUFFER.offer(i);
                System.out.println("[producer] produced: " + i + "  | buffer=" + BUFFER);
                MONITOR.notify(); // wake one waiting consumer
            }
            sleep(80);
        }
    }

    // Consumer loop; waits if buffer empty; stops when interrupted (for demo end)
    static void consume() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (MONITOR) {
                while (BUFFER.isEmpty()) {
                    try {
                        System.out.println("[" + Thread.currentThread().getName() + "] buffer empty; waiting...");
                        MONITOR.wait(); // release lock, wait for producer
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
                if (Thread.currentThread().isInterrupted()) break;
                Integer val = BUFFER.poll();
                System.out.println("[" + Thread.currentThread().getName() + "] consumed: " + val + "  | buffer=" + BUFFER);
                MONITOR.notify(); // signal producer that space is available
            }
            sleep(60);
        }
        System.out.println("[" + Thread.currentThread().getName() + "] consumer exiting");
    }

    // For notifyAll() demo: consume one item if present; otherwise wait once, then proceed
    static void consumeOnceThenWait() {
        synchronized (MONITOR) {
            if (BUFFER.isEmpty()) {
                try {
                    System.out.println("[" + Thread.currentThread().getName() + "] waiting (notifyAll demo)...");
                    MONITOR.wait(); // will be woken by notifyAll()
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            Integer v = BUFFER.poll();
            System.out.println("[" + Thread.currentThread().getName() + "] woke up, took: " + v + "  | buffer=" + BUFFER);
        }
    }

    // ---------- util ----------
    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

//Multi-threaded (shared list using synchronized block)
    //(only if you really want threads — slower in practice due to locks)
    public class SafeInPlaceRemove {
        public static void main(String[] args) throws InterruptedException {
            List<String> list = new CopyOnWriteArrayList<>(Arrays.asList(
                    "a", "bb", "ccc", "dddd", "ee", "f", "ggg"));

            int cores = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(cores);

            for (int i = 0; i < cores; i++) {
                executor.submit(() -> {
                    for (String s : list) {
                        if (s == null || s.length() < 3) {
                            list.remove(s); // safe with CopyOnWriteArrayList
                        }
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            System.out.println("Filtered list: " + list);
        }
    }


}
