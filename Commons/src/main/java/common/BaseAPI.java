package common;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import reporting.ExtentManager;
import reporting.ExtentTestManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BaseAPI {
    public static ExtentReports extent;

    protected static void sleepForSeconds(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            System.out.println("Manual Sleep Failed");
        }
    }

    //Hooks
    @BeforeSuite
    public void extentSetup(ITestContext context) {
        ExtentManager.setOutputDirectory(context);
        extent = ExtentManager.getInstance();
    }

    @BeforeMethod
    public void startExtent(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        //String methodName = method.getName().toLowerCase();
        ExtentTestManager.startTest(method.getName());
        ExtentTestManager.getTest().assignCategory(className);
    }

    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    @AfterMethod
    public void afterEachTestMethod(ITestResult result) {
        ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis()));
        ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis()));
        for (String group : result.getMethod().getGroups()) {
            ExtentTestManager.getTest().assignCategory(group);
        }
        if (result.getStatus() == 1) {
            ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");
        } else if (result.getStatus() == 2) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));
        } else if (result.getStatus() == 3) {
            ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");
        }
        ExtentTestManager.endTest();
        extent.flush();
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot(driver, result.getName());
        }
       // driver.quit();
    }

    @AfterSuite
    public void generateReport() {
        extent.close();
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    public static void captureScreenshot(WebDriver driver, String screenshotName) {
        DateFormat df = new SimpleDateFormat("(yyMMddHHmmssZ)");
        //DateFormat df = new SimpleDateFormat("(MM.dd.yyyy-HH:mma)");
        Date date = new Date();
        df.format(date);
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(file,
                    new File(System.getProperty("user.dir") + "/Screenshots/" + screenshotName + " " + df.format(date) + ".png"));
            System.out.println("Screenshot captured");
        } catch (Exception e) {
            System.out.println("Exception while taking screenshot " + e.getMessage());
        }
    }
    @AfterMethod
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot,
                    "image/jpg",
                    "LoginPage");
        }
    }
    @AfterTest
    public void tearDown(){
        cleanUp();
    }

    // Browser SetUp
    public static WebDriver driver = null;
    public static WebDriverWait wait;
    public static Wait fluentWait;

    /**
     * This method will open the browser in the specified step, with chosen browser between Chrome, Firefox or Safari.
     * Driver opens browser with deleting cookies for security, and maximized window.
     * Driver also adds fluentWait which can be customized.
     * More can be added if required. WebDriverManager by BoniGarcia is being used for the drivers.
     *
     * @param browserName
     * @param actualURL
     * @param waitSeconds
     */
    public void openBrowser(String browserName, String actualURL, int waitSeconds) {
        setUp(browserName);
        getURL(actualURL, waitSeconds);
    }

    private WebDriver setUp(String browserName) {
        switch (browserName) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "safari":
                driver = new SafariDriver();
                break;
            default:
                System.out.println("Please pass the correct browser value: 'chrome' or 'firefox' or 'safari'");
                break;
        }

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        return driver;
    }

    private void getURL(String actualURL, int waitSeconds) {
        fluentWaitWithPolling(10, 3);
        driver.get(actualURL);
    }


    /**
     * This can be added to any method to utilize fluentwait
     */
    public static void fluentWaitWithPolling(int TimeoutSec, int pollingEverySec) {
        fluentWait = new FluentWait(driver)
                .withTimeout(Duration.ofSeconds(TimeoutSec))
                .pollingEvery(Duration.ofSeconds(pollingEverySec))
                .ignoring(Exception.class);
    }

    //Customizable Helper Methods

    /**
     * With this method, user can find broken links from the page.
     *
     * @throws IOException
     */
    public void brokenLink() throws IOException {
        //Step:1-->Get the list of all the links and images
        List<WebElement> linkLists = driver.findElements(By.tagName("a"));
        linkLists.addAll(driver.findElements(By.tagName("img")));

        System.out.println("Total number of links and images--------->>> " + linkLists.size());

        List<WebElement> activeLinks = new ArrayList<WebElement>();
        //Step:2-->Iterate linksList: exclude all links/images which does not have any href attribute
        for (int i = 0; i < linkLists.size(); i++) {
            //System.out.println(linkLists.get(i).getAttribute("href"));
            if (linkLists.get(i).getAttribute("href") != null && (!linkLists.get(i).getAttribute("href").contains("javascript") && (!linkLists.get(i).getAttribute("href").contains("mailto")))) {
                activeLinks.add(linkLists.get(i));
            }
        }
        System.out.println("Total number of active links and images-------->>> " + activeLinks.size());

        //Step:3--> Check the href url, with http connection api
        for (int j = 0; j < activeLinks.size(); j++) {
            HttpURLConnection connection = (HttpURLConnection) new URL(activeLinks.get(j).getAttribute("href")).openConnection();
            connection.connect();
            String response = connection.getResponseMessage();
            connection.disconnect();
            System.out.println(activeLinks.get(j).getAttribute("href") + "--------->>> " + response);
        }
    }


    /**
     * Handling new tabs that was opened by the driver.
     *
     * @param sdriver
     * @return
     */
    public static WebDriver handleNewTab(WebDriver sdriver) {
        String oldTab = sdriver.getWindowHandle();
        List<String> newTabs = new ArrayList<String>(sdriver.getWindowHandles());
        newTabs.remove(oldTab);
        sdriver.switchTo().window(newTabs.get(0));
        return sdriver;
    }

    public void typeAndEnter(String locatorName, String textValue) {
        try {
            driver.findElement(By.id(locatorName)).sendKeys(textValue, Keys.ENTER);
        } catch (Exception idException) {
            System.out.println("ID Locator Unavailable");
        }
        try {
            driver.findElement(By.xpath(locatorName)).sendKeys(textValue, Keys.ENTER);
        } catch (Exception xPathException) {
            System.out.println("XPath Locator Unavailable");
        }
        try {
            driver.findElement(By.cssSelector(locatorName)).sendKeys(textValue, Keys.ENTER);
        } catch (Exception cssException) {
            System.out.println("CSS Selector Locator Unavailable");
        }
        try {
            driver.findElement(By.linkText(locatorName)).sendKeys(textValue, Keys.ENTER);
        } catch (Exception lTextException) {
            System.out.println("LinkText Locator Unavailable");
        }
        try {
            driver.findElement(By.name(locatorName)).sendKeys(textValue, Keys.ENTER);
        } catch (Exception nameException) {
            System.out.println("Name Locator Unavailable");
        }
    }

    public void cleanUp() {
        //driver.close();
        driver.quit();
    }

    protected static void assertTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "Test Failed: Provided Title Did Not Match");
    }

    protected static void assertURL(String expectedURL) {
        String actualTitle = driver.getCurrentUrl();
        Assert.assertEquals(actualTitle, expectedURL, "URL Did not Match As Provided.");
    }

}
