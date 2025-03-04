package E2E;

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
import java.util.UUID;

public class E2E
{
    @Test
    void E2E()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.E2E, "1199", tc ->
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
            WebDrv drv = WebDrv.EDGE;
            UUID internalUser = tc.browser.start(drv, ETestData.QA_ENV_URL, new CoreStartOptions().startIncognito(), new CoreStartOptions());
            tc.browser.login(ETestData.FUNCTIONAL_USER);
            WaitFor.condition(() -> tc.button.exists(EButton.CONTACT));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.edit.exists(EEdit.MOBILE));
            tc.addStepInfo("Page with details for reporting an issue is opened - Part 1. Personal information",
                    true, tc.progressBar.getNames().contains("Personal Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.edit.sendKeys(EEdit.MOBILE, number, true);
            WaitFor.specificTime(Duration.ofSeconds(2));
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
            tc.stepEvaluator.reset();
            tc.edit.sendKeys(EEdit.SUBJECT, subject);
            tc.edit.sendKeys(EEdit.REASON, reason);
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.combo.select(EComboBox.SHS_TEAM, shsTeam);
            tc.edit.sendKeys(EEdit.SOLD_TO, soldTo);
            tc.edit.sendKeys(EEdit.SHIP_TO, shipTo);
            tc.edit.sendKeys(EEdit.PURCHASE_ORDER, purchaseOrder);
            tc.edit.sendKeys(EEdit.ADDITIONAL_EMAIL, emailId);
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
            tc.stepEvaluator.reset();
            tc.checkBox.check(ECheckBox.MARK_AS_URGENT);
            tc.stepEvaluator
                    .add(() -> tc.checkBox.isChecked(ECheckBox.MARK_AS_URGENT), "check box is not checked");
            tc.addStepInfo("Checkboxes can be checked", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 11
            tc.button.click(EButton.SAVE);
            tc.addStepInfo("Popup with following text is diplayed: Request saved successfully. For reference check attached screenshot", true,
                    tc.browser.getMessage().equalsIgnoreCase("Request saved successfully."), new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            tc.browser.closePopUp();
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with following text is diplayed: Are you sure you want to cancel this request? All progress will be lost. For reference check attached screenshot", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            tc.button.click(EButton.YES_CANCEL_REQUEST);
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is displayed", true, tc.tile.exists(ETile.REPORT_AN_ISSUE),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 14
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard . For reference check attached screenshot .",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 15
            tc.tab.select(ETab.CREATED_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            List<String> requests = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2));
            tc.addStepInfo("Page with list of all requests created by the user is opened on tab My Dashboard " +
                    "For reference check attached screenshot", true, !requests.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 16
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            boolean request = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject);
            tc.addStepInfo("Request is displayed.", true,
                    request, new ComparerOptions().takeScreenShotPlatform());

            //Step 17
            tc.addStepInfo("Request is in status 'Saved'", true, tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(6)).contains("Saved"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 18
            tc.browser.closePopUp();
            String requestId = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(0)).get(0);
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(2), subject);
            WaitFor.condition(() -> tc.edit.exists(EEdit.MOBILE));
            tc.addStepInfo("Page with details for reporting an issue is opened - Part 1. Personal information",
                    true, tc.progressBar.getNames().contains("Personal Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 19
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.edit.exists(EEdit.MOBILE), "Mobile is not editable")
                    .add(() -> tc.edit.getValue(EEdit.PREFERRED_LANGUAGE).equalsIgnoreCase(language), "language is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 3. User is able to modify values in fields listed in step", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 20
            tc.button.click(EButton.NEXT);
            WaitFor.condition(() -> tc.edit.exists(EEdit.SUBJECT));
            tc.addStepInfo("Page with details for reporting an issue is opened - Part 2. Request information",
                    true, tc.progressBar.getNames().contains("Request Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 21
            tc.stepEvaluator.reset();
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SUBJECT).equalsIgnoreCase(subject), "subject is not editable")
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 5. User is able to modify values in fields listed in step",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 22
            tc.button.click(EButton.NEXT);
            WaitFor.condition(() -> tc.browser.getPageTitle().equalsIgnoreCase("Almost there! Please check your information."));
            tc.addStepInfo("Page with details for reporting an issue is opened - 3. Check your information",
                    true, tc.progressBar.getNames().contains("Check your Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 23
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SUBJECT).equalsIgnoreCase(subject), "subject is not editable")
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable")
                    .add(() -> tc.edit.getValue(EEdit.PREFERRED_LANGUAGE).equalsIgnoreCase(language), "language is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 19 and 21",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 24
            tc.button.click(EButton.SAVE_PLUS_SUBMIT);
            WaitFor.condition(tc.modal::exists);
            String modalTitle1 = tc.modal.getTitle();
            String modalContent1 = tc.modal.getContent();
            boolean isModalPopped = modalTitle1.equalsIgnoreCase("Save & Submit request") && modalContent1
                    .equalsIgnoreCase("Are you sure you want to submit this request? You will not be able to make any changes after submission.");
            tc.addStepInfo("""
                    Popup with following text is displayed:         
                    Save & Submit Request
                    Are you sure you want to submit this request? You will not be able to make any changes after submission""",
                    true, isModalPopped, new ComparerOptions().takeScreenShotPlatform());

            //Step 25
            tc.button.click(EButton.YES_SUBMIT);
            WaitFor.specificTime(Duration.ofSeconds(3));
            WaitFor.condition(tc.modal::exists, Duration.ofSeconds(40));
            boolean isModalPopped2 = tc.modal.exists();
            tc.addStepInfo("popup with following test is diplayed:" +
                    "Congratulations! Your issue has been reported.", true, isModalPopped2, new ComparerOptions().takeScreenShotPlatform());

            //Step 26
            tc.button.click(EButton.GO_BACK_TO_THE_DASHBOARD);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Welcome"));
            tc.addStepInfo("Landing page is displayed", true, tc.browser.getPageTitle().contains("Welcome"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 27
            tc.addStepInfo("Notification email was not received by email addresses menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 28
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard.",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 29
            tc.tab.select(ETab.CREATED_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            List<String> requests1 = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2));
            tc.addStepInfo("Page with list of all requests created by the user is opened on tab My Dashboard " +
                    "For reference check attached screenshot", true, !requests1.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 30
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            boolean request1 = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject);
            tc.addStepInfo("Request is displayed.", true,
                    request1, new ComparerOptions().takeScreenShotPlatform());

            //Step 31
            tc.addStepInfo("Request is in status 'Submitted'", true, tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(6)).contains("Submitted"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 32
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(2), subject);
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 33
            WaitFor.condition(() -> this.getComment().contains(reason));
            List<String> selectedReason = this.getComment();
            tc.addStepInfo("Reason from step 5 is used as 1st comment", true,
                    selectedReason.contains(reason), new ComparerOptions().takeScreenShotPlatform());

            //Step 34
            tc.edit.sendKeys(EEdit.COMMENT, reason+" New");
            tc.button.click(EButton.SEND_ICON);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(EButton.SUBMIT_ANYWAY);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.specificTime(Duration.ofSeconds(2));
            List<String> selectedReason1 = this.getComment();
            tc.addStepInfo("New comment is added", true,
                    selectedReason1.contains(reason+" New"), new ComparerOptions().takeScreenShotPlatform());

            //Step 35
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

            //Step 36
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.REQUESTOR_INFORMATION);
            WaitFor.condition(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 3 and 5", "ok", tc.stepEvaluator.eval()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 37
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

            //Step 38
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 39
            tc.tab.select(ETab.MY_RESPONSE_NEEDED);
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            tc.addStepInfo("Page with list of all requests with status Pending Requestor Response created by the user is opened For reference check attached screenshot",
                    true, tc.tab.isSelected(ETab.MY_RESPONSE_NEEDED), new ComparerOptions().takeScreenShotPlatform());

            //Step 40
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Page with list of all requests with status Pending Requestor Response created by the user is opened For reference check attached screenshot",
                    true, tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 41
            UUID admin = tc.browser.start(drv, ETestData.QA_ENV_URL);
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 42
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard . For reference check attached screenshot .",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 43
            tc.tab.select(ETab.MDLA_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            tc.toggle.check(EToggle.GLOBAL_SEARCH);
            tc.addStepInfo("List of all requests created by the users in status Submitted is displayed For reference check attached screenshot", true,
                    tc.tab.isSelected(ETab.MDLA_REQUESTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 44
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Request is displayed.",true , !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).isEmpty(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 45
            tc.table.scrollToColumn(ETable.APP_TABLE, EColumn.byCustomValue("Assignee"));
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(9), "Assign to me");
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Popup with following test is displayed: Task has been assigned to you. For reference check attached screenshot", true,
                    !tc.browser.getMessage().isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 46
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.addStepInfo("Request is no longer visible in tab \"Submitted\"", true, !tc.table.
                    getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject), new ComparerOptions().takeScreenShotPlatform());

            //Step 47
            tc.addStepInfo("Notification email with latest comment was received by email address menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 48
            tc.tab.select(ETab.MDLA_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed For reference check attached screenshot", true,
                    !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 49
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            tc.addStepInfo("Only list of requests where Assignee is current user is displayed", true,
                    !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 50
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Request is displayed.",true, !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).isEmpty(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 51
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(3), "#0");
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.menu.selectFromDropDown(EMenu.CHANGE_STATUS, "Put Request on Hold");
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with mandatory comment is displayed", true, tc.modal.exists()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 52
            tc.edit.sendKeys(EEdit.COMMENT_MODAL, "Modal_Comment");
            tc.button.click(EButton.PUT_ON_HOLD);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.SUBMIT_ANYWAY);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> this.getComment().contains("Modal_Comment"));
            tc.addStepInfo("Button 'Put on Hold' is activated", true, this.getComment().contains("Modal_Comment"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 53
            tc.addStepInfo("New comment is added.", true, this.getComment().contains("Modal_Comment"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 54
            tc.addStepInfo("Notification email with latest comment was received by email address menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 55
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tab.exists(ETab.MDLA_REQUESTS));
            tc.tab.select(ETab.MDLA_REQUESTS);
            tc.addStepInfo("List of all requests created by the users in status Submitted is displayed For reference check attached screenshot", true,
                    tc.tab.isSelected(ETab.MDLA_REQUESTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 56
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(3), "#0");
            WaitFor.condition(() -> this.getComment().isEmpty());
            tc.addStepInfo("Request detail page is displayed on Comment tab with full comment history", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 57
            tc.menu.selectFromDropDown(EMenu.CHANGE_STATUS, "Set Request Active");
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup is dipslayed", true, tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 58
            tc.button.click(EButton.SET_ACTIVE);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.addStepInfo("Popup is displayed.", true, !tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 59
            tc.addStepInfo("Notification email with latest comment was received by email address menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 60
            tc.browser.navigateBack();
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.tab.exists(ETab.MDLA_REQUESTS));
            tc.tab.select(ETab.MDLA_REQUESTS);
            tc.addStepInfo("List of all requests created by the users in status Submitted is displayed For reference check attached screenshot", true,
                    tc.tab.isSelected(ETab.MDLA_REQUESTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 61
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            tc.browser.closePopUp();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject));
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(3), subject);
            WaitFor.condition(() -> !this.getComment().isEmpty());
            tc.addStepInfo("Request detail page is displayed on Comment tab with full comment history", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 62
            tc.addStepInfo("Reason from step 5 is used as 1st comment", true, this.getComment().contains(reason),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 63
            tc.edit.sendKeys(EEdit.COMMENT, reason+" New 2");
            tc.button.click(EButton.SEND_ICON);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(EButton.SUBMIT_ANYWAY);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.specificTime(Duration.ofSeconds(2));
            List<String> selectedReason2 = this.getComment();
            tc.addStepInfo("New comment is added", true,
                    selectedReason2.contains(reason+" New 2"), new ComparerOptions().takeScreenShotPlatform());

            //Step 64
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.REQUEST_INFORMATION);
            WaitFor.condition(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SUBJECT).equalsIgnoreCase(subject), "subject is not editable")
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 19 and 21", "ok", tc.stepEvaluator.eval()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 65
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.REQUESTOR_INFORMATION);
            WaitFor.condition(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SHIP_TO).equalsIgnoreCase(shipTo), "Ship to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 19 and 21", "ok", tc.stepEvaluator.eval()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 66
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.REQUEST_OVERVIEW);
            WaitFor.condition(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number));
            tc.stepEvaluator
                    .add(() -> tc.edit.getValue(EEdit.REASON).equalsIgnoreCase(reason), "reason is not editable")
                    .add(() -> tc.edit.getValue(EEdit.SOLD_TO).equalsIgnoreCase(soldTo), "Sold to is not editable")
                    .add(() -> tc.edit.getValue(EEdit.MOBILE).equalsIgnoreCase(number), "Mobile is not editable");
            tc.addStepInfo("Values of the fields are the same as were entered in step 19 and 21", "ok", tc.stepEvaluator.eval()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 67
            tc.tab.select(ETab.ACTIVITY_STREAM);
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            tc.addStepInfo("History of request updates is displayed", true, !tc.table.getItemsFromColumn(ETable.APP_TABLE,
                    EColumn.byIndex(2)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 68
            tc.menu.selectFromDropDown(EMenu.CHANGE_STATUS, "Request Requestor Feedback");
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with mandatory comment and possibility to add attachment is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 69
            tc.edit.sendKeys(EEdit.COMMENT_MODAL, "Modal_Comment_2");
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.addStepInfo("Button 'Confirm' is activated", true, tc.button.exists(EButton.CONFIRM),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 70
            tc.button.click(EButton.CONFIRM);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.SUBMIT_ANYWAY);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.addStepInfo("Status of request is changed to Pending Requestor Response", true,
                    !tc.browser.getMessage().isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 71
            tc.addStepInfo("Notification email with latest comment was received by email address menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 72
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tab.exists(ETab.MDLA_REQUESTS));
            tc.tab.select(ETab.MDLA_REQUESTS);
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed", true,
                    tc.tab.isSelected(ETab.MDLA_REQUESTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 73
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.addStepInfo("Request is no longer visible in tab Under Review", true, !tc.table.
                    getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 74
            tc.tab.select(ETab.MDLA_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).isEmpty());
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed For reference check attached screenshot", true,
                    !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 75
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject));
            tc.addStepInfo("Only list of requests where Assignee is current user is displayed", true,
                    !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 76
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject));
            boolean request3 = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject);
            tc.addStepInfo("Request is displayed.", true,
                    request3, new ComparerOptions().takeScreenShotPlatform());

            //Step 77
            tc.browser.switchToWebDriver(internalUser);
            tc.button.click(EButton.APP_NAME);
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 78
            tc.notification.openOrClose(true);
            WaitFor.condition(() -> tc.notification.exists(requestId));
            boolean isRequestUpdated = tc.notification.exists(requestId);
            tc.notification.openOrClose(false);
            tc.addStepInfo("Notification about change of status for request updated in steps above is displayed.",
                    true, isRequestUpdated, new ComparerOptions().takeScreenShotPlatform());

            //Step 79
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard . For reference check attached screenshot .",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 80
            tc.tab.select(ETab.CREATED_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            List<String> req = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2));
            tc.addStepInfo("Page with list of all requests created by the user is opened on tab My Dashboard " +
                    "For reference check attached screenshot", true, !req.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 81
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            boolean isThere = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject);
            tc.addStepInfo("Only requests matching search conditions are displayed", true,
                    isThere, new ComparerOptions().takeScreenShotPlatform());

            //Step 82
            tc.addStepInfo("Request is in status 'Pending Requestor Response'", true, tc.table.
                    getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(6)).contains("Pending Requestor Response"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 83
            tc.tab.select(ETab.MY_RESPONSE_NEEDED);
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            tc.addStepInfo("Page with list of all requests with status Pending Requestor Response created by the user is opened For reference check attached screenshot",
                    true, tc.tab.isSelected(ETab.MY_RESPONSE_NEEDED), new ComparerOptions().takeScreenShotPlatform());

            //Step 84
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Request is displayed.", true,!tc.table.getItemsFromColumn
                    (ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 85
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(3), "#0");
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 86
            tc.edit.sendKeys(EEdit.COMMENT, reason+"_New3");
            tc.button.click(EButton.SEND_ICON);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(EButton.SUBMIT_ANYWAY);
            tc.addStepInfo("Popup is displayed", true, tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 87
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.COMMENT_ONLY);
            WaitFor.specificTime(Duration.ofSeconds(2));
            List<String> selectedReason3 = this.getComment();
            tc.addStepInfo("New comment is added", true,
                    selectedReason3.contains(reason+"_New3"), new ComparerOptions().takeScreenShotPlatform());

            //Step 88
            tc.edit.sendKeys(EEdit.COMMENT, reason+"_New4");
            tc.button.click(EButton.SEND_ICON);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(EButton.SUBMIT_ANYWAY);
            tc.addStepInfo("Popup is displayed", true, tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 89
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.COMMENT_AND_SEND);
            WaitFor.specificTime(Duration.ofSeconds(2));
            List<String> selectedReason4 = this.getComment();
            tc.addStepInfo("New comment is added", true,
                    selectedReason4.contains(reason+"_New4"), new ComparerOptions().takeScreenShotPlatform());

            //Step 90
            tc.browser.switchToWebDriver(admin);
            tc.button.click(EButton.APP_NAME);
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_All_REQUESTS));
            tc.addStepInfo("Landing page is displayed", true, tc.tile.exists(ETile.SHOW_ME_All_REQUESTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 91
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 92
            tc.tab.select(ETab.MDLA_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed For reference check attached screenshot", true,
                    !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 93
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            tc.addStepInfo("Request is displayed.", true, tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 94
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(3), subject);
            WaitFor.condition(() -> tc.tab.isSelected(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab with full comment history", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 95
            tc.menu.selectFromDropDown(EMenu.CHANGE_STATUS, "Mark Request Resolved");
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with mandatory comment and possibility to add attachment is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 96
            tc.edit.sendKeys(EEdit.COMMENT_MODAL, "Modal_Comment_4");
            tc.combo.select(EComboBox.CLASSIFICATIONS, "Delivery");
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.addStepInfo("Button 'Resolve Request' is activated", true, tc.button.exists(EButton.RESOLVE_REQUEST),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 97
            tc.button.click(EButton.RESOLVE_REQUEST);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.SUBMIT_ANYWAY);
            WaitFor.condition(() -> !tc.browser.getMessage().isEmpty());
            tc.addStepInfo("""
                    New comment is added.
                    Status of request is changed to "Resolved"             
                    Popup is displayed.
                    Request successfully marked resolved""", true, !tc.browser.getMessage().isEmpty(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 98
            tc.addStepInfo("Notification email was not received by email addresses menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 99
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tab.exists(ETab.MDLA_REQUESTS));
            tc.tab.select(ETab.MDLA_REQUESTS);
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed", true,
                    tc.tab.isSelected(ETab.MDLA_REQUESTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 100
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.addStepInfo("Request is no longer visible in tab Under Review", true, !tc.table.
                    getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject), new ComparerOptions().takeScreenShotPlatform());

            //Step 101
            tc.tab.select(ETab.MDLA_REQUESTS);
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed", true,
                    tc.tab.isSelected(ETab.MDLA_REQUESTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 102
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject));
            tc.addStepInfo("Only list of requests where Assignee is current user is displayed", true,
                    !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 103
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.addStepInfo("Request is no longer visible in tab Under Review", true, tc.table.
                    getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject), new ComparerOptions().takeScreenShotPlatform());

            //Step 104
            tc.browser.switchToWebDriver(internalUser);
            tc.button.click(EButton.APP_NAME);
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS));
            tc.addStepInfo("Landing page is displayed", true, tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 105
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard . For reference check attached screenshot .",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 106
            tc.tab.select(ETab.CREATED_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            List<String> req1= tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2));
            tc.addStepInfo("Page with list of all requests created by the user is opened on tab My Dashboard " +
                    "For reference check attached screenshot", true, !req1.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 107
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            boolean isPresent = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject);
            tc.addStepInfo("Only requests matching search conditions are displayed", true,
                    isPresent, new ComparerOptions().takeScreenShotPlatform());

            //Step 108
            tc.addStepInfo("Request is in status Resolved", true, tc.table.
                            getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(6)).contains("Resolved"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 109
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(3), "#0");
            WaitFor.condition(() -> tc.tab.exists(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 110
            tc.button.click(EButton.REOPEN_REQUEST);
            tc.addStepInfo("Popup with mandatory comment and possibility to add attachment is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 111
            tc.edit.sendKeys(EEdit.COMMENT_MODAL, "Comment_4");
            tc.addStepInfo("Button \"Reopen request\" is activated", true, tc.button.exists(EButton.REOPEN_REQUEST_MODAL),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 112
            tc.button.click(EButton.REOPEN_REQUEST_MODAL);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.SUBMIT_ANYWAY);
            WaitFor.condition(() -> !this.getComment().isEmpty());
            tc.addStepInfo("New comment is added.", true, this.getComment().contains("Modal_Comment"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 113
            tc.addStepInfo("Notification email was not received by email addresses menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 114
            tc.browser.switchToWebDriver(admin);
            tc.button.click(EButton.APP_NAME);
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_All_REQUESTS));
            tc.addStepInfo("Landing page is displayed", true, tc.tile.exists(ETile.SHOW_ME_All_REQUESTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 115
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 116
            tc.tab.select(ETab.MDLA_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed For reference check attached screenshot", true,
                    !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 117
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject));
            tc.addStepInfo("Request is displayed.", true, tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 118
            tc.table.scrollToColumn(ETable.APP_TABLE, EColumn.byCustomValue("Assignee"));
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(9), "Assign to me");
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Popup with following test is displayed: Task has been assigned to you. For reference check attached screenshot", true,
                    !tc.browser.getMessage().isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 119
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            tc.addStepInfo("List of all requests created by the users in status Under Review is displayed", true,
                    tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 120
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            tc.addStepInfo("Request is displayed.", true, tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(3)).contains(subject),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 121
            tc.browser.closePopUp();
            tc.table.itemClick(ETable.APP_TABLE, EColumn.byIndex(3), "#0");
            WaitFor.condition(() -> tc.tab.isSelected(ETab.COMMENTS));
            tc.addStepInfo("Request detail page is opened on Comment tab with full comment history", true,
                    tc.tab.isSelected(ETab.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //Step 122
            tc.menu.selectFromDropDown(EMenu.CHANGE_STATUS, "Mark Request Resolved");
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with mandatory comment and possibility to add attachment is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 123
            tc.edit.sendKeys(EEdit.COMMENT_MODAL, "Modal_Comment_4");
            tc.combo.select(EComboBox.CLASSIFICATIONS, "Delivery");
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.addStepInfo("Button 'Resolve Request' is activated", true, tc.button.exists(EButton.RESOLVE_REQUEST),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 124
            tc.button.click(EButton.RESOLVE_REQUEST);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.SUBMIT_ANYWAY);
            WaitFor.condition(() -> !tc.browser.getMessage().isEmpty());
            tc.addStepInfo("""
                    New comment is added.
                    Status of request is changed to "Resolved"             
                    Popup is displayed.
                    Request successfully marked resolved""", true, !tc.browser.getMessage().isEmpty(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 125
            tc.addStepInfo("Notification email was not received by email addresses menitoned in fields E-Mail and Additional E-Mail",
                    true, true, new ComparerOptions().takeScreenShotPlatform());

            //Step 126
            tc.browser.switchToWebDriver(internalUser);
            tc.button.click(EButton.APP_NAME);
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS));
            tc.addStepInfo("Landing page is displayed", true, tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 127
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.tab.select(ETab.MY_DASHBOARD);
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened on tab My Dashboard . For reference check attached screenshot .",
                    true, tc.tab.isSelected(ETab.MY_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            //Step 128
            tc.tab.select(ETab.CREATED_REQUESTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).isEmpty());
            List<String> req2= tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2));
            tc.addStepInfo("Page with list of all requests created by the user is opened on tab My Dashboard " +
                    "For reference check attached screenshot", true, !req1.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 129
            tc.edit.sendKeys(EEdit.SEARCH, subject, true);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject));
            boolean isPresent2 = tc.table.getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(2)).contains(subject);
            tc.addStepInfo("Only requests matching search conditions are displayed", true,
                    isPresent2, new ComparerOptions().takeScreenShotPlatform());

            //Step 130
            tc.addStepInfo("Request is in status Resolved", true, tc.table.
                            getItemsFromColumn(ETable.APP_TABLE, EColumn.byIndex(6)).contains("Resolved"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 131
            tc.addStepInfo("""
                    Request detail page is opened on Comment tab with full comment history
                    It is not possible ro reopen the same request for 2nd time
                    For reference check attached screenshot""", true, !tc.button.exists(EButton.REOPEN_REQUEST),
                    new ComparerOptions().takeScreenShotPlatform());

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


