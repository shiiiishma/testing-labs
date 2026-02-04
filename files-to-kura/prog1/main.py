def f(x, n):
    res = ''
    while x > 0:
        res = str(x % n) + res
        x //= n
    return res