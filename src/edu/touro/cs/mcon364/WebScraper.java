package edu.touro.cs.mcon364;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebScraper {
    public static final Logger logger = LogManager.getLogger(WebScraper.class);

    public static void main(String[] args) {
        LinkedBlockingQueue<ScrapeURL> taskQueue = new LinkedBlockingQueue<>();
        taskQueue.add(new ScrapeURL("https://www.touro.edu/"));
        List<Email> allEmailList = new CopyOnWriteArrayList<>();
        Set<String> addedEmails = new CopyOnWriteArraySet<>();
        Set<String> processedLinks = new CopyOnWriteArraySet<>();
        AtomicInteger databaseConnectionCounter = new AtomicInteger();//do baches of 500 so when reaches 20 shutsdown the program

        ExecutorService es = Executors.newFixedThreadPool(16);

        // Create and start multiple worker threads
        for (int i = 0; i < 16; i++) {
            es.execute(() -> {
                while (true) {
                    try {
                        ScrapeURL scrapeURL = taskQueue.take();  // Blocks if the queue is empty
                        String url = scrapeURL.getUrl();
                        logger.info("Processing URL: {}", url);

                        scrapeURL.scrapeLinksAndEmails();


                        // Process emails
                        Set<Email> scrapedEmails = scrapeURL.getEmailList();
                        for (Email email : scrapedEmails) {
                            logger.info("Scraped Email: {}", email.getEmailAddress());
                        }

                        //Add them to main list and get rid of already added emails
                        synchronized (allEmailList) {
                            for (Email email : scrapedEmails) {
                                if (!addedEmails.contains(email.getEmailAddress())) {
                                    allEmailList.add(email);
                                    addedEmails.add(email.getEmailAddress());
                                }
                            }
                        }

                        // Check if got 10,000 emails
                        if (databaseConnectionCounter.get() >= 20) {
                            logger.info("Reached 10,000 emails. Stopping all threads.");
                            es.shutdownNow(); // Stop all threads
                            break;
                        }

                        synchronized (allEmailList) {
                            if (allEmailList.size() >= 500) {
                                logger.info("Reached 500 emails. Sending batch to Database.");
                                SaveToDatabase std = new SaveToDatabase(allEmailList);
                                std.saveEmailBatch();
                                databaseConnectionCounter.getAndIncrement();
                                allEmailList.clear();
                            }
                        }


                        // Get the scraped URLs
                        List<String> scrapedURLs = scrapeURL.getUrlList();

                        // Add new tasks to the queue
                        for (String scrapedURL : scrapedURLs) {
                            if (!processedLinks.contains(scrapedURL)) {
                                taskQueue.add(new ScrapeURL(scrapedURL));
                                processedLinks.add(scrapedURL);
                            }
                        }

                    } catch (InterruptedException e) {
                        // Handle the exception or break the loop
                        break;
                    }
                }
            });
        }


        // Shutdown the executor service once all tasks are completed
        es.shutdown();

    }
    }








