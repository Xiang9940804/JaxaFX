package models;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Separator;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import models.OrderDetailDAO;
import models.OrderDetail_Analysis;
import models.Product;
import models.ProductDAO;
import mypos.App;

public class Analysis extends Application {

    private Scene scene; // 增加此行以聲明 scene 變數
    private OrderDetailDAO orderDetailDAO;
    private ProductDAO productDAO;
    private SaleOrderDAO saleOrderDAO;
    private Date selectedDate;
    private String selectedDateString;
    private Date FinselectedDate;

    public Analysis() {
        this.orderDetailDAO = new OrderDetailDAO();
        this.productDAO = new ProductDAO();
        this.saleOrderDAO = new SaleOrderDAO();
        this.selectedDateString = ""; // 初始化 selectedDateString
    }

    // 定義方法來重新設置每個 PieChart Data 的 mouse event listener
    private void resetMouseEventListeners(PieChart chart) {
        for (PieChart.Data data : chart.getData()) {
            data.getNode().setOnMouseEntered(e -> {
                data.getNode().setOpacity(0.7);
                String text = String.format("%.1f%%", (data.getPieValue() / chart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum()) * 100);
                chart.setTitle(data.getName() + " (" + text + ")");
            });
            data.getNode().setOnMouseExited(e -> {
                data.getNode().setOpacity(1);
                if (selectedDate == null) {
                    String initialTitle = getFormattedDate() + " 銷售分析";
                    chart.setTitle(initialTitle);
                } else {
                    String initialTitle = selectedDateString + " 銷售分析";
                    chart.setTitle(initialTitle);
                }

            });
        }
    }

    private void updatePieChartData(PieChart chart, Map<String, Integer> newData) {
        // 清除原有的 PieChart 數據
        chart.getData().clear();

        // 將新數據添加到 PieChart 中
        for (Map.Entry<String, Integer> entry : newData.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey() + ": " + entry.getValue(), entry.getValue());
            chart.getData().add(data);
        }
        chart.setVisible(true);
        // 重新设置每個 PieChart Data 的鼠標事件監聽器
        resetMouseEventListeners(chart);

    }

    // 定義方法獲取特定類別的銷售數據
    private Map<String, Integer> getSalesDataByCategory(String category) {
        // 從數據庫中獲取所有訂單細節
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        List<OrderDetail_Analysis> orderDetails = orderDetailDAO.getAllOrderDetails();

        // 創建Map來存儲產品類別和其訂購數量
        Map<String, Integer> salesData = new HashMap<>();

        // 遍歷訂單細節，計算特定類別的訂購數量
        for (OrderDetail_Analysis orderDetail : orderDetails) {
            String productId = orderDetail.getProduct_id();
            ProductDAO productDAO = new ProductDAO();
            Product product = productDAO.findById(productId);
            if (product != null && product.getCategory().equals(category)) {
                int quantity = orderDetail.getQuantity();
                salesData.put(product.getName(), salesData.getOrDefault(product.getName(), 0) + quantity);
            }
        }

        return salesData;
    }

    // 定義方法獲取格式化後的今天日期
    private String getFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return today.format(formatter);
    }

    private Map<String, Integer> getDailySalesDataFromDatabase() {
        // 從資料庫中獲取訂單詳情資料
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        List<OrderDetail_Analysis> orderDetails = orderDetailDAO.getAllOrderDetails();

        // 創建一個Map來存儲產品ID和其訂購數量
        Map<String, Integer> productSalesMap = new HashMap<>();

        // 根據訂單詳情計算每個產品的訂購數量
        for (OrderDetail_Analysis orderDetail : orderDetails) {
            String productId = orderDetail.getProduct_id();
            int quantity = orderDetail.getQuantity();

            // 如果Map中已經有了該產品的資料，就將數量加上去
            if (productSalesMap.containsKey(productId)) {
                int totalQuantity = productSalesMap.get(productId);
                productSalesMap.put(productId, totalQuantity + quantity);
            } else {
                // 否則，添加新的產品到Map中
                productSalesMap.put(productId, quantity);
            }
        }

        // 從資料庫中獲取產品名稱
        ProductDAO productDAO = new ProductDAO();
        Map<String, String> productNameMap = new HashMap<>();
        for (String productId : productSalesMap.keySet()) {
            Product product = productDAO.findById(productId);
            if (product != null) {
                productNameMap.put(productId, product.getName());
            }
        }

        // 創建一個Map來存儲產品名稱和其訂購數量
        Map<String, Integer> dailySalesData = new HashMap<>();
        for (Map.Entry<String, Integer> entry : productSalesMap.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            String productName = productNameMap.getOrDefault(productId, "未知產品");
            dailySalesData.put(productName, quantity);
        }

        return dailySalesData;
    }

    // 將搜尋按鈕的事件處理程序抽取成一個獨立的方法
    private void handleSearchEvent(DatePicker filterField, PieChart chart) {
        // 獲取使用者所選擇的日期
        if (filterField.getValue() == null) {
            // 如果日期选择器为空，则弹出提示窗口
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText("未選擇日期");
            alert.setContentText("請先選擇日期，然後再進行查詢。");
            alert.showAndWait();
        } else {
            selectedDate = Date.valueOf(filterField.getValue());
            if (selectedDate != null) {
                // 格式化所選日期
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                selectedDateString = formatter.format(selectedDate.toLocalDate());

                // 更新图表標題
                chart.setTitle(selectedDateString + " 銷售分析");

                // 修改圖表標題為所選日期
                System.out.println("所選日期: " + selectedDate);

                // 從SaleOrderDAO.java中獲取在指定日期下的訂單編號列表
                List<String> orderNumbers = saleOrderDAO.getSaleOrdersByDate(selectedDate);

                for (String orderNumber : orderNumbers) {
                    System.out.println("訂單編號: " + orderNumber);
                }
                // 創建一個用於存放所有訂單詳細信息的列表
                List<OrderDetail_Analysis> allOrderDetails = new ArrayList<>();

                // 對於每個訂單編號，獲取相應的訂單詳細信息並添加到allOrderDetails中
                for (String orderNumber : orderNumbers) {
                    // 根據訂單編號獲取相應的訂單詳細信息
                    List<OrderDetail_Analysis> orderDetails = orderDetailDAO.getOrderDetailsByOrderNumber(orderNumber);
                    // 將訂單詳細信息添加到allOrderDetails中
                    allOrderDetails.addAll(orderDetails);
                }

                // 3. 從訂單詳細信息中提取產品ID和數量，並將它們相加以獲得每個產品的總銷售量
                Map<String, Integer> salesData = new HashMap<>();
                for (OrderDetail_Analysis orderDetail : allOrderDetails) {
                    String productId = orderDetail.getProduct_id();
                    int quantity = orderDetail.getQuantity();
                    salesData.put(productId, salesData.getOrDefault(productId, 0) + quantity);
                }

                // 4. 使用ProductDAO.java中的方法將產品ID轉換為產品名稱
                Map<String, Integer> salesDataWithName = new HashMap<>();
                for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
                    String productId = entry.getKey();
                    int quantity = entry.getValue();
                    Product product = productDAO.findById(productId);
                    if (product != null) {
                        salesDataWithName.put(product.getName(), salesDataWithName.getOrDefault(product.getName(), 0) + quantity);
                    }
                }
                updatePieChartData(chart, salesDataWithName);
            }
        }
    }

    // 定義方法處理瀏覽全部事件
    private void handleViewAllEvent(Date selectedDate, PieChart chart) {
        List<String> orderNumbers = saleOrderDAO.getSaleOrdersByDate(selectedDate);
        List<OrderDetail_Analysis> allOrderDetails = new ArrayList<>();
        for (String orderNumber : orderNumbers) {
            List<OrderDetail_Analysis> orderDetails = orderDetailDAO.getOrderDetailsByOrderNumber(orderNumber);
            allOrderDetails.addAll(orderDetails);
        }
        Map<String, Integer> salesData = new HashMap<>();
        for (OrderDetail_Analysis orderDetail : allOrderDetails) {
            String productId = orderDetail.getProduct_id();
            int quantity = orderDetail.getQuantity();
            salesData.put(productId, salesData.getOrDefault(productId, 0) + quantity);
        }
        Map<String, Integer> salesDataWithName = new HashMap<>();
        for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = productDAO.findById(productId);
            if (product != null) {
                salesDataWithName.put(product.getName(), salesDataWithName.getOrDefault(product.getName(), 0) + quantity);
            }
        }
        updatePieChartData(chart, salesDataWithName);
    }

    // 定義方法處理生鮮肉類事件
    private void handleMeatEvent(Date selectedDate, PieChart chart) {
        List<String> orderNumbers = saleOrderDAO.getSaleOrdersByDate(selectedDate);
        List<OrderDetail_Analysis> allOrderDetails = new ArrayList<>();
        for (String orderNumber : orderNumbers) {
            List<OrderDetail_Analysis> orderDetails = orderDetailDAO.getOrderDetailsByOrderNumber(orderNumber);
            allOrderDetails.addAll(orderDetails);
        }
        Map<String, Integer> salesData = new HashMap<>();
        for (OrderDetail_Analysis orderDetail : allOrderDetails) {
            String productId = orderDetail.getProduct_id();
            int quantity = orderDetail.getQuantity();
            salesData.put(productId, salesData.getOrDefault(productId, 0) + quantity);
        }
        Map<String, Integer> salesDataWithName = new HashMap<>();
        for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = productDAO.findById(productId);
            if (product != null && product.getCategory().equals("生鮮肉類")) {
                salesDataWithName.put(product.getName(), salesDataWithName.getOrDefault(product.getName(), 0) + quantity);
            }
        }
        updatePieChartData(chart, salesDataWithName);
    }

    // 定義方法處理生鮮魚類事件
    private void handleFishEvent(Date selectedDate, PieChart chart) {
        List<String> orderNumbers = saleOrderDAO.getSaleOrdersByDate(selectedDate);
        List<OrderDetail_Analysis> allOrderDetails = new ArrayList<>();
        for (String orderNumber : orderNumbers) {
            List<OrderDetail_Analysis> orderDetails = orderDetailDAO.getOrderDetailsByOrderNumber(orderNumber);
            allOrderDetails.addAll(orderDetails);
        }
        Map<String, Integer> salesData = new HashMap<>();
        for (OrderDetail_Analysis orderDetail : allOrderDetails) {
            String productId = orderDetail.getProduct_id();
            int quantity = orderDetail.getQuantity();
            salesData.put(productId, salesData.getOrDefault(productId, 0) + quantity);
        }
        Map<String, Integer> salesDataWithName = new HashMap<>();
        for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = productDAO.findById(productId);
            if (product != null && product.getCategory().equals("生鮮魚類")) {
                salesDataWithName.put(product.getName(), salesDataWithName.getOrDefault(product.getName(), 0) + quantity);
            }
        }
        updatePieChartData(chart, salesDataWithName);
    }

    private BorderPane createButtonBox(PieChart chart, Stage stage) {
        Button viewAllButton = new Button("瀏覽全部");
        viewAllButton.setStyle("-fx-font-size: 14px; -fx-background-color: #336699; -fx-text-fill: white;");
        // 瀏覽全部按鈕事件處理
        viewAllButton.setOnAction(e -> handleViewAllEvent(selectedDate, chart));

        Button meatButton = new Button("生鮮肉類");
        meatButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FFA07A; -fx-text-fill: white;");
        // 生鮮肉類按鈕事件處理
        meatButton.setOnAction(e -> handleMeatEvent(selectedDate, chart));

        Button fishButton = new Button("生鮮魚類");
        fishButton.setStyle("-fx-font-size: 14px; -fx-background-color: #87CEFA; -fx-text-fill: white;");
        // 生鮮魚類按鈕事件處理
        fishButton.setOnAction(e -> handleFishEvent(selectedDate, chart));
        
        

        Button backButton = new Button("回管理後台");
        backButton.setStyle("-fx-font-size: 14px; -fx-background-color: #D8BFD8; -fx-text-fill: white;");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                VBox root = new VBox();
                root.setSpacing(10);
                root.setPadding(new Insets(10, 10, 10, 10));
                root.getStylesheets().add("/css/bootstrap3.css");

                PosProduct posProduct = new PosProduct();
                VBox posProductRoot = posProduct.get_root_pane();

                root.getChildren().addAll(posProductRoot);
                scene.setRoot(root);
            }
        });

        Button backcheckoutButton = new Button("回結帳介面");
        backcheckoutButton.setStyle("-fx-font-size: 14px; -fx-background-color: #D8BFD8; -fx-text-fill: white;");
        backcheckoutButton.setOnAction(e -> handleBackToCheckoutEvent(stage));

        // 將回後台管理和回顧客結帳的按鈕放入一個新的 HBox 中，並設置對齊方式為右對齊
        HBox managementButtonBox = new HBox(10);
        managementButtonBox.setAlignment(Pos.CENTER_RIGHT); // 右對齊
        managementButtonBox.getChildren().addAll(backButton, backcheckoutButton);

        // 使用HBox容器放置前面數個按鈕
        HBox buttonContainerLeft = new HBox(5); // HBox with spacing of 5
        buttonContainerLeft.getChildren().addAll(viewAllButton, meatButton, fishButton);

        // 使用HBox容器放置回結帳介面和每日分析按钮
        HBox buttonContainerRight = new HBox(5); // HBox with spacing of 5
        buttonContainerRight.getChildren().addAll(backButton, backcheckoutButton);

        // 使用BorderPane放置HBox和每日分析按钮
        BorderPane containerProductCategory = new BorderPane();
        containerProductCategory.setLeft(buttonContainerLeft);
        containerProductCategory.setRight(buttonContainerRight);

        return containerProductCategory;
    }

    @Override
    public void start(Stage stage) {

        // 獲取今天的日期
        String todayDate = getFormattedDate();

        // 獲取今天的銷售數據
        Map<String, Integer> dailySalesData = getDailySalesDataFromDatabase();

        // 創建PieChart的數據列表
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : dailySalesData.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey() + ": " + entry.getValue(), entry.getValue());
            pieChartData.add(data);
        }

        // 創建PieChart
        final PieChart chart = new PieChart(pieChartData);
        String initialTitle = todayDate + " 銷售分析"; // 设置标题为今天的日期
        chart.setTitle(initialTitle);

        // 將數量顯示在圖表上
        resetMouseEventListeners(chart);

        // 創建功能按鈕並獲取按鈕配置區域
        BorderPane buttonBox = createButtonBox(chart, stage);

        // 創建一個默認的日期選擇器，並設置日期為今天
        DatePicker defaultDate = new DatePicker(LocalDate.now());
        // 調用處理搜索事件的方法，並傳入預設日期選擇器和圖表
        handleSearchEvent(defaultDate, chart);

        Text descriptionText = new Text("透過日期查找銷售情形：");
        descriptionText.setStyle("-fx-font-size: 14px;");
        // 創建篩選區塊
        DatePicker filterField = new DatePicker(); // 使用 DatePicker 作为日期筛选器
        filterField.setPromptText("選擇日期");
        filterField.setPrefWidth(100); // 設置日期選擇器的寬度
        filterField.setPrefHeight(30); // 設置日期選擇器的高度
        // 設置DatePicker只能選擇過去的日期
        filterField.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now()));
            }
        });

        Button searchButton = new Button("搜尋");
        searchButton.setStyle("-fx-font-size: 14px; -fx-background-color: #5cb85c; -fx-text-fill: white;");
        // 在按鈕的事件處理常式中添加代碼以執行上述步驟
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                handleSearchEvent(filterField, chart);
            }
        });

        // 創建按鈕用於回到今天的日期
        Button todayButton = new Button("回到今天");
        todayButton.setStyle("-fx-font-size: 14px; -fx-background-color: #EAC100; -fx-text-fill: white;");
        todayButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // 获取今天的日期并更新图表显示
                LocalDate today = LocalDate.now();
                filterField.setValue(today);
                handleSearchEvent(filterField, chart);
            }
        });

        // 將說明文本節點、日期選擇器和搜索按鈕放入水準箱子
        HBox filterBox = new HBox(10);

        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10));
        filterBox.getChildren().addAll(descriptionText, filterField, searchButton, todayButton);
        // 创建根容器
        VBox topBox = new VBox(10);

        topBox.getChildren().addAll(buttonBox, new Separator(), filterBox); // 添加水平线

        // 將PieChart添加到VBox中
        VBox vbox = new VBox(chart);

        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        // 創建一個BorderPane作為根節點
        BorderPane root = new BorderPane();
        root.setTop(topBox); // 将顶端的按钮和筛选区域放置在顶部
        root.setCenter(vbox);
        root.setPadding(new Insets(15)); // 设置边距

        //創建場景並顯示
        scene = new Scene(root, 950, 550);

        root.getStylesheets().add("/css/bootstrap3.css");
        scene.getStylesheets().add(getClass().getResource("/css/analysis.css").toExternalForm());
        stage.setScene(scene);

        stage.setTitle("每日分析");
        stage.show();
    }

    // 定義方法處理回到顧客結帳介面事件
    private void handleBackToCheckoutEvent(Stage stage) {
        // 关闭当前窗口
        stage.close();

        // 打开 App.java 中的界面
        App app = new App();
        Stage newStage = new Stage();
        app.start(newStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
