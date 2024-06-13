-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- 主機： 127.0.0.1
-- 產生時間： 2024-06-13 18:52:02
-- 伺服器版本： 10.4.32-MariaDB
-- PHP 版本： 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `db_pos`
--
DROP DATABASE IF EXISTS `db_pos`;
CREATE DATABASE IF NOT EXISTS `db_pos` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `db_pos`;

-- --------------------------------------------------------

--
-- 資料表結構 `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `order_id` int(11) NOT NULL,
  `order_num` varchar(20) NOT NULL,
  `product_id` varchar(20) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `order_detail`
--

INSERT INTO `order_detail` (`order_id`, `order_num`, `product_id`, `quantity`) VALUES
(1, 'ord-101', 'p-b-101', 10),
(2, 'ord-103', 'p-b-101', 1),
(3, 'ord-104', 'p-b-102', 1),
(4, 'ord-105', 'p-b-101', 1),
(5, 'ord-105', 'p-b-102', 1),
(6, 'ord-105', 'p-b-103', 1),
(7, 'ord-106', 'p-b-101', 1),
(8, 'ord-107', 'p-b-101', 1),
(9, 'ord-107', 'p-b-102', 1),
(10, 'ord-107', 'p-b-103', 1),
(11, 'ord-107', 'p-f-107', 2),
(12, 'ord-107', 'p-f-109', 1),
(13, 'ord-107', 'p-f-110', 1),
(14, 'ord-108', 'p-b-101', 1),
(15, 'ord-108', 'p-b-103', 1),
(16, 'ord-108', 'p-b-105', 1),
(17, 'ord-109', 'p-b-101', 1),
(18, 'ord-109', 'p-b-102', 1),
(19, 'ord-109', 'p-b-103', 1),
(20, 'ord-109', 'p-b-104', 1),
(21, 'ord-109', 'p-b-105', 1),
(22, 'ord-109', 'p-b-106', 1),
(23, 'ord-109', 'p-f-107', 1),
(24, 'ord-109', 'p-f-108', 1),
(25, 'ord-109', 'p-f-109', 1),
(26, 'ord-109', 'p-f-110', 1),
(27, 'ord-109', 'p-f-111', 1),
(28, 'ord-109', 'p-f-112', 1),
(29, 'ord-109', 'p-f-113', 1),
(30, 'ord-110', 'p-b-101', 1),
(31, 'ord-110', 'p-b-102', 1),
(32, 'ord-110', 'p-b-103', 4),
(33, 'ord-110', 'p-b-104', 1),
(34, 'ord-110', 'p-f-107', 1),
(35, 'ord-110', 'p-f-108', 1),
(36, 'ord-111', 'p-b-101', 1),
(37, 'ord-111', 'p-b-102', 1),
(38, 'ord-111', 'p-b-103', 4),
(39, 'ord-111', 'p-b-104', 1),
(40, 'ord-111', 'p-f-107', 1),
(41, 'ord-111', 'p-f-108', 1),
(42, 'ord-112', 'p-b-101', 1),
(43, 'ord-112', 'p-b-102', 1),
(44, 'ord-112', 'p-b-103', 1);

-- --------------------------------------------------------

--
-- 資料表結構 `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id` varchar(20) NOT NULL,
  `category` varchar(50) NOT NULL,
  `name` varchar(150) NOT NULL,
  `price` int(11) NOT NULL,
  `photo` varchar(200) NOT NULL,
  `description` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `product`
--

INSERT INTO `product` (`product_id`, `category`, `name`, `price`, `photo`, `description`) VALUES
('p-b-101', '生鮮肉類', '牛肉', 80, 'beef.jpg', '優質牛肉，口感絕佳，滿足您挑剔的味蕾。'),
('p-b-102', '生鮮肉類', '雞肉', 80, 'chicken.jpg', '新鮮雞肉，美味多汁'),
('p-b-103', '生鮮肉類', '豬肉', 90, 'pork.jpg', '豬肉營養豐富，烹飪方式多樣，是餐桌上的美味佳餚。'),
('p-b-104', '生鮮肉類', '鴨肉', 100, 'duck.jpg', '鴨肉營養豐富，風味獨特，適合各種料理方式，是餐桌上不可或缺的美味佳餚。'),
('p-b-105', '生鮮肉類', '鵝肉', 100, 'goose.jpg', '優質鵝肉，營養美味，料理百搭，健康養生。'),
('p-b-106', '生鮮肉類', '羊肉', 100, 'mutton.jpg', '精選新鮮羊肉，口感軟嫩，香氣濃郁，涮火鍋、燒烤、燉湯，美味多樣。'),
('p-f-107', '生鮮魚類', '吳郭魚', 120, 'tilapia.jpg', '吳郭魚肉質鮮美，細刺少，無論清蒸、水煮、煎炸或滷煮，都非常美味。'),
('p-f-108', '生鮮魚類', '午仔魚', 75, 'Midnight_fish.jpg', '一午二鮸三嘉鱲，午仔魚肉質鮮嫩，煎、蒸、煮、炸樣樣都好吃。'),
('p-f-109', '生鮮魚類', '白鯧', 65, 'white_pomfret.jpg', '白鯧，肉質細緻鮮嫩，入口即化，適合清蒸或煎烤，是年節佳餚。'),
('p-f-110', '生鮮魚類', '土魠魚', 60, 'native_kingfish.jpg', '土魠魚，油脂肥美，肉質細嫩，適合多種料理方式，是台南著名的年菜食材。'),
('p-f-111', '生鮮魚類', '鱸魚', 45, 'sea​​bass.jpg', '鮮美細嫩鱸魚肉，富含營養好滋味，清蒸、水煮、煎烤都適合。'),
('p-f-112', '生鮮魚類', '黃鰭鯛', 45, 'yellowfin_snapper.jpg', '黃鰭鯛肉質鮮美細嫩，刺少適合各種料理，是台灣沿海的高級食用魚類。'),
('p-f-113', '生鮮魚類', '台灣鯛', 70, 'Taiwanese_snapper.jpg', '台灣鯛，肉質鮮美細嫩，刺少無腥味，是料理佳品。');

-- --------------------------------------------------------

--
-- 資料表結構 `sale_order`
--

DROP TABLE IF EXISTS `sale_order`;
CREATE TABLE `sale_order` (
  `order_num` varchar(20) NOT NULL,
  `order_date` datetime NOT NULL DEFAULT current_timestamp(),
  `order_date_ymd` date NOT NULL DEFAULT current_timestamp(),
  `total_price` double(22,0) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `sale_order`
--

INSERT INTO `sale_order` (`order_num`, `order_date`, `order_date_ymd`, `total_price`, `user_id`) VALUES
('ord-101', '2021-05-04 22:54:47', '2024-05-04', 70, 1),
('ord-102', '2021-05-04 22:55:19', '2024-05-04', 380, 2),
('ord-103', '2024-06-01 01:26:06', '2024-06-01', 70, 0),
('ord-104', '2024-06-01 01:26:55', '2024-06-01', 80, 1),
('ord-105', '2024-06-01 01:29:25', '2024-06-01', 240, 0),
('ord-106', '2024-06-06 22:57:33', '2024-06-06', 80, 0),
('ord-107', '2024-06-06 23:03:04', '2024-06-06', 615, 0),
('ord-108', '2024-06-08 01:38:01', '2024-06-08', 270, 0),
('ord-109', '2024-06-08 15:14:11', '2024-06-08', 1030, 0),
('ord-110', '2024-06-13 22:45:06', '2024-06-13', 815, 0),
('ord-111', '2024-06-13 23:48:01', '2024-06-13', 815, 4),
('ord-112', '2024-06-14 00:31:59', '2024-06-14', 250, 0);

-- --------------------------------------------------------

--
-- 資料表結構 `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `user_name` varchar(20) NOT NULL,
  `user_phone` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 傾印資料表的資料 `users`
--

INSERT INTO `users` (`user_id`, `user_name`, `user_phone`) VALUES
(0, '訪客', '0000000000'),
(1, '李大同', '0912345678'),
(2, '王曉民', '0932567891'),
(4, 'abc', '0988754544'),
(5, '2121', '0987854612');

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `order_detail`
--
ALTER TABLE `order_detail`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `order_num` (`order_num`),
  ADD KEY `product_id` (`product_id`);

--
-- 資料表索引 `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`);

--
-- 資料表索引 `sale_order`
--
ALTER TABLE `sale_order`
  ADD PRIMARY KEY (`order_num`),
  ADD KEY `user_id` (`user_id`);

--
-- 資料表索引 `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- 在傾印的資料表使用自動遞增(AUTO_INCREMENT)
--

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `order_detail`
--
ALTER TABLE `order_detail`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `order_detail`
--
ALTER TABLE `order_detail`
  ADD CONSTRAINT `FK_order_detail_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  ADD CONSTRAINT `FK_order_detail_sale_order` FOREIGN KEY (`order_num`) REFERENCES `sale_order` (`order_num`);

--
-- 資料表的限制式 `sale_order`
--
ALTER TABLE `sale_order`
  ADD CONSTRAINT `FK_sale_order_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
