Feature: Login to check SauceDemo
Scenario: Check for valid login credentials
Given User is on login page
When User enters a valid login credentails
And Click on login Button
Then User navigate to home page
And Close the browser