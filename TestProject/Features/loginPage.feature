Feature: Login Portal

  Scenario: User is able to successfully login
    Given user is using "chrome" browser and navigates to page: "https://admin-demo.nopcommerce.com/"
    When user provides emailID as "admin@example.com"
    And user provides password as "admin"
    And user clicks the login button
    Then user is redirected to the user profile url "https://admin-demo.nopcommerce.com/Admin"
    And user verifies the title of the page is "Dashboard / nopCommerce administration"

