package org.trec.liveqa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fi.iki.elonen.NanoHTTPD;

/**
 * Copyright 2015 Yahoo Inc.<br>
 * Licensed under the terms of the MIT license. Please see LICENSE file at the root of this project for terms.
 * <p/>
 * Sample server-side application for 2015 TREC LiveQA challenge.<br>
 * Usage: TrecLiveQaDemoServer [port-id]<br>
 * Stops on any input.
 * 
 * @author yuvalp@yahoo-inc.com
 * 
 */

public class TrecLiveQaDFKIServer extends NanoHTTPD {

	public static final String PARTICIPANT_ID = "dfkiqa";

	public static final String QUESTION_ID_PARAMETER_NAME = "qid";
	public static final String QUESTION_TITLE_PARAMETER_NAME = "title";
	public static final String QUESTION_BODY_PARAMETER_NAME = "body";
	public static final String QUESTION_CATEGORY_PARAMETER_NAME = "category";

	public static final String ANSWER_ROOT_ELEMENT_NAME = "xml";
	public static final String ANSWER_BASE_ELEMENT_NAME = "answer";
	public static final String ANSWER_PARTICIPANT_ID_ATTRIBUTE_NAME = "pid";
	public static final String ANSWER_ANSWERED_YES_NO_ATTRIBUTE_NAME = "answered";
	public static final String ANSWER_REPORTED_TIME_MILLISECONDS_ATTRIBUTE_NAME = "time";
	public static final String ANSWER_WHY_NOT_ANSWERED_ELEMENT_NAME = "discard-reason";
	public static final String ANSWER_CONTENT_ELEMENT_NAME = "content";
	public static final String ANSWER_RESOURCES_ELEMENT_NAME = "resources";
	public static final String RESOURCES_LIST_SEPARATOR = ",";

	public static final String YES = "yes";
	public static final String NO = "no";

	public static final String EXCUSE = "I just couldn't cut it :(";

	public static final int DEFAULT_PORT = 11000;
	public static final Locale WORKING_LOCALE = Locale.US;
	public static final String WORKING_TIME_ZONE_ID = "UTC";
	public static final TimeZone WORKING_TIME_ZONE = TimeZone.getTimeZone(WORKING_TIME_ZONE_ID);
	public static final Charset WORKING_CHARSET = StandardCharsets.UTF_8;
	public static Patterns pat=new Patterns();

	private static final Logger logger = Logger.getLogger(TrecLiveQaDemoServer.class.getName());

	public TrecLiveQaDFKIServer(String hostname, int port) {
		super(hostname, port);
		
	}

	public TrecLiveQaDFKIServer(int port) {
		super(port);
		int threshold=1000;
		String QAfile="./data/qatrain.corpus";
		
		pat.WordCounts(QAfile,threshold);
		pat.TrainVectors(QAfile,pat.freqs_g);
		pat.CalculateDFIDF();
	}


