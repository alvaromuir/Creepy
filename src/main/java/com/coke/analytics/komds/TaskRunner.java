package com.coke.analytics.komds;

import java.util.concurrent.Semaphore;

import static com.coke.analytics.komds.Crawler.crawlUrl;

/** Runs the crawler with user-defined number of threads
 * Created by Alvaro Muir <alvaro@coca-cola.com>
 * KO MDS Digital Analytics
 * Nov 08, 2019
 */
class TaskRunner {
    private static int threadLimit;
    Semaphore semaphore;


    TaskRunner(int threadLimit) {
        TaskRunner.threadLimit = threadLimit;
        this.semaphore = new Semaphore(threadLimit);
    }

    Semaphore getSemaphore() {
        return this.semaphore;
    }

    /**
     * Prints desired results of the crawler
     * @param url String url to parse
     */
    static void printCrawlResults(String url, int timeout) {
        String results = crawlUrl(url, timeout);
        String header = results.isEmpty() ? "no results" :
                String.format("%-35s | %-4s | %-85s | %-8s | %-9s | %-11s | %-9s", "domain", "HTTP", "OT script",
                        "OT place", "OT source", "GTM-ID", "GTM place");
        System.out.println(header);
        System.out.println(results);
    }
}

class CrawlerThread extends Thread {
    private Semaphore semaphore;
    private String url;
    private int timeout;

    CrawlerThread(Semaphore semaphore, String url, int timeout) {
        this.semaphore = semaphore;
        this.url = url;
        this.timeout = timeout;
    }

    /**
     * Kicks off the process
     */
    public void run() {
        try {
            semaphore.acquire();
            TaskRunner.printCrawlResults(url, timeout);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ignored) { } // being lazy.
        finally {
            semaphore.release();
        }
    }
}