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
	public class FullFlow extends TestBase {

		@Test(dataProviderClass = TestDataProvider.class, dataProvider = "Nplate", priority = 1)
		public void nPlate(Hashtable<String, String> table, Method m)
				throws Exception {

			// Checking runmodes
			validateRunmodes(m.getName(), Constants.NPLATE_SUITE, table.get(Constants.RUNMODE_COL));

			try {			

				String userName = table.get(Constants.USERNAME);
				String password = table.get(Constants.PASSWORD);
				String plateletcount = table.get(Constants.PLATELETCOUNT);

				configDriverWithReport(m, "Nplate script", "");//Configuring Mobile Driver
				String deviceName = URLReader.getDeviceName("Nplate");
				launchApplication(prop.getProperty("deviceType"), deviceName, prop.getProperty("appType"), prop.getProperty("VeevaCRMappName"), "", "");	//Launching Application

				
				if(client.isElementFound("NATIVE", "xpath=//*[@text='Login to Nplate']", 0)){
					Thread.sleep(2000);
		             report.updateTestLog("Verify Onboarding Screen","Onboarding Screen is displayed", Status.PASS);
		            }
				else 
				{
					Thread.sleep(2000);
					report.updateTestLog("Verify Onboarding Screen","Onboarding Screen is not displayed", Status.FAIL);
				}
					
				
				

				client.click("NATIVE", "xpath=//*[@text='Login to Nplate']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@placeholder='Enter your email address']", 0, 1);
		        client.sendText(userName);
		        client.click("NATIVE", "xpath=//*[@placeholder='Enter your password']", 0, 1);
		        client.sendText(password);
		        client.click("NATIVE", "xpath=//*[@text='return']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Login Securely']", 0, 1);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text='Home' and @class='UIAStaticText']", 0))
		        {
		        	 Thread.sleep(1000);
		        	   report.updateTestLog("Verify Home Screen","Home Screen is displayed", Status.PASS);
	            }
			else 
			{
				Thread.sleep(1000);
				 report.updateTestLog("Verify Home Screen","Home Screen is not displayed", Status.FAIL);
			}
		        
		        client.click("NATIVE", "xpath=((//*[@class='UIAView' and ./parent::*[@class='UIAView' and ./parent::*[@class='UIAView' and ./preceding-sibling::*[@accessibilityLabel='Home'] and ./parent::*[@class='UIAView']]]]/*[@class='UIAView'])[1]/*[@class='UIAButton'])[2]", 0, 1);
		        
		        
		        Thread.sleep(1000);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text=concat('We', \"'\", 're sorry you', \"'\", 're having connectivity problems. Please try using the Nplate app when your connection is restored.')]", 0)){
		            // If statement
		        	 client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		        }
		       

		        
		        
		        client.click("NATIVE", "xpath=//*[@text and @class='UIAButton' and ./parent::*[@class='UIAView']]", 0, 1);
		        client.click("NATIVE", "xpath=//*[@accessibilityLabel='Down arrow']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@knownSuperClass='UICollectionViewCell']", 13, 1);
		        client.click("NATIVE", "xpath=//*[@accessibilityLabel='Up arrow']", 0, 1);
		        client.elementSendText("NATIVE", "xpath=//*[@placeholder='Enter Platelet Count']", 0, plateletcount);
		        client.elementSwipe("NATIVE", "xpath=//*[@class='UIASlider']", 0, "Left", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Save']", 0, 1);
		        
		        if(client.isElementFound("NATIVE", "xpath=//*[@text='Your Platelet & Dosage entry has been added successfully.']", 0)){
		        	Thread.sleep(1000);
		        	 report.updateTestLog("Verify Platelet & Dosage Entry","Platelet Count & Dosage Entry is successfull", Status.PASS);
	            }
			else 
			{
				Thread.sleep(1000);
				 report.updateTestLog("Verify Platelet & Dosage Entry","Platelet Count & Dosage Entry is not successfull", Status.FAIL);
			}
		        
		        client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		        
		        Thread.sleep(1000);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text=concat('We', \"'\", 're sorry you', \"'\", 're having connectivity problems. Please try using the Nplate app when your connection is restored.')]", 0)){
		            // If statement
		        	 client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		        }
		       

		        client.click("NATIVE", "xpath=(//*[@accessibilityLabel='Home']/*[@text and @class='UIAButton'])[1]", 0, 1);
		        
		        Thread.sleep(1000);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text=concat('We', \"'\", 're sorry you', \"'\", 're having connectivity problems. Please try using the Nplate app when your connection is restored.')]", 0)){
		            // If statement
		        	 client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		        }
		       

		        
		        client.click("NATIVE", "xpath=//*[@text and @class='UIAButton' and ./parent::*[@class='UIAView']]", 0, 1);
		        client.click("NATIVE", "xpath=//*[@class='UIAView' and ./*[@class='UIACollectionView']]//*[@knownSuperClass='UILabel']", 34, 1);
		        client.click("NATIVE", "xpath=//*[@text='TIME:']", 0, 1);
		        if(client.waitForElement("NATIVE", "xpath=//*[@class='UIAPickerWheel']", 0, 10000)){
		            
		        }
		        client.swipe("Down", 20, 200);
		        client.click("NATIVE", "xpath=//*[@text='TIME:']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@accessibilityLabel='Down arrow' and ./preceding-sibling::*[@text='NOTE:']]", 0, 1);
		        client.click("NATIVE", "xpath=(//*[@class='UIATable']/*/*[@class='UIAStaticText' and ./parent::*[@class='UIAView']])[1]", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Save']", 0, 1);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text='Your appointment added successfully']", 0)){
		        	Thread.sleep(1000);
		        	 report.updateTestLog("Verify Appointment Added Successfully","Appointment is Added Successfully", Status.PASS);
	            }
			else 
			{
				Thread.sleep(1000);
				report.updateTestLog("Verify Appointment Added Successfully","Appointment is not Added Successfully", Status.FAIL);
			}
		        
		        client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		        
		        Thread.sleep(1000);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text=concat('We', \"'\", 're sorry you', \"'\", 're having connectivity problems. Please try using the Nplate app when your connection is restored.')]", 0)){
		            // If statement
		        	 client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		        }
		       

		        client.click("NATIVE", "xpath=//*[@text='Back']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Profile']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@text='Logout']", 0, 1);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text='You will now be logged out. You have to enter your email address and password to login again.']", 0)){
		        	report.updateTestLog("Verify Logout pop up","Logout pop up is displayed Successfully", Status.PASS);
	            }
			else 
			{
				Thread.sleep(1000);
				report.updateTestLog("Verify Logout pop up","Logout pop up is not displayed Successfully", Status.FAIL);
			}
		        
		        
		        client.click("NATIVE", "xpath=//*[@text='Ok']", 0, 1);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text='Login Securely']", 0)){
		        	Thread.sleep(1000);
		        	report.updateTestLog("Verify Login Screen","Login Screen is displayed successfully", Status.PASS);
	            }
			else 
			{
				Thread.sleep(1000);
				report.updateTestLog("Verify Login Screen","Login Screen is not displayed successfully", Status.FAIL);
			}
		        
		        }
		    

				
				catch(Exception e){
					testFailed(e);
				}
			}
		}	




