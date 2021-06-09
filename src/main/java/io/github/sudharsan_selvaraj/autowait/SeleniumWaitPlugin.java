package io.github.sudharsan_selvaraj.autowait;

import io.github.sudharsan_selvaraj.SpyDriver;
import org.openqa.selenium.WebDriver;

public class SeleniumWaitPlugin<T extends WebDriver> {

    private final SpyDriver<T> spyDriver;

    public SeleniumWaitPlugin(T driver, SeleniumWaitOptions options) {
        this(new SpyDriver<T>(driver), options);
    }

    public SeleniumWaitPlugin(SpyDriver<T> spyDriver, SeleniumWaitOptions options) {
        this.spyDriver = spyDriver;
        DriverEventListener listener = new DriverEventListener(spyDriver.getWrappedDriver(), options);
        spyDriver.addListener(listener);
    }

    public T getDriver() {
        return spyDriver.getSpyDriver();
    }

}
