package TestAutomation_1;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2425
{
    @Test
    void TC_RS_1887_As_a_Demand_Planner_All()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION, "2425", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS Part Properties";
            tc.menu.resource.filterResources(workBook, true);
            tc.addStepInfoWithScreenShot("Workbook '%s' is opened".formatted(workBook), workBook, tc.workBook.getSelected());

            //----- STEP 3 -----
            WaitFor.condition(()-> tc.button.exists(EButton.DATA_SETTINGS));
            tc.workBook.menu.openDataSettings();
            String scenario = "S&OP Candidate";
            String partValue = "1000007";
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.PART, partValue)
            ));
            tc.addStepInfoWithScreenShot("Scenario '%s' is selected.".formatted(scenario), scenario, tc.combo.getSelected(EComboBox.SCENARIO));

            //----- STEP 4 -----
            // (1) user without global demand rights and other 2 or 3 groups -> cannot edit public scenario
            // (2) user within these 3 groups can edit values for public scenario - special popup is displayed
            // TODO: 25. 10. 2023 change user to the one with restricted rights, and repeat step 4 for user
            //  with global deman planner group righs
            tc.button.click(EButton.DATA_SETTINGS);
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.AVERAGE_SELLING_PRICE);

            tc.stepEvaluator.add(() ->
                            tc.table.getColumnNames(ETable.byIndex(0))
                                    .stream()
                                    .anyMatch(columnName -> columnName.equalsIgnoreCase(EColumn.AVERAGE_SELLING_PRICE.getValue())),
                    "Column '%s' has not been found.".formatted(EColumn.AVERAGE_SELLING_PRICE.getValue()));
            tc.stepEvaluator.add(true, !tc.table.isPencilInColumnHeader(ETable.byIndex(0), EColumn.AVERAGE_SELLING_PRICE),
                    "Pencil should be present for global planner.");
            tc.stepEvaluator.add(true, !tc.table.isCellValueEditable(ETable.byIndex(0),
                            0,
                            EColumn.AVERAGE_SELLING_PRICE,
                            "-€100.00"),
                    "Value is not editable. It should be for global demand planer.");
            tc.addStepInfoWithScreenShot("Able to view the Average selling price, but cannot see pencil mark and cannot edit value!  (except global demand planner and some specified groups)", "ok",
                    tc.stepEvaluator.eval());

            //----- STEP 5 -----
            tc.button.click(EButton.DATA_SETTINGS);
            String privateScenario = tc.menu.scenarios.create("S&OP Candidate");
            tc.menu.closeMenu();
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, privateScenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.PART, "1000007")
            ));
            tc.addStepInfoWithScreenShot("Private Scenario under S&OP candidate Scenario is selected. '%s' is selected.".formatted(privateScenario), privateScenario, tc.combo.getSelected(EComboBox.SCENARIO));

            //----- STEP 6 -----
            tc.stepEvaluator.reset();
            tc.button.click(EButton.DATA_SETTINGS);
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.AVERAGE_SELLING_PRICE);

            tc.stepEvaluator.add(() ->
                            tc.table.getColumnNames(ETable.byIndex(0))
                                    .stream()
                                    .anyMatch(columnName -> columnName.equalsIgnoreCase(EColumn.AVERAGE_SELLING_PRICE.getValue())),
                    "Column '%s' has not been found.".formatted(EColumn.AVERAGE_SELLING_PRICE.getValue()));
            tc.stepEvaluator.add(true, tc.table.isPencilInColumnHeader(ETable.byIndex(0), EColumn.AVERAGE_SELLING_PRICE),
                    "Pencil is not present");
            tc.stepEvaluator.add(true, tc.table.isCellValueEditable(ETable.byIndex(0),
                            0,
                            EColumn.AVERAGE_SELLING_PRICE,
                            "-€100.00"),
                    "Value is not editable.");
            tc.addStepInfoWithScreenShot("Able to view the Average selling price and also edit the selling price. ", "ok", tc.stepEvaluator.eval());
        });
    }
}


