package ru.spbstu.hsai;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import java.util.List;
import static org.testng.Assert.*;

public class HomePageNavigationTestsResult extends DriverSetup {

    // 1. Проверка заголовка страницы
    @Test(priority = 1)
    public void testPageTitle() {
        String title = driver.getTitle();
        assertEquals(title, "Home Page",
                "Заголовок вкладки не совпадает с ожидаемым 'Home Page'. Получено: " + title);

        // Дополнительная проверка: заголовок не должен быть пустым
        assertFalse(title.trim().isEmpty(), "Заголовок страницы пустой");

        // Проверка, что заголовок содержит ключевое слово
        assertTrue(title.toLowerCase().contains("home"),
                "Заголовок должен содержать слово 'home'");
    }

    // 2. Проверка количества пунктов в верхнем меню
    @Test(priority = 3)
    public void testHeaderMenuCount() {
        List<WebElement> menuItems = driver.findElements(By.cssSelector("ul.uui-navigation.nav > li"));
        assertEquals(menuItems.size(), 4,
                "Количество пунктов в верхнем меню должно быть 4. Найдено: " + menuItems.size());
    }

    // 3. Проверка текстов пунктов верхнего меню
    @Test(priority = 4, dependsOnMethods = "testHeaderMenuCount")
    public void testHeaderMenuTexts() {
        List<WebElement> menuItems = driver.findElements(By.cssSelector("ul.uui-navigation.nav > li"));
        String[] expectedTexts = {"HOME", "CONTACT FORM", "SERVICE", "METALS & COLORS"};

        assertEquals(menuItems.size(), expectedTexts.length,
                "Количество элементов меню не совпадает с количеством ожидаемых текстов");

        for (int i = 0; i < expectedTexts.length; i++) {
            String actualText = menuItems.get(i).getText().trim().toUpperCase();
            assertEquals(actualText, expectedTexts[i],
                    "Текст пункта меню #" + (i+1) + " неверен. Ожидалось: '" + expectedTexts[i] +
                            "', получено: '" + actualText + "'");
        }
    }

    // 4. Проверка ссылок верхнего меню (href атрибуты)
    @Test(priority = 5, dependsOnMethods = "testHeaderMenuTexts")
    public void testHeaderMenuLinks() {
        List<WebElement> menuLinks = driver.findElements(By.cssSelector("ul.uui-navigation.nav > li > a"));

        assertEquals(menuLinks.size(), 4, "В верхнем меню должно быть 4 ссылки");

        String[] expectedHrefs = {"index.html", "contact.html", "service.html", "metals.html"};

        for (int i = 0; i < menuLinks.size(); i++) {
            WebElement link = menuLinks.get(i);
            String href = link.getAttribute("href");

            // Проверка, что ссылка отображается и кликабельна
            assertTrue(link.isDisplayed(), "Ссылка меню #" + (i+1) + " не отображается");
            assertTrue(link.isEnabled(), "Ссылка меню #" + (i+1) + " не активна");

            // Если href есть — проверяем его содержание
            if (href != null && !href.trim().isEmpty()) {
                boolean containsExpected = false;
                for (String expected : expectedHrefs) {
                    if (href.toLowerCase().contains(expected.toLowerCase())) {
                        containsExpected = true;
                        break;
                    }
                }
                assertTrue(containsExpected || href.endsWith("#") || href.contains("jdi-testing"),
                        "href пункта меню #" + (i+1) + " должен содержать один из ожидаемых паттернов: " +
                                String.join(", ", expectedHrefs) + ". Получено: " + href);
            }
        }
    }

    // 5. Проверка кликабельности пунктов меню
    @Test(priority = 6, dependsOnMethods = "testHeaderMenuLinks")
    public void testHeaderMenuClickable() {
        List<WebElement> menuItems = driver.findElements(By.cssSelector("ul.uui-navigation.nav > li > a"));

        for (int i = 0; i < menuItems.size(); i++) {
            WebElement item = menuItems.get(i);

            // Проверка, что элемент отображается и активен (основная проверка кликабельности)
            assertTrue(item.isDisplayed(), "Пункт меню #" + (i+1) + " не отображается");
            assertTrue(item.isEnabled(), "Пункт меню #" + (i+1) + " не активен");
        }
    }

    // 6. Проверка имени залогиненного пользователя
    @Test(priority = 7)
    public void testLoggedInUserName() {
        WebElement userNameElement = driver.findElement(By.id("user-name"));
        assertTrue(userNameElement.isDisplayed(), "Элемент с именем пользователя не найден");

        String userName = userNameElement.getText().trim();
        assertFalse(userName.isEmpty(), "Имя пользователя пустое");

        // Проверка формата имени (должно содержать имя и фамилию)
        assertTrue(userName.toUpperCase().contains("ROMAN") || userName.toUpperCase().contains("IOVLEV"),
                "Неверное имя пользователя: " + userName + ". Ожидалось содержание 'ROMAN' или 'IOVLEV'");

        // Проверка, что имя не содержит специальных символов (кроме пробелов)
        assertTrue(userName.matches("^[a-zA-Z\\s]+$"),
                "Имя пользователя содержит недопустимые символы: " + userName);
    }

    // 7. Проверка аватара/иконки пользователя
    @Test(priority = 8, dependsOnMethods = "testLoggedInUserName")
    public void testUserAvatar() {
        WebElement userIcon = driver.findElement(By.cssSelector(".profile-photo, .user-icon"));

        // Основная проверка: элемент существует и видим
        assertTrue(userIcon.isDisplayed(), "Иконка пользователя не отображается");
        assertTrue(userIcon.isEnabled(), "Иконка пользователя не активна");

        // Проверяем, что элемент имеет размеры (не пустой)
        int width = userIcon.getSize().getWidth();
        int height = userIcon.getSize().getHeight();
        assertTrue(width > 0 && height > 0,
                "Иконка пользователя имеет нулевые размеры: " + width + "x" + height);

        // Проверяем наличие любого визуального контента
        // (текст, изображение, иконка) — элемент не должен быть полностью пустым
        String tagName = userIcon.getTagName();
        String innerText = userIcon.getText().trim();
        String className = userIcon.getAttribute("class");

        // Либо есть текст, либо класс содержит "icon"/"avatar"/"photo", либо это img/svg
        boolean hasVisualContent =
                !innerText.isEmpty() ||
                        className.toLowerCase().matches(".*(icon|avatar|photo|user).*") ||
                        tagName.equals("img") || tagName.equals("svg");

        assertTrue(hasVisualContent,
                "Элемент иконки пользователя не содержит визуального контента: tag=" + tagName +
                        ", class=" + className + ", text='" + innerText + "'");
    }
    // 8. Проверка логотипа сайта
    @Test(priority = 10)
    public void testSiteLogo() {
        // Поиск логотипа по различным селекторам
        WebElement logo = null;
        try {
            logo = driver.findElement(By.cssSelector(".brand-logo, .logo, img[alt*='logo'], .jdi-logo"));
        } catch (Exception e) {
            // Если не нашли по классам, ищем первый img в header
            logo = driver.findElement(By.cssSelector("header img"));
        }

        assertNotNull(logo, "Логотип сайта не найден");
        assertTrue(logo.isDisplayed(), "Логотип не отображается");

        // Проверка alt атрибута
        String alt = logo.getAttribute("alt");
        assertNotNull(alt, "У логотипа отсутствует атрибут alt");

        // Проверка кликабельности логотипа (должен вести на главную)
        WebElement logoLink = logo.findElement(By.xpath("./ancestor::a[1]"));
        assertNotNull(logoLink, "Логотип не обернут в ссылку");

        String href = logoLink.getAttribute("href");
        assertNotNull(href, "У ссылки логотипа отсутствует href");
        assertTrue(href.contains("index") || href.endsWith("/") || href.contains("jdi-testing"),
                "Логотип должен вести на главную страницу");
    }

    // 9. Проверка футера страницы
    @Test(priority = 11)
    public void testFooterContent() {
        // Проверяем наличие футера (не обязательно тега <footer>, может быть div/class)
        WebElement footer = driver.findElement(By.cssSelector("footer, .footer, [role='contentinfo']"));
        assertTrue(footer.isDisplayed(), "Футер страницы не отображается");

        // Проверка, что футер содержит какой-либо текст
        String footerText = footer.getText().trim();
        assertFalse(footerText.isEmpty(), "Текст футера пустой");

        // Опциональная проверка: футер должен содержать хотя бы одно из ожидаемых слов
        // (если структура сайта изменится, тест не упадёт критично)
        boolean hasExpectedContent =
                footerText.toLowerCase().contains("©") ||
                        footerText.toLowerCase().contains("copyright") ||
                        footerText.matches(".*\\d{4}.*") ||
                        footerText.toLowerCase().contains("jdi") ||
                        footerText.toLowerCase().contains("epam");

        assertTrue(hasExpectedContent,
                "Футер не содержит ожидаемого контента. Текст: '" + footerText + "'");
    }

    // 10. Проверка responsive элементов (наличие классов адаптивности)
    @Test(priority = 12)
    public void testResponsiveClasses() {
        // Проверка наличия container или wrapper классов
        WebElement mainContainer = driver.findElement(By.cssSelector(".container, .wrapper, .main-content"));
        assertNotNull(mainContainer, "Отсутствует основной контейнер страницы");

        // Проверка, что элементы имеют классы для адаптивности
        String containerClass = mainContainer.getAttribute("class");
        assertTrue(containerClass.contains("container") ||
                        containerClass.contains("wrapper") ||
                        containerClass.contains("content"),
                "Основной контейнер должен иметь класс container/wrapper/content");
    }
}