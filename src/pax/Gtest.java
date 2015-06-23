package pax;


import java.util.*;
import java.io.*;
import java.net.*;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.impl.xpath.XPath;
import org.xml.sax.InputSource;

import gate.*;
import gate.creole.*;
import gate.util.*;
import gate.util.persistence.PersistenceManager;
import gate.corpora.RepositioningInfo;

public class Gtest {
	@SuppressWarnings("deprecation")
	
	public static void main(String[] args) throws GateException, IOException,ExecutionException, XPathExpressionException{
		
		Gate.init();
		Corpus corpus = Factory.newCorpus("testCorpus");
		//File directory = new File("/home/shikhar/Desktop/test.txt");
	
		File docFile = new File("abc.txt");
		
		Document doc = Factory.newDocument(docFile.toURI().toURL());
		corpus.add(doc);

		Gate.getCreoleRegister().registerDirectories(
				new File(Gate.getPluginsHome(),"ANNIE").toURI().toURL()
				);
		Gate.getCreoleRegister().registerDirectories(
				new File(Gate.getPluginsHome(),"Parser_Stanford").toURI().toURL()
				);
	
		Gate.getCreoleRegister().registerDirectories(
				new File(Gate.getPluginsHome(),"MuNPEx").toURI().toURL()
				);


		Gate.getCreoleRegister().registerDirectories(
				new File(Gate.getPluginsHome(),"Tools").toURI().toURL()
				);

		ProcessingResource sp = null;
		sp = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter");

		ProcessingResource tok = null;
		tok = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.DefaultTokeniser");

		ProcessingResource tagger = null;
		tagger = (ProcessingResource) Factory.createResource("gate.creole.POSTagger");			

		ProcessingResource morphor = null;
		morphor = (ProcessingResource) Factory.createResource("gate.creole.morph.Morph");		//from tools

		ProcessingResource parser = null;
		parser = (ProcessingResource) Factory.createResource("gate.stanford.Parser");

		FeatureMap params = Factory.newFeatureMap();
		params.put("parserASName", "stanford");
		
		ProcessingResource paExtractor = null;
		paExtractor = (ProcessingResource) Factory.createResource("info.semanticsoftware.munpex.MuNPExENTransducer");

		SerialAnalyserController pipeline = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
	
		pipeline.add(sp);
		pipeline.add(tok);
		pipeline.add(tagger);
		pipeline.add(morphor);
		pipeline.add(parser);
		pipeline.add(paExtractor);
	
		pipeline.setCorpus(corpus);
		pipeline.execute(); // from here the errormessage is originated

		String docXmlString = null;
		
		docXmlString = doc.toXml();

		//System.out.print(docXmlString);
		String xml = docXmlString;

		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = (XPath) xpathFactory.newXPath();

		InputSource source = new InputSource(new StringReader(
		    xml));
		String status = ((javax.xml.xpath.XPath) xpath).evaluate("/AnnotationSet/Annotation/Feature", source);

		System.out.println("satus=" + status);
		
		
	}

}

