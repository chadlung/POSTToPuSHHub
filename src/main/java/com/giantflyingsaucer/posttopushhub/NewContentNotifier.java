package com.giantflyingsaucer.posttopushhub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * POST to a PuSH Hub Server
 * New Content Notification
 * Reference:  http://pubsubhubbub.googlecode.com/svn/trunk/pubsubhubbub-core-0.3.html#anchor9
 * 
 */
public class NewContentNotifier implements Runnable {
    
    private final String HUB_URL;
    private final String ENTRY_URL;
    private final String FORM_PARAM_HUB_MODE = "hub.mode";
    private final String FORM_PARAM_HUB_URL = "hub.url";
    private final String PUBLISH = "publish";
    private final String CONTENT_TYPE = "Content-type";
    private final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private final String USER_AGENT = "User-agent";
    private final String USER_AGENT_VALUE = "Atom Hopper - PuSH PubSubHubBub 0.3";
    
    public NewContentNotifier(String hubURL, String entryURL) {
        this.HUB_URL = hubURL;
        this.ENTRY_URL = entryURL;
    }
    
    private void logMessage(Level level, String message) {
        Logger.getLogger(NewContentNotifier.class.getName()).log(level, message);
    }

    public void run() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(HUB_URL);
        
        try {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair(FORM_PARAM_HUB_MODE, PUBLISH));
            nvps.add(new BasicNameValuePair(FORM_PARAM_HUB_URL, ENTRY_URL));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            httpPost.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
            httpPost.setHeader(USER_AGENT, USER_AGENT_VALUE);
        } catch (UnsupportedEncodingException ex) {
            logMessage(Level.SEVERE, ex.getMessage());
        }

        ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {

            public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

                logMessage(Level.INFO, "HTTP Status Code: " + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                } else {
                    return null;
                }
            }
        };

        try {
            byte[] response = httpclient.execute(httpPost, handler);

            if (response != null) {
                logMessage(Level.INFO, new String(response));
            }
        } catch (IOException ioex) {
            logMessage(Level.SEVERE, ioex.getMessage());
        }
    }
}
