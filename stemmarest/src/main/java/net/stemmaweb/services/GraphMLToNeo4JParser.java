package net.stemmaweb.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.stemmaweb.rest.Nodes;
import net.stemmaweb.rest.Relations;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class GraphMLToNeo4JParser
{
	
	public static void parseGraphML(String filename, String databasePath) throws FileNotFoundException, XMLStreamException
	{
		XMLInputFactory factory;
		XMLStreamReader reader;
		File file = new File(filename);
		InputStream in = new FileInputStream(file);
		factory = XMLInputFactory.newInstance();
		reader = factory.createXMLStreamReader(in);
		
		int depth = 0; 
		// 0 root, 1 <graphml>, 2 <graph>, 3 <node>, 4 <data>
		int type_nd = 0;
		// 0 = no, 1 = edge, 2 = node
		HashMap<String, String> map = new HashMap<String, String>();
		// to store all keys of the introduction part
		
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
    	GraphDatabaseService db= dbFactory.newEmbeddedDatabase(databasePath);
		
    	try (Transaction tx = db.beginTx()) 
    	{
	    	Node currNode = null;
	    	Relationship rel = null;
			while (true) {
			    int event = reader.next();
			    if(event == XMLStreamConstants.END_ELEMENT)
			    {
			    	if(reader.getLocalName().equals("graph") ||
			    		reader.getLocalName().equals("graphml") ||
			    		reader.getLocalName().equals("node"))
			    	{
			    		depth--;
			    		type_nd = 0;
			    	}
			    }
			    if (event == XMLStreamConstants.END_DOCUMENT) {
			       reader.close();
			       break;
			    }
			    if (event == XMLStreamConstants.START_ELEMENT) {
			    	
			        if(reader.getLocalName().equals("data"))
			        {
			        	if(depth==3)
			        	{
			        		if(type_nd==2) // node
			        		{
			        			if(map.get(reader.getAttributeValue(0))!=null)
			        				currNode.setProperty(map.get(reader.getAttributeValue(0)), 
			        							reader.getElementText());
			        		}
			        	}
			        	else
			        	{
			        		// needs implementation of meta data here
			        	}
			        }
			        else if(reader.getLocalName().equals("node"))
			        {
			        	currNode = db.createNode(Nodes.WORD);
			        	currNode.setProperty("id", reader.getAttributeValue(0));
			        	depth++;
			        	type_nd = 2;
			        }
			        else if(reader.getLocalName().equals("key") /*&& (depth==1)*/)
			        {
			        	String key = "";
			        	String value = "";
			  
			        	for(int i=0;i<reader.getAttributeCount();i++)
			        	{
			        		if(reader.getAttributeName(i).equals(new QName("attr.name")))
			        		{
			        			value = reader.getAttributeValue(i);
			        		}
			        		else if(reader.getAttributeName(i).equals(new QName("id")))
			        		{
			        			key = reader.getAttributeValue(i);
			        		}
			        	}
			        	map.put(key, value);
			        }
			        else if(reader.getLocalName().equals("graphml"))
			        {
			        	depth++;
			        }
			        else if(reader.getLocalName().equals("graph"))
			        {
			        	depth++;
			        }
			    }
			}
			tx.success();
		}
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	System.out.println("Error!");
	    }
    	
    	in = new FileInputStream(file);
		factory = XMLInputFactory.newInstance();
		reader = factory.createXMLStreamReader(in);
    	
    	try (Transaction tx = db.beginTx()) 
    	{
	    	Node currNode = null;
	    	Relationship rel = null;
			while (true) {
			    int event = reader.next();
			    if(event == XMLStreamConstants.END_ELEMENT)
			    {
			    	if(reader.getLocalName().equals("graph") ||
			    		reader.getLocalName().equals("graphml") ||
			    		reader.getLocalName().equals("edge"))
			    	{
			    		depth--;
			    		type_nd = 0;
			    	}
			    }
			    if (event == XMLStreamConstants.END_DOCUMENT) {
			       reader.close();
			       break;
			    }
			    if (event == XMLStreamConstants.START_ELEMENT) {
			    	
			        if(reader.getLocalName().equals("data"))
			        {
			        	if(depth==3)
			        	{
			        		if(type_nd==1) // edge
			        		{
			        			if(map.get(reader.getAttributeValue(0))!=null)
			        				rel.setProperty(map.get(reader.getAttributeValue(0)),
			        						reader.getElementText());
			        		}
			        	}
			        	else
			        	{
			        		// needs implementation of meta data here
			        	}
			        }
			        else if(reader.getLocalName().equals("edge"))
			        {
			        	ResourceIterable<Node> startNodes = db.findNodesByLabelAndProperty(Nodes.WORD, "id", reader.getAttributeValue(0));
			        	ResourceIterable<Node> endNodes = db.findNodesByLabelAndProperty(Nodes.WORD, "id", reader.getAttributeValue(1));
			        	Iterator st_it = startNodes.iterator();
			        	Iterator en_it = endNodes.iterator();
			        	if(st_it.hasNext() && en_it.hasNext())
			        	{
			        		rel = ((Node)st_it.next()).createRelationshipTo(((Node)en_it.next()), Relations.NORMAL);
			        		rel.setProperty("id", reader.getAttributeValue(2));
			        	}
			        	depth++;
			        	type_nd = 1;
			        }
			        else if(reader.getLocalName().equals("graphml"))
			        {
			        	depth++;
			        }
			        else if(reader.getLocalName().equals("graph"))
			        {
			        	depth++;
			        }
			    }
			}
			tx.success();
		}
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	System.out.println("Error!");
	    }
	    finally
	    {
	    	db.shutdown();
	    }

	}
	
}