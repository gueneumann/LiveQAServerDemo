package org.trec.liveqa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcException;
import org.trec.liveqa.ParseXMLString.UrlAnswerScore;
import org.trec.liveqa.TrecLiveQaDFKIServer.AnswerAndResources;
//import org.htmlparser.sax.XMLReader;

public class ClassifyQ {

	public static TreeSet ENGLISH_QUESTION_MARKS;
	public static TreeSet GERMAN_QUESTION_MARKS;
	public static TreeSet PORTUGUESE_QUESTION_MARKS;
	public static TreeSet SPANISH_QUESTION_MARKS;

	public static TreeSet LOCATION_QUESTION_MARKS;
	public static TreeSet PERSON_QUESTION_MARKS;
	public static TreeSet DATE_QUESTION_MARKS;
	public static TreeSet NUM_QUESTION_MARKS;
	public static TreeSet TEMP_QUESTION_MARKS;
	public static TreeSet PERIOD_QUESTION_MARKS;
	
	public ClassifyQ()
	{		
		if (ENGLISH_QUESTION_MARKS==null)
		{
			ENGLISH_QUESTION_MARKS = new TreeSet();
			ENGLISH_QUESTION_MARKS.add("WHAT YEAR ");
			ENGLISH_QUESTION_MARKS.add("WHEN ");
			ENGLISH_QUESTION_MARKS.add("WHO ");
			ENGLISH_QUESTION_MARKS.add("WHOM ");
			ENGLISH_QUESTION_MARKS.add("WHERE ");
			ENGLISH_QUESTION_MARKS.add("HOW MANY ");
			ENGLISH_QUESTION_MARKS.add("HOW TALL ");
			ENGLISH_QUESTION_MARKS.add("HOW BIG ");
			ENGLISH_QUESTION_MARKS.add("HOW MUCH ");
			ENGLISH_QUESTION_MARKS.add("HOW FAST ");
			ENGLISH_QUESTION_MARKS.add("ABOUT HOW MUCH ");
			ENGLISH_QUESTION_MARKS.add("ABOUT HOW MANY ");
			ENGLISH_QUESTION_MARKS.add("ABOUT HOW FAST ");
			ENGLISH_QUESTION_MARKS.add("WHAT IS THE NUMBER ");
			ENGLISH_QUESTION_MARKS.add("WHAT IS THE AMOUNT ");
			ENGLISH_QUESTION_MARKS.add("WHAT CITY ");
			ENGLISH_QUESTION_MARKS.add("WHAT COUNTRY ");
			ENGLISH_QUESTION_MARKS.add("WHAT TOWN ");
			ENGLISH_QUESTION_MARKS.add("WHAT CONTINENT ");
			ENGLISH_QUESTION_MARKS.add("WHAT PLANET ");
			ENGLISH_QUESTION_MARKS.add("WHAT AREA ");
			ENGLISH_QUESTION_MARKS.add("WHAT REGION ");
			ENGLISH_QUESTION_MARKS.add("IN WHAT CITY ");
			ENGLISH_QUESTION_MARKS.add("IN WHAT COUNTRY ");
			ENGLISH_QUESTION_MARKS.add("IN WHAT TOWN ");
			ENGLISH_QUESTION_MARKS.add("IN WHAT CONTINENT ");
			ENGLISH_QUESTION_MARKS.add("IN WHAT PLANET ");
			ENGLISH_QUESTION_MARKS.add("IN WHAT AREA ");
			ENGLISH_QUESTION_MARKS.add("IN WHAT REGION ");
			ENGLISH_QUESTION_MARKS.add("IN WHICH CITY ");
			ENGLISH_QUESTION_MARKS.add("IN WHICH COUNTRY ");
			ENGLISH_QUESTION_MARKS.add("IN WHICH TOWN ");
			ENGLISH_QUESTION_MARKS.add("IN WHICH CONTINENT ");
			ENGLISH_QUESTION_MARKS.add("IN WHICH PLANET ");
			ENGLISH_QUESTION_MARKS.add("IN WHICH REGION ");
			ENGLISH_QUESTION_MARKS.add("IN WHICH AREA ");
			ENGLISH_QUESTION_MARKS.add("WHAT IS THE NAME OF ");
			ENGLISH_QUESTION_MARKS.add("WHAT MAN ");
			ENGLISH_QUESTION_MARKS.add("WHAT COMPANY ");
			ENGLISH_QUESTION_MARKS.add("WHAT ENTERPRISE ");
			ENGLISH_QUESTION_MARKS.add("WHAT WOMAN ");
			ENGLISH_QUESTION_MARKS.add("WHAT PERSON ");
			ENGLISH_QUESTION_MARKS.add("WHAT ORGANIZATION ");
			ENGLISH_QUESTION_MARKS.add("WHAT DAY ");
			ENGLISH_QUESTION_MARKS.add("WHAT IS THE PERIOD ");
			ENGLISH_QUESTION_MARKS.add("WHAT IS THE TIME ");
			ENGLISH_QUESTION_MARKS.add("WHAT IS THE PERIOD ");
			ENGLISH_QUESTION_MARKS.add("WHAT IS THE PERIOD ");
			ENGLISH_QUESTION_MARKS.add("HOW LONG ");
			ENGLISH_QUESTION_MARKS.add("HOW OLD ");
			ENGLISH_QUESTION_MARKS.add("HOW HOT ");
			
		}
		if (LOCATION_QUESTION_MARKS==null)
		{
			LOCATION_QUESTION_MARKS = new TreeSet();
			LOCATION_QUESTION_MARKS.add("WHERE ");
			LOCATION_QUESTION_MARKS.add("WHAT CITY ");
			LOCATION_QUESTION_MARKS.add("WHAT COUNTRY ");
			LOCATION_QUESTION_MARKS.add("WHAT TOWN ");
			LOCATION_QUESTION_MARKS.add("WHAT CONTINENT ");
			LOCATION_QUESTION_MARKS.add("WHAT PLANET ");
			LOCATION_QUESTION_MARKS.add("WHAT AREA ");
			LOCATION_QUESTION_MARKS.add("WHAT REGION ");
			LOCATION_QUESTION_MARKS.add("IN WHAT CITY ");
			LOCATION_QUESTION_MARKS.add("IN WHAT COUNTRY ");
			LOCATION_QUESTION_MARKS.add("IN WHAT TOWN ");
			LOCATION_QUESTION_MARKS.add("IN WHAT CONTINENT ");
			LOCATION_QUESTION_MARKS.add("IN WHAT PLANET ");
			LOCATION_QUESTION_MARKS.add("IN WHAT AREA ");
			LOCATION_QUESTION_MARKS.add("IN WHAT REGION ");
			LOCATION_QUESTION_MARKS.add("IN WHICH CITY ");
			LOCATION_QUESTION_MARKS.add("IN WHICH COUNTRY ");
			LOCATION_QUESTION_MARKS.add("IN WHICH TOWN ");
			LOCATION_QUESTION_MARKS.add("IN WHICH CONTINENT ");
			LOCATION_QUESTION_MARKS.add("IN WHICH PLANET ");
			LOCATION_QUESTION_MARKS.add("IN WHICH REGION ");
			LOCATION_QUESTION_MARKS.add("IN WHICH AREA ");
			
		}
		if (PERSON_QUESTION_MARKS==null)
		{
			PERSON_QUESTION_MARKS = new TreeSet();
			PERSON_QUESTION_MARKS.add("WHO ");
			PERSON_QUESTION_MARKS.add("WHOM ");
			PERSON_QUESTION_MARKS.add("WHAT IS THE NAME OF ");
			PERSON_QUESTION_MARKS.add("WHAT MAN ");
			PERSON_QUESTION_MARKS.add("WHAT COMPANY ");
			PERSON_QUESTION_MARKS.add("WHAT ENTERPRISE ");
			PERSON_QUESTION_MARKS.add("WHAT WOMAN ");
			PERSON_QUESTION_MARKS.add("WHAT PERSON ");
			PERSON_QUESTION_MARKS.add("WHAT ORGANIZATION ");
		}
		if (DATE_QUESTION_MARKS==null)
		{
			DATE_QUESTION_MARKS = new TreeSet();
			DATE_QUESTION_MARKS.add("WHEN ");
			DATE_QUESTION_MARKS.add("WHAT YEAR ");
			DATE_QUESTION_MARKS.add("WHAT DAY ");
		}
		if (NUM_QUESTION_MARKS==null)
		{
			NUM_QUESTION_MARKS = new TreeSet();
			NUM_QUESTION_MARKS.add("HOW MANY ");
			NUM_QUESTION_MARKS.add("HOW TALL ");
			NUM_QUESTION_MARKS.add("HOW BIG ");
			NUM_QUESTION_MARKS.add("HOW MUCH ");
			NUM_QUESTION_MARKS.add("HOW FAST ");
			NUM_QUESTION_MARKS.add("ABOUT HOW MUCH ");
			NUM_QUESTION_MARKS.add("ABOUT HOW MANY ");
			NUM_QUESTION_MARKS.add("ABOUT HOW FAST ");
			NUM_QUESTION_MARKS.add("WHAT IS THE NUMBER ");
			NUM_QUESTION_MARKS.add("WHAT IS THE AMOUNT ");
			
		}
		if (TEMP_QUESTION_MARKS==null)			
		{
			TEMP_QUESTION_MARKS = new TreeSet();
			TEMP_QUESTION_MARKS.add("HOW HOT ");
		}
		
		if (PERIOD_QUESTION_MARKS==null)			
		{
			PERIOD_QUESTION_MARKS = new TreeSet();
			PERIOD_QUESTION_MARKS.add("WHAT IS THE PERIOD ");
			PERIOD_QUESTION_MARKS.add("WHAT IS THE TIME ");
			PERIOD_QUESTION_MARKS.add("WHAT IS THE PERIOD ");
			PERIOD_QUESTION_MARKS.add("WHAT IS THE PERIOD ");
			PERIOD_QUESTION_MARKS.add("HOW LONG ");
			PERIOD_QUESTION_MARKS.add("HOW OLD ");
		}
	}
	
	
	
