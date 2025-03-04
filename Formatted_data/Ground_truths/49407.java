package Button_Functions;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class Document_Validty
{
    @Test
    void Document_Validty()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.BUTTON_FUNCTIONS, "49407", tc ->
        {
            String supplierID = "SHS0132";
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.menu.select(EMenu.CREATE, EMenu.VALIDITY_DOCUMENTS);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("Button click works as expected and 'validity documents' screen opened", true,
                    tc.combo.exists(EComboBox.SELECT_A_QMS) , new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.button.click(EButton.GO);
            WaitFor.condition(()-> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.combo.select(EComboBox.SELECT_A_QMS, "LD_POC");
            tc.button.click(EButton.CANCEL);
            tc.stepEvaluator.add(tc.modal::exists, "Modal didn't popped up")
                    .add(() -> tc.button.exists(EButton.CANCEL), "Cancel button not found")
                    .add(() -> tc.button.exists(EButton.OK), "OK button not found");
            tc.addStepInfo("'You are about to discard your unsaved changes.' alert message popup should be displayed " +
                    "with OK and Cancel button", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.button.click(EButton.OK);
            WaitFor.condition(() -> !tc.modal.exists());
            tc.addStepInfo("Popup message should be closed", true, !tc.modal.exists(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.button.click(EButton.GO);
            WaitFor.condition(()-> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.combo.select(EComboBox.SELECT_A_QMS, "LD_POC");
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(tc.modal::exists);
            tc.modal.closeModalByTitle(EModal.UNSAVED_CHANGES);
            tc.addStepInfo("Popup message should be closed", true, !tc.button.exists(EButton.OK),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(tc.button.exists(EButton.OK) ? EButton.OK : EButton.DISCARD);
            tc.addStepInfo("Popup message should be closed", true, !tc.button.exists(EButton.OK),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.combo.select(EComboBox.SELECT_A_QMS, "LD_POC");
            tc.button.click(EButton.SAVE);
            WaitFor.condition(() -> tc.button.exists(EButton.SAVE));
            tc.addStepInfo("QMS is saved", true, tc.button.exists(EButton.SAVE), new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.button.exists(EButton.SUPPLIER_ID));
            tc.addStepInfo("QMS is submitted successfully", true, tc.edit.exists(EEdit.SUPPLIER_ID),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 8
            tc.edit.sendKeys(EEdit.SUPPLIER_ID, supplierID);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.condition(() -> tc.table.getValuesFromRow(ETable.byIndex(0), 0).contains(supplierID));
            String id = tc.table.getValuesFromRow(ETable.byIndex(0), 0).get(4);
            tc.addStepInfo("A list of suppliers is shown on the click of 'Search for Suppliers'", supplierID, id, new ComparerOptions().takeScreenShotPlatform());

            //Step 9
            tc.button.click(EButton.CLEAR_SEARCH_CRITERIA);
            WaitFor.condition(() -> !tc.edit.getValue(EEdit.SUPPLIER_ID).equals(supplierID));
            tc.addStepInfo("All fields will reset", true, !tc.edit.getValue(EEdit.SUPPLIER_ID).equalsIgnoreCase(supplierID),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.addStepInfo("Validity Documents screen will be closed without saving anything", true,
                    tc.button.exists(EButton.GO), new ComparerOptions().takeScreenShotPlatform());

            //Step11
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.SUPPLIER_ID));
            tc.edit.sendKeys(EEdit.SUPPLIER_ID, supplierID);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.condition(() -> tc.table.getValuesFromRow(ETable.byIndex(0), 0).contains(supplierID));
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.button.click(EButton.CONTINUE);
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Validity Documents"));
            tc.addStepInfo("Supplier is selected and 'Validity document' screen opened on the click of Continue", true, tc.browser.getPageHeaders()
                    .contains("Validity Documents"), new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.addStepInfo("Validity documents screen will be closed without saving anything", true,
                    tc.button.exists(EButton.GO), new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.editor.exists(EEditor.COMMENTS), Duration.ofMinutes(5));
            tc.editor.sendKeys(EEditor.COMMENTS, "Cancel_Comment");
            tc.button.click(EButton.SAVE);
            WaitFor.condition(() -> tc.button.exists(EButton.GO),  Duration.ofMinutes(1));
            tc.browser.refresh();
            tc.addStepInfo("Changes will be saved", true, tc.button.exists(EButton.GO), new ComparerOptions()
                    .takeScreenShotPlatform());

            //Step 14
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.SUBMIT));
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.byIndex(0), 0);
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("CREATING RECORDS"));
            tc.addStepInfo("Validity documents is submitted successfully", true, tc.node.getValues(ENode.CASE_DETAILS).contains("CREATING RECORDS")
                    ,new ComparerOptions().takeScreenShotPlatform());

            //Step 15
            tc.menu.selectWithOutExpand(EMenu.SUPPLIER);
            tc.button.click(EButton.SHOW_360_DREGEE_VIEW);
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.browser.switchToWindow(1);
            WaitFor.condition(()->tc.browser.getTitleOfActiveWindow().contains("Supplier360ViewDetails"), Duration.ofSeconds(10));
            tc.addStepInfo("360° View should be opened", true, tc.browser.getTitleOfActiveWindow().
                    contains("Supplier360ViewDetails"), new ComparerOptions().takeScreenShotPlatform());

            //Step 16
            tc.browser.switchToWindow(0);
            tc.menu.select(EMenu.CREATE, EMenu.VALIDITY_DOCUMENTS);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("'Validity documents' screen opened", true, tc.combo.exists(EComboBox.SELECT_A_QMS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 17
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.CANCEL_CASE);
            WaitFor.condition(() -> tc.editor.exists(EEditor.CANCELLATION_COMMENTS));
            tc.addStepInfo("Cancel case screen is displayed", true, tc.editor.exists(EEditor.CANCELLATION_COMMENTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 18
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.addStepInfo("Cancel case screen closed without saving", true, tc.button.exists(EButton.GO),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 19
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.ACTIONS));
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.CANCEL_CASE);
            WaitFor.condition(() -> tc.editor.exists(EEditor.CANCELLATION_COMMENTS));
            tc.addStepInfo("Cancellation comment is saved", true, tc.editor.exists(EEditor.CANCELLATION_COMMENTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 20
            tc.editor.sendKeys(EEditor.CANCELLATION_COMMENTS, "Comment_Cancel");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-CANCELLED"));
            tc.addStepInfo("UD case is cancelled", true, tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-CANCELLED"),
                    new ComparerOptions().takeScreenShotPlatform());



        });
    }
}


