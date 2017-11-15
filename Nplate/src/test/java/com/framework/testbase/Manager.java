package com.framework.testbase;
 
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
 
public class Manager {
    private static ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();
 
    public static WebDriver getDriver() {
        return webDriver.get();
    }
 
    static void setWebDriver(WebDriver driver) {
        webDriver.set(driver);
        
    }
}