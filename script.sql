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
  pw VARCHAR(50) NOT NULL,
  username VARCHAR(50) NOT NULL,
  cpf VARCHAR(11) NOT NULL,
  birth DATE NOT NULL,
  ongName VARCHAR(100) NOT NULL,
  publicKey VARCHAR(1000) NOT NULL,
  privateKey VARCHAR(1000) NOT NULL,
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
  name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  pw VARCHAR(50) NOT NULL,
  publicKey VARCHAR(500) NOT NULL,
  privateKey VARCHAR(500) NOT NULL
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

CREATE TABLE state (
  stateName VARCHAR(20) NOT NULL,
  acronym VARCHAR(2) NOT NULL,
  ibgeCode VARCHAR(2) NOT NULL PRIMARY KEY
);

INSERT INTO state (ibgeCode, acronym, stateName) VALUES
    (11, 'RO', 'Rondonia'),
    (12, 'AC', 'Acre'),
    (13, 'AM', 'Amazonas'),
    (14, 'RR', 'Roraima'),
    (15, 'PA', 'Para'),
    (16, 'AP', 'Amapa'),
    (17, 'TO', 'Tocantins'),
    (21, 'MA', 'Maranhao'),
    (22, 'PI', 'Piaui'),
    (23, 'CE', 'Ceara'),
    (24, 'RN', 'Rio Grande do Norte'),
    (25, 'PB', 'Paraiba'),
    (26, 'PE', 'Pernambuco'),
    (27, 'AL', 'Alagoas'),
    (28, 'SE', 'Sergipe'),
    (29, 'BA', 'Bahia'),
    (31, 'MG', 'Minas Gerais'),
    (32, 'ES', 'Espirito Santo'),
    (33, 'RJ', 'Rio de Janeiro'),
    (35, 'SP', 'Sao Paulo'),
    (41, 'PR', 'Parana'),
    (42, 'SC', 'Santa Catarina'),
    (43, 'RS', 'Rio Grande do Sul'),
    (50, 'MS', 'Mato Grosso do Sul'),
    (51, 'MT', 'Mato Grosso'),
    (52, 'GO', 'Goias'),
    (53, 'DF', 'Distrito Federal');


