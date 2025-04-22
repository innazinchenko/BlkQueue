package ait.mediation;

import java.util.LinkedList;

public class BlkQueueImpl<T> implements BlkQueue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxSize;

    public BlkQueueImpl(int maxSize) {
        this.maxSize = maxSize;
    }


    @Override
    public synchronized void push(T message) {
        while (queue.size() >= maxSize) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.add(message);
        this.notifyAll();
    }

    @Override
    public synchronized T pop() {
        while (queue.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        T message = queue.removeFirst();
        notifyAll();
        return message;
    }


}
