def f(x, n):
    res = ''
    if x == 0:
        return '0'
    while x > 0:
        res = str(x % n) + res
        x //= n
    return res

print(f(17, 8))