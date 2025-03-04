package TestAutomation_3;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_4696 {
    @Test
   void Ability_to_adjust_the_statistical_forecast_item_parameters()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TEST_AUTOMATION_3, "4696", tc ->
        {
            //STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response window will open.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //STEP 2
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);
            tc.addStepInfoWithScreenShot("The scenario is created", true, !scenario.isEmpty());

            //STEP 3
            String workBook1 = "SHS S&OP Forecast Item Management";
            String workSheet = "Define Forecast Item Level";
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook1, true);
            WaitFor.condition(()-> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.FILTER, "SHS All Active Parts"),
                    Map.entry(EComboBox.SITE, "1030")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.button.click(EButton.OPEN);
            tc.addStepInfoWithScreenShot("The workbook SHS S&OP Forecast Item Management will open", "SHS S&OP Forecast Item Management", tc.workBook.getSelected());

            //STEP 4
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.STAT_FORECAST_LEVEL);
            tc.table.filter(ETable.byIndex(0), EColumn.STAT_FORECAST_LEVEL, "''");
            List<String> columnValues1 = tc.table.getColumnItems(ETable.byIndex(0), EColumn.STAT_FORECAST_LEVEL);
            tc.addStepInfoWithScreenShot("Only those records which don’t have a forecast items assigned " +
                    "against them are visible. The Stat Forecast Level column will be empty for unassigned items"
            , true, columnValues1.stream().allMatch(s-> s.equals("")));

            //STEP 5
            tc.table.performActionOnColumn(ETable.byIndex(0), EColumn.STAT_FORECAST_LEVEL, EAction.EDIT_RANGE);
            tc.combo.select(EComboBox.OPERATION, "Copy from column");
            tc.combo.select(EComboBox.COLUMN, "Part Name");
            tc.button.click(EButton.APPLY);
            WaitFor.condition(() -> tc.button.exists(EButton.SAVE));
            tc.button.click(EButton.SAVE);
            tc.addStepInfoWithScreenShot("Changes to statistical forecast item can be saved", true, !tc.button.exists(EButton.SAVE));

            //STEP 6
            tc.table.filter(ETable.byIndex(0), EColumn.STAT_FORECAST_LEVEL, "");
            List<String> columnValues2 = tc.table.getColumnItems(ETable.byIndex(0), EColumn.STAT_FORECAST_LEVEL);
            tc.addStepInfoWithScreenShot("Forecast items are visible in the stat forecast Level (Modify) column", true, !columnValues2.isEmpty());
        });
    }

}


