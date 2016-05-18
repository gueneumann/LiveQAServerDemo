package org.trec.liveqa;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class WebQAClient {
	
	
	public static void main(String[] args) throws MalformedURLException, XmlRpcException
	{
		WebQAClient webqac=new WebQAClient();
		String x=webqac.answers("Wisdom teeth extraction and normal food ? Im eating mashed potatoes, soup, pudding, jello, and other soft foods . I was wondering, typically, when most people can go back to eating normal foods like pizzas, burgers, fries, etc.?");
		
		System.out.println("Output:\n"+x);
		
	}
	public void classtype(Object obj) {
		Class cls = obj.getClass();
		System.out.println("The type of the object is: " + cls.getName());
	}
	
	public String answers(String question) throws XmlRpcException, MalformedURLException{

		
		String filename = "data/properties_client.txt";   
		String result ="";

		File filename_f = new File (filename);		

		FileInputStream propInFile = null;
		try {
			propInFile = new FileInputStream(filename_f);
		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
			System.out.println("cannot find properties file");
		} 


		Properties p2 = new Properties();
		try {
			p2.loadFromXML(propInFile);
		} catch (InvalidPropertiesFormatException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl(); 


		String serverUrl = p2.getProperty("serverUrl");
		//  String serverUrl = "http://localhost:"+port+"/xmlrpc";  
		// String serverUrl = "http://lns-87009.sb.dfki.de:"+port+"/xmlrpc";
       //System.out.println("Server Url: " + serverUrl);

		config.setServerURL(new URL(serverUrl));
		config.setEnabledForExceptions(true);
		config.setConnectionTimeout(60*100);
		config.setReplyTimeout(60 * 1000);
		try{
		XmlRpcClient client = new XmlRpcClient();
		
		client.setConfig(config);

		

		Vector<String> v = new Vector<String>();
		v.add(question);

		//    System.out.println("execute client");
		
		
		 result = (String) client.execute("test.startController", v);
		//  System.out.println("wait for reply");
		//System.out.println(result);
		}
		catch(Exception e)
		{
			//System.err.print("Exception is -"+e);
		}


		return result;
	}

}

