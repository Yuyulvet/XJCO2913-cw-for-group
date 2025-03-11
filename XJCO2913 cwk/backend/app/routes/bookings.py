from flask import jsonify, request
from app import app, db
from app.models import Booking, Scooter
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime

@app.route('/api/bookings', methods=['POST'])
@jwt_required()
def create_booking():
    user_id = get_jwt_identity()
    data = request.get_json()
    
    scooter = Scooter.query.get(data['scooter_id'])
    if not scooter or scooter.status != 'available':
        return jsonify({'message': '滑板车不可用'}), 400
        
    booking = Booking(
        user_id=user_id,
        scooter_id=data['scooter_id'],
        start_time=datetime.now(),
        status='active'
    )
    
    scooter.status = 'in_use'
    
    db.session.add(booking)
    db.session.commit()
    
    return jsonify({'message': '预订成功', 'booking_id': booking.id}), 201

@app.route('/api/bookings/<int:booking_id>/end', methods=['POST'])
@jwt_required()
def end_booking(booking_id):
    booking = Booking.query.get_or_404(booking_id)
    if booking.user_id != get_jwt_identity():
        return jsonify({'message': '无权限'}), 403
        
    booking.end_time = datetime.now()
    booking.status = 'completed'
    booking.scooter.status = 'available'
    
    # 计算费用
    duration = (booking.end_time - booking.start_time).total_seconds() / 60
    booking.cost = calculate_cost(duration)
    
    db.session.commit()
    
    return jsonify({
        'message': '预订已结束',
        'duration': duration,
        'cost': booking.cost
    }) 