package com.framework.testbase;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.UploadALM.util.ALMResultUpdater;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.Eyes;
import com.cognizant.framework.Status;
import com.cognizant.framework.Util;
import com.cognizant.framework.selenium.Browser;
import com.cognizant.framework.selenium.ResultSummaryManager;
import com.experitest.client.Client;
import com.experitest.selenium.MobileWebDriver;
import com.framework.utility.Constants;
import com.framework.utility.EyesProvider;
import com.framework.utility.HtmlReport;
import com.framework.utility.Utility;
import com.framework.utility.Xls_Reader;
import com.itextpdf.text.log.SysoCounter;

import atu.testng.reports.ATUReports;
import atu.testng.reports.listeners.ATUReportsListener;
import atu.testng.reports.listeners.ConfigurationListener;
import atu.testng.reports.listeners.MethodListener;
import atu.testng.reports.logging.LogAs;
import atu.testng.reports.utils.Utils;
import atu.testng.selenium.reports.CaptureScreen;
import atu.testng.selenium.reports.CaptureScreen.ScreenshotOf;
import supportlibraries.DriverScript;
import supportlibraries.SeleniumReport;
import supportlibraries.SeleniumTestParameters;

@Listeners({ ATUReportsListener.class, ConfigurationListener.class,MethodListener.class })
public class TestBase extends ATUReportsListener{
	{
		System.setProperty("atu.reporter.config",System.getProperty("user.dir")+"\\resources\\atu.properties");
		System.setProperty("environment","N");
	}
	public static Properties prop;
	public String seleniumOPruntimePath;
	public static Logger Application_logger = Logger.getLogger("*VeevaCRM*");
	public DesiredCapabilities caps = null;
	//	private static ThreadLocal<MobileWebDriver> mobileWebDriver = new ThreadLocal<MobileWebDriver>();
	public static MobileWebDriver mobileWebDriver = null;
	public static Client client;
	public String GridOption = "No";
	public WebDriver driver= null;;// Dont use static word for this variable. IT will create issues in GRID

	//Report
	public SeleniumReport report;
	protected SeleniumTestParameters testParameters;
	protected DriverScript driverScript;
	private ResultSummaryManager resultSummaryManager = new ResultSummaryManager();
	private Date startTime, endTime;

	//***********************************************    Report and ALM  Starts    *******************************************
	public SeleniumReport getReport(){

		return report;

	}

	public SeleniumReport getCurReport(){

		return report;

	}
	/**
	 * Function to do the required set-up activities before executing the overall test suite in TestNG
	 * @param testContext The TestNG {@link ITestContext} of the current test suite 
	 * @throws Exception 
	 */
	@BeforeSuite
	public void suiteSetup(ITestContext testContext) throws Exception
	{
		try{
			initialize();
			//Applitools Config
			String name = testContext.getCurrentXmlTest().getSuite().getName();
			if(!name.contentEquals("Default suite")){
				EyesProvider.SetBatchIfEmpty(name);
			}else if(testContext.getAllTestMethods().length>1){
				EyesProvider.SetBatchIfEmpty(this.getClass().getName());
				//EyesProvider.SetBatchIfEmpty(this.getClass().getSimpleName());
			}

			//report 
			resultSummaryManager.setRelativePath();
			resultSummaryManager.initializeTestBatch(testContext.getSuite().getName());

			int nThreads;
			if (testContext.getSuite().getParallel().equalsIgnoreCase("false")) {
				nThreads = 1;
			} else {
				nThreads = testContext.getCurrentXmlTest().getThreadCount();
			}
			resultSummaryManager.initializeSummaryReport(nThreads);
			resultSummaryManager.setupErrorLog();
			System.out.println("Before suite done");
		}catch(Exception e){
			Application_logger.debug("Error in before suite "+e.getMessage());
			System.out.println("Error in before suite "+e.getMessage());
		}
	}

	/**
	 * Function to do the required set-up activities before executing each test case in TestNG
	 */
	public boolean stopExecution = false;
	@BeforeMethod
	public void testMethodSetup(Method method)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH-mm-ss a");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timeStampStr = sdf.format(timestamp);
		System.out.println(timeStampStr);

		//		String testName = method.getName();
		seleniumOPruntimePath = prop.getProperty("SeleniumScreenshotPath")+"\\Run "+timeStampStr;
		new File(seleniumOPruntimePath).mkdir();

		System.out.println("status is:" + System.getProperty("ExecuteFromHPALM"));
		/*FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();
			if(frameworkParameters.getStopExecution()) {
				suiteTearDown();

				throw new SkipException("Aborting all subsequent tests!");
			} else {*/

		if (System.getProperty("ExecuteFromHPALM")!=null) {
			if (System.getProperty("ExecuteFromHPALM").equals("Y")) {
				com.UploadALM.rest.Constants.ALMUPLOAD = "false";
			}
		}		

		//*******AppliTools*************
		Eyes eyes = getEyes();
		eyes.setMatchLevel(MatchLevel.CONTENT);		

		System.out.println("Opening the eyes instance");


		stopExecution = false;
		startTime = Util.getCurrentTime();

