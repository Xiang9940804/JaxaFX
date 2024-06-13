package models;

import java.util.TreeMap;
import models.Product;

public class ReadCategoryProduct {
    
     //準備好產品清單  
    public static TreeMap<String, Product> readProduct() {
        //read_product_from_file(); //從檔案或資料庫讀入產品菜單資訊
        //放所有產品  產品編號  產品物件
        TreeMap<String, Product> product_dict = new TreeMap<>();
        String[][] product_array = {
            {"p-b-101", "生鮮肉類", "牛肉", "70", "beef.jpg", "優質牛肉，口感絕佳，滿足您挑剔的味蕾。"},
            {"p-b-102", "生鮮肉類", "雞肉", "80", "chicken.jpg", "新鮮雞肉，美味多汁"},
            {"p-b-103", "生鮮肉類", "豬肉", "90", "pork.jpg", "豬肉營養豐富，烹飪方式多樣，是餐桌上的美味佳餚。"},
            {"p-b-104", "生鮮肉類", "鴨肉", "100", "duck.jpg", "鴨肉營養豐富，風味獨特，適合各種料理方式，是餐桌上不可或缺的美味佳餚。"},
            {"p-b-105", "生鮮肉類", "鵝肉", "100", "goose.jpg", "優質鵝肉，營養美味，料理百搭，健康養生。"},
            {"p-b-106", "生鮮肉類", "羊肉", "100", "mutton.jpg", "精選新鮮羊肉，口感軟嫩，香氣濃郁，涮火鍋、燒烤、燉湯，美味多樣。"},
            {"p-f-107", "生鮮魚類", "吳郭魚", "120", "tilapia.jpg", "吳郭魚肉質鮮美，細刺少，無論清蒸、水煮、煎炸或滷煮，都非常美味。"},
            {"p-f-108", "生鮮魚類", "午仔魚", "75", "Midnight_fish.jpg", "一午二鮸三嘉鱲，午仔魚肉質鮮嫩，煎、蒸、煮、炸樣樣都好吃。"},
            {"p-f-109", "生鮮魚類", "白鯧", "65", "white_pomfret.jpg", "白鯧，肉質細緻鮮嫩，入口即化，適合清蒸或煎烤，是年節佳餚。"},
            {"p-f-110", "生鮮魚類", "土魠魚", "60", "native_kingfish.jpg", "土魠魚，油脂肥美，肉質細嫩，適合多種料理方式，是台南著名的年菜食材。"},
            {"p-f-111", "生鮮魚類", "鱸魚", "45", "sea​​bass.jpg", "鮮美細嫩鱸魚肉，富含營養好滋味，清蒸、水煮、煎烤都適合。"},
            {"p-f-112", "生鮮魚類", "黃鰭鯛", "45", "yellowfin_snapper.jpg", "黃鰭鯛肉質鮮美細嫩，刺少適合各種料理，是台灣沿海的高級食用魚類。"},
            {"p-f-113", "生鮮魚類", "台灣鯛", "70", "Taiwanese_snapper.jpg", "台灣鯛，肉質鮮美細嫩，刺少無腥味，是料理佳品。"}
        };

        //一筆放入字典變數product_dict中
        for (String[] item : product_array) {
            Product product = new Product(
                    item[0], 
                    item[1], 
                    item[2], 
                    Integer.parseInt(item[3]), //價格轉為int
                    item[4], 
                    item[5]);
            //將這一筆放入字典變數product_dict中 
            product_dict.put(product.getProduct_id(), product);
        }
        return product_dict; 
    }
}
