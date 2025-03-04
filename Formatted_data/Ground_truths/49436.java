package Button_Functions;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class Bulk_Reassignment
{
    @Test
    void Bulk_Reassignment()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.BUTTON_FUNCTIONS, "49436", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.menu.select(EMenu.BULK_TRANSFER_TASK);
            WaitFor.condition(() -> tc.edit.exists(EEdit.REASSIGN_FROM));
            tc.addStepInfo("\"Bulk Reassign\" screen opens", true,
                    tc.edit.exists(EEdit.REASSIGN_FROM), new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.combo.select(EComboBox.REASSIGN_FROM,"rishith koppula.");
            WaitFor.condition(()->tc.table.getRowsCount(ETable.byIndex(0))>0,Duration.ofSeconds(10));
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(1),1);
            WaitFor.condition(()->tc.button.isClickable(EButton.REASSIGN),Duration.ofSeconds(10));
            tc.addStepInfo("\"Reassign\" button is now enabled", true,
                    tc.button.isClickable(EButton.REASSIGN), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.button.click(EButton.REASSIGN);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Bulk Transfer Assignment popup window displayed", true,
                    tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.combo.select(EComboBox.REASSIGN_FROM,"rishith koppula.");
            tc.button.click(EButton.CANCEL);
            WaitFor.condition(()->!tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Bulk Transfer Assignment popup window will be closed", true,
                    !tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(1),1);
            WaitFor.condition(()->tc.button.isClickable(EButton.REASSIGN),Duration.ofSeconds(10));
            tc.button.click(EButton.REASSIGN);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.edit.sendKeys(EEdit.OPERATOR,"Rishith Koppula",true);
            WaitFor.condition(()->tc.table.exists(ETable.OPERATOR),Duration.ofSeconds(10));
            tc.table.clickOnRow(ETable.OPERATOR,0);
            tc.button.click(EButton.CLOSE_MODAL);
            WaitFor.condition(()->!tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Bulk Transfer Assignment popup window will be closed", true,
                    !tc.modal.exists(EModal.MODAL_WRAPPER), new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(1),1);
            WaitFor.condition(()->tc.button.isClickable(EButton.REASSIGN),Duration.ofSeconds(10));
            tc.button.click(EButton.REASSIGN);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.edit.sendKeys(EEdit.OPERATOR,"Rishith Koppula",true);
            WaitFor.condition(()->tc.table.exists(ETable.OPERATOR),Duration.ofSeconds(10));
            tc.table.clickOnRow(ETable.OPERATOR,0);
            tc.button.click(EButton.TRANSFER);
            WaitFor.condition(()->!tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.addStepInfo("Case ID should be transferred to the selected user", true,
                    !tc.modal.exists(EModal.MODAL_WRAPPER) && !tc.button.isClickable(EButton.REASSIGN), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