		String currentScenario =
				capitalizeFirstLetter(this.getClass().getPackage().getName());//.substring(12));
		System.out.println(this.getClass().getPackage().getName());
		String currentTestcase = this.getClass().getSimpleName();
		System.out.println(this.getClass().getSimpleName());
		testParameters = new SeleniumTestParameters(currentScenario, currentTestcase);
		Constants.FILENAME_REPORT = this.getClass().getPackage().getName() + "_" + this.getClass().getSimpleName();
		//}
		System.out.println("Before method done");

	}
	//Integrating the result with ALM
	public void almIntegration(org.testng.ITestResult iTestResult) throws Exception{
		String methodname = iTestResult.getMethod().getMethodName();
		com.UploadALM.rest.Constants.HPALMTESTNAME = getClass().getName() + "$" + iTestResult.getMethod().getMethodName() ;
		System.out.println(com.UploadALM.rest.Constants.HPALMTESTNAME);

		if (com.UploadALM.rest.Constants.ALMUPLOAD.equals("true")) {
			if (iTestResult.getStatus() == iTestResult.SUCCESS) {
				System.out.println("Log Message:: Passed");
				ALMResultUpdater.updateStatus(methodname, "Passed", 10, "");
				//ALMResultUpdater.
			} else if (iTestResult.getStatus() == iTestResult.FAILURE) {
				System.out.println("Log Message:: Failed");
				ALMResultUpdater.updateStatus(methodname, "Failed", 10, null);
			} else {
				System.out.println("Log Message:: Skipped");
				ALMResultUpdater.updateStatus(methodname, "Not Completed", 10, null);
			}
			System.out.println("Test Results uploaded in ALM");
		}

		BufferedWriter bufferedWriter = null;
		try {
			String filename = "Results/" + iTestResult.getTestClass().getName() + "#"
					+ iTestResult.getMethod().getMethodName() + ".txt";

			System.out.println("name: " + filename);
			bufferedWriter = new BufferedWriter(new FileWriter(filename));
			if (iTestResult.getStatus() == org.testng.ITestResult.SUCCESS) {
				System.out.println("Log Message:: Passed");
				bufferedWriter.write("Passed");
			} else if (iTestResult.getStatus() == org.testng.ITestResult.FAILURE) {
				System.out.println("Log Message:: Failed");
				bufferedWriter.write("Failed");
			} else {
				System.out.println("Log Message:: Skipped");
				bufferedWriter.write("Not Completed");
			}
		} catch (Throwable e) {
			System.out.println("\nLog Message::@AfterMethod: Exception caught");
			e.printStackTrace();
		} 
		finally {

			try {
				bufferedWriter.close();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void zipFiles(List<String> files, org.testng.ITestResult iTestResult){

		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		FileInputStream fis = null;
		try {
			fos = new FileOutputStream(System.getProperty("user.dir")  + "\\" +  com.UploadALM.rest.Constants.ATTACHMENT + "\\"  + getClass().getName() + "#" + iTestResult.getMethod().getMethodName() + ".zip" );

			com.UploadALM.rest.Constants.ALMTestName = getClass().getName() + "#" + iTestResult.getMethod().getMethodName();
			zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			for(String filePath:files){
				File input = new File(filePath);
				fis = new FileInputStream(input);
				ZipEntry ze = new ZipEntry(input.getName());
				System.out.println("Zipping the file: "+input.getName());
				zipOut.putNextEntry(ze);
				byte[] tmp = new byte[4*1024];
				int size = 0;
				while((size = fis.read(tmp)) != -1){
					zipOut.write(tmp, 0, size);
				}
				zipOut.flush();
				fis.close();
			}
			zipOut.close();
			System.out.println("Done... Zipped the files...");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try{
				if(fos != null) fos.close();
			} catch(Exception ex){
				System.out.println("Error in zipping the HTML and word file");

			}
		}
	}

	//***************AppliTools Configuration Code*************************

	@AfterMethod
	public void finalize() throws URISyntaxException{
		EyesProvider.Instance().abortIfNotClosed();	
	}

	public void AppliTools_results(){
		Eyes eyes = getEyes();
		//*********Customized result and reporting*************************
		TestResults results = eyes.close(false); 
		if(results.isNew()){
			System.out.println("This is new Baseline");
			Assert.assertTrue(true,"This is new Baseline");
			ATUReports.add("AppliTools: New Baseline image",LogAs.INFO, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));

		}else if(!results.isPassed()){
			System.out.println("Images are not matching");
			Assert.assertTrue(true, "Images are not matching");
			ATUReports.add("AppliTools: Images are not matching",LogAs.WARNING, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));

		}else{
			System.out.println("Images are matching");
			Assert.assertTrue(true, "Images are matching");
			ATUReports.add("AppliTools: Images are matching",LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
		}
	}

	public Eyes getEyes(){
		try {
			return EyesProvider.Instance();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}




	/**
	 * Function to do the required wrap-up activities after executing each test case in TestNG
	 * @throws Exception 
	 */
	@AfterMethod
	public void testMethodTearDown(org.testng.ITestResult iTestResult) throws Exception
	{
		String testStatus;
		if (iTestResult.getStatus() == iTestResult.SUCCESS) {
			testStatus = "passed";
		}else{
			testStatus = "failed";
		}		

		//ITestResult.getTestName(); // "Test" ; //driverScript.getTestStatus();
		endTime = Util.getCurrentTime();
		String executionTime = Util.getTimeDifference(startTime, endTime);

		resultSummaryManager.updateResultSummary(testParameters.getCurrentScenario(),
				testParameters.getCurrentTestcase(),
				testParameters.getCurrentTestDescription(),
				executionTime, testStatus);
		//		driverScript.report.addTestLogFooter(executionTime);

		//Adding result to html and releasing client 
		HtmlReport.getInstance().addResult(iTestResult);

		if(client!= null){
			client.applicationClose(prop.getProperty("VeevaCRMappName"));
			client.generateReport(false);
			client.releaseClient();
			System.out.println("Client released");	
			Application_logger.debug("Client released");
		}

		if(driver!= null){
			driver.quit();
		}

		//aborting eyes
		Eyes eyes = getEyes();		
		if(eyes != null){
			eyes.abortIfNotClosed();
		}
		System.out.println("Closing the eyes instance");


		driverScript.wrapUp();
		//Zip the attachment and keep it ready for ALM upload

		List<String> files = new ArrayList<String>();
		System.out.println(driverScript.reportPath.replace("\\", "/") + "/Screenshots (Consolidated)/" + Constants.FILENAME_REPORT + ".docx");
		System.out.println(driverScript.reportPath.replace("\\", "/") + "/HTML Results/" + Constants.FILENAME_REPORT + ".html");
		files.add(driverScript.reportPath.replace("\\", "/") + "/Screenshots (Consolidated)/" + Constants.FILENAME_REPORT + ".docx");
		files.add(driverScript.reportPath.replace("\\", "/") + "/HTML Results/" + Constants.FILENAME_REPORT + ".html");

		zipFiles(files,iTestResult);

		//Integrate to ALM	
		almIntegration(iTestResult);
	}

	/**
	 * Function to do the required wrap-up activities after executing the overall test suite in TestNG
	 */
	@AfterSuite
	public void suiteTearDown()
	{
		resultSummaryManager.wrapUp(true);
		//resultSummaryManager.launchResultSummary();
		//driverScript.wrapUp();
	}


	//***********************************************    Report and ALM  Ends    *******************************************







	//***********************Generic Functions******************************//
	//********************Initializing Functions************//
	public static void initialize() throws Exception{
		try{
			Application_logger.debug("Intializing properties file");
			String Path= System.getProperty("user.dir")+"\\resources\\Project.properties";
			System.out.println("initializing property");
			prop = new Properties();
			FileInputStream fs = new FileInputStream(Path);
			prop.load(fs);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("failed to initialize property");
			throw new Exception(e.getMessage());
		}
	}

	//If testcase failed at any point this method will called from testcase catch statement
	public void testFailed(Exception t){
		System.out.println("Test Case failed "+t.getMessage());
		//ATUReports.add("Test step failed", t.getMessage(),LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
		report.updateTestLog("Verfiy Test Case Passed", " Test Case failed. ErrorMsg: " + t.getMessage(), Status.FAIL);
		Assert.fail("Test Case failed. ErrorMsg: "+ t.getMessage());
		t.printStackTrace();
	}

	private String capitalizeFirstLetter(String myString) 
	{
		StringBuilder stringBuilder = new StringBuilder(myString);
		stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
		return stringBuilder.toString();
	}

	public void validateRunmodes(String TestName, String SuiteName, String DataRunmode ){

		Application_logger.debug("Validating runmode of "+TestName+"of suite "+SuiteName);
		//		initialize();//Initializing properties file
		//Checking runmodes of Test suite, test case and data run mode

		Boolean SuiteRunmode = Utility.isSuiteRunnable(SuiteName, new Xls_Reader(System.getProperty("user.dir")+"\\src\\test\\java\\com\\IRep\\datatables\\Suite.xlsx"));
		Boolean TestRunmode = Utility.isTestCaseRunnable(TestName, new Xls_Reader(System.getProperty("user.dir")+"\\src\\test\\java\\com\\IRep\\datatables\\"+SuiteName+".xlsx"));
		Boolean setDataRunmode = false;
		if(DataRunmode.equals(Constants.RUNMODE_YES))
			setDataRunmode = true;

		Application_logger.debug("SuiteRunmode "+SuiteRunmode);
		Application_logger.debug("TestRunmode "+TestRunmode);
		Application_logger.debug("setDataRunmode "+setDataRunmode);

		System.out.println("SuiteRunmode: "+ SuiteRunmode + "TestRunmode: "+ TestRunmode + "setDataRunmode: "+ setDataRunmode);
		if(!(SuiteRunmode && TestRunmode && setDataRunmode)){
			Application_logger.debug("Skipping test "+TestName+ " inside Suite "+SuiteName);
			throw new SkipException("Skipping test "+TestName+ " inside Suite "+SuiteName);
		}
		else
			Application_logger.debug("Testcase "+ TestName+" is runnable");
	}
	//*******************Test Reporting****************************//

	public void TestReporting(String Author,String TestRunDescription){

		ATUReports.setWebDriver(driver);
		//ATUReports.getWebDriver();
		ATUReports.indexPageDescription = "Digicomm Automation : "
				+ "27 Productions Smoke Tests"
				+ "114 Regression Suite test cases";

		ATUReports.setAuthorInfo(Author, Utils.getCurrentTime(),"1.0");
		//ATUReportsListener.setPlatfromBrowserDetails();
		ATUReports.setTestCaseReqCoverage("This test is mapped to "+this.getClass().getName()+" class");
		ATUReports.currentRunDescription=TestRunDescription;

	}





	//*************************     App Specific Method    *******************************
	//April 21
	//Author: 		Magdoon Basha M
	//Description: 	Method to select account type from accounts list (Ver 171.10.0)
	public void clickMyAccountsThenSelectAccType(String accType) throws Exception{

		String listXpath= prop.getProperty("AccountSelectionTablexpath");
		String itemXpath = "xpath=//*[@text='"+accType+"' and @class='UITableViewLabel']";
		String itemLabelXpath = "xpath=//*[@text='"+accType+"' and @class='UILabel']";

		try {
			elementClick("NATIVE", "CRM_MyAccounts", 0, 1, "My Accounts Tab");
			client.waitForElement("NATIVE", prop.getProperty("CRM_AccTypeLabel"), 0, 15000);
			client.verifyElementFound("NATIVE", prop.getProperty("CRM_AccTypeLabel"), 0);
			client.elementScrollToTableRow("NATIVE", listXpath, 0, 0);
			if(client.elementSwipeWhileNotFound("NATIVE", listXpath, "Down", 25, 1000, "NATIVE", itemXpath, 0, 2000, 10, true)){
				client.waitForElement("NATIVE", itemLabelXpath, 0, 15000);
				client.verifyElementFound("NATIVE", itemLabelXpath, 0);
				Application_logger.debug(accType + " is selected from accounts list");
				report.updateTestLog("Verfiy "+accType + " is selected from accounts list succesfully", accType + " is selected from accounts list succesfull", Status.PASS);
			}else {
				Application_logger.debug(accType + " is not present in accounts list");
				System.out.println(accType + " is not present in accounts list");
				throw new Exception(accType + " is not present in accounts list");
			}



		}catch (Exception e) {
			Application_logger.debug(accType + " from accounts list not selected. Error: " + e.getMessage());
			report.updateTestLog("Verfiy "+accType + " is not selected from accounts list succesfully", accType + " is selected from accounts list succesfull", Status.FAIL);
			throw new Exception(e.getMessage());
		}
	}
	
	public void loginWebApp(String siteName, String accRegion) throws Exception { 
		try {

			//		openBrowser("Chrome");
			launchBrowser("Chrome");
			report = getCurReport();
			report.setExecMode("web");
			report.setDriver(Manager.getDriver());
			System.out.println("Browser Launched");
			//		openBrowser("Mozilla");
			if(siteName.equalsIgnoreCase("Admin")) {
				System.out.println("Login to Admin Site");
				String adminUsername = null;
				String adminPassword = null;
				if(accRegion.equalsIgnoreCase("US")) {
					adminUsername = prop.getProperty("US_VeevaCRMAdminUsername");
					adminPassword = prop.getProperty("US_VeevaCRMAdminPassword");
				}else if(accRegion.equalsIgnoreCase("INT")) {
					adminUsername = prop.getProperty("INT_VeevaCRMAdminUsername");
					adminPassword = prop.getProperty("INT_VeevaCRMAdminPassword");
				}
				System.out.println("Admin URL : "+	prop.getProperty("VeevaCRMAdminURL"));
				driver.get(prop.getProperty("VeevaCRMAdminURL"));
				System.out.println("Admin URL Navigated");
				waitAndGetElement("id", "AdminUserNameBox").sendKeys(adminUsername);
				waitAndGetElement("id", "AdminPasswordBox").sendKeys(adminPassword);
				verifyLink("id", "AdminLoginButton", "Login button",  "AdminHomePageUrl", "Admin home");
				//			takeScreenshot("Admin user "+adminUsername+" logged in successfully");
				System.out.println(" Admin "+adminUsername + " logged in succesfully");
				//			takeScreenshot("Admin logged");
				System.out.println("Exce mode: "+report.getExecMode() );
				report.updateTestLog("Verfiy Admin user logged in succesfully", "Admin user logged in succesfully", Status.PASS);
			}else if(siteName.equalsIgnoreCase("Network")) {
				System.out.println("Login to Network Site");
				System.out.println("Admin URL : "+	prop.getProperty("VeevaCRMNetworkURL"));
				driver.get(prop.getProperty("VeevaCRMNetworkURL"));
				System.out.println("Network URL Navigated");
				waitAndGetElement("id", "NetworkUsernameBox").sendKeys(prop.getProperty("VeevaCRMNetworkUsername"));
				waitAndGetElement("id", "NetworkPasswordBox").sendKeys(prop.getProperty("VeevaCRMNetworkPassword"));
				verifyLink("id", "NetworkLoginButton", "Login button",  "NetworkHomePageUlr", "Network home");
				System.out.println(" Network "+prop.getProperty("VeevaCRMNetworkUsername") + " logged in succesfully");
				//			takeScreenshot("Network logged");
				//			takeScreenshot("Network user "+prop.getProperty("VeevaCRMNetworkUsername")+" logged in successfully");

				report.updateTestLog("Verfiy data steward user logged in succesfully", "data steward user logged in succesfully", Status.PASS);
			}
		}catch(Exception e){
			System.out.println("Failed to login Site "+siteName);
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception("Failed to login Site "+siteName);
		}
	}
	
	public void launchBrowser(String BrowserName){

		try{
			System.out.println("BrowserName: "+BrowserName);
			if(BrowserName.equalsIgnoreCase("Chrome")){
				//****ChromeOtpions is used to disable chrome extensions
				ChromeOptions options = new ChromeOptions();
				options.addArguments("chrome.switches","--disable-extensions");
				options.addArguments("--disable-popup-blocking");

				//Capabilities of Chrome
				caps= new DesiredCapabilities();
				caps = DesiredCapabilities.chrome();
				caps.setCapability(ChromeOptions.CAPABILITY, options);
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				caps.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				caps.setPlatform(Platform.WINDOWS);
				caps.setJavascriptEnabled(true);

				caps.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true); 

				System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"\\resources\\chromedriver.exe");
				driver = new ChromeDriver(caps);

				Manager.setWebDriver(driver);
				//				driver=Manager.getDriver();
				System.out.println("Thread id = " + Thread.currentThread().getId());
			}
		}catch(Exception e){
			System.out.println("Error in launchBrowser method: "+e.getMessage());
			e.printStackTrace();
			Assert.fail("Not able to open browser "+ e.getMessage());		
		}
	}
	
	//Description: 	Method to close the app
	public void closeApp(String appName){
		client.applicationClose(prop.getProperty(appName));
	}

	//Config report
	//Author: 		Magdoon Basha M
	//Description: 	Method to config driver with report
	public void configDriverWithReport(Method m, String description, String browser ) throws Exception{
		try{
			String execMode = prop.getProperty("ExcecutionMode");
			if(execMode.equalsIgnoreCase("mobile")){
				testParameters.setCurrentTestDescription(description);
				driverScript = new DriverScript(testParameters);
				driverScript.driveTestExecution();
				report = driverScript.getReport();
				report.setExecMode("mobile");
				configMobileWebDriver(m.getName());	//Configuring Mobile Driver
				report.setDriver(mobileWebDriver);	
				report.setClient(mobileWebDriver.client);
			}else if(execMode.equalsIgnoreCase("web")){
				testParameters.setCurrentTestDescription(description);
				driverScript = new DriverScript(testParameters);
				System.out.println("calling open browser");
				openBrowser(browser);
				driverScript.driveTestExecution();
				report = driverScript.getReport();
				report.setExecMode("web");
				report.setDriver(driver);	
				System.out.println("driver configured with browser");
			}
		}catch(Exception e) {
			Application_logger.debug("Error found while configuring driver with report ");
			System.out.println("Error found while configuring driver with report "+ e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	//Seetest Methods
	//Author: 		Magdoon Basha M
	//Description: 	Method to config mobile web driver
	public void configMobileWebDriver(String testName) throws Exception {
		try{
			System.out.println("Config mob");
			String host = prop.getProperty("Host");
			int port = Integer.parseInt(prop.getProperty("Port"));
			String projectBaseDirectory = prop.getProperty("ProjectBaseDirectory");
			String reportType = prop.getProperty("ReportType");
			String reportName = prop.getProperty("ReportName");

			//			String reportName
			Application_logger.debug("Intializing MobileWebDriver");
			mobileWebDriver = new MobileWebDriver(host, port, projectBaseDirectory, reportType, reportName, testName);
			client = mobileWebDriver.client;
			Application_logger.debug("MobileWebDriver instance created");

			//			mobileWebDriver.getDeviceInformation();

		}catch(Exception e){
			Application_logger.debug("Error found while intializing MobileWebDriver");
			throw new Exception(e.getMessage());
		}
	}

	//Author: 		Magdoon Basha M
	//Description: 	Method to change the orientation of device
	public void changeOrientation() throws Exception{
		String orientation = prop.getProperty("orientation").trim();
		System.out.println("ori prop: "+ orientation);
		//		System.out.println(  client.getDeviceProperty( "device.name" ));
		System.out.println("Current Device orientation: "+ client.getDeviceProperty("orientation"));
		if (client.getDeviceProperty("orientation").equalsIgnoreCase(orientation)) {
			//			Application_logger.debug("Device orientation: " + client.getDeviceProperty("orientation"));
			System.out.println("Device orientation: " + client.getDeviceProperty("orientation"));
		}else{
			//client.sendText("\"{"+orientation+"}\"");

			client.sendText("{LANDSCAPE}");
			//client.sendText(orientation);
			//client.deviceAction(orientation);
			Application_logger.debug("Device orientation changed");
			System.out.println("Device orientation changed");
		}

	}



	//Description: 	Method to click the element. This will throw exception if element not found
	public void elementClick(String zone, String element, int index, int clicks, String elementName) throws Exception {
		try { 
			client.waitForElement(zone, prop.getProperty(element), index, 15000);
			//			client.verifyElementFound(zone, prop.getProperty(element), index);
			client.click(zone, prop.getProperty(element), index, clicks);
			System.out.println(elementName + " clicked successfully");
			Application_logger.debug(elementName + " clicked successfully");

		}catch(Exception e) {
			Application_logger.debug("Error while clicking in " + elementName);
			System.out.println("Error while clicking " + elementName);
			throw new Exception(e.getMessage());
		}
	}

	//Author: 		Magdoon Basha M
	//Description: 	Method to click the element. This will throw exception if element not found
	public void dynamicElementClick(String zone, String element, int index, int clicks, String elementName) throws Exception {
		try { 
			client.waitForElement(zone, element, index, 15000);
			client.verifyElementFound(zone,element, index);
			client.click(zone, element, index, clicks);
			System.out.println(elementName + " clicked successfully");
			Application_logger.debug(elementName + " clicked successfully");

		}catch(Exception e) {
			Application_logger.debug("Error while clicking in " + elementName);
			System.out.println("Error while clicking " + elementName);
			throw new Exception(e.getMessage());
		}
	}

	//Author: 		Magdoon Basha M
	//Description: 	Method to wait for the element present
	public boolean waitForElement(String zone, String element, int index, int timeout, String elementName) {
		try {
			if(client.waitForElement(zone, prop.getProperty(element), index, timeout)){
				System.out.println(elementName + " found within timeout");
				Application_logger.debug(elementName + " found within timeout");
				return true;
			}else {
				System.out.println(elementName + " not found within timeout");
				Application_logger.debug(elementName + " not found within timeout");
				return false;
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}




	//Author: 		Magdoon Basha M
	//Description: 	Method to wait for the element to disappear
	public boolean waitForElementToVanish(String zone, String element, int index, int timeout, String elementName) {
		try {
			if(client.waitForElementToVanish(zone, prop.getProperty(element), index, timeout)){
				System.out.println(elementName + " disappeared within timeout");
				Application_logger.debug(elementName + " disappeared within timeout");
				return true;
			}else {
				System.out.println(elementName + " not disappeared within timeout");
				Application_logger.debug(elementName + " not disappeared within timeout");
				return false;
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

		

	/*protected boolean verifyElementClick(String zone, String element, int index, String elementName, String expElemZone, String expElement, int expElemIndex, String expElemName) throws Exception{
		boolean status = false;
		elementClick(zone, element, index, 1, elementName);
		client.waitForElement(expElemZone, prop.getProperty(expElement), expElemIndex, 15000);
		if (isElementFound(expElemZone, expElement, expElemIndex, expElemName)) {
			status = true;
		}else{
			throw new Exception(expElemName+" Not found");
		}
		return status;
	}*/


	
	public void openBrowser(String BrowserName){
		try{

			Constants.BROWSER_VAL = BrowserName;
			if(BrowserName.equalsIgnoreCase("Mozilla")){

				// Firefox profile
				FirefoxProfile profile = new FirefoxProfile(); 
				profile.setPreference("capability.policy.default.Window.QueryInterface","allAccess"); 
				profile.setPreference("capability.policy.default.Window.frameElement.get","allAccess"); 
				profile.setAcceptUntrustedCertificates(true); 
				profile.setAssumeUntrustedCertificateIssuer(true); 

				//Capabilities of Mozilla
				caps=new DesiredCapabilities();
				caps = DesiredCapabilities.firefox();
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				caps.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
				caps.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				caps.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
				caps.setCapability(CapabilityType.ROTATABLE, true);
				caps.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, true);
				caps.setPlatform(Platform.VISTA);
				caps.setJavascriptEnabled(true);

				if(GridOption.equalsIgnoreCase("No")){
					System.setProperty("webdriver.firefox.bin", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
					driver = new FirefoxDriver(caps);
				}else{
					driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),caps);
					driver = new Augmenter().augment(driver);
					Manager.setWebDriver(driver);
					driver=Manager.getDriver();
					System.out.println("Thread id = " + Thread.currentThread().getId());
				}

			}else if(BrowserName.equalsIgnoreCase("Chrome")){
				System.out.println("Setting options for chrome browser");
				//****ChromeOtpions is used to disable chrome extensions
				ChromeOptions options = new ChromeOptions();
				options.addArguments("chrome.switches","--disable-extensions");
				options.addArguments("--disable-popup-blocking");

				//Capabilities of Chrome
				caps= new DesiredCapabilities();
				caps = DesiredCapabilities.chrome();
				caps.setCapability(ChromeOptions.CAPABILITY, options);
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				caps.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				caps.setPlatform(Platform.VISTA);
				caps.setJavascriptEnabled(true);

				if(GridOption.equalsIgnoreCase("No")){
					System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"\\resources\\chromedriver.exe");
					driver = new ChromeDriver(caps);
				}else{
					//dr = new ThreadLocal<WebDriver>();
					driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),caps);
					driver = new Augmenter().augment(driver);
					Manager.setWebDriver(driver);
					driver=Manager.getDriver();
					System.out.println("Thread id = " + Thread.currentThread().getId());
				}

			}else if(BrowserName.equalsIgnoreCase("IE")){

				//Capabilities of IE
				caps =new DesiredCapabilities();
				caps.setBrowserName("internet explorer");
				caps.setCapability("ignoreZoomSetting", true);
				caps.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
				caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
				caps.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
				caps.setPlatform(Platform.VISTA);
				caps.setJavascriptEnabled(true);

				//Programmatically disabling IE pop up block option
				String cmd = "REG ADD \"HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\New Windows\" /F /V \"PopupMgr\" /T REG_SZ /D \"no\"";
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (Throwable e) {
					System.out.println("Error ocured!");
				}

				if(GridOption.equalsIgnoreCase("No")){
					System.setProperty("webdriver.ie.driver",System.getProperty("user.dir")+"\\resources\\IEDriverServer.exe");
					driver = new InternetExplorerDriver(caps);
					testParameters.setBrowser(Browser.InternetExplorer);

				}else{
					//dr = new ThreadLocal<WebDriver>();
					driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),caps);
					driver = new Augmenter().augment(driver);
					Manager.setWebDriver(driver);
					driver=Manager.getDriver();

					System.out.println("Thread id = " + Thread.currentThread().getId());
				}
			}else if(BrowserName.equalsIgnoreCase("Safari")){

				//Capabilities of Safari
				caps =new DesiredCapabilities();
				caps.setBrowserName("safari");
				caps.setCapability("ignoreZoomSetting", true);
				caps.setPlatform(Platform.MAC);

				if(GridOption.equalsIgnoreCase("No")){
					System.setProperty("webdriver.safari.driver",System.getProperty("user.dir")+"\\resources\\IEDriverServer.exe");
					driver = new SafariDriver(caps);
				}else{
					driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),caps);
					driver = new Augmenter().augment(driver);
					Manager.setWebDriver(driver);
					driver=Manager.getDriver();
					System.out.println("Thread id = " + Thread.currentThread().getId());
				}
			}
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.manage().deleteAllCookies();
			System.out.println("Driver created");
		}catch(Throwable e){
			Assert.fail("Not able to open browser "+ e.getMessage());
			e.printStackTrace();
		}
	}

	//Author: 		Magdoon Basha M
	//Description: 	Method to set the current device state
	public void SetCurrentDevice(String deviceType, String deviceName, String appType, String appName, String browser, String appURL) throws Exception {
		try {
			String androidPrefix = "adb:" + deviceName;
			String iosPrefix = "ios_app:" + deviceName;

			if (deviceType.equalsIgnoreCase("ANDROID") && appType.equalsIgnoreCase("NATIVE")) {
				mobileWebDriver.setDevice(androidPrefix);
				mobileWebDriver.application(appName).launch(true, true);
			}else if(deviceType.equalsIgnoreCase("ANDROID") && appType.equalsIgnoreCase("WEB")) {
				mobileWebDriver.setDevice(androidPrefix);
				mobileWebDriver.application(browser+":" + appURL).launch(true, true);
			}else if(deviceType.equalsIgnoreCase("IOS") && appType.equalsIgnoreCase("NATIVE")) {
				mobileWebDriver.setDevice(iosPrefix);
				//				client.launch(appName, true, true);

				//				mobileWebDriver.application(appName).launch(true, true);
			}else if(deviceType.equalsIgnoreCase("IOS") && appType.equalsIgnoreCase("WEB")) {
				mobileWebDriver.setDevice(iosPrefix); 
				mobileWebDriver.application(browser+":" + appURL).launch(true, true);
			}

			Application_logger.debug(appName +" Application Launched successfully in "+ deviceName);
			System.out.println(appName +" Application Launched successfully in "+ deviceName);
			report.updateTestLog("Verfiy app launched", appName +" Application Launched successfully in "+ deviceName, Status.PASS);
		}catch(Exception e) {
			Application_logger.debug("Error found while launching " + appName + " application in " + deviceName);
			report.updateTestLog("Verfiy app launched", appName +" Application not Launched successfully in "+ deviceName, Status.FAIL);
			throw new Exception(e.getMessage());
		}
	}



	void passControlToNewTab() {
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(allTabs.get(1));
	}
	void passControlToOldTab() {
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(allTabs.get(0));
	}
	void switchToProcessFrame() {
		//Switching frmae
		WebElement fr = driver.findElement(By.name("vod_iframe"));
		driver.switchTo().frame(fr);
		System.out.println("Process frame switched"); 
	}


	public boolean swipeDownWhileElementPresent(String element, int swipeCount) {
		if(client.swipeWhileNotFound("Down", 0, 2000, "NATIVE", prop.getProperty(element), 0, 1000, swipeCount, false)){
			return true;
		}else {
			return false;
		}
	}

	public boolean swipeTopWhileElementPresent(String element, int swipeCount) {
		if(client.swipeWhileNotFound("Top", 0, 2000, "NATIVE", prop.getProperty(element), 0, 1000, swipeCount, false)){
			return true;
		}else {
			return false;
		}
	}


	public void pageRefresh() {

		try {
			driver.navigate().refresh();
			Thread.sleep(5000);
			System.out.println("Page Refreshed");
		} catch (InterruptedException e) {
			System.out.println("Error while refreshing page");
			e.printStackTrace();
		}
	}




	//**********************   Web Application Methods   ******************
	public void verifyLinkNewWindow(String propertyType, String propertyValue,  String clickedElemName,  String url, String pageName  ) throws InterruptedException {
		//		String curentTab = driver.getWindowHandle();
		waitAndClickElement(propertyType, propertyValue);

		Thread.sleep(2000);
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		//		allTabs.remove(curentTab);
		driver.switchTo().window(allTabs.get(1));

		String currentURL = null;
		for (int i=1000; i<=15000; i=i+1000){
			Thread.sleep(i);
			currentURL = driver.getCurrentUrl();
			if (currentURL.contains(prop.getProperty(url))) {
				break;
			}
		}

		if (currentURL.contains(prop.getProperty(url))) {
			//			ATUReports.add("\""+pageName + "\" page opened in new tab successfully after clicking " + "\""+clickedElemName+ "\"", LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			System.out.println("\""+pageName + "\" page opened in new tab successfully after clicking " + "\""+clickedElemName+ "\"");
		} else {
			//			ATUReports.add("\""+pageName + "\" page not opened after clicking " + "\""+clickedElemName+ "\"", LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			System.out.println("\""+pageName + "\" page not opened after clicking " + "\""+clickedElemName+ "\"");
		}
	}
	public void verifyLink(String propertyType, String propertyValue,  String clickedElemName,  String url, String pageName  ) throws InterruptedException {

		waitAndClickElement(propertyType, propertyValue);
		//		System.out.println("url: "+ prop.getProperty(url));
		String currentURL = null;
		for (int i=1000; i<=15000; i=i+1000){
			Thread.sleep(i);
			currentURL = driver.getCurrentUrl();
			//			System.out.println("currentURL: "+ currentURL);
			if (currentURL.contains(prop.getProperty(url))) {
				break;
			}
		}

		System.out.println("Final currentURL: "+ currentURL);
		if (currentURL.contains(prop.getProperty(url))) {
			//			ATUReports.add("\""+pageName + "\" page opened successfully after clicking " + "\""+clickedElemName+ "\"", LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			System.out.println("\""+pageName + "\" page opened successfully after clicking " + "\""+clickedElemName+ "\"");
		} else {
			//			ATUReports.add("\""+pageName + "\" page not opened after clicking " + "\""+clickedElemName+ "\"", LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			System.out.println("\""+pageName + "\" page not opened after clicking " + "\""+clickedElemName+ "\"");
		}
	}
	public WebElement waitAndGetElement(String ByType, String ByValue) {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		WebElement webElement = null;

		if (ByType.equalsIgnoreCase("xpath")) {
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty(ByValue))));
			if(webElement!=null) { 
				JavascriptExecutor jse = (JavascriptExecutor)driver;
				jse.executeScript("arguments[0].scrollIntoView();", webElement);
			}
		}else if (ByType.equalsIgnoreCase("link")) {
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(prop.getProperty(ByValue))));
			if(webElement!=null) { 
				JavascriptExecutor jse = (JavascriptExecutor)driver;
				jse.executeScript("arguments[0].scrollIntoView();", webElement);
			}
		}else if (ByType.equalsIgnoreCase("css")) {
			webElement =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty(ByValue))));
			if(webElement!=null) { 
				JavascriptExecutor jse = (JavascriptExecutor)driver;
				jse.executeScript("arguments[0].scrollIntoView();", webElement);
			}
		}else if (ByType.equalsIgnoreCase("id")) {
			webElement =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(prop.getProperty(ByValue))));
			if(webElement!=null) { 
				JavascriptExecutor jse = (JavascriptExecutor)driver;
				jse.executeScript("arguments[0].scrollIntoView();", webElement);
			}
		}


		return webElement;
	}

	public void dropDownSelection(WebElement webElement, String selectionType, String value) {
		Select select = new Select(webElement);
		if (selectionType.equalsIgnoreCase("visibleText")) {
			select.selectByVisibleText(value);
		}else if (selectionType.equalsIgnoreCase("value")) {
			select.selectByValue(value);
		}else if (selectionType.equalsIgnoreCase("index")) {
			select.selectByIndex(Integer.parseInt(value));
		}		
	}

	public void switchToIframe(String byValue) throws TimeoutException {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		By by = By.xpath(prop.getProperty(byValue));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));

	}

	public void waitAndClickElement(String ByType, String ByValue) {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		WebElement webElement = null;

		if (ByType.equalsIgnoreCase("xpath")) {
			webElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty(ByValue))));
		}else if (ByType.equalsIgnoreCase("link")) {
			webElement = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(prop.getProperty(ByValue))));
		}else if (ByType.equalsIgnoreCase("css")) {
			webElement =  wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(prop.getProperty(ByValue))));
		}else if (ByType.equalsIgnoreCase("id")) {
			webElement =  wait.until(ExpectedConditions.elementToBeClickable(By.id(prop.getProperty(ByValue))));
		}
		if (webElement != null) {
			JavaScriptExecutor(webElement);
		}
	}

	public void JavaScriptExecutor(Object element){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("arguments[0].scrollIntoView();", element);
		jse.executeScript("arguments[0].click();", element);
	}

	public void moveToElement(String ByType, String ByValue) {
		//		Actions action = new Actions(driver);
		WebElement element = waitAndGetElement(ByType, ByValue);
		//		action.moveToElement(element).build().perform();
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("arguments[0].scrollIntoView();", element);
	}

	public void selectElementFromList(String zone, String listXpath, String element, int index, int swipeCount, String elementName) throws Exception { 
		if(client.elementSwipeWhileNotFound(zone, prop.getProperty(listXpath), "Down", 25, 1000, "NATIVE", prop.getProperty(element), index, 2000, swipeCount, false)){
			//			if(client.swipeWhileNotFound("Down", 0, 2000, "NATIVE", userXpath, 0, 1000, 5, true)) {
			System.out.println(elementName+" element selected from list successfully");
		}else {
			System.out.println(elementName+" element not selected from list");
			throw new Exception(elementName+" element not selected from list");
		}
	}

	
	public void takeScreenshot(String fileName) throws Exception 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH-mm-ss a");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timeStampStr = sdf.format(timestamp);
		System.out.println(timeStampStr);

		//		String resultPath = prop.getProperty("SeleniumScreenshotPath");
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File(seleniumOPruntimePath+"\\"+timeStampStr+"_"+fileName+".jpg"));
	}

	//Description: 	Method to launch application in device
	public void launchApplication(String deviceType, String deviceName, String appType, String appName, String browser, String appURL) throws Exception {
		try {
			String androidPrefix = "adb:" + deviceName;
			String iosPrefix = "ios_app:" + deviceName;
			System.out.println(deviceName);
			client.waitForDevice("@name= '"+deviceName+"'", 30000);
			//				                      client.setDevice("ios_app:"+prop.getProperty("IphoneDevice"));
			client.openDevice();
			if (deviceType.equalsIgnoreCase("ANDROID") && appType.equalsIgnoreCase("NATIVE")) {
				mobileWebDriver.setDevice(androidPrefix);
				mobileWebDriver.application(appName).launch(true, true);
			}else if(deviceType.equalsIgnoreCase("ANDROID") && appType.equalsIgnoreCase("WEB")) {
				mobileWebDriver.setDevice(androidPrefix);
				mobileWebDriver.application(browser+":" + appURL).launch(true, true);
			}else if(deviceType.equalsIgnoreCase("IOS") && appType.equalsIgnoreCase("NATIVE")) {
				mobileWebDriver.setDevice(iosPrefix);
				//client.applicationClose(appName);
				//Thread.sleep(9000);
				//				client.clearDeviceLog();
				//				String query = "@added='false' and @location='US' and @manufacture='Apple' and @os='iOS' and @version='10.2.1' and @remote='true'";
				//				client.waitForDevice(query, 30000);
				//				client.openDevice();
				//				client.deviceAction("Landscape");
				//				Thread.sleep(3000);
				//				client.applicationClearData(appName);
				client.launch(appName,true, true);

				//				mobileWebDriver.application(appName).launch(true, true);
			}else if(deviceType.equalsIgnoreCase("IOS") && appType.equalsIgnoreCase("WEB")) {
				mobileWebDriver.setDevice(iosPrefix);
				//				mobileWebDriver.manage().window().maximize();
				//				client.maximize();
				//					client.hybridClearCache(true, true); //commented newly
				mobileWebDriver.application(browser+":" + appURL).launch(true, true);
				client.hybridWaitForPageLoad(15000);
			}

			Application_logger.debug(appName +" Application Launched successfully in "+ deviceName);
			System.out.println(appName +" Application Launched successfully in "+ deviceName);
			report.updateTestLog("Verfiy app launched", appName +" Application Launched successfully in "+ deviceName, Status.PASS);
		}catch(Exception e) {
			Application_logger.debug("Error found while launching " + appName + " application in " + deviceName);
			report.updateTestLog("Verfiy app launched", appName +" Application not Launched successfully in "+ deviceName, Status.FAIL);
			throw new Exception("Error while launching app "+e.getMessage());
		}
	}


	
	

}
