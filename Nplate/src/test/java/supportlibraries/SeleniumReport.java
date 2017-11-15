/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.cognizant.framework.FrameworkException
 *  com.cognizant.framework.Report
 *  com.cognizant.framework.ReportSettings
 *  com.cognizant.framework.ReportTheme
 *  org.apache.commons.io.FileUtils
 *  org.openqa.selenium.Capabilities
 *  org.openqa.selenium.OutputType
 *  org.openqa.selenium.TakesScreenshot
 *  org.openqa.selenium.WebDriver
 *  org.openqa.selenium.remote.Augmenter
 *  org.openqa.selenium.remote.RemoteWebDriver
 */
package supportlibraries;

import com.cognizant.framework.FrameworkException;

import com.cognizant.framework.Report;
import com.cognizant.framework.ReportSettings;
import com.cognizant.framework.ReportTheme;
import com.experitest.client.Client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumReport
extends Report {
	private WebDriver driver;
	private Client client;
	private String execMode;

	public void setExecMode(String execMode){
		this.execMode = execMode;
	}

	public String getExecMode(){
		return execMode;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public SeleniumReport(ReportSettings reportSettings, ReportTheme reportTheme) {
		super(reportSettings, reportTheme);
	}

	protected void takeScreenshot(String screenshotPath) {
		//System.out.println("Sel report screenshotPath: "+screenshotPath);
		File scrFile = null;
		if(execMode.equalsIgnoreCase("web")){
			//System.out.println("Taking web sc");
			if (this.driver == null) {
				throw new FrameworkException("Report.driver is not initialized!");
			}
			if (this.driver.getClass().getSimpleName().equals("HtmlUnitDriver") || this.driver.getClass().getGenericSuperclass().toString().equals("class org.openqa.selenium.htmlunit.HtmlUnitDriver")) {
				return;
			}
			if (this.driver.getClass().getSimpleName().equals("RemoteWebDriver")) {
				Capabilities capabilities = ((RemoteWebDriver)this.driver).getCapabilities();
				if (capabilities.getBrowserName().equals("htmlunit")) {
					return;
				}
				WebDriver augmentedDriver = new Augmenter().augment(this.driver);
				scrFile = (File)((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
			} else {
				scrFile = (File)((TakesScreenshot)this.driver).getScreenshotAs(OutputType.FILE);
				//System.out.println(scrFile.getAbsolutePath());
			}
			//System.out.println(scrFile.getAbsolutePath());
		}else if(execMode.equalsIgnoreCase("mobile")){
			//System.out.println("Taking mob sc");
			scrFile = new File(client.capture());
			int i = 0;
			while (!scrFile.exists()) {
				try {
					Thread.sleep(1000);
					i++;
					if (i > 30) {
						break;
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

		try {
			FileUtils.copyFile(scrFile, new File(screenshotPath), true);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException(
					"Error while writing screenshot to file");
		}

		try {
			FileUtils.copyFile((File)scrFile, (File)new File(screenshotPath), (boolean)true);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException("Error while writing screenshot to file");
		}
	}
}

