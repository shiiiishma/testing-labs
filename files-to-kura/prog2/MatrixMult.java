import java.util.Random;

public class MatrixMult {

    // Метод генерации случайной матрицы
    public static double[][] generateMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = rand.nextDouble();
            }
        }
        return matrix;
    }

    // Проверка, что матрица не null
    public static boolean isNotNull(double[][] matrix) {
        return matrix != null;
    }

    // Простой метод умножения матриц
    public static double[][] multiply(double[][] firstMatrix, double[][] secondMatrix) {
        int n1 = firstMatrix.length;
        int m1 = firstMatrix[0].length;
        int n2 = secondMatrix.length;
        int m2 = secondMatrix[0].length;

        if (m1 != n2) {
            throw new IllegalArgumentException("Размерности матриц не совпадают для умножения");
        }

        double[][] result = new double[n1][m2];

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < m2; j++) {
                double sum = 0;
                for (int k = 0; k < m1; k++) {
                    sum += firstMatrix[i][k] * secondMatrix[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    public static void printMatrix(double[][] A) {
        if (isNotNull(A)) {
            for (int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[0].length; j++) {
                    System.out.print(A[i][j] + "\t");
                }
                System.out.println();
            }
        }
        else {
            System.out.println("It is null matrix!");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int n = 5;

        double[][] A = generateMatrix(n, n);
        double[][] B = generateMatrix(n, n);
        
        System.out.println("First matrix:");
        printMatrix(A);

        System.out.println("Second matrix:");
        printMatrix(B);

        double[][] C = multiply(A, B);

        System.out.println("Multiply matrix:");
        printMatrix(C);

    }

}
