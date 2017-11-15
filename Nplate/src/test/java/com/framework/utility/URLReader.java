package com.framework.utility;

import com.framework.testbase.TestBase;

public class URLReader extends TestBase{
	/*public static String getURL(String testEnvironmnet, String testName) {
		Xls_ReaderForUrl xls = null;
		if(testEnvironmnet.equalsIgnoreCase("Regression")) {
			System.out.println(System.getProperty("user.dir") + "\\src\\test\\java\\com\\digicomm\\datatables\\"
					+ Constants.REGRESSION_URL_SUITE + ".xlsx");
			xls = new Xls_ReaderForUrl(System.getProperty("user.dir") + "\\src\\test\\java\\com\\digicomm\\datatables\\"
					+ Constants.REGRESSION_URL_SUITE + ".xlsx");
		}else if(testEnvironmnet.equalsIgnoreCase("Production")) {
			xls = new Xls_ReaderForUrl(System.getProperty("user.dir") + "\\src\\test\\java\\com\\Digicomm\\datatables\\"
					+ Constants.PRODUCTION_URL_SUITE + ".xlsx");
			System.out.println(System.getProperty("user.dir") + "\\src\\test\\java\\com\\Digicomm\\datatables\\"
					+ Constants.PRODUCTION_URL_SUITE + ".xlsx");
		}

		int row = xls.getCellRowNum(Constants.TEST_ENVIRONMENT_COL, Constants.TESTCASE_COL, testName);
		System.out.println("row :"+row);
		String url = xls.getCellData(Constants.TEST_ENVIRONMENT_COL, Constants.ENV_URL_COL, row);
		return url;

	}*/

	public static String getDeviceName(String testName) {
		Xls_ReaderForUrl xls = null;

		System.out.println(System.getProperty("user.dir") + "\\src\\test\\java\\com\\IRep\\datatables\\"
				+ Constants.SUITE_SHEET + ".xlsx");
		xls = new Xls_ReaderForUrl(System.getProperty("user.dir") + "\\src\\test\\java\\com\\IRep\\datatables\\"
				+ Constants.SUITE_SHEET + ".xlsx");

		int row = xls.getCellRowNum(Constants.SUITE_SHEET, Constants.SUITENAME_COL, testName);
		System.out.println("row :"+row);
		String deviceName = xls.getCellData(Constants.SUITE_SHEET, Constants.DEVICE_NAME__COL, row);
		return deviceName;

	}
}
