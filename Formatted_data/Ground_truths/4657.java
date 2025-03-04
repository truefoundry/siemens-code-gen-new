package TestAutomation_3;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_4657
{
    @Test
    void TC_RS_Ability_to_input_the_forecast_in_volume()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TEST_AUTOMATION_3, "4657", tc -> {
            // STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //Create scenario
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            WaitFor.condition(() -> tc.button.exists(EButton.NEW_SCENARIO), Duration.ofSeconds(15));
            String scenario = tc.menu.scenarios.create(scenarioParent);

            // STEP 2
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Consensus Demand Planning";
            String workSheet = "Consensus Demand Plan - Units Summary";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.addStepInfoWithScreenShot("The workbook 'SHS S&OP Consensus Demand Planning' will open.", workBook, tc.workBook.getSelected());

            // STEP 3
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Items"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.UNIT_OF_MEASURE, "= No conversion =")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.button.click(EButton.OPEN);
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            tc.button.click(EButton.DATA_SETTINGS);
            String businessLineAT = "Business Line (AT)";
            String spareParts = "Spare Parts (CS)";
            String businessLineXP = "Business Line (XP)";
            String statistical = "Statistical";
            EColumn currentDate = EColumn.getColumnNameForCurrentMonth("MM-DD-YY");
            tc.stepEvaluator
                    .add(() -> tc.tab.getSelected().equalsIgnoreCase(workSheet), "The '%s' is not opened.".formatted(workSheet))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    businessLineAT, currentDate).isEmpty(),
                                    "Data not present for forecast category %s".formatted(businessLineAT))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    spareParts, currentDate).isEmpty(),
                            "Data not present for forecast category %s".formatted(spareParts))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    businessLineXP, currentDate).isEmpty(),
                            "Data not present for forecast category %s".formatted(businessLineXP))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    statistical, currentDate).isEmpty(),
                            "Data not present for forecast category %s".formatted(statistical));
            tc.addStepInfoWithScreenShot("The 'Consensus Demand Plan –Units Summary' worksheet will open and" +
                    "the data for each forecast category can be viewed from this workbook.", "ok", tc.stepEvaluator.eval());

            // STEP 4
            String workBook1 = "SHS SpareParts CS";
            tc.table.clickLinkInsideCell(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Spare Parts (CS)", EColumn.byIndex(rowLabelsColIndex));
            WaitFor.condition(() -> tc.workBook.getSelected().equalsIgnoreCase(workBook1), Duration.ofSeconds(10));
            tc.addStepInfoWithScreenShot("The workbook for the forecast category 'Spare Parts (CS) will open.",
                    workBook1, tc.workBook.getSelected());

            // STEP 5
            WaitFor.condition(() -> tc.tab.exists(ETab.PROPOSED_PLAN_DETAILS_UNITS));
            tc.tab.select(ETab.PROPOSED_PLAN_DETAILS_UNITS);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.tab.getSelected().equalsIgnoreCase(ETab.PROPOSED_PLAN_DETAILS_UNITS.getValue()),
                            "'%s' worksheet is not opened".formatted(ETab.PROPOSED_PLAN_DETAILS_UNITS.getValue()))
                    .add(() ->tc.table.isCellValueEditable(ETable.byIndex(0), 0, EColumn.byIndex(4), "10"),
                            "Forcast cannot be edited in monthly buckets");
            tc.addStepInfoWithScreenShot("The Proposed Plan Detail worksheet will open and the forecast can be input/edited in monthly buckets",
                    "ok", tc.stepEvaluator.eval());
        });

    }
}


