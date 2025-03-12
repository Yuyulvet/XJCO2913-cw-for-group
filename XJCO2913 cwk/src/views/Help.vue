<template>
  <div class="help-center">
    <div class="help-header">
      <h2>帮助中心</h2>
      <el-input
        v-model="searchQuery"
        placeholder="搜索帮助文档..."
        prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <el-row :gutter="20" class="help-content">
      <!-- 左侧分类导航 -->
      <el-col :span="6">
        <el-card class="category-nav">
          <el-menu
            :default-active="activeCategory"
            @select="handleCategorySelect"
          >
            <el-menu-item 
              v-for="category in categories"
              :key="category.id"
              :index="category.id.toString()"
            >
              <el-icon>
                <component :is="category.icon" />
              </el-icon>
              <span>{{ category.name }}</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧文章列表/详情 -->
      <el-col :span="18">
        <el-card v-if="!selectedArticle">
          <!-- 置顶文章 -->
          <div v-if="pinnedArticles.length > 0" class="pinned-articles">
            <h3>常见问题</h3>
            <div 
              v-for="article in pinnedArticles" 
              :key="article.id"
              class="article-item pinned"
              @click="showArticle(article)"
            >
              <el-icon><Star /></el-icon>
              <span>{{ article.title }}</span>
            </div>
          </div>

          <!-- 文章列表 -->
          <div class="article-list">
            <h3>全部文章</h3>
            <div 
              v-for="article in filteredArticles" 
              :key="article.id"
              class="article-item"
              @click="showArticle(article)"
            >
              <el-icon><Document /></el-icon>
              <span>{{ article.title }}</span>
            </div>
          </div>
        </el-card>

        <!-- 文章详情 -->
        <el-card v-else class="article-detail">
          <template #header>
            <div class="article-header">
              <el-button 
                text 
                @click="selectedArticle = null"
              >
                <el-icon><ArrowLeft /></el-icon>
                返回
              </el-button>
              <h3>{{ selectedArticle.title }}</h3>
            </div>
          </template>

          <div class="article-content" v-html="selectedArticle.content" />
          
          <div class="article-feedback">
            <p>这篇文章对您有帮助吗？</p>
            <div class="feedback-buttons">
              <el-button 
                :type="feedback === true ? 'primary' : 'default'"
                @click="submitFeedback(true)"
              >
                有帮助
              </el-button>
              <el-button 
                :type="feedback === false ? 'primary' : 'default'"
                @click="submitFeedback(false)"
              >
                没帮助
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Star, Document, ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'

const categories = ref([])
const articles = ref([])
const activeCategory = ref('')
const selectedArticle = ref(null)
const searchQuery = ref('')
const feedback = ref(null)

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/help/categories')
    categories.value = response.data
    if (categories.value.length > 0) {
      activeCategory.value = categories.value[0].id.toString()
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取文章列表
const fetchArticles = async (categoryId) => {
  try {
    const response = await axios.get(`/api/help/articles?category=${categoryId}`)
    articles.value = response.data
  } catch (error) {
    console.error('获取文章失败:', error)
  }
}

// 置顶文章
const pinnedArticles = computed(() => 
  articles.value.filter(article => article.is_pinned)
)

// 根据搜索过滤文章
const filteredArticles = computed(() => {
  if (!searchQuery.value) {
    return articles.value.filter(article => !article.is_pinned)
  }
  return articles.value.filter(article => 
    article.title.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 处理分类选择
const handleCategorySelect = (categoryId) => {
  activeCategory.value = categoryId
  fetchArticles(categoryId)
  selectedArticle.value = null
}

// 显示文章详情
const showArticle = async (article) => {
  try {
    const response = await axios.get(`/api/help/articles/${article.id}`)
    selectedArticle.value = response.data
  } catch (error) {
    console.error('获取文章详情失败:', error)
  }
}

// 提交反馈
const submitFeedback = async (isHelpful) => {
  if (feedback.value === isHelpful) return
  
  try {
    await axios.post(`/api/help/articles/${selectedArticle.value.id}/feedback`, {
      is_helpful: isHelpful
    })
    feedback.value = isHelpful
  } catch (error) {
    console.error('提交反馈失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  if (selectedArticle.value) {
    selectedArticle.value = null
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.help-center {
  padding: 20px;
}

.help-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.help-header .el-input {
  width: 300px;
}

.category-nav {
  margin-bottom: 20px;
}

.article-item {
  display: flex;
  align-items: center;
  padding: 12px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.article-item:hover {
  background-color: #f5f7fa;
}

.article-item .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.pinned-articles {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.article-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.article-content {
  line-height: 1.8;
  margin: 20px 0;
}

.article-feedback {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
}

.feedback-buttons {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style> 