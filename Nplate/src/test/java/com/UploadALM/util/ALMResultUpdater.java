package com.UploadALM.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import com.UploadALM.rest.Constants;
import com.UploadALM.rest.EntityUpdater;

public class ALMResultUpdater {

    private static Properties properties = Settings.getInstance();

    /*
     * static String testSetID;
     * static EntityUpdater restObj= new EntityUpdater(Constants.DOMAIN,
     * Constants.PROJECT); private static String run_ID;
     */

    String testSetID;

    static EntityUpdater restObj = new EntityUpdater(Constants.DOMAIN, Constants.PROJECT);
    private String run_ID;

    public void connect() {
        try {
            System.setProperty(
                    "org.xml.sax.driver",
                    "com.sun.org.apache.xerces.internal.parsers.SAXParser");
            System.setProperty(
                    "javax.xml.parsers.DocumentBuilderFactory",
                    "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            System.setProperty(
                    "javax.xml.parsers.SAXParserFactory",
                    "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
            restObj.loginCall();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Boolean updateStatus(
            String testName, String status, Integer duration, String testSetID) {

        ALMResultUpdater almResultUpdater = new ALMResultUpdater();
        almResultUpdater.connect();
        testName =  Constants.HPALMTESTNAME.replace(" ", "%20") ;
        //testName = "Regression.alm_demo$sampletest_old" ;
        System.out.println(testName);
        almResultUpdater.createRun(
                testName,
                status,
                new Date(),
                duration.toString(),
                properties.getProperty("HPALMUserName"),
                testSetID);
        return true;
    }

    @Test
    public void testMethod() {
        updateStatus(
                "Computer Science URLs",
                "Passed",
                10,
                "1289");
    }

    public synchronized void createRun(
            String testCaseName, String testCaseStatus, Date duration, String executionTime,
            String userName, String testSetID) {
        String testInstance = null;
        String testId = null;
        if (testSetID == "" || testSetID == null) {
            this.testSetID = properties.getProperty("HPALMTestSet").trim();
        } else {
            this.testSetID = testSetID;
        }
        String[] testIdArray = getTestID(testCaseName);

        for (int i = 0; i < testIdArray.length; i++) {
            testInstance = getTestInstance(testIdArray[i], this.testSetID);
            if (testInstance != null) {
                testId = testIdArray[i];
                break;
            }
        }
        System.out.println("test id: " + testId);
        String responseXMLToGetRunID = restObj.createEntity(
                "run",
                "name",
                "Run:" + duration,
                testInstance,
                testId,
                userName,
                "Not Completed",
                this.testSetID);
        System.out.println("response " + responseXMLToGetRunID );
        run_ID = StringUtils.substringBetween(
                responseXMLToGetRunID,
                "<Field Name=\"id\"><Value>",
                "</Value></Field>");
        String attachmentPath = System.getProperty("user.dir") + "\\" + Constants.ATTACHMENT + "\\" + com.UploadALM.rest.Constants.ALMTestName + ".zip";
        //attachmentPath.replace("#", "%23");
        System.out.println("Attachment Path for ALM Upload --" + attachmentPath);
        updateAttachment(attachmentPath, testCaseName);
        restObj.updateRunStatus(
                "run",
                run_ID,
                "status",
                testCaseStatus,
                "duration",
                executionTime.split("minute")[0]);
    }

    public synchronized void updateAttachment(String attachmentPath, String testCaseName) {
        byte[] filedata;
        filedata = convertTobytes(attachmentPath);
        restObj.CreateTCAttachment(run_ID, filedata, testCaseName + ".zip");
    }

    public byte[] convertTobytes(String attachPath) {
        File file = new File(attachPath);
        byte[] b = new byte[(int) file.length()];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }

        return b;

    }

    public synchronized void disconnect() {
        try {
            restObj.logoutCall();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* Function to get test instances and test id */
    public static HashMap<String, String> getTestInstances(EntityUpdater rest, String testSetID) {
        String responseXml = rest.getEntity(
                "test-instance",
                null,
                "contains-test-set.id[" + testSetID + "]");

        String[] testInstanceIDs = StringUtils.substringsBetween(
                responseXml,
                "<Field Name=\"id\"><Value>",
                "</Value></Field>");

        String[] testIDs = StringUtils.substringsBetween(
                responseXml,
                "<Field Name=\"test-id\"><Value>",
                "</Value></Field>");

        HashMap<String, String> testInstanceID = new HashMap<String, String>();

        int testcount = testInstanceIDs.length;

        for (int i = 0; i < testcount; i++) {
            testInstanceID.put(testInstanceIDs[i], testIDs[i]);
        }

        return testInstanceID;

    }

    /* Function to get test instance based on test Id and test Set */
    public synchronized String getTestInstance(String testId, String testSetID) {
        String responseXML = restObj.getEntity(
                "test-instance",
                null,
                "cycle-id[" + testSetID + "];test-id[" + testId + "]");
        String testInstanceId = StringUtils.substringBetween(
                responseXML,
                "<Field Name=\"id\"><Value>",
                "</Value></Field>");

        return testInstanceId;
    }

    /* Function to get test id from test Name */
    public synchronized String[] getTestID(String testName) {
        String startValue = "<Field Name=\"id\"><Value>";
        String endValue = "</Value></Field>";
        testName = testName.replace("#", "%23");
        String responseXML = restObj.getEntity("test", null, "name['" + testName + "']");
        System.out.println(responseXML);
        String[] array = new String[StringUtils.countMatches(responseXML, startValue)];
        Pattern pattern = Pattern.compile(
                Pattern.quote(startValue) + "(.*?)" + Pattern.quote(endValue));
        Matcher matcher = pattern.matcher(responseXML);
        int count = 0;
        while (matcher.find()) {
            array[count] = matcher.group(1);
            count++;
        }
        return array;
    }

}
