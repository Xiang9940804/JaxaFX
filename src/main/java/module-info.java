module mypos {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql; //資料庫
    exports mypos;
    exports models; //models模型套件目錄
}
