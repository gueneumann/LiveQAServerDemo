package org.trec.liveqa;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.w3c.dom.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseXMLString  {



	public static ArrayList<String> textFromUrl(String urlstring)  {

		Map<String, String> res = new HashMap<>();
		String urlcontent="";
		ArrayList<String> urltextcontent=new ArrayList<String>();
        boolean readable=true;
        String urloriginal=urlstring;
        
		/*
		if ( ! urlstring.contains("http"))
		{
			urlstring="http://"+urlstring.replace("^\"", "").replace("\"$", "");
		}
		*/
		

		//Testing delete later
		//System.out.println(urlstring);
		long start =  System.currentTimeMillis();
		float elapsedTimeSec=0;
        String getprotocol="";
        String getFile="";
        String getHost="";
        String getAuthority="";
		String getPath="";
		String getQuery="";
		String getUserInfo="";

		try
		{	


		
			//Add http if its not present.
			if ( ! urlstring.matches("^http.*"))
			{

            /*	if ( ! urlstring.matches("^www.*") )
				{
					urlstring ="www." + urlstring;
					System.out.println("Added www.:"+urlstring);
				}
             */
				urlstring="http://"+urlstring;
				
			}


		
			
			URL url = new URL(urlstring);
			
			getprotocol=url.getProtocol();
			getFile=url.getFile();
			getHost=url.getHost();
			getAuthority=url.getAuthority();
			getPath=url.getPath();
			getQuery=url.getQuery();
			getUserInfo=url.getUserInfo();
		
		   //System.out.println( urlstring+"\n----1----\n"+getprotocol+"\n"+getFile+"\n"+getHost+"\n"+getAuthority+"\n"+getPath+"\n"+getQuery+"\n"+getUserInfo+"\n------");
				
			  
			URLConnection con = url.openConnection();  
			InputStream is =con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;

			// read each line and write to System.out
			while ((line = br.readLine()) != null) {
				urlcontent+=line;
			}

		}

		catch(IOException ie)
		{
			//System.out.println("With Protocol:"+getprotocol+" .Excpetion while connecting to :"+urlstring);
			//ie.printStackTrace();
			
			System.out.println("Cannot retrieve 1:"+urlstring);

			// Try with https
			try
			{	
				if ( ! urloriginal.matches("^http.*"))
						urlstring="https://"+urloriginal;
				else
					urlstring.replace("^http://", "https://");
				     

				
				//System.out.println("changed url -" + urlstring);   open this line later
				URL url = new URL(urlstring);
				getprotocol=url.getProtocol();
				getFile=url.getFile();
				getHost=url.getHost();
				getAuthority=url.getAuthority();
				getPath=url.getPath();
				getQuery=url.getQuery();
				getUserInfo=url.getUserInfo();
			
			    //System.out.println( urloriginal+"\n"+urlstring+"\n-----2-----\n"+getprotocol+"\n"+getFile+"\n"+getHost+"\n"+getAuthority+"\n"+getPath+"\n"+getQuery+"\n"+getUserInfo+"\n------");
			
				
				URLConnection con = url.openConnection();  
				InputStream is =con.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				String line = null;
				// read each line and write to System.out
				while ((line = br.readLine()) != null) {
					urlcontent+=line;
				}
			}

			catch(IOException ie2)
			{
				//ie2.printStackTrace();
                readable=false;
                
                System.out.println("Cannot retrieve 2:"+urlstring);
			}

		}




		//String page = IOUtils.toString(url.openConnection().getInputStream());
		long elapsedTimeMillis =  System.currentTimeMillis()- start;
		elapsedTimeSec = elapsedTimeMillis/1000F;

		
		String htmlcontent=Jsoup.parse(urlcontent).text();

		String text="";

		Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(htmlcontent);

		while (reMatcher.find()) {
			text=reMatcher.group();

			// If the text is too long don't bother checking the score.adding the text
			if ( text.length() > 1000)
				continue;

			urltextcontent.add(text);			
		}

		//for(String s:urltextcontent)
		//    System.out.println(s);

		//elapsedTimeMillis =  System.currentTimeMillis()- start;
		//float elapsedTimeSec_2 = elapsedTimeMillis/1000F;
		//System.out.println("reading:"+elapsedTimeSec+",parsing:"+elapsedTimeSec_2+" (seconds) for reading "+urltextcontent.size()+" lines in " + urlstring);
	
		if ( readable)
			System.out.println("Read url:"+urloriginal);


		return urltextcontent;
	}

	public static Map<Integer,Double> bestScoreIsland(ArrayList<Double> scores,int maxlines)
	{
		Map<Integer,Double> retvalue=new HashMap();
		Integer[] scoreIsland=null;
		int bestpos=0;
		Double maxscore=0.0;
		for ( int i=0;i<=scores.size()-maxlines;i++)
		{
			Double score=0.0;

			for(int y=0;y<maxlines;y++)
				score += scores.get(i+y);

			if  ( maxscore <= score)
			{
				maxscore=score;
				bestpos=i;
			}

		}
		//scoreIsland=Arrays.copyOfRange(scores, bestpos, bestpos+maxlines-1);

		retvalue.put(bestpos, maxscore);
		return retvalue;
	}
	// Sample retreival
	public String retrieve_first(String xmlRecords) {


		xmlRecords=xmlRecords.replaceAll(">\\s*\n\\s*<", "><");
		String firstsnippet="";
		String secondsnippet="";

		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);

			NodeList nodes = doc.getElementsByTagName("qa-object");

			Element element = (Element) nodes.item(0);
			NodeList snippetList = element.getElementsByTagName("snippets");

			for (int i = 0; i < snippetList.getLength(); i++) {
				Element movieElement = (Element) snippetList.item(i);
				NodeList snippet = movieElement.getElementsByTagName("snippet");

				firstsnippet=snippet.item(0).getFirstChild().getTextContent();
				secondsnippet=snippet.item(1).getChildNodes().item(2).getTextContent();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}


		return secondsnippet;
	}


	
	
	public UrlAnswerScore retrieve_from_urls(String Question,String xmlRecords,Patterns pat,int maxtime) throws Exception
	{
		//TreeMap <String,String> url_answers=new TreeMap<String,String>();
		ArrayList<UrlAnswerScore> url_answers=new ArrayList<UrlAnswerScore>();
		
		// To retrieve top ten search engine results
		url_answers=mineURLs(Question,retrieve_urls(xmlRecords),pat,maxtime);
		
		//To retrieve top ten webqa suggested results
		//url_answers=mineURLs(Question,retrieve_semantic_urls(xmlRecords),pat,maxtime);
		
		String answer="";
		UrlAnswerScore urlanswerscore = new UrlAnswerScore("","",0.0);
		
		int i=1;

		//System.out.println("\nFINALLY:");
		for( UrlAnswerScore obj:url_answers)
		{
			//System.out.println(":\nURL:"+obj.url+"\nANSWER:"+obj.answer+"\nSCORE:"+obj.score);


			if ( i==1)
			{
				urlanswerscore=obj;
				answer=obj.answer;
			}
			i++;

		}


		return urlanswerscore;
	}


	/**
	 * @author Tyler Klement
	 */
	public ArrayList<String> retrieve_semantic_urls(String xmlRecords) {
		ArrayList<String> urls = new ArrayList<String>();
		try {
			SAXBuilder builder = new SAXBuilder();
			// qualified names are necessary since DOM, used in other areas, shares these names
			org.jdom2.Document document = (org.jdom2.Document) builder.build(new StringReader(xmlRecords));
			org.jdom2.Element rootNode = document.getRootElement();
			org.jdom2.Element sentenceNode = rootNode.getChild("sentences");
			List sentenceList = sentenceNode.getChildren("sentence");
			org.jdom2.Element snippetsNode = rootNode.getChild("snippets");
			List snippetsList = snippetsNode.getChildren("snippet");
			
			ArrayList<String> snippetUrls = new ArrayList<String>();
			
			for(int i = 0; i < snippetsList.size(); i++){
				org.jdom2.Element snippetNode = (org.jdom2.Element) snippetsList.get(i);
				String url = snippetNode.getChildText("url");
				snippetUrls.add(url);
			}
			
			//gets top 10 best snippets and adds their urls to the urls list, in order
			int sentencesRetrieved = 0;
			for(int i = sentenceList.size() - 1; i >= 0 && sentencesRetrieved < 10; i--){
				org.jdom2.Element sentence = (org.jdom2.Element) sentenceList.get(i);
				org.jdom2.Element sourcesNode = sentence.getChild("sources");
				List sourceList = sourcesNode.getChildren("source");
				
				for(int j = 0; j < sourceList.size(); j++){
					org.jdom2.Element sourceNode = (org.jdom2.Element) sourceList.get(j);
					urls.add(snippetUrls.get(Integer.parseInt(sourceNode.getText())));
				}
				
				sentencesRetrieved++;
			}
			
		} catch (IOException | JDOMException e) {
			e.printStackTrace();
		}
		
		return urls;
	}
	

	//@SuppressWarnings("deprecation")
	public ArrayList<UrlAnswerScore> mineURLs(String Question,ArrayList<String> urls,Patterns pat,int maxtime) throws Exception 
	{

		Map<String,Double> answertfidfs=pat.ProbableAnswerPatterns(Question, pat.freqs_g);


		String Answer="";


		ArrayList<String> Lines=new ArrayList<String>();
		HashMap<String,Double> url_scores=new HashMap < String,Double>();
		Map<String,Double> orderedscores=new TreeMap < String,Double>();
		HashMap<String,String> url_answers=new HashMap < String,String>();
		ArrayList<UrlAnswerScore> urlanswerscores=new ArrayList<UrlAnswerScore>();
		ArrayList<BestAnswerFromURL> allbestans=new ArrayList<BestAnswerFromURL>();

		for (String url: urls)
		{


			// Creating new thread for Answer From this url
			System.out.println("Creating new thread for url:"+url+" maxtime:"+maxtime);
			BestAnswerFromURL thread=new BestAnswerFromURL(Question,url,pat,answertfidfs);
			allbestans.add(thread);
			//Delete below 3
			//Scanner sn=new Scanner(System.in);
			//if ( sn.nextLine() == "#" )
				// break;
            
		}

		// Wait for some time and take the best answers + kill the threads.
		long start =  System.currentTimeMillis();
		float elapsedTimeSec=0;

		while(elapsedTimeSec <= maxtime)    // Choose appropriate SECONDS - IMPORTANT
		{
			long elapsedTimeMillis =  System.currentTimeMillis()- start;
			elapsedTimeSec = elapsedTimeMillis/1000F;
			

		}

		for(BestAnswerFromURL bestie:allbestans)
		{


			url_answers.put(bestie.url, bestie.bestanswer);
			url_scores.put(bestie.url,bestie.score);

		}

		for(BestAnswerFromURL bestie:allbestans)
		{

			if ( bestie.isAlive())
			{
				System.out.println("new Date() -Stopping the search in url:"+bestie.url);
				bestie.interrupt();//stop();  // Kill the thread?
				System.out.println("bestie status - "+bestie.isAlive());
			}

		}

/*
		System.out.println("URL SCORES populating?:");		
		for(Map.Entry urlscore:url_scores.entrySet())
		{
			String key=(String) urlscore.getKey();
			Double score=(Double) urlscore.getValue();
			System.out.println("url-"+key+"\nAnswer:"+url_answers.get(key)+"\nScore:"+score);

		}

*/


		orderedscores=pat.SortByValue(url_scores);


		Iterator it=orderedscores.entrySet().iterator();

		
		while( it.hasNext())
		{

			Map.Entry me = (Map.Entry)it.next();
			String url=(String)me.getKey();
			Double score=(Double) me.getValue();
			urlanswerscores.add(new UrlAnswerScore(url,(String) url_answers.get(url),score));
			/*
              if ( x !=0)
	    		{
            	  System.out.println(me.getKey()+" -- "+Questions_g.get(Integer.parseInt((String)me.getKey())));
		    		  Scanner sn=new Scanner(System.in);
	    		  x=sn.nextInt();
	    		}*/
		}

		return urlanswerscores;
	}

	public ArrayList<String> retrieve_urls(String xmlRecords) {


		//System.out.println(xmlRecords);
		xmlRecords=xmlRecords.replaceAll(">\\s*\n\\s*<", "><");
		ArrayList<String> urls = new ArrayList<String>();
		String text="";
		

		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);

			// NodeList nodes = doc.getElementsByTagName("qa-object");
			// Element element = (Element) nodes.item(0);
			//NodeList urlList = element.getElementsByTagName("url");
			
			NodeList urlList = doc.getElementsByTagName("url");


		//	for (int i = 0; i < urlList.getLength() ; i++) {
			for (int i = 0; i < urlList.getLength() && i < 10 ; i++) {
				text=urlList.item(i).getTextContent().replaceAll("\"","");
				// To ignore website http://trec.nist.gov
				if ( text.contains("http://trec.nist.gov"))
					continue;
				// To ignore website http://trec.nist.gov
				
				System.out.println("Adding url "+i+":"+text);
				urls.add(text);

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}


		return urls;
	}


	
	
	
	
	public String retrieve_bestsnip(String Question,String xmlRecords,Patterns pat) throws IOException {



		xmlRecords=xmlRecords.replaceAll(">\\s*\n\\s*<", "><");
		Map<String,String> urlnsnippets=new HashMap();
		Map<String,Double> answertfidfs=pat.ProbableAnswerPatterns(Question, pat.freqs_g);
		String text="",answertext="";

		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);

			NodeList nodes = doc.getElementsByTagName("qa-object");

			Element element = (Element) nodes.item(0);
			NodeList snippetList = element.getElementsByTagName("snippets");
			Element movieElement = (Element) snippetList.item(0);
			NodeList snippets = movieElement.getElementsByTagName("snippet");
			double  max=0.0,answerscore;

			for (int i = 0; i < snippets.getLength(); i++) {

				text=snippets.item(i).getFirstChild().getTextContent();
				answerscore=pat.AnswerScore(Question,text, pat.freqs_g, answertfidfs);

				if (  answerscore <= max )
				{
					max = answerscore;
					answertext=text;
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}


		return answertext;
	}


	public String retrieve_best(String xmlRecords) {

		xmlRecords=xmlRecords.replaceAll(">\\s*\n\\s*<", "><");
		String best_answer="";
		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);

			NodeList nodes = doc.getElementsByTagName("qa-object");

			Element element = (Element) nodes.item(0);
			//answer candidates
			NodeList answersList = element.getElementsByTagName("answers-candidates");
			Element elmanscand=(Element) answersList.item(0);

			//predicted answers
			NodeList predictedList = element.getElementsByTagName("predicted-answers");
			Element elmpredans=(Element) predictedList.item(0);

			//predicted answer
			NodeList predictedanswers=elmpredans.getElementsByTagName("predicted-answer");

			//sentences
			NodeList sentences=element.getElementsByTagName("sentences");
			Element elmsentences=(Element) sentences.item(0);

			//sentence
			NodeList sentence=element.getElementsByTagName("sentence");


			System.out.println("answers-candidates length = "+elmanscand.getAttribute("amount")+"\n");

			if ( ! elmanscand.getAttribute("amount").equals("0") )
			{   

				Element movieElement = (Element) answersList.item(0);
				NodeList answercandidate = movieElement.getElementsByTagName("answer-candidate");
				Node requiredanswer=answercandidate.item(0); // Get the first answer - check if it can be changed later.
				Element elm_requiredanswer=(Element) requiredanswer;

				NodeList answersources = elm_requiredanswer.getElementsByTagName("source");
				for ( int i=0;i<answersources.getLength();i++)
				{
					//first_answercandidate=answercandidate.item(0).getFirstChild().getTextContent();
					int predsource=Integer.parseInt(answersources.item(i).getTextContent());
					Element elm_predictedanswer = (Element) predictedanswers.item(predsource);

					NodeList predans_sources = elm_predictedanswer.getElementsByTagName("source");
					float maxsim=0;
					int ans_sentence=0;

					System.out.println("Predicted rank - "+ elm_predictedanswer.getAttribute("rank-value")+"\n");

					if ( maxsim <= Float.parseFloat(elm_predictedanswer.getAttribute("rank-value")) )
					{
						maxsim=Float.parseFloat(elm_predictedanswer.getAttribute("rank-value"));


						for ( int j=0;j<predans_sources.getLength();j++)
						{
							int sentenceid=Integer.parseInt(predans_sources.item(j).getTextContent());
							Element elm_predans=(Element) predans_sources.item(j);
							System.out.println("sentence id="+sentenceid+"\n");

							best_answer = sentence.item(sentenceid).getTextContent();
							ans_sentence=sentenceid;
						}
					}

					//second_answercandidate=answercandidate.item(0).getChildNodes().item(2).getTextContent();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		;
		return best_answer;
	}


	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}


	private static BufferedWriter getWriter(String fileLocation) throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileLocation))));
	}

	public static void main ( String[] args) throws Exception
	{

		String file = args[0];
		String filecontent="";
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		List<String> qids = new ArrayList<>();


		//Define new Patterns object and do the training
		Patterns pat=new Patterns();
		System.out.println(new Date().toString());
		String QAfile="./data/qapattern.train";
		int threshold=300; // hard coded threshold 
		pat.WordCounts(QAfile,threshold);
		pat.TrainVectors(QAfile,pat.freqs_g);
        pat.FetchWord2Vecs("./data/Word2Vectors");
		pat.CalculateDFIDF();

		
		ParseXMLString parser=new ParseXMLString();

		String l = reader.readLine();
		System.out.println("Reading file -"+file);
		String Question="",Answer="";
		while (l != null) {

			filecontent += l;
			System.out.println(l);
			if ( l.equals("</qa-object>") )
				break;
			if ( l.matches(".*<nl-query>.*"))	
				Question=l.replaceAll("\\s*<nl-query>([^<]*)\\s*</nl-query>", "$1");
			l = reader.readLine();

		}

		reader.close();

		System.out.println(new Date()+"Retrievingurls for Question -"+Question);
		UrlAnswerScore urlanswerscore = parser.retrieve_from_urls(Question,filecontent,pat,30);
		//Answer=parser.retrieve_bestsnip(Question,filecontent,pat);
		System.out.println(urlanswerscore.answer);
		
	}

	public class UrlAnswerScore
	{
		public String url;
		public String answer;
		public Double score;

		public UrlAnswerScore(String u,String a,Double s)
		{
			this.url=u;
			this.answer=a;
			this.score=s;
		}
	}

	private class BestAnswerFromURL extends Thread
	{

		public String Question,url,bestanswer="";
		public Double score=0.0;
		public Patterns pat=new Patterns();
		public Map<String,Double> answertfidfs=new HashMap();
		private Thread runner;



		public BestAnswerFromURL(String Question,String url,Patterns pat,Map<String,Double> answertfidfs)
		{
			this.Question=Question;
			this.pat=pat;
			this.answertfidfs=answertfidfs;
			this.url=url;
			runner = new Thread(this);	 // Start Thread when object is initialized
			runner.start();

		}

		public void run() 
		{

			ArrayList<String> Lines=new ArrayList<String>();;

			Lines = textFromUrl(url);
			
			
			//System.out.println("Lines in url-"+url+" = "+Lines.size());

			ArrayList<Double> AnswerScores= new ArrayList<Double>();

			for (String line:Lines)
			{

				line=line.replaceAll("[^a-zA-Z \\.]", "").toLowerCase();
				double answerscore=0.0;

				try {
				
					//lowercase the Answer line from URLs
					line=line.toLowerCase();
					answerscore = pat.AnswerScore(Question,line, pat.freqs_g, answertfidfs);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("IOException at pat.AnswerScore");
				//	e.printStackTrace();
				}

				//Debug line
				//System.out.println("answerscore="+answerscore);

				AnswerScores.add(answerscore);

			}

			// Initialize local bestanswer and score values

			Integer islandpos=0;
			int maxlines=3;   // Plays key role


			Map<Integer,Double> bestisl=bestScoreIsland(AnswerScores,maxlines);

			for(Map.Entry<Integer,Double> obj:bestisl.entrySet())
			{
				islandpos=obj.getKey();
				score=obj.getValue();
			}

			for(int j=islandpos;j<islandpos+maxlines;j++)
			{
				try{
				bestanswer += Lines.get(j);
				}
				catch(Exception e)
				{
					//System.out.println("Lines.size="+Lines.size()+"\n j="+j+"maxlines -"+maxlines);
				}
			}
	 //  System.out.println(new Date()+"Thread ending..\nURL :"+url+"\nAnswer:"+bestanswer+"\nScore:"+score);

		}

	}

}