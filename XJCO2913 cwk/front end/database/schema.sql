CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('user', 'manager') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE scooters (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status ENUM('available', 'in_use', 'maintenance') DEFAULT 'available',
    battery_level INT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    last_maintained TIMESTAMP
);

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    scooter_id INT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status ENUM('active', 'completed', 'cancelled'),
    cost DECIMAL(10, 2),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (scooter_id) REFERENCES scooters(id)
);

CREATE TABLE issues (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    scooter_id INT,
    description TEXT,
    status ENUM('pending', 'in_progress', 'resolved'),
    priority INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    feedback TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (scooter_id) REFERENCES scooters(id)
);

CREATE TABLE pricing (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('per_minute', 'weekly', 'monthly'),
    price DECIMAL(10, 2),
    discount_threshold INT,
    discount_percentage INT
);

CREATE TABLE help_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(50),
    sort_order INT DEFAULT 0
);

CREATE TABLE help_articles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    views INT DEFAULT 0,
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES help_categories(id)
);

CREATE TABLE help_feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT,
    user_id INT,
    is_helpful BOOLEAN,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES help_articles(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
); 