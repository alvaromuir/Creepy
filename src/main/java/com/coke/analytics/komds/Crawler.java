package com.coke.analytics.komds;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Alvaro Muir <alvaro@coca-cola.com>
 * KO MDS Digital Analytics
 * Nov 08, 2019
 */

class Crawler {
    /**
     * Returns a formatted string of parsed web page elements including
     * a url, response code, OneTrust javascript, OneTrust doc placement
     * (head, body, footer, etc.), OneTrust URI source type (CDN, Single
     * locations), GTM Id and GTM doc placement (head, body, footer, etc.)
     * If response code is 3xx, follows link and returns requested values
     * @param url String url to parse
     */
     static String crawlUrl(String url, int timeout) {

        WebPage webPage = new WebPageBuilder().setUrl(url).setTimeout(timeout).build();

        StringBuilder oneTrustScript = new StringBuilder();
        StringBuilder oneTrustScriptPlacement = new StringBuilder();
        StringBuilder oneTrustHostingPlatform = new StringBuilder();
        StringBuilder gtmID = new StringBuilder();
        StringBuilder gtmIDPlacement = new StringBuilder();
        StringBuilder output = new StringBuilder();

        String statusCode = webPage.getStatusCode().toString();

        output.append(String.format("%-65s | %s", url, statusCode));

        if(statusCode.equals("200")) {
            parseScripts(webPage, oneTrustScript, oneTrustScriptPlacement, oneTrustHostingPlatform, gtmID, gtmIDPlacement);
        }

        if(statusCode.startsWith("3")) {
            WebPage redirectPage = new WebPageBuilder().setUrl(url).setFollowRedirects(true).build();

            parseScripts(redirectPage, oneTrustScript, oneTrustScriptPlacement, oneTrustHostingPlatform, gtmID, gtmIDPlacement);

        }

         output.append(oneTrustScript.toString().trim().equals("") ? String.format(" | %-89s", (Object) null): String.format(" | %-89s", oneTrustScript));
         output.append(oneTrustScriptPlacement.toString().trim().equals("") ? String.format(" | %s", (Object) null): String.format(" | %s", oneTrustScriptPlacement));
         output.append(oneTrustHostingPlatform.toString().trim().equals("") ? String.format(" | %-6s", (Object) null): String.format(" | %-6s", oneTrustHostingPlatform));
         output.append(gtmID.toString().trim().equals("") ? String.format(" | %-23s", (Object) null): String.format(" | %-23s", gtmID));
         output.append(gtmIDPlacement.toString().trim().equals("") ? String.format(" | %-5s ", (Object) null): String.format(" | %-5s", gtmIDPlacement));

        return output.toString();

    }

    /**
     * Appends desired result strings with responses from script attribute queries
     * @param webPage WebPage object to query
     * @param oneTrustScript StringBuilder for OneTrust scripts
     * @param oneTrustScriptPlacement StringBuilder for OneTrust placements in doc (e.g. head, body, footer)
     * @param oneTrustHostingPlatform StringBuilder for OneTrust source platforms (CDN or Single Location)
     * @param gtmID StringBuilder for GTM Id (GTM-XXXXXXX)
     * @param gtmIDPlacement StringBuilder for GTM Id placements in doc (e.g. head, body, footer)
     */
    private static void parseScripts(WebPage webPage, StringBuilder oneTrustScript, StringBuilder oneTrustScriptPlacement, StringBuilder oneTrustHostingPlatform, StringBuilder gtmID, StringBuilder gtmIDPlacement) {
        List<String> oneTrustCdnScriptList = webPage.getRemoteScripts().stream()
                .filter(l -> l.contains("cdn.cookielaw.org"))
                .collect(Collectors.toList());

        parseOneTrust(webPage, oneTrustScript, oneTrustScriptPlacement, oneTrustHostingPlatform, oneTrustCdnScriptList);

        List<String> oneTrustSingleLocationScriptList = webPage.getRemoteScripts().stream()
                .filter(l -> l.contains("optanon.blob.core.windows.net"))
                .collect(Collectors.toList());

        parseOneTrust(webPage, oneTrustScript, oneTrustScriptPlacement, oneTrustHostingPlatform, oneTrustSingleLocationScriptList);

        List<String> gtmContainers = webPage.getInlineScripts().stream()
                .filter(l -> l.contains("gtm"))
                .collect(Collectors.toList());

        gtmID.append(getGtmId(gtmContainers));


        gtmIDPlacement.append(webPage.getInlineScriptsParentElement("gtm"));
    }

    /**
     * Parses a page for OneTrust settings
     * @param webPage WebPage object to query
     * @param oneTrustScript StringBuilder for OneTrust scripts
     * @param oneTrustScriptPlacement StringBuilder for OneTrust placements in doc (e.g. head, body, footer)
     * @param oneTrustHostingPlatform StringBuilder for OneTrust source platforms (CDN or Single Location)
     * @param oneTrustScriptsList List<String> of OneTrust source javascript URI's
     */
    private static void parseOneTrust(WebPage webPage,
                                      StringBuilder oneTrustScript,
                                      StringBuilder oneTrustScriptPlacement,
                                      StringBuilder oneTrustHostingPlatform,
                                      List<String> oneTrustScriptsList) {

        if (!oneTrustScriptsList.isEmpty()) {
            oneTrustScript.append(String.join(", ", oneTrustScriptsList));

            oneTrustScriptPlacement.append(oneTrustScriptsList.stream()
                    .map(webPage::getRemoteScriptParentElement)
                    .collect(Collectors.joining(", ")));

            oneTrustHostingPlatform.append(oneTrustScriptsList.stream()
                    .map(uri -> uri.contains("cdn") ? "CDN" : "Direct")
                    .collect(Collectors.joining(", ")));
        }
    }

    /**
     * Returns a valid GTM ID by parsing a GTM container script
     * @param inlineScriptsList List<String> of in-line GTM scripts
     * @return String GTM ID
     */
    private static String getGtmId(List<String> inlineScriptsList) {
        Pattern pattern = Pattern.compile("(GTM-)\\w+");

        return inlineScriptsList.stream()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .collect(Collectors.joining(","));
    }
}
