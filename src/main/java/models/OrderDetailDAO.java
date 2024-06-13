package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO {

    private Connection conn;

    // 獲取所有訂單詳細資料的方法
    public List<OrderDetail_Analysis> getAllOrderDetails() {
        conn = DBConnection.getConnection();
        List<OrderDetail_Analysis> orderDetails = new ArrayList<>();
        String query = "SELECT * FROM order_detail";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                OrderDetail_Analysis orderDetail = new OrderDetail_Analysis(
                        rset.getInt("order_id"),
                        rset.getString("order_num"),
                        rset.getString("product_id"),
                        rset.getInt("quantity")
                );
                orderDetails.add(orderDetail);
            }
        } catch (SQLException ex) {
            System.out.println("獲取所有訂單詳細資料時發生錯誤: " + ex.toString());
        }
        return orderDetails;
    }

    // 新增訂單詳細資料的方法
    public void insert(OrderDetail_Analysis orderDetail) {
        conn = DBConnection.getConnection();
        String query = "INSERT INTO order_detail (order_num, product_id, quantity) VALUES (?, ?, ?)";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, orderDetail.getOrder_num());
            state.setString(2, orderDetail.getProduct_id());
            state.setInt(3, orderDetail.getQuantity());
            state.executeUpdate();
            System.out.println("新增訂單詳細資料成功");
        } catch (SQLException ex) {
            System.out.println("新增訂單詳細資料時發生錯誤: " + ex.toString());
        }
    }
    
    // 根據訂單編號查詢訂單詳細資料的方法
    public List<OrderDetail_Analysis> getOrderDetailsByOrderNumber(String orderNum) {
        List<OrderDetail_Analysis> orderDetails = new ArrayList<>();
        String query = "SELECT * FROM order_detail WHERE order_num = ?";
        try {
            conn = DBConnection.getConnection();
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, orderNum);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                OrderDetail_Analysis orderDetail = new OrderDetail_Analysis(
                        rset.getInt("order_id"),
                        rset.getString("order_num"),
                        rset.getString("product_id"),
                        rset.getInt("quantity")
                );
                orderDetails.add(orderDetail);
            }
        } catch (SQLException ex) {
            System.out.println("根據訂單編號查詢訂單詳細資料時發生錯誤: " + ex.toString());
        }
        return orderDetails;
    }

    // 刪除訂單詳細資料的方法
    public void delete(int orderId) {
        conn = DBConnection.getConnection();
        String query = "DELETE FROM order_detail WHERE order_id = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setInt(1, orderId);
            state.executeUpdate();
            System.out.println("刪除訂單詳細資料成功");
        } catch (SQLException ex) {
            System.out.println("刪除訂單詳細資料時發生錯誤: " + ex.toString());
        }
    }
}
