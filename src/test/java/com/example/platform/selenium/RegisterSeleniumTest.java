package com.example.platform.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste Selenium per faqen e Regjistrimit (/register.html).
 */
@DisplayName("Selenium – Testimi i Regjistrimit")
class RegisterSeleniumTest extends SeleniumBase {

    @Test
    @DisplayName("Faqja /register.html ngarkohet me sukses")
    void registerPage_loads_correctly() {
        goTo("/register.html");

        waitFor(By.id("emri"));

        assertTrue(driver.findElement(By.id("emri")).isDisplayed());
        assertTrue(driver.findElement(By.id("atesia")).isDisplayed());
        assertTrue(driver.findElement(By.id("mbiemri")).isDisplayed());
        assertTrue(driver.findElement(By.id("nrTel")).isDisplayed());
        assertTrue(driver.findElement(By.id("datelindja")).isDisplayed());
        assertTrue(driver.findElement(By.id("email")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElement(By.id("confirmPassword")).isDisplayed());
    }

    @Test
    @DisplayName("Regjistrim me te dhena valide → shfaq mesazh suksesi")
    void register_withValidData_showsSuccess() {
        fillAndSubmit(
                "Redon", "Hysni", "Agushi",
                "+355692222222", "1998-06-15",
                "selenium.reg@test.com", "Test@1234", "Test@1234"
        );

        WebElement ok = waitFor(By.id("ok"));
        assertTrue(ok.isDisplayed());
    }

    @Test
    @DisplayName("Password nuk perputhet → shfaq gabim frontend")
    void register_passwordMismatch_showsFrontendError() {
        fillAndSubmit(
                "Redon", "Hysni", "Agushi",
                "+355693333333", "1998-06-15",
                "mismatch@test.com", "Test@1234", "Wrong@1234"
        );

        WebElement msg = waitFor(By.id("msg"));
        assertTrue(msg.isDisplayed());
    }

    @Test
    @DisplayName("Password i dobet → shfaq gabim validimi")
    void register_weakPassword_showsError() {
        fillAndSubmit(
                "Redon", "Hysni", "Agushi",
                "+355694444444", "1998-06-15",
                "weak@test.com", "weak", "weak"
        );

        WebElement msg = waitFor(By.id("msg"));
        assertTrue(msg.isDisplayed());
    }

    @Test
    @DisplayName("Nr tel me format gabim → shfaq gabim")
    void register_invalidPhone_showsError() {
        fillAndSubmit(
                "Redon", "Hysni", "Agushi",
                "0682345678", "1998-06-15",
                "badphone@test.com", "Test@1234", "Test@1234"
        );

        WebElement msg = waitFor(By.id("msg"));
        assertTrue(msg.isDisplayed());
    }

    @Test
    @DisplayName("Email ekzistues (admin) → shfaq gabim nga serveri")
    void register_existingEmail_showsServerError() {
        fillAndSubmit(
                "Admin", "System", "Root",
                "+355695555555", "1990-01-01",
                "admin@email.com",
                "Test@1234", "Test@1234"
        );

        WebElement msg = waitFor(By.id("msg"));
        assertTrue(msg.isDisplayed());
    }

    @Test
    @DisplayName("Faqja ka lidhjen 'Login' → shkon te /login.html")
    void registerPage_hasLoginLink() {
        goTo("/register.html");

        waitFor(By.cssSelector("a[href='/login.html']")).click();
        waitForUrl("/login.html");
        assertTrue(driver.getCurrentUrl().contains("/login.html"));
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private void fillAndSubmit(String emri, String atesia, String mbiemri,
                               String nrTel, String datelindja,
                               String email, String password, String confirm) {
        goTo("/register.html");

        waitFor(By.id("emri")).sendKeys(emri);
        driver.findElement(By.id("atesia")).sendKeys(atesia);
        driver.findElement(By.id("mbiemri")).sendKeys(mbiemri);
        driver.findElement(By.id("nrTel")).sendKeys(nrTel);
        fillDate("datelindja", datelindja);   // JS per date input
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("confirmPassword")).sendKeys(confirm);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
}
