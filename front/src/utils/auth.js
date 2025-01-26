import router from '../router'
import { ElMessage } from 'element-plus'

// 用于管理token的工具函数
export const getToken = () => {
    return localStorage.getItem('token')
}

export const setToken = (token) => {
    localStorage.setItem('token', token)
}

export const removeToken = () => {
    localStorage.removeItem('token')
}

// 修改注销方法，使用 nextTick 确保组件卸载完成
export const logout = async () => {
    removeToken()
    await router.push('/login')
    ElMessage.success('已退出登录')
} 