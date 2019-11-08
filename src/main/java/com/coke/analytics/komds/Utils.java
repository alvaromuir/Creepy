package com.coke.analytics.komds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alvaro Muir <alvaro@coca-cola.com>
 * KO MDS Digital Analytics
 * Nov 07, 2019
 */

class Utils {
    /**
     * Returns an the base url from a supplied url string
     *
     * @param  inputUrl url to parse: https://wwww.example.com:80/?&foo=bar -> www.example.com
     * @return          String base url
     */
    static String distillBaseUrl(String inputUrl) {
        String baseUrl = "";
        try {
            URL url = new URL(inputUrl);
            baseUrl = url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            baseUrl = "ERROR: malformed input";
        }
        return baseUrl;
    }

    /**
     * Returns an list collection of strings based on supplied file path
     *
     * @param  filePath location of file to read
     * @return          List<String> of items from file
     */
    static List<String> readFileToList(String filePath) {
        List<String> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            list = stream.filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
