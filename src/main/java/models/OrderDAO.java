package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OrderDAO {

    private Connection conn;

    public String getMaxOrderNum() {
        conn = DBConnection.getConnection();
        String maxVal = null;

        String query = "SELECT Max(order_num) as `max_id` FROM `sale_order`";
        //String query = "SELECT Max(order_num) as `max_id` FROM `sale_order`";
        //String query = "SELECT Max(order_num) as `max_id` FROM `sale_order` LIMIT 1";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                maxVal = rset.getString("max_id");
            }
        } catch (SQLException ex) {
            System.out.println("資料庫getMaxOrderNum操作異常:" + ex.toString());
        }
        return maxVal;
    }

    public boolean insertCart(Order cart, int userId) {
        conn = DBConnection.getConnection();
        String query = "insert into `sale_order`(order_num,total_price, user_id) "
                + "VALUES (?, ?, ?)";
        boolean success = false;
        try {
            PreparedStatement state = conn.prepareStatement(query);

            state.setString(1, cart.getOrder_num());
            state.setInt(2, cart.getTotal_price());
            state.setInt(3, userId); // 將使用者ID插入到資料庫中

            state.execute();
            success = true;
            System.out.println("insert cart成功!");
        } catch (SQLException ex) {
            System.out.println("insert異常:" + ex.toString());
        }
        return success;
    }
    


    //新增訂單明細 應該寫在OrderDetailDAO.java比較好
    public boolean insertOrderDetailItem(OrderDetail item) {
        //String order_num =  getMaxOrderNum();
        conn = DBConnection.getConnection();

        String query = "INSERT INTO `order_detail` (`order_num`, `product_id`, `quantity`) VALUES (?, ?, ?)";
        boolean success = false;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, item.getOrder_num());
            state.setString(2, item.getProduct_id());
            state.setInt(3, item.getQuantity());
            //state.setInt(4, item.getProduct_price()); //optional
            //state.setString(5, item.getProduct_name());//optional
            state.execute();
            success = true;
            System.out.println("insert order detail成功!");
        } catch (SQLException ex) {
            System.out.println("insert異常:" + ex.toString());
        }
        return success;
    }

    public boolean insertOrderDetailItemV0(OrderDetail item) {
        //String order_num =  getMaxOrderNum();
        conn = DBConnection.getConnection();

        String query = "INSERT INTO `order_detail` (`order_num`, `product_id`, `quantity`, product_price, product_name) VALUES (?, ?, ?, ?, ?)";
        boolean success = false;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, item.getOrder_num());
            state.setString(2, item.getProduct_id());
            state.setInt(3, item.getQuantity());
            state.setInt(4, item.getProduct_price()); //optional
            state.setString(5, item.getProduct_name());//optional
            state.execute();
            success = true;
            System.out.println("insert order detail成功!");
        } catch (SQLException ex) {
            System.out.println("insert異常:" + ex.toString());
        }
        return success;
    }
}
