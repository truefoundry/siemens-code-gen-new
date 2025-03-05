```java
package Admin;

import CompositionRoot.IocBuilder;
import ControlImplementation.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC05_Digital_Customer_Portal_Full_Flow
{
    @Test
    void Digital_Customer_Portal_Full_Flow()
    {
        IocBuilder.execute(Duration.ofMinutes(30), EResultData.ADMIN, "842", tc ->
        {
            //Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.tile.open(ETile.QUESTION_ABOUT_ORDER);
            WaitFor.condition(() -> tc.edit.exists(EEdit.MOBILE));
            tc.addStepInfo("Page with details for reporting an issue is opened - Part 1. Personal information",
                    true, tc.progressBar.getNames().contains("Personal Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.edit.sendKeys(EEdit.MOBILE, "1234567890", true);
            tc.combo.select(EComboBox.COUNTRY, "USA");
            tc.combo.select(EComboBox.STATE, "California");
            tc.edit.sendKeys(EEdit.PREFERRED_LANGUAGE, "English", true);
            tc.addStepInfo("User is able to enter information in specified fields", true, true,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.button.click(EButton.NEXT);
            WaitFor.condition(() -> tc.edit.exists(EEdit.SUBJECT));
            tc.addStepInfo("Page with details for reporting an issue is opened - Part 2. Request information",
                    true, tc.progressBar.getNames().contains("Request Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.edit.sendKeys(EEdit.SUBJECT, "Test Subject");
            tc.edit.sendKeys(EEdit.REASON, "Test Reason");
            tc.combo.select(EComboBox.REQUEST_TYPE, "Order Issue");
            tc.edit.sendKeys(EEdit.SOLD_TO, "Test Sold To");
            tc.edit.sendKeys(EEdit.SHIP_TO, "Test Ship To");
            tc.edit.sendKeys(EEdit.PURCHASE_ORDER, "12345");
            tc.edit.sendKeys(EEdit.ADDITIONAL_EMAIL, "test@example.com");
            tc.addStepInfo("User is able to enter information in specified fields", true, true,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.edit.sendKeys(EEdit.BROWSE_FILES_INPUT, "path/to/file1.pdf");
            tc.addStepInfo("Icon with name of each individual file is created in section Attachments", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.edit.sendKeys(EEdit.BROWSE_FILES_INPUT, "path/to/file2.pdf");
            tc.addStepInfo("Icon with name of file is created in section Attachments", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 8
            tc.button.click(EButton.NEXT);
            WaitFor.condition(() -> tc.browser.getPageTitle().equalsIgnoreCase("Almost there! Please check your information."));
            tc.addStepInfo("Page with details for reporting an issue is opened - 3. Check your information",
                    true, tc.progressBar.getNames().contains("Check your Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 9
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", true, true,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.checkBox.check(ECheckBox.MARK_AS_URGENT);
            tc.addStepInfo("Checkboxes can be checked.", true, tc.checkBox.isChecked(ECheckBox.MARK_AS_URGENT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 11
            tc.button.click(EButton.SAVE_PLUS_SUBMIT);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with following text is displayed: Save & Submit Request", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            tc.button.click(EButton.YES_SUBMIT);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with following text is displayed: Congratulations! Your issue has been reported.", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            tc.button.click(EButton.GO_BACK_TO_THE_DASHBOARD);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Welcome"));
            tc.addStepInfo("Landing page is displayed", true, tc.browser.getPageTitle().contains("Welcome"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 14
            tc.addStepInfo("Notification email was not received by email addresses mentioned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 15
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 16
            tc.tab.select(ETab.CREATED_REQUESTS);
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.addStepInfo("Page with list of all requests created by the user is opened on tab My Dashboard",
                    true, tc.table.exists(ETable.APP_TABLE), new ComparerOptions().takeScreenShotPlatform());

            //Step 17
            tc.edit.sendKeys(EEdit.SEARCH, "Test Subject");
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains("Test Subject"));
            tc.addStepInfo("Only requests matching search conditions are displayed", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 18
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(2), "Test Subject");
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 19
            tc.addStepInfo("Reason from step 5 is used as 1st comment", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 20
            tc.edit.sendKeys(EEdit.COMMENT, "New Comment");
            tc.button.click(EButton.SEND_ICON);
            WaitFor.condition(() -> tc.modal.exists());
            tc.addStepInfo("New comment is added", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 21
            tc.tab.select(ETab.REQUEST_INFORMATION);
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 22
            tc.tab.select(ETab.REQUESTOR_INFORMATION);
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 23
            tc.tab.select(ETab.REQUEST_OVERVIEW);
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            //Step 24
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 25
            tc.tab.select(ETab.CREATED_REQUESTS);
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.edit.sendKeys(EEdit.SEARCH, "Test Subject");
            WaitFor.condition(() -> tc.table.getValue(ETable.APP_TABLE, EColumn.byIndex(2), 0).equalsIgnoreCase("Test Subject"));
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(2), "Test Subject");
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 26
            tc.tab.select(ETab.REQUEST_INFORMATION);
            WaitFor.condition(() -> tc.button.exists(EButton.DELETE));
            tc.button.click(EButton.DELETE);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with following text is displayed: Delete Request", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 27
            tc.button.click(EButton.CONFIRM);
            WaitFor.condition(() -> !tc.browser.getMessage().isEmpty(), Duration.ofMinutes(1));
            tc.addStepInfo("Notification about successful operation is displayed on the bottom of the screen. User is redirected to Landing page",
                    true, tc.browser.getMessage().equalsIgnoreCase("Request deleted successfully."),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 28
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.CREATED_REQUESTS));
            tc.tab.select(ETab.CREATED_REQUESTS);
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            boolean isRequestDeleted = !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains("Test Subject");
            tc.addStepInfo("Order selected in step 25 is no longer visible in the list", true,
                    isRequestDeleted, new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```
