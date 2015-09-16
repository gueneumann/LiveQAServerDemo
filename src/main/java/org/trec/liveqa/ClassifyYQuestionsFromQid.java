package org.trec.liveqa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;



import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.trec.liveqa.ParseXMLString.UrlAnswerScore;
import org.trec.liveqa.TrecLiveQaDFKIServer.AnswerAndResources;

/**
 * Copyright 2015 Yahoo Inc.<br>
 * Licensed under the terms of the MIT license. Please see LICENSE file at the root of this project for terms.
 * <p/>
 * 
 * This class scrapes Yahoo Answers pages by a list of QIDs, obtaining certain properties.
 * 
 * @author varanasi.stalin@gmail.com
 * 
 */

public class ClassifyYQuestionsFromQid {

    public interface ElementPredicate {

        /**
         * Given an HTML Element, see if it conforms to a Yahoo Answers tag pattern specific to the desired property.
         * @param e
         * @return
         */
    	
        public boolean check(Element e);
        
        public String getText(Element e);

    }

    public static class CheckTitle implements ElementPredicate {

        @Override
        public boolean check(Element e) {
            return e.nodeName().equals("meta") && e.attr("name").equals("title");
        }

        @Override
        public String getText(Element e) {
            return e.attr("content");
        }

    }

    public static class CheckBody implements ElementPredicate {

        @Override
        public boolean check(Element e) {
            return e.nodeName().equals("meta") && e.attr("name").equals("description");
        }

        @Override
        public String getText(Element e) {
            return e.attr("content");
        }

    }

    public static class CheckBestAnswer implements ElementPredicate {

        @Override
        public boolean check(Element e) {
            // implement your own...
            return false;
        }

        @Override
        public String getText(Element e) {
            // implement your own...
            return null;
        }

    }

    public static class CheckTopLevelCategory implements ElementPredicate {

        @Override
        public boolean check(Element e) {
            return e.nodeName().equals("a") && e.className().contains("Clr-b");
        }

        @Override
        public String getText(Element e) {
            return e.text();
        }

    }

    // ---------------------------------------------------
    
    private static final String URL_PREFIX = "https://answers.yahoo.com/question/index?qid=";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
    private static CheckTitle ct = new CheckTitle();
    private static CheckBody cb = new CheckBody();
    private static CheckTopLevelCategory cc = new CheckTopLevelCategory();
    private static CheckBestAnswer cba = new CheckBestAnswer(); // not implemented
    private static ClassifyQ classifyq = new ClassifyQ();
    private static String title,body,categ;
    
    

