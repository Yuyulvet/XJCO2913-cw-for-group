import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
api.interceptors.response.use(
    response => response.data,
    error => {
        if (error.response) {
            // 处理错误响应
            switch (error.response.status) {
                case 401:
                    // 未授权，清除token并跳转到登录页
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                    break;
                case 403:
                    // 权限不足
                    console.error('没有权限访问此资源');
                    break;
                case 404:
                    // 资源不存在
                    console.error('请求的资源不存在');
                    break;
                default:
                    console.error('服务器错误');
            }
        }
        return Promise.reject(error);
    }
);

export default api; 