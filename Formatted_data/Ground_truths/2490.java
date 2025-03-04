package TestAutomation_3;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2490
{

    @Test
    void TC_RS_Ability_to_assign_the_Plan_or_no_plan_status_of_a_part()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TEST_AUTOMATION_3, "2490", tc -> {
            // STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            // STEP 2
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);
            tc.addStepInfoWithScreenShot("Private scenario is created.", true, !scenario.isEmpty());

            // STEP 3
            String workBook = "SHS Part Properties";
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook, true);
            tc.addStepInfoWithScreenShot("Workbook '%s' is opened.".formatted(workBook), workBook, tc.workBook.getSelected());

            // STEP 4
            tc.workBook.menu.openDataSettings();
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "1040"),
                    Map.entry(EComboBox.PART, "10012169")
            ));
            tc.addStepInfoWithScreenShot("Workbook is opened and with the private scenario created.", scenario,
                    tc.combo.getSelected(EComboBox.SCENARIO));

            // STEP 5
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.IS_ACTIVE);
            boolean isChecked = tc.table.isCheckboxInsideCellChecked(ETable.byIndex(0), EColumn.byIndex(0), "#0", EColumn.IS_ACTIVE);
            tc.table.checkCheckboxInsideCell(ETable.byIndex(0), EColumn.byIndex(0), "#0", EColumn.IS_ACTIVE, !isChecked);
            tc.button.click(EButton.SAVE);
            tc.addStepInfoWithScreenShot("'Is Active' column is updated.", !isChecked,
                    tc.table.isCheckboxInsideCellChecked(ETable.byIndex(0), EColumn.byIndex(0), "#0", EColumn.IS_ACTIVE));

        });

    }
}


