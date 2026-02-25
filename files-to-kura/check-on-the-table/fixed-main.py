class LongArithmetic:
    def __init__(self, M: int, N: int):
        if M <= 0 or N <= 0:
            raise ValueError("M и N должны быть положительными числами")
        self.M = M
        self.N = N
    
    @staticmethod
    def _normalize(digits: list) -> list:
        if not digits:
            return [0]
        
        # Находим первый ненулевой элемент
        i = 0
        while i < len(digits) - 1 and digits[i] == 0:
            i += 1
        return digits[i:]
    
    def from_int(self, num: int) -> list:
        if num < 0:
            raise ValueError("Число должно быть неотрицательным")
        
        if num == 0:
            return [0]
        
        digits = []
        while num > 0:
            digits.append(num % self.M)
            num //= self.M
        
        digits.reverse()
        return digits
    
    def to_int(self, digits: list) -> int:
        normalized = self._normalize(digits)
        result = 0
        for digit in normalized:
            result = result * self.M + digit
        return result
    
    def compare(self, a: list, b: list) -> int:
        a_norm = self._normalize(a[:])
        b_norm = self._normalize(b[:])
        
        if len(a_norm) > len(b_norm):
            return 1
        if len(a_norm) < len(b_norm):
            return -1
        
        for x, y in zip(a_norm, b_norm):
            if x > y:
                return 1
            if x < y:
                return -1
        return 0
    
    def _align_length(self, a: list, b: list) -> tuple:
        max_len = max(len(a), len(b))
        a_aligned = [0] * (max_len - len(a)) + a
        b_aligned = [0] * (max_len - len(b)) + b
        return a_aligned, b_aligned
    
    def add(self, a: list, b: list) -> list:
        a_aligned, b_aligned = self._align_length(a, b)
        
        carry = 0
        result = [0] * (max(len(a_aligned), len(b_aligned)) + 1)
        
        for i in range(len(a_aligned) - 1, -1, -1):
            total = a_aligned[i] + b_aligned[i] + carry
            result[i + 1] = total % self.M
            carry = total // self.M
        
        result[0] = carry
        return self._normalize(result)
    
    def subtract(self, a: list, b: list) -> list:
        # Проверяем, что a >= b
        if self.compare(a, b) < 0:
            raise ValueError("Вычитание дает отрицательный результат, "
                           "что не поддерживается текущей реализацией")
        
        a_aligned, b_aligned = self._align_length(a, b)
        
        result = [0] * len(a_aligned)
        borrow = 0
        
        for i in range(len(a_aligned) - 1, -1, -1):
            val = a_aligned[i] - b_aligned[i] - borrow
            if val < 0:
                val += self.M
                borrow = 1
            else:
                borrow = 0
            result[i] = val
        
        return self._normalize(result)
    
    def _multiply_by_digit(self, num: list, digit: int) -> list:
        if digit == 0 or (len(num) == 1 and num[0] == 0):
            return [0]
        
        result = [0] * (len(num) + 1)
        carry = 0
        
        for i in range(len(num) - 1, -1, -1):
            product = num[i] * digit + carry
            result[i + 1] = product % self.M
            carry = product // self.M
        
        result[0] = carry
        return self._normalize(result)
    
    def multiply(self, a: list, b: list) -> list:
        # Проверка на ноль
        if (len(a) == 1 and a[0] == 0) or (len(b) == 1 and b[0] == 0):
            return [0]
        
        # Для оптимизации умножаем более короткое число на более длинное
        if len(a) < len(b):
            a, b = b, a
        
        result = [0]
        
        for i, digit in enumerate(reversed(b)):
            # Умножаем a на текущую цифру b
            partial = self._multiply_by_digit(a, digit)
            
            # Добавляем нули в конец (сдвиг влево на i разрядов)
            partial = partial + [0] * i
            
            # Суммируем с результатом
            result = self.add(result, partial)
        
        return result
    
    def divide(self, a: list, b: list) -> list:
        # Проверка деления на ноль
        if len(b) == 1 and b[0] == 0:
            raise ZeroDivisionError("Деление на ноль")
        
        # Проверка, что a >= b
        if self.compare(a, b) < 0:
            return [0]
        
        a_norm = self._normalize(a[:])
        b_norm = self._normalize(b[:])
        
        quotient = []
        remainder = [0]
        
        for digit in a_norm:
            # Добавляем следующую цифру к остатку
            if len(remainder) == 1 and remainder[0] == 0:
                remainder = [digit]
            else:
                remainder = remainder + [digit]
            
            remainder = self._normalize(remainder)
            
            # Бинарный поиск цифры частного
            low, high = 0, self.M - 1
            q_digit = 0
            
            while low <= high:
                mid = (low + high) // 2
                product = self._multiply_by_digit(b_norm, mid)
                
                if self.compare(product, remainder) <= 0:
                    q_digit = mid
                    low = mid + 1
                else:
                    high = mid - 1
            
            quotient.append(q_digit)
            
            if q_digit > 0:
                product = self._multiply_by_digit(b_norm, q_digit)
                remainder = self.subtract(remainder, product)
        
        return self._normalize(quotient)
    
    def print_number(self, num: list, name: str) -> None:
        print(f"{name} = {num} --- {self.to_int(num)}")


def main():
    print="=" * 60
    print("ПРОГРАММНЫЙ МОДУЛЬ 1: Длинная арифметика")
    print("=" * 60)
    
    print("\n" + "=" * 60)
    print("Интерактивный режим")
    print("=" * 60)
    
    # Интерактивный режим
    try:
        # Ввод параметров
        M = int(input("Введите основание системы счисления M (M > 0): "))
        N = int(input("Введите количество разрядов N (N > 0): "))
        
        # Создаем экземпляр класса
        la = LongArithmetic(M, N)
        
        # Ввод чисел
        a_val = int(input("Введите первое число a (a >= 0): "))
        b_val = int(input("Введите второе число b (b >= 0): "))
        
        # Преобразование
        a = la.from_int(a_val)
        b = la.from_int(b_val)
        
        print("\nРезультаты:")
        print("-" * 40)
        
        la.print_number(a, "a")
        la.print_number(b, "b")
        
        # Выполнение операций
        sum_result = la.add(a, b)
        la.print_number(sum_result, "Сумма (a + b)")
        
        if la.compare(a, b) >= 0:
            sub_result = la.subtract(a, b)
            la.print_number(sub_result, "Разность (a - b)")
        else:
            print("Разность (a - b): a < b, вычитание не поддерживается")
        
        mul_result = la.multiply(a, b)
        la.print_number(mul_result, "Произведение (a * b)")
        
        if not (len(b) == 1 and b[0] == 0):
            div_result = la.divide(a, b)
            la.print_number(div_result, "Частное (a // b)")
        else:
            print("Частное (a // b): деление на ноль")
            
    except ValueError as e:
        print(f"Ошибка ввода: {e}")
    except ZeroDivisionError as e:
        print(f"Ошибка: {e}")
    except Exception as e:
        print(f"Непредвиденная ошибка: {e}")


if __name__ == "__main__":
    main()