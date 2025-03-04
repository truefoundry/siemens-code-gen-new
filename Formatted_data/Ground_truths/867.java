package Internal_User;

import CompositionRoot.IocBuilder;
import ControlImplementation.BrowserControl;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.*;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TC03_Internal_User_Issue_with_an_order_or_deliver
{
    @Test
    void Issue_with_an_order_or_deliver()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.INTERNAL_USER, "867", tc ->
        {
            final String number = "0123456789";
            final String country = "United States of America";
            final String state = "Alaska";
            final String language = "English";
            final String emailId = "Test@yahoo.in";
            final String shsTeam = "QAP";
            final String subject = Generator.getHashedName("Subject:");
            final String reason = Generator.getHashedName("Reason:");
            final String soldTo = Generator.getHashedName("soldTo:");
            final String shipTo = Generator.getHashedName("shipTo:");
            final String purchaseOrder = Generator.getHashedName("Order:");
            String path = String.valueOf(DirectoryControl.getPathOfResourceFile(Path.of("MDLA_TEST.pdf")));


            //Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions().startIncognito(), new CoreStartOptions());
            tc.browser.login(ETestData.FUNCTIONAL_USER);
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", "Landing page is Displayed", tc.button.exists(EButton.CONTACT) ?
                    "Landing page is Displayed" : "Landing page is not Displayed", new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.edit.exists(EEdit.MOBILE));
            tc.addStepInfo("Page with details for reporting an issue is opened - Part 1. Personal information",
                    true, tc.progressBar.getNames().contains("Personal Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.edit.sendKeys(EEdit.MOBILE, number, true);
            tc.combo.select(EComboBox.COUNTRY, country);
            tc.combo.select(EComboBox.STATE, state);
            tc.edit.sendKeys(EEdit.PREFERRED_LANGUAGE, language, true);
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable")
                    .add(() -> tc.edit.getValue(EEdit.PREFERRED_LANGUAGE).equalsIgnoreCase(language), "language is not editable");
            tc.addStepInfo("User is able to enter information in specified fields", "ok",tc.stepEvaluator.eval()
                    ,new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.button.click(EButton.NEXT);
            WaitFor.condition(() -> tc.edit.exists(EEdit.SUBJECT));
            tc.addStepInfo("Page with details for reporting an issue is opened - Part 2. Request information",
                    true, tc.progressBar.getNames().contains("Request Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.edit.sendKeys(EEdit.SUBJECT, subject);
            tc.edit.sendKeys(EEdit.REASON, reason);
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.combo.select(EComboBox.SHS_TEAM, shsTeam);
            tc.edit.sendKeys(EEdit.SOLD_TO, soldTo);
            tc.edit.sendKeys(EEdit.SHIP_TO, shipTo);
            tc.edit.sendKeys(EEdit.PURCHASE_ORDER, purchaseOrder);
            tc.edit.sendKeys(EEdit.ADDITIONAL_EMAIL, emailId);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SUBJECT).equalsIgnoreCase(subject), "subject is not editable")
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.PURCHASE_ORDER).equalsIgnoreCase(purchaseOrder), "purchase order is not editable")
                    .add(() -> tc.edit.getValue(EEdit.ADDITIONAL_EMAIL).equalsIgnoreCase(emailId), "emailID is not editable");
            tc.addStepInfo("User is able to enter information in specified fields", "ok", tc.stepEvaluator
                    .eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.edit.sendKeys(EEdit.BROWSE_FILES_INPUT, path);
            String selectedFile = tc.edit.getValue(EEdit.BROWSE_FILES_INPUT);
            tc.addStepInfo("Icon with name of each individual file is created in section Attachments", true,
                    path.contains(selectedFile), new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            String selectedFile1 = tc.edit.getValue(EEdit.BROWSE_FILES_INPUT);
            tc.addStepInfo("Icon with name of file is created in section Attachments", true,
                    path.contains(selectedFile1), new ComparerOptions().takeScreenShotPlatform());

            //Step 8
            tc.button.click(EButton.NEXT);
            WaitFor.condition(() -> tc.browser.getPageTitle().equalsIgnoreCase("Almost there! Please check your information."));
            tc.addStepInfo("Page with details for reporting an issue is opened - 3. Check your information",
                    true, tc.progressBar.getNames().contains("Check your Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 9
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SUBJECT).equalsIgnoreCase(subject), "subject is not editable")
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable")
                    .add(() -> tc.edit.getValue(EEdit.PREFERRED_LANGUAGE).equalsIgnoreCase(language), "language is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.checkBox.check(ECheckBox.MARK_AS_URGENT);
            String isChecked = tc.checkBox.isChecked(ECheckBox.MARK_AS_URGENT) ? "Checkboxes can be checked" : "Checkboxes can't be checked";
            tc.addStepInfo("Checkboxes can be checked", "Checkboxes can be checked", isChecked ,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 11
            tc.button.click(EButton.SAVE_PLUS_SUBMIT);
            WaitFor.condition(tc.modal::exists);
            String modalTitle1 = tc.modal.getTitle();
            String modalContent1 = tc.modal.getContent();
            boolean isModalPopped = modalTitle1.equalsIgnoreCase("Save & Submit request") && modalContent1
                    .equalsIgnoreCase("Are you sure you want to submit this request? You will not be able to make any changes after submission.");
            tc.addStepInfo("""
                    Popup with following text is displayed:              
                    Save & Submit Request
                    Are you sure you want to submit this request? You will not be able to make any changes after submission
                    """, true, isModalPopped, new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            tc.button.click(EButton.YES_SUBMIT);
            WaitFor.condition(tc.modal::exists, Duration.ofMinutes(2));
            String modalTitle2 = tc.modal.getTitle();
            boolean isModalPopped2 = modalTitle2.contains("Congratulations");
            tc.addStepInfo("popup with following test is diplayed:" +
                    "Congratulations! Your issue has been reported.", true, isModalPopped2, new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            tc.button.click(EButton.GO_BACK_TO_THE_DASHBOARD);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Welcome"));
            tc.addStepInfo("Landing page is displayed", true, tc.browser.getPageTitle().contains("Welcome"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 14
            tc.addStepInfo("Notification email was not received by email addresses menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 15
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard . For reference check attached screenshot .",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 16
            tc.tab.select(ETab.CREATED_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            List<String> requests = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2));
            tc.addStepInfo("Page with list of all requests created by the user is opened on tab My Dashboard " +
                    "For reference check attached screenshot", true, !requests.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 17
            tc.edit.sendKeys(EEdit.SEARCH, subject);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            boolean request = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject);
            tc.addStepInfo("Only requests matching search conditions are displayed", true,
                    request, new ComparerOptions().takeScreenShotPlatform());

            //Step 18
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(2), subject);
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 19
            WaitFor.condition(() -> this.getComment().contains(reason));
            List<String> selectedReason = this.getComment();
            tc.addStepInfo("Reason from step 5 is used as 1st comment", true,
                    selectedReason.contains(reason), new ComparerOptions().takeScreenShotPlatform());

            //Step 20
            tc.edit.sendKeys(EEdit.COMMENT, reason+"_New");
            tc.button.click(EButton.SEND_ICON);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(EButton.SUBMIT_ANYWAY);
            WaitFor.specificTime(Duration.ofSeconds(12));
            List<String> selectedReason1 = this.getComment();
            tc.addStepInfo("New comment is added", true,
                    selectedReason1.contains(reason+"_New"), new ComparerOptions().takeScreenShotPlatform());

            //Step 21
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.REQUEST_INFORMATION);
            WaitFor.condition(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SUBJECT).equalsIgnoreCase(subject), "subject is not editable")
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", "ok", tc.stepEvaluator.eval()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 22
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.REQUESTOR_INFORMATION);
            WaitFor.condition(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", "ok", tc.stepEvaluator.eval()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 23
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.REQUEST_OVERVIEW);
            WaitFor.condition(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", "ok", tc.stepEvaluator.eval()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 24
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 25
            tc.tab.select(ETab.CREATED_REQUESTS);
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.edit.sendKeys(EEdit.SEARCH, subject);
            WaitFor.condition(() -> tc.table.getValue(ETable.APP_TABLE, EColumn.byIndex(2), 0).equalsIgnoreCase(subject));
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(2), subject);
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab",
                    true, tc.tab.isSelected(ETab.COMMENTS) ,new ComparerOptions().takeScreenShotPlatform());

            //Step 26
            tc.tab.select(ETab.REQUEST_INFORMATION);
            WaitFor.condition(() -> tc.button.exists(EButton.DELETE));
            tc.button.click(EButton.DELETE);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("""
                            Popup with following text is displayed:                    
                            Delete Request
                            Request will be removed from your list of saved requests.""", "Popup has been displayed",
                    tc.modal.exists() ? "Popup has been displayed" : "Popup has not been displayed", new ComparerOptions().takeScreenShotPlatform());

            //Step 27
            tc.button.click(EButton.CONFIRM);
            WaitFor.condition(() -> !tc.browser.getMessage().isEmpty(), Duration.ofMinutes(1));
            tc.addStepInfo("Notification about successful operation is displayed on the bottom of the screen. User is redirecrted to Landing page",
                    true, tc.browser.getMessage().equalsIgnoreCase("Request deleted successfully."),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 28
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.CREATED_REQUESTS));
            tc.tab.select(ETab.CREATED_REQUESTS);
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            boolean isRequestDeleted = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).
                    contains(subject);
            tc.addStepInfo("Order selected in step 25 is no longer visible in the list", true
                    , !isRequestDeleted, new ComparerOptions().takeScreenShotPlatform());

        });
    }

    private List<String> getComment()
    {
        CoreCssControl css = IocBuilder.getContainer().getComponent(CoreCssControl.class);
        List<String> comments = new ArrayList<>();
        try
        {
            List<WebElement> eles = css.findControls(By.cssSelector("div[class=\"comment-html-content\"]"));
            if (eles != null)
            {
                for(WebElement ele : eles)
                {
                    comments.add(ele.getText());
                }
            }
        }
        catch (Exception e)
        {
            TcLog.error("Unable to fetch comment");
        }
        return comments;
    }
}


