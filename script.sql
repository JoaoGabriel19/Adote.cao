drop database if exists adotecao;
CREATE DATABASE adotecao;

USE adotecao;

CREATE TABLE adress (
  idAdress INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  state VARCHAR(50) NOT NULL,
  city VARCHAR(50) NOT NULL,
  neighbor VARCHAR(50) NOT NULL,
  cep VARCHAR(10) NOT NULL
);

CREATE TABLE userOng (
  idOng INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  login VARCHAR(50) NOT NULL,
  pw VARCHAR(64) NOT NULL,
  username VARCHAR(50) NOT NULL,
  cpf VARCHAR(11) NOT NULL,
  birth DATE NOT NULL,
  ongName VARCHAR(100) NOT NULL,
  publicKey VARCHAR(1000) NOT NULL,
  privateKey VARCHAR(1000) NOT NULL,
  secretKey VARCHAR(300),
  idAdress INT NOT NULL,
  FOREIGN KEY (idAdress) REFERENCES adress(idAdress)
);

CREATE TABLE animal (
  idAnimal INT PRIMARY KEY,
  race VARCHAR(50),
  name VARCHAR(50),
  size VARCHAR(50),
  age INT,
  idOng INT,
  FOREIGN KEY (idOng) REFERENCES userOng(idOng) ON DELETE CASCADE
);


CREATE TABLE userAdopter (
  idAdopter INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  login VARCHAR(100) NOT NULL,
  pw VARCHAR(64) NOT NULL,
  cpf VARCHAR(11) NOT NULL,
  birth DATE NOT NULL,
  publicKey VARCHAR(1000) NOT NULL,
  privateKey VARCHAR(1000) NOT NULL,
  secretKey VARCHAR(300),
  idAdress INT NOT NULL,
  FOREIGN KEY (idAdress) REFERENCES adress(idAdress)
);

CREATE TABLE animalAdoption (
  idAnimal INT,
  idAdopter INT,
  FOREIGN KEY (idAnimal) REFERENCES animal(idAnimal),
  FOREIGN KEY (idAdopter) REFERENCES userAdopter(idAdopter) ON DELETE CASCADE,
  PRIMARY KEY (idAnimal, idAdopter)
);


CREATE TABLE behavior (
  idBehavior INT NOT NULL PRIMARY KEY
);

CREATE TABLE animalBehavior (
  idAnimal INT NOT NULL,
  idBehavior INT NOT NULL,
  FOREIGN KEY (idAnimal) REFERENCES animal(idAnimal),
  FOREIGN KEY (idBehavior) REFERENCES behavior(idBehavior),
  PRIMARY KEY (idAnimal, idBehavior)
);