	private static BufferedWriter getWriter(String fileLocation) throws FileNotFoundException {
		//System.out.println("Appending File "+ fileLocation);
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileLocation))));
	}

	@Override
	public Response serve(IHTTPSession session) {
		// extract get time from system
		final long getTime = System.currentTimeMillis();
		logger.info("Got request at " + getTime);

		// read question data
		Map<String, String> files = new HashMap<>();
		Method method = session.getMethod();
		if (Method.POST.equals(method)) {
			try {
				session.parseBody(files);
			} catch (IOException ioe) {
				return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT,
						"SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
			} catch (ResponseException re) {
				return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
			}
		}
		Map<String, String> params = session.getParms();
		String qid = params.get(QUESTION_ID_PARAMETER_NAME);
		String title = params.get(QUESTION_TITLE_PARAMETER_NAME);
		String body = params.get(QUESTION_BODY_PARAMETER_NAME);
		String category = params.get(QUESTION_CATEGORY_PARAMETER_NAME);
		logger.info("QID: " + qid);

		// "get answer"
		AnswerAndResources answerAndResources = null;
		try {
			answerAndResources = getAnswerAndResources(qid, title, body, category);
		} catch (Exception e) {
			logger.warning("Failed to retrieve answer and resources");
			e.printStackTrace();
			return null;
		}

		// initialize response document
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.warning("Could not build XML document");
			e.printStackTrace();
			return null;
		}
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(ANSWER_ROOT_ELEMENT_NAME);
		doc.appendChild(rootElement);
		Element answerElement = doc.createElement(ANSWER_BASE_ELEMENT_NAME);
		rootElement.appendChild(answerElement);

		// populate fields
		if (answerAndResources != null) {
			answerElement.setAttribute(ANSWER_ANSWERED_YES_NO_ATTRIBUTE_NAME, YES);
			XmlUtils.addElementWithText(doc, answerElement, ANSWER_CONTENT_ELEMENT_NAME, answerAndResources.answer());
			XmlUtils.addElementWithText(doc, answerElement, ANSWER_RESOURCES_ELEMENT_NAME,
					answerAndResources.resources());
			logger.info("Response: " + answerAndResources.answer() + "; Resources: " + answerAndResources.resources());
		} else {
			answerElement.setAttribute(ANSWER_ANSWERED_YES_NO_ATTRIBUTE_NAME, NO);
			XmlUtils.addElementWithText(doc, answerElement, ANSWER_WHY_NOT_ANSWERED_ELEMENT_NAME, EXCUSE);
			logger.info("No answer given: " + EXCUSE);
		}

		final long timeElapsed = System.currentTimeMillis() - getTime;
		answerElement.setAttribute(ANSWER_PARTICIPANT_ID_ATTRIBUTE_NAME, participantId());
		answerElement.setAttribute(ANSWER_REPORTED_TIME_MILLISECONDS_ATTRIBUTE_NAME, Long.toString(timeElapsed));
		answerElement.setAttribute(QUESTION_ID_PARAMETER_NAME, qid);
		logger.info("Internal time logged: " + timeElapsed);

		String resp = XmlUtils.writeDocumentToString(doc);
		return new Response(resp);
	}

	protected String participantId() {
		return PARTICIPANT_ID;
	}

	protected void StoringQs(String qid, String title, String body, String category)
			throws InterruptedException {
		try  {
			//BufferedWriter bw=getWriter("data/CorpusOfLiveQs.txt");
			BufferedWriter bw=new BufferedWriter(new FileWriter("data/CorpusOfLiveQs.txt",true));    	   
			bw.append('\n'+qid+'\n'+title+'\n'+body+'\n'+category+'\n');
			bw.append("\n--------  ######## --------\n");
			bw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}    	

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
	    
	/**
	 * Server's algorithmic payload.
	 * 
	 * @param qid unique question id
	 * @param title question title (roughly 10 words)
	 * @param body question body (could be empty, could be lengthy)
	 * @param category (verbal description)
	 * @return server's answer and a list of resources
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	protected AnswerAndResources getAnswerAndResources(String qid, String title, String body, String category)
			throws InterruptedException, IOException {
		if (qid != null) {
			StoringQs(qid,title,body,category);
			System.out.println(qid+'\n'+title+'\n'+body+'\n'+category);    	
		}
		
		
		// Reading Properties  
		String propertiesfile="./data/LiveQA.properties";
	    String maxtimestr=getParam("maxtime",propertiesfile);
    	String searchengine=getParam("searchengine",propertiesfile);
    	int maxtime=Integer.parseInt(maxtimestr);
		
        ClassifyQ classifyq = new ClassifyQ();
        String answer="";

        try {
                        answer=classifyq.analyzeYQsnippet(title,body,category,pat,maxtime,searchengine);
                } catch (IOException e) {
                        System.err.println("");
                }

        System.out.println(qid+'\n'+title+'\n'+body+'\n'+category);
        return new AnswerAndResources("DFKI - answer for "+title, ": "+answer+"\nresource1:\nresource2:");

	}

	protected static class AnswerAndResources {

		private String answer;
		private String resources;

		public AnswerAndResources(String iAnswer, String iResources) {
			answer = iAnswer;
			resources = iResources;
		}

		public String answer() {
			return answer;
		}

		public String resources() {
			return resources;
		}

	}

	// ---------------------------------------------




	public static void main(String[] args) throws IOException {
		TrecLiveQaDFKIServer server =
				// IP address and port
				// "localhost" or "134.96.187.212"
				// talk to it via: http://localhost:11000
				new TrecLiveQaDFKIServer(11000);
		server.start();
		System.out.println(server.getListeningPort());
		System.in.read();
		server.stop();
	}

}
