package stepDefinitions;

import common.BaseAPI;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import loginPage.LoginPage;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.PageFactory;

public class LoginPageSteps extends BaseAPI {
    static LoginPage loginPage = PageFactory.initElements(driver,
            LoginPage.class);




    @Given("user is using {string} browser and navigates to page: {string}")
    public void userIsUsingBrowserAndNavigatesToPage(String browserName, String pageURL) {
       openBrowser(browserName,pageURL,10);
       sleepForSeconds(5000);
    }

    @When("user provides emailID as {string}")
    public void userProvidesEmailIDAs(String emailID) {
        loginPage.enterEmailID(emailID);
        sleepForSeconds(3000);
    }

    @And("user provides password as {string}")
    public void userProvidesPasswordAs(String password) {
        loginPage.enterPassword(password);
        sleepForSeconds(3000);
    }

    @And("user clicks the login button")
    public void userClicksTheLoginButton() {
        loginPage.clickLogin();
        sleepForSeconds(3000);

    }


    @Then("user is redirected to the user profile url {string}")
    public void userIsRedirectedToTheUserProfileUrl(String expectedURL) {
        BaseAPI.assertURL(expectedURL);
    }

    @And("user verifies the title of the page is {string}")
    public void userVerifiesTheTitleOfThePageIs(String expectedTitle) {
        BaseAPI.assertTitle(expectedTitle);
    }



}
