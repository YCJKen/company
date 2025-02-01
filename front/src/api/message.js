import axios from 'axios'
import router from '../router'
import { getToken } from '../utils/auth'
import { ElMessage } from 'element-plus'

const api = axios.create({
    baseURL: 'http://localhost:8080/api'
})

// 请求拦截器
api.interceptors.request.use(
    config => {
        const token = getToken()
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
api.interceptors.response.use(
    response => {
        return response
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    ElMessage.error('请先登录')
                    router.push('/login')
                    break
                case 403:
                    ElMessage.error('权限不足')
                    break
                default:
                    ElMessage.error(error.response.data.message || '服务器错误')
            }
        }
        return Promise.reject(error)
    }
)

export const messageApi = {
    sendMessage(data) {
        return api.post('/messages', data)
    },
    getConversation(friendId) {
        return api.get(`/messages/conversation/${friendId}`)
    },
    getUnreadCount() {
        return api.get('/messages/unread/count')
    }
} 