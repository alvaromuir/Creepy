package com.coke.analytics.komds;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import static com.coke.analytics.komds.Utils.distillBaseUrl;
import static com.coke.analytics.komds.Utils.readFileToList;



class UtilsTest {
    private String basePath = new File("").getAbsolutePath();

    @Test
    void distillBaseUrlTest() {
        String baseTestUrl = "https://us.coca-cola.com/";

        assertThat(distillBaseUrl(baseTestUrl), is("www.coca-cola.al"));
        assertThat(distillBaseUrl(baseTestUrl), not(baseTestUrl));

    }
    @Test
    void readFromFileTest() {
        String fileName = "input.txt";

        List urlLIst = readFileToList(basePath + "/" + fileName);
        assertThat(urlLIst.size(), is(328));

        String firstVal = "www.coca-cola.al";
        assertThat(urlLIst.contains(firstVal), is(true));

        String unexpectedUrl = "https://us.coca-cola.com/";
        assertThat(urlLIst.contains(unexpectedUrl), is(false));

    }

}