package NPlate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cognizant.framework.Status;
import com.experitest.selenium.MobileWebDriver;
import com.framework.utility.Constants;
import com.framework.utility.HtmlReport;
import com.framework.utility.TestDataProvider;
import com.framework.testbase.TestBase;
import atu.testng.reports.ATUReports;
import atu.testng.reports.listeners.ATUReportsListener;
import atu.testng.reports.listeners.ConfigurationListener;
import atu.testng.reports.listeners.MethodListener;
import atu.testng.reports.logging.LogAs;
import atu.testng.selenium.reports.CaptureScreen;
import supportlibraries.DriverScript;


@Listeners({ ATUReportsListener.class, ConfigurationListener.class, MethodListener.class })


	/*@Test(dataProviderClass = TestDataProvider.class, dataProvider = "INT_AccountCreation_Suite", priority = 1)
	public void INT_Regression_Account_Creation_iRep(Hashtable<String, String> table, Method m)
			throws InterruptedException, IOException {
		
		// Checking runmodes
		validateRunmodes(m.getName(), Constants.IREP_INT_ACCOUNT_CREATION_SUITE, table.get(Constants.RUNMODE_COL));*/

		

	public	class Nplate extends TestBase{
			
		    private String host = "localhost";
		    private int port = 8889;
		    private String projectBaseDirectory = "C:\\Users\\480401\\workspace\\project2";
		    protected MobileWebDriver driver = null;

		    @Before
		    public void setUp(){
		        driver = new MobileWebDriver( host, port, projectBaseDirectory, "xml", "reports", "Login");
		    }

		    @Test
		    public void testLogin(){
		        driver.setDevice("ios_app:iPhone-Amgen (2)");
		        driver.application("com.amgen.MigraineLog").launch(true, false);
		        driver.useNativeIdentification();
		        driver.findElement(By.xpath("//*[@text='Login to Nplate']")).click();
		        driver.findElement(By.xpath("//*[@placeholder='Enter your email address']")).click();
		        driver.device().sendText("mohamedkahaf@gmail.com");
		        driver.findElement(By.xpath("//*[@placeholder='Enter your password']")).click();
		        driver.device().sendText("123456");
		        driver.findElement(By.xpath("//*[@text='return']")).click();
		        driver.findElement(By.xpath("//*[@text='Login Securely']")).click();
		        if(driver.client.isElementFound("NATIVE", "//*[contains(text(),'Touch ID')]", 0)){
		            // If statement
		        }
		        driver.findElement(By.xpath("//*[@text='Profile']")).click();
		        driver.findElement(By.xpath("//*[@text='Logout']")).click();
		        driver.findElement(By.xpath("//*[@text='Ok']")).click();
		    }

		    @After
		    public void tearDown(){
		        // Generates a report of the test case.
		        // For more information - https://docs.experitest.com/display/public/SA/Report+Of+Executed+Test
		        driver.generateReport(false);
		        // Releases the client so that other clients can approach the agent in the near future. 
		        driver.releaseClient();
		    }
		

		
		
		
}
