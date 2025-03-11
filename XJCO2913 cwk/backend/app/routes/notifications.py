from flask import jsonify
from app import app, db
from app.models import Notification
from flask_jwt_extended import jwt_required, get_jwt_identity

@app.route('/api/notifications', methods=['GET'])
@jwt_required()
def get_notifications():
    user_id = get_jwt_identity()
    notifications = Notification.query.filter_by(user_id=user_id)\
        .order_by(Notification.created_at.desc())\
        .limit(50)\
        .all()
    return jsonify([n.to_dict() for n in notifications])

@app.route('/api/notifications/<int:notification_id>/read', methods=['POST'])
@jwt_required()
def mark_as_read(notification_id):
    user_id = get_jwt_identity()
    notification = Notification.query.filter_by(
        id=notification_id, 
        user_id=user_id
    ).first_or_404()
    
    notification.is_read = True
    db.session.commit()
    return jsonify({'message': 'success'})

@app.route('/api/notifications/read-all', methods=['POST'])
@jwt_required()
def mark_all_as_read():
    user_id = get_jwt_identity()
    Notification.query.filter_by(
        user_id=user_id, 
        is_read=False
    ).update({'is_read': True})
    
    db.session.commit()
    return jsonify({'message': 'success'})

# 发送通知的工具函数
def send_notification(user_id, title, content, type='system'):
    notification = Notification(
        user_id=user_id,
        title=title,
        content=content,
        type=type
    )
    db.session.add(notification)
    db.session.commit() 