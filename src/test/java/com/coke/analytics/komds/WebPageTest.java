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
        String firstScript = "gtm";

        List<String> scripts = webPage.getInlineScripts().stream()
                .filter(l -> l.contains(firstScript))
                .collect(Collectors.toList());

        assertThat(scripts.size(), is(1));
    }

    @Test
    void getRemoteScriptParentElementTest() {
        String firstScript = "https://s3.amazonaws.com/brandscom/sso/prod/post-robot.js";

        String elementParent = webPage.getRemoteScriptParentElement(firstScript);
        assertThat(elementParent, is("head"));
    }


    @Test
    void getInlineScriptsParentElementTest() {
        String scriptFilter = "gtm";

        String inlineScriptParent = webPage.getInlineScriptsParentElement(scriptFilter);
        assertThat(inlineScriptParent, is("head"));
    }

}