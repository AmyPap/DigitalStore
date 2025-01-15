CREATE TABLE Categories (
	id INT AUTO_INCREMENT PRIMARY KEY,
	parent_id INT,
	category_name VARCHAR(255) NOT NULL,
	FOREIGN KEY (parent_id) REFERENCES Categories(id)
	);

INSERT INTO Categories (category_name, parent_id)
VALUES
('Shoes', NULL),       -- Shoes,clothes,Accessories have no parent because they are Main Categories
('Clothes', NULL),
('Accessories', NULL),
('Men', 1),          -- Subcategory is linked to the main category using the parent_id (1 is Shoes,2 is Clothes osv)
('Women', 1),
('Kids', 1),
('Men', 2),
('Women', 2),
('Kids', 2),
('Men', 3),
('Women', 3),
('Kids', 3);

SELECT * FROM Categories;

CREATE TABLE Brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    brand_name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO Brands (brand_name)
VALUES ('Nike'), ('Adidas'), ('Puma'), ('Converse'), ('VANS');

SELECT * FROM Brands ORDER BY id ASC;


CREATE TABLE Products (
	id INT AUTO_INCREMENT PRIMARY KEY,
	product_code VARCHAR(100) UNIQUE NOT NULL,
	product_name VARCHAR(255) NOT NULL,
	brand_id INT NOT NULL,
	description TEXT,
	price DECIMAL(10,2) NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (category_id) REFERENCES Categories(id),
	FOREIGN KEY (brand_id) REFERENCES Brands(id)
);


INSERT INTO Products (product_code, product_name, brand_id, description, price, category_id)
VALUES
('ABC123', 'Football Shoes',2, 'Predator League L FG', 749.00, 4),
('ABC1234', 'Jacket',1, 'Fernie W jacket', 1799.00, 8),
('ABCD123', 'Water Bottle',3, '500ml stainless steel water bottle', 199.00, 3),
('ABC234', 'Sneakers', 2 , 'VL Court 3.0 mr Sneakers', 479.00 , 6);


CREATE TABLE Sizes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    size VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO Sizes (size)
VALUES ('NOSIZE') , ('ONESIZE'), ('XSMALL'), ('SMALL'), ('MEDIUM'), ('LARGE'), ('XLARGE'),
	('18'), ('19'), ('20'), ('21'), ('22'), ('23'), ('24'), ('25'),
    ('26'), ('27'), ('28'), ('29'), ('30'), ('31'), ('32'), ('33'),
    ('34'), ('35'), ('36'), ('37'), ('38'), ('39'), ('40'), ('41'),
    ('42'), ('43'), ('44'), ('45'), ('46'),('47');

SELECT * FROM Sizes ORDER BY id ASC;

CREATE TABLE Colors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    color VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO Colors (color)
VALUES ('Red'), ('Blue'), ('Black'), ('White'),
		('Green'), ('Yellow'), ('Pink') , ('Brown'),
		('Silver') , ('Gold'), ('Orange') , ('Multicolor');


SELECT * FROM Colors ORDER BY id ASC;

CREATE TABLE Product_Options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    size_id INT,
    color_id INT,
    stock_quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES Products(id),
    FOREIGN KEY (size_id) REFERENCES Sizes(id),
    FOREIGN KEY (color_id) REFERENCES Colors(id)
);

INSERT INTO Product_Options (product_id, size_id, color_id, stock_quantity)
VALUES
(1, 30, 1, 10), -- Football Shoes (product_id =1)Size 40, Red
(1, 31, 2, 8),
(1, 32, 3, 6),
(2, 4, 3, 15),   -- Jacket(product_id= 2) Medium, Black
(2, 5, 4, 5),
(3, 1, 5, 20),
(3, 1, 11, 20),
(4, 25, 7, 15),
(4, 26, 1, 12),
(4, 27, 6, 8);




