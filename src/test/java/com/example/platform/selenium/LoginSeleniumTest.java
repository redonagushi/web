package com.example.platform.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste Selenium per faqen e Login-it (/login.html).
 */
@DisplayName("Selenium – Testimi i Login-it")
class LoginSeleniumTest extends SeleniumBase {

    @Test
    @DisplayName("Faqja /login.html ngarkohet me sukses")
    void loginPage_loads_correctly() {
        goTo("/login.html");

        // prit derisa forma te ngarkohet
        waitFor(By.id("emailOrPhone"));

        assertEquals("Login – Platform", driver.getTitle());
        assertTrue(driver.findElement(By.id("emailOrPhone")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed());
    }

    @Test
    @DisplayName("Login me kredenciale valide (admin) → ridrejton te /admin.html")
    void login_withAdminCredentials_redirectsToAdmin() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        waitForUrl("/admin.html");
        assertTrue(driver.getCurrentUrl().contains("/admin.html"));
    }

    @Test
    @DisplayName("Login me password gabim → shfaq mesazh gabimi")
    void login_withWrongPassword_showsError() {
        goTo("/login.html");

        waitFor(By.id("emailOrPhone")).sendKeys(ADMIN_EMAIL);
        driver.findElement(By.id("password")).sendKeys("WrongPassword@1");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement msg = waitFor(By.id("msg"));
        assertTrue(msg.isDisplayed());
        assertFalse(msg.getText().isBlank());
    }

    @Test
    @DisplayName("Login me email joekzistues → shfaq mesazh gabimi")
    void login_withNonExistentEmail_showsError() {
        goTo("/login.html");

        waitFor(By.id("emailOrPhone")).sendKeys("nobody@nowhere.com");
        driver.findElement(By.id("password")).sendKeys("SomePass@1");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement msg = waitFor(By.id("msg"));
        assertTrue(msg.isDisplayed());
    }

    @Test
    @DisplayName("Faqja ka lidhjen 'Signup' → shkon te /register.html")
    void loginPage_hasSignupLink() {
        goTo("/login.html");

        waitFor(By.cssSelector("a[href='/register.html']")).click();
        waitForUrl("/register.html");
        assertTrue(driver.getCurrentUrl().contains("/register.html"));
    }

    @Test
    @DisplayName("Faqja ka lidhjen 'Home' → shkon te /index.html")
    void loginPage_hasHomeLink() {
        goTo("/login.html");

        waitFor(By.cssSelector("a[href='/index.html']")).click();
        waitForUrl("/index.html");
        assertTrue(driver.getCurrentUrl().contains("/index.html") ||
                   driver.getCurrentUrl().endsWith("/"));
    }

    @Test
    @DisplayName("Login me nr tel → ridrejton te /profile.html")
    void login_withPhoneNumber_redirectsToProfile() {
        // regjistro user me nr tel
        registerUser("tel.login@test.com", "+355691111111");

        // login me nr tel
        goTo("/login.html");
        waitFor(By.id("emailOrPhone")).sendKeys("+355691111111");
        driver.findElement(By.id("password")).sendKeys("Test@1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrl("/profile.html");
        assertTrue(driver.getCurrentUrl().contains("/profile.html"));
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private void registerUser(String email, String phone) {
        goTo("/register.html");
        waitFor(By.id("emri")).sendKeys("Testi");
        driver.findElement(By.id("atesia")).sendKeys("Testit");
        driver.findElement(By.id("mbiemri")).sendKeys("Testimir");
        driver.findElement(By.id("nrTel")).sendKeys(phone);
        fillDate("datelindja", "1998-06-15");
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys("Test@1234");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitFor(By.id("ok"));
    }
}