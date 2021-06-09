package io.github.sudharsan_selvaraj.autowait;

import io.github.sudharsan_selvaraj.SpyDriverListener;
import io.github.sudharsan_selvaraj.autowait.annotations.IgnoreWait;
import io.github.sudharsan_selvaraj.autowait.annotations.WaitProperties;
import io.github.sudharsan_selvaraj.types.BaseCommand;
import io.github.sudharsan_selvaraj.types.driver.DriverCommand;
import io.github.sudharsan_selvaraj.types.driver.DriverCommandException;
import io.github.sudharsan_selvaraj.types.driver.DriverCommandResult;
import io.github.sudharsan_selvaraj.types.element.ElementCommand;
import io.github.sudharsan_selvaraj.types.element.ElementCommandException;
import io.github.sudharsan_selvaraj.types.element.ElementCommandResult;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class DriverEventListener implements SpyDriverListener {

    private final WebDriver driver;
    private final SeleniumWaitOptions defaultOption;

    @Override
    public void beforeDriverCommandExecuted(DriverCommand command) {

        SeleniumWaitOptions options = getOptions();
        if (ignoreWait(options, command.getMethod())) {
            return;
        }

        processFindElementCommand(command, options);
    }

    @Override
    public void beforeElementCommandExecuted(ElementCommand command) {

        SeleniumWaitOptions options = getOptions();
        if (ignoreWait(options, command.getMethod())) {
            return;
        }

        if (!processFindElementCommand(command, options)) {
            WebDriverWaitUtil webDriverWaitUtil = getWaitUtil(options.getDefaultWaitTime());
            if (command.getMethod().getName().matches("click|sendKeys|clear")) {
                webDriverWaitUtil.waitForElementClickable(command.getElement());
            } else {
                webDriverWaitUtil.waitForElementToBeDisplayed(command.getElement());
            }
        }
    }

    @Override
    public void afterDriverCommandExecuted(DriverCommandResult command) {

    }

    @Override
    public void onException(DriverCommandException command) {

    }


    @Override
    public void afterElementCommandExecuted(ElementCommandResult command) {

    }

    @Override
    public void onException(ElementCommandException command) {

    }

    private Boolean processFindElementCommand(BaseCommand command, SeleniumWaitOptions options) {
        String methodName = command.getMethod().getName();
        By locator = null;
        if (methodName.equals("findElement")) {
            locator = (By) command.getArguments()[0];
        } else if (methodName.contains("findElementBy")) {
            locator = getLocatorFromFindBy(command.getMethod(), (String) command.getArguments()[0]);
        }

        if (locator != null) {
            getWaitUtil(options.getDefaultWaitTime()).waitForElementPresent(locator);
        }
        return false;
    }

    private By getLocatorFromFindBy(Method method, String locatorText) {
        String locatorType = method.getName().replace("findElementBy", "").toLowerCase();
        switch (locatorType) {
            case "id":
                return By.id(locatorText);
            case "linktext":
                return By.linkText(locatorText);
            case "partiallinktext":
                return By.partialLinkText(locatorText);
            case "tagname":
                return By.tagName(locatorText);
            case "name":
                return By.name(locatorText);
            case "classname":
                return By.className(locatorText);
            case "cssselector":
                return By.cssSelector(locatorText);
            case "xpath":
                return By.xpath(locatorText);
            default:
                return null;
        }
    }

    private SeleniumWaitOptions getOptions() {
        InvocationTracer invocationTracer = new InvocationTracer(defaultOption.getPackageToBeParsed());
        if (defaultOption.isParseAnnotations()) {
            List<Method> callers = invocationTracer.getTargetMethod();
            if (hasIgnoreAnnotation(callers)) {
                return SeleniumWaitOptions.builder().ignoreWait(true).build();
            } else {
                WaitProperties waitProperties = getAnnotatedWaitProperties(callers);
                if (waitProperties == null) {
                    return defaultOption;
                } else {
                    return buildOptionsFromAnnotation(waitProperties);
                }
            }
        } else {
            return defaultOption;
        }
    }

    private SeleniumWaitOptions buildOptionsFromAnnotation(WaitProperties waitProperties) {
        Duration waitTime = waitProperties.timeout() == 0 ?
                defaultOption.getDefaultWaitTime() : Duration.ofSeconds(waitProperties.timeout());
        List<String> exclude = waitProperties.exclude().length == 0 ?
                defaultOption.getExcludedMethods() : Arrays.asList(waitProperties.exclude());

        return SeleniumWaitOptions
                .builder()
                .defaultWaitTime(waitTime)
                .excludedMethods(exclude)
                .parseAnnotations(defaultOption.isParseAnnotations())
                .build();
    }

    private WebDriverWaitUtil getWaitUtil(Duration waitTime) {
        return new WebDriverWaitUtil(driver, waitTime.toSeconds());
    }

    private Boolean ignoreWait(SeleniumWaitOptions options, Method m) {
        return options.isIgnoreWait() || options.getExcludedMethods().contains(m.getName());
    }

    private Boolean hasIgnoreAnnotation(List<Method> methodCalls) {
        return methodCalls
                .stream()
                .anyMatch(method -> {
                    return method.getAnnotation(IgnoreWait.class) != null;
                });
    }

    private WaitProperties getAnnotatedWaitProperties(List<Method> methodCalls) {
        WaitProperties annotation = null;
        for (Method m : methodCalls) {
            annotation = m.getAnnotation(WaitProperties.class);
            if (annotation != null) {
                break;
            }
        }
        return annotation;
    }
}
