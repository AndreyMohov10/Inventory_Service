CREATE TABLE products(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku VARCHAR(32) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(100,2) NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL
);

CREATE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_products_updated_at
BEFORE UPDATE ON Products
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_products_sku ON Products(sku);

CREATE OR REPLACE FUNCTION check_quantity()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.quantity < 0 THEN
        RAISE EXCEPTION 'остаток на складе не может быть отрицательным';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_quantity
BEFORE UPDATE ON Products
FOR EACH ROW EXECUTE FUNCTION check_quantity();