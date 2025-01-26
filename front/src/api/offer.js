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

export const offerApi = {
    createOffer(data) {
        return api.post('/offers', data)
    },
    getUserOffers() {
        return api.get('/offers')
    },
    deleteOffer(offerId) {
        return api.delete(`/offers/${offerId}`)
    },
    getRecommendedOffers(pageNum = 1, pageSize = 10) {
        return api.get(`/offers/recommended?pageNum=${pageNum}&pageSize=${pageSize}`)
    }
} 