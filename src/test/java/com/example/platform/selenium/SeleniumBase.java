package com.example.platform.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

/**
 * Klasa baze per te gjitha testet Selenium.
 *
 * - Nis nje server Spring Boot me port te rastesishem (RANDOM_PORT)
 *   per te shmangur konfliktin kur aplikacioni eshte duke punuar.
 * - Krijon nje ChromeDriver per cdo test (izolim i plote).
 * - Ofron metoda ndihmuese: goTo(), loginAs(), waitFor(), waitForUrl().
 *
 * Kerkesa: Google Chrome te instaluar ne sistem.
 * ChromeDriver: C:\Users\User\Desktop\chromedriver.exe
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SeleniumBase {

    @LocalServerPort
    private int port;

    // kredencialet e admin-it te seeduara nga SeedConfig
    protected static final String ADMIN_EMAIL    = "admin@email.com";
    protected static final String ADMIN_PASSWORD = "Admin@123";

    protected WebDriver driver;
    protected WebDriverWait wait;

    /** URL baze dinamike bazuar ne portin e zgjedhur nga Spring. */
    protected String base() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUpDriver() {
        // shkarkon automatikisht ChromeDriver qe perputhet me Chrome-in e instaluar
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,800");

        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) driver.quit();
    }

    // ─── Metoda ndihmuese ────────────────────────────────────────────────────

    protected void goTo(String path) {
        driver.get(base() + path);
    }

    /** Navigon te /login.html, fut kredencialet dhe pret ridrejtimin. */
    protected void loginAs(String emailOrPhone, String password) {
        goTo("/login.html");

        driver.findElement(By.id("emailOrPhone")).sendKeys(emailOrPhone);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login.html")));
    }

    /** Prit derisa elementi te jete i dukshem. */
    protected WebElement waitFor(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Prit derisa URL te permbaje fragmentin e dhene. */
    protected void waitForUrl(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    /**
     * Mbush nje fushe date (type="date") nepermjet JavaScript,
     * pasi sendKeys nuk funksionon sakte ne Chrome headless.
     * @param elementId id-ja e input-it
     * @param isoDate   data ne formatin YYYY-MM-DD
     */
    protected void fillDate(String elementId, String isoDate) {
        WebElement el = driver.findElement(By.id(elementId));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];", el, isoDate);
    }
}