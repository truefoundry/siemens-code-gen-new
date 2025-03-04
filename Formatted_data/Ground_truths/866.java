package Internal_User;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class TC02_Internal_User_Top_Ribbon_Functionality_Check
{

    @Test
    void Top_Ribbon_Functionality_Check()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.INTERNAL_USER, "866", tc ->
        {
            //Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions().startIncognito(), new CoreStartOptions());
            tc.browser.login(ETestData.FUNCTIONAL_USER);
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.button.click(EButton.CONTACT);
            WaitFor.condition(tc.modal::exists);
            String modalTitle = "Contact Support";
            String modalContent = "For any questions regarding this portal feel free to reach out to our E-Support team: 800-242-3233";
            tc.stepEvaluator
                    .add(() -> tc.modal.getTitle().equalsIgnoreCase(modalTitle), "modal title not found")
                    .add(() -> tc.modal.getContent().equalsIgnoreCase(modalContent), "Modal content not found");
            tc.addStepInfo("""
                    Popup with following text is displayed:
                    Contact Support
                    For any questions regarding this portal feel free to reach out to our E-Support team: 800-242-3233
                    For reference check attached screenshot
                    """, "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.button.click(EButton.CLOSE);
            tc.addStepInfo("Landing page is displayed", true, !tc.modal.exists(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.menu.select(EMenu.LANGUAGE);
            tc.addStepInfo("Options English and Deutsch are displayed.", true, tc.menu.isMenuItemsDisplayed()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.menu.selectFromDropDown(EMenu.LANGUAGE, "Deutsch");
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Willkommen"));
            boolean isLanguageGM = tc.browser.getPageTitle().contains("Willkommen");
            tc.addStepInfo("Landing page is displayed. Language is changed to german language.", true, isLanguageGM,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.menu.selectFromDropDown(EMenu.LANGUAGE, "English");
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Welcome"));
            boolean isLanguageEN = tc.browser.getPageTitle().contains("Welcome");
            tc.addStepInfo("Landing page is displayed. Language is changed to English language.", true, isLanguageEN,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.menu.select(EMenu.SETTINGS);
            tc.addStepInfo("Options 'Theme' and 'Email Notification Settings' are displayed.", true, tc.menu.isMenuItemsDisplayed()
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 8
            tc.menu.selectFromDropDown(EMenu.SETTINGS, "User Settings");
            WaitFor.specificTime(Duration.ofSeconds(2));
            WaitFor.condition(() -> tc.toggle.exists(EToggle.byIndex(0)));
            tc.addStepInfo("""
                    Email Notifications page is displayedAll notifications are turned on by default.
                    For reference check attached screenshot""", true, tc.toggle.exists(EToggle.byIndex(0))
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 9
            tc.stepEvaluator.reset();
            tc.toggle.check(EToggle.byIndex(1));
            tc.toggle.uncheck(EToggle.byIndex(1));
            tc.stepEvaluator.add(() -> tc.browser.getMessage().contains("Successfully updated settings") ,
                    "pop up not displayed after Unchecking toggle");
            tc.addStepInfo("Messsage 'Sucessfuly updated settings' is displayed after any notification option is changed",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.button.click(EButton.APP_NAME);
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTIONS_ABOUT_AN_ORDER));
            boolean isHopePageOpened = tc.browser.getPageTitle().contains("Welcome");
            tc.addStepInfo("User is redirected to landing page", true, isHopePageOpened,
                    new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


