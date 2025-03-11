import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app import create_app, db
from app.models import *
from sqlalchemy import create_engine
from sqlalchemy_utils import database_exists, create_database
from config import Config

def create_database():
    """创建数据库（如果不存在）"""
    try:
        engine = create_engine(Config.SQLALCHEMY_DATABASE_URI)
        if not database_exists(engine.url):
            create_database(engine.url)
            print(f"数据库创建成功!")
        else:
            print(f"数据库已存在!")
    except Exception as e:
        print(f"创建数据库时出错: {e}")

def create_tables():
    """创建所有数据库表"""
    try:
        db.create_all()
        print("数据库表创建成功!")
    except Exception as e:
        print(f"创建数据库表时出错: {e}")

def init_db():
    """初始化数据库"""
    app = create_app()
    with app.app_context():
        try:
            # 先删除所有表
            db.drop_all()
            print("已删除旧表")
            
            # 创建新表
            db.create_all()
            print("已创建新表")
            
            # 添加初始数据
            
            # 1. 创建管理员用户
            admin = User(
                username='admin',
                email='admin@example.com',
                role='manager'
            )
            admin.set_password('admin123')
            db.session.add(admin)
            
            # 2. 创建测试用户
            test_user = User(
                username='test',
                email='test@example.com',
                role='user'
            )
            test_user.set_password('test123')
            db.session.add(test_user)
            
            # 3. 添加测试滑板车
            scooters = [
                Scooter(
                    status='available',
                    battery_level=100,
                    latitude=31.2304,
                    longitude=121.4737
                ),
                Scooter(
                    status='available',
                    battery_level=90,
                    latitude=31.2314,
                    longitude=121.4747
                ),
                Scooter(
                    status='maintenance',
                    battery_level=20,
                    latitude=31.2324,
                    longitude=121.4757
                )
            ]
            db.session.add_all(scooters)
            
            # 4. 添加价格配置
            pricing_configs = [
                Pricing(type='per_minute', price=0.5),
                Pricing(type='weekly', price=50.0, discount_threshold=7, discount_percentage=10),
                Pricing(type='monthly', price=180.0, discount_threshold=30, discount_percentage=20)
            ]
            db.session.add_all(pricing_configs)
            
            # 5. 添加帮助分类
            help_categories = [
                HelpCategory(name='使用指南', icon='Document', sort_order=1),
                HelpCategory(name='故障解决', icon='Warning', sort_order=2),
                HelpCategory(name='收费说明', icon='Money', sort_order=3),
                HelpCategory(name='安全须知', icon='Shield', sort_order=4)
            ]
            db.session.add_all(help_categories)
            
            # 6. 添加一些示例帮助文章
            help_articles = [
                HelpArticle(
                    category_id=1,
                    title='如何开始使用滑板车',
                    content='1. 扫描二维码解锁\n2. 检查车辆状态\n3. 开始使用',
                    is_pinned=True
                ),
                HelpArticle(
                    category_id=2,
                    title='遇到故障怎么办',
                    content='如果遇到故障，请：\n1. 确保安全\n2. 联系客服\n3. 等待处理',
                    is_pinned=True
                )
            ]
            db.session.add_all(help_articles)
            
            # 提交所有更改
            db.session.commit()
            print("初始数据添加成功!")
            
        except Exception as e:
            db.session.rollback()
            print(f"初始化数据库时出错: {e}")

if __name__ == '__main__':
    # 创建数据库
    create_database()
    
    # 初始化数据库表和数据
    init_db() 