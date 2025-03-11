from flask import jsonify, request
from app import app, db
from app.models import User, Booking, Pricing
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime, timedelta
from flask import Blueprint

admin_bp = Blueprint('admin', __name__)

def admin_required(fn):
    @jwt_required()
    def wrapper(*args, **kwargs):
        user_id = get_jwt_identity()
        user = User.query.get(user_id)
        if user.role != 'manager':
            return jsonify({'message': '需要管理员权限'}), 403
        return fn(*args, **kwargs)
    return wrapper

@app.route('/api/admin/revenue', methods=['GET'])
@admin_required
def get_revenue():
    start_date = request.args.get('start_date', 
                                (datetime.now() - timedelta(days=30)).strftime('%Y-%m-%d'))
    end_date = request.args.get('end_date', datetime.now().strftime('%Y-%m-%d'))
    
    bookings = Booking.query.filter(
        Booking.start_time.between(start_date, end_date),
        Booking.status == 'completed'
    ).all()
    
    total_revenue = sum(booking.cost for booking in bookings)
    
    return jsonify({
        'total_revenue': total_revenue,
        'booking_count': len(bookings)
    })

@app.route('/api/admin/pricing', methods=['PUT'])
@admin_required
def update_pricing():
    data = request.get_json()
    pricing = Pricing.query.get(data['id'])
    
    pricing.price = data['price']
    pricing.discount_threshold = data.get('discount_threshold', pricing.discount_threshold)
    pricing.discount_percentage = data.get('discount_percentage', pricing.discount_percentage)
    
    db.session.commit()
    
    return jsonify({'message': '价格更新成功'}) 