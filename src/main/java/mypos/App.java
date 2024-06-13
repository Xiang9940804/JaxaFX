package mypos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.OrderDetail;
import models.Product;
import models.ReadCategoryProduct;
import models.DBConnection;
import models.Order;
import models.OrderDAO;
import models.PosProduct;
import models.ProductDAO;
import models.ReadCategoryProductFromDB;
import models.UserDAO;
import models.Users;

public class App extends Application {

    //***********產生資料DAO來使用訂單輸入資料庫之功能
    private OrderDAO orderDao = new OrderDAO();

    private UserDAO userDAO = new UserDAO();

    //取得產品字典
    private ProductDAO prodcutDao = new ProductDAO();

    private TilePane menuBeef = getProductCategoryMenu("生鮮肉類");
    private TilePane menuFish = getProductCategoryMenu("生鮮魚類");

    private ObservableList<OrderDetail> order_list;
    private TableView<OrderDetail> table;

//    private TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();
    private TreeMap<String, Product> product_dict = ReadCategoryProductFromDB.readProduct();

    private TextArea display = new TextArea();
    private TextArea orderSummaryDisplay = new TextArea(); // 新增的TextArea顯示訂單摘要

    // 新增會員按鈕
    private Button addMemberButton;
    // 新增訪客按鈕
    private Button visitorMemberButton;
    // 查詢會員按鈕
    private Button searchMemberButton;
    // 登入會員按鈕
    private Button loginMemberButton;
    // 後台管理按鈕
    private Button backendManagementButton;
    // 下拉選單
    private MenuButton memberMenuButton;

    // 定義右側面板
    private VBox rightPane;
    // 用於存儲當前使用者的ID
    private Integer currentUserId;

    private Scene scene; // 增加此行以聲明 scene 變數

    VBox menuContainerPane = new VBox();
    VBox selectedProductPane = new VBox();
    ComboBox<Integer> quantityComboBox = new ComboBox<>();
    Label placeholderLabel = new Label("目前還沒有東西被選擇");

    private String selectedProductId = null;

