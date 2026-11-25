-- Таблиця для всіх об'єктів ієрархії Clothes (Clothes, Pants, Jeans, Shirts, FormalShirt).
-- Поле type визначає клас об'єкта, поля окремих підкласів можуть бути NULL.
CREATE TABLE IF NOT EXISTS clothes (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    size VARCHAR(5) NOT NULL,
    color VARCHAR(50) NOT NULL,
    material VARCHAR(20) NOT NULL,
    release_year INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    quantity INTEGER NOT NULL,
    waist INTEGER,
    leg_length INTEGER,
    fit VARCHAR(20),
    density INTEGER,
    sleeve VARCHAR(20),
    collar INTEGER,
    cufflinks BOOLEAN,
    non_iron BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
