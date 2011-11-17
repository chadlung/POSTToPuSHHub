package com.giantflyingsaucer.posttopushhub;

public class MainApp 
{
    public static void main( String[] args )
    {
        // Testing URLS
        final String hubURL = "http://localhost:8080/EchoService-1.0-SNAPSHOT/";
        final String newEntryURL = "http://www.some-atom-hopper.com/namespace/feed/entries/r23408ytwfr23";
        
        Thread newContentNotifierThread = new Thread(new NewContentNotifier(hubURL, newEntryURL));
        newContentNotifierThread.start();
    }
}
