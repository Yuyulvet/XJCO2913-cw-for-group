from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager
from flask_cors import CORS

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://username:password@localhost/scooter_db'
app.config['JWT_SECRET_KEY'] = 'your-secret-key'

db = SQLAlchemy(app)
jwt = JWTManager(app)
CORS(app)

from app import routes, models 