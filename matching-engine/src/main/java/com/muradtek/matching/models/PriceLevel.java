package com.muradtek.matching.models;

public class PriceLevel {
    private final double price;

    private Order head;
    private Order tail;

    private int orderCount;
    private double totalQuantity;

    public PriceLevel(double price) {
        this.price = price;
        totalQuantity = 0;
    }

    public void addOrder(Order order) {
        order.parentLevel = this;
        order.next = null;

        if (tail == null) {
            head = order;
            tail = order;
            order.prev = null;
        } else {
            tail.next = order;
            order.prev = tail;
            tail = order;
        }

        orderCount++;
        totalQuantity += order.getQuantity();
    }

    public void removeOrder(Order order) {
        if (order.parentLevel != this)
            return;

        if (order.prev != null)
            order.prev.next = order.next;
        else
            head = order.next;


        if (order.next != null)
            order.next.prev = order.prev;
        else
            tail = order.prev;

        orderCount--;
        totalQuantity -= order.getQuantity();

        order.prev = null;
        order.next = null;
        order.parentLevel = null;
    }

    public boolean isEmpty() {
        return orderCount == 0;
    }

    public double getPrice() {
        return price;
    }

    public Order getHead() {
        return head;
    }

    public Order getTail() {
        return tail;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }
}
