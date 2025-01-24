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