import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api'
})

export const userApi = {
    register(data) {
        return api.post('/users/register', data)
    },
    login(data) {
        return api.post('/users/login', data)
    }
} 