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
	public class AddEntry extends TestBase {

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
		        client.click("NATIVE", "xpath=//*[@placeholder='Enter your password']", 0, 1);
		        client.sendText(password);
		        client.click("NATIVE", "xpath=//*[@text='return']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Login Securely']", 0, 1);
		/*        if(client.waitForElement("NATIVE", "//*[contains(text(),'Touch ID')]", 0, 10000)){
		            // If statement
		        }
		        client.click("NATIVE", "//*[contains(text(),'Cancel')]", 0, 1);
		        if(client.waitForElement("NATIVE", "xpath=//*[@text=' EMAIL CONFIRMATION REQUIRED']", 0, 10000)){
		            // If statement
		        }
		        client.click("NATIVE", "xpath=//*[@text='Skip']", 0, 1);*/
		    /*    client.click("NATIVE", "//*[contains(text(),'Entry')]", 0, 1);
		        if(client.waitForElement("NATIVE", "//*[contains(text(),'Entries')]", 0, 5000)){
		            // If statement
		        }*/
		        client.click("NATIVE", "xpath=//*[@text='Add Entry']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@accessibilityLabel='Down arrow']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@knownSuperClass='UICollectionViewCell']", 13, 1);
		        client.click("NATIVE", "xpath=//*[@accessibilityLabel='Up arrow']", 0, 1);
		        client.elementSendText("NATIVE", "xpath=//*[@placeholder='Enter Platelet Count']", 0, "67");
		        client.elementSwipe("NATIVE", "xpath=//*[@class='UIASlider']", 0, "Left", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Save']", 0, 1);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text='Your Platelet & Dosage entry has been added successfully.']", 0)){
		            // If statement
		        }
		        client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);


	
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
			
			}catch(Exception e){
				testFailed(e);
			}
		}
	}	



