package org.renaissance.jdk.concurrent.threadMatrixMultiplication;

import org.renaissance.jdk.concurrent.matrix.Matrix;
import org.renaissance.jdk.concurrent.matrix.MatrixMultiplication;
import org.renaissance.jdk.concurrent.matrix.MatrixMultiplicationException;
import org.renaissance.jdk.concurrent.threadPartialMatrixMultiplication.MultiplyPartiallyByRowKthThread;

import java.util.ArrayList;
import java.util.List;

public class MultiplyByRowKthThread implements Runnable {

    private final Matrix A;
    private final Matrix B;
    private final Matrix C;
    private final int partialMultiplicationCount;

    public MultiplyByRowKthThread(Matrix A, Matrix B, int partialMultiplicationCount) {
        super();

        C = MatrixMultiplication.emptyMatrixOfMultiply(A, B);
        if (partialMultiplicationCount > C.getRowCount() * C.getColumnCount()) {
            throw new MatrixMultiplicationException("MatrixMultiplicationException: MultiplyByColumn");
        }

        this.A = A;
        this.B = B;
        this.partialMultiplicationCount = partialMultiplicationCount;
    }

    @Override
    public void run() {
        List<Thread> threads = new ArrayList<>();

        for (int partialMultiplicationNo = 0; partialMultiplicationNo < partialMultiplicationCount; partialMultiplicationNo++) {
            Thread thread = new MultiplyPartiallyByRowKthThread(A, B, C, partialMultiplicationCount, partialMultiplicationNo);
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        });
    }
}
