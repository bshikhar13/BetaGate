package annie;

import java.util.*;
import java.io.*;
import java.net.*;

import gate.*;
import gate.creole.*;
import gate.util.*;
import gate.util.persistence.PersistenceManager;
import gate.corpora.RepositioningInfo;

public class StandAloneAnnie {

	
	private CorpusController annieController;

	public void initAnnie() throws GateException, IOException {
		System.out.println("Initialising ANNIE...");

		// load the ANNIE application from the saved state in plugins/ANNIE
		File pluginsHome = Gate.getPluginsHome();
		File anniePlugin = new File(pluginsHome, "ANNIE");
		File annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
		annieController = (CorpusController) PersistenceManager
				.loadObjectFromFile(annieGapp);
		System.out.println("...ANNIE loaded");

	} 


	public void setCorpus(Corpus corpus) {
		annieController.setCorpus(corpus);
	} 

	public void execute() throws GateException {
		Out.prln("Running ANNIE...");
		annieController.execute();
		System.out.println("...ANNIE complete");
	} 

	public static void main(String args[]) throws GateException, IOException {

		System.out.println("Initializing GATE ...");
		Gate.init();
		System.out.println("...GATE initialised");

		// initialise ANNIE (this may take several minutes)
		StandAloneAnnie annie = new StandAloneAnnie();
		annie.initAnnie();

		Corpus corpus = Factory.newCorpus("StandAloneAnnie corpus");

		FeatureMap params = Factory.newFeatureMap();
		String testString = "I go to U.S.A.. This is a test String and It will come back to take the revenge. Iphone is the best phone in the market. It is manufactured by Apple Inc. Steve Jobs is the founder of Apple Computers. He lives in India. On 26 June 1975, Prime Minister Indira Gandhi declared a state of emergency in India which lasted until 1977. During this period, many of her political opponents were jailed and opposition groups (including the RSS) were banned.[47][48] As pracharak in-charge of the Akhil Bharatiya Vidyarthi Parishad (ABVP), the student wing of the RSS, Modi was forced to go underground in Gujarat and frequently traveled in disguise to avoid arrest. He became involved in printing pamphlets opposing the government, sending them to Delhi and organising demonstrations.[21][49][50][51] During this period Modi wrote a Gujarati book, Sangharsh ma Gujarat (The Struggles of Gujarat), describing events during the Emergency. ";

		params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, testString);

		FeatureMap feats = Factory.newFeatureMap();
		feats.put("createdBy", "ShikharB");
		Factory.createResource("gate.corpora.DocumentImpl", params, feats,
				"This is Home");// Out.prln("Insert start: "+annotStart+" at size position: "+size);
		
		Document doc = Factory.newDocument(testString);
		corpus.add(doc);

		annie.setCorpus(corpus);
		annie.execute();

		Iterator iter = corpus.iterator();
		int count = 0;
		String startTagPart_1 = "<span GateID=\"";
		String startTagPart_2 = "\" title=\"";
		String startTagPart_3 = "\" style=\"background:Red;\">";
		String endTag = "</span>";

		while (iter.hasNext()) {
			doc = (Document) iter.next();
			AnnotationSet defaultAnnotSet = doc.getAnnotations();
			Set annotTypesRequired = new HashSet();
			annotTypesRequired.add("Person");
			annotTypesRequired.add("Location");
			Set<Annotation> peopleAndPlaces = new HashSet<Annotation>(
					defaultAnnotSet.get(annotTypesRequired));

			FeatureMap features = doc.getFeatures();
			String originalContent = (String) features
					.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
			RepositioningInfo info = (RepositioningInfo) features
					.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);

