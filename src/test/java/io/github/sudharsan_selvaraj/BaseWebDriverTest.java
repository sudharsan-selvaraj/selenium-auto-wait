package io.github.sudharsan_selvaraj;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.sudharsan_selvaraj.autowait.DriverEventListener;
import io.github.sudharsan_selvaraj.autowait.SeleniumWaitOptions;
import io.github.sudharsan_selvaraj.autowait.SeleniumWaitPlugin;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseWebDriverTest {

    public ThreadLocal<SeleniumWaitPlugin> seleniumWaitPluginThreadLocal = new ThreadLocal<>();

    @BeforeSuite
    public void setup() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void createDriver() {
        SeleniumWaitOptions options = SeleniumWaitOptions.builder()
                .parseAnnotations(true)
                .packageToBeParsed("io.github.sudharsan_selvaraj")
                .build();
        SeleniumWaitPlugin seleniumWaitPlugin = new SeleniumWaitPlugin(new ChromeDriver(), options);
        seleniumWaitPluginThreadLocal.set(seleniumWaitPlugin);
    }

    @AfterMethod
    public void afterMethod() {
        seleniumWaitPluginThreadLocal.get().getDriver().quit();
    }
}
