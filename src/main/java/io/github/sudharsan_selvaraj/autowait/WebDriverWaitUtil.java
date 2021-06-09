package io.github.sudharsan_selvaraj.autowait;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@AllArgsConstructor
public class WebDriverWaitUtil {

    private final WebDriver driver;
    private final long defaultWaitTime;

    public void waitForElementPresent(By locator) {
        webDriverWait()
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitForElementToBeDisplayed(WebElement element) {
        webDriverWait()
                .until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementClickable(WebElement element) {
        webDriverWait()
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    private WebDriverWait webDriverWait() {
        return new WebDriverWait(driver, defaultWaitTime);
    }
}