    private TilePane getProductCategoryMenu(String category) {

        //取得產品清單，這裡的清單是區域變數，不是全域變數
        final TreeMap<String, Product> product_dict_category = prodcutDao.readProductByCategoryToTreeMap(category);

        //以前的寫法
        //取得產品清單(呼叫靜態方法取得)
        //TreeMap<String, Product> product_dict_category = ReadCategoryProduct.readProduct();
        //磁磚窗格
        TilePane category_menu = new TilePane(); //
        category_menu.setVgap(10);  //垂直間隙
        category_menu.setHgap(10);
        //設定一個 row有4個columns，放不下就放到下一個row
        category_menu.setPrefColumns(3);

        //將產品清單內容一一置放入產品菜單磁磚窗格
        for (final String item_id : product_dict_category.keySet()) {

            if (product_dict_category.get(item_id).getCategory().equals(category)) {
                //定義新增一筆按鈕
                Button btn = new Button();

                //width, height 按鈕外框的大小，你要自行調整，讓它美觀。沒有設定外框會大小不一不好看
                btn.setPrefSize(120, 120);
                //btn.setText(product_dict_category.get(item_id).getName()); //不要顯示文字，顯示圖片就好

                Image img;
                ImageView imgview;

                //若沒有圖檔也不會報錯
                try {
                    img = new Image("/imgs/" + product_dict_category.get(item_id).getPhoto()); //讀出圖片
                    //按鈕元件顯示圖片Creating a graphic (image)
                    imgview = new ImageView(img);//圖片顯示物件
                } catch (Exception ex) {
                    System.out.println("讀取圖片例外");
                    imgview = new ImageView();//空白的圖片
                }

                imgview.setFitHeight(80);
                imgview.setFitWidth(100);

                //Setting a graphic to the button
                btn.setGraphic(imgview); //按鈕元件顯示圖片
                category_menu.getChildren().add(btn);  //放入菜單磁磚窗格

                //定義按鈕事件-->點選一次，就加入購物車，再點選一次，數量要+1
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        displaySelectedProduct(item_id);
                    }
                });
            }
        }
        return category_menu;
    }

    private void displaySelectedProduct(String item_id) {
        selectedProductId = item_id;
        Product product = product_dict.get(item_id);

        selectedProductPane.getChildren().clear();

        Image img;
        ImageView imgview;

        try {
            img = new Image("/imgs/" + product.getPhoto());
            //按鈕元件顯示圖片Creating a graphic (image)
            imgview = new ImageView(img);
            imgview.setFitHeight(100);
            imgview.setFitWidth(100);
        } catch (Exception ex) {
            System.out.println("讀取圖片例外");
            imgview = new ImageView();//空白的圖片
        }

        Label nameLabel = new Label(product.getName() + " $" + product.getPrice());
        nameLabel.setStyle("-fx-font-size: 20px;");

        quantityComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        quantityComboBox.setValue(1);
        quantityComboBox.getStyleClass().add("form-control"); // 套用 Bootstrap 樣式

        Button addToCartButton = new Button("加入購物車");
        addToCartButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;"); // 修改按鈕顏色
        addToCartButton.setOnAction(event -> addToCart(selectedProductId));

        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.getChildren().addAll(quantityComboBox, addToCartButton);

        selectedProductPane.getChildren().addAll(imgview, nameLabel, controlBox);
        selectedProductPane.setSpacing(10);
        selectedProductPane.setAlignment(Pos.CENTER);
        selectedProductPane.setPadding(new Insets(10));
        selectedProductPane.setStyle("-fx-background-color: #E6E6FA; -fx-border-radius: 10; -fx-background-radius: 10;"); // 設置圓角
    }

    public TilePane getMenuSelectionContainer() {
        Button btnBeef = new Button("生鮮肉類");
        btnBeef.setStyle("-fx-font-size: 14px; -fx-background-color: #FFA07A; -fx-text-fill: white;");
        btnBeef.setOnAction(event -> select_category_menu(event));

        Button btnFish = new Button("生鮮魚類");
        btnFish.setStyle("-fx-font-size: 14px; -fx-background-color: #87CEFA; -fx-text-fill: white;");
        btnFish.setOnAction(event -> select_category_menu(event));

        TilePane containerCategoryMenuBtn = new TilePane();
        containerCategoryMenuBtn.setVgap(10);
        containerCategoryMenuBtn.setHgap(10);
        containerCategoryMenuBtn.getChildren().addAll(btnBeef, btnFish);
        return containerCategoryMenuBtn;
    }

    public void select_category_menu(ActionEvent event) {
        String category = ((Button) event.getSource()).getText();
        menuContainerPane.getChildren().clear();
        switch (category) {
            case "生鮮肉類":
                menuContainerPane.getChildren().add(menuBeef);
                break;
            case "生鮮魚類":
                menuContainerPane.getChildren().add(menuFish);
                break;
            default:
                break;
        }

        // 清空紫色方框並顯示占位元文本
        selectedProductPane.getChildren().clear();
        selectedProductPane.getChildren().add(placeholderLabel);
    }

    public void initializeOrderTable() {
        order_list = FXCollections.observableArrayList();
        checkTotal();
        table = new TableView<>();
        table.setEditable(true);
        table.setPrefHeight(200); // 將表格高度縮小

        TableColumn<OrderDetail, String> order_item_name = new TableColumn<>("品名");
        order_item_name.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        order_item_name.setCellFactory(TextFieldTableCell.forTableColumn());
        order_item_name.setPrefWidth(100);
        order_item_name.setMinWidth(100);

        TableColumn<OrderDetail, Integer> order_item_price = new TableColumn<>("價格");
        order_item_price.setCellValueFactory(new PropertyValueFactory<>("product_price"));

        TableColumn<OrderDetail, Integer> order_item_qty = new TableColumn<>("數量");
        order_item_qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        order_item_qty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        order_item_qty.setOnEditCommit(event -> {
            int row_num = event.getTablePosition().getRow();
            int new_val = event.getNewValue();
            OrderDetail target = event.getTableView().getItems().get(row_num);
            target.setQuantity(new_val);
            checkTotal();
            System.out.println("哪個產品被修改數量:" + order_list.get(row_num).getProduct_name());
            System.out.println("數量被修改為:" + order_list.get(row_num).getQuantity());
        });

        table.setItems(order_list);
        checkTotal();
        table.getColumns().addAll(order_item_name, order_item_price, order_item_qty);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        display.setPrefHeight(40); // 將總金額文字方塊的高度調整為兩行文字的大小
    }

    public void checkTotal() {
        double total = 0;
        for (OrderDetail od : order_list) {
            total += od.getProduct_price() * od.getQuantity();
        }
        String totalmsg = String.format("%s %d\n", "總金額:", Math.round(total));
        display.setText(totalmsg);
    }

    public void addToCart(String item_id) {
        boolean duplication = false;
        int selectedQuantity = quantityComboBox.getValue();
        for (int i = 0; i < order_list.size(); i++) {
            if (order_list.get(i).getProduct_id().equals(item_id)) {
                int qty = order_list.get(i).getQuantity() + selectedQuantity;
                order_list.get(i).setQuantity(qty);
                duplication = true;
                table.refresh();
                checkTotal();
                System.out.println(item_id + "此筆已經加入購物車，數量+" + selectedQuantity);
                break;
            }
        }
        if (!duplication) {
            OrderDetail new_ord = new OrderDetail(
                    item_id,
                    product_dict.get(item_id).getName(),
                    product_dict.get(item_id).getPrice(),
                    selectedQuantity);
            order_list.add(new_ord);
            table.refresh();
            checkTotal();
            System.out.println(item_id + "此筆曾經尚未加入過購物車，數量+" + selectedQuantity);
        }
    }

    private void cancelOrder() {
        order_list.clear();
        table.refresh();
        checkTotal();
        System.out.println("所有訂單取消");
    }

    private void cancelSelectedItem() {
        OrderDetail selectedOrder = table.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            order_list.remove(selectedOrder);
            table.refresh();
            checkTotal();
            System.out.println("單筆訂單取消: " + selectedOrder.getProduct_name());
        }
    }

    private boolean isCartEmpty() {
        return order_list.isEmpty();
    }

    private void checkout() {
        // 檢查用戶是否已登入
        if (currentUserId == null) {
            // 顯示警告訊息
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("請先登入再結帳。");
            alert.showAndWait();
            return; // 終止結帳流程
        }

        // 檢查購物車是否為空
        if (isCartEmpty()) {
            // 顯示警告訊息
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("購物車為空，請先選擇商品再結帳。");
            alert.showAndWait();
            return; // 終止結帳流程
        }

        double total = calculateTotal();

        String order_num = orderDao.getMaxOrderNum();
        if (order_num == null) {
            order_num = "ord-100";
        }

        int serial_num = Integer.parseInt(order_num.split("-")[1]) + 1;
        String new_order_num = "ord-" + serial_num;

        int sum = calculateTotal();

        // 使用存儲的使用者ID創建 Order 物件
        Order crt = new Order();
        crt.setOrder_num(new_order_num);
        crt.setTotal_price(sum);
        crt.setUser_id(currentUserId); // 設置用戶ID

        orderDao.insertCart(crt, currentUserId);

        for (int i = 0; i < order_list.size(); i++) {
            OrderDetail item = new OrderDetail();
            item.setOrder_num(new_order_num);
            item.setProduct_id(order_list.get(i).getProduct_id());
            item.setQuantity(order_list.get(i).getQuantity());

            orderDao.insertOrderDetailItem(item);
        }

        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Taipei"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        StringBuilder orderSummary = new StringBuilder();
        orderSummary.append("購物清單明細\n");
        orderSummary.append("訂單成立時間：").append(formattedTime).append("\n\n");
        orderSummary.append(String.format("%-18s %-15s %-15s %-15s\n", "品名", "單價", "數量", "金額"));
        orderSummary.append(String.format("%-18s %-15s %-15s %-15s\n", "------------------------------------------------", "", "", ""));

        for (OrderDetail od : order_list) {
            String productName = od.getProduct_name();
            int productPrice = od.getProduct_price();
            int quantity = od.getQuantity();
            int totalPrice = productPrice * quantity;

            int namePadding = 0;
            int pricePadding = 0;
            int quantityPadding = 0;

            if (productName.length() == 2) {
                namePadding = 3;
            }

            if (String.valueOf(productPrice).length() == 3) {
                pricePadding = 1;
            }

            if (String.valueOf(productPrice).length() == 2) {
                pricePadding = 2;
            }

            if (String.valueOf(quantity).length() == 1) {
                quantityPadding = 2;
            }

            if (String.valueOf(quantity).length() == 2) {
                quantityPadding = 1;
            }

            orderSummary.append(String.format("%-" + (15 + namePadding) + "s $%-" + (15 + pricePadding) + "d x%-" + (15 + quantityPadding) + "d $%-10d\n",
                    productName, productPrice, quantity, totalPrice));
        }
        orderSummary.append("\n總金額：").append(Math.round(total));
        orderSummaryDisplay.setText(orderSummary.toString());
        orderSummaryDisplay.setVisible(true);

        // 新增：保存訂單資訊到txt檔
        try {
            DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String fileName = currentTime.format(fileFormatter) + ".txt";
            Path filePath = Paths.get("src/main/resources/txt", fileName);

            // 確保目錄存在
            Files.createDirectories(filePath.getParent());

            // 保存訂單詳情到檔
            Files.write(filePath, orderSummary.toString().getBytes(StandardCharsets.UTF_8));

            System.out.println("Order details saved to: " + filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectedProductPane.getChildren().clear();
        selectedProductPane.getChildren().add(placeholderLabel);

        logout();
        clearOrder();
    }

    private int calculateTotal() {
        int total = 0;
        for (OrderDetail od : order_list) {
            total += od.getProduct_price() * od.getQuantity();
        }
        return total;
    }

    private void clearOrder() {
        order_list.clear();
        table.refresh();
        checkTotal();
    }

    private void showAddMemberDialog() {
        showDialog("", "");
    }

    private void showDialog(String initialName, String initialPhone) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("新增會員");
        dialog.setHeaderText("請輸入姓名和電話");

        ButtonType confirmButtonType = new ButtonType("確認", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("姓名");
        nameField.setText(initialName);
        TextField phoneField = new TextField();
        phoneField.setPromptText("電話");
        phoneField.setText(initialPhone);

        // 提示信息 Label
        Label phoneErrorLabel = new Label("手機號碼需以 09 開頭");
        phoneErrorLabel.setTextFill(Color.RED);
        phoneErrorLabel.setVisible(false); // 初始隐藏

        // 监听手机号码输入框的焦点事件
        phoneField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // 失去焦点时检查手机号码格式
                String phoneNumber = phoneField.getText();
                if (!phoneNumber.isEmpty() && !phoneNumber.startsWith("09")) {
                    phoneErrorLabel.setVisible(true); // 显示提示信息
                } else {
                    phoneErrorLabel.setVisible(false); // 隐藏提示信息
                }
            } else {
                // 获得焦点时隐藏提示信息
                phoneErrorLabel.setVisible(false);
            }
        });

        // 监听手机号码输入框的文字变化，且仅在焦点在手机号码输入框时生效
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (phoneField.isFocused()) {
                if (!newValue.isEmpty() && !newValue.startsWith("09")) {
                    phoneErrorLabel.setVisible(true); // 显示提示信息
                } else {
                    phoneErrorLabel.setVisible(false); // 隐藏提示信息
                }
            }
        });

        // 监听手机号码输入框的长度，超过10个字符则截断
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 10) {
                phoneField.setText(oldValue);
            }
        });

        grid.add(new Label("姓名:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("電話:"), 0, 1);
        grid.add(phoneField, 1, 1);
        GridPane.setColumnSpan(phoneErrorLabel, 2); // 扩展到两列
        grid.add(phoneErrorLabel, 0, 2); // 添加提示信息到 GridPane 第三行

        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        // 监听两个输入框的变化，更新确认按钮状态
        ChangeListener<String> inputListener = (observable, oldValue, newValue) -> {
            boolean isNameEmpty = nameField.getText().trim().isEmpty();
            boolean isPhoneEmpty = phoneField.getText().trim().isEmpty();
            boolean isValidPhone = phoneField.getText().startsWith("09");
            confirmButton.setDisable(isNameEmpty || isPhoneEmpty || !isValidPhone);

            // 当手机号码输入不正确时，确认按钮禁用的情况下，也显示提示信息
            if (!phoneField.getText().isEmpty() && !isValidPhone) {
                phoneErrorLabel.setVisible(true);
            } else {
                phoneErrorLabel.setVisible(false);
            }
        };

        nameField.textProperty().addListener(inputListener);
        phoneField.textProperty().addListener(inputListener);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return new Pair<>(nameField.getText(), phoneField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            String user_name = result.getKey();
            String user_phone = result.getValue();

            // 檢查手機號碼是否已存在
            if (userDAO.isPhoneNumberExists(user_phone)) {
                Alert phoneExistsAlert = new Alert(Alert.AlertType.WARNING);
                phoneExistsAlert.setTitle("錯誤");
                phoneExistsAlert.setHeaderText(null);
                phoneExistsAlert.setContentText("手機號碼已有人註冊過。");
                phoneExistsAlert.showAndWait();

                // 清空電話號碼欄位，保留姓名欄位
                showDialog(user_name, "");
                return;
            }

            Integer user_id = userDAO.getMaxOrderNum();
            if (user_id == null) {
                user_id = 0;
            }

            int serial_num = user_id + 1;
            int new_user_id = serial_num;

            Users newUser = new Users(new_user_id, user_name, user_phone);

            // 调用 UserDAO 中的新增方法将用户添加到数据库
            userDAO.insert(newUser);

            // 显示新增成功的对话框
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("新增成功");
            successAlert.setHeaderText(null);
            successAlert.setContentText("會員新增成功！");
            successAlert.showAndWait();
        });
    }

    //查詢會員和登入會員
    private void showMemberDialog(String title, String headerText, boolean isLogin) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        ButtonType searchButtonType = new ButtonType("確認送出", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("請輸入電話號碼");

        grid.add(new Label("電話號碼:"), 0, 0);
        grid.add(phoneNumberField, 1, 0);

        Node searchButton = dialog.getDialogPane().lookupButton(searchButtonType);
        searchButton.setDisable(true);
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return new Pair<>(phoneNumberField.getText(), null);
            }
            return null;
        });

        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(searchButtonType);
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        dialog.showAndWait().ifPresent(result -> {
            String phoneNumber = result.getKey();
            Platform.runLater(() -> {
                UserDAO userDAO = new UserDAO(); // 初始化UserDAO對象
                List<Users> userList = userDAO.findByPhone(phoneNumber);
                if (!userList.isEmpty()) {
                    Users user = userList.get(0);
                    String name = user.getUser_name();
                    currentUserId = user.getUser_id(); // 設置用戶ID
                    if (isLogin) {
                        // 登錄會員
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("登入訊息");
                        alert.setHeaderText(null);
                        alert.setContentText("登入成功！");
                        alert.showAndWait();
                    } else {
                        // 查詢會員資訊
                        String phone = user.getUser_phone();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("會員資料");
                        alert.setHeaderText(null);
                        alert.setContentText("姓名: " + name + "\n電話: " + phone);
                        alert.showAndWait();
                    }
                    // 顯示會員姓名及登出按鈕
                    Label nameLabel = new Label("會員姓名: " + name);
                    Button logoutButton = new Button("登出");
                    logoutButton.setOnAction(event -> logout());

                    visitorMemberButton.setVisible(false);
                    visitorMemberButton.managedProperty().bind(visitorMemberButton.visibleProperty());
                    memberMenuButton.setVisible(false);
                    memberMenuButton.managedProperty().bind(memberMenuButton.visibleProperty());
                    backendManagementButton.setVisible(false);
                    backendManagementButton.managedProperty().bind(backendManagementButton.visibleProperty());

                    HBox memberInfoBox = new HBox(10);
                    memberInfoBox.setAlignment(Pos.CENTER_RIGHT);
                    memberInfoBox.getChildren().addAll(nameLabel, logoutButton);
                    rightPane.getChildren().add(0, memberInfoBox); // 在rightPane的頂部添加會員資訊
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText(null);
                    alert.setContentText("未找到電話號碼為： " + phoneNumber + " 的會員資料");
                    alert.showAndWait();
                }
            });
        });

    }

    private void loginSearchMemberDialog() {
        showMemberDialog("登入會員", "請輸入電話號碼登入會員", true);
    }

    private void showSearchMemberDialog() {
        showMemberDialog("查詢會員", "請輸入電話號碼查詢會員資料", false);
    }

    private void showVisitorInfo() {
        String visitorPhone = "0000000000";

        Platform.runLater(() -> {
            UserDAO userDAO = new UserDAO();
            List<Users> userList = userDAO.findByPhone(visitorPhone);
            if (!userList.isEmpty()) {
                Users user = userList.get(0);
                String name = user.getUser_name();
                String phone = user.getUser_phone();
                currentUserId = user.getUser_id(); // 存儲使用者ID

                // 顯示訪客會員資料
                Label nameLabel = new Label(name);
                Button logoutButton = new Button("登出");
                logoutButton.setOnAction(event -> logout());

                // 隱藏其他按鈕
                visitorMemberButton.setVisible(false);
                visitorMemberButton.managedProperty().bind(visitorMemberButton.visibleProperty());
                memberMenuButton.setVisible(false);
                memberMenuButton.managedProperty().bind(memberMenuButton.visibleProperty());
                backendManagementButton.setVisible(false);
                backendManagementButton.managedProperty().bind(backendManagementButton.visibleProperty());

                // 顯示訪客信息及登出按鈕
                HBox memberInfoBox = new HBox(10);
                memberInfoBox.setAlignment(Pos.CENTER_RIGHT);
                memberInfoBox.getChildren().addAll(nameLabel, logoutButton);
                rightPane.getChildren().add(0, memberInfoBox); // 在rightPane的頂部添加訪客信息
            }
        });
    }

    private void logout() {
        // 隱藏會員姓名和登出按鈕
        rightPane.getChildren().remove(0); // 移除第一個子節點，即會員資訊和登出按鈕

        // 顯示訪客登入、新增會員、查詢會員、登入會員按鈕
        visitorMemberButton.setVisible(true);
        memberMenuButton.setVisible(true);
        backendManagementButton.setVisible(true);
    }

    // 在 App 類中定義一個方法來初始化會員相關的按鈕
    private void initializeMemberButtons() {

        // 初始化訪客登入按鈕
        visitorMemberButton = new Button("訪客登入");
        visitorMemberButton.setStyle("-fx-background-color: #D8BFD8; -fx-text-fill: white;"); // 修改按鈕顏色
        visitorMemberButton.setOnAction(event -> showVisitorInfo());

        // 創建一個下拉式功能表按鈕
        memberMenuButton = new MenuButton("會員管理");
        memberMenuButton.setStyle("-fx-background-color: #D8BFD8; -fx-text-fill: white;"); // 修改按鈕顏色

        // 初始化新增會員按鈕
        MenuItem addMemberItem = new MenuItem("新增會員");
        addMemberItem.setOnAction(event -> showAddMemberDialog());
        addMemberItem.setStyle("-fx-text-fill: white;"); // 設置字體顏色為白色
        addMemberItem.getStyleClass().add("menu-item");

        // 初始化查詢會員按鈕
        MenuItem searchMemberItem = new MenuItem("查詢會員");
        searchMemberItem.setOnAction(event -> showSearchMemberDialog());
        searchMemberItem.setStyle("-fx-text-fill: white;"); // 設置字體顏色為白色
        searchMemberItem.getStyleClass().add("menu-item");

        // 初始化登入會員按鈕
        MenuItem loginMemberItem = new MenuItem("登入會員");
        loginMemberItem.setOnAction(event -> loginSearchMemberDialog());
        loginMemberItem.setStyle("-fx-text-fill: white;"); // 設置字體顏色為白色
        loginMemberItem.getStyleClass().add("menu-item");

        // 將功能表項目添加到下拉式功能表按鈕
        memberMenuButton.getItems().addAll(addMemberItem, searchMemberItem, loginMemberItem);

        // 初始化後台管理按鈕
        backendManagementButton = new Button("後台管理");
        backendManagementButton.setStyle("-fx-background-color: #D8BFD8; -fx-text-fill: white;"); // 修改按鈕顏色
        backendManagementButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // 在同一個VBox中顯示後台管理內容
                VBox root = new VBox();
                root.setSpacing(10);
                root.setPadding(new Insets(10, 10, 10, 10));
                root.getStylesheets().add("/css/bootstrap3.css");

                // 創建後臺管理的內容
                PosProduct posProduct = new PosProduct();
                VBox posProductRoot = posProduct.get_root_pane();

                root.getChildren().addAll(posProductRoot);
                scene.setRoot(root); // 設置新的根節點以顯示後臺管理的內容
            }
        });

        // 將按鈕添加到介面佈局中
        VBox buttonContainer = new VBox(10);
        buttonContainer.getChildren().addAll(visitorMemberButton, memberMenuButton, backendManagementButton);

        // 將 buttonContainer 添加到主介面佈局中，例如頂部、右側等
        rightPane.getChildren().add(buttonContainer);
    }

    @Override
    public void start(Stage stage) {

        HBox root = new HBox();
        root.setSpacing(20);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getStylesheets().add("/css/bootstrap3.css");

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10, 10, 10, 10));
        leftPane.setPrefWidth(400);
        leftPane.setAlignment(Pos.TOP_CENTER);

        selectedProductPane.setAlignment(Pos.CENTER);
        selectedProductPane.setPadding(new Insets(10));
        selectedProductPane.setSpacing(10);
        selectedProductPane.setPrefSize(200, 200);
        selectedProductPane.setStyle("-fx-background-color: #E6E6FA; -fx-border-radius: 10; -fx-background-radius: 10;");

        selectedProductPane.getChildren().add(placeholderLabel);

        leftPane.getChildren().addAll(selectedProductPane);

        initializeOrderTable();
        display.setEditable(false);
        leftPane.getChildren().addAll(table, display);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button checkoutButton = new Button("結帳");
        checkoutButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        checkoutButton.setOnAction(event -> checkout());

        Button cancelOrderButton = new Button("取消訂單");
        cancelOrderButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelOrderButton.setOnAction(event -> cancelOrder());

        Button cancelSelectedItemButton = new Button("取消單筆");
        cancelSelectedItemButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
        cancelSelectedItemButton.setOnAction(event -> cancelSelectedItem());

        buttonBox.getChildren().addAll(checkoutButton, cancelOrderButton, cancelSelectedItemButton);
        leftPane.getChildren().add(buttonBox);

        // 初始化右側面板
        rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10, 10, 10, 10));
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setPrefWidth(500);

        // 移動會員按鈕到右側
        HBox memberButtonsBox = new HBox(10);
        memberButtonsBox.setAlignment(Pos.CENTER_RIGHT); // 按鈕靠右對齊

        // 初始化會員相關的按鈕
        initializeMemberButtons();

        memberButtonsBox.getChildren().addAll(visitorMemberButton, memberMenuButton, backendManagementButton);
        rightPane.getChildren().add(0, memberButtonsBox); // 將memberButtonsBox添加到rightPane的頂部

        // 增加一條細細的灰色水準線
        HBox separator = new HBox();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #D3D3D3;"); // 灰色的細線
        rightPane.getChildren().add(separator);

        TilePane menuSelectionTile = getMenuSelectionContainer();
        rightPane.getChildren().add(menuSelectionTile);

        menuContainerPane.getChildren().add(menuBeef);
        rightPane.getChildren().add(menuContainerPane);

        orderSummaryDisplay.setEditable(false);
        orderSummaryDisplay.setVisible(false); // Initially hide the order summary
        rightPane.getChildren().add(orderSummaryDisplay);

        // 創建 scene
        scene = new Scene(root);
        root.getChildren().addAll(leftPane, rightPane);
        rightPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setTitle("生鮮魚肉市場");
        stage.setScene(scene);
        stage.show();

    }

    public VBox get_root_pane() {
        // 創建根節點 VBox
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getStylesheets().add("/css/bootstrap3.css");

        // 將菜單選擇容器添加到根節點中
        root.getChildren().add(getMenuSelectionContainer());

        // 將選擇的產品容器添加到根節點中
        root.getChildren().add(selectedProductPane);

        // 將訂單表格添加到根節點中
        root.getChildren().add(table);

        // 返回根節點
        return root;
    }

    public static void main(String[] args) {
        launch();
    }

}
