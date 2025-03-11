from app import db
from werkzeug.security import generate_password_hash, check_password_hash
from datetime import datetime

class User(db.Model):
    __tablename__ = 'users'
    
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password_hash = db.Column(db.String(255), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    role = db.Column(db.Enum('user', 'manager'), default='user')
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    bookings = db.relationship('Booking', backref='user', lazy=True)
    issues = db.relationship('Issue', backref='user', lazy=True)
    
    def set_password(self, password):
        self.password_hash = generate_password_hash(password)
        
    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

class Scooter(db.Model):
    __tablename__ = 'scooters'
    
    id = db.Column(db.Integer, primary_key=True)
    status = db.Column(db.Enum('available', 'in_use', 'maintenance'), default='available')
    battery_level = db.Column(db.Integer)
    latitude = db.Column(db.Numeric(10, 8))
    longitude = db.Column(db.Numeric(11, 8))
    last_maintained = db.Column(db.DateTime)
    
    bookings = db.relationship('Booking', backref='scooter', lazy=True)
    issues = db.relationship('Issue', backref='scooter', lazy=True)

class Booking(db.Model):
    __tablename__ = 'bookings'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    scooter_id = db.Column(db.Integer, db.ForeignKey('scooters.id'))
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime)
    status = db.Column(db.Enum('active', 'completed', 'cancelled'))
    cost = db.Column(db.Numeric(10, 2))

class Payment(db.Model):
    __tablename__ = 'payments'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    order_no = db.Column(db.String(32), unique=True, nullable=False)
    amount = db.Column(db.Numeric(10, 2), nullable=False)
    payment_type = db.Column(db.String(20), nullable=False)
    status = db.Column(db.String(20), default='pending')
    description = db.Column(db.String(255))
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    paid_at = db.Column(db.DateTime)
    
    def to_dict(self):
        return {
            'id': self.id,
            'order_no': self.order_no,
            'amount': float(self.amount),
            'payment_type': self.payment_type,
            'status': self.status,
            'description': self.description,
            'created_at': self.created_at.isoformat(),
            'paid_at': self.paid_at.isoformat() if self.paid_at else None
        }

class Subscription(db.Model):
    __tablename__ = 'subscriptions'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    plan_name = db.Column(db.String(50), nullable=False)
    start_date = db.Column(db.DateTime, nullable=False)
    end_date = db.Column(db.DateTime, nullable=False)
    status = db.Column(db.String(20), default='active')
    cancelled_at = db.Column(db.DateTime)
    
    def to_dict(self):
        return {
            'id': self.id,
            'plan_name': self.plan_name,
            'start_date': self.start_date.isoformat(),
            'end_date': self.end_date.isoformat(),
            'status': self.status,
            'cancelled_at': self.cancelled_at.isoformat() if self.cancelled_at else None
        }

class Notification(db.Model):
    __tablename__ = 'notifications'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    title = db.Column(db.String(100), nullable=False)
    content = db.Column(db.Text, nullable=False)
    type = db.Column(db.String(20), nullable=False)  # system, booking, payment, issue
    is_read = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'content': self.content,
            'type': self.type,
            'is_read': self.is_read,
            'created_at': self.created_at.isoformat()
        }

class HelpCategory(db.Model):
    __tablename__ = 'help_categories'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)
    icon = db.Column(db.String(50))
    sort_order = db.Column(db.Integer, default=0)
    
    articles = db.relationship('HelpArticle', backref='category', lazy=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'icon': self.icon,
            'sort_order': self.sort_order
        }

class HelpArticle(db.Model):
    __tablename__ = 'help_articles'
    
    id = db.Column(db.Integer, primary_key=True)
    category_id = db.Column(db.Integer, db.ForeignKey('help_categories.id'))
    title = db.Column(db.String(200), nullable=False)
    content = db.Column(db.Text, nullable=False)
    views = db.Column(db.Integer, default=0)
    is_pinned = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    feedback = db.relationship('HelpFeedback', backref='article', lazy=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'category_id': self.category_id,
            'title': self.title,
            'content': self.content,
            'views': self.views,
            'is_pinned': self.is_pinned,
            'created_at': self.created_at.isoformat(),
            'updated_at': self.updated_at.isoformat()
        }

class HelpFeedback(db.Model):
    __tablename__ = 'help_feedback'
    
    id = db.Column(db.Integer, primary_key=True)
    article_id = db.Column(db.Integer, db.ForeignKey('help_articles.id'))
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    is_helpful = db.Column(db.Boolean)
    comment = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow) 