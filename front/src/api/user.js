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
                case 401: // 未认证
                    ElMessage.error('请先登录')
                    router.push('/login')
                    break
                case 403: // 权限不足
                    ElMessage.error('权限不足')
                    break
                default:
                    ElMessage.error(error.response.data.message || '服务器错误')
            }
        }
        return Promise.reject(error)
    }
)

export const userApi = {
    register(data) {
        return api.post('/users/register', data)
    },
    login(data) {
        return api.post('/users/login', data)
    },
    updateUserInfo(data) {
        return api.put('/users/update', data)
    },
    searchUsers(keyword) {
        return api.get(`/users/search?keyword=${keyword}`)
    },
    sendFriendRequest(friendId) {
        return api.post('/users/friends/request', { friendId })
    },
    handleFriendRequest(friendId, accept) {
        return api.post(`/users/friends/handle?accept=${accept}`, { friendId })
    },
    getFriendRequests() {
        return api.get('/users/friends/requests')
    },
    getFriends() {
        return api.get('/users/friends')
    }
} 