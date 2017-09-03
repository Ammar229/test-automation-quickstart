package com.opencredo.test.ui.acceptance.test.interaction.objects;

import com.opencredo.test.ui.acceptance.test.config.webdriver.WaitConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Reusable methods for all page objects
 */
public abstract class AbstractPageObject {
    private String path;
    private final WebDriver driver;
    private final int waitTimeOutSeconds;

    public AbstractPageObject(String path, WebDriver driver, int waitTimeOutSeconds) {
        this.path = path;
        this.driver = driver;
        this.waitTimeOutSeconds = waitTimeOutSeconds;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getPath() {
        return path;
    }

    public void goToAndWait() {
        getDriver().navigate().to(path);
        ensure_is_current();
    }

    public void ensure_is_current() {
        wait_until_true_or_timeout(WaitConditions.urlContains(path));
    }

    public boolean is_text_present(String text) {
        wait_until_true_or_timeout(WaitConditions.pageContainsText(text));
        return true;
    }

    /**
     * wait until condition is true or timeout is reached
     */
    protected <V> V wait_until_true_or_timeout(ExpectedCondition<V> isTrue) {
        Wait<WebDriver> wait = new WebDriverWait(this.driver, waitTimeOutSeconds)
                .ignoring(StaleElementReferenceException.class);
        try {
            return wait.until(isTrue);
        } catch (TimeoutException rte) {
            throw new TimeoutException(rte.getMessage() + "\n\nPageSource:\n\n" + getDriver().getPageSource());
        }
    }

    public void setText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    public void submit(WebElement element) {
        element.submit();
    }

    public void selectDropdownByText(WebElement element, String visibleText){
        Select filterSelect = new Select(element);
        waitForDropdownItems(element);
        filterSelect.selectByVisibleText(visibleText);
    }

    private void waitForDropdownItems(WebElement element) {
        WebDriverWait wait = new WebDriverWait(getDriver(),waitTimeOutSeconds );
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement find(By locator) {
        try {
            return getDriver().findElement(locator);
        } catch (NoSuchElementException ex) {
            throw new NoSuchElementException(ex.getMessage() + "\n\nPageSource:\n\n" + getDriver().getPageSource());
        }
    }

    public void screenshot(Scenario scenario, String screenShotName) {
        try 
        {
            scenario.write("Current Page URL is " + sharedDriver.getCurrentUrl());
            if (sharedDriver.getWrappedDriver() instanceof TakesScreenshot) 
            {
            	 File scrFile = (File) sharedDriver.getScreenshotAs(OutputType.FILE);
            	 try 
            	 {
                     FileUtils.copyFile(scrFile, new File(screenShotName+".png"));
                 } 
            	 catch (IOException e) 
            	 {
                     e.printStackTrace();
                 }
            }
        } 
        catch (Throwable somePlatformsDontSupportScreenshotsOrBrowserHasDied) 
        {
            somePlatformsDontSupportScreenshotsOrBrowserHasDied.printStackTrace(System.err);
        }
    }    


}
