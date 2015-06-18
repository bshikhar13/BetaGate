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
		
		params.put(
				Document.DOCUMENT_URL_PARAMETER_NAME,new URL("http://stackoverflow.com/questions/8943661/please-initialize-the-log4j-system-properly-warning"));
		
		params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
		
		
		FeatureMap feats = Factory.newFeatureMap();
		feats.put("date", new Date());
		Factory.createResource("gate.corpora.DocumentImpl",params,feats,"This is Home");
		Document doc = Factory.newDocument(new URL("http://stackoverflow.com/questions/8943661/please-initialize-the-log4j-system-properly-warning"), "UTF-8");
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