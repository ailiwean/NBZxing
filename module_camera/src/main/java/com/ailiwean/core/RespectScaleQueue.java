package com.ailiwean.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Package: com.ailiwean.core
 * @ClassName: RespectScaleQueue
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/22 2:21 PM
 */
class RespectScaleQueue<T extends TypeRunnable> implements BlockingQueue<T>, java.io.Serializable {

    ArrayBlockingQueue<T> normalQueue;
    ArrayBlockingQueue<T> scaleQueue;
    ArrayBlockingQueue<T> otherQueue;

    private RespectScaleQueue(int normalSize, int scaleSize, int otherSize) {
        normalQueue = new ArrayBlockingQueue<>(normalSize, true);
        scaleQueue = new ArrayBlockingQueue<>(scaleSize, true);
        otherQueue = new ArrayBlockingQueue<>(otherSize, true);
    }

    public static RespectScaleQueue<TypeRunnable> create(int normalSize, int scaleSize, int otherSize) {
        return new RespectScaleQueue<>(normalSize, scaleSize, otherSize);
    }

    @Override
    public boolean add(T t) {
        return false;
    }

    @Override
    public boolean offer(T t) {
        if (t.getType() == TypeRunnable.NORMAL) return normalQueue.offer(t);
        else if (t.getType() == TypeRunnable.SCALE) return scaleQueue.offer(t);
        else return otherQueue.offer(t);
    }

    @Override
    public T remove() {
        return null;
    }

    @Nullable
    @Override
    public T poll() {
        return null;
    }

    public T poll(@TypeRunnable.Range int type) {
        if (type == TypeRunnable.NORMAL) {
            return normalQueue.poll();
        } else if (type == TypeRunnable.SCALE) {
            return scaleQueue.poll();
        } else if (type == TypeRunnable.OTHER) {
            return otherQueue.poll();
        }
        return null;
    }

    @Override
    public T element() {
        return null;
    }

    @Nullable
    @Override
    public T peek() {
        return null;
    }

    @Override
    public void put(T t) {
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) {
        return false;
    }

    int takeIndex = -1;

    /***
     * core
     * @return
     * @throws InterruptedException
     */
    @Override
    public T take() throws InterruptedException {
        takeIndex = ++takeIndex % 3;
        T result = null;
        if (takeIndex == 0 && !normalQueue.isEmpty()) result = normalQueue.take();
        else if (takeIndex == 1 && !scaleQueue.isEmpty()) result = scaleQueue.take();
        else if (takeIndex == 2 && !otherQueue.isEmpty()) result = otherQueue.take();
        if (result == null) {
            if (!scaleQueue.isEmpty()) result = scaleQueue.take();
            else if (!otherQueue.isEmpty()) result = otherQueue.take();
            else result = normalQueue.take();
        }
        return result;
    }

    @Override
    public T poll(long timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return -1;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        normalQueue.clear();
        scaleQueue.clear();
        otherQueue.clear();
    }

    @Override
    public int size() {
        return normalQueue.size() + scaleQueue.size() + otherQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return normalQueue.isEmpty() && scaleQueue.isEmpty() && otherQueue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return normalQueue.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[]{};
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] a) {
        return a;
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        return -1;
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        return -1;
    }
}

