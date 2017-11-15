package com.framework.utility;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Hashtable;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import atu.testng.reports.listeners.ATUReportsListener;
import atu.testng.reports.listeners.ConfigurationListener;
import atu.testng.reports.listeners.MethodListener;
//@Listeners({ ATUReportsListener.class, ConfigurationListener.class, MethodListener.class })
public class FinalTest {

	/*@Test(priority = 1, dataProviderClass = TestDataProvider.class, dataProvider = "SUITE_SHEET")
//	@AfterSuite
	public void FinalTestReport(Hashtable<String, String> table,Method m) throws InterruptedException, IOException, URISyntaxException {
		System.out.println("Final test");
		HtmlReport.getInstance().generateHTML("D:\\OverallReport.html");
	}*/
	
	//FinalTest.FinalTestReport();
	@Test
	public void FinalTestReport(){
		System.out.println("Final test");
		HtmlReport.getInstance().generateHTML("C:\\Users\\asubram1\\Videos\\Documents\\VeevaCRM_iRep_Automation MS3 April 24\\VeevaCRM_iRep_Automation\\Results\\HTMLSummary\\iRep_TestSummary.html");
	}

}
