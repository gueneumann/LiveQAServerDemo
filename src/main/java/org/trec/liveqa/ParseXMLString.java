package org.trec.liveqa;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;

public class ParseXMLString {

  public String retrieve_first(String xmlRecords) {
	  
	 // System.out.println("xmlRecords"+xmlRecords);
   /* xmlRecords =
      "<data>" +
      " <employee>" +
      "   <name>John</name>" +
      "   <title>Manager</title>" +
      " </employee>" +
      " <employee>" +
      "   <name>Sara</name>" +
      "   <title>Clerk</title>" +
      " </employee>" +
      "</data>";
      */
	  
	  /*
	  xmlRecords="<mdb>" +
			    "<movies>" +
			   "<movie id=\"godfather\">" +
			      "<title>The Godfather</title>" +
			      "<year>1972</year>" +
			      "<directors>" +
			        "<director idref=\"francisfordcoppola\"/>" +
			      "</directors>" +
			      "<genres>" +
			        "<genre>Crime</genre>" +
			        "<genre>Drama</genre>" +
			      "</genres>" +
			       "<cast>" +
			        "<performer>" +
			          "<actor idref=\"marlonbrando\"/>" +
			          "<role>Don Vito Corleone</role>" +
			        "</performer>" +
			     "</cast>" +
			    "</movie>" +
			    "</movies>" +
			    "<performer id=\"kimnovak\">" +
			      "<name>Marilyn Pauline Novak</name>" +
			      "<dob>1933-02-13</dob>"+
			      "<pob>Chicago, Illinois, USA</pob>" +
			      "<actedin>" +
			        "<movie idref=\"vertigo\"/>" +
			      "</actedin>" +
			    "</performer>" +
			    "</mdb>";
	  */
	
	  xmlRecords=xmlRecords.replaceAll(">\\s*\n\\s*<", "><");
	  String firstsnippet="";

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
       // System.out.println(movieElement.getAttributes().getNamedItem("amount").getNodeValue()); //no.of snippets: 
           NodeList snippet = movieElement.getElementsByTagName("snippet");

           firstsnippet=snippet.item(0).getFirstChild().getTextContent();
          // System.out.println(snippet.item(0).getFirstChild().getTextContent());
         }
    }
    catch (Exception e) {
        e.printStackTrace();
    }

    return firstsnippet;   
  }

  public static String getCharacterDataFromElement(Element e) {
    Node child = e.getFirstChild();
    if (child instanceof CharacterData) {
       CharacterData cd = (CharacterData) child;
       return cd.getData();
    }
    return "?";
  }
}