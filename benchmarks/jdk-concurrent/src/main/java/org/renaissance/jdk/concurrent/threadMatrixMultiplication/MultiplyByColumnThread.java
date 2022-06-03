package org.renaissance.jdk.concurrent.threadMatrixMultiplication;

import org.renaissance.jdk.concurrent.matrix.Matrix;
import org.renaissance.jdk.concurrent.matrix.MatrixMultiplication;
import org.renaissance.jdk.concurrent.matrix.MatrixMultiplicationException;
import org.renaissance.jdk.concurrent.threadPartialMatrixMultiplication.MultiplyPartiallyByColumnThread;

import java.util.ArrayList;
import java.util.List;

public class MultiplyByColumnThread implements Runnable {

    private final Matrix A;
    private final Matrix B;
    private final Matrix C;
    private final int partialMultiplicationCount;

    public MultiplyByColumnThread(Matrix A, Matrix B, int partialMultiplicationCount) {
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
        int elementCountPerPartialMultiplication = C.getElementCount() / partialMultiplicationCount;

        for (int partialMultiplicationNo = 0; partialMultiplicationNo < partialMultiplicationCount - 1; partialMultiplicationNo++) {
            int index = partialMultiplicationNo * elementCountPerPartialMultiplication;
            int startRowIndex = index % C.getRowCount();
            int startColumnIndex = index / C.getRowCount();

            Thread thread = new MultiplyPartiallyByColumnThread(A, B, C, elementCountPerPartialMultiplication, startRowIndex, startColumnIndex);
            threads.add(thread);
        }
        // last partial multiplication
        int index = (partialMultiplicationCount - 1) * elementCountPerPartialMultiplication;
        int startRowIndex = index % C.getRowCount();
        int startColumnIndex = index / C.getRowCount();
        elementCountPerPartialMultiplication += C.getElementCount() % partialMultiplicationCount;
        Thread thread = new MultiplyPartiallyByColumnThread(A, B, C, elementCountPerPartialMultiplication, startRowIndex, startColumnIndex);
        threads.add(thread);

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
