from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager
from config import Config

# 创建全局对象
db = SQLAlchemy()
jwt = JWTManager()

def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)

    # 初始化扩展
    db.init_app(app)
    jwt.init_app(app)

    # 注册蓝图
    from app.routes import auth_bp, admin_bp, booking_bp, help_bp
    app.register_blueprint(auth_bp)
    app.register_blueprint(admin_bp)
    app.register_blueprint(booking_bp)
    app.register_blueprint(help_bp)

    return app