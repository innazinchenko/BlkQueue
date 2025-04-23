package ait.mediation;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueueImpl<T> implements BlkQueue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxSize;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BlkQueueImpl(int maxSize) {
        this.maxSize = maxSize;
    }


    @Override
    public synchronized void push(T message) {
        while (queue.size() >= maxSize) {
            lock.lock();
            try {
                while (queue.size() >= maxSize) {
                    notFull.await();
                }
                queue.add(message);
                notEmpty.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public synchronized T pop() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
        T message = queue.removeFirst();
        notFull.signal();
        return message;
    } catch(InterruptedException e){
        throw new RuntimeException(e);
    } finally {
        lock.unlock();
    }
 }
}
