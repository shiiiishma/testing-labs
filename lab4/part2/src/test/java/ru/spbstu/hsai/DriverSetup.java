package ru.spbstu.hsai;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class DriverSetup {

    // Общий экземпляр драйвера для всех тестовых классов пакета
    protected static WebDriver driver;

    // Выполняется один раз перед всеми тестами в suite
    // Требование TestNG: метод должен быть public static
    @BeforeTest
    public static void setup() {
        // Запуск браузера (Selenium Manager сам скачает драйвер)
        driver = new ChromeDriver();

        // 1. Open test site by URL
        driver.navigate().to("https://jdi-testing.github.io/jdi-light/index.html");

        // 3. Perform login
        driver.findElement(By.cssSelector("html > body > header > div > nav > ul.uui-navigation.navbar-nav.navbar-right > li > a > span")).click();
        driver.findElement(By.id("name")).sendKeys("Roman");
        driver.findElement(By.id("password")).sendKeys("Jdi1234");
        driver.findElement(By.id("login-button")).click();
    }

    // Выполняется после всех тестов: освобождение ресурсов
    // Требование TestNG: метод должен быть public static
    @AfterTest
    public static void exit() {
        //10. Close Browser
        driver.close();
    }
}