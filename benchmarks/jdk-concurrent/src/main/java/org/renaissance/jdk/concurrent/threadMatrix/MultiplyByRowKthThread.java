package org.renaissance.jdk.concurrent.threadMatrix;

import org.renaissance.jdk.concurrent.matrix.Matrix;
import org.renaissance.jdk.concurrent.matrix.MatrixMultiplication;
import org.renaissance.jdk.concurrent.matrix.MatrixMultiplicationException;
import org.renaissance.jdk.concurrent.threads.MultiplyPartiallyByRowKthThread;

import java.util.ArrayList;
import java.util.List;

public class MultiplyByRowKthThread extends Thread {
    private final Matrix A;
    private final Matrix B;
    private final Matrix C;
    private final int tasksNo;

    public MultiplyByRowKthThread(Matrix A, Matrix B, int tasksNo) {
        super();

        C = MatrixMultiplication.emptyMatrixOfMultiply(A, B);
        if (tasksNo > C.getRowsNo() * C.getColumnsNo()) {
            throw new MatrixMultiplicationException("MatrixMultiplicationException: MultiplyByColumn");
        }

        this.A = A;
        this.B = B;
        this.tasksNo = tasksNo;
    }

    @Override
    public void run() {
        List<Thread> threads = new ArrayList<>();

        for (int orderNo = 0; orderNo < tasksNo; orderNo++) {
            Thread thread = new MultiplyPartiallyByRowKthThread(A, B, C, tasksNo, orderNo);
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