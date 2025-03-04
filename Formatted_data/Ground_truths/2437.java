package TestAutomation_1;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2437
{
    @Test
    void tc_rs_1909()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION, "2437", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);
            tc.menu.openMenu(EActivity.RESOURCES);
            final String workBook = "SHS Data Integrity Check";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            if(!tc.button.isPressed(EButton.DATA_SETTINGS))
                tc.button.click(EButton.DATA_SETTINGS);
            tc.tab.select(ETab.GENERAL);
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.PART, "= All =")
            ));
            tc.addStepInfoWithScreenShot("workbook ‘SHS Data Integrity Check’ is opened.", workBook, tc.workBook.getSelected());

            //----- STEP 3 -----
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.HAS_ISSUES);
            tc.table.filter(ETable.byIndex(0), EColumn.HAS_ISSUES, "Y");
            List<String> result = tc.table.getColumnItems(ETable.byIndex(0), EColumn.PART);
            tc.addStepInfoWithScreenShot("The parts missing in the master data are visible.", true, !result.isEmpty());
        });
    }
}


