package Button_Functions;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class Re_Qualification
{
    @Disabled
    @Test
    void Re_Qualification()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.BUTTON_FUNCTIONS, "49426", tc ->
        {
            String supplierID = "SHS0132";
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //Step 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.menu.select(EMenu.CREATE, EMenu.SUPPLIER_QUALIFICATION);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("Button click works as expected and upload documents screen opened", true,
                    tc.combo.exists(EComboBox.SELECT_A_QMS) , new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.button.click(EButton.CANCEL);

        });
    }
}


