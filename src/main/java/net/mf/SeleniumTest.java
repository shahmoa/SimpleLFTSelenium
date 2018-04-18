package net.mf;

import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;


import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import com.hp.lft.sdk.*;
import com.hp.lft.sdk.web.*;
import com.hp.lft.report.*;
import com.hp.lft.verifications.*;
import com.hpe.leanft.selenium.Utils;
import com.hpe.leanft.selenium.ByEach;
import com.hpe.leanft.selenium.By;

public class SeleniumTest  {
    //private static String TEST_URL = "http://www.advantageonlineshopping.com";
    private static String TEST_URL = "http://nimbusserver:8000/";

    public SeleniumTest() {
    //Change this constructor to private if you supply your own public constructor
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        // initialize the SDK and report only once per process
        // this needs to be done since you are using a different framework than LFT.
        // More info at https://admhelp.microfocus.com/leanft/en/14.03/HelpCenter/Content/HowTo/CustomFrameworks.htm
        try{

            ModifiableSDKConfiguration config = new ModifiableSDKConfiguration();
            config.setServerAddress(new URI("ws://localhost:5095"));
            SDK.init(config);

            Reporter.init();
            Reporter.setReportLevel(ReportLevel.All);
            Reporter.setSnapshotCaptureLevel(CaptureLevel.All);
        }
        catch(Exception e){
            System.out.println("ERROR OCCURRED: ");
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        //Generate the report and cleanup the SDK usage.
        Reporter.generateReport();
        SDK.cleanup();
    }

    @Test
    public void test() throws Exception {
        WebElement we;

        // Location of where your chromedriver is locate.
        // If you don't use the setPropery, then you will need to have chromedriver in your system path

        System.setProperty("webdriver.chrome.driver", "./2.36/chromedriver");

        //System.setProperty("webdriver.chrome.driver", "./2.36/chromedriver");
        ChromeOptions co = new ChromeOptions();
        co.addExtensions(new File("/opt/leanft/Installations/Chrome/Agent.crx")); // path to agent on my linux yours may differ
        WebDriver driver = new ChromeDriver(co);

        WebDriverWait w = new WebDriverWait(driver,20);
        w.ignoring(NoSuchElementException.class);
        org.openqa.selenium.WebElement P = null;

        try {
            Reporter.reportEvent("Launch Selenium webdriver", "<b>Using URL:</b><br><h1><a href='"+TEST_URL+"' target='_blank'>"+TEST_URL+"</h1>");

            driver.get(TEST_URL);

            // This line is using the Micro Focus  extension of Selenium through the new attribute 'visibleText'
            // I often find the WebDriverWait to be not very reliable and often I need to put in Thread.sleep ()
            w.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("TABLETS")));  //this is one way to sync on objects in Selenium

            driver.findElement(By.visibleText("TABLETS")).click();


            // This line is using normal Selenium attributes
            Thread.sleep(2000);
            w.until(ExpectedConditions.visibilityOfElementLocated(By.id("accordionPrice")));  //this is one way to sync on objects in Selenium
            we = driver.findElement(By.id("accordionPrice"));
            we.click();

            Utils.highlight(we);
            RenderedImage snapshot = Utils.getSnapshot(we);
            Reporter.reportEvent("Accordion Price", "", Status.Passed,snapshot);

            we = driver.findElement(new ByEach(
                        By.tagName("a"),
                        By.visibleText(Pattern.compile("\\$1,00\\d\\.\\d\\d"))
                        ));

            Verify.areEqual ("$1,009.99", we.getText());
            Verify.areEqual ("$1,009.00", we.getText(), "Price Verification","Verify price displayed matches price expected", Utils.getSnapshot(we));


            // ---------- Switch to having UFT Pro (LeanFT) work on the browser ----------
            // The following will attach LeanFT to the browser opened by Selenium and work using LeanFT libraries
            Reporter.reportEvent("Attach to Browser","Current URL: "+driver.getCurrentUrl());
            Browser browser = BrowserFactory.attach(new BrowserDescription.Builder().url(driver.getCurrentUrl()).build());

            ImageDescription imageDescription = new ImageDescription.Builder()
                    .className("imgProduct")
                    .src(new RegExpProperty(".*image_id=3100"))
                    .tagName("IMG")
                    .type(com.hp.lft.sdk.web.ImageType.NORMAL).build();

            browser.describe(Image.class, imageDescription).highlight();
            browser.describe(Image.class, imageDescription).click();


        }
        catch (Exception e){
            Reporter.reportEvent("Test Fail", "Error in script execution<br>Check the error thrown", Status.Failed, e);
        }
        finally {
            driver.quit();
        }

    }
}