			++count;
			File file = new File("StANNIE_" + count + ".HTML");
			Out.prln("File name: '" + file.getAbsolutePath() + "'");
			if (originalContent != null && info != null) {
				System.out
						.println("OrigContent and reposInfo existing. Generate file...");

				Iterator it = peopleAndPlaces.iterator();
				Annotation currAnnot;
				SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

				while (it.hasNext()) {
					currAnnot = (Annotation) it.next();
					sortedAnnotations.addSortedExclusive(currAnnot);
				}

				StringBuffer editableContent = new StringBuffer(originalContent);
				long insertPositionEnd;
				long insertPositionStart;

				System.out.println("Unsorted annotations count: "
						+ peopleAndPlaces.size());
				System.out.println("Sorted annotations count: "
						+ sortedAnnotations.size());
				for (int i = sortedAnnotations.size() - 1; i >= 0; --i) {
					currAnnot = (Annotation) sortedAnnotations.get(i);
					insertPositionStart = currAnnot.getStartNode().getOffset()
							.longValue();
					insertPositionStart = info
							.getOriginalPos(insertPositionStart);
					insertPositionEnd = currAnnot.getEndNode().getOffset()
							.longValue();
					insertPositionEnd = info.getOriginalPos(insertPositionEnd,
							true);
					if (insertPositionEnd != -1 && insertPositionStart != -1) {
						editableContent.insert((int) insertPositionEnd, endTag);
						editableContent.insert((int) insertPositionStart,
								startTagPart_3);
						editableContent.insert((int) insertPositionStart,
								currAnnot.getType());
						editableContent.insert((int) insertPositionStart,
								startTagPart_2);
						editableContent.insert((int) insertPositionStart,
								currAnnot.getId().toString());
						editableContent.insert((int) insertPositionStart,
								startTagPart_1);
					}
				}

				FileWriter writer = new FileWriter(file);
				writer.write(editableContent.toString());
				writer.close();
			} else if (originalContent != null) {
				System.out.println("OrigContent existing. Generate file...");

				Iterator it = peopleAndPlaces.iterator();
				Annotation currAnnot;
				SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

				while (it.hasNext()) {
					currAnnot = (Annotation) it.next();
					sortedAnnotations.addSortedExclusive(currAnnot);
				}

				StringBuffer editableContent = new StringBuffer(originalContent);
				long insertPositionEnd;
				long insertPositionStart;

				System.out.println("Unsorted annotations count: "+ peopleAndPlaces.size());
				System.out.println("Sorted annotations count: "+ sortedAnnotations.size());

				for (int i = sortedAnnotations.size() - 1; i >= 0; --i) {
					currAnnot = (Annotation) sortedAnnotations.get(i);
					insertPositionStart = currAnnot.getStartNode().getOffset()
							.longValue();
					insertPositionEnd = currAnnot.getEndNode().getOffset()
							.longValue();
					if (insertPositionEnd != -1 && insertPositionStart != -1) {
						editableContent.insert((int) insertPositionEnd, endTag);
						editableContent.insert((int) insertPositionStart,
								startTagPart_3);
						editableContent.insert((int) insertPositionStart,
								currAnnot.getType());
						editableContent.insert((int) insertPositionStart,
								startTagPart_2);
						editableContent.insert((int) insertPositionStart,
								currAnnot.getId().toString());
						editableContent.insert((int) insertPositionStart,
								startTagPart_1);
					}
				}

				FileWriter writer = new FileWriter(file);
				writer.write(editableContent.toString());
				writer.close();
			} else {
				System.out.println("Content : " + originalContent);
				System.out.println("Repositioning: " + info);
			}

			String xmlDocument = doc.toXml(peopleAndPlaces, false);
			String fileName = new String("StANNIE_toXML_" + count + ".HTML");
			FileWriter writer = new FileWriter(fileName);
			writer.write(xmlDocument);
			writer.close();

		}
	}

	public static class SortedAnnotationList extends Vector {
		public SortedAnnotationList() {
			super();
		}

		public boolean addSortedExclusive(Annotation annot) {
			Annotation currAnot = null;

			for (int i = 0; i < size(); ++i) {
				currAnot = (Annotation) get(i);
				if (annot.overlaps(currAnot)) {
					return false;
				}
			}

			long annotStart = annot.getStartNode().getOffset().longValue();
			long currStart;

			for (int i = 0; i < size(); ++i) {
				currAnot = (Annotation) get(i);
				currStart = currAnot.getStartNode().getOffset().longValue();
				if (annotStart < currStart) {
					insertElementAt(annot, i);

					return true;
				} 
			} 

			int size = size();
			insertElementAt(annot, size);
			
			return true;
		} 
	} 
} 