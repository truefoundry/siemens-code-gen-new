```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DigitalCustomerPortalTest {

    public static void main(String[] args) {
        // Set up WebDriver (Assuming ChromeDriver is in the system path)
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            // Step 1: Log in to Digital Customer Portal
            driver.get("https://digitalcustomerportal.com/login");
            driver.findElement(By.id("username")).sendKeys("your_username");
            driver.findElement(By.id("password")).sendKeys("your_password");
            driver.findElement(By.id("loginButton")).click();
            wait.until(ExpectedConditions.titleIs("Landing Page"));

            // Step 2: Click on "Contact" icon on top ribbon
            driver.findElement(By.id("contactIcon")).click();
            WebElement contactPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contactPopup")));
            assert contactPopup.getText().contains("Contact Support");
            assert contactPopup.getText().contains("800-242-3233");

            // Step 3: Click on "Close" button
            driver.findElement(By.id("closeContactPopup")).click();
            wait.until(ExpectedConditions.titleIs("Landing Page"));

            // Step 4: Click on "Language" icon on top ribbon
            driver.findElement(By.id("languageIcon")).click();
            WebElement languageOptions = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("languageOptions")));
            assert languageOptions.getText().contains("English");
            assert languageOptions.getText().contains("Deutsch");

            // Step 5: Click on "Deutsch"
            driver.findElement(By.id("languageDeutsch")).click();
            wait.until(ExpectedConditions.titleIs("Landing Page"));
            assert driver.findElement(By.tagName("body")).getText().contains("Willkommen"); // Example check for German text

            // Step 6: Click on "Language" icon on top ribbon and select "English"
            driver.findElement(By.id("languageIcon")).click();
            driver.findElement(By.id("languageEnglish")).click();
            wait.until(ExpectedConditions.titleIs("Landing Page"));
            assert driver.findElement(By.tagName("body")).getText().contains("Welcome"); // Example check for English text

            // Step 7: Click on "Settings" icon on top ribbon
            driver.findElement(By.id("settingsIcon")).click();
            WebElement settingsOptions = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("settingsOptions")));
            assert settingsOptions.getText().contains("Theme");
            assert settingsOptions.getText().contains("Email Notification Settings");

            // Step 8: Click on "Email Notification Settings"
            driver.findElement(By.id("emailNotificationSettings")).click();
            wait.until(ExpectedConditions.titleIs("Email Notifications"));
            WebElement notificationsPage = driver.findElement(By.id("notificationsPage"));
            assert notificationsPage.getText().contains("All notifications are turned on by default");

            // Step 9: Change notifications from ON to OFF and back to ON
            WebElement notificationToggle = driver.findElement(By.id("notificationToggle"));
            notificationToggle.click(); // Turn OFF
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
            assert driver.findElement(By.id("successMessage")).getText().equals("Successfully updated settings");
            notificationToggle.click(); // Turn ON
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
            assert driver.findElement(By.id("successMessage")).getText().equals("Successfully updated settings");

            // Step 10: Click on "My Digital Lab Assistant (vX.X.X)" on top ribbon
            driver.findElement(By.id("digitalLabAssistant")).click();
            wait.until(ExpectedConditions.titleIs("Landing Page"));

        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
```