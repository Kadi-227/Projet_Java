-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: unilib_db
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `alerte`
--

DROP TABLE IF EXISTS `alerte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alerte` (
  `id_alerte` int NOT NULL AUTO_INCREMENT,
  `id_emprunt` int DEFAULT NULL,
  `cne_etudiant` varchar(20) DEFAULT NULL,
  `login_admin` varchar(50) DEFAULT NULL,
  `type_alerte` varchar(50) DEFAULT NULL,
  `contenu` text,
  PRIMARY KEY (`id_alerte`),
  KEY `id_emprunt` (`id_emprunt`),
  KEY `cne_etudiant` (`cne_etudiant`),
  KEY `login_admin` (`login_admin`),
  CONSTRAINT `alerte_ibfk_1` FOREIGN KEY (`id_emprunt`) REFERENCES `emprunt` (`id_emprunt`),
  CONSTRAINT `alerte_ibfk_2` FOREIGN KEY (`cne_etudiant`) REFERENCES `etudiant` (`cne`),
  CONSTRAINT `alerte_ibfk_3` FOREIGN KEY (`login_admin`) REFERENCES `utilisateur` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alerte`
--

LOCK TABLES `alerte` WRITE;
/*!40000 ALTER TABLE `alerte` DISABLE KEYS */;
/*!40000 ALTER TABLE `alerte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `avis`
--

DROP TABLE IF EXISTS `avis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avis` (
  `id_avis` int NOT NULL AUTO_INCREMENT,
  `cin` varchar(20) DEFAULT NULL,
  `num_inventaire_document` varchar(50) DEFAULT NULL,
  `date_avis` date DEFAULT NULL,
  `contenu` text,
  `note` int DEFAULT NULL,
  PRIMARY KEY (`id_avis`),
  KEY `cin` (`cin`),
  KEY `num_inventaire_document` (`num_inventaire_document`),
  CONSTRAINT `avis_ibfk_2` FOREIGN KEY (`num_inventaire_document`) REFERENCES `document` (`num_inventaire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avis`
--

LOCK TABLES `avis` WRITE;
/*!40000 ALTER TABLE `avis` DISABLE KEYS */;
/*!40000 ALTER TABLE `avis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `demande`
--

DROP TABLE IF EXISTS `demande`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demande` (
  `id_demande` int NOT NULL AUTO_INCREMENT,
  `type_demande` enum('Inscription','Reservation','Validée') NOT NULL,
  `num_inventaire_document` varchar(50) DEFAULT NULL,
  `nb_messages_non_lus` int DEFAULT '0',
  PRIMARY KEY (`id_demande`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `demande`
--

LOCK TABLES `demande` WRITE;
/*!40000 ALTER TABLE `demande` DISABLE KEYS */;
/*!40000 ALTER TABLE `demande` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `num_inventaire` varchar(50) NOT NULL,
  `type_document` enum('Livre','Rapport Licence','Memoire Master','These Doctorat') NOT NULL,
  `titre` varchar(255) NOT NULL,
  `auteur` varchar(255) NOT NULL,
  `nbr_exemplaires` int DEFAULT NULL,
  `edition` varchar(50) DEFAULT NULL,
  `cote` varchar(50) DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `annee` int DEFAULT NULL,
  `encadrant` varchar(255) DEFAULT NULL,
  `sujet` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`num_inventaire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document`
--

LOCK TABLES `document` WRITE;
/*!40000 ALTER TABLE `document` DISABLE KEYS */;
INSERT INTO `document`  VALUES 
/*Thèses Doctorat*/
('THS001', 'These Doctorat', 'Thèse sur IA', 'Dr. Karim', 2, '1ère', 'T001', 'IA', 'img1.jpg', 2022, 'Prof. Ait', 'Apprentissage automatique'),
('THS002', 'These Doctorat', 'Thèse en biologie', 'Dr. Selma', 1, '2ème', 'T002', 'Biologie', 'img2.jpg', 2023, 'Prof. Bakkali', 'Cellules souches'),
('THS003', 'These Doctorat', 'Thèse en droit', 'Dr. Farah', 1, '3ème', 'T003', 'Droit', 'img3.jpg', 2021, 'Dr. Lamrani', 'Constitution'),
('THS004', 'These Doctorat', 'Thèse en économie', 'Dr. Yassine', 2, '2ème', 'T004', 'Économie', 'img4.jpg', 2020, 'Prof. Sami', 'Macroéconomie'),
('THS005', 'These Doctorat', 'Thèse en robotique', 'Dr. Amine', 2, '1ère', 'T005', 'Robotique', 'img5.jpg', 2024, 'Mme. Kamilia', 'Vision par ordinateur'),
('THS006', 'These Doctorat', 'Thèse sur les réseaux', 'Dr. Ines', 1, '1ère', 'T006', 'Informatique', 'img6.jpg', 2023, 'Prof. Aziz', 'Sécurité'),
('THS007', 'These Doctorat', 'Thèse en chimie', 'Dr. Rachid', 1, '4ème', 'T007', 'Chimie', 'img7.jpg', 2022, 'Mme. Nawal', 'Réactions organiques'),
('THS008', 'These Doctorat', 'Thèse en mathématiques', 'Dr. Salma', 2, '2ème', 'T008', 'Maths', 'img8.jpg', 2023, 'M. Idriss', 'Topologie'),
('THS009', 'These Doctorat', 'Thèse en physique', 'Dr. Walid', 1, '3ème', 'T009', 'Physique', 'img9.jpg', 2022, 'Dr. Jihane', 'Relativité'),
('THS010', 'These Doctorat', 'Thèse en géologie', 'Dr. Nizar', 2, '1ère', 'T010', 'Terre', 'img10.jpg', 2021, 'Dr. Omar', 'Sédiments'),
-- Rapports Licence
('RPL001','Rapport Licence','Rapport1','x',2,'2','123',`categorie`,`image`,2021,'y',`sujet`),
('RPL002','Rapport Licence','Rapport2','x',3,'1','123',`categorie`,`image`,2023,'y',`sujet`),
('RPL003','Rapport Licence','Rapport3','x',5,'3','123',`categorie`,`image`,2004,'y',`sujet`),
('RPL004','Rapport Licence','Rapport4','x',1,'6','123',`categorie`,`image`,2007,'y',`sujet`),
('RPL005', 'Rapport Licence', 'Rapport BDD', 'Alice', 2, '3ème', 'RL001', 'Informatique', 'img11.jpg', 2020, 'M. Faouzi', 'MySQL'),
('RPL006', 'Rapport Licence', 'Rapport Réseaux', 'Bob', 3, '2ème', 'RL002', 'Informatique', 'img12.jpg', 2021, 'Mme. Kenza', 'Protocoles réseau'),
('RPL007', 'Rapport Licence', 'Rapport en Java', 'Clara', 1, '1ère', 'RL003', 'Programmation', 'img13.jpg', 2023, 'Dr. Ali', 'POO'),
('RPL008', 'Rapport Licence', 'Rapport en C++', 'Youssef', 2, '2ème', 'RL004', 'Dev', 'img14.jpg', 2022, 'Mme. Imane', 'Pointeurs'),
('RPL009', 'Rapport Licence', 'Rapport Web', 'Leila', 1, '1ère', 'RL005', 'Web', 'img15.jpg', 2021, 'Dr. Zineb', 'HTML/CSS'),
('RPL010', 'Rapport Licence', 'Rapport Sécurité', 'Khalid', 3, '4ème', 'RL006', 'Réseau', 'img16.jpg', 2022, 'M. Karim', 'Firewall'),
-- Livres
('INV003','Livre','livre1','x',4,'1','123',`categorie`,`image`,2021,null,`sujet`),
('INV001','Livre','livre2','x',1,'1','123',`categorie`,`image`,2021,null,`sujet`),
('INV002','Livre','livre3','x',3,'3','123',`categorie`,`image`,2021,null,`sujet`),
('INV004','Livre','livre4','x',5,'2','123','categorie','image',2025,null,'sujet'),
('INV005', 'Livre', 'Biologie Générale', 'Dr. Laila', 2, '1ère', 'LV001', 'SVT', 'img21.jpg', 2019, NULL, NULL),
('INV006', 'Livre', 'Littérature Française', 'Jean V.', 3, '4ème', 'LV002', 'Littérature', 'img22.jpg', 2018, NULL, NULL),
('INV007', 'Livre', 'Physique Mécanique', 'Prof. Omar', 4, '2ème', 'LV003', 'Physique', 'img23.jpg', 2020, NULL, NULL),
('INV008', 'Livre', 'Chimie Organique', 'Dr. Amina', 5, '1ère', 'LV004', 'Chimie', 'img24.jpg', 2021, NULL, NULL),
('INV009', 'Livre', 'Mathématiques Appliquées', 'M. Yassir', 3, '3ème', 'LV005', 'Maths', 'img25.jpg', 2023, NULL, NULL),
('INV010', 'Livre', 'Histoire de France', 'Jules M.', 2, '1ère', 'LV006', 'Histoire', 'img26.jpg', 2022, NULL, NULL),
-- Mémoires Master
('MEM001', 'Memoire Master', 'Deep Learning', 'Ahmed', 1, '1ère', 'MM001', 'IA', 'img31.jpg', 2024, 'Prof. Ouali', 'Réseaux'),
('MEM002', 'Memoire Master', 'Traitement du Signal', 'Sofia', 2, '2ème', 'MM002', 'Télécom', 'img32.jpg', 2022, 'Dr. Mounir', 'Filtrage'),
('MEM003', 'Memoire Master', 'Vision par Ordinateur', 'Ilyass', 1, '1ère', 'MM003', 'IA', 'img33.jpg', 2023, 'Mme. Sanae', 'Détection'),
('MEM004', 'Memoire Master', 'Cloud Computing', 'Yasmine', 2, '3ème', 'MM004', 'Cloud', 'img34.jpg', 2021, 'Dr. Reda', 'Stockage'),
('MEM005', 'Memoire Master', 'IoT et capteurs', 'Tarik', 3, '1ère', 'MM005', 'Électronique', 'img35.jpg', 2022, 'Dr. Kamal', 'Réseaux de capteurs'),
('MEM006', 'Memoire Master', 'NLP', 'Amal', 2, '2ème', 'MM006', 'IA', 'img36.jpg', 2023, 'Mme. Rania', 'Texte'),
('MEM007', 'Memoire Master', 'Blockchain', 'Rania', 1, '1ère', 'MM007', 'Sécurité', 'img37.jpg', 2024, 'Prof. Sami', 'Cryptographie'),
('MEM008', 'Memoire Master', 'Big Data', 'Hamza', 3, '3ème', 'MM008', 'Data', 'img38.jpg', 2022, 'Dr. Adil', 'Analyse'),
('MEM009', 'Memoire Master', 'Réalité Virtuelle', 'Hind', 2, '2ème', 'MM009', 'Informatique', 'img39.jpg', 2021, 'Dr. Hicham', '3D'),
('MEM010', 'Memoire Master', 'Sécurité Réseau', 'Nabil', 2, '4ème', 'MM010', 'Réseau', 'img40.jpg', 2023, 'Mme. Ilham', 'Attaques');
/*!40000 ALTER TABLE `document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emprunt`
--

DROP TABLE IF EXISTS `emprunt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emprunt` (
  `id_emprunt` int NOT NULL AUTO_INCREMENT,
  `cin` varchar(20) DEFAULT NULL,
  `num_inventaire_document` varchar(50) DEFAULT NULL,
  `date_emprunt` date DEFAULT NULL,
  `duree_prevue` int DEFAULT NULL,
  `date_retour_prevue` date DEFAULT NULL,
  `date_retour_effective` date DEFAULT NULL,
  PRIMARY KEY (`id_emprunt`),
  KEY `cin` (`cin`),
  KEY `num_inventaire_document` (`num_inventaire_document`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emprunt`
--

LOCK TABLES `emprunt` WRITE;
/*!40000 ALTER TABLE `emprunt` DISABLE KEYS */;
/*!40000 ALTER TABLE `emprunt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `etudiant`
--

DROP TABLE IF EXISTS `etudiant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `etudiant` (
  `cne` varchar(20) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `niveau_etudes` enum('Licence','Master','Doctorat') DEFAULT NULL,
  `id_filiere` int DEFAULT NULL,
  `id_niveau` int DEFAULT NULL,
  `cin_utilisateur` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`cne`),
  UNIQUE KEY `cin_utilisateur` (`cin_utilisateur`),
  UNIQUE KEY `email` (`email`),
  KEY `id_filiere` (`id_filiere`),
  KEY `id_niveau` (`id_niveau`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `etudiant`
--

LOCK TABLES `etudiant` WRITE;
/*!40000 ALTER TABLE `etudiant` DISABLE KEYS */;
INSERT INTO `etudiant` VALUES ('11223','Durand','Pierre',NULL,'Doctorat',1,3,'AA1234'),('12345','Dupont','Jean',NULL,'Licence',1,1,'BB5678'),('67890','Martin','Marie',NULL,'Master',2,2,'CC9012');
/*!40000 ALTER TABLE `etudiant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `filiere`
--

DROP TABLE IF EXISTS `filiere`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `filiere` (
  `id_filiere` int NOT NULL AUTO_INCREMENT,
  `nom_filiere` varchar(100) NOT NULL,
  PRIMARY KEY (`id_filiere`),
  UNIQUE KEY `nom_filiere` (`nom_filiere`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `filiere`
--

LOCK TABLES `filiere` WRITE;
/*!40000 ALTER TABLE `filiere` DISABLE KEYS */;
INSERT INTO `filiere` VALUES (1, 'Mathématiques'),
(2, 'Informatique'),
(3, 'Physique'),
(4, 'Chimie'),
(5, 'Géologie'),
(6, 'Biologie'),
(7, 'Enseignements transversaux');
/*!40000 ALTER TABLE `filiere` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `livre`
--

DROP TABLE IF EXISTS `livre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `livre` (
  `id_livre` int NOT NULL AUTO_INCREMENT,
  `nbr_exemplaires` int DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL,
  `cote` varchar(50) DEFAULT NULL,
  `num_inventaire` varchar(50) NOT NULL,
  PRIMARY KEY (`id_livre`),
  UNIQUE KEY `num_inventaire` (`num_inventaire`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `livre`
--

LOCK TABLES `livre` WRITE;
/*!40000 ALTER TABLE `livre` DISABLE KEYS */;
INSERT INTO `livre` VALUES (1,1,'categorie','123','INV001'),(2,3,'categorie','123','INV002'),
(3,4,'categorie','123','INV003'),(4,5,'categorie','123','INV004'),
(5, 2, 'SVT', 'LV001', 'INV005'), (6, 3, 'Littérature', 'LV002', 'INV006'),
(7, 4, 'Physique', 'LV003', 'INV007'), (8, 5, 'Chimie', 'LV004', 'INV008'),
(9, 3, 'Maths', 'LV005', 'INV009'), (10, 2, 'Histoire', 'LV006', 'INV010');
/*!40000 ALTER TABLE `livre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memoire_master`
--

DROP TABLE IF EXISTS `memoire_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `memoire_master` (
  `id_memoire` int NOT NULL AUTO_INCREMENT,
  `num_inventaire` varchar(50) NOT NULL,
  `nbr_exemplaires` int DEFAULT NULL,
  PRIMARY KEY (`id_memoire`),
  UNIQUE KEY `num_inventaire` (`num_inventaire`),
  CONSTRAINT `memoire_master_ibfk_1` FOREIGN KEY (`num_inventaire`) REFERENCES `document` (`num_inventaire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memoire_master`
--

LOCK TABLES `memoire_master` WRITE;
/*!40000 ALTER TABLE `memoire_master` DISABLE KEYS */;
INSERT INTO `memoire_master` VALUES
(1, 'MEM001', 1), (2, 'MEM002', 2), (3, 'MEM003', 1), (4, 'MEM004', 2), (5, 'MEM005', 3),
(6, 'MEM006', 2), (7, 'MEM007', 1), (8, 'MEM008', 3), (9, 'MEM009', 2), (10, 'MEM010', 2);
/*!40000 ALTER TABLE `memoire_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id_message` int NOT NULL AUTO_INCREMENT,
  `cne_etudiant` varchar(20) DEFAULT NULL,
  `login_admin` varchar(50) DEFAULT NULL,
  `contenu` text,
  `reponse` enum('OK','Refuser') DEFAULT NULL,
  `id_demande` int DEFAULT NULL,
  PRIMARY KEY (`id_message`),
  KEY `cne_etudiant` (`cne_etudiant`),
  KEY `login_admin` (`login_admin`),
  KEY `id_demande` (`id_demande`),
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`cne_etudiant`) REFERENCES `etudiant` (`cne`),
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`login_admin`) REFERENCES `utilisateur` (`login`),
  CONSTRAINT `message_ibfk_3` FOREIGN KEY (`id_demande`) REFERENCES `demande` (`id_demande`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `niveau_etudes`
--

DROP TABLE IF EXISTS `niveau_etudes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `niveau_etudes` (
  `id_niveau` int NOT NULL AUTO_INCREMENT,
  `nom_niveau` enum('Licence','Master','Doctorat') NOT NULL,
  PRIMARY KEY (`id_niveau`),
  UNIQUE KEY `nom_niveau` (`nom_niveau`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `niveau_etudes`
--

LOCK TABLES `niveau_etudes` WRITE;
/*!40000 ALTER TABLE `niveau_etudes` DISABLE KEYS */;
INSERT INTO `niveau_etudes` VALUES (1,'Licence'),(2,'Master'),(3,'Doctorat');
/*!40000 ALTER TABLE `niveau_etudes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `niveau_filiere`
--

DROP TABLE IF EXISTS `niveau_filiere`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `niveau_filiere` (
  `id_niveau` int NOT NULL,
  `id_filiere` int NOT NULL,
  PRIMARY KEY (`id_niveau`,`id_filiere`),
  KEY `id_filiere` (`id_filiere`),
  CONSTRAINT `niveau_filiere_ibfk_1` FOREIGN KEY (`id_niveau`) REFERENCES `niveau_etudes` (`id_niveau`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `niveau_filiere`
--

LOCK TABLES `niveau_filiere` WRITE;
/*!40000 ALTER TABLE `niveau_filiere` DISABLE KEYS */;
/*!40000 ALTER TABLE `niveau_filiere` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `penalite`
--

DROP TABLE IF EXISTS `penalite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `penalite` (
  `id_penalite` int NOT NULL AUTO_INCREMENT,
  `id_emprunt` int DEFAULT NULL,
  `date_penalite` date DEFAULT NULL,
  `type_penalite` varchar(100) DEFAULT NULL,
  `montant` decimal(10,2) DEFAULT NULL,
  `statut` varchar(50) DEFAULT NULL,
  `demande_excuse` text,
  `decision_demande` enum('acceptée','refusée','en cours') DEFAULT NULL,
  PRIMARY KEY (`id_penalite`),
  KEY `id_emprunt` (`id_emprunt`),
  CONSTRAINT `penalite_ibfk_1` FOREIGN KEY (`id_emprunt`) REFERENCES `emprunt` (`id_emprunt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `penalite`
--

LOCK TABLES `penalite` WRITE;
/*!40000 ALTER TABLE `penalite` DISABLE KEYS */;
/*!40000 ALTER TABLE `penalite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rapport_licence`
--

DROP TABLE IF EXISTS `rapport_licence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rapport_licence` (
  `id_rapport` int NOT NULL AUTO_INCREMENT,
  `num_inventaire` varchar(50) NOT NULL,
  `nbr_exemplaires` int DEFAULT NULL,
  PRIMARY KEY (`id_rapport`),
  UNIQUE KEY `num_inventaire` (`num_inventaire`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rapport_licence`
--

LOCK TABLES `rapport_licence` WRITE;
/*!40000 ALTER TABLE `rapport_licence` DISABLE KEYS */;
INSERT INTO `rapport_licence` VALUES (1,'RPL001',2);
INSERT INTO `rapport_licence` VALUES (2,'RPL002',3);
INSERT INTO `rapport_licence` VALUES (3,'RPL003',5);
INSERT INTO `rapport_licence` VALUES (4,'RPL004',1);
-- Rapports Licence
INSERT INTO `rapport_licence` VALUES
(5, 'RPL005', 2), (6, 'RPL006', 3), (7, 'RPL007', 1), (8, 'RPL008', 2), (9, 'RPL009', 1),(10, 'RPL010', 3);
/*!40000 ALTER TABLE `rapport_licence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id_reservation` int NOT NULL AUTO_INCREMENT,
  `cne_etudiant` varchar(20) DEFAULT NULL,
  `num_inventaire_document` varchar(50) DEFAULT NULL,
  `date_reservation` date DEFAULT NULL,
  `date_limite_reservation` date DEFAULT NULL,
  `statut_reservation` enum('en cours','expirée','confirmée','annulée') DEFAULT 'en cours',
  PRIMARY KEY (`id_reservation`),
  KEY `cne_etudiant` (`cne_etudiant`),
  KEY `num_inventaire_document` (`num_inventaire_document`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`cne_etudiant`) REFERENCES `etudiant` (`cne`),
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`num_inventaire_document`) REFERENCES `document` (`num_inventaire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `these_doctorat`
--

DROP TABLE IF EXISTS `these_doctorat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `these_doctorat` (
  `id_these` int NOT NULL AUTO_INCREMENT,
  `num_inventaire` varchar(50) NOT NULL,
  `nbr_exemplaires` int DEFAULT NULL,
  PRIMARY KEY (`id_these`),
  UNIQUE KEY `num_inventaire` (`num_inventaire`),
  CONSTRAINT `these_doctorat_ibfk_1` FOREIGN KEY (`num_inventaire`) REFERENCES `document` (`num_inventaire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `these_doctorat`
--

LOCK TABLES `these_doctorat` WRITE;
/*!40000 ALTER TABLE `these_doctorat` DISABLE KEYS */;
-- Thèses
INSERT INTO `these_doctorat` VALUES
(1, 'THS001', 2), (2, 'THS002', 1), (3, 'THS003', 1), (4, 'THS004', 2), (5, 'THS005', 2),
(6, 'THS006', 1), (7, 'THS007', 1), (8, 'THS008', 2), (9, 'THS009', 1), (10, 'THS010', 2);
/*!40000 ALTER TABLE `these_doctorat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilisateur` (
  `cin` varchar(20) NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','etudiant') NOT NULL,
  `statut` enum('actif','bloque') NOT NULL DEFAULT 'actif',
  `date_bloque` date DEFAULT NULL,
  `date_dernier_changement_password` date DEFAULT NULL,
  PRIMARY KEY (`cin`),
  UNIQUE KEY `cin` (`cin`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES ('M123','DurandPierre','123','etudiant','actif',NULL,NULL);
INSERT INTO `utilisateur` VALUES ('K876','DupontJean','123','etudiant','bloque',2025-05-06,NULL);
INSERT INTO `utilisateur` VALUES ('D127839','MartinMarie','123','etudiant','actif',NULL,NULL);
INSERT INTO `utilisateur` VALUES ('AA1234','admin1','motdepasse1','admin','actif',NULL,NULL);
INSERT INTO `utilisateur` VALUES ('BB5678','etudiant1','motdepasse2','etudiant','actif',NULL,NULL);
INSERT INTO `utilisateur` VALUES ('CC9012','etudiant2','motdepasse3','etudiant','actif',NULL,NULL);
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-06 14:19:04
