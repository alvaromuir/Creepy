package com.coke.analytics.komds;

import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/** Represents a parsed web page, if found.
 * Created by Alvaro Muir <alvaro@coca-cola.com>
 * KO MDS Digital Analytics
 * Nov 07, 2019
 */
class WebPage {
    private Integer statusCode;
    private String statusMessage;
    private Document parsedBody;

    WebPage(String url, Integer timeout, Boolean followRedirects) {
        /* Creates a WebPage object
          @param url String url to pare
         */
        try {
            Response response = Jsoup.connect(url)
                    .timeout(timeout)
                    .ignoreHttpErrors(true)
                    .followRedirects(followRedirects)
                    .execute();
            statusCode = response.statusCode();
            statusMessage = response.statusMessage();
            parsedBody =  response.parse();

        } catch (UnknownHostException e) {
            this.statusCode = 105;
            this.statusMessage = "Name not resolved";
        } catch (HttpStatusException e) {
            this.statusCode = 404;
            this.statusMessage = "Page not found";
        } catch(SSLHandshakeException e) {
            this.statusCode = 106;
            this.statusMessage = "SSL handshake error";
        } catch (IOException | NullPointerException ignored) { }

    }

    /**
     * Returns the integer value of a HTTP response, 105 if server not found
     * @return     Integer responseCode
     */

    Integer getStatusCode() {
        return this.statusCode;
    }

    /**
     * Returns the String value of a HTTP response, 'Name not resolved' if server not found
     * @return     Integer responseCode
     */
    String getStatusMessage() {
        return this.statusMessage;
    }

    /**
     * Returns the Document value of a parsed webpage
     * @return      Document responseParsedBody
     */
    Document getParsedBody() {
        return this.parsedBody;
    }

    List<String> getRemoteScripts() {
        List<String> list = new ArrayList<>();
        this.parsedBody.select("script[src$=.js]").forEach(l -> list.add(l.attr("src")));
        return list;
    }

    List<String> getInlineScripts() {
        List<String> list = new ArrayList<>();
        this.parsedBody.getElementsByTag("script").forEach(script -> list.add(script.data()));
        return list;
    }

    String getRemoteScriptParentElement(String elementSrc) {
        Element element = this.parsedBody.select("script[src$="+elementSrc+"]").first();
        return element.parentNode().nodeName();
    }

    String getInlineScriptsParentElement(String elementFilter) {
        Element script = this.parsedBody.getElementsByTag("script").first();
        return script.parentNode().nodeName();
    }

}

class WebPageBuilder {

    private String url;
    private int timeout = 30000;
    private Boolean followRedirects = false;


    WebPageBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    WebPageBuilder setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }


    WebPageBuilder setFollowRedirects() {
        this.followRedirects = true;
        return this;
    }

    WebPage build() {
        return new WebPage(url, timeout, followRedirects);
    }
}
