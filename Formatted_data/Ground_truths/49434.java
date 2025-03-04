package Button_Functions;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.DirectoryControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

public class Define_Supplier_List
{
    @Test
    void Define_Supplier_List()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.BUTTON_FUNCTIONS, "49434", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.menu.select(EMenu.DEFINE_SUPPLIER_LIST);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("Define supplier list screen displayed", true,
                    tc.combo.isDisplayed(EComboBox.SELECT_A_QMS), new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.combo.select(EComboBox.SELECT_A_QMS,"ARGENTINA");
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.condition(()->!tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).isEmpty() , Duration.ofSeconds(10));
            tc.addStepInfo("A list of suppliers (Name beginning with 'SHS') is shown on the click of 'Search for Suppliers'", true,
                    !tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            BrowserControl.waitForLoadingIndicator();
            tc.button.click(EButton.CLEAR_SEARCH_CRITERIA);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.specificTime(Duration.ofSeconds(5));
            WaitFor.condition(()->tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(1)).isEmpty() , Duration.ofSeconds(10));
            tc.addStepInfo("All fields will reset", true,
                    tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(1)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.combo.select(EComboBox.SELECT_A_QMS,"ARGENTINA");
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->!tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).isEmpty() , Duration.ofSeconds(10));
            tc.addStepInfo("Suppliers list will display", true,
                    !tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            Date download = new Date();
            tc.button.click(EButton.EXPORT_TO_EXCEL);
            tc.browser.checkDownloadedExcelFile(download);
            tc.addStepInfo("Supplier list will be downloaded in excel", true,
                    DirectoryControl.getLastDownloadedFile().toString().contains("MonthlyEvaluation"), new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.table.filter(ETable.SUPPLIER_DETAILS,EColumn.CATEGORIZATION,"A");
            tc.table.setTextToEditBox(ETable.SUPPLIER_DETAILS,EColumn.MIN_MAX,0,"40");
            tc.table.setTextToEditBox(ETable.SUPPLIER_DETAILS,EColumn.STD_PO,0,"30");
            tc.table.setTextToEditBox(ETable.SUPPLIER_DETAILS,EColumn.JIT,0,"30");
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.table.selectCheckBox(ETable.byIndex(0),EColumn.byIndex(1),1);
            tc.button.click(EButton.SAVE);
            WaitFor.condition(()-> !this.getNotification().isEmpty(),Duration.ofSeconds(30));
            tc.addStepInfo("Define supplier list will be saved", "Your changes have been successfully saved",
                   this.getNotification(), new ComparerOptions().takeScreenShotPlatform());
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


