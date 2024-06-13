package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ProductDAO {

    //private Connection conn = DBConnection.getConnection();
    private Connection conn;
    
    // 獲取所有類別
    public List<String> getAllCategories() {
        conn = DBConnection.getConnection();
        String query = "SELECT DISTINCT category FROM product";
        List<String> categories = new ArrayList<>();
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // 取得所有產品
    public List<Product> getAllProducts() {

        conn = DBConnection.getConnection();
        String query = "select * from product";
        List<Product> product_list = new ArrayList();

        try {
            PreparedStatement ps
                    = conn.prepareStatement(query);
            ResultSet rset = ps.executeQuery();

            while (rset.next()) {
                Product product = new Product();
                product.setProduct_id(rset.getString("product_id"));
                product.setCategory(rset.getString("category"));
                product.setName(rset.getString("name"));
                product.setPrice(rset.getInt("price"));
                product.setPhoto(rset.getString("photo"));
                product.setDescription(rset.getString("description"));
                product_list.add(product);

                //不要斷線，一直會用到，使用持續連線的方式
                //conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("getAllproducts異常:" + ex.toString());
        }

        return product_list;
    }

    public List<Product> findByProductIDOrNameContaining(String keyword) {
        conn = DBConnection.getConnection();
        List<Product> productList = new ArrayList<>();
        String query = "SELECT * FROM product WHERE product_id LIKE ? OR name LIKE ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, "%" + keyword + "%");
            state.setString(2, "%" + keyword + "%");
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                Product product = new Product();
                product.setProduct_id(rset.getString("product_id"));
                product.setCategory(rset.getString("category"));
                product.setName(rset.getString("name"));
                product.setPrice(rset.getInt("price"));
                product.setPhoto(rset.getString("photo"));
                product.setDescription(rset.getString("description"));
                productList.add(product);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫 findByProductIDOrNameContaining 操作異常:" + ex.toString());
        }
        return productList;
    }

    //查詢價格低於多少錢的產品
    public List<Product> findByPriceLessThanEqual(int price) {
        conn = DBConnection.getConnection();
        List<Product> product_list = new ArrayList();
        String query = "select * from product where price <= ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setInt(1, price);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                Product product = new Product();
                product.setProduct_id(rset.getString("product_id"));
                product.setCategory(rset.getString("category"));
                product.setName(rset.getString("name"));
                product.setPrice(rset.getInt("price"));
                product.setPhoto(rset.getString("photo"));
                product.setDescription(rset.getString("description"));
                product_list.add(product);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫selectByPrice作異常:" + ex.toString());
        }
        return product_list;
    }

    //查詢過濾filter 某個大類別的產品
    public List<Product> findByCate(String cate) {
        conn = DBConnection.getConnection();
        List<Product> product_list = new ArrayList();
        String query = "select * from product where category = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, cate);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                Product product = new Product();
                product.setProduct_id(rset.getString("product_id"));
                product.setCategory(rset.getString("category"));
                product.setName(rset.getString("name"));
                product.setPrice(rset.getInt("price"));
                product.setPhoto(rset.getString("photo"));
                product.setDescription(rset.getString("description"));
                product_list.add(product);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫selectByCate異常:" + ex.toString());
        }
        return product_list;
    }

    //選擇某個product_id
    public Product findById(String id) {
        conn = DBConnection.getConnection();
        boolean success = false;
        String query = "select * from product where product_id = ?";
        //String query = String.format("select * from student where student_id = '%s'", id);
        Product product = new Product();
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, id);
            ResultSet rset = state.executeQuery();

            while (rset.next()) {
                success = true;
                product.setProduct_id(rset.getString("product_id"));
                product.setCategory(rset.getString("category"));
                product.setName(rset.getString("name"));
                product.setPrice(rset.getInt("price"));
                product.setPhoto(rset.getString("photo"));
                product.setDescription(rset.getString("description"));
            }
        } catch (SQLException ex) {
            System.out.println("資料庫selectByID操作異常:" + ex.toString());
        }

        if (success) {
            return product;
        } else {
            return null;
        }

    }

    //新增一項產品 
    public boolean insert(Product product) {
        conn = DBConnection.getConnection();
        String query = "insert into product(product_id,name,category,price,photo,description) VALUES (?,?,?,?,?,?)";
        boolean success = false;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, product.getProduct_id());
            state.setString(2, product.getName());
            state.setString(3, product.getCategory());
            state.setInt(4, product.getPrice());
            state.setString(5, product.getPhoto());
            state.setString(6, product.getDescription());

            state.execute();
            //state.executeUpdate();
            success = true;
            System.out.println("新增成功");
        } catch (SQLException ex) {
            System.out.println("insert異常:" + ex.toString());
        }
        return success;
    }

    //刪除某項產品
    public boolean delete(String id) {
        conn = DBConnection.getConnection();

        boolean sucess_product = false;
        boolean sucess_order = false;
        
        //先刪除order_detail裡面的product_id相同的資料
        String query_order = "delete from order_detail where product_id =?";

        try {
            PreparedStatement statement_order = conn.prepareStatement(query_order);
            statement_order.setString(1, id);

            //statement.execute();
            sucess_order = statement_order.executeUpdate() > 0;

            if (sucess_order) {
                System.out.println("Record deleted Order_detail successfully.");
            } else {
                System.out.println("Record not found.");
            }
        } catch (SQLException ex) {
            System.out.println("delete異常:\n" + ex.toString());
        }

        //刪除product 裡面的product_id
        String query_product = "delete from product where product_id =?";
        try {
            PreparedStatement statement_product = conn.prepareStatement(query_product);
            statement_product.setString(1, id);

            sucess_product = statement_product.executeUpdate() > 0;

            if (sucess_product) {
                System.out.println("Record deleted Product successfully.");
            } else {
                System.out.println("Record not found.");
            }
        } catch (SQLException ex) {
            System.out.println("delete異常:\n" + ex.toString());
        }
        return sucess_product;
    }

    // 更新某項產品
    public void update(Product product) {
        conn = DBConnection.getConnection();
        String query = "update product set name=?, category=?, price=?, photo= ?, description=? where product_id = ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(6, product.getProduct_id());
            state.setString(1, product.getName());
            state.setString(2, product.getCategory());
            state.setInt(3, product.getPrice());
            state.setString(4, product.getPhoto());
            state.setString(5, product.getDescription());

            state.executeUpdate();
            System.out.println("更新成功!");
        } catch (SQLException ex) {
            System.out.println("update異常:" + ex.toString());
        }
    }

    public TreeMap<String, Product> readProductByCategoryToTreeMap(String cate) {
        //read_product_from_file(); //從檔案或資料庫讀入產品菜單資訊

        //***********產生資料DAO來使用
        ProductDAO productDao = new ProductDAO();
        //***************從檔案或資料庫讀入產品菜單資訊
//        List<Product> products = this.findByCate(cate);
        List<Product> products = productDao.getAllProducts();

        //放所有產品  產品編號  產品物件Product
        TreeMap<String, Product> product_dict = new TreeMap();

        //準備產品的字典 從資料庫中讀入
        //放入product_dict中點選產品與顯示產品比較方便
        for (Product product : products) {
            //System.out.println(product.getCategory());
            product_dict.put(product.getProduct_id(), product);
        }
        return product_dict;
    }

    //dictionary  (key, value or content)
    //Map --> dict  {'key':'value'}
    public TreeMap<String, Product> readAllProductToTreeMap() {
        //read_product_from_file(); //從檔案或資料庫讀入產品菜單資訊

        //***********產生資料DAO來使用
        ProductDAO productDao = new ProductDAO();
        //***************從檔案或資料庫讀入產品菜單資訊
//        List<Product> products = this.getAllProducts();
        List<Product> products = productDao.getAllProducts();

        //放所有產品  產品編號  產品物件Product
        TreeMap<String, Product> product_dict = new TreeMap();

        //準備產品的字典 從資料庫中讀入
        //放入product_dict中點選產品與顯示產品比較方便
        for (Product product : products) {
            //System.out.println(product.getCategory());
            product_dict.put(product.getProduct_id(), product);
        }

        return product_dict;

    }

}
