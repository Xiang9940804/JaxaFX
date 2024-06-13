package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import models.DBConnection;
import models.SaleOrder;

public class SaleOrderDAO {

    private Connection conn;

    // Constructor
    public SaleOrderDAO() {
        conn = DBConnection.getConnection();
    }

    // Create
    public void createSaleOrder(SaleOrder saleOrder) {
        String query = "INSERT INTO sale_order (order_num, order_date, total_price, user_id) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, saleOrder.getOrderNum());
            state.setDate(2, new java.sql.Date(saleOrder.getOrderDate().getTime()));
            state.setDouble(3, saleOrder.getTotalPrice());
            state.setInt(4, saleOrder.getUserId());
            state.executeUpdate();
            System.out.println("Sale order created successfully.");
        } catch (SQLException ex) {
            System.out.println("Database insert operation exception: " + ex.toString());
        }
    }

    // Read all sale orders
    public List<SaleOrder> getAllSaleOrders() {
        List<SaleOrder> saleOrderList = new ArrayList<>();
        String query = "SELECT * FROM sale_order";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                String orderNum = rset.getString("order_num");
                Date orderDate = rset.getDate("order_date");
                Date orderDateYmd = rset.getDate("order_date_ymd");
                double totalPrice = rset.getDouble("total_price");
                int userId = rset.getInt("user_id");
                SaleOrder saleOrder = new SaleOrder(orderNum, orderDate, orderDateYmd, totalPrice, userId);
                saleOrderList.add(saleOrder);
            }
        } catch (SQLException ex) {
            System.out.println("Database query operation exception: " + ex.toString());
        }
        return saleOrderList;
    }

    // 在 SaleOrderDAO.java 中添加方法，根据日期查询销售订单
    // 根據日期查詢銷售訂單
    public List<String> getSaleOrdersByDate(Date date) {
        List<String> orderNumbers = new ArrayList<>();
        String query = "SELECT order_num FROM sale_order WHERE DATE(order_date_ymd) = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setDate(1, date);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                String orderNum = rset.getString("order_num");
                orderNumbers.add(orderNum);
            }
        } catch (SQLException ex) {
            System.out.println("Database query operation exception: " + ex.toString());
        }
        return orderNumbers;
    }

    // Update
    public void updateSaleOrder(SaleOrder saleOrder) {
        String query = "UPDATE sale_order SET order_date = ?, total_price = ?, user_id = ? WHERE order_num = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setDate(1, new java.sql.Date(saleOrder.getOrderDate().getTime()));
            state.setDouble(2, saleOrder.getTotalPrice());
            state.setInt(3, saleOrder.getUserId());
            state.setString(4, saleOrder.getOrderNum());
            state.executeUpdate();
            System.out.println("Sale order updated successfully.");
        } catch (SQLException ex) {
            System.out.println("Database update operation exception: " + ex.toString());
        }
    }

    // Delete
    public void deleteSaleOrder(int orderNum) {
        String query = "DELETE FROM sale_order WHERE order_num = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setInt(1, orderNum);
            state.executeUpdate();
            System.out.println("Sale order deleted successfully.");
        } catch (SQLException ex) {
            System.out.println("Database delete operation exception: " + ex.toString());
        }
    }
}
