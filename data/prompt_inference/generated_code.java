```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DigitalCustomerPortalTest {

    public static void main(String[] args) {
        // Set up the WebDriver
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Step 1: Log in to Digital Customer Portal
            driver.get("https://digital-customer-portal.com/login");
            driver.findElement(By.id("username")).sendKeys("your_username");
            driver.findElement(By.id("password")).sendKeys("your_password");
            driver.findElement(By.id("loginButton")).click();

            // Verify landing page is displayed
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("landingPage")));

            // Step 2: Check top ribbon content
            WebElement topRibbon = driver.findElement(By.id("topRibbon"));
            assert topRibbon.findElement(By.id("siemensLogo")).isDisplayed();
            assert topRibbon.findElement(By.id("digitalLabAssistant")).getText().contains("My Digital Lab Assistant");
            assert topRibbon.findElement(By.id("contactIcon")).isDisplayed();
            assert topRibbon.findElement(By.id("languageIcon")).isDisplayed();
            assert topRibbon.findElement(By.id("settingsIcon")).isDisplayed();
            assert topRibbon.findElement(By.id("notificationBellIcon")).isDisplayed();
            assert topRibbon.findElement(By.id("nameShortcutIcon")).isDisplayed();
            assert topRibbon.findElement(By.id("loggedInUserName")).isDisplayed();
            assert topRibbon.findElement(By.id("adminIcon")).isDisplayed();

            // Step 3: Check page tiles content
            WebElement tilesSection = driver.findElement(By.id("tilesSection"));
            assert tilesSection.findElement(By.id("reportIssueTile")).isDisplayed();
            assert tilesSection.findElement(By.id("showRequestsTile")).isDisplayed();
            assert tilesSection.findElement(By.id("orderSupportTile")).isDisplayed();
            assert tilesSection.findElement(By.id("accountQuestionTile")).isDisplayed();
            assert tilesSection.findElement(By.id("setRequestTile")).isDisplayed();

            // Step 4: Click on tile "Report an issue with an order or delivery"
            tilesSection.findElement(By.id("reportIssueTile")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reportIssuePage")));

            // Step 5: Click on tile "Show me my Requests"
            driver.navigate().back();
            tilesSection.findElement(By.id("showRequestsTile")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestsDashboardPage")));

            // Step 6: Click on tile "Question about an order or eSupport assistance"
            driver.navigate().back();
            tilesSection.findElement(By.id("orderSupportTile")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orderSupportPage")));

            // Step 7: Click on tile "Question about my Account"
            driver.navigate().back();
            tilesSection.findElement(By.id("accountQuestionTile")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountQuestionPage")));

            // Step 8: Click on tile "Request Allocation or Saturday Delivery (SET Request)"
            driver.navigate().back();
            tilesSection.findElement(By.id("setRequestTile")).click();
            wait.until(ExpectedConditions.urlContains("sales-efficiency-tool.com"));

        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
```