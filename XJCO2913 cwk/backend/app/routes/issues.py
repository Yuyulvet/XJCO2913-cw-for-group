from flask import jsonify, request
from app import app, db
from app.models import Issue
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime

@app.route('/api/issues', methods=['POST'])
@jwt_required()
def create_issue():
    user_id = get_jwt_identity()
    data = request.get_json()
    
    issue = Issue(
        user_id=user_id,
        scooter_id=data['scooter_id'],
        description=data['description'],
        status='pending'
    )
    
    db.session.add(issue)
    db.session.commit()
    
    return jsonify({'message': '问题已报告', 'issue_id': issue.id}), 201

@app.route('/api/issues/<int:issue_id>', methods=['PUT'])
@jwt_required()
def update_issue(issue_id):
    data = request.get_json()
    issue = Issue.query.get_or_404(issue_id)
    
    issue.status = data.get('status', issue.status)
    issue.priority = data.get('priority', issue.priority)
    issue.feedback = data.get('feedback', issue.feedback)
    
    if data.get('status') == 'resolved':
        issue.resolved_at = datetime.now()
    
    db.session.commit()
    
    return jsonify({'message': '问题已更新'}) 