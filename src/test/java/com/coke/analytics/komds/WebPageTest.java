package com.coke.analytics.komds;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


class WebPageTest {
    private String goodUrl = "https://us.coca-cola.com/";
    private String badUrl = "https://bad-cola.com";

    private WebPage webPage = new WebPageBuilder().setUrl(goodUrl).build();
    private WebPage badWebPage = new WebPageBuilder().setUrl(badUrl).build();

    @Test
    void WebPagedTest() {

    }

    @Test
    void getResponseCodeTest() {
        int statusCode = webPage.getStatusCode();
        int badStatusCode = badWebPage.getStatusCode();

        assertThat(statusCode, is(200));

        assertThat(badStatusCode, is(105));
        assertThat(badStatusCode, not(200));
    }

    @Test
    void getResponseStatusTest() {
        String statusMessage = webPage.getStatusMessage();
        String badStatusMessage = badWebPage.getStatusMessage();

        assertThat(statusMessage, is("OK"));

        assertThat(badStatusMessage, is("Name not resolved"));
        assertThat(badStatusMessage, not("OK"));
    }


    @Test
    void getParsedBodyTest() {
        String title = "Unlock Holiday Magic with Coca-Cola | Coca Cola";

        assertThat(webPage.getParsedBody().title(), is(title));
        assertThat(webPage.getParsedBody().charset(), is(StandardCharsets.UTF_8));
        assertThat(webPage.getParsedBody().location(), is(goodUrl));
        assertThat(webPage.getParsedBody().location(), not(badUrl));
    }


    @Test
    void getRemoteScriptsTest() {
        String firstScript = "https://s3.amazonaws.com/brandscom/sso/prod/post-robot.js";

        List<String> scripts = webPage.getRemoteScripts();

        assertThat(scripts.contains(firstScript), is(true));
    }

    @Test
    void getInlineScriptsTest() {
        String firstScript = "GTM-";
        String expectedResult = "(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start': \n" +
                "new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0], \n" +
                "j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src= \n" +
                "'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f); \n" +
                "})(window,document,'script','dataLayer','GTM-KL6MCF');";

        List<String> scripts = webPage.getInlineScripts().stream()
                .filter(l -> l.contains(firstScript))
                .collect(Collectors.toList());

        assertThat(scripts.get(0), is(expectedResult));
    }

    @Test
    void getRemoteScriptParentElementTest() {
        String firstScript = "https://s3.amazonaws.com/brandscom/sso/prod/post-robot.js";

        String elementParent = webPage.getRemoteScriptParentElement(firstScript);
        assertThat(elementParent, is("head"));
    }


    @Test
    void getInlineScriptsParentElementTest() {
        String scriptFilter = "GTM-";

        String inlineScriptParent = webPage.getInlineScriptsParentElement(scriptFilter);
        assertThat(inlineScriptParent, is("head"));
    }

}