package com.sms.util;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread pool management service providing asynchronous background task execution.
 * Ensures the web container's HTTP request-response thread is never blocked during
 * heavy report generation or email dispatch.
 * 
 * // [CONCURRENCY] ExecutorService thread pool for background report generation and email notifications
 * // [CONCURRENCY] Clean lifecycle shutdown
 */
public final class ThreadPoolManager {

    private static final Logger LOGGER = Logger.getLogger(ThreadPoolManager.class.getName());
    private static volatile ThreadPoolManager instance;

    private final ExecutorService executorService;

    private ThreadPoolManager() {
        // Creates a fixed pool of daemon worker threads for async background jobs
        this.executorService = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private int counter = 1;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "SMS-WorkerThread-" + counter++);
                t.setDaemon(true);
                return t;
            }
        });
        LOGGER.info("SMS ThreadPoolManager initialized with 4 background worker threads.");
    }

    /**
     * Thread-safe Singleton accessor.
     */
    public static ThreadPoolManager getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolManager.class) {
                if (instance == null) {
                    instance = new ThreadPoolManager();
                }
            }
        }
        return instance;
    }

    /**
     * Submits a Runnable background task (e.g., non-blocking notification delivery).
     */
    public void execute(Runnable task) {
        executorService.execute(task);
    }

    /**
     * [CONCURRENCY] Submits a Callable task returning a Future for asynchronous result computation.
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    /**
     * [CONCURRENCY] Simulates an asynchronous email notification in a background thread.
     */
    public void sendAsyncNotification(String recipientEmail, String subject, String messageContent) {
        execute(() -> {
            try {
                LOGGER.info(() -> String.format("[Thread: %s] Beginning background notification to: %s",
                        Thread.currentThread().getName(), recipientEmail));
                // Simulate network latency for SMTP server communication
                Thread.sleep(1500);
                LOGGER.info(() -> String.format("[Thread: %s] EMAIL DELIVERED! Subject: '%s' -> %s",
                        Thread.currentThread().getName(), subject, recipientEmail));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Notification thread interrupted", e);
            }
        });
    }

    /**
     * Gracefully shuts down the executor pool during servlet context destruction.
     */
    public void shutdown() {
        LOGGER.info("Initiating graceful shutdown of SMS ThreadPoolManager...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("SMS ThreadPoolManager shut down successfully.");
    }
}
