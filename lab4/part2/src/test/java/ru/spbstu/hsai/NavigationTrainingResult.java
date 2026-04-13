package ru.spbstu.hsai;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class NavigationTrainingResult extends DriverSetup {

    // Навигация на страницу Support через пункт меню "Service" → "Support"
    private void navigateToSupport() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            // Открыть подменю "Service"
            WebElement serviceMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Service')]")
            ));
            serviceMenu.click();
            Thread.sleep(500);

            // Кликнуть по пункту "Support" в выпавшем подменю
            WebElement supportLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Support')]")
            ));
            supportLink.click();
            Thread.sleep(1500);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось перейти на страницу Support", e);
        }
    }

    // Сохранение скриншота и HTML при падении теста для отладки
    private void saveDebugInfo(String testName, Exception e) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            src.renameTo(new File("error_" + testName + ".png"));

            String html = driver.getPageSource();
            FileWriter writer = new FileWriter("error_" + testName + ".html");
            writer.write(html);
            writer.close();

            System.out.println("DEBUG: Скриншот и HTML сохранены для теста " + testName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testSupportPageFullCheck() {
        SoftAssert softAssert = new SoftAssert();
        String testName = "SupportPage";

        try {
            // 1. Navigate to the "Support" page using the main menu navigation
            navigateToSupport();

            // 2. Assert that the page title contains the word "support" (case-insensitive)
            String title = driver.getTitle().toLowerCase();
            softAssert.assertTrue(
                    title.contains("support"),
                    "Page title should contain 'support'. Actual title: " + title
            );

            // 3. Assert that a form or contact block is present on the page
            // FORM[0] — скрытая login-форма в шапке (присутствует на всех страницах сайта)
            List<WebElement> forms = driver.findElements(By.tagName("form"));
            softAssert.assertFalse(forms.isEmpty(), "Form or contact block should be present on the page");

            if (!forms.isEmpty()) {
                WebElement form = forms.get(0);

                // 4. Assert that the form is NOT displayed and visible to the user
                // FORM[0] — login-форма скрыта (class="hidden"), isDisplayed() == false
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", form);
                Thread.sleep(500);
                softAssert.assertFalse(
                        form.isDisplayed(),
                        "Form should NOT be displayed and visible to the user"
                );

                // 5. Assert that the form contains at least one input field (<input>)
                // Исключаем hidden/submit/button — ищем поля ввода логина и пароля
                List<WebElement> inputs = form.findElements(By.xpath(
                        ".//input[not(@type='hidden') and not(@type='submit') and not(@type='button')]"
                ));
                softAssert.assertTrue(
                        inputs.size() >= 1,
                        "Form should contain at least one input field. Found: " + inputs.size()
                );

                // 6. Assert that the form does NOT contain a textarea element
                // Login-форма не содержит textarea — проверяем отсутствие
                List<WebElement> textAreas = form.findElements(By.tagName("textarea"));
                softAssert.assertTrue(
                        textAreas.isEmpty(),
                        "Form should NOT contain textarea element. But found: " + textAreas.size()
                );

                // 7. Assert that the form contains a submit button
                // Login-форма содержит <button> без атрибута type
                List<WebElement> submitButtons = form.findElements(By.xpath(
                        ".//button[@type='submit'] | .//input[@type='submit'] | .//button[not(@type)]"
                ));
                softAssert.assertTrue(
                        !submitButtons.isEmpty(),
                        "Form should contain a submit button. Found: " + submitButtons.size()
                );
            }

        } catch (Exception e) {
            saveDebugInfo(testName, e);
            softAssert.fail("Error in test " + testName + ": " + e.getMessage());
        }

        softAssert.assertAll();
    }
}