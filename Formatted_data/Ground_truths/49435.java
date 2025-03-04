package Button_Functions;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class Manual_Supplier_List
{
    @Test
    void Manual_Supplier_List()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.BUTTON_FUNCTIONS, "49435", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.menu.select(EMenu.MANUAL_SUPPLIER_LIST);
            WaitFor.condition(() -> tc.button.exists(EButton.CREATE_SUPPLIER));
            tc.addStepInfo("'Manual supplier list' screen will be displayed",true,
                    tc.table.getColumnNames(ETable.byIndex(0)).containsAll(List.of("SHSID","Name","Street","City","Postal Code","Country","Edit")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.button.click(EButton.CREATE_SUPPLIER);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("Manual supplier screen displayed", true,
                    tc.combo.isDisplayed(EComboBox.SELECT_A_QMS), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.combo.select(EComboBox.SELECT_A_QMS, "CT");
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("\"You are about to discard your unsaved changes.\" alert message popup should be displayed with OK and Cancel button", true,
                    tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(()->!tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Popup message should be closed", true,
                    !tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.button.click(EButton.CLOSE);
            WaitFor.condition(()->!tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Popup message should be closed", true,
                    !tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.button.click(EButton.OK);
            WaitFor.condition(()->!tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("New changes are reverted", true,
                    !tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.menu.select(EMenu.MANUAL_SUPPLIER_LIST);
            WaitFor.condition(() -> tc.button.exists(EButton.CREATE_SUPPLIER));
            tc.combo.select(EComboBox.SELECT_A_QMS, "CT");
            tc.button.click(EButton.CREATE_SUPPLIER);
            tc.button.click(EButton.SAVE);
            tc.addStepInfo("QMS is saved", true,false
                    , new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.button.click(EButton.SUBMIT);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.edit.exists(EEdit.DEPARTMENT_SUPPLIER_NAME),Duration.ofSeconds(10));
            tc.addStepInfo("QMS is submitted successfully", true, tc.edit.exists(EEdit.DEPARTMENT_SUPPLIER_NAME),
                    new ComparerOptions().takeScreenShotPlatform());


            //STEP 9
            tc.combo.select(EComboBox.BUSINESS_ORGANIZATION, "IT");
            tc.edit.sendKeys(EEdit.DEPARTMENT_SUPPLIER_NAME, "supplierName");
            tc.edit.sendKeys(EEdit.STREET, "SHS Street");
            tc.edit.sendKeys(EEdit.CITY, "Bangalore");
            tc.combo.select(EComboBox.COUNTRY, "IN");
            tc.combo.select(EComboBox.REGION, "KA");
            tc.button.click(EButton.CANCEL);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.button.exists(EButton.GO),Duration.ofSeconds(10));
            tc.addStepInfo("'Define manual supplier' screen closed without saving anything", true,
                    tc.button.exists(EButton.GO), new ComparerOptions().takeScreenShotPlatform());


            //STEP 10
            tc.button.click(EButton.GO);
            tc.combo.select(EComboBox.BUSINESS_ORGANIZATION, "IT");
            tc.edit.sendKeys(EEdit.DEPARTMENT_SUPPLIER_NAME, "supplierName");
            tc.edit.sendKeys(EEdit.STREET, "SHS Street");
            tc.edit.sendKeys(EEdit.CITY, "Bangalore");
            tc.combo.select(EComboBox.COUNTRY, "IN");
            tc.combo.select(EComboBox.REGION, "KA");
            tc.button.click(EButton.SAVE);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Entered details are saved in 'Define manual supplier'", true,
                    tc.button.exists(EButton.SAVE), new ComparerOptions().takeScreenShotPlatform());

            //STEP 11
            tc.button.click(EButton.SUBMIT);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("'Define manual supplier' is submitted", true,
                    tc.alert.getAlertMessage().equals("Thank you! The next step in this case has been routed appropriately."),
                    new ComparerOptions().takeScreenShotPlatform());


            //STEP 12
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.CANCEL_CASE);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> tc.edit.exists(EEditor.CANCELLATION_COMMENTS),Duration.ofSeconds(10));
            tc.addStepInfo("Cancel case screen will display", true,
                    tc.edit.exists(EEditor.CANCELLATION_COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //STEP 13
            tc.editor.sendKeys(EEditor.CANCELLATION_COMMENTS,"Tester Cancelled the case",true);
            tc.button.click(EButton.CANCEL);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Cancel case screen closed without saving", true,
                    tc.alert.getAlertMessage().equals("Thank you! The next step in this case has been routed appropriately."), new ComparerOptions().takeScreenShotPlatform());

            //STEP 14
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.CANCEL_CASE);
            BrowserControl.waitForLoadingIndicator();
            tc.editor.sendKeys(EEditor.CANCELLATION_COMMENTS,"Tester Cancelled the case",true);
            tc.button.click(EButton.SAVE);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Cancellation comment is saved", true,
                    false, new ComparerOptions().takeScreenShotPlatform());

            //STEP 15
            tc.button.click(EButton.SUBMIT);
            tc.addStepInfo("Manual supplier case is cancelled", true,
                    false, new ComparerOptions().takeScreenShotPlatform());

            //STEP 16
            tc.menu.select(EMenu.MANUAL_SUPPLIER_LIST);
            WaitFor.condition(() -> tc.button.exists(EButton.CREATE_SUPPLIER));
            tc.addStepInfo("'Manual supplier list' screen will be displayed",true,
                    false,
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 17
            tc.table.buttonClick(ETable.byIndex(0),EColumn.EDIT,0,EButton.EDIT);
            tc.addStepInfo("Manual supplier screen displayed for the selected supplier", true,
                    false, new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


