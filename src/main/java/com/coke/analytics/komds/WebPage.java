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
    private String location;

    WebPage(String url, Integer timeout, Boolean ignoreHttpErrors, Boolean followRedirects) {
        /* Creates a WebPage object
          @param url String url to pare
         */
        try {
            Response response = Jsoup.connect(url)
                    .timeout(timeout)
                    .ignoreHttpErrors(ignoreHttpErrors)
                    .followRedirects(followRedirects)
                    .execute();
            statusCode = response.statusCode();
            statusMessage = response.statusMessage();
            parsedBody =  response.parse();
            location = response.header("location");

        } catch (UnknownHostException e) {
            this.statusCode = 105;
            this.statusMessage = "Name not resolved";
        } catch(SSLHandshakeException e) {
            this.statusCode = 106;
            this.statusMessage = "SSL handshake error";
        } catch (HttpStatusException e) {
            this.statusCode = 404;
            this.statusMessage = "Page not found";
        } catch (IOException | NullPointerException ignored) { } // being lazy

    }

    /**
     * Returns the integer value of a HTTP response, 105 if server not found
     * @return     Integer
     */

    Integer getStatusCode() {
        return this.statusCode;
    }

    /**
     * Returns the String value of a HTTP response, 'Name not resolved' if server not found
     * @return     Integer
     */
    String getStatusMessage() {
        return this.statusMessage;
    }

    /**
     * Returns the Document value of a parsed webpage
     * @return     Document
     */
    Document getParsedBody() {
        return this.parsedBody;
    }

    /**
     * Returns the response location a parsed webpage
     * @return     String
     */
    String getLocation() {
        return this.location;
    }


    /**
     * Returns the remote scripts webpage
     * @return     List
     */
    List<String> getRemoteScripts() {
        List<String> list = new ArrayList<>();
        this.parsedBody.select("script[src$=.js]").forEach(l -> list.add(l.attr("src")));
        return list;
    }

    /**
     * Returns a list of inline scripts
     * @return    List<String>
     */
    List<String> getInlineScripts() {
        List<String> list = new ArrayList<>();
        this.parsedBody.getElementsByTag("script").forEach(script -> list.add(script.data()));
        return list;
    }

    /**
     * Returns a the name of the parent element of the user-defined remote script, if found
     * @return   String
     */

    String getRemoteScriptParentElement(String elementSrc) {
        Element element = this.parsedBody.select("script[src$="+elementSrc+"]").first();
        return element.parentNode().nodeName();
    }

    /**
     * Returns a the name of the parent element of the user-defined inline script, if found
     * @return   String
     */

    String getInlineScriptsParentElement(String elementFilter) {
        Element script = this.parsedBody.getElementsByTag("script").first();
        return script.parentNode().nodeName();
    }

}

class WebPageBuilder {

    private String url;
    private int timeout = 30000;
    private Boolean ignoreHttpErrors = false;
    private Boolean followRedirects = false;


    WebPageBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    WebPageBuilder setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }


    WebPageBuilder setIgnoreHttpErrors() {
        this.ignoreHttpErrors = true;
        return this;
    }

    WebPageBuilder setFollowRedirects() {
        this.followRedirects = true;
        return this;
    }

    WebPage build() {
        return new WebPage(url, timeout, ignoreHttpErrors, followRedirects);
    }
}
