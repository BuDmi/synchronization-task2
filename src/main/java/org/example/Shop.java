package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Shop {
    private final List<Car> cars = new ArrayList<>();
    private final int carNumForSellPlan;
    private final AtomicInteger soldCar = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Shop(int carNumForSellPlan) {
        this.carNumForSellPlan = carNumForSellPlan;
    }

    public Car sellCar() {
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + " enter to shop");
            while (cars.size() == 0) {
                System.out.println("No cars");
                condition.await();
            }

            System.out.println(Thread.currentThread().getName() + " drove on new car");
            System.out.println("Sold " + soldCar.incrementAndGet() + " cars");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " finished shopping");
            return null;
        } finally {
            lock.unlock();
        }
        return cars.remove(0);
    }

    public void receiveCar() {
        try {
            lock.lock();
            cars.add(new Car());
            System.out.println("Producer produced new " + Thread.currentThread().getName());
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public boolean isFinishedSellPlan() {
        return soldCar.get() >= carNumForSellPlan;
    }
}

