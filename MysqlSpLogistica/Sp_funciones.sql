-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gestion_inventario
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bodegas`
--

DROP TABLE IF EXISTS `bodegas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bodegas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `ubicacion` varchar(150) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bodegas`
--

LOCK TABLES `bodegas` WRITE;
/*!40000 ALTER TABLE `bodegas` DISABLE KEYS */;
INSERT INTO `bodegas` VALUES (1,'Bodega Norte','San Pablo y Tambo Ibarra','ACTIVO'),(2,'Bodega Sur','San Pedro y Laguna Quito','ACTIVO'),(3,'Bodega Tulipa','Moras y Manzanas Guayaquil ','ACTIVO');
/*!40000 ALTER TABLE `bodegas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_movimiento`
--

DROP TABLE IF EXISTS `detalle_movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_movimiento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nota_movimiento_id` int NOT NULL,
  `lote_id` int NOT NULL,
  `producto_id` int DEFAULT NULL,
  `cantidad` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `nota_movimiento_id` (`nota_movimiento_id`),
  KEY `lote_id` (`lote_id`),
  KEY `fk_detalle_producto` (`producto_id`),
  CONSTRAINT `detalle_movimiento_ibfk_1` FOREIGN KEY (`nota_movimiento_id`) REFERENCES `nota_movimiento` (`id`) ON DELETE CASCADE,
  CONSTRAINT `detalle_movimiento_ibfk_2` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`),
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`),
  CONSTRAINT `detalle_movimiento_chk_1` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_movimiento`
--

