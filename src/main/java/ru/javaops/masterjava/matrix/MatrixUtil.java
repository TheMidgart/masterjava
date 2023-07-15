package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int threadNumber = 10;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBT = new int[matrixSize][matrixSize];
        final int oneTaskSize = matrixSize / threadNumber;

        //Транспонируем матрицу
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }

        //Делим на задачи для потоков
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int taskIndex = 0; taskIndex < threadNumber; taskIndex++) {
            int finalTaskIndex = taskIndex;
            tasks.add(() -> {
                        for (int i = oneTaskSize * finalTaskIndex; i < oneTaskSize * (finalTaskIndex + 1); i++) {
                            for (int j = 0; j < matrixSize; j++) {
                                int sum = 0;
                                for (int k = 0; k < matrixSize; k++) {
                                    sum += matrixA[i][k] * matrixBT[j][k];
                                }
                                matrixC[i][j] = sum;
                            }
                        }
                        return null;
                    }
            );
        }
        executor.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        int[][] matrixC = new int[matrixSize][matrixSize];
        int[] thatColumn = new int[matrixSize];

        for (int j = 0; j < matrixSize; j++) {
            for (int k = 0; k < matrixSize; k++) {
                thatColumn[k] = matrixB[k][j];
            }
            for (int i = 0; i < matrixSize; i++) {
                int thisRow[] = matrixA[i];
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += thisRow[k] * thatColumn[k];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
