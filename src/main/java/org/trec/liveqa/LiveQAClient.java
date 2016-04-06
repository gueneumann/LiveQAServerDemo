package org.trec.liveqa;


/* LiveQAClient 
 *
 *  @author : varanasi.stalin@gmail.com
 *  Class to send (qid,title,body,category) as http request to the LiveQA Server 
 *  
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
 

import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class LiveQAClient {
 
    private static final String USER_AGENT = "Mozilla/5.0";
 
    private static final String GET_URL = "http://localhost:9090/SpringMVCExample";
 
    //private static final String POST_URL = "http://localhost:11000";
    private static final String POST_URL = "http://www.dfki.de/liveQA"; 
 
    public static void main(String[] args) throws IOException {
       // sendGET();
       //System.out.println("GET DONE");
        sendPOST("1","Fire ant bites.. help me..?","","");
        System.out.println("POST DONE");
    }
 
    private static void sendGET() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(GET_URL);
        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
 
        System.out.println("GET Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());
 
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));
 
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
 
        // print result
        System.out.println(response.toString());
        httpClient.close();
    }
 
    public static String sendPOST(String qid,String title,String body,String category) throws IOException {
 
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        HttpPost httpPost = new HttpPost(POST_URL);
        httpPost.addHeader("User-Agent", USER_AGENT);
 
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("qid", qid));
        urlParameters.add(new BasicNameValuePair("title", title));
        urlParameters.add(new BasicNameValuePair("body", body));
        urlParameters.add(new BasicNameValuePair("category", category));        
        
        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        httpPost.setEntity(postParams);
 
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
 
        System.out.println("POST Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());
 
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));
 
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
 
        // print result
        //System.out.println(response.toString());
        httpClient.close();
        //Return response xml from server
        return response.toString();
 
    }
 
}