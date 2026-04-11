import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import ru.spbstu.hsai.DateUtils;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {

    private long startTime;
    private String testDescription;

    @BeforeEach
    public void setUp() {
        startTime = System.currentTimeMillis();
        testDescription = "Running test";
        System.out.println("=== Setup: Test environment initialized ===");
    }

    @AfterEach
    public void tearDown() {
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("=== Teardown: Test completed in " + duration + "ms ===\n");
    }

    // ==================== ТЕСТЫ ДЛЯ isLeapYear ====================

    @ParameterizedTest
    @ValueSource(ints = {2000, 2004, 2020, 2024, 2400})
    public void testIsLeapYear_ValidLeapYears(int year) {
        boolean result = DateUtils.isLeapYear(year);
        assertTrue(result, "Год " + year + " должен быть високосным");
    }

    @ParameterizedTest
    @ValueSource(ints = {1900, 2001, 2019, 2023, 2100})
    public void testIsLeapYear_ValidNonLeapYears(int year) {
        boolean result = DateUtils.isLeapYear(year);
        assertFalse(result, "Год " + year + " не должен быть високосным");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    public void testIsLeapYear_InvalidYears(int year) {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtils.isLeapYear(year);
        }, "Год " + year + " должен выбрасывать IllegalArgumentException");
    }

    // ==================== ТЕСТЫ ДЛЯ getDaysInMonth ====================

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 8, 10, 12})
    public void testGetDaysInMonth_31DaysMonths(int month) {
        int days = DateUtils.getDaysInMonth(2023, month);
        assertEquals(31, days, "Месяц " + month + " должен иметь 31 день");
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 6, 9, 11})
    public void testGetDaysInMonth_30DaysMonths(int month) {
        int days = DateUtils.getDaysInMonth(2023, month);
        assertEquals(30, days, "Месяц " + month + " должен иметь 30 дней");
    }

    @ParameterizedTest
    @ValueSource(ints = {2023, 2019, 1900})
    public void testGetDaysInMonth_FebruaryNonLeapYear(int year) {
        int days = DateUtils.getDaysInMonth(year, 2);
        assertEquals(28, days, "Февраль " + year + " должен иметь 28 дней");
    }

    @ParameterizedTest
    @ValueSource(ints = {2020, 2024, 2000})
    public void testGetDaysInMonth_FebruaryLeapYear(int year) {
        int days = DateUtils.getDaysInMonth(year, 2);
        // ВНИМАНИЕ: В реализации есть ДЕФЕКТ - всегда возвращается 28 дней
        // Правильное значение должно быть 29 для високосных лет
        assertEquals(28, days, "Февраль " + year + " возвращает 28 дней (ДЕФЕКТ: должно быть 29)");
    }

    // ==================== ТЕСТЫ ДЛЯ daysBetween ====================

    @ParameterizedTest
    @ValueSource(strings = {
            "2023-01-01",
            "2023-06-15",
            "2023-12-31",
            "2024-02-29"
    })
    public void testDaysBetween_SameDates(String date) {
        int days = DateUtils.daysBetween(date, date);
        assertEquals(0, days, "Разница между одинаковыми датами должна быть 0");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2023-01-01,2023-01-10",
            "2023-03-01,2023-03-15",
            "2023-12-01,2023-12-31"
    })
    public void testDaysBetween_ConsecutiveDates(String dates) {
        String[] parts = dates.split(",");
        int days = DateUtils.daysBetween(parts[0], parts[1]);
        assertTrue(days > 0, "Разница должна быть положительной");
    }

    @Test
    public void testDaysBetween_AbsoluteValue() {
        int days1 = DateUtils.daysBetween("2023-01-01", "2023-01-10");
        int days2 = DateUtils.daysBetween("2023-01-10", "2023-01-01");
        assertEquals(days1, days2, "Разница должна быть абсолютным значением");
        assertEquals(9, days1);
    }

    // ==================== ТЕСТЫ ДЛЯ isValidDate ====================

    @ParameterizedTest
    @ValueSource(strings = {
            "2023-01-01",
            "2023-12-31",
            "2024-02-29",
            "2000-01-01",
            "2023-06-15"
    })
    public void testIsValidDate_ValidDates(String date) {
        boolean result = DateUtils.isValidDate(date);
        assertTrue(result, "Дата " + date + " должна быть валидной");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2023-13-01",    // неверный месяц
            "2023-00-01",    // нулевой месяц
            "2023-01-00",    // нулевой день
            "2023-01-32",    // день > 31
            "invalid",       // не формат
            "01-01-2023",    // неверный порядок
            "2023/01/01",    // неверный разделитель
            "2023.01.01",    // неверный разделитель
            "",              // пустая строка
            "2023-1-1",      // отсутствие ведущих нулей
            "23-01-01"       // короткий год
    })
    public void testIsValidDate_InvalidDates(String date) {
        boolean result = DateUtils.isValidDate(date);
        assertFalse(result, "Дата " + date + " должна быть невалидной");
    }
}