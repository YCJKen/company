import axios from 'axios'
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
        ElMessage.error(error.response?.data?.message || '上传失败')
        return Promise.reject(error)
    }
)

export const uploadApi = {
    uploadFile(formData) {
        return api.post('/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then(response => {
            if (response.data.code === 200) {
                return response
            } else {
                throw new Error(response.data.message || '上传失败')
            }
        }).catch(error => {
            throw error
        })
    }
} 