package io.github.sudharsan_selvaraj;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.sudharsan_selvaraj.autowait.DriverEventListener;
import io.github.sudharsan_selvaraj.autowait.SeleniumWaitOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;


public class Sanity {

    @BeforeSuite
    public static void Before() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    private void testWithoutWait() {
        SpyDriver<ChromeDriver> spyDriver = new SpyDriver<>(new ChromeDriver());

        SeleniumWaitOptions options = SeleniumWaitOptions
                .builder()
                .parseAnnotations(true)
                .build();

        spyDriver.addListener(new DriverEventListener(spyDriver.getSpyDriver(), options));
        ChromeDriver driver = spyDriver.getSpyDriver();

        driver.get("https://react-redux.realworld.io/");
        driver.findElement(By.linkText("Sign in")).click();
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys("abc@tester.com");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("Qwerty@123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        driver.findElement(By.partialLinkText("TesterABC123")).click();
        driver.findElement(By.partialLinkText("Home")).click();
        driver.findElement(By.xpath(".//*[contains(@class,'tag-pill')][text()='HITLER']")).click();
    }

    @Test
    private void testWithWait() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://react-redux.realworld.io/");
        driver.findElement(By.linkText("Sign in")).click();
        WebElement email = new WebDriverWait(driver, 10)
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='email']")));
        email.sendKeys("abc@tester.com");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("Qwerty@123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement username = new WebDriverWait(driver, 10)
                .until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("TesterABC123")));
        username.click();
        driver.findElement(By.partialLinkText("Home")).click();
        WebElement tag = new WebDriverWait(driver, 10)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[contains(@class,'tag-pill')][text()='HITLER']")));
        tag.click();
    }
}
