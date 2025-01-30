<template>
    <div class="login-form">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <h2>用户登录</h2>
                </div>
            </template>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
                <el-form-item prop="email" label="邮箱">
                    <el-input v-model="form.email" placeholder="请输入邮箱">
                        <template #prefix>
                            <el-icon><Message /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password" label="密码">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password>
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSubmit" :loading="loading">登录</el-button>
                    <el-button @click="$router.push('/register')">没有账号？去注册</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api/user'
import { ElMessage } from 'element-plus'
import { Message, Lock } from '@element-plus/icons-vue'
import { setToken } from '../utils/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
    email: '',
    password: ''
})

const rules = {
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleSubmit = async () => {
    if (!formRef.value) return
    
    try {
        await formRef.value.validate()
        loading.value = true
        const response = await userApi.login(form)
        if (response.data.code === 200) {
            // 保存token
            setToken(response.data.data.token)
            // 保存用户信息
            localStorage.setItem('userInfo', JSON.stringify(response.data.data))
            ElMessage.success('登录成功')
            router.push('/home')
        } else {
            ElMessage.error(response.data.message)
        }
    } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-form {
    max-width: 400px;
    margin: 100px auto;
}
.card-header {
    display: flex;
    justify-content: center;
    align-items: center;
}
.el-button {
    width: 45%;
    margin: 0 2.5%;
}
</style> 