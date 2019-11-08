package com.coke.analytics.komds;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;


/**
 * Created by Alvaro Muir <alvaro@coca-cola.com>
 * KO MDS Digital Analytics
 * Nov 06, 2019
 */

public class Creepy {
    private static final Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

    /**
     * Generate help information with Apache Commons CLI.
     *
     * @param options Instance of Options to be used to prepare
     *    help formatter.
     */
    private static void printHelp(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "creepy <parameters>";
        final String usageHeader = "Parameters";
        final String usageFooter = "README - https://bitbucket.coke.com/projects/KOMDS/repos/creepy";
        System.out.println("\ncreepy parses a url for KO MDS Global required tags.");
        formatter.printHelp(syntax, usageHeader, options, usageFooter);
    }

    /**
     * Generate usage information with Apache Commons CLI.
     *
     * @param options Instance of Options to be used to prepare
     *    usage formatter.
     */
    private static void printUsage(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "komds-creepy";
        final PrintWriter pw  = new PrintWriter(System.out);
        formatter.printUsage(pw, 80, syntax, options);
        pw.flush();
    }

    /**
     * Returns all selected from command line
     * @param option Option object
     * @param commandLine CommandLine object
     * @return
     */
    private static String getOption(final char option, final CommandLine commandLine) {
        if (commandLine.hasOption(option)) {
            return commandLine.getOptionValue(option);
        }
        return StringUtils.EMPTY;
    }

    public static void main(String[] args) throws Exception {
        final Options options = new Options();

        options.addOption("u", "url", true, "url to scan");
        options.addOption("l", "list", true, "list urls (comma separated) to scan");
        options.addOption("f", "file", true, "file of urls (one per line) to scan");
        options.addOption("t", "limit", true, "limit of threads to use, default 10");
        options.addOption("o", "timeout", true, "timeout in seconds, default 3");
        options.addOption("?", "help", false, "prints help information");

        final CommandLineParser parser = new DefaultParser();

        try {
            final CommandLine cmd = parser.parse(options, args);
            HelpFormatter formatter = new HelpFormatter();

            final String url = getOption('u', cmd);
            final String list = getOption('l', cmd);
            final String file = getOption('f', cmd);
            final String limit = getOption('t', cmd);
            final String timeout = getOption('o', cmd);

            if (cmd.hasOption('?') || cmd.hasOption("help")) {
                printHelp(options);
            } else {

                int threadLimit = cmd.hasOption('t') ? Integer.parseInt(limit) : 10;
                int timeOut = cmd.hasOption('o') ? Integer.parseInt(timeout) : 3000;
                TaskRunner taskRunner = new TaskRunner(threadLimit);

                Semaphore semaphore = taskRunner.getSemaphore();

                if(cmd.hasOption('u')) {
                    TaskRunner.printCrawlResults(url, timeOut);
                }

                if(cmd.hasOption('l')) {
                    List<String> urls = Arrays.asList(list.split("\\s*,\\s*"));
                    urls.forEach(l -> new CrawlerThread(semaphore, l, timeOut).start());
                }

                if(cmd.hasOption('f')) {
                    List<String> urls = Utils.readFileToList(file);
                    urls.forEach(l -> new CrawlerThread(semaphore, l, timeOut).start());
                }
            }
        } catch( ParseException e ) {
            String errMsg = "An error has occurred: " + e.getMessage();
            printUsage(options);
            log.error(errMsg);
            throw new Exception(errMsg);
        }
    }

}