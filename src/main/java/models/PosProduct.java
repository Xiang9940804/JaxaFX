package models;

import java.util.ArrayList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
//import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;

import models.Product;
import models.ProductDAO;
import mypos.App;

public class PosProduct extends Application {

    //***********產生資料DAO來使用
    private ProductDAO productDao = new ProductDAO();
    //ObservableList    product_list有新增或刪除都會處動table的更新，也就是發生任何改變時都被通知
    //先放入一個空的ArrayList
    private ObservableList<Product> product_list = FXCollections.observableList(new ArrayList());

    //顯示產品內容表格，大家都會用到，全域變數。實例變數
    private TableView<Product> table;

    private String currentCategory = "瀏覽全部";  // Track the current category

    // 在類別中定義searchField作為類別屬性
    private TextField searchField;

    private BorderPane getProductCategoryContainer() {
        // 定義檢索全部按鈕
        Button btnQueryAll = new Button("瀏覽全部");
        btnQueryAll.setStyle("-fx-font-size: 14px; -fx-background-color: #336699; -fx-text-fill: white;");
        btnQueryAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentCategory = "瀏覽全部";  // 更新當前類別
                product_list = FXCollections.observableList(productDao.getAllProducts());
                table.setItems(product_list);
                System.out.println(product_list);
            }
        });

        // 定義"生鮮肉類"按鈕
        Button btnBeef = new Button("生鮮肉類");
        btnBeef.setStyle("-fx-font-size: 14px; -fx-background-color: #FFA07A; -fx-text-fill: white;");
        btnBeef.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentCategory = "生鮮肉類";  // 更新當前類別
                product_list = FXCollections.observableList(productDao.findByCate("生鮮肉類"));
                table.setItems(product_list);
                System.out.println(product_list);
            }
        });

        // 定義"生鮮魚類"按鈕
        Button btnFish = new Button("生鮮魚類");
        btnFish.setStyle("-fx-font-size: 14px; -fx-background-color: #87CEFA; -fx-text-fill: white;");
        btnFish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                currentCategory = "生鮮魚類";  // 更新當前類別
                product_list = FXCollections.observableList(productDao.findByCate("生鮮魚類"));
                table.setItems(product_list);
                System.out.println(product_list);
            }
        });

        // 使用HBox容器放置前面數個按鈕
        HBox buttonContainerLeft = new HBox(5); // HBox with spacing of 5
        buttonContainerLeft.getChildren().addAll(btnQueryAll, btnBeef, btnFish);

        // 定義"回結帳介面"按鈕
        Button btnCheckout = new Button("回結帳介面");
        btnCheckout.setStyle("-fx-font-size: 14px; -fx-background-color: #D8BFD8; -fx-text-fill: white;");
        btnCheckout.setOnAction(e -> switchToAppInterface());

        // 定義"每日分析"按鈕
        Button btnDailyAnalysis = new Button("每日分析");
        btnDailyAnalysis.setStyle("-fx-font-size: 14px; -fx-background-color: #EAC100; -fx-text-fill: white;");
        btnDailyAnalysis.setOnAction(e -> performDailyAnalysis());

        // 使用HBox容器放置回結帳介面和每日分析按鈕
        HBox buttonContainerRight = new HBox(5); // HBox with spacing of 5
        buttonContainerRight.getChildren().addAll(btnDailyAnalysis, btnCheckout);

        // 使用BorderPane放置HBox和每日分析按鈕
        BorderPane containerProductCategory = new BorderPane();
        containerProductCategory.setLeft(buttonContainerLeft);
        containerProductCategory.setRight(buttonContainerRight);

        return containerProductCategory;

    }

    private void performDailyAnalysis() {
        Analysis Analysis = new Analysis();
        Stage stage = (Stage) table.getScene().getWindow();
        try {
            Analysis.start(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //一個窗格(用磁磚窗格最方便)置放產品過濾與選擇按鈕，置放於主視窗的最上方區域。
    private HBox getProductSelectionContainer() {

        // 定義搜尋按鈕
        Button btnSearch = new Button("搜尋");
        btnSearch.getStyleClass().setAll("button", "info");
        searchField = new TextField("");
        searchField.setPromptText("輸入產品編號或產品名稱");

        // 在搜尋按鈕事件處理程式中處理找不到內容的情況
        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String keyword = searchField.getText();
                if (keyword != null && !keyword.trim().isEmpty()) {
                    ObservableList<Product> searchResult = FXCollections.observableArrayList();
                    // 調用 DAO 的方法進行模糊查詢
                    searchResult.addAll(productDao.findByProductIDOrNameContaining(keyword));
                    if (searchResult.isEmpty()) {
                        // 如果搜索結果為空，顯示提示視窗
                        showAlert("找不到內容，請重新輸入");
                    } else {
                        table.setItems(searchResult);
                        System.out.println(searchResult);
                    }
                }
            }
        });

        Button btnClearFilters = new Button("清空篩選條件");
        btnClearFilters.setStyle("-fx-font-size: 14px; -fx-background-color: #FF5151; -fx-text-fill: white;");
        btnClearFilters.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                searchField.clear();
                switch (currentCategory) {
                    case "瀏覽全部":
                        product_list = FXCollections.observableList(productDao.getAllProducts());
                        break;
                    case "生鮮肉類":
                        product_list = FXCollections.observableList(productDao.findByCate("生鮮肉類"));
                        break;
                    case "生鮮魚類":
                        product_list = FXCollections.observableList(productDao.findByCate("生鮮魚類"));
                        break;
                    default:
                        product_list = FXCollections.observableList(productDao.getAllProducts());
                        break;
                }
                table.setItems(product_list);
                System.out.println(product_list);
            }
        });

        // 使用容器放置搜尋按鈕、輸入框和清空按鈕
        HBox containerProductSelection = new HBox();
        containerProductSelection.setSpacing(10);
        containerProductSelection.getChildren().addAll(searchField, btnSearch, btnClearFilters);

        // 增加水準線和標籤
        VBox vbox = new VBox();
        vbox.setSpacing(5);  // 調整這裡的值以縮小間距
        vbox.setPadding(new Insets(5, 0, 5, 0));  // 調整這裡的值以縮小間距

        Label filterLabel = new Label("建立篩選條件");
        filterLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        vbox.getChildren().addAll(
                new Separator(),
                filterLabel,
                containerProductSelection,
                new Separator()
        );

        return new HBox(vbox);
    }

    // 修改showAlert方法，使用類別屬性searchField
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 清空輸入框的內容
                searchField.clear();
                // 將表格資料恢復到還沒有篩選之前被選到的類別
                switch (currentCategory) {
                    case "瀏覽全部":
                        product_list = FXCollections.observableList(productDao.getAllProducts());
                        break;
                    case "生鮮肉類":
                        product_list = FXCollections.observableList(productDao.findByCate("生鮮肉類"));
                        break;
                    case "生鮮魚類":
                        product_list = FXCollections.observableList(productDao.findByCate("生鮮魚類"));
                        break;
                    default:
                        product_list = FXCollections.observableList(productDao.getAllProducts());
                        break;
                }
                table.setItems(product_list);
            }
        });
    }

    // 修改表格初始化方法，讓表格內的元素不可編輯
    private void initializeProductTable() {

        table = new TableView<>();
        table.setEditable(false);  // 將表格設置為不可編輯
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columnNames = {"product_id", "category", "name", "price", "photo", "description"};
        String[] columnChineseNames = {"產品編號", "類別", "名稱", "價格", "照片連結", "產品描述"};
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn<Product, String> col = new TableColumn<>(columnChineseNames[i]);
            col.setCellValueFactory(new PropertyValueFactory<>(columnNames[i]));
            table.getColumns().add(col);
        }

        TableColumn<Product, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button modifyButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                modifyButton.setStyle("-fx-font-size: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");  // 設置背景顏色
                deleteButton.setStyle("-fx-font-size: 10px; -fx-background-color: #F44336; -fx-text-fill: white;");  // 設置背景顏色

                modifyButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showUpdateDialog(product);
                });

                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showDeleteDialog(product);
                });

                HBox pane = new HBox(modifyButton, deleteButton);
                pane.setSpacing(10);
                setGraphic(pane);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox pane = new HBox(modifyButton, deleteButton);
                    pane.setSpacing(10);
                    setGraphic(pane);
                }
            }
        });

        table.getColumns().add(colAction);
    }

    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("新增產品");

        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));

        // 使用 GridPane 來對齊標籤和輸入欄位
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(70);
        gridPane.getColumnConstraints().addAll(col1, col2);

        // 產品編號
        Label idLabel = new Label("產品編號:");
        TextField idField = new TextField();
        gridPane.add(idLabel, 0, 0);
        gridPane.add(idField, 1, 0);

        // 類別
        Label categoryLabel = new Label("類別:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-pref-width: 150px;");
        categoryComboBox.getItems().remove("其他");
        categoryComboBox.getItems().addAll(productDao.getAllCategories()); // 從資料庫獲取所有類別
        categoryComboBox.getItems().add("其他"); // 新增「其他」選項

        gridPane.add(categoryLabel, 0, 1);
        gridPane.add(categoryComboBox, 1, 1);

        TextField otherCategoryField = new TextField();
        otherCategoryField.setPromptText("請輸入想要新增的類別");
        otherCategoryField.setVisible(false); // Initially hide the TextField
        otherCategoryField.managedProperty().bind(otherCategoryField.visibleProperty()); // Bind managedProperty to visibleProperty

        categoryComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("其他")) {
                    otherCategoryField.setVisible(true);
                } else {
                    otherCategoryField.setVisible(false);
                }
            }
        });

        gridPane.add(otherCategoryField, 1, 2);

        // 名稱
        Label nameLabel = new Label("名稱:");
        TextField nameField = new TextField();
        gridPane.add(nameLabel, 0, 3);
        gridPane.add(nameField, 1, 3);

        // 價格
        Label priceLabel = new Label("價格:");
        TextField priceField = new TextField();
        gridPane.add(priceLabel, 0, 4);
        gridPane.add(priceField, 1, 4);

        // 照片連結
        Label photoLabel = new Label("照片連結:");
        TextField photoField = new TextField();
        gridPane.add(photoLabel, 0, 5);
        gridPane.add(photoField, 1, 5);

        // 產品描述
        Label descriptionLabel = new Label("產品描述:");
        TextArea descriptionField = new TextArea();
        descriptionField.setPrefRowCount(4); // 設置 TextArea 的行數
        gridPane.add(descriptionLabel, 0, 6);
        gridPane.add(descriptionField, 1, 6);

        // 確定新增按鈕
        Button confirmButton = new Button("確定新增");
        confirmButton.setOnAction(e -> {
            String categoryId;
            if (categoryComboBox.getValue().equals("其他")) {
                categoryId = otherCategoryField.getText();
            } else {
                categoryId = categoryComboBox.getValue();
            }

            Product newProduct = new Product(
                    idField.getText(),
                    categoryId,
                    nameField.getText(),
                    Integer.parseInt(priceField.getText()),
                    photoField.getText(),
                    descriptionField.getText()
            );
            productDao.insert(newProduct);
            product_list.add(newProduct);
            table.refresh();
            dialog.close();
        });

        VBox buttonBox = new VBox(confirmButton);
        buttonBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().addAll(gridPane, buttonBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showUpdateDialog(Product product) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Product");

        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));

        // 使用 GridPane 來對齊標籤和輸入欄位
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(70);
        gridPane.getColumnConstraints().addAll(col1, col2);

        // 產品編號
        Label idLabel = new Label("產品編號:");
        TextField idField = new TextField(product.getProduct_id());
        idField.setEditable(false); // 禁止編輯產品編號
        gridPane.add(idLabel, 0, 0);
        gridPane.add(idField, 1, 0);

        // 類別
        Label categoryLabel = new Label("類別:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-pref-width: 150px;");
        categoryComboBox.getItems().addAll(productDao.getAllCategories());  // 從資料庫獲取所有類別
        categoryComboBox.getItems().add("其他"); // 添加 "其他" 選項
        categoryComboBox.setValue(product.getCategory());  // 設置初始值為產品的當前類別
        gridPane.add(categoryLabel, 0, 1);
        gridPane.add(categoryComboBox, 1, 1);

        // 其他類別輸入框
        TextField otherCategoryField = new TextField();
        otherCategoryField.setPromptText("請輸入想要新增的類別");
        otherCategoryField.setVisible(false); // Initially hide the TextField
        otherCategoryField.managedProperty().bind(otherCategoryField.visibleProperty()); // Bind managedProperty to visibleProperty
        gridPane.add(otherCategoryField, 1, 2);

        // 監聽類別下拉清單的值變化事件
        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.equals("其他")) {
                otherCategoryField.setVisible(true);
            } else {
                otherCategoryField.setVisible(false);
            }
        });

        // 名稱
        Label nameLabel = new Label("名稱:");
        TextField nameField = new TextField(product.getName());
        gridPane.add(nameLabel, 0, 3);
        gridPane.add(nameField, 1, 3);

        // 價格
        Label priceLabel = new Label("價格:");
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        gridPane.add(priceLabel, 0, 4);
        gridPane.add(priceField, 1, 4);

        // 照片連結
        Label photoLabel = new Label("照片連結:");
        TextField photoField = new TextField(product.getPhoto());
        gridPane.add(photoLabel, 0, 5);
        gridPane.add(photoField, 1, 5);

        // 產品描述
        Label descriptionLabel = new Label("產品描述:");
        TextArea descriptionField = new TextArea(product.getDescription());
        descriptionField.setPrefRowCount(4); // 設置 TextArea 的行數
        gridPane.add(descriptionLabel, 0, 6);
        gridPane.add(descriptionField, 1, 6);

        // 確定更改按鈕
        Button confirmButton = new Button("確定更改");
        confirmButton.setOnAction(e -> {
            String categoryId;
            if (categoryComboBox.getValue().equals("其他")) {
                categoryId = otherCategoryField.getText();
            } else {
                categoryId = categoryComboBox.getValue();
            }

            product.setProduct_id(idField.getText());
            product.setCategory(categoryId);  // 獲取選中的類別
            product.setName(nameField.getText());
            product.setPrice(Integer.parseInt(priceField.getText()));
            product.setPhoto(photoField.getText());
            product.setDescription(descriptionField.getText());

            productDao.update(product);
            table.refresh();
            dialog.close();
        });

        VBox buttonBox = new VBox(confirmButton);
        buttonBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().addAll(gridPane, buttonBox);

        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showDeleteDialog(Product product) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("刪除產品");

        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));

        // 使用 GridPane 來對齊標籤和輸入欄位
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(70);
        gridPane.getColumnConstraints().addAll(col1, col2);

        // 產品編號
        Label idLabel = new Label("產品編號:");
        TextField idField = new TextField(product.getProduct_id());
        idField.setEditable(false); // 禁止編輯產品編號
        gridPane.add(idLabel, 0, 0);
        gridPane.add(idField, 1, 0);

        // 類別
        Label categoryLabel = new Label("類別:");
        TextField categoryField = new TextField(product.getCategory());
        categoryField.setEditable(false); // 禁止編輯
        gridPane.add(categoryLabel, 0, 1);
        gridPane.add(categoryField, 1, 1);

        // 名稱
        Label nameLabel = new Label("名稱:");
        TextField nameField = new TextField(product.getName());
        nameField.setEditable(false); // 禁止編輯
        gridPane.add(nameLabel, 0, 2);
        gridPane.add(nameField, 1, 2);

        // 價格
        Label priceLabel = new Label("價格:");
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        priceField.setEditable(false); // 禁止編輯
        gridPane.add(priceLabel, 0, 3);
        gridPane.add(priceField, 1, 3);

        // 照片連結
        Label photoLabel = new Label("照片連結:");
        TextField photoField = new TextField(product.getPhoto());
        photoField.setEditable(false); // 禁止編輯
        gridPane.add(photoLabel, 0, 4);
        gridPane.add(photoField, 1, 4);

        // 產品描述
        Label descriptionLabel = new Label("產品描述:");
        TextArea descriptionField = new TextArea(product.getDescription());
        descriptionField.setEditable(false); // 禁止編輯
        descriptionField.setPrefRowCount(4); // 設置 TextArea 的行數
        gridPane.add(descriptionLabel, 0, 5);
        gridPane.add(descriptionField, 1, 5);

        // 確定刪除按鈕
        Button confirmButton = new Button("確定刪除");
        confirmButton.setOnAction(e -> {
            productDao.delete(product.getProduct_id());
            product_list.remove(product); // 從清單中移除相應的產品物件
            table.refresh(); // 刷新表格
            dialog.close();
        });

        VBox buttonBox = new VBox(confirmButton);
        buttonBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().addAll(gridPane, buttonBox);

        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

//表格新增項目刪除項目之操作區塊
    private HBox getProductOperationContainer() {

        //定義新增一筆按鈕
        Button btnBlank = new Button("新增產品");
        btnBlank.getStyleClass().setAll("button", "success");
        btnBlank.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showAddDialog();
            }
        });
        //放置前述任務功能按鈕
        HBox containerProductOperation = new HBox();
        containerProductOperation.setSpacing(5);
        containerProductOperation.getChildren().add(btnBlank);

        return containerProductOperation;
    }

    //所有元件與事件並將所有元件放入root
    public VBox get_root_pane() {
        //根容器 所有的元件都放在裡面container，
        //最後再放進佈景中scene，佈景再放進舞臺中stage
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getStylesheets().add("/css/bootstrap3.css");

        //塞入產品類別過濾區塊
        BorderPane productCategoryTile = getProductCategoryContainer();
        root.getChildren().add(productCategoryTile);

        //塞入產品選擇區塊
        HBox productSelectionTile = getProductSelectionContainer();
        root.getChildren().add(productSelectionTile);

        //塞入增加表格刪除項目操作之容器
        root.getChildren().add(getProductOperationContainer());

        //塞入表格
        initializeProductTable(); //表格初始化
        root.getChildren().add(table);

        return root;
    }

    @Override
    public void start(Stage stage) throws Exception {
        // 取得root pane
        VBox root = get_root_pane();

        // 塞入佈景
        Scene scene = new Scene(root);
        stage.setTitle("產品資料維護");
        stage.setScene(scene);
        stage.show();
    }

    private void switchToAppInterface() {
        App app = new App();
        Stage stage = (Stage) table.getScene().getWindow();
        try {
            app.start(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
