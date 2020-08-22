package com.ailiwean.core;

import androidx.annotation.IntRange;
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

    int ratio;

    private RespectScaleQueue(int normalSize, int scaleSize) {
        normalQueue = new ArrayBlockingQueue<>(normalSize, true);
        scaleQueue = new ArrayBlockingQueue<>(scaleSize, true);
        ratio = (normalSize + scaleSize) / scaleSize;
    }

    public static RespectScaleQueue<TypeRunnable> create(int normalSize, int scaleSize) {
        return new RespectScaleQueue<>(normalSize, scaleSize);
    }

    @Override
    public boolean add(T t) {
        if (t.getType() == TypeRunnable.NORMAL)
            return normalQueue.add(t);
        else return scaleQueue.add(t);
    }

    @Override
    public boolean offer(T t) {
        if (t.getType() == TypeRunnable.NORMAL)
            return normalQueue.offer(t);
        else return scaleQueue.offer(t);
    }

    int removeIndex = 0;

    @Override
    public T remove() {
        if (removeIndex++ % ratio == 0)
            return scaleQueue.remove();
        else return normalQueue.remove();
    }

    int pollIndex = 0;

    @Nullable
    @Override
    public T poll() {
        if (pollIndex++ % ratio == 0)
            return scaleQueue.poll();
        else return normalQueue.poll();
    }

    public void poll(@IntRange(from = 0, to = 1) int type) {
        if (type == TypeRunnable.NORMAL) {
            normalQueue.poll();
        } else {
            scaleQueue.poll();
        }
    }

    int elementIndex = 0;

    @Override
    public T element() {
        if (elementIndex++ % ratio == 0)
            return scaleQueue.peek();
        else return normalQueue.peek();
    }

    int peekIndex = 0;

    @Nullable
    @Override
    public T peek() {
        if (peekIndex++ % ratio == 0)
            return normalQueue.peek();
        else return scaleQueue.peek();
    }

    @Override
    public void put(T t) throws InterruptedException {
        if (t.getType() == TypeRunnable.NORMAL)
            normalQueue.put(t);
        else scaleQueue.put(t);
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        if (t.getType() == TypeRunnable.NORMAL)
            return normalQueue.offer(t, timeout, unit);
        else return scaleQueue.offer(t, timeout, unit);
    }


    int takeIndex = 0;

    /***
     * core
     * @return
     * @throws InterruptedException
     */
    @Override
    public T take() throws InterruptedException {
        if (takeIndex++ % ratio == 0) {
            T t = normalQueue.take();
            if (t == null)
                t = scaleQueue.take();
            return t;
        } else {
            T t = scaleQueue.take();
            if (t == null)
                t = normalQueue.take();
            return t;
        }
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        if (pollIndex++ % ratio == 0)
            return normalQueue.poll(timeout, unit);
        else return scaleQueue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return normalQueue.remainingCapacity() + scaleQueue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof TypeRunnable) {
            if (((TypeRunnable) o).getType() == TypeRunnable.NORMAL)
                return normalQueue.remove(o);
            else return scaleQueue.remove(o);
        } else return normalQueue.remove(o) || scaleQueue.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return normalQueue.containsAll(c) || scaleQueue.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        if (isNormal(c))
            return normalQueue.addAll(c);
        else return scaleQueue.addAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        if (isNormal(c))
            return normalQueue.removeAll(c);
        else return scaleQueue.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        if (isNormal(c))
            return normalQueue.retainAll(c);
        else return scaleQueue.retainAll(c);
    }

    @Override
    public void clear() {
        normalQueue.clear();
        scaleQueue.clear();
    }

    @Override
    public int size() {
        return normalQueue.size() + scaleQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return normalQueue.isEmpty() && scaleQueue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof TypeRunnable) {
            if (((TypeRunnable) o).getType() == TypeRunnable.NORMAL)
                return normalQueue.contains(o);
            else return scaleQueue.contains(o);
        } else return normalQueue.contains(o) || scaleQueue.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        ArrayBlockingQueue<T> all = new ArrayBlockingQueue<>(normalQueue.size()
                + scaleQueue.size(), true);
        all.addAll(normalQueue);
        all.addAll(scaleQueue);
        return all.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        ArrayBlockingQueue<T> all = new ArrayBlockingQueue<>(normalQueue.size()
                + scaleQueue.size(), true);
        all.addAll(normalQueue);
        all.addAll(scaleQueue);
        return all.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] a) {
        if (a.length == 0)
            return a;
        if (a[0] instanceof TypeRunnable) {
            if (((TypeRunnable) a[0]).getType() == TypeRunnable.NORMAL)
                return normalQueue.toArray(a);
            else return scaleQueue.toArray(a);
        }
        return a;
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        if (isNormal(c))
            return normalQueue.drainTo(c);
        else return scaleQueue.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        if (isNormal(c))
            return normalQueue.drainTo(c, maxElements);
        else return scaleQueue.drainTo(c, maxElements);
    }

    private boolean isNormal(Collection<?> c) {
        Iterator<?> iterator = c.iterator();
        if (iterator.hasNext()) {
            Object o = iterator.next();
            return o instanceof TypeRunnable && ((TypeRunnable) o).getType() == TypeRunnable.NORMAL;
        }
        return false;
    }
}

