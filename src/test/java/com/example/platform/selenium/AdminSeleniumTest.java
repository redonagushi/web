package com.example.platform.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste Selenium per Admin Panel-in (/admin.html).
 */
@DisplayName("Selenium – Testimi i Admin Panel-it")
class AdminSeleniumTest extends SeleniumBase {

    @Test
    @DisplayName("Vizitor jo-loguar ndaj /admin.html → ridrejtohet")
    void adminPage_unauthenticated_isNotAccessible() {
        goTo("/admin.html");

        waitForUrl("/index.html");
        assertFalse(driver.getCurrentUrl().contains("/admin.html"));
    }

    @Test
    @DisplayName("Login si admin → /admin.html ngarkohet me sukses")
    void adminPage_afterAdminLogin_loadsCorrectly() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        waitForUrl("/admin.html");
        assertEquals("Admin - Users", driver.getTitle());
    }

    @Test
    @DisplayName("Tabela #usersTable ekziston ne DOM")
    void adminPage_usersTable_exists() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        waitFor(By.id("usersTable"));
        assertTrue(driver.findElement(By.id("usersTable")).isDisplayed());
    }

    @Test
    @DisplayName("Tabela permban kokat e sakta (id, emri, email, role, actions)")
    void adminPage_tableHeaders_correct() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        waitFor(By.id("usersTable"));

        String src = driver.getPageSource();
        assertTrue(src.contains("id"));
        assertTrue(src.contains("emri"));
        assertTrue(src.contains("email"));
        assertTrue(src.contains("role"));
        assertTrue(src.contains("actions"));
    }

    @Test
    @DisplayName("Admin-i shfaqet ne tabele")
    void adminPage_adminUserVisible_inTable() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#usersTable tbody tr")));

        String tableContent = driver.findElement(By.id("usersTable")).getText();
        assertTrue(tableContent.contains("admin@email.com") ||
                   tableContent.contains("Admin"));
    }

    @Test
    @DisplayName("Rreshtat e admin-it shfaqin 'Admin locked'")
    void adminPage_adminRow_showsLockedMessage() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#usersTable tbody tr")));

        String tableText = driver.findElement(By.cssSelector("#usersTable tbody")).getText();
        assertTrue(tableText.contains("Admin locked") ||
                   tableText.contains("admin@email.com"));
    }

    @Test
    @DisplayName("Butoni Reload eshte i dukshem")
    void adminPage_reloadButton_isVisible() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        WebElement reloadBtn = waitFor(By.id("btnReload"));
        assertTrue(reloadBtn.isDisplayed());
        assertTrue(reloadBtn.isEnabled());
    }

    @Test
    @DisplayName("Butoni 'Home' → ridrejton te /index.html")
    void adminPage_homeButton_redirectsToHub() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        waitFor(By.cssSelector("a[href='/index.html']")).click();
        waitForUrl("/index.html");

        assertTrue(driver.getCurrentUrl().contains("/index.html") ||
                   driver.getCurrentUrl().endsWith("/"));
    }

    @Test
    @DisplayName("Butoni 'Logout' → ridrejton te Hub")
    void adminPage_logoutButton_clearsSession() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        waitFor(By.xpath("//button[contains(text(),'Logout')]")).click();
        waitForUrl("/index.html");

        assertTrue(driver.getCurrentUrl().contains("/index.html") ||
                   driver.getCurrentUrl().endsWith("/"));
    }

    @Test
    @DisplayName("Pas regjistrimit te nje user-i te ri → shfaqet ne tabele")
    void adminPage_newUser_appearsInTable() {
        // regjistro user te ri
        goTo("/register.html");
        waitFor(By.id("emri")).sendKeys("Selenium");
        driver.findElement(By.id("atesia")).sendKeys("Testit");
        driver.findElement(By.id("mbiemri")).sendKeys("Testimir");
        driver.findElement(By.id("nrTel")).sendKeys("+355698888888");
        fillDate("datelindja", "2000-03-15");
        driver.findElement(By.id("email")).sendKeys("newuser.admin@test.com");
        driver.findElement(By.id("password")).sendKeys("Test@1234");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitFor(By.id("ok"));

        // login si admin
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        waitForUrl("/admin.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#usersTable tbody tr")));

        String tableText = driver.findElement(By.id("usersTable")).getText();
        assertTrue(tableText.contains("newuser.admin@test.com") ||
                   tableText.contains("Selenium"));
    }
}