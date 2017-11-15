package com.framework.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.framework.utility.HtmlReport;
import com.framework.utility.TestResult;

public class HtmlReport {

	private static HtmlReport htmlReport;
	int passedTestCount = 0;
	int failedTestCount = 0;
	int skipedTestCount = 0;
	TestResult testResult;
	private static List<TestResult> indTestResult = new ArrayList<TestResult>();;
	
	//Function to create and return instance of HtmlReport class
	public static HtmlReport getInstance()
	{
		
		if (htmlReport == null) {					//Verify instance already created
			htmlReport = new HtmlReport();
//			System.out.println("html ins created");
		}
//		System.out.println("html ins returned");
		return htmlReport;
		
	}
	
	//Function to initialize ArrayList
	public void initilize(){
		System.out.println("initilizing html");
//		indTestResult = new ArrayList<TestResult>();
	}
	
	//Function to add individual test result instance to ArrayList
	public void addResult(org.testng.ITestResult iTestResult){
		System.out.println("browser name : " + Constants.BROWSER_VAL);
		//System.out.println("Reasong filaure : " + Reporter.() );
		String result="";
		String startDate="";
		String endDate="";
		String duration;
		String testName;
		Boolean isSuccess;
		String className;
		
		className = iTestResult.getClass().getName();
		testName = iTestResult.getMethod().getMethodName();
		isSuccess = iTestResult.isSuccess();
		
		
		if (iTestResult.getStatus() == ITestResult.SUCCESS) {
			result = "Pass";
			passedTestCount++;
		}
		else if ( iTestResult.getStatus() == ITestResult.FAILURE){
			result = "Fail";
			failedTestCount++;
		}
		else if ( iTestResult.getStatus() == ITestResult.SKIP){
			result = "Skipped";
			skipedTestCount++;
		}
	
		
		Date date1=new Date(iTestResult.getStartMillis());
		Date date2=new Date(iTestResult.getEndMillis());
		
		long diff =  date2.getTime() - date1.getTime(); // modified - vinoth
		long diffSeconds = diff / 1000 % 60;  
		long diffMinutes = diff / (60 * 1000) % 60;         
		long diffHours = diff / (60 * 60 * 1000);   
//		duration = diffHours+" Hr "+diffMinutes+" Min "+diffSeconds+" Sec "+diff+" MSec ";
		duration = diffHours+" Hr "+diffMinutes+" Min "+diffSeconds+" Sec ";
		
		testResult = new TestResult();
		testResult.setTestName(testName);
		testResult.setStartDate(startDate);
		testResult.setEndDate(endDate);
		testResult.setDuration(duration);
		testResult.setResult(result);
		testResult.setClassName(className);
	
		indTestResult.add(testResult);		//Adding testResult instance into List
		
	}
	
	//Function to populate overall Statistical Result
	 	String populateStatisticalResult(){
		String head1="Overall Summary";
		String head2="Count";
		String totTC = "Total Test Cases";
		String totPassed = "Passed Tests";
		String totfailed = "Failed Tests";
		String totSkipped = "Skipped Tests";
		int totTcCount = passedTestCount + failedTestCount + skipedTestCount; 
		String row1, row2, row3, row4;
		String statTable;
		
		//Table style
		String statTablePre = "<table style=\"height: 97px; margin-left: auto; margin-right: auto;\" width=\"357\">";
		String statTableSuf = "</table>";
		//Table head style
		String thead = "<thead><tr style=\"background-color: #1b4f72;\"><td style=\"text-align: center;\"><strong><span style=\"color: #ffffff;\">"+head1+"</span></strong></td><td style=\"text-align: center;\"><strong><span style=\"color: #ffffff;\">"+ head2+ "</span></strong></td></tr></thead>";
		//Table Row style
		String tRowPre = "<tr style=\"background-color: #aed6f1;\">";
		String tRowSuf = "</tr>";
		//Usual cell data style
		String tdataPre = "<td style=\"padding-left: 30px;\"><strong><span style=\"color: #800000;\">";
		String tdataSuf = "</span></strong></td>";
		//Result cell data style
		String tdataPre2 ="<td style=\"background-color: #9b59b6; text-align: center;\"><span style=\"color: #ffffff;\"><strong>";
		String tdataPre3 ="<td style=\"background-color: #239b56; text-align: center;\"><span style=\"color: #ffffff;\"><strong>";
		String tdataPre4 ="<td style=\"background-color: #e74c3c; text-align: center;\"><span style=\"color: #ffffff;\"><strong>";
		String tdataPre5 ="<td style=\"background-color: #dc7633; text-align: center;\"><span style=\"color: #ffffff;\"><strong>";
		String tdataSuf2 = "</strong></span></td>";
		
		//Creating rows
		row1 =  tRowPre + tdataPre + totTC + tdataSuf + tdataPre2 + totTcCount +  tdataSuf2 + tRowSuf;
		row2 =  tRowPre + tdataPre + totPassed + tdataSuf + tdataPre3 + passedTestCount +  tdataSuf2 + tRowSuf;
		row3 =  tRowPre + tdataPre + totfailed + tdataSuf + tdataPre4 + failedTestCount +  tdataSuf2 + tRowSuf;
		row4 =  tRowPre + tdataPre + totSkipped + tdataSuf + tdataPre5 + skipedTestCount +  tdataSuf2 + tRowSuf;
		
		//Merging rows and head to the table
		statTable = statTablePre + thead + row1 + row2 + row3 + row4 + statTableSuf;
		
		return statTable;
	}
	
	//Function to create string object of individual test results
	String populateIndResult(){
		String serialNumHead = "S.No";
		String AppicationName= "Application Name";
		String browserName = "Browser Name";
		String tcNameHead = "Test Name";
		String durationHead = "Duration";
		String resultHead = "Result";
		StringBuilder tRow = new StringBuilder();
		String testName;
		String duration;
		String result;
		int serialnum = 0;
		String resultTD = "";
		String row;
		String tdataPre;
		String tdataSuf;
		String appName;
		String browsername;
		
		
		//Table style
		String statTablePre = "<table style=\"margin-left: auto; margin-right: auto;\" >"; //width=\"100%\">";
		String statTableSuf = "</table>";
		//Table Head style
		String theadPre = "<thead><tr style=\"background-color: #9b59b6; text-align: center; vertical-align: middle; \">";
		String theadSuf = "</tr></thead>";
		//Table Head data style 
		String tHeadData1 = "<td width=\"50\"  height=\"30\" style=\"text-align: center; \"><h3><strong><span style=\"color: #ffffff;\">"+ serialNumHead +"</span></strong></h3></td>";
		String tHeadData2 = "<td width=\"150\" style=\"text-align: center; \"><h3><strong><span style=\"color: #ffffff;\">"+ AppicationName +"</span></strong></h3></td>";
		String tHeadData3 = "<td width=\"125\"style=\"text-align: center; \"><h3><strong><span style=\"color: #ffffff;\">"+ browserName + "</span></strong></h3></td>";
		String tHeadData4 = "<td width=\"450\" style=\"text-align: center; \"><h3><strong><span style=\"color: #ffffff;\">"+ tcNameHead +"</span></strong></h3></td>";
		String tHeadData5 = "<td width=\"150\" style=\"text-align: center; \"><h3><strong><span style=\"color: #ffffff;\">"+ durationHead + "</span></strong></h3></td>";
		String tHeadData6 = "<td width=\"150\"style=\"text-align: center; \"><h3><strong><span style=\"color: #ffffff;\">"+ resultHead + "</span></strong></h3></td>";
		//Creating table head row
		String thead = theadPre + tHeadData1 + tHeadData2 + tHeadData3 + tHeadData4 + tHeadData5 + tHeadData6 + theadSuf;
		
		//Usual row style
		String tRowPre = "<tr style=\"background-color: #aeb6bf;\">";
		String tRowSuf = "</tr>";
				
		//Adding table head to string builder
		tRow.append(thead);		
		
		//Iterating TestResult List
		for (TestResult TestResult : indTestResult){
			testName = TestResult.getTestName();
			duration = TestResult.getDuration();
			result = TestResult.getResult();
			appName = TestResult.getClassName();
			browsername = Constants.BROWSER_VAL; // Browser Name
//			result="Skipped";
			serialnum++;				//Serial number generating
			
			//Changing background color of Cell Data based on test result
			if (result == "Pass"){					
				tdataPre = "<td style=\"background-color: #239b56; text-align: center; \"><span style=\"color: #ffffff;\"><strong>";
				tdataSuf = "</strong></span></td>";
				resultTD = tdataPre+result+tdataSuf;
			}else if (result == "Fail"){
				tdataPre = "<td style=\"background-color: #e74c3c; text-align: center;\"><span style=\"color: #ffffff;\"><strong>";
				tdataSuf = "</strong></span></td>";
				resultTD = tdataPre+result+tdataSuf;
			}else if (result == "Skipped"){
				tdataPre = "<td style=\"background-color: #dc7633; text-align: center;\"><span style=\"color: #ffffff;\"><strong>";
				tdataSuf = "</strong></span></td>";
				resultTD = tdataPre+result+tdataSuf;
				browsername = "NA";
			}
			
			
			//Usual data cell style
			tdataPre = "<td style=\"padding-left: 60px;\"><strong>";
			tdataSuf = "</strong></td>";
			
			String tdataPre1 = "<td style=\"text-align: center;\"><strong>";
			String tdataSuf1 = "</strong></td>";
			
			//Creating table row
			row = tRowPre+tdataPre1+serialnum+tdataSuf1+tdataPre+appName+tdataSuf+ tdataPre+ browsername +tdataSuf + tdataPre+testName+tdataSuf+tdataPre+duration+tdataSuf+resultTD+tRowSuf;
			tRow.append(row);		//Appending row into String builder(Table head already added)
			
		}
		
		return statTablePre + tRow.toString() + statTableSuf;
	}
	
	//Function to create string object of Main Title
	String populateMainTitle(){
		String title= "Veeva IRep Automation Test Execution Result Summary";
		
		return "<h1 style=\"text-align: center;\"><span style=\"color: #7B241C;\"><strong><u>" + title + "</u></strong></span></h1>";
	}
	
	//Function to create string object of sub Title
	String populateSubTitle(){
		String title= "Detailed Test Results";
		
		return "<h2 style=\"text-align: center;\"><span style=\"color: #7B241C;\"><strong><u>" + title + "</u></strong></span></h2>";
	}
	
	//Function to generate HTML file using String object
	public void generateHTML(String path){
		System.out.println("gen Html");
		String htmlPre = "<html><body bgcolor=\"#EBF5FB\">";
		String htmlSuf = "</body></html>";
		String htmlString;
			
		//Creating complete Html String by getting Strings from Methods
		htmlString = htmlPre + populateMainTitle() + populateStatisticalResult() + populateSubTitle() + populateIndResult() + htmlSuf;
		System.out.println("Html String: "+ htmlString );
		System.out.println("Mid");
		//Creating a File in a given path
//		File htmlTemplateFile = new File(path);
	    try {	
	    	System.out.println("path: "+ path);
				FileUtils.writeStringToFile(new File(path), htmlString);	//Writing HTML String in to File
				System.out.println("sd");
			} catch (IOException e) {
			
				e.printStackTrace();
			}
	    System.out.println("End");
	}
}
