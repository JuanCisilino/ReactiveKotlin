DROP TABLE IF EXISTS users;

CREATE TABLE users(
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name VARCHAR(250) NOT NULL,
    email VARCHAR(250) NOT NULL
);

INSERT INTO users (first_name, last_name, email) VALUES
    ('Juan', 'Cisilino', 'juan.cisilino@gmail.com'),
    ('Jose', 'Chocobar', 'jose.chocobar@gmail.com'),
    ('Ines', 'Estevez', 'ines.estevez@gmail.com'),
    ('Jose', 'Villa', 'pancho.villa@gmail.com');