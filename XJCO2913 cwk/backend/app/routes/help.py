from flask import jsonify, request
from app import app, db
from app.models import HelpCategory, HelpArticle, HelpFeedback
from flask_jwt_extended import jwt_required, get_jwt_identity

@app.route('/api/help/categories', methods=['GET'])
def get_categories():
    categories = HelpCategory.query.order_by(HelpCategory.sort_order).all()
    return jsonify([category.to_dict() for category in categories])

@app.route('/api/help/articles', methods=['GET'])
def get_articles():
    category_id = request.args.get('category', type=int)
    query = HelpArticle.query
    
    if category_id:
        query = query.filter_by(category_id=category_id)
    
    articles = query.order_by(HelpArticle.is_pinned.desc(), 
                            HelpArticle.created_at.desc()).all()
    return jsonify([article.to_dict() for article in articles])

@app.route('/api/help/articles/<int:article_id>', methods=['GET'])
def get_article(article_id):
    article = HelpArticle.query.get_or_404(article_id)
    
    # 增加浏览次数
    article.views += 1
    db.session.commit()
    
    return jsonify(article.to_dict())

@app.route('/api/help/articles/<int:article_id>/feedback', methods=['POST'])
@jwt_required()
def submit_feedback(article_id):
    user_id = get_jwt_identity()
    data = request.get_json()
    
    feedback = HelpFeedback.query.filter_by(
        article_id=article_id,
        user_id=user_id
    ).first()
    
    if feedback:
        feedback.is_helpful = data['is_helpful']
        if 'comment' in data:
            feedback.comment = data['comment']
    else:
        feedback = HelpFeedback(
            article_id=article_id,
            user_id=user_id,
            is_helpful=data['is_helpful'],
            comment=data.get('comment')
        )
        db.session.add(feedback)
    
    db.session.commit()
    return jsonify({'message': 'success'})

# 管理员接口
@app.route('/api/admin/help/articles', methods=['POST'])
@jwt_required()
def create_article():
    # 验证管理员权限
    data = request.get_json()
    
    article = HelpArticle(
        category_id=data['category_id'],
        title=data['title'],
        content=data['content'],
        is_pinned=data.get('is_pinned', False)
    )
    
    db.session.add(article)
    db.session.commit()
    
    return jsonify(article.to_dict()), 201

@app.route('/api/admin/help/articles/<int:article_id>', methods=['PUT'])
@jwt_required()
def update_article(article_id):
    article = HelpArticle.query.get_or_404(article_id)
    data = request.get_json()
    
    article.title = data.get('title', article.title)
    article.content = data.get('content', article.content)
    article.category_id = data.get('category_id', article.category_id)
    article.is_pinned = data.get('is_pinned', article.is_pinned)
    
    db.session.commit()
    return jsonify(article.to_dict()) 