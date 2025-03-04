package TestAutomation_3;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import utils.TestUtils;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_4643
{
    @Test
    void Ability_to_view_and_import_up_to_60_months_of_historical_data_in_the_planning_tool()
    {

        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TEST_AUTOMATION_3, "4643", tc -> {

            //STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //STEP 2
            String workBook1 = "SHS S&OP Consensus Demand Planning";
            String workBook2 = "SHS SpareParts CS";
            String workSheet = "Consensus Demand Plan - Units Summary";
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook1, true);
            tc.addStepInfoWithScreenShot("The workbook SHS S&OP Consensus Demand Planning is opened", workBook1, tc.workBook.getSelected());

            //STEP 3
            WaitFor.condition(()-> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites")
            ));
            tc.button.click(EButton.OPEN);
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            tc.addStepInfoWithScreenShot("A private scenario under 'S&OP Candidate' is created and selected.", true, !scenario.isEmpty());

            //STEP 4
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(1)));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.button.click(EButton.DATA_SETTINGS);
            tc.table.clickLinkInsideCell(ETable.byIndex(1), EColumn.byIndex(rowLabelsColIndex), "Spare Parts (CS)", EColumn.byIndex(0));
            WaitFor.condition(()-> tc.workBook.getSelected().equalsIgnoreCase(workBook2), Duration.ofSeconds(10));
            tc.addStepInfoWithScreenShot("The respective SHS SpareParts CS workbook opens.", workBook2, tc.workBook.getSelected());

            //STEP 5
            tc.hierarchy.openHierarchyPanel();
            tc.hierarchy.add(EHierarchy.SHS_PRODUCT);
            WaitFor.condition(() -> tc.table.getColumnNames(ETable.byIndex(0)).contains("SHS Product Active"), Duration.ofSeconds(20));
            List<String> columnNames =  tc.table.getColumnNames(ETable.byIndex(0));
            tc.addStepInfoWithScreenShot("The data is filtered for the selected hierarchy settings.", true, columnNames.contains("SHS Product Active"));

            //STEP 6
            bucketSettings(tc);
            List<String> cellValues = tc.table.getAllRowItems(ETable.byIndex(0), 7);
            tc.addStepInfoWithScreenShot("60 months of historical data will be visible", true, !cellValues.isEmpty());
        });
    }

    private void bucketSettings(InVivoHandler tc)
    {
        tc.tab.select(ETab.BUCKETS);
        tc.button.click(EButton.EDIT_BUCKETS);
        if (tc.edit.getText(EEdit.BUCKET_SETTINGS).equals("65") && tc.edit.getText(EEdit.BUCKETS_AFTER_PLANNING_DATE_BUCKETS).equals("0"))
        {
            tc.button.click(EButton.CANCEL);
        }
        else
        {
            tc.edit.sendKeys(EEdit.BUCKET_SETTINGS, "65", true);
            tc.edit.sendKeys(EEdit.BUCKETS_AFTER_PLANNING_DATE_BUCKETS, "0", true);
            WaitFor.condition(() -> tc.button.isClickable(EButton.APPLY), Duration.ofSeconds(5));
            tc.button.click(EButton.APPLY);
        }
    }

}