	public ArrayList findoutkeyQ(String paragraph)
	{

		ArrayList<String> returnstr = new ArrayList<>();
		ArrayList<String> str = new ArrayList<>();
		ArrayList<String> buffer = new ArrayList<>();
		String sentence,previous_sentence="";
		Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(paragraph);

		//Pattern legitimateq = Pattern.compile("^[\\s]*([Aa][Mm]|[Aa][Rr][eE]|[Ii][Ss]|[Cc][Aa][Nn]|[Ww][Hh][eE][Nn]|[Ww][Hh][aA][Tt]|[Hh][Oo][Ww]|[Ww][Hh][Oo])[\\s]*",Pattern.MULTILINE);
		Pattern legitimateq = Pattern.compile("^[\\s]*(AM|ARE|CAN|SHOULD|WHAT|WHO|HOW|WHEN|SHALL|WILL|COULD|WERE)[\\s]*",Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);

		while (reMatcher.find()) {
			sentence=reMatcher.group();
			//System.out.println("sentence:"+sentence);
			
			// Remove paranthesis in the text for WebQA Client
			sentence.replace("(","");
			sentence.replace(")","");
			
			str.clear();
			if ( Pattern.matches("(.*(\\s|^)it\\s.*)|(.*(\\s|^)that\\s.*)|(.*(\\s|^)this\\s.*)|(.*(\\s|^)their\\s.*)|(.*(\\s|^)these\\s.*)|(.*(\\s|^)him\\s.*)|(.*(\\s|^)her\\s.*)|(.*(\\s|^)[Hh][Ee][Ll][Pp].*)|(.*(\\s|^)[Pp][Ll][Ee][Aa][Ss][Ee].*)|(.*\\?\\s*$)",sentence ))
			{
				if ( previous_sentence != "")
					buffer.add(previous_sentence);
			}
			
			else
				buffer.clear();

			if (sentence.matches("(.*\\?\\s*$)|(.*\\s*[Hh][Ee][Ll][Pp].*)"))
			{
				if(buffer.size() != 0)
					str.addAll(buffer);
				str.add(sentence);

			}


			if ( str.size() != 0)
			{
				previous_sentence=""; // don't store previous sentence if already marked as important
				buffer.clear();
				returnstr.addAll(str);
				
			}
			else
				previous_sentence=sentence;

		}


		return returnstr;

	}
	
	public int analyzeQuery(String nlQuery) 
	{
		//int detectedLanguage       = 0;
		//int detectedExpectedAnswer = 0;
		String nlQueryTmp = nlQuery.toUpperCase();
		
		
		/* in case of factoid questions */
		//if (contains(LOCATION_QUESTION_MARKS,nlQueryTmp) || contains(PERSON_QUESTION_MARKS,nlQueryTmp) || contains(DATE_QUESTION_MARKS,nlQueryTmp) || contains(NUM_QUESTION_MARKS,nlQueryTmp) || contains(TEMP_QUESTION_MARKS,nlQueryTmp) || contains(PERIOD_QUESTION_MARKS,nlQueryTmp) )
		if ( true)
		     return 1;		
		else
			return 0;
			
	}	
	
	private boolean contains(TreeSet words, String query)
	{
		final Iterator iterador = words.iterator();		
		boolean flag = false;
		
		while (iterador.hasNext())
		{
			String word = iterador.next().toString();
			if (query.indexOf(word,0) > -1)
			{
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
    private static BufferedWriter getWriter(String fileLocation) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileLocation),true)));
    }
    
    public String weedout(String keysentence)
    {
    	String newsentence="";
    	
    	
    	newsentence=keysentence.replace("&#39;","").replace(":&lt;br", "").replace("&#xa;","").replace("&amp;","").replace("&[lg]t;","");
    	
    	   	
    	return newsentence;
    	
    }
	
	public AnswerAndResources analyzeYQsnippet(String title,String body,String categ,Patterns pat,int maxtime,String searchengine) throws  IOException
	{
		List<String> scope_body=new ArrayList<>();
		List<String> scope_title=new ArrayList<>();
		List<String> scope_categ=new ArrayList<>();
		WebQAClient webqaclient = new WebQAClient();
		
		//Map<String,String> answerunit=new HashMap();
		ParseXMLString parser = new ParseXMLString();
		String answer = "",answerunit="";
		UrlAnswerScore urlanswerscore=parser.new UrlAnswerScore("","",0.0);
		
		
		
		boolean webqaflag = false;
		String webqalogfile = "data/WebQAClient.log";
		String qalogfile = "data/QA.log";
		String webqaxml = "data/webqa.xml";
		
		
		BufferedWriter replyxml = getWriter(webqaxml);
		BufferedWriter webqalog = getWriter(webqalogfile);
		BufferedWriter qalog = getWriter(qalogfile);
		
		
		long start =  System.currentTimeMillis();
		float elapsedTimeSec=0;

		
	
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		
        // Reduce the scope of the question to key context
		
		
		if ( body != null)
			scope_body=findoutkeyQ(body);
		
		if ( title != null)
			scope_title=findoutkeyQ(title);
		
		if ( categ!=null )
			scope_categ=findoutkeyQ(categ);

		
		qalog.append(new Date()+"\ntitle:"+title + "\n"+ "body:"+body+"\ncateg:"+categ+"\n");
		qalog.flush();
		
		
		String keysentence="";
		 for (String sent:scope_title)
			 keysentence += sent;
		 
		 for (String sent:scope_body)
			 keysentence += sent;
		 
		 String xmlstring="";
		 
		 //When title and body are null , check and add categ as keysentence
		 if  ( keysentence.equals(""))
		 {
			 
		       for ( String sent:scope_categ)
				    keysentence += sent;
		 }
		 
		 // if keysentence is "" then don't call others
		 if ( keysentence.equals(""))
		 {
			 return ( new AnswerAndResources("","") );
		 }
		 
		 keysentence=weedout(keysentence);
		 
		 keysentence = keysentence +" "+searchengine;
		 //System.out.println("keyQ="+keysentence);
		 
		 
		 // Try 1 to webqa
		 try {
			 //System.out.println("Calling webqaclient 1");
			 xmlstring=webqaclient.answers(keysentence);
			 //System.out.println("Called webqa client 1");
			 
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			webqalog.append("\n---------\nError in WebQA for:\n"+keysentence);
			webqalog.append("\nException is"+e+"\n");
			webqalog.flush();
			e.printStackTrace();
		}
		 
		 
		 // Try 2 to webqa
		 // if try 1 retrieves null result - space out all the "[.?]"  a little bit
		 
		 if ( xmlstring == "")
		 {
			  keysentence=keysentence.replaceAll("\\."," . ").replaceAll("\\?"," ? ");
		      System.out.println("Trying again webqa call with :"+keysentence);
		      try {
					 xmlstring=webqaclient.answers(keysentence);
				} catch (XmlRpcException e) {
					// TODO Auto-generated catch block
					webqalog.append("\n---------\nError in WebQA for:\n"+keysentence);
					webqalog.append("\nException is"+e+"\n");
					e.printStackTrace();
				}
		    	
		 }
		 
		 
		 // Writing the xml down

		 replyxml.write(xmlstring);
		 replyxml.flush();
		
		 
		 System.out.println("Calling retrieve_from_urls ..");
		 // Retrieve the answer
		 try {
		  urlanswerscore=parser.retrieve_from_urls(keysentence,xmlstring,pat,maxtime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			webqalog.append("\n---------\nError while retrieveing the answer : retrieve_from_urls\n");
			webqalog.append("\nException is"+e+"\n");
			webqalog.flush();
			e.printStackTrace();
		}

		 long elapsedTimeMillis =  System.currentTimeMillis()- start;
		 elapsedTimeSec = elapsedTimeMillis/1000F;

		qalog.append("Answer :" + urlanswerscore.answer + "\n");
		qalog.append("Resource :" + urlanswerscore.url + "\n");
		qalog.append("Score :" + urlanswerscore.score + "\n");
		qalog.append(new Date()+" -Time taken:"+elapsedTimeSec+"\n\n");
		qalog.flush();
	
		
		webqalog.flush();
		webqalog.close();
		qalog.flush();
		qalog.close();
		replyxml.close();
		return (new AnswerAndResources(urlanswerscore.answer,urlanswerscore.url));
	}

}