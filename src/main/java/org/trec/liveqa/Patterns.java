package org.trec.liveqa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.*;





public class Patterns {		
	//public Map<String,Integer> freqwords=new HashMap();



	public  Map<Integer,Map<String,Integer>> Qvectors_g=new HashMap();
	public  Map<Integer,Map<String,Integer>> Avectors_g=new HashMap();
	public  Map<String,Map<Integer,Integer>> ngramqvectors_g=new HashMap();
	public  Map<String,Map<Integer,Integer>> ngramavectors_g=new HashMap();

	// Just for testing - delete afterwards
	public  Map<Integer,String> Questions_g=new HashMap();
	public  Map<Integer,String> Answers_g=new HashMap();

	public  Map<String,Double> idfq_g=new HashMap();
	public  Map<String,Double> idfa_g=new HashMap<String ,Double>();

	public  Map<Integer,Integer> maxq_g=new HashMap();
	public  Map<Integer,Integer> maxa_g=new HashMap();
	public  Map<String,Integer> topwordcounts_g=new HashMap();
	public  TreeMap<String,Integer> freqs_g = new TreeMap<String,Integer>();
	public  int top=200;

	public  Map<String,Integer> Text2Vec(String Text, Map<String,Integer> freqwords) throws IOException {	 

		Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(Text);
		String sentence="",trigram="",join;
		Map<String,Integer> pcounts=new HashMap();
		BufferedWriter simplecorpus=getWriter("./data/corpus.simplified");



		while (reMatcher.find()) {
			sentence=reMatcher.group();
			join="<s>";
			for (String word:sentence.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+"))
			{
				if (freqwords.containsKey(word))
					join += " "+word; 
				else
					join += " xxxx";
			}

			simplecorpus.append(join.replaceAll("^<s>", "")+".");
			simplecorpus.flush();

			join += " <\\s>";


			/*  if ( join.matches("^<s> when.*"))
			      System.out.println(join);*/


			NGramIterator gram3=new NGramIterator(3,join);
			//			NGramIterator gram3=new NGramIterator(4,join);

			while(gram3.hasNext())
			{

				trigram=gram3.next();
				if (! ( trigram.matches(".*xxxx xxxx.*") || trigram.matches("xxxx .* xxxx") ))
				{

					// System.out.println("trigram - "+trigram);

					if ( pcounts.containsKey(trigram))
					{
						pcounts.put(trigram, pcounts.get(trigram)+1);
					}
					else
					{
						pcounts.put(trigram, 1);

					}
				}
			}


		}
		simplecorpus.append("\n");
		simplecorpus.flush();
		simplecorpus.close();
		return pcounts;
	}


	//Method to initiate a File Writer
	private  BufferedWriter getWriter(String fileLocation) throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileLocation),true)));
	}

	// Get the highest frequency words
	public   void WordCounts(String filename,Integer threshholdf) 
	{
		BufferedReader reader;
		String line="";
		TreeMap<String,Integer> highfreqs = new TreeMap<String,Integer>();

		try {
			reader = new BufferedReader(new FileReader(new File(filename)));
			line = reader.readLine();

			Map<String, Integer> count = new HashMap();


			System.out.println("Reading file -"+filename+" ...\n");
			int q=0;
			int a=0;
			int lno=0;
			int mostfreq=0;
			while (line != null) {
				lno++;
				if ( line.matches("^#Question:"))
				{
					q++;
					line=reader.readLine();
					continue;
				}
				if ( line.matches("^#Answer:"))
				{
					a++;
					line=reader.readLine();
					continue;

				}

				for (String token : line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+")) {
					// ...
					//token.toLowerCase();
					if ( count.containsKey(token))
					{
						count.put(token, count.get(token)+1);
					}
					else
					{
						count.put(token, 1);    		
					}
				}
				line=reader.readLine();

			}
			ValueComparatorInt bvc =  new ValueComparatorInt(count);
			TreeMap<String,Integer> sorted_count = new TreeMap<String,Integer>(bvc);
			sorted_count.putAll(count);


			int wcount=1;
			for(Map.Entry<String, Integer> entry : sorted_count.entrySet())
			{
				String key=entry.getKey();
				int value= entry.getValue();
				if ( wcount <= top )
				{
					topwordcounts_g.put(key, value);
				}
				wcount++;
				if ( value < threshholdf )
				{
					break;
				}
				highfreqs.put(key,value);
				mostfreq++;
			}

			System.out.println("No of lines:"+lno+"\nQs: "+q+"\n A: "+a+"\nmostfreq-" +mostfreq);
			System.out.println("No. of words - "+sorted_count.size());

			reader.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("FileNotFoundExecption..yo");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOExecption..yo");
			e.printStackTrace();
		}
		//return highfreqs;
		freqs_g=highfreqs;
	}

	// To Calculate IDF of the ngrams
	public  void CalculateDFIDF()
	{
		int totalqdocs=Qvectors_g.size();
		int totaladocs=Avectors_g.size();

		for (String str:ngramqvectors_g.keySet())
		{

			int count=0;
			for (Map.Entry <Integer,Integer> obj: ngramqvectors_g.get(str).entrySet())
			{
				count += obj.getValue();
			}

			if (! idfq_g.containsKey(str))
			{
				idfq_g.put(str, Math.log(((double)totalqdocs/count)));
			}

		}


		for (String str:ngramavectors_g.keySet())
		{

			int count=0;
			for (Map.Entry <Integer,Integer> obj: ngramavectors_g.get(str).entrySet())
			{
				count += obj.getValue();
			}

			if (! idfa_g.containsKey(str))
			{
				idfa_g.put(str, Math.log(((double)totaladocs/count)));

				//idfa_g.put(str, (0.0+totaladocs)/count);
			}

		}


		/*
		for (Integer qid:Qvectors_g.keySet())	
		{

			maxq_g.put(qid,Collections.max(Qvectors_g.get(qid).values()));
		}

		for (Integer qid:Avectors_g.keySet())
		{
			maxa_g.put(qid,Collections.max(Avectors_g.get(qid).values()));
		}
		 */

	}

	public  void GetBestAnswer(String Question,String text)
	{
		
		
	}

    // To fetch the n most similar text(question) that is seen
	public  TreeMap<Integer,Double>  nmostsimilar(Map<String,Integer> tobecompared,boolean question,int top)
	{
		//List<Integer> qids=new ArrayList<Integer>();
		TreeMap<Integer,Double> qids = new TreeMap();

		Map <Integer,Double> cosinediff= new HashMap();

		//int mostfreq=Collections.max(tobecompared.values());
		int mostfreq=1;

		if ( question)
		{
			for ( String ngram: tobecompared.keySet())
			{

				if ( ngramqvectors_g.containsKey(ngram))
					for (int qid: ngramqvectors_g.get(ngram).keySet())
					{

						double tfsubj=tobecompared.get(ngram)/mostfreq;
						//double tfsubj=(0.0+tobecompared.get(ngram))/Qvectors_g.get(qid).size();



						//double tfidfdot=tfsubj*(Qvectors_g.get(qid).get(ngram)*idfq_g.get(ngram))/maxq_g.get(qid);
						//		System.out.println("qid -" +qid+" ngram "+ngram+"Qvector_g contains qid:"+Qvectors_g.containsKey(qid)+"Qvectorsg[qid] contains ngram:");
						double tfidfdot=0.0001;
						try{
							tfidfdot=tfsubj*(Qvectors_g.get(qid).get(ngram)*idfq_g.get(ngram)*idfq_g.get(ngram));
						}
						catch(Exception e)
						{
							System.out.println("qid -"+qid+":"+Questions_g.get(qid));

						}
						if (! cosinediff.containsKey(qid))	
							cosinediff.put(qid,tfidfdot);
						else
							cosinediff.put(qid,cosinediff.get(qid)+tfidfdot);

					}
			}

			HashMap<String, Double> stringcosinediff = new HashMap<String, Double>();
			for ( Map.Entry <Integer,Double> obj:cosinediff.entrySet())
				stringcosinediff.put(String.valueOf(obj.getKey()), obj.getValue());

			TreeMap<String,Double> sortedcosinediff=SortByValue(stringcosinediff);
			Iterator it=sortedcosinediff.entrySet().iterator();

			int count=1;
			int x=1;
			while( it.hasNext() && count <= top)
			{

				Map.Entry me = (Map.Entry)it.next();
				qids.put(Integer.parseInt((String)me.getKey()),(Double)me.getValue());

				/*
                  if ( x !=0)
		    		{
                	  System.out.println(me.getKey()+" -- "+Questions_g.get(Integer.parseInt((String)me.getKey())));
  		    		  Scanner sn=new Scanner(System.in);
		    		  x=sn.nextInt();
		    		}*/


			}

		}


		return qids;
	}

	//To fetch the probable answer patterns from the question.
	public  Map<String,Double> ProbableAnswerPatterns(String question,Map <String,Integer> freqs) throws IOException
	{


		Map<String,Integer> qgrams = Text2Vec(question,freqs);
		TreeMap<Integer,Double> similarqids= nmostsimilar(qgrams,true,50);
		HashMap <String,Double> answerpatterns=new HashMap();

		for (Map.Entry<Integer, Double> obj: similarqids.entrySet())
		{
			int qid=obj.getKey();
			//double qvalue=obj.getValue()/maxa_g.get(qid);
			double qvalue=obj.getValue();
			//double alpmatcha=0.4;

			topwordcounts_g.put("xxxx", 1);
			topwordcounts_g.put("<s>", 1);
			topwordcounts_g.put("<\\s>", 1);

			for (String ngram:Avectors_g.get(qid).keySet())
			{
				//System.out.println("ngram - "+ngram+"idfa_g -"+idfa_g.get(ngram));
				boolean toofreq=true;
				for (String word:ngram.split("\\s+"))
				{
					if ( ! topwordcounts_g.containsKey(word))
						toofreq=false;
				}

				if (! toofreq)
					if ( ! answerpatterns.containsKey(ngram))
					{
						//answerpatterns.put(ngram,idfa_g.get(ngram)*idfa_g.get(ngram));
						try
						{
							answerpatterns.put(ngram,qvalue*idfa_g.get(ngram)*Avectors_g.get(qid).get(ngram));
						}
						catch(Exception e)
						{

							System.out.println("Something wrong :pos2.");

						}
					}
					else
					{
						try
						{
							answerpatterns.put(ngram,answerpatterns.get(ngram)+(idfa_g.get(ngram)*idfa_g.get(ngram)));
							answerpatterns.put(ngram,answerpatterns.get(ngram)+(qvalue*idfa_g.get(ngram)*Avectors_g.get(qid).get(ngram)));
						}
						catch(Exception e)
						{
							
							System.out.println("Something wrong :pos3.");

						}
					}
			}
		}

		TreeMap<String, Double> sortedanswerpatterns = SortByValue(answerpatterns);


		Iterator it=sortedanswerpatterns.entrySet().iterator();

		//Delete afterwards
		int x=1;

		while(it.hasNext())
		{
			Map.Entry<String,Double> obj=(Map.Entry)it.next();

			/*//Delete afterwards;
    		  if ( x != 0)
              {
    		  System.out.println(obj.getKey()+" -- "+obj.getValue());
    	      System.out.println("Idf = "+idfa_g.get(obj.getKey()));

              Scanner sn=new Scanner(System.in);
              x=sn.nextInt();
              }

			 */
		}

		return answerpatterns;
	}

    // To get score for the answer , given a question.
	public double AnswerScore(String A,Map<String,Integer> freqs,Map<String,Double> answerpatterns_tfidfs) throws IOException
	{

		Map<String,Integer> agrams = Text2Vec(A,freqs);


		double similarity=0.0;

		for (String ngram:agrams.keySet())
		{
			try
			{
				similarity += agrams.get(ngram)*idfa_g.get(ngram)*answerpatterns_tfidfs.get(ngram);
			}
			catch(Exception e)
			{
				//System.out.println("something wrong -\ncheck: idfa_g size=" + idfa_g.size()+"\ncheck:answerpatterns_tfidfs size="+answerpatterns_tfidfs.size()+"\ncheck: agrams size="+agrams.size());
			}
		}

		return similarity;	

	}

    // Train ngram vectors 
	public  void TrainVectors(String filename, Map<String,Integer> freqwords)
	{
		String line=""; 
		int qid=1,lno=0,qstatus=0;
		Map<Integer,Map<String,Integer>> Qvectors=new HashMap();
		Map<Integer,Map<String,Integer>> Avectors=new HashMap();
		Map<String,Map<Integer,Integer>> ngramqvectors=new HashMap();
		Map<String,Map<Integer,Integer>> ngramavectors=new HashMap();



		Map<String,Integer> patterncounts=new HashMap<String,Integer>();
		Map<String,Integer> buffercounts=new HashMap<String,Integer>();



		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			line = reader.readLine();
			String Text=""; //Remove afterwards
			while (line != null) {
				lno++;
				if ( line.matches("^#Question:"))
				{

					Avectors.put(qid,patterncounts);
					//Answers_g.put(qid,Text);

					qid++;
					line=reader.readLine();
					Text=line;
					patterncounts=new HashMap();
					qstatus=1;
					continue;
				}
				if ( line.matches("^#Answer:"))
				{
					line=reader.readLine();
					Qvectors.put(qid,patterncounts);
					//Questions_g.put(qid,Text);
					//System.out.println("qid :"+qid + "\n question:"+Questions_g.get(qid));
					Text=line;		

					patterncounts=new HashMap();
					qstatus=0;
					continue;
				}

				// get trigram features from line and make the vector


				buffercounts=Text2Vec(line,freqwords);

				int tmpint=0;
				// System.out.println("line - "+line+"\nNo. of trigram items in buffer counts = " + buffercounts.entrySet().size());
				for(Map.Entry<String, Integer> entry : buffercounts.entrySet())
				{
					String tempkey=entry.getKey();
					int tempval=entry.getValue();

					/*if ( tmpstr == tempkey)
						  System.out.println(tempkey+"-"+tempval);
					  else
						  tmpstr=tempkey;
					 */

					if ( patterncounts.containsKey(tempkey))
						patterncounts.put(tempkey, patterncounts.get(tempkey)+tempval);
					else
						patterncounts.put(tempkey, tempval);	 


					if ( qstatus == 1)
					{
						if ( ngramqvectors.containsKey(tempkey))
							if (ngramqvectors.get(tempkey).containsKey(qid))
							{
								int gramqcount=ngramqvectors.get(tempkey).get(qid);
								ngramqvectors.get(tempkey).put(qid, gramqcount+tempval);
							}
							else
								ngramqvectors.get(tempkey).put(qid, tempval);
						else
						{
							Map<Integer,Integer> ngramcounts=new HashMap<Integer,Integer>();
							ngramcounts.put(qid,tempval);
							ngramqvectors.put(tempkey,ngramcounts);
						}
					}
					else
					{
						if ( ngramavectors.containsKey(tempkey))
							if (ngramavectors.get(tempkey).containsKey(qid))
							{
								int gramqcount=ngramavectors.get(tempkey).get(qid);
								ngramavectors.get(tempkey).put(qid, gramqcount+tempval);
							}
							else
								ngramavectors.get(tempkey).put(qid, tempval);
						else
						{
							Map<Integer,Integer> ngramcounts=new HashMap<Integer,Integer>();
							ngramcounts.put(qid,tempval);
							ngramavectors.put(tempkey,ngramcounts);
						}

					}

					//if (qstatus == 1)
					//System.out.println("ngramqvectors["+tempkey+"]=<"+qid+","+ngramqvectors.get(tempkey).get(qid)+">");

				}

				line=reader.readLine();
				//Delete afterwards
				Text += line;

			}


			System.out.println("Total no. of trigrams = "+Avectors.entrySet().size()+" "+Qvectors.entrySet().size());
		//	System.out.println("Questions size -"+Questions_g.size()+" Answers size -"+Answers_g.size());
		}

		catch(IOException ioe)
		{
			System.out.println("IOException .. yo!");
		}



		Qvectors_g=Qvectors;
		Avectors_g=Avectors;
		ngramqvectors_g=ngramqvectors;
		ngramavectors_g=ngramavectors;



	}

	// Help function - to sort tree maps
	public  TreeMap<String, Double> SortByValue 
	(HashMap<String, Double> map) {
		ValueComparator vc =  new ValueComparator(map);
		TreeMap<String,Double> sortedMap = new TreeMap<String,Double>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}

	// Main function
	public  void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: TrainPatterns.java <QuestionandAnswersFile> <Frequency threshold>");
			return;
		}

		Patterns pat=new Patterns();
		System.out.println(new Date().toString());
		String QAfile=args[0];
		int threshold=Integer.valueOf(args[1]);
		pat.WordCounts(QAfile,threshold);
		pat.TrainVectors(QAfile,freqs_g);
		pat.CalculateDFIDF();

		//ProbableAnswer
		Scanner input=new Scanner(System.in);
		String line="";
		System.out.println(new Date().toString());

		while(true)
		{
			line="";

			while ( true )
			{
				System.out.println("Enter Question:");
				String s= input.nextLine();
				if ( s.equals("#"))
					break;
				line += s;
				System.out.println(line);
			}

			Map<String,Double> answerpatterns_tfidfs=pat.ProbableAnswerPatterns(line,pat.freqs_g);

			line="";
			while ( true )
			{
				System.out.println("Enter Answer text:");
				String s= input.nextLine();
				if ( s.equals("#"))
					break;
				line += s;
				System.out.println(line);
			}

			System.out.println("Answer score is "+ pat.AnswerScore(line,pat.freqs_g,answerpatterns_tfidfs));

		}



		/*
	        Iterator it=repwords.keySet().iterator();
	        while(it.hasNext())
	        {
	        	Map.Entry pair = ( Map.Entry ) it.next();
	            System.out.println( pair.getKey() + " -- "+ pair.getValue()+"\n");
	            it.remove();

	        }
		 */



	}
}



// Further supporting classes for class Patterns

class NGramIterator implements Iterator<String> {

	String[] words;
	int pos = 0, n;

	public NGramIterator(int n, String str) {
		this.n = n;
		words = str.split(" ");
	}

	public boolean hasNext() {
		return pos < words.length - n + 1;
	}

	public String next() {
		StringBuilder sb = new StringBuilder();
		for (int i = pos; i < pos + n; i++)
			sb.append((i > pos ? " " : "") + words[i]);
		pos++;
		return sb.toString();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}




class ValueComparatorInt implements Comparator<String> {

	Map<String, Integer> base;

	public ValueComparatorInt(Map<String, Integer> base) {
		this.base = base;
	}



	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}


class ValueComparator implements Comparator<String> {

	HashMap<String, Double> base;
	public ValueComparator(HashMap<String, Double> base) {
		this.base = base;
	}


	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
