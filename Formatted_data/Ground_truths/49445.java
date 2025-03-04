package Button_Functions;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;

import java.time.Duration;
import java.util.Objects;

public class Manage_CFT
{
    @Test
    void Manage_CFT()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.BUTTON_FUNCTIONS, "49445", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.menu.select(EMenu.MANAGE_CFT);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("Manage CFT screen opens", true,
                    tc.combo.exists(EComboBox.SELECT_A_QMS), new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.combo.select(EComboBox.REASSIGN_FROM,"Administrator1@pega.com");
            tc.button.click(EButton.CLEAR_SEARCH_CRITERIA);
            WaitFor.condition(()->tc.edit.getText(EEdit.REASSIGN_FROM).isEmpty(),Duration.ofSeconds(10));
            tc.addStepInfo("Selected user will be removed", true,
                    tc.edit.getText(EEdit.REASSIGN_FROM).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.combo.select(EComboBox.REASSIGN_FROM,"Administrator1@pega.com");
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.condition(()->!tc.table.getItemsFromColumn(ETable.byIndex(0),EColumn.byIndex(0)).isEmpty() , Duration.ofSeconds(10));
            tc.addStepInfo("Supplier list should be displayed", true,
                    !tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());


            //STEP 4
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(0),0);
            tc.combo.select(EComboBox.REASSIGN_FROM,"Rahul R");
            WaitFor.condition(()->tc.button.isClickable(EButton.REASSIGN),Duration.ofSeconds(10));
            tc.button.click(EButton.REASSIGN);
            WaitFor.condition(() -> this.getNotification().isEmpty(), Duration.ofSeconds(10));
            tc.addStepInfo("Operation completed successfully", true,
                    this.getNotification().contains("The operation completed successfully."), new ComparerOptions().takeScreenShotPlatform());

        });
    }

    private String getNotification()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        String val = "";
        try
        {
            return val = Objects.requireNonNull(css.findControl(By.cssSelector("div[class*='noheader-notifications']"))).getText();
        }
        catch (NullPointerException | NotFoundException e)
        {
            TcLog.error("Unable to find notification");
        }
        return val;
    }
}


