package Button_Functions;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.DirectoryControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class Upload_Documents
{
    @Test
    void Upload_Documents()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.BUTTON_FUNCTIONS, "49406", tc ->
        {
            String supplierID = "SHS0132";
            String attached_file = "SLIM_TEST.pdf";
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.menu.select(EMenu.CREATE, EMenu.UPLOAD_DOCUMENTS);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("Button click works as expected and upload documents screen opened", true,
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
            WaitFor.condition(() -> !tc.edit.getText(EEdit.SUPPLIER_ID).equals(supplierID));
            tc.addStepInfo("All fields will reset", true, !tc.edit.getText(EEdit.SUPPLIER_ID).equalsIgnoreCase(supplierID),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.addStepInfo("Upload documents screen will be closed without saving anything", true,
                    tc.button.exists(EButton.GO), new ComparerOptions().takeScreenShotPlatform());

            //Step11
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.SUPPLIER_ID));
            tc.edit.sendKeys(EEdit.SUPPLIER_ID, supplierID);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.condition(() -> tc.table.getValuesFromRow(ETable.byIndex(0), 0).contains(supplierID));
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.button.click(EButton.CONTINUE);
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Upload Documents"));
            tc.addStepInfo("Supplier is selected and 'Upload document' screen opened on the click of Continue", true, tc.browser.getPageHeaders()
                    .contains("Upload Documents"), new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            WaitFor.condition(() -> tc.button.exists(EButton.ACTIONS));
            tc.button.click(EButton.ACTIONS);
            WaitFor.specificTime(Duration.ofMillis(2000));
            tc.addStepInfo("On Click of actions, dropdown list will display", true, tc.button.isDropDownItemPresent(EButton.ACTIONS, EDropDown.REFRESH)
                    ,new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            boolean isComboOptionsPresent = tc.combo.getAvailableOptions(EComboBox.DOCUMENT_CATEGORY).isEmpty();
            tc.addStepInfo("On Click of document category, dropdown list will display", true, !isComboOptionsPresent,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 14
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.addStepInfo("Upload documents screen will be closed", true, tc.button.exists(EButton.GO),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 15
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.DOCUMENT_CATEGORY));
            tc.combo.select(EComboBox.DOCUMENT_CATEGORY, "Basic Information");
            WaitFor.condition(() -> tc.button.exists(EButton.ADD));
            tc.button.click(EButton.ADD);
            WaitFor.condition(() -> tc.edit.exists(EEdit.DOCUMENT_TITLE));
            tc.addStepInfo("Fields to specify for 'Basic Supplier Information' will appear", true,
                    tc.edit.exists(EEdit.DOCUMENT_TITLE), new ComparerOptions().takeScreenShotPlatform());

            //Step 16
            tc.button.click(EButton.REMOVE_DOCUMENT);
            WaitFor.condition(() -> !tc.edit.exists(EEdit.DOCUMENT_TITLE));
            tc.addStepInfo("Uploaded files should be removed", true,
                    !tc.edit.exists(EEdit.DOCUMENT_TITLE), new ComparerOptions().takeScreenShotPlatform());

            //Step 17
            tc.combo.select(EComboBox.DOCUMENT_CATEGORY, "Basic Information");
            WaitFor.condition(() -> tc.button.exists(EButton.ADD));
            tc.button.click(EButton.ADD);
            WaitFor.condition(() -> tc.edit.exists(EEdit.DOCUMENT_TITLE));
            tc.addStepInfo("Fields to specify for 'Basic Supplier Information' will appear", true,
                    tc.edit.exists(EEdit.DOCUMENT_TITLE), new ComparerOptions().takeScreenShotPlatform());

            //Step 18
            tc.button.click(EButton.UPLOAD_FILE);
            WaitFor.condition(tc.modal::exists);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.click(EButton.CANCEL);
            tc.addStepInfo("Attach files popup window displayed and closed on click of 'Cancel'", true,
                    !tc.button.exists(EButton.ATTACH), new ComparerOptions().takeScreenShotPlatform());

            //Step 19
            tc.button.click(EButton.UPLOAD_FILE);
            WaitFor.condition(tc.modal::exists);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.click(EButton.CANCEL);
            tc.addStepInfo("Attach files popup window displayed and closed on click of 'X' icon", true,
                    !tc.button.exists(EButton.ATTACH), new ComparerOptions().takeScreenShotPlatform());

            //Step 20
            tc.button.click(EButton.UPLOAD_FILE);
            WaitFor.condition(tc.modal::exists);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.sendKeys(EButton.SELECT_FILES, String.valueOf(DirectoryControl.getPathOfTestFile(attached_file)));
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(1)).contains(attached_file),
                    Duration.ofSeconds(7));
            tc.button.click(EButton.ATTACH);
            WaitFor.condition(() -> !tc.node.getValues(ENode.ATTACHMENTS).isEmpty());
            List<String> attachedFile = tc.node.getValues(ENode.ATTACHMENTS);
            tc.addStepInfo("Select files window will display and files will be attached on click of 'Attach'", true,
                    attachedFile.contains("SLIM_TEST"), new ComparerOptions().takeScreenShotPlatform());

            //Step 21
            tc.button.click(EButton.SAVE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.browser.refresh();
            WaitFor.condition(() -> tc.button.exists(EButton.GO), Duration.ofMinutes(2));
            tc.addStepInfo("Changes should be saved", true, tc.button.exists(EButton.GO), new ComparerOptions().takeScreenShotPlatform());

            //Step 22
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.SUBMIT));
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("CREATING RECORDS"));
            tc.addStepInfo("Upload documents submitted successfully", true, tc.node.getValues(ENode.CASE_DETAILS).contains("CREATING RECORDS")
            ,new ComparerOptions().takeScreenShotPlatform());

            //Step 23
            tc.menu.selectWithOutExpand(EMenu.SUPPLIER);
            tc.button.click(EButton.SHOW_360_DREGEE_VIEW);
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.browser.switchToWindow(1);
            WaitFor.condition(()->tc.browser.getTitleOfActiveWindow().contains("Supplier360ViewDetails"), Duration.ofSeconds(10));
            tc.addStepInfo("360° View should be opened", true, tc.browser.getTitleOfActiveWindow().
                    contains("Supplier360ViewDetails"), new ComparerOptions().takeScreenShotPlatform());

            //Step 24
            tc.browser.switchToWindow(0);
            tc.menu.select(EMenu.CREATE, EMenu.UPLOAD_DOCUMENTS);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("upload documents screen opened", true, tc.combo.exists(EComboBox.SELECT_A_QMS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 25
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.CANCEL_CASE);
            WaitFor.condition(() -> tc.editor.exists(EEditor.CANCELLATION_COMMENTS));
            tc.addStepInfo("Cancel case screen is displayed", true, tc.editor.exists(EEditor.CANCELLATION_COMMENTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 26
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(() -> tc.button.exists(EButton.GO));
            tc.addStepInfo("Cancel case screen closed without saving", true, tc.button.exists(EButton.GO),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 27
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.ACTIONS));
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.CANCEL_CASE);
            WaitFor.condition(() -> tc.editor.exists(EEditor.CANCELLATION_COMMENTS));
            tc.addStepInfo("Cancellation comment is saved", true, tc.editor.exists(EEditor.CANCELLATION_COMMENTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 26
            tc.editor.sendKeys(EEditor.CANCELLATION_COMMENTS, "Comment_Cancel");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-CANCELLED"));
            tc.addStepInfo("UD case is cancelled", true, tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-CANCELLED"),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


