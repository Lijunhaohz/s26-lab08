package edu.cmu.cs.cs214.rec08.queue;

import java.util.ArrayDeque;
import java.util.Deque;

import net.jcip.annotations.ThreadSafe;
import net.jcip.annotations.GuardedBy;

/**
 * Modify this class to be thread-safe and be an UnboundedBlockingQueue.
 */
@ThreadSafe
public class UnboundedBlockingQueue<E> implements SimpleQueue<E> {
    @GuardedBy("this")
    private Deque<E> queue = new ArrayDeque<>();

    public UnboundedBlockingQueue() { }

    public synchronized boolean isEmpty() { 
        return queue.isEmpty(); 
    }

    public synchronized int size() { 
        return queue.size(); 
    }

    public synchronized E peek() { 
        return queue.peek(); 
    }

    public synchronized void enqueue(E element) { 
        queue.add(element);
        notifyAll();
    }

    /**
     * This method blocks (waiting for an enqueue) when the queue is empty
     * rather than throwing an exception.
     */
    public synchronized E dequeue() {
        while (queue.isEmpty()) {
            try {
                // (Release the lock and wait until an element is enqueued)
                wait();
                // (When wake up, re-acquire the lock)
            } catch (InterruptedException e) {
                // If the thread is interrupted while waiting, we should restore the interrupt status
                Thread.currentThread().interrupt(); 
                throw new RuntimeException("Interrupted while waiting", e);
            }
        }
        return queue.remove();
    }

    public synchronized String toString() { 
        return queue.toString(); 
    }
}
