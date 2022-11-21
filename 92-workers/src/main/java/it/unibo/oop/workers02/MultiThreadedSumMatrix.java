package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int threadNumber;

    public MultiThreadedSumMatrix(final int threadNumber) {
        this.threadNumber = threadNumber;
    }

    @Override
    public double sum(double[][] matrix) {
        final var list = matrixToList(matrix);
        final int size = list.size() % threadNumber + list.size() / threadNumber;
        
        // Creating a list of workers.
        final List<Worker> workers = new ArrayList<>();
        for (int start = 0; start < list.size(); start += size) {
            workers.add(new Worker(list, start, size));
        }

        // Starting the workers.
        for (final Worker worker : workers) {
            worker.start();
        }

        // Waiting for every worker to end and returning the final sum.
        long sum = 0;
        for (final Worker worker : workers) {
            try {
                worker.join();
                sum += worker.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException();
            }
        }
        return sum;
    }

    private static class Worker extends Thread {

        // A Map containing the elements to sum.
        private final List<Double> elementsList;
        private final int start;
        private final int elementNumber;
        private long result;

        public Worker(final List<Double> elementsList, final int start, final int elementNumber) {
            super();
            this.elementsList = elementsList;
            this.start = start;
            this.elementNumber = elementNumber;
        }

        @Override
        public void run() {
            System.out.println("Working FROM: " + this.start + " TO: " + (this.start + this.elementNumber - 1));
            for (int i = this.start; i < elementsList.size(); i++) {
                this.result += this.elementsList.get(i);
            }
        }

        public long getResult() {
            return this.result;
        }

    }

    private List<Double> matrixToList(final double[][] matrix) {
        final List<Double> result = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                result.add(matrix[i][j]);
            }
        }
        return Collections.unmodifiableList(result);
    }
    
}
