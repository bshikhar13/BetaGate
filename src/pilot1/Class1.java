package pilot1;


import gate.*;
import gate.gui.*;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.net.URL;



class Class1{
	public static void main(String args[]) throws Exception{

		Gate.init();
		MainFrame.getInstance().setVisible(true);
		//Factory.newDocument("This is a Document, or I must say this is a test document");
		
		FeatureMap params = Factory.newFeatureMap();
		 String testString = "I go to U.S.A.. This is a test String and It will come back to take the revenge. Iphone is the best phone in the market. It is manufactured by Apple Inc. Steve Jobs is the founder of Apple Computers. He lives in India. On 26 June 1975, Prime Minister Indira Gandhi declared a state of emergency in India which lasted until 1977. During this period, many of her political opponents were jailed and opposition groups (including the RSS) were banned.[47][48] As pracharak in-charge of the Akhil Bharatiya Vidyarthi Parishad (ABVP), the student wing of the RSS, Modi was forced to go underground in Gujarat and frequently traveled in disguise to avoid arrest. He became involved in printing pamphlets opposing the government, sending them to Delhi and organising demonstrations.[21][49][50][51] During this period Modi wrote a Gujarati book, Sangharsh ma Gujarat (The Struggles of Gujarat), describing events during the Emergency. "; 
		params.put(
				Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME,testString);
		
		
		FeatureMap feats = Factory.newFeatureMap();
		feats.put("createdBy", "ShikharB");
		Factory.createResource("gate.corpora.DocumentImpl",params,feats,"This is Home");
		Document doc = Factory.newDocument(testString);
		
		Map<String, AnnotationSet> namedASes = doc.getNamedAnnotationSets();
		System.out.println("No. of Named Annotation Sets : "+ namedASes.size());
		
		for (String setName : namedASes.keySet()){
			AnnotationSet aSet = namedASes.get(setName);
			System.out.println("No. of Annotations for : "+setName + " : "+aSet.size());
			Set<String> annotTypes = aSet.getAllTypes();
			for(String aType:annotTypes){
				System.out.println("  "+aType+" : "+aSet.get(aType).size());
			}
		}
		
		AnnotationSet origMarkupsSet = doc.getAnnotations("Original markups");
		AnnotationSet anchorSet = origMarkupsSet.get("a");
		
		for (Annotation anchor : anchorSet){
			String href = (String)anchor.getFeatures().get("href");
			if(href!=null){
				System.out.println(new URL(doc.getSourceUrl(),href));
			}
		}
		
		File pluginsDir = Gate.getPluginsHome();
		// Let’s load the ANNIE plugin
		File aPluginDir = new File(pluginsDir, "ANNIE");
		// load the plugin.
		Gate.getCreoleRegister().registerDirectories(
				aPluginDir.toURI().toURL());
		//create tokenizer
		LanguageAnalyser pr = (LanguageAnalyser)
				Factory.createResource(
						"gate.creole.tokeniser.DefaultTokeniser");
		
		
		pr.setDocument(doc);
		pr.setCorpus(null);
		pr.execute(); 
		
		
	}
}