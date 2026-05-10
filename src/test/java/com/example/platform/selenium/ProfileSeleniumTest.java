package com.example.platform.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste Selenium per faqen e Profilit (/profile.html).
 *
 * Testohet:
 *  - Ridrejtimi i user-it jo-loguar te login
 *  - Ngarkimi i te dhenave te profilit pas login-it
 *  - Perditesimi i suksesshem i profilit
 *  - Validimi i fushave ne frontend
 *  - Prania e seksionit te upload-it te fotos
 */
@DisplayName("Selenium – Testimi i Profilit")
class ProfileSeleniumTest extends SeleniumBase {

    @Test
    @DisplayName("Vizitor jo-loguar → ridrejtohet te /login.html")
    void profilePage_unauthenticated_redirectsToLogin() {
        goTo("/profile.html");

        waitForUrl("/login.html");
        assertTrue(driver.getCurrentUrl().contains("/login.html"),
                "User-i jo-loguar duhet ridrejtuar te login, por URL eshte: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Pas login-it si admin → /profile.html ngarkohet me te dhena")
    void profilePage_afterAdminLogin_loadsData() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);

        goTo("/profile.html");

        // emri i adminit duhet te shfaqet ne fushe
        WebElement emriInput = waitFor(By.id("emri"));
        assertFalse(emriInput.getAttribute("value").isBlank(),
                "Fusha 'emri' duhet te jete e mbushur me te dhenat e profilit");
    }

    @Test
    @DisplayName("Badge-i i roles shfaqet si ADMIN")
    void profilePage_roleBadge_showsAdminRole() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        goTo("/profile.html");

        WebElement badge = waitFor(By.id("roleBadge"));
        assertEquals("ADMIN", badge.getText().trim());
    }

    @Test
    @DisplayName("Ruajtja e profilit me te dhena valide → mesazh suksesi")
    void saveProfile_withValidData_showsSuccess() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        goTo("/profile.html");

        // prit derisa fushat te ngarkohen
        waitFor(By.id("emri"));

        // pastro dhe vendos vlera te reja
        WebElement emriInput = driver.findElement(By.id("emri"));
        emriInput.clear();
        emriInput.sendKeys("Admin");

        WebElement atesiaInput = driver.findElement(By.id("atesia"));
        atesiaInput.clear();
        atesiaInput.sendKeys("System");

        WebElement mbiemriInput = driver.findElement(By.id("mbiemri"));
        mbiemriInput.clear();
        mbiemriInput.sendKeys("Root");

        driver.findElement(By.id("btnSave")).click();

        WebElement ok = waitFor(By.id("ok"));
        assertTrue(ok.isDisplayed(), "Mesazhi i suksesit duhet te shfaqet pas ruajtjes");
    }

    @Test
    @DisplayName("Emri me karakter jo-alfabetik → gabim validimi")
    void saveProfile_withInvalidEmri_showsError() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        goTo("/profile.html");

        waitFor(By.id("emri"));

        WebElement emriInput = driver.findElement(By.id("emri"));
        emriInput.clear();
        emriInput.sendKeys("Admin123!"); // jo-valid

        driver.findElement(By.id("btnSave")).click();

        WebElement msg = waitFor(By.id("msg"));
        assertTrue(msg.isDisplayed(), "Gabimi i validimit duhet te shfaqet");
    }

    @Test
    @DisplayName("Seksioni i upload-it te fotos eshte prezent")
    void profilePage_uploadSection_isPresent() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        goTo("/profile.html");

        // prit ngarkimin e profilit (API call asinkron)
        waitFor(By.id("emri"));

        assertFalse(driver.findElements(By.id("file")).isEmpty(),
                "Input-i per upload foto duhet te ekzistoje");
        // prit derisa butoni te behet i dukshem
        waitFor(By.id("btnUpload"));
        assertTrue(driver.findElement(By.id("btnUpload")).isDisplayed(),
                "Butoni Upload duhet te jete i dukshem");
    }

    @Test
    @DisplayName("Foto e profilit (img#photo) eshte prezente ne DOM")
    void profilePage_photoElement_exists() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        goTo("/profile.html");

        waitFor(By.id("emri")); // prit ngarkimin
        assertFalse(driver.findElements(By.id("photo")).isEmpty(),
                "Elementi img#photo duhet te ekzistoje");
    }

    @Test
    @DisplayName("Butoni 'Logout' → pastron session dhe shkon te Hub")
    void logoutButton_clearsSessionAndRedirects() {
        loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        goTo("/profile.html");

        waitFor(By.id("emri"));

        // kliko logout
        driver.findElement(By.xpath("//a[contains(text(),'Logout') or @onclick='logout()']")).click();

        waitForUrl("/index.html");
        assertTrue(driver.getCurrentUrl().contains("/index.html") ||
                   driver.getCurrentUrl().endsWith("/"));
    }
}