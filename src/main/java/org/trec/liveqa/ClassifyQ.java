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
	
	public String analyzeYQsnippet(String title,String body,String categ,Patterns pat,int maxtime,String searchengine) throws  IOException
	{
		List<String> scope_body=new ArrayList<>();
		List<String> scope_title=new ArrayList<>();
		WebQAClient webqaclient = new WebQAClient();
		//Map<String,String> answerunit=new HashMap();
		ParseXMLString parser = new ParseXMLString();
		String answer = "",answerunit="";
		
		boolean webqaflag = false;
		String webqalogfile = "data/WebQAClient.log";
		String qalogfile = "data/QA.log";
		String towebqafile = "data/TowebQA.txt";
		String nottowebqafile = "data/notTowebQA.txt";
		
		
		BufferedWriter replyxml = getWriter("webqa.xml");
		BufferedWriter webqalog = getWriter(webqalogfile);
		BufferedWriter qalog = getWriter(qalogfile);
		BufferedWriter towebqa = getWriter(towebqafile);
		BufferedWriter nottowebqa = getWriter(nottowebqafile);
		
		/* To be removed */
		int webqacallcount=0;
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		
        // Reduce the scope of the question to key context
		
		if ( body != null)
			scope_body=findoutkeyQ(body);
		
		if ( title != null)
			scope_title=findoutkeyQ(title);

		//System.out.println("Scope_body - " + scope_body.toString()+","+scope_body.size());
		//System.out.println("Scope title - " + scope_title.toString());


		
		
		for ( String keysentence:scope_title)
		{
			if ( analyzeQuery(keysentence) == 1 )
			{
				webqaflag=true;
				webqalog.append("To WebQA(title): "+sdf.format(date)+":"+keysentence+"\n");
				
				
				
				try {
				
					webqacallcount++;
					
					
					keysentence=keysentence.replaceAll("[^a-zA-Z0-9\\s]"," ");
					
					// Below line is to search in the giga search open source api
					keysentence = keysentence + " "+searchengine;
					
					if ( keysentence.length()>150)
					{
					
					String xmlstring=webqaclient.answers(keysentence.substring(0,150));
				
					replyxml.write(xmlstring);
					replyxml.flush();
					
					parser.retrieve_from_urls(keysentence.substring(0,150),xmlstring,pat,maxtime);
					
					

				
					}
					else if ( keysentence !="" )
					{
			        String xmlstring=webqaclient.answers(keysentence);
					replyxml.write(xmlstring);
					replyxml.flush();
			        answerunit=parser.retrieve_from_urls(keysentence,xmlstring,pat,maxtime);

							
					}
				} 
				//catch (XmlRpcException e) {		
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("error in webqa - "+keysentence+"\n");
					//e.printStackTrace();
				}				
				//webqalog.append("Predicted Answer:"+answerunit+"\n");
				webqalog.flush();
				answer=answer + answerunit;
			}
		}
		

		for ( String keysentence:scope_body)
		{

			if ( analyzeQuery(keysentence) == 1 )
			{
				webqaflag = true;
				webqalog.append("To WebQA (body):"+sdf.format(date)+": "+keysentence+"\n");
				//System.out.println("To WebQA 2:"+keysentence+"\n");
				try {
					
					
					webqacallcount++;
					
					
					keysentence=keysentence.replaceAll("[^a-zA-Z0-9\\s]"," ");
					
					// Below line is to search in the giga search open source api
					keysentence = keysentence + " "+searchengine;
					
					
					if ( keysentence.length() > 150)
					{
					
						String xmlstring=webqaclient.answers(keysentence.substring(0,150));
						replyxml.write(xmlstring);
						replyxml.flush();
						answerunit=parser.retrieve_from_urls(keysentence.substring(0,150),xmlstring,pat,maxtime);

					}
					
					else if ( keysentence !="" )
					{

						String xmlstring=webqaclient.answers(keysentence);
						replyxml.write(xmlstring);
						replyxml.flush();
						answerunit=parser.retrieve_from_urls(keysentence,xmlstring,pat,maxtime);
							
					}
					
					}// catch (XmlRpcException e) {
				   catch (Exception e) {	
					   
					// TODO Auto-generated catch block
					
					System.out.println("error in webqa - "+keysentence+"\n length of the question -"+keysentence.length()+"\n");
					//e.printStackTrace();
			
				}
				//webqalog.append("Predicted Answer:"+answerunit+"\n");
				webqalog.flush();
				
			}
		}
		
		/* 3 lines to be removed later*/
		if ( webqaflag == true)
		{
			webqalog.append("----------------------\n");
			towebqa.append("title:"+title+"\nscopetitle:"+scope_title + "\n"+ "body:"+body+"\nscope body:"+scope_body+"\n");
		    towebqa.flush();
		    towebqa.close();
		    
		}
		else
			nottowebqa.append("title:"+title+"\nbody:"+body+"\n");
		    nottowebqa.flush();
		    nottowebqa.close();
			
			
		webqalog.flush();
		
		if ( webqaflag == true && answer.matches(".*[a-zA-Z0-9].*"))
		{
			qalog.append("title-scope:"+scope_title + "\n"+ "body-scope:"+scope_body+"\n");
			qalog.append("Answer :" + answer + "\n\n");
			qalog.flush();
		}
		
		webqalog.flush();
		webqalog.close();
		qalog.flush();
		qalog.close();
		replyxml.close();
		return answer;
	}

}