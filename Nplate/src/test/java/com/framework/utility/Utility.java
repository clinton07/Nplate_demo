package com.framework.utility;

import java.util.Hashtable;

public class Utility {

	//*************Check if suite is marked to run or not***************//
	public static boolean isSuiteRunnable(String SuiteName, Xls_Reader xls){
		
	int rows = xls.getRowCount(Constants.SUITE_SHEET);
	
	for(int rNum= 2; rNum<=rows; rNum++){
		String data = xls.getCellData(Constants.SUITE_SHEET, Constants.SUITENAME_COL, rNum);	
		if(data.equals(SuiteName)){
			String runmode = xls.getCellData(Constants.SUITE_SHEET, Constants.RUNMODE_COL, rNum);
			if(runmode.equals(Constants.RUNMODE_YES))
				return true;
				else
				return false;	
		}
	}
	return false;	// default--in case above code doesnot return anything
}
	//*******************Check if test case is marked to run or not********//
	public static boolean isTestCaseRunnable(String TestCaseName, Xls_Reader xls){
		
		int rows = xls.getRowCount(Constants.TESTCASE_SHEET);
		
		for(int rNum = 2; rNum<=rows;rNum++){
			String data = xls.getCellData(Constants.TESTCASE_SHEET, Constants.TESTCASE_COL, rNum);
			if(data.equals(TestCaseName)){
				String runmode = xls.getCellData(Constants.TESTCASE_SHEET, Constants.RUNMODE_COL, rNum);
				if(runmode.equals(Constants.RUNMODE_YES))
					return true;
				else
					return false;
			}
		}
		return false;
		
	}
	//*******************Reading data from the excel***************//
	public static Object [][] getData(String TestName, Xls_Reader xls){
		
		int rows = xls.getRowCount("Data");
		//System.out.println("There are "+rows+" rows");
		
		//Calculating row number for test case
		int TestRowNum;
		for(TestRowNum= 1; TestRowNum<= rows; TestRowNum++){
			String TestNameXls = xls.getCellData(Constants.DATA_SHEET, 0, TestRowNum);//Constants.DATA_SHEET=data
			if(TestNameXls.equalsIgnoreCase(TestName))
				break;
		}
		//System.out.println("Test starts from row number "+TestRowNum);
		
		int colStartRowNum = TestRowNum +1;
		int dataStartRowNum = TestRowNum +2;
		
		//Number of data rows
		int testrows =1;
		while((!xls.getCellData(Constants.DATA_SHEET, 0, dataStartRowNum+testrows).equals(""))){
			testrows++;
		}
		//System.out.println("Total rows of data are "+testrows);
		
		//Number of data cols
		int testcols = 0;
		while(!(xls.getCellData(Constants.DATA_SHEET, testcols, colStartRowNum)).equals("")){
			testcols++;
		}
		//System.out.println("Total cols of data are "+testcols);
		
//****Printing or extracting all data of selected testcase*******Use of hash table to extract multiple data columns****
		
		Object [][] data= new Object [testrows][1];// creating dynamic array of data
	
		int r=0;
		for(int rNum=dataStartRowNum; rNum<(dataStartRowNum+testrows);rNum++){
			Hashtable <String,String> table = new Hashtable <String,String>();
			for(int cNum=0;cNum<testcols;cNum++){
			table.put(xls.getCellData(Constants.DATA_SHEET, cNum, colStartRowNum), xls.getCellData(Constants.DATA_SHEET, cNum, rNum));
			}
			data[r][0]=table; //******data of one row
			r++; //*****moving to next row
		}
		return data;
	}
}
