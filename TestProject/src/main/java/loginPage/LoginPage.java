package loginPage;

import common.BaseAPI;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.Assert;

import static loginPage.LoginPageElements.*;

public class LoginPage extends BaseAPI {

    @FindBy(how = How.ID, using = emailBoxID)
    public WebElement emailBox;
    @FindBy(how = How.ID, using = passwordBoxID)
    public WebElement passwordBox;
    @FindBy(how = How.ID, using = loginButtonXpath)
    public WebElement getLoginButton;





    public void enterEmailID(String emailID) {
        emailBox.clear();
        emailBox.sendKeys(emailID);
    }

    public void enterPassword(String password) {
        passwordBox.clear();
        passwordBox.sendKeys(password);
    }

    public void clickLogin() {
        getLoginButton.click();
    }

}
