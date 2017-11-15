package com.framework.utility;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;

import com.framework.testbase.TestBase;
public class TestDataProvider extends TestBase {
	
//***********Use "parallel=true" in @DataProvider annotation to run the tests in parallel on GRID****************//


	@DataProvider(name = "Nplate")
	public static Object[][] getSpinrazaAffiliation(Method m) {
		Xls_Reader xls = new Xls_Reader(System.getProperty("user.dir") + "\\src\\test\\java\\com\\IRep\\datatables\\"
				+ Constants.NPLATE_SUITE + ".xlsx");
		return Utility.getData(m.getName(), xls);
	}
}
