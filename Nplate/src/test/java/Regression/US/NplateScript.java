package Regression.US;

	import java.lang.reflect.Method;
	import java.util.Hashtable;
	import org.testng.annotations.Listeners;
	import org.testng.annotations.Test;

	import com.framework.utility.Constants;
	import com.framework.utility.TestDataProvider;
	import com.framework.utility.URLReader;
	import com.cognizant.framework.Status;
	import com.framework.testbase.*;

	import atu.testng.reports.listeners.ATUReportsListener;
	import atu.testng.reports.listeners.ConfigurationListener;
	import atu.testng.reports.listeners.MethodListener;

	/*Description: Verify that user is able to create  the survey */

	@Listeners({ ATUReportsListener.class, ConfigurationListener.class, MethodListener.class })
	public class NplateScript extends TestBase {

		@Test(dataProviderClass = TestDataProvider.class, dataProvider = "Nplate", priority = 1)
		public void nPlate(Hashtable<String, String> table, Method m)
				throws Exception {

			// Checking runmodes
			validateRunmodes(m.getName(), Constants.NPLATE_SUITE, table.get(Constants.RUNMODE_COL));

			try {			

				String userName = table.get(Constants.USERNAME);
				String password = table.get(Constants.PASSWORD);

				configDriverWithReport(m, "Nplate script", "");//Configuring Mobile Driver
				String deviceName = URLReader.getDeviceName("Nplate");
				launchApplication(prop.getProperty("deviceType"), deviceName, prop.getProperty("appType"), prop.getProperty("VeevaCRMappName"), "", "");	//Launching Application

				
				
		        client.click("NATIVE", "xpath=//*[@text='Login to Nplate']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@placeholder='Enter your email address']", 0, 1);
		        client.sendText(userName);
		        report.updateTestLog("Verfiy user entered"," username entered successfully in ", Status.PASS);
				
		        client.click("NATIVE", "xpath=//*[@placeholder='Enter your password']", 0, 1);
		        client.sendText(password);
		        client.click("NATIVE", "xpath=//*[@text='return']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Login Securely']", 0, 1);
		        if(client.isElementFound("NATIVE", "//*[contains(text(),'Touch ID')]", 0)){
		            // If statement
		        }
		        client.click("NATIVE", "xpath=//*[@text='Profile']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Logout']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		    

		   /* @AfterTest
		    public void tearDown(){
		        // Generates a report of the test case.
		        // For more information - https://docs.experitest.com/display/public/SA/Report+Of+Executed+Test
		        client.generateReport(false);
		        // Releases the client so that other clients can approach the agent in the near future. 
		        client.releaseClient();*/
		    

				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				/*//Login to VeevaCRM
				loginToVeevaCRM(userName, password);
				
				//verify already created survey target
				client.click("NATIVE",prop.getProperty("CRM_SurveyTargetsTab"), 0, 1);
				if(client.waitForElement("NATIVE",prop.getProperty("CRM_SurveyTargetsTab"), 0, 30000)){				
					report.updateTestLog("Tap on Survey targets", "Clicked on survey Targets and the user could able to see the created Survey Target as expected", Status.PASS);
					client.click("NATIVE","xpath= //*[contains(@accessibilityLabel,'"+table.get(Constants.US_SURVEYCREATION_TOPIC_COL)+"')]", 0, 1);
					client.verifyElementFound("NATIVE", prop.getProperty("US_SurveyQuestionCell"), 0);
					report.updateTestLog("verify Created data of survey targets", "The Survey Target created in online is displayed in iRep with correct data as expected", Status.PASS);
					client.click("NATIVE",prop.getProperty("MC_CancelButton"), 0, 1);
				}else{
					report.updateTestLog("verify Created data of survey targets", "User Unable to see created Survey target with the data in iRep application", Status.FAIL);
				}
				
				//Select HCP account from My Accounts 
				clickMyAccountsAndSelectType(table.get(Constants.US_INTERACTIONTYPE_ACCTYPE_COL));					
				String accName=table.get(Constants.US_DCR_ACCOUNTNAME_COL);
				client.elementSendText("NATIVE", prop.getProperty("US_SearchBoxHomePagev10"), 0, "");
				client.elementSendText("NATIVE", prop.getProperty("US_SearchBoxHomePagev10"), 0, accName);
				client.click("NATIVE","xpath= //*[contains(@accessibilityLabel,'"+accName+"')]", 0, 1);

				//create a new survey target and verify the same data.
				createAndVerifyNewSurveyTarget(table.get(Constants.US_SURVEYCREATION_TOPIC_COL));
				*/
			}catch(Exception e){
				testFailed(e);
			}
		}
	}	