    private static String answer="";
    private static boolean webqaflag = false;
    
    
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.out.println("Usage: ClassifyYQuestionsFromQid ./data/1k-qids.txt ./data/unanswered-qs.txt ./data/answered-qs.txt ./data/qapattern.train 1000");
            return;
        }
        
        // Train QA file
        Patterns pat=new Patterns();
        String QAfile=args[3];
        int threshold=Integer.valueOf(args[4]);
        pat.WordCounts(QAfile,threshold);
		pat.TrainVectors(QAfile,pat.freqs_g);
		pat.CalculateDFIDF();
		pat.FetchWord2Vecs("./data/Words2Vectors");
		
        BufferedWriter unansweredwriter = getWriter(args[1]);      
        BufferedWriter answeredwriter = getWriter(args[2]);   
        String[] qids = getQids(args[0]);
        int qidcounter=0;
        
        String propertiesfile="data/LiveQA.properties";
    	String maxtimestr=getParam("maxtime",propertiesfile);
    	String searchengine=getParam("searchengine",propertiesfile);
    	int maxtime=Integer.parseInt(maxtimestr);
    	
    	
        

        for (String qid : qids) 
        {
        	if ( qid == null )
        		continue;

        	qidcounter++;
        	System.out.println("Getting data for QID " + qid+"  ,qid counter-"+qidcounter+"\n");
        	
        	answer="";
        	webqaflag = false;
            for (Entry<String, String> kv : extractData(qid).entrySet()) {
            	if ( kv.getKey() == "Title")
            		title=kv.getValue();
            	if ( kv.getKey() == "Body")
            		body=kv.getValue();
            	if ( kv.getKey() == "Top level Category")
            		categ=kv.getValue();
            }
            
            long start =  System.currentTimeMillis();
    		float elapsedTimeSec=0;
    		
    		
    		AnswerAndResources answerandresources=null;
            System.out.println("\nTitle:"+title+"\nBody:"+body+"\n");
        	
            answerandresources=classifyq.analyzeYQsnippet(title,body,categ,pat,maxtime,searchengine);

            long elapsedTimeMillis =  System.currentTimeMillis()- start;
    		elapsedTimeSec = elapsedTimeMillis/1000F;
      
    		
            if ( answerandresources.answer() != "")
            {

            	answeredwriter.append(qid + ":");
            	answeredwriter.append("Title:"+title+"\n\n"+body+"\n");

            	answeredwriter.append("Answer:"+answerandresources.answer()+"\n-------\nTime Taken (seconds):"+elapsedTimeSec+"\n");
            	System.out.println("Answer:"+answerandresources.answer()+"\nResources:"+answerandresources.resources()+"\n-------\nTime Taken (seconds):"+elapsedTimeSec);
            	answeredwriter.flush();
            }
            else
            {
            	unansweredwriter.append(qid + ":");
            	unansweredwriter.append("Title:"+title+"\n\n"+body+"\nTime Taken (seconds):"+elapsedTimeSec);
                unansweredwriter.flush();      
            }
            
        }
        
        answeredwriter.flush();
        answeredwriter.close();
        
        
        unansweredwriter.flush();
        unansweredwriter.close();
        
    }

    private static String[] getQids(String inFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(inFile)));
        List<String> qids = new ArrayList<>();
        String l = reader.readLine();
        while (l != null) {
            qids.add(l);
            l = reader.readLine();
        }
        reader.close();
        return qids.toArray(new String[qids.size()]);
    }
    

    private static String getParam(String param,String inFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(inFile)));
        String l = reader.readLine();
        String[] varval;
       
        while (l != null) {
        	varval=l.split("=");
           if ( param.equals(varval[0]))
           {
        	   reader.close();
        	   return varval[1];
           }
            l = reader.readLine();
           }
        reader.close();
        
        return "";
    }
    
    private static BufferedWriter getWriter(String fileLocation) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileLocation))));
    }

    /**
     * 
     * @param iQid question ID
     * @return map of features and attributes: question title, body, category, best answer, date
     * @throws Exception
     */
    public static Map<String, String> extractData(String iQid) throws Exception {

        Map<String, String> res = new HashMap<>();

        // parse date from qid
        res.put("Date", DATE_FORMAT.parse(iQid.substring(0, 14)).toString());

        // get and mine html page
        String url = URL_PREFIX + iQid;
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            InputStream responseBody = method.getResponseBodyAsStream();

            // strip top levels
            Document doc = Jsoup.parse(responseBody, "UTF8", url);
            Element html = doc.child(0);
            
            Element body = html.child(1);
            Element head = html.child(0);

            // get category
            res.put("Top level Category", findElementText(body, cc));

            // get title
            res.put("Title", findElementText(head, ct));

            // get body
            res.put("Body", findElementText(head, cb));

            // get best answer
            // res.put("Best Answer", findElementText(body, cba));

            responseBody.close();

        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

        return res;
    }

    private static String findElementText(Element topElem, ElementPredicate f) {
        Element elem = findElement(topElem, f);
        return elem == null ? "" : f.getText(elem);
    }

    private static Element findElement(Element e, ElementPredicate f) {
        if (f.check(e)) {
            return e;
        }
        for (Element c : e.children()) {
            Element elem = findElement(c, f);
            if (elem != null) {
                return elem;
            }
        }
        return null;
    }

}
