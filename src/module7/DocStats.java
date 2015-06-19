package module7;

import gate.creole.AbstractLanguageAnalyser;
import gate.Resource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;


public class DocStats extends AbstractLanguageAnalyser {
	
	@Override
	public void execute() throws ExecutionException{
		int tokens = document.getAnnotations().get("Token").size();
		document.getFeatures().put("token_count",tokens);
	}
	
	@Override
	public Resource init() throws ResourceInstantiationException{
		System.out.println(getClass().getName()+ " is instantiating");
		return this;
	}
	
}
