package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.DBConnection;
import models.Users;

public class UserDAO {

    private Connection conn;

    public Integer getMaxOrderNum() {
        conn = DBConnection.getConnection();
        Integer maxVal = null;

        String query = "SELECT Max(user_id) as `max_id` FROM `users`";
        //String query = "SELECT Max(order_num) as `max_id` FROM `sale_order`";
        //String query = "SELECT Max(order_num) as `max_id` FROM `sale_order` LIMIT 1";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                maxVal = rset.getInt("max_id");
            }
        } catch (SQLException ex) {
            System.out.println("資料庫getMaxUserId操作異常:" + ex.toString());
        }
        return maxVal;
    }

    // 新增用戶
    public void insert(Users user) {
        conn = DBConnection.getConnection();
        String query = "INSERT INTO users (user_name, user_phone) VALUES (?, ?)";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, user.getUser_name());
            state.setString(2, user.getUser_phone());
            state.executeUpdate();
            System.out.println("新增成功");
        } catch (SQLException ex) {
            System.out.println("資料庫新增操作異常: " + ex.toString());
        }
    }

    // 修改用戶
    public void update(Users user) {
        conn = DBConnection.getConnection();
        String query = "UPDATE users SET user_name = ?, user_phone = ? WHERE user_id = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, user.getUser_name());
            state.setString(2, user.getUser_phone());
            state.setInt(3, user.getUser_id());
            state.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("資料庫更新操作異常: " + ex.toString());
        }
    }

    // 根據手機號碼查詢用戶
    public List<Users> findByPhone(String phone) {
        conn = DBConnection.getConnection();
        List<Users> userList = new ArrayList<>();
        String query = "SELECT * FROM users WHERE user_phone = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, phone);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                Users user = new Users(rset.getInt("user_id"), rset.getString("user_name"), rset.getString("user_phone"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫查詢操作異常: " + ex.toString());
        }
        return userList;
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        conn = DBConnection.getConnection();
        String query = "SELECT COUNT(*) FROM users WHERE user_phone = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 刪除用戶
    public void delete(int userId) {
        conn = DBConnection.getConnection();
        String query = "DELETE FROM users WHERE user_id = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setInt(1, userId);
            state.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("資料庫刪除操作異常: " + ex.toString());
        }
    }
}
