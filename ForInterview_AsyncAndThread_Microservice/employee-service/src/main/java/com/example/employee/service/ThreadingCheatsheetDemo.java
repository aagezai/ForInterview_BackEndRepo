package com.example.employee.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadingCheatsheetDemo {

    // Shared state for demos
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final AtomicInteger ATOMIC = new AtomicInteger(0);
    private static volatile int VOLATILE_COUNTER = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Threading Cheatsheet Demo ===");

        rawThreadDemo();
        futureCallableDemo();
        completableFutureDemoWithCommonPool();
        completableFutureDemoWithCustomPool();
        latchSemaphoreLockDemo();
        scheduledExecutorDemo();
        uncaughtExceptionDemo();

        System.out.println("=== Done ===");
    }

    /** 1) Raw Thread + Runnable */
    private static void rawThreadDemo() throws InterruptedException {

        Thread t = new Thread(() -> {
            // simulate work
            sleep(200);
            VOLATILE_COUNTER++;
            System.out.println("[rawThread] ran; VOLATILE_COUNTER=" + VOLATILE_COUNTER);
        }, "raw-thread-1");

        t.start();
        t.join(); // wait
    }

    /** 2) ExecutorService + Callable + Future */
    private static void futureCallableDemo() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2, named("fixed-"));
        try {
            Callable<Integer> task = () -> {
                sleep(150);
                int val = ATOMIC.incrementAndGet();
                System.out.println("[fixed] callable val=" + val);
                return val;
            };
            Future<Integer> f = pool.submit(task);
            Integer result = f.get(1, TimeUnit.SECONDS);
            System.out.println("[fixed] future result=" + result);
        } finally {
            pool.shutdown();
        }
    }

    /** 3) CompletableFuture with common ForkJoinPool */
    private static void completableFutureDemoWithCommonPool() throws Exception {
        CompletableFuture<Integer> cf =
                CompletableFuture.supplyAsync(() -> {
                    sleep(100);
                    System.out.println("[CF-common] supply");
                    return 10;
                }).thenApply(v -> {
                    System.out.println("[CF-common] thenApply");
                    return v * 2;
                }).exceptionally(ex -> {
                    System.out.println("[CF-common] exceptionally: " + ex);
                    return 0;
                });

        Integer out = cf.get(1, TimeUnit.SECONDS);
        System.out.println("[CF-common] out=" + out);
    }

    /** 4) CompletableFuture with custom pool (recommended) */
    private static void completableFutureDemoWithCustomPool() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4, named("cf-"));
        try {
            CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> {
                sleep(120);
                System.out.println("[CF-pool] A");
                return 5;
            }, pool);

            CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> {
                sleep(80);
                System.out.println("[CF-pool] B");
                return 7;
            }, pool);

            // run both in parallel, then combine
            Integer combined = a.thenCombine(b, Integer::sum)
                                .orTimeout(1, TimeUnit.SECONDS)
                                .get();
            System.out.println("[CF-pool] combined=" + combined);
        } finally {
            pool.shutdown();
        }
    }

    /** 5) CountDownLatch + Semaphore + ReentrantLock */
    private static void latchSemaphoreLockDemo() throws InterruptedException {
        int workers = 3;
        CountDownLatch latch = new CountDownLatch(workers);
        Semaphore semaphore = new Semaphore(2); // allow 2 concurrent sections
        ExecutorService pool = Executors.newFixedThreadPool(workers, named("sync-"));

        try {
            for (int i = 0; i < workers; i++) {
                int idx = i;
                pool.submit(() -> {
                    try {
                        semaphore.acquire();
                        LOCK.lock();
                        try {
                            sleep(100 + idx * 30);
                            int after = ATOMIC.incrementAndGet();
                            System.out.println("[sync] idx=" + idx + " after=" + after);
                        } finally {
                            LOCK.unlock();
                            semaphore.release();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await(2, TimeUnit.SECONDS);
        } finally {
            pool.shutdown();
        }
    }

    /** 6) Scheduled tasks */
    private static void scheduledExecutorDemo() throws Exception {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1, named("sched-"));
        ScheduledFuture<?> sf = ses.schedule(() -> {
            System.out.println("[sched] run after 200ms");
        }, 200, TimeUnit.MILLISECONDS);

        sf.get(1, TimeUnit.SECONDS); // wait at most 1s
        ses.shutdown();
    }

    /** 7) UncaughtExceptionHandler + ThreadFactory */
    private static void uncaughtExceptionDemo() throws InterruptedException {
        ThreadFactory tf = r -> {
            Thread t = new Thread(r, "ueh-1");
            t.setUncaughtExceptionHandler((th, ex) ->
                    System.out.println("[UEH] thread=" + th.getName() + " ex=" + ex.getMessage()));
            return t;
        };
        Thread t = tf.newThread(() -> {
            throw new RuntimeException("boom");
        });
        t.start();
        t.join();
    }

    /* ===== helpers ===== */
    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static ThreadFactory named(String prefix) {
        AtomicInteger ctr = new AtomicInteger(1);
        return r -> {
            Thread t = new Thread(r, prefix + ctr.getAndIncrement());
            t.setDaemon(false);
            return t;
        };
    }
}
