from flask import jsonify, request
from app import app, db
from app.models import Payment, Subscription
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime, timedelta

@app.route('/api/payments', methods=['GET'])
@jwt_required()
def get_payments():
    user_id = get_jwt_identity()
    page = request.args.get('page', 1, type=int)
    size = request.args.get('size', 10, type=int)
    
    query = Payment.query.filter_by(user_id=user_id)
    total = query.count()
    
    payments = query.order_by(Payment.created_at.desc())\
        .offset((page - 1) * size)\
        .limit(size)\
        .all()
    
    return jsonify({
        'items': [payment.to_dict() for payment in payments],
        'total': total
    })

@app.route('/api/payments/<int:payment_id>/pay', methods=['POST'])
@jwt_required()
def pay_order(payment_id):
    payment = Payment.query.get_or_404(payment_id)
    
    if payment.user_id != get_jwt_identity():
        return jsonify({'message': '无权限'}), 403
    
    if payment.status != 'pending':
        return jsonify({'message': '订单状态不正确'}), 400
    
    # 这里应该调用实际的支付网关API
    # 示例中直接返回支付URL
    pay_url = f"https://payment-gateway.example.com/pay/{payment.id}"
    
    return jsonify({
        'pay_url': pay_url
    })

@app.route('/api/subscriptions', methods=['GET'])
@jwt_required()
def get_subscriptions():
    user_id = get_jwt_identity()
    subscriptions = Subscription.query.filter_by(user_id=user_id).all()
    return jsonify([sub.to_dict() for sub in subscriptions])

@app.route('/api/subscriptions/<int:subscription_id>/cancel', methods=['POST'])
@jwt_required()
def cancel_subscription(subscription_id):
    subscription = Subscription.query.get_or_404(subscription_id)
    
    if subscription.user_id != get_jwt_identity():
        return jsonify({'message': '无权限'}), 403
    
    if subscription.status != 'active':
        return jsonify({'message': '订阅状态不正确'}), 400
    
    subscription.status = 'cancelled'
    subscription.cancelled_at = datetime.now()
    db.session.commit()
    
    return jsonify({'message': '订阅已取消'})

# 支付回调接口
@app.route('/api/payments/callback', methods=['POST'])
def payment_callback():
    # 验证签名等安全检查
    data = request.get_json()
    
    payment = Payment.query.get(data['payment_id'])
    if not payment:
        return jsonify({'message': '订单不存在'}), 404
    
    if data['status'] == 'success':
        payment.status = 'paid'
        payment.paid_at = datetime.now()
        
        # 如果是套餐订阅，创建或更新订阅记录
        if payment.payment_type == 'subscription':
            subscription = Subscription(
                user_id=payment.user_id,
                plan_name=payment.description,
                start_date=datetime.now(),
                end_date=datetime.now() + timedelta(days=30),  # 假设是月套餐
                status='active'
            )
            db.session.add(subscription)
    else:
        payment.status = 'failed'
    
    db.session.commit()
    return jsonify({'message': 'success'}) 