# selenium-auto-wait
selenium-auto-wait automatically manages all weblement waits and makes you to write wait free selenium tests.

## Features
1. Waits till element found when using findElement method. Unlike Webdriver's implicit wait method, you can control this behaviour using annotations.
2. Waits for the element to become intractable before performing any action on it.
3. If you using pageobject model and wants to ignore auto wait for certain methods, you can use `@IgnoreWait` annotation.
4. You can use `@WaitProperties` annotation to control the behaviour of auto wait for a specific method in the page object method.

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.sudharsan-selvaraj</groupId>
    <artifactId>selenium-auto-wait</artifactId>
    <version>1.0.0</version>
</dependency> 
```

### Gradle

```groovy
implementation group: 'io.github.sudharsan-selvaraj', name: 'selenium-auto-wait', version: '1.0.0'
```

Also while downloading selenium, make sure to exclude `net.bytebuddy:byte-buddy` library by using

### Maven
```xml
<dependency>
   <groupId>org.seleniumhq.selenium</groupId>
   <artifactId>selenium-java</artifactId>
   <version>3.141.59</version>
   <exclusions>
      <exclusion>
         <groupId>net.bytebuddy</groupId>
         <artifactId>byte-buddy</artifactId>
      </exclusion>
   </exclusions>
</dependency>
```

### Gradle
```groovy
implementation (group: 'org.seleniumhq.selenium', name: 'selenium-java', version: '3.141.59') {
   exclude group: 'net.bytebuddy', module: 'byte-buddy'
 }
```

## Quickstart

Initialize the wait plugin using
```java
SeleniumWaitOptions options = SeleniumWaitOptions.builder()
                .parseAnnotations(true)
                .defaultWaitTime(Duration.ofSeconds(30))
                .build();
SeleniumWaitPlugin<ChromeDriver> seleniumWaitPlugin = new SeleniumWaitPlugin<ChromeDriver>(new ChromeDriver(), options);
WebDriver driver =  seleniumWaitPlugin.getDriver();
```
That's it. Now the driver object can be used in the test.

## Available options

* `defaultWaitTime` (Duration) - Used as a timeout while waiting for element.
* `excludedMethods` (List<String>) - List of method names that will be ignored in auto wait.
* `parseAnnotations` (Boolean) - If true, the plugin will look for `@IgnoreWait` or `@WaitProperties` annotation and manages wait based on it. Default value is `false`
* `packageToBeParsed` (String) - if `parseAnnotations` is true, the plugin will parse all the methods from the stacktrace looking for annotations. If you want to search the annotations only on a specific package then you can mention it here.

## Annotation Example:

```java
public class WaitTest {
    
    public WebDriver getDriver() {
        SeleniumWaitOptions options = SeleniumWaitOptions.builder()
                .parseAnnotations(true)
                .defaultWaitTime(Duration.ofSeconds(30))
                .build();
      
        SeleniumWaitPlugin seleniumWaitPlugin = new SeleniumWaitPlugin(new ChromeDriver(), options);
        return seleniumWaitPlugin.getDriver();
    }
    
    @Test
    public void test() {
        WebDriver driver = getDriver();
        searchAmazon(driver);
        searchAmazonWithoutWait(driver);
        searchAmazonWithCustomWait(driver);
    }

    public void searchAmazon(WebDriver driver) {
        driver.get("https://www.amazon.in");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("oneplus 7");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(Keys.ENTER);
        driver.findElement(By.partialLinkText("OnePlus 7 Pro")).click();
        driver.switchTo().window(driver.getWindowHandles().toArray(new String[]{})[1]);
        driver.findElement(By.id("add-to-cart-button")).click();
        driver.findElement(By.id("attach-view-cart-button-form")).click();
    }
    
    @IgnoreWait // will not automatically wait for any element interaction
    public void searchAmazonWithoutWait(WebDriver driver) {
        driver.get("https://www.amazon.in");
        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("twotabsearchtextbox")));
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("oneplus 7", Keys.ENTER);
        new WebDriverWait(driver, 10)
                .until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("OnePlus 7 Pro")));
        driver.findElement(By.partialLinkText("OnePlus 7 Pro")).click();
        driver.switchTo().window(driver.getWindowHandles().toArray(new String[]{})[1]);
        new WebDriverWait(driver, 10)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("add-to-cart-button")));
        driver.findElement(By.id("add-to-cart-button")).click();
        new WebDriverWait(driver, 10)
                .until(ExpectedConditions.elementToBeClickable(By.id("attach-view-cart-button-form")));
        driver.findElement(By.id("attach-view-cart-button-form")).click();
    }

    @WaitProperties(
            timeout = 10, //custom wait time in seconds
            exclude = {"sendKeys"} // will not automatically wait for sendKeys method
    )
    public void searchAmazonWithCustomWait(WebDriver driver) {
        driver.get("https://www.amazon.in");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("oneplus 7");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(Keys.ENTER);
        driver.findElement(By.partialLinkText("OnePlus 7 Pro")).click();
        driver.switchTo().window(driver.getWindowHandles().toArray(new String[]{})[1]);
        driver.findElement(By.id("add-to-cart-button")).click();
        driver.findElement(By.id("attach-view-cart-button-form")).click();
    }
}
```


