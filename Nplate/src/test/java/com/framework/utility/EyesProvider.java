package com.framework.utility;

import java.net.URI;
import java.net.URISyntaxException;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.selenium.Eyes;

public class EyesProvider {
public static final ThreadLocal<Eyes> eyes = new ThreadLocal<Eyes>();
private static BatchInfo batch = null;

public static void SetBatchIfEmpty(String batchName){
	if(batch == null){
		batch = new BatchInfo(batchName);
		String batchId = System.getenv("APPLITOOLS_BATCH_ID");
		if (batchId != null) {
			batch.setId(batchId);
		}
	}
}

public static Eyes Instance() throws URISyntaxException{

	if(eyes.get() == null){
		Eyes obj = new Eyes(new URI("https://biogeneyes.applitools.com"));
		obj.setApiKey("3jNqbXmlpPEP7ziRfxeMUqa59gb4LzP98xMPqe104105XXRI110");
		obj.setForceFullPageScreenshot(true);
		obj.setBatch(batch);
		eyes.set(obj);
	}
	
	return eyes.get();
	}
}
