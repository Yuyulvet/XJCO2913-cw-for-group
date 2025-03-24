import axios from 'axios';
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const api = axios.create({
    baseURL: 'http://localhost:8080/api',  // 直接使用完整的 URL
    timeout: 10000,  // 增加超时时间
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
});

// 请求拦截器
api.interceptors.request.use(
    config => {
        console.log('发送请求:', config.url, config.data);
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
api.interceptors.response.use(
    response => {
        console.log('收到响应:', response.data);
        return response.data;
    },
    error => {
        console.error('响应错误:', error);
        
        if (error.response) {
            // 服务器返回错误状态码
            console.error('错误状态:', error.response.status);
            console.error('错误数据:', error.response.data);
            
            switch (error.response.status) {
                case 401:
                    ElMessage.error('未授权，请重新登录');
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                    break;
                case 403:
                    ElMessage.error('没有权限访问此资源');
                    break;
                case 404:
                    ElMessage.error('请求的资源不存在');
                    break;
                case 500:
                    ElMessage.error('服务器错误，请稍后重试');
                    break;
                default:
                    ElMessage.error(error.response.data || '请求失败');
            }
        } else if (error.request) {
            // 请求发出但没有收到响应
            console.error('没有收到响应:', error.request);
            ElMessage.error('无法连接到服务器，请检查网络连接');
        } else {
            // 请求配置出错
            console.error('请求配置错误:', error.message);
            ElMessage.error('请求配置错误');
        }
        
        return Promise.reject(error);
    }
);

export default api; 