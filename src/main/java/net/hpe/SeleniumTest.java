package net.hpe;

import static org.junit.Assert.*;

import com.google.common.base.Verify;
import com.hp.lft.report.Reporter;
import com.hp.lft.report.Status;
import com.sun.jndi.toolkit.url.Uri;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.pagefactory.ByChained;
import com.hpe.leanft.selenium.By;
import com.hpe.leanft.selenium.ByEach;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.hp.lft.sdk.*;
import com.hp.lft.sdk.web.*;
import com.hp.lft.verifications.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumTest  {

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
        try{

            ModifiableSDKConfiguration config = new ModifiableSDKConfiguration();
            config.setServerAddress(new URI("ws://localhost:5095"));
            SDK.init(config);

            Reporter.init();
        }
        catch(Exception e){
            System.out.println("ERROR OCCURRED: \n"+e.toString());
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
        //Reporter.init();

        // Location of where your chromedriver is locate.
        // If you don't use the setPropery, then you will need to have chromedriver in your system path
        System.setProperty("webdriver.chrome.driver", "/opt/selenium/2.27/chromedriver");
        ChromeOptions co = new ChromeOptions();
        co.addExtensions(new File("/opt/leanft/Installations/Chrome/Agent.crx")); // path to agent on my linux yours may differ
        WebDriver driver = new ChromeDriver(co);

        WebDriverWait w = new WebDriverWait(driver,20);
        w.ignoring(NoSuchElementException.class);
        org.openqa.selenium.WebElement P = null;

        try {
            //driver.get("http://www.advantageonlineshopping.com");
            driver.get("http://dockerserver:8000/#/");
            //driver.get("http://dockerserver:8000/#/");

            // This line is using the HPE extension of Selenium through the new attribute 'visibleText'
            w.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("TABLETS")));  //this is one way to sync on objects in Selenium
            //P=w.until(ExpectedConditions.elementToBeClickable(By.visibleText("TABLETS")));  //this is one way to sync on objects in Selenium
            driver.findElement(By.visibleText("TABLETS")).click();

            // This line is using normal Selenium attributes
            w.until(ExpectedConditions.visibilityOfElementLocated(By.id("accordionPrice")));  //this is one way to sync on objects in Selenium
            driver.findElement(By.id("accordionPrice")).click();

            //String priceAccordian = "/html/body/div[3]/section/article/div[3]/div/div/div[2]/ul/li[1]/p[2]/a";
            //w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(priceAccordian)));
            //String price = driver.findElement(By.xpath(priceAccordian)).getText();
            //com.hp.lft.verifications.Verify.areEqual("$1,009.00", price);

            // The following will attach LeanFT to the browser opened by Selenium and work using LeanFT libraries
            Reporter.reportEvent("Attach to Browser","Current URL: "+driver.getCurrentUrl().toString(),Status.Passed);
            Browser browser = BrowserFactory.attach(new BrowserDescription.Builder().url(driver.getCurrentUrl().toString()).build());

            browser.describe(Image.class, new ImageDescription.Builder()
                    .className("imgProduct")
                    .src(new RegExpProperty(".*image_id=3100"))
                    .tagName("IMG")
                    .type(com.hp.lft.sdk.web.ImageType.NORMAL).build()).highlight();

        }
        catch (Exception e){
            Reporter.reportEvent("Test Fail", "Error in script execution<br>Check the error thrown", Status.Failed, e);
        }
        finally {
            driver.quit();
        }

    }
}