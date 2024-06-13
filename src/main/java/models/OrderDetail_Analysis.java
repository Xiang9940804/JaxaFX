package models;

public class OrderDetail_Analysis {
    private int order_id;
    private String order_num;
    private String product_id;
    private int quantity;

    public OrderDetail_Analysis(int order_id, String order_num, String product_id, int quantity) {
        this.order_id = order_id;
        this.order_num = order_num;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    // Getter 和 Setter 方法
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // toString 方法
    @Override
    public String toString() {
        return "OrderDetail{" +
                "order_id=" + order_id +
                ", order_num='" + order_num + '\'' +
                ", product_id='" + product_id + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