LOCK TABLES `detalle_movimiento` WRITE;
/*!40000 ALTER TABLE `detalle_movimiento` DISABLE KEYS */;
INSERT INTO `detalle_movimiento` VALUES (3,13,4,NULL,50),(4,14,4,NULL,25),(5,15,1,NULL,20),(6,16,1,NULL,30),(7,17,1,NULL,50),(8,18,1,NULL,10),(9,20,5,NULL,20),(10,21,19,NULL,20),(11,22,20,NULL,10),(12,23,25,NULL,10),(13,26,19,NULL,10),(14,27,19,NULL,10),(15,28,25,NULL,10),(16,29,25,NULL,10),(17,33,28,NULL,200),(18,34,28,NULL,200),(19,42,31,NULL,20),(20,43,31,13,3),(21,48,31,12,80),(22,49,31,12,20),(23,50,31,12,20),(24,51,31,12,20),(25,54,31,12,20),(26,58,31,12,20),(27,58,31,12,20),(28,58,31,12,20),(29,59,31,12,20),(30,60,31,12,20),(31,61,31,12,20),(32,62,31,12,20),(33,63,31,12,20),(34,64,31,12,20),(35,65,31,12,20),(36,66,31,12,20),(37,66,31,12,20),(38,67,31,12,20),(39,66,31,12,20),(40,67,31,12,20),(41,67,31,12,50),(42,68,31,12,20),(43,69,31,12,20),(44,70,31,12,40),(45,71,34,15,10),(46,72,34,15,10),(47,73,34,15,10),(48,74,31,13,10),(49,75,31,12,10),(50,76,31,12,20),(51,77,34,15,10),(52,78,34,15,10),(53,79,31,12,10),(54,80,31,12,20),(55,81,34,15,10),(56,82,34,15,10),(57,83,34,15,10),(58,84,34,15,10),(59,85,31,12,20),(60,86,31,12,20),(61,87,31,12,20),(62,88,31,12,20),(63,90,31,12,20),(64,97,32,14,10),(65,98,32,16,10),(66,98,32,16,5),(67,101,34,15,10),(68,109,31,12,20),(69,110,32,17,10),(70,111,31,12,50),(71,112,31,13,20),(72,113,34,15,10),(73,114,34,4,20),(74,115,1,1,20),(75,116,32,17,20),(76,118,34,15,5),(77,119,2,1,20),(78,120,34,2,30),(79,121,1,2,30),(80,122,1,6,50),(81,123,2,6,20),(82,126,35,18,50),(83,127,35,18,10),(84,128,1,6,10),(85,129,35,18,10),(86,130,31,13,10);
/*!40000 ALTER TABLE `detalle_movimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario_bodega`
--

DROP TABLE IF EXISTS `inventario_bodega`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario_bodega` (
  `id` int NOT NULL AUTO_INCREMENT,
  `bodega_id` int NOT NULL,
  `lote_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `stock` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bodega_lote_producto` (`bodega_id`,`lote_id`,`producto_id`),
  KEY `lote_id` (`lote_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `inventario_bodega_ibfk_1` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`id`),
  CONSTRAINT `inventario_bodega_ibfk_2` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`),
  CONSTRAINT `inventario_bodega_ibfk_3` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario_bodega`
--

LOCK TABLES `inventario_bodega` WRITE;
/*!40000 ALTER TABLE `inventario_bodega` DISABLE KEYS */;
INSERT INTO `inventario_bodega` VALUES (1,1,31,12,350.00),(2,1,31,13,30.00),(3,1,32,14,50.00),(4,2,31,12,340.00),(9,1,34,15,5.00),(10,3,34,15,10.00),(11,2,31,13,40.00),(12,2,34,15,35.00),(13,1,32,16,35.00),(14,2,32,17,20.00),(15,1,32,17,30.00),(16,2,34,4,20.00),(17,2,1,1,20.00),(18,1,2,1,20.00),(19,1,34,2,30.00),(20,2,1,2,30.00),(21,1,1,6,40.00),(22,2,2,6,20.00),(23,1,35,18,100.00),(24,1,35,19,80.00),(25,2,35,18,30.00),(26,2,1,6,10.00);
/*!40000 ALTER TABLE `inventario_bodega` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lotes`
--

DROP TABLE IF EXISTS `lotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lotes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_lote` varchar(50) NOT NULL,
  `bodega_id` int NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_lotes_bodegas` (`bodega_id`),
  CONSTRAINT `fk_lotes_bodegas` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lotes`
--

LOCK TABLES `lotes` WRITE;
/*!40000 ALTER TABLE `lotes` DISABLE KEYS */;
INSERT INTO `lotes` VALUES (1,'15Le',1,'2026-10-10','ACTIVO','2026-08-26 04:18:22'),(2,'16P',2,'2026-12-30','INACTIVO','2026-08-26 04:21:51'),(3,'17X',2,'2028-10-21','ACTIVO','2026-08-27 13:24:09'),(4,'18U',1,'2028-12-09','ACTIVO','2026-08-29 03:19:00'),(5,'19B',2,'2029-05-12','ACTIVO','2026-08-30 04:45:43'),(19,'20P',1,'2027-01-01','ACTIVO','2026-08-30 06:38:16'),(20,'21Y',1,'2029-01-01','ACTIVO','2026-08-30 23:45:45'),(25,'22q',1,'2029-01-01','ACTIVO','2026-08-31 01:36:44'),(26,'18Uv',1,'2028-12-09','ACTIVO','2026-08-31 01:40:11'),(27,'23Ñ',1,'2029-05-02','ACTIVO','2026-08-31 01:43:42'),(28,'101R',3,'2029-01-01','ACTIVO','2026-08-31 17:35:32'),(29,'102R',1,'2028-01-01','ACTIVO','2026-08-31 17:46:57'),(30,'125i',1,'2024-01-01','ACTIVO','2026-08-31 22:17:01'),(31,'155A',1,'2029-10-10','ACTIVO','2026-08-31 22:51:57'),(32,'777Prue',2,'2034-10-10','ACTIVO','2026-09-01 02:21:02'),(33,'10785O',1,'2029-10-10','ACTIVO','2026-09-01 05:44:16'),(34,'11111111F',1,'2029-10-10','ACTIVO','2026-09-01 05:45:07'),(35,'224LY',2,'2029-12-12','ACTIVO','2026-09-02 02:39:58');
/*!40000 ALTER TABLE `lotes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nota_movimiento`
--

DROP TABLE IF EXISTS `nota_movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nota_movimiento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_movimiento` varchar(20) NOT NULL,
  `bodega_origen_id` int DEFAULT NULL,
  `bodega_destino_id` int DEFAULT NULL,
  `responsable_id` int NOT NULL,
  `fecha_movimiento` datetime DEFAULT CURRENT_TIMESTAMP,
  `observacion` text,
  PRIMARY KEY (`id`),
  KEY `bodega_origen_id` (`bodega_origen_id`),
  KEY `bodega_destino_id` (`bodega_destino_id`),
  KEY `responsable_id` (`responsable_id`),
  CONSTRAINT `nota_movimiento_ibfk_1` FOREIGN KEY (`bodega_origen_id`) REFERENCES `bodegas` (`id`),
  CONSTRAINT `nota_movimiento_ibfk_2` FOREIGN KEY (`bodega_destino_id`) REFERENCES `bodegas` (`id`),
  CONSTRAINT `nota_movimiento_ibfk_3` FOREIGN KEY (`responsable_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nota_movimiento`
--

LOCK TABLES `nota_movimiento` WRITE;
/*!40000 ALTER TABLE `nota_movimiento` DISABLE KEYS */;
INSERT INTO `nota_movimiento` VALUES (1,'ENTRADA',NULL,1,1,'2026-08-27 08:54:37','Productos alimentarios'),(2,'ENTRADA',NULL,1,1,'2026-08-27 09:01:20','productos alimentarios'),(3,'ENTRADA',NULL,1,1,'2026-08-27 09:03:53','Productos alimentarios'),(4,'SALIDA',2,NULL,1,'2026-08-27 09:06:43','20 medias'),(5,'SALIDA',1,2,1,'2026-08-27 09:11:37','salida'),(6,'ENTRADA',NULL,2,1,'2026-08-27 09:12:17','ingreso producto'),(7,'ENTRADA',NULL,1,1,'2026-08-27 20:02:53','sg'),(8,'ENTRADA',NULL,1,1,'2026-08-27 20:17:38','producto ah'),(9,'ENTRADA',NULL,1,1,'2026-08-27 20:29:06','asd'),(10,'ENTRADA',NULL,1,1,'2026-08-27 20:33:57','jTextField2'),(11,'ENTRADA',NULL,1,1,'2026-08-28 22:15:11','jTextField2'),(12,'ENTRADA',NULL,1,1,'2026-08-28 22:20:08','uva seca'),(13,'ENTRADA',NULL,1,1,'2026-08-28 22:31:49','uvas'),(14,'SALIDA',1,2,1,'2026-08-28 22:34:06','salida de uvas'),(15,'ENTRADA',NULL,1,1,'2026-08-28 22:52:51','jTextField2'),(16,'SALIDA',1,2,1,'2026-08-28 23:01:07','salida de producto'),(17,'ENTRADA',NULL,1,1,'2026-08-28 23:12:22','entrada'),(18,'ENTRADA',3,1,1,'2026-08-28 23:37:51','ingreso  de producto'),(19,'ENTRADA',1,1,1,'2026-08-28 23:56:29','ert'),(20,'ENTRADA',1,3,2,'2026-08-29 23:47:02','entrada de bolas'),(21,'ENTRADA',1,1,1,'2026-08-30 01:40:27','entrada'),(22,'SALIDA',1,2,1,'2026-08-30 18:46:57','salida de juguetes yoyo'),(23,'ENTRADA',3,1,1,'2026-08-30 20:37:46','entrada de quesos'),(24,'ENTRADA',1,1,1,'2026-08-30 22:56:35','entrada de producto'),(25,'ENTRADA',1,1,1,'2026-08-30 23:01:00','yjk'),(26,'ENTRADA',1,1,1,'2026-08-30 23:09:54','tyu'),(27,'ENTRADA',1,1,1,'2026-08-30 23:17:10','sdfwetg'),(28,'ENTRADA',1,3,1,'2026-08-30 23:20:19','etyey'),(29,'ENTRADA',1,3,1,'2026-08-30 23:21:55','sdf'),(30,'ENTRADA',1,1,1,'2026-08-30 23:28:12','hrth'),(31,'ENTRADA',1,1,1,'2026-08-30 23:30:11','gerg'),(32,'SALIDA',3,3,1,'2026-08-31 12:37:38','PRUEBA'),(33,'SALIDA',3,1,1,'2026-08-31 12:38:06','RATON'),(34,'ENTRADA',3,1,1,'2026-08-31 12:41:22','RECEPCION'),(35,'SALIDA',1,2,1,'2026-08-31 18:17:01','salida de producto'),(36,'SALIDA',1,2,1,'2026-08-31 20:31:34','salida de producto a'),(37,'SALIDA',1,2,1,'2026-08-31 20:35:10','sdfg'),(38,'SALIDA',1,2,1,'2026-08-31 20:37:07','sdfgsg'),(39,'SALIDA',1,2,1,'2026-08-31 20:38:28','eyh'),(40,'ENTRADA',1,2,1,'2026-08-31 20:41:45','5u85'),(41,'SALIDA',1,2,1,'2026-08-31 20:48:43','sdgfg'),(42,'SALIDA',1,2,1,'2026-08-31 20:56:30','sfdsdf'),(43,'SALIDA',1,2,1,'2026-08-31 21:11:14','yutjh'),(44,'SALIDA',2,1,5,'2026-08-31 21:22:19','ryjjy'),(45,'SALIDA',2,1,5,'2026-08-31 21:31:14','eertg'),(46,'SALIDA',2,1,5,'2026-08-31 21:34:56','fbgdf'),(47,'SALIDA',2,1,5,'2026-08-31 22:00:01','erhgetrh'),(48,'SALIDA',1,2,1,'2026-08-31 22:06:42','salida de producto'),(49,'ENTRADA',1,1,1,'2026-08-31 22:45:11','Factura 100125-458-fav'),(50,'ENTRADA',1,1,1,'2026-08-31 22:47:51','factura-458-10-mavesa'),(51,'ENTRADA',NULL,1,1,'2026-08-31 22:56:05','factura-4478-14-ret'),(52,'SALIDA',NULL,2,1,'2026-08-31 22:56:51','salida de jabon'),(53,'SALIDA',NULL,2,1,'2026-08-31 22:57:46','7ik7'),(54,'ENTRADA',NULL,1,1,'2026-08-31 23:04:32','factura-5548-25-Fav'),(55,'SALIDA',NULL,2,1,'2026-08-31 23:05:55','salida de jabón'),(56,'SALIDA',NULL,2,1,'2026-08-31 23:14:44','3tg3'),(57,'SALIDA',NULL,2,1,'2026-08-31 23:16:26','sdvfw'),(58,'SALIDA',NULL,2,1,'2026-08-31 23:18:25','8llo'),(59,'SALIDA',NULL,2,1,'2026-08-31 23:22:36','fghh'),(60,'SALIDA',NULL,2,1,'2026-08-31 23:29:10','dh'),(61,'ENTRADA',NULL,2,1,'2026-08-31 23:32:18','gsger'),(62,'SALIDA',NULL,2,1,'2026-08-31 23:47:00','asdsad'),(63,'ENTRADA',NULL,2,1,'2026-08-31 23:48:15','fsdf'),(64,'SALIDA',NULL,2,1,'2026-08-31 23:53:45','yfcyfu'),(65,'SALIDA',NULL,2,1,'2026-09-01 00:02:38','dsvgs'),(66,'SALIDA',NULL,2,1,'2026-09-01 00:15:36','rurtu'),(67,'SALIDA',NULL,2,1,'2026-09-01 00:21:09','78o678'),(68,'SALIDA',NULL,2,1,'2026-09-01 00:24:51','erthyter'),(69,'SALIDA',NULL,2,1,'2026-09-01 00:37:31','t7io'),(70,'ENTRADA',NULL,1,1,'2026-09-01 00:39:29','Factura-1458-215-fav'),(71,'SALIDA',NULL,3,1,'2026-09-01 00:45:46','salida'),(72,'ENTRADA',NULL,1,1,'2026-09-01 00:55:24','grterg'),(73,'ENTRADA',NULL,1,1,'2026-09-01 01:00:27','etrhger'),(74,'ENTRADA',NULL,2,5,'2026-09-01 01:02:22','sdfg'),(75,'SALIDA',NULL,1,5,'2026-09-01 01:03:12','ergegr'),(76,'SALIDA',NULL,1,5,'2026-09-01 01:04:56','dsf'),(77,'ENTRADA',NULL,1,1,'2026-09-01 01:09:17','sdf'),(78,'SALIDA',NULL,2,1,'2026-09-01 01:10:13','eyert'),(79,'SALIDA',NULL,1,5,'2026-09-01 01:30:45','rtytry'),(80,'SALIDA',NULL,2,5,'2026-09-01 01:40:05','ee'),(81,'SALIDA',NULL,2,1,'2026-09-01 01:42:06','yuiuy'),(82,'SALIDA',NULL,2,1,'2026-09-01 01:42:43','sdf'),(83,'SALIDA',NULL,2,1,'2026-09-01 01:46:51','ukhj'),(84,'SALIDA',NULL,2,1,'2026-09-01 01:48:49','eth'),(85,'SALIDA',NULL,1,5,'2026-09-01 08:54:38','ggre'),(86,'SALIDA',NULL,1,5,'2026-09-01 09:06:02','dsvsv'),(87,'SALIDA',NULL,1,5,'2026-09-01 09:19:11','dfvfb'),(88,'TRANSFERENCIA',NULL,1,5,'2026-09-01 09:22:52','cdsfdf'),(89,'TRANSFERENCIA',NULL,1,5,'2026-09-01 09:24:52','dfvfd'),(90,'TRANSFERENCIA',NULL,1,5,'2026-09-01 09:26:23','czdc'),(91,'TRANSFERENCIA',2,1,5,'2026-09-01 09:30:06','sdfdsf'),(92,'TRANSFERENCIA',2,1,5,'2026-09-01 09:31:16','ghnh'),(93,'TRANSFERENCIA',2,1,5,'2026-09-01 09:37:13','dfv'),(94,'TRANSFERENCIA',2,1,5,'2026-09-01 09:41:07','dsfsf'),(95,'TRANSFERENCIA',2,1,5,'2026-09-01 09:42:43','sad'),(96,'TRANSFERENCIA',2,2,5,'2026-09-01 09:45:40','dsfdsf'),(97,'TRANSFERENCIA',2,1,5,'2026-09-01 09:48:09','sfg'),(98,'TRANSFERENCIA',2,1,5,'2026-09-01 09:49:40','o.o,'),(99,'TRANSFERENCIA',2,1,5,'2026-09-01 10:00:01','ffg'),(100,'TRANSFERENCIA',1,2,1,'2026-09-01 10:10:18','dsf'),(101,'TRANSFERENCIA',1,2,1,'2026-09-01 10:14:03','sdgg'),(102,'TRANSFERENCIA',2,1,5,'2026-09-01 10:15:24','sdreg'),(103,'TRANSFERENCIA',2,1,5,'2026-09-01 10:17:41','dfg'),(104,'TRANSFERENCIA',2,1,5,'2026-09-01 10:40:07','dfg'),(105,'TRANSFERENCIA',2,1,5,'2026-09-01 10:51:06','vsfs'),(106,'TRANSFERENCIA',2,1,5,'2026-09-01 10:51:41','adsd'),(107,'TRANSFERENCIA',2,1,5,'2026-09-01 10:56:07','adfdf'),(108,'TRANSFERENCIA',2,1,5,'2026-09-01 10:57:08','sdfs'),(109,'TRANSFERENCIA',1,2,1,'2026-09-01 11:02:03','fdvgdf'),(110,'TRANSFERENCIA',2,1,5,'2026-09-01 11:17:37','fgs'),(111,'ENTRADA',NULL,1,1,'2026-09-01 11:21:17','sdv'),(112,'ENTRADA',NULL,2,5,'2026-09-01 11:31:43','factura-1224587-01-fav'),(113,'TRANSFERENCIA',1,1,1,'2026-09-01 14:15:49','sdfsfs'),(114,'ENTRADA',NULL,2,5,'2026-09-01 14:30:49','entrada'),(115,'ENTRADA',NULL,2,5,'2026-09-01 14:31:31','sfs'),(116,'TRANSFERENCIA',2,1,5,'2026-09-01 14:32:43','btb'),(117,'TRANSFERENCIA',2,1,5,'2026-09-01 14:33:43','dfgg'),(118,'TRANSFERENCIA',1,2,1,'2026-09-01 14:34:46','rthrth'),(119,'ENTRADA',NULL,1,5,'2026-09-01 14:39:15','ttjtj'),(120,'ENTRADA',NULL,1,5,'2026-09-01 14:57:34','sdfrf'),(121,'ENTRADA',NULL,2,5,'2026-09-01 14:59:55','ccsd'),(122,'ENTRADA',NULL,1,5,'2026-09-01 15:00:39','sdcs'),(123,'ENTRADA',NULL,2,5,'2026-09-01 15:01:34','zvv'),(124,'TRANSFERENCIA',2,1,5,'2026-09-01 21:41:51','salida de producto'),(125,'TRANSFERENCIA',2,1,5,'2026-09-01 21:43:43','hrh'),(126,'ENTRADA',NULL,2,5,'2026-09-01 21:47:14','Factura-0001-1458-Far'),(127,'TRANSFERENCIA',2,1,5,'2026-09-01 21:48:14','salida'),(128,'TRANSFERENCIA',1,2,1,'2026-09-01 21:51:02','salida'),(129,'TRANSFERENCIA',2,1,5,'2026-09-01 21:54:27','salidsa'),(130,'TRANSFERENCIA',1,2,1,'2026-09-01 22:07:45','salida');
/*!40000 ALTER TABLE `nota_movimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `estado_producto` varchar(50) DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'15Leche','leche vita','con vitamina','ACTIVO','BUEN ESTADO','2026-08-26 04:44:20'),(2,'17Pan','Pan de trigo','con frutos secos','ACTIVO','BUEN ESTADO','2026-08-26 04:57:38'),(3,'rtgrt','pan','rty','INACTIVO','BUENO','2026-08-26 05:17:53'),(4,'19M','Medias','negras rallas','INACTIVO','MAL ESATDO','2026-08-26 14:06:02'),(5,'asc','sdv','sfsvs','ACTIVO','BUENO ESATDO','2026-08-26 14:06:40'),(6,'6U','uvas','secas','ACTIVO','BUENO ESATDO','2026-08-29 03:19:38'),(7,'1r','bolas','negras','ACTIVO','BUENO ESATDO','2026-08-30 04:46:20'),(8,'8P','paso','negros','ACTIVO','BUENO ESATDO','2026-08-30 06:39:08'),(9,'9y','yoyo','juguete','ACTIVO','BUENO ESATDO','2026-08-30 23:46:25'),(10,'20Q','queso','queso de sal','ACTIVO','BUENO ESATDO','2026-08-31 01:37:25'),(11,'R101','RATON','NEGRO','ACTIVO','BUENO ESATDO','2026-08-31 17:36:41'),(12,'1444','jabón','rexona','ACTIVO','BUENO ESATDO','2026-08-31 23:15:28'),(13,'1441Sh','shampoo','hys','ACTIVO','BUENO ESATDO','2026-09-01 01:31:14'),(14,'8451df','df','prueba','ACTIVO','BUENO ESATDO','2026-09-01 02:21:50'),(15,'1571f','foot','negros','ACTIVO','BUENO ESATDO','2026-09-01 05:45:28'),(16,'d1515f','bombo','negro','ACTIVO','BUENO ESATDO','2026-09-01 14:31:07'),(17,'11245','naranja','chonera','ACTIVO','BUENO ESATDO','2026-09-01 15:56:55'),(18,'158K','Kiw','limpi puertas','ACTIVO','BUENO ESATDO','2026-09-02 02:41:09'),(19,'126N','noni','Limpia ventana','ACTIVO','BUENO ESATDO','2026-09-02 02:42:56');
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombres` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contrasena` varchar(50) NOT NULL,
  `rol` varchar(35) NOT NULL,
  `estado` varchar(20) DEFAULT 'ACTIVO',
  `bodega_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_usuario_bodega` (`bodega_id`),
  CONSTRAINT `fk_usuario_bodega` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Kevin Yapu','kevinyapu@gmail.com','123','Bodeguero ','ACTIVO',1),(2,'Oswaldo Quenguan','oswaldo@gmail.com','123','Administrador','ACTIVO',NULL),(5,'Fatima Loyo','fatima@gmail.com','123','Bodeguero ','ACTIVO',2),(6,'Tupac Vaca','tupac@gmail.com','123','Bodeguero ','ACTIVO',3);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'gestion_inventario'
--
/*!50003 DROP PROCEDURE IF EXISTS `sp_actualizar_producto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_actualizar_producto`(
    IN p_id INT,
    IN p_codigo VARCHAR(50),
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    IN p_stock DECIMAL(10,2), -- Mantenido por firma, ignorado en tabla productos
    IN p_estado_producto VARCHAR(50),
    IN p_lote_id INT
)
BEGIN
    UPDATE productos 
    SET codigo = p_codigo,
        nombre = p_nombre,
        descripcion = p_descripcion,
        estado_producto = p_estado_producto,
        lote_id = p_lote_id
    WHERE id = p_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_bodega` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_bodega`(
    IN p_criterio VARCHAR(100)
)
BEGIN
    SELECT id, nombre, ubicacion, estado 
    FROM bodegas 
    WHERE nombre LIKE CONCAT('%', p_criterio, '%') 
       OR ubicacion LIKE CONCAT('%', p_criterio, '%');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_lote` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_lote`(
    IN p_criterio VARCHAR(100)
)
BEGIN
    SELECT l.id, l.codigo_lote, p.nombre AS producto, b.nombre AS bodega, l.cantidad, l.fecha_vencimiento, l.estado
    FROM lotes l
    INNER JOIN productos p ON l.id_producto = p.id
    INNER JOIN bodegas b ON l.id_bodega = b.id
    WHERE l.codigo_lote LIKE CONCAT('%', p_criterio, '%')
       OR p.nombre LIKE CONCAT('%', p_criterio, '%');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_lotes` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_lotes`(
    IN p_criterio VARCHAR(50)
)
BEGIN
    SELECT 
        l.id,
        l.numero_lote,
        p.id AS producto_id,
        IFNULL(p.nombre, 'Sin asignar') AS nombre_producto,
        b.nombre AS nombre_bodega,
        l.fecha_vencimiento,
        l.estado
    FROM lotes l
    LEFT JOIN productos p ON p.lote_id = l.id
    INNER JOIN bodegas b ON l.bodega_id = b.id
    WHERE l.numero_lote LIKE CONCAT('%', p_criterio, '%');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_producto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_producto`(IN p_criterio VARCHAR(100))
BEGIN
    SELECT 
        p.id, 
        p.codigo, 
        p.nombre, 
        p.descripcion, 
        p.stock, 
        p.estado, 
        p.estado_producto, 
        IFNULL(l.numero_lote, 'Sin Lote') AS lote
    FROM productos p
    LEFT JOIN lotes l ON p.lote_id = l.id
    WHERE (p.codigo LIKE CONCAT('%', p_criterio, '%') 
       OR p.nombre LIKE CONCAT('%', p_criterio, '%'));
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_deshabilitar_bodega` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_deshabilitar_bodega`(
    IN p_id INT
)
BEGIN
    UPDATE bodegas 
    SET estado = 'INACTIVO' 
    WHERE id = p_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_deshabilitar_lote` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_deshabilitar_lote`(
    IN p_numero_lote VARCHAR(50)
)
BEGIN
    UPDATE lotes 
    SET estado = 'INACTIVO' 
    WHERE numero_lote = p_numero_lote;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_deshabilitar_producto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_deshabilitar_producto`(
    IN p_id INT
)
BEGIN
    UPDATE productos 
    SET estado = 'INACTIVO'
    WHERE id = p_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_editar_bodega` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_editar_bodega`(
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_ubicacion VARCHAR(150)
)
BEGIN
    UPDATE bodegas 
    SET nombre = p_nombre, 
        ubicacion = p_ubicacion 
    WHERE id = p_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_editar_lote` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_editar_lote`(
    IN p_id INT,
    IN p_numero_lote VARCHAR(50),
    IN p_bodega_id INT,
    IN p_fecha_vencimiento DATE
)
BEGIN
    UPDATE lotes 
    SET numero_lote = p_numero_lote,
        bodega_id = p_bodega_id,
        fecha_vencimiento = p_fecha_vencimiento
    WHERE id = p_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_editar_producto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_editar_producto`(
    IN p_codigo VARCHAR(50),
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(255),
    IN p_stock DECIMAL(10,2)
)
BEGIN
    UPDATE productos 
    SET nombre = p_nombre, 
        descripcion = p_descripcion, 
        stock = p_stock 
    WHERE codigo = p_codigo;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_ingresar_bodega` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ingresar_bodega`(
    IN p_nombre VARCHAR(100),
    IN p_ubicacion VARCHAR(150)
)
BEGIN
    INSERT INTO bodegas (nombre, ubicacion, estado) 
    VALUES (p_nombre, p_ubicacion, 'ACTIVO');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_ingresar_lote` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ingresar_lote`(
    IN p_numero_lote VARCHAR(50),
    IN p_producto_id INT,
    IN p_bodega_id INT,
    IN p_fecha_vencimiento DATE
)
BEGIN
    INSERT INTO lotes (numero_lote, producto_id, bodega_id, fecha_vencimiento, estado)
    VALUES (p_numero_lote, p_producto_id, p_bodega_id, p_fecha_vencimiento, 'ACTIVO');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_ingresar_producto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ingresar_producto`(
    IN p_codigo VARCHAR(50),
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    IN p_stock DECIMAL(10,2),
    IN p_estado_producto VARCHAR(50),
    IN p_lote_id INT
)
BEGIN
    DECLARE v_producto_id INT;

    -- 1. Crear el producto
    INSERT INTO productos (codigo, nombre, descripcion, estado_producto, estado)
    VALUES (p_codigo, p_nombre, p_descripcion, p_estado_producto, 'ACTIVO');

    SET v_producto_id = LAST_INSERT_ID();

    -- 2. Vincular el lote y stock inicial en la bodega principal (ID 1)
    IF p_lote_id IS NOT NULL AND p_lote_id > 0 AND p_stock > 0 THEN
        INSERT INTO inventario_bodega (bodega_id, lote_id, producto_id, stock)
        VALUES (1, p_lote_id, v_producto_id, p_stock)
        ON DUPLICATE KEY UPDATE stock = stock + p_stock;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_insertar_lote` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insertar_lote`(
    IN p_numero_lote VARCHAR(50),
    IN p_bodega_id INT,
    IN p_fecha_vencimiento DATE
)
BEGIN
    INSERT INTO lotes (numero_lote, bodega_id, fecha_vencimiento, estado)
    VALUES (p_numero_lote, p_bodega_id, p_fecha_vencimiento, 'ACTIVO');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_insertar_movimiento` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insertar_movimiento`(
    IN p_tipo_movimiento VARCHAR(50),
    IN p_bodega_origen_id INT,
    IN p_bodega_destino_id INT,
    IN p_responsable_id INT,
    IN p_observacion TEXT,
    OUT p_id_generado INT
)
BEGIN
    -- Inserción del registro en la tabla de notas de movimiento
    INSERT INTO nota_movimiento (
        tipo_movimiento,
        bodega_origen_id,
        bodega_destino_id,
        responsable_id,
        fecha_movimiento,
        observacion
    ) VALUES (
        p_tipo_movimiento,
        p_bodega_origen_id,
        p_bodega_destino_id,
        p_responsable_id,
        NOW(), -- Asigna la fecha y hora actual automáticamente
        p_observacion
    );

    -- Captura el ID recién generado para retornarlo a Java
    SET p_id_generado = LAST_INSERT_ID();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_bodegas` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_bodegas`()
BEGIN
    SELECT id, nombre, ubicacion, estado 
    FROM bodegas;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_lotes` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_lotes`()
BEGIN
    SELECT 
        l.id,
        l.numero_lote,
        p.nombre AS producto,
        b.nombre AS bodega,
        l.fecha_vencimiento,
        l.estado
    FROM lotes l
    INNER JOIN productos p ON l.producto_id = p.id
    INNER JOIN bodegas b ON l.bodega_id = b.id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_productos` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_productos`()
BEGIN
    SELECT 
        p.id, 
        p.codigo, 
        p.nombre, 
        p.descripcion, 
        p.stock, 
        p.estado, 
        p.estado_producto, 
        COALESCE(l.numero_lote, 'Sin Lote') AS lote
    FROM productos p
    LEFT JOIN lotes l ON p.lote_id = l.id  -- <--- Cambiado id_lote por lote_id
    ORDER BY p.id DESC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_lotes` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_lotes`()
BEGIN
    SELECT 
        l.id,
        l.numero_lote,
        p.id AS producto_id,
        IFNULL(p.nombre, 'Sin asignar') AS nombre_producto,
        b.nombre AS nombre_bodega,
        l.fecha_vencimiento,
        l.estado
    FROM lotes l
    LEFT JOIN productos p ON p.lote_id = l.id
    INNER JOIN bodegas b ON l.bodega_id = b.id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_reportes` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_reportes`(
    IN p_fInicio VARCHAR(10),
    IN p_fFin VARCHAR(10),
    IN p_actor VARCHAR(100),
    IN p_tipoMov VARCHAR(50)
)
BEGIN
    SELECT 
        DATE(nm.fecha_movimiento) AS fecha,
        u.rol AS responsable,
        p.nombre AS producto,
        dm.cantidad AS cantidad,
        -- Transforma 'TRANSFERENCIA' en 'SALIDA' para la vista
        CASE 
            WHEN UPPER(nm.tipo_movimiento) = 'TRANSFERENCIA' THEN 'SALIDA'
            ELSE UPPER(nm.tipo_movimiento)
        END AS tipo_movimiento
    FROM nota_movimiento nm
    INNER JOIN detalle_movimiento dm ON nm.id = dm.nota_movimiento_id
    INNER JOIN productos p ON dm.producto_id = p.id
    INNER JOIN usuarios u ON nm.responsable_id = u.id
    LEFT JOIN bodegas b ON nm.bodega_destino_id = b.id
    LEFT JOIN lotes l ON dm.lote_id = l.id
    WHERE (p_fInicio = '' OR DATE(nm.fecha_movimiento) >= p_fInicio)
      AND (p_fFin = '' OR DATE(nm.fecha_movimiento) <= p_fFin)
      AND (p_actor = '' OR p_actor = 'TODOS' OR LOWER(u.rol) LIKE CONCAT('%', LOWER(p_actor), '%'))
      -- Filtro de tipo movimiento adaptado al nuevo contexto
      AND (
          p_tipoMov = '' 
          OR p_tipoMov = 'TODOS' 
          OR (
              LOWER(p_tipoMov) = 'salida' 
              AND LOWER(nm.tipo_movimiento) IN ('salida', 'transferencia')
          )
          OR LOWER(nm.tipo_movimiento) = LOWER(p_tipoMov)
      );
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_registrar_detalle_movimiento` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_registrar_detalle_movimiento`(
    IN p_nota_id INT,
    IN p_lote_id INT,
    IN p_producto_id INT,
    IN p_cantidad DECIMAL(10,2),
    IN p_tipo_movimiento VARCHAR(50)
)
PROC_BODY: BEGIN
    DECLARE v_bodega_origen INT;
    DECLARE v_bodega_destino INT;
    DECLARE v_stock_actual DECIMAL(10,2) DEFAULT 0;

    -- Revertir transacción si ocurre un error SQL
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 1. Obtener bodegas asociadas a la nota
    SELECT bodega_origen_id, bodega_destino_id 
    INTO v_bodega_origen, v_bodega_destino
    FROM nota_movimiento 
    WHERE id = p_nota_id;

    -- 2. Validar stock en la bodega de origen si es una transferencia/salida
    IF v_bodega_origen IS NOT NULL THEN
        SELECT COALESCE(stock, 0) INTO v_stock_actual
        FROM inventario_bodega
        WHERE bodega_id = v_bodega_origen 
          AND lote_id = p_lote_id 
          AND producto_id = p_producto_id;

        IF v_stock_actual < p_cantidad THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Stock insuficiente en la bodega de origen para realizar la transferencia.';
        END IF;
    END IF;

    -- 3. Registrar el detalle
    INSERT INTO detalle_movimiento (nota_movimiento_id, lote_id, producto_id, cantidad)
    VALUES (p_nota_id, p_lote_id, p_producto_id, p_cantidad);

    -- 4. RESTAR stock de la Bodega Origen
    IF v_bodega_origen IS NOT NULL THEN
        UPDATE inventario_bodega 
        SET stock = stock - p_cantidad 
        WHERE bodega_id = v_bodega_origen 
          AND lote_id = p_lote_id 
          AND producto_id = p_producto_id;
    END IF;

    -- 5. SUMAR stock en la Bodega Destino
    IF v_bodega_destino IS NOT NULL THEN
        IF EXISTS (SELECT 1 FROM inventario_bodega WHERE bodega_id = v_bodega_destino AND lote_id = p_lote_id AND producto_id = p_producto_id) THEN
            UPDATE inventario_bodega 
            SET stock = stock + p_cantidad 
            WHERE bodega_id = v_bodega_destino 
              AND lote_id = p_lote_id 
              AND producto_id = p_producto_id;
        ELSE
            INSERT INTO inventario_bodega (bodega_id, lote_id, producto_id, stock)
            VALUES (v_bodega_destino, p_lote_id, p_producto_id, p_cantidad);
        END IF;
    END IF;

    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_registrar_detalle_y_actualizar_stock` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_registrar_detalle_y_actualizar_stock`(
    IN p_nota_id INT,
    IN p_numero_lote VARCHAR(50),
    IN p_producto_id INT,
    IN p_cantidad INT,
    OUT p_resultado INT
)
sp_main: BEGIN
    DECLARE v_tipo_movimiento VARCHAR(20);
    DECLARE v_bodega_origen INT;
    DECLARE v_bodega_destino INT;
    DECLARE v_lote_id INT;
    DECLARE v_stock_actual DECIMAL(10,2) DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        SET p_resultado = -1;
    END;

    -- 1. Obtener ID del Lote
    SELECT id INTO v_lote_id FROM lotes WHERE numero_lote = p_numero_lote LIMIT 1;

    -- 2. Obtener datos de la Nota
    SELECT tipo_movimiento, bodega_origen_id, bodega_destino_id 
    INTO v_tipo_movimiento, v_bodega_origen, v_bodega_destino 
    FROM nota_movimiento WHERE id = p_nota_id;

    START TRANSACTION;

    -- 3. Manejo de SALIDA (Descuenta en Origen y Transfiere a Destino)
    IF v_tipo_movimiento = 'SALIDA' THEN
        SELECT stock INTO v_stock_actual 
        FROM inventario_bodega 
        WHERE bodega_id = v_bodega_origen AND lote_id = v_lote_id AND producto_id = p_producto_id;

        -- Validar stock suficiente en origen
        IF v_stock_actual IS NULL OR v_stock_actual < p_cantidad THEN
            ROLLBACK;
            SET p_resultado = 0;
            LEAVE sp_main;
        END IF;

        -- Restar del inventario origen
        UPDATE inventario_bodega 
        SET stock = stock - p_cantidad 
        WHERE bodega_id = v_bodega_origen AND lote_id = v_lote_id AND producto_id = p_producto_id;

        -- Sumar/Crear registro en la bodega de destino (si existe bodega_destino)
        IF v_bodega_destino IS NOT NULL AND v_bodega_destino > 0 THEN
            INSERT INTO inventario_bodega (bodega_id, lote_id, producto_id, stock)
            VALUES (v_bodega_destino, v_lote_id, p_producto_id, p_cantidad)
            ON DUPLICATE KEY UPDATE stock = stock + p_cantidad;
        END IF;

    -- 4. Manejo de ENTRADA (Carga directa al destino)
    ELSEIF v_tipo_movimiento = 'ENTRADA' THEN
        INSERT INTO inventario_bodega (bodega_id, lote_id, producto_id, stock)
        VALUES (v_bodega_destino, v_lote_id, p_producto_id, p_cantidad)
        ON DUPLICATE KEY UPDATE stock = stock + p_cantidad;
    END IF;

    -- 5. Registrar el detalle
    INSERT INTO detalle_movimiento (nota_movimiento_id, lote_id, producto_id, cantidad) 
    VALUES (p_nota_id, v_lote_id, p_producto_id, p_cantidad);

    COMMIT;
    SET p_resultado = 1;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_validar_login` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_validar_login`(
    IN p_email VARCHAR(100),
    IN p_contrasena VARCHAR(100)
)
BEGIN
    SELECT id, nombres, email, contrasena, rol 
    FROM usuarios 
    WHERE TRIM(email) = TRIM(p_email) 
      AND TRIM(contrasena) = TRIM(p_contrasena);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-01 22:50:54
