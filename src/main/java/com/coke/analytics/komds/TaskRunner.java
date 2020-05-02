package com.coke.analytics.komds;

import java.util.concurrent.Semaphore;

import static com.coke.analytics.komds.Crawler.crawlUrl;

/** Runs the crawler with user-defined number of threads
 * Created by Alvaro Muir <alvaro@coca-cola.com>
 * KO MDS Digital Analytics
 * Nov 08, 2019
 */
class TaskRunner {
    Semaphore semaphore;


    TaskRunner(int threadLimit) {
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
        System.out.println(results);
    }
}

class CrawlerThread extends Thread {
    private final Semaphore semaphore;
    private final String url;
    private final int timeout;

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