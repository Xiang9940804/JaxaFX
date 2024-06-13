package models;
import java.util.Date;

public class SaleOrder {
    private String orderNum;
    private Date orderDate;
    private Date orderDateYmd;
    private double totalPrice;
    private int userId;

    // Constructor
    public SaleOrder(String orderNum, Date orderDate,Date orderDateYmd, double totalPrice, int userId) {
        this.orderNum = orderNum;
        this.orderDate = orderDate;
        this.orderDateYmd = orderDateYmd;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderDateYmd(Date orderDateYmd) {
        this.orderDateYmd = orderDateYmd;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getOrderDateYmd() {
        return orderDateYmd;
    }

    // Getters
    public String getOrderNum() {
        return orderNum;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getUserId() {
        return userId;
    }
}
