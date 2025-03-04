package Button_Functions;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class Bulk_Submission_Of_Tasks
{
    @Test
    void Bulk_Submission_Of_Tasks()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.BUTTON_FUNCTIONS, "49438", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.menu.select(EMenu.BULK_SUBMISSION_TASKS);
            WaitFor.condition(() -> tc.button.exists(EButton.GROUP),Duration.ofSeconds(10));
            tc.addStepInfo("'Bulk submission' screen displayed", true,
                    tc.button.exists(EButton.GROUP), new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.button.click(EButton.GROUP);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.combo.exists(EComboBox.SELECT_FIELD),Duration.ofSeconds(10));
            tc.addStepInfo("Dropdown list will display on click of Group", true,
                    tc.combo.exists(EComboBox.SELECT_FIELD), new ComparerOptions().takeScreenShotPlatform());
            //STEP 3
            tc.button.click(EButton.FIELDS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.check.isEnabled(ECheckBox.CASE_TYPE),Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.check.isEnabled(ECheckBox.CASE_TYPE),"'%s' checkbox is not present.".formatted(ECheckBox.CASE_TYPE))
                    .add(()->tc.check.isEnabled(ECheckBox.TASK_NAME),"'%s' checkbox is not present.".formatted(ECheckBox.TASK_NAME))
                    .add(()->tc.check.isEnabled(ECheckBox.TASK_INSTRUCTIONS),"'%s' checkbox is not present.".formatted(ECheckBox.TASK_INSTRUCTIONS))
                    .add(()->tc.check.isEnabled(ECheckBox.SUPPLIER_ID),"'%s' checkbox is not present.".formatted(ECheckBox.SUPPLIER_ID))
                    .add(()->tc.check.isEnabled(ECheckBox.SUPPLIER_NAME),"'%s' checkbox is not present.".formatted(ECheckBox.SUPPLIER_NAME))
                    .add(()->tc.check.isEnabled(ECheckBox.QMS),"'%s' checkbox is not present.".formatted(ECheckBox.QMS))
                    .add(()->tc.check.isEnabled(ECheckBox.CREATED_DATE),"'%s' checkbox is not present.".formatted(ECheckBox.CREATED_DATE));
            tc.addStepInfo("Dropdown list will display on click of Fields", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.button.click(EButton.DENSITY);
            BrowserControl.waitForLoadingIndicator();
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.button.isDropDownItemPresent(EButton.DENSITY,EDropDown.SHORT),"'%s' DropDown item is not present in the DropDown '%s'".formatted(EDropDown.SHORT,EButton.DENSITY))
                    .add(()->tc.button.isDropDownItemPresent(EButton.DENSITY,EDropDown.MEDIUM),"'%s' DropDown item is not present in the DropDown '%s'".formatted(EDropDown.MEDIUM,EButton.DENSITY))
                    .add(()->tc.button.isDropDownItemPresent(EButton.DENSITY,EDropDown.TALL),"'%s' DropDown item is not present in the DropDown '%s'".formatted(EDropDown.TALL,EButton.DENSITY))
                    .add(()->tc.button.isDropDownItemPresent(EButton.DENSITY,EDropDown.FULL_CONTENT),"'%s' DropDown item is not present in the DropDown '%s'".formatted(EDropDown.FULL_CONTENT,EButton.DENSITY));
            tc.addStepInfo("Dropdown list will display on click of Density", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(1),1);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> tc.button.isClickable(EButton.BULK_SUBMIT),Duration.ofSeconds(10));
            tc.button.click(EButton.BULK_SUBMIT);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.modal.exists(EModal.MODAL_WRAPPER),"Modal does not exist")
                    .add(()->tc.button.exists(EButton.CLOSE_MODAL),"'%s' Button does not exist".formatted(EButton.CLOSE_MODAL))
                    .add(()->tc.button.exists(EButton.SUBMIT),"'%s' Button does not exist".formatted(EButton.SUBMIT))
                    .add(()->tc.button.exists(EButton.CANCEL),"'%s' Button does not exist".formatted(EButton.CANCEL));
            tc.addStepInfo("Bulk submit task popup window will be displayed with Cancel and Submit buttons", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.CANCEL);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> !tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Bulk submit task popup window will be closed", true,
                    !tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());


            //STEP 7
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(1),1);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> tc.button.isClickable(EButton.BULK_SUBMIT),Duration.ofSeconds(10));
            tc.button.click(EButton.BULK_SUBMIT);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.modal.exists(EModal.MODAL_WRAPPER),"Modal does not exist")
                    .add(()->tc.button.exists(EButton.CLOSE_MODAL),"'%s' Button does not exist".formatted(EButton.CLOSE_MODAL))
                    .add(()->tc.button.exists(EButton.SUBMIT),"'%s' Button does not exist".formatted(EButton.SUBMIT))
                    .add(()->tc.button.exists(EButton.CANCEL),"'%s' Button does not exist".formatted(EButton.CANCEL));
            tc.button.click(EButton.CLOSE_MODAL);
            WaitFor.condition(()-> !tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.stepEvaluator
                    .add(()->!tc.modal.exists(EModal.MODAL_WRAPPER),"Modal is Exist");
            tc.addStepInfo("Bulk submit task popup window will be closed", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(1),1);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> tc.button.isClickable(EButton.BULK_SUBMIT),Duration.ofSeconds(10));
            tc.button.click(EButton.BULK_SUBMIT);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.modal.exists(EModal.MODAL_WRAPPER),"Modal does not exist")
                    .add(()->tc.button.exists(EButton.CLOSE_MODAL),"'%s' Button does not exist".formatted(EButton.CLOSE_MODAL))
                    .add(()->tc.button.exists(EButton.SUBMIT),"'%s' Button does not exist".formatted(EButton.SUBMIT))
                    .add(()->tc.button.exists(EButton.CANCEL),"'%s' Button does not exist".formatted(EButton.CANCEL));
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(()-> !tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Bulk submit task popup window will be closed", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());


        });
    }
}


