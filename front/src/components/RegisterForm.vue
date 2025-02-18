<template>
    <div class="register-form">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <h2>用户注册</h2>
                </div>
            </template>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item prop="name" label="姓名">
                    <el-input v-model="form.name" placeholder="请输入姓名">
                        <template #prefix>
                            <el-icon><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
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
                <el-form-item prop="targetIndustry" label="目标行业">
                    <el-select v-model="form.targetIndustry" placeholder="请选择目标行业" style="width: 100%">
                        <el-option label="技术" value="Tech" />
                        <el-option label="金融" value="Finance" />
                        <el-option label="医疗" value="Medical" />
                        <el-option label="教育" value="Education" />
                    </el-select>
                </el-form-item>
                <el-form-item prop="preferredLocation" label="偏好地点">
                    <el-select v-model="form.preferredLocation" placeholder="请选择偏好地点" style="width: 100%">
                        <el-option label="北京" value="Beijing" />
                        <el-option label="上海" value="Shanghai" />
                        <el-option label="深圳" value="Shenzhen" />
                        <el-option label="广州" value="Guangzhou" />
                    </el-select>
                </el-form-item>
                <el-form-item prop="workLifeBalanceScore" label="工作生活平衡评分">
                    <el-input-number 
                        v-model="form.workLifeBalanceScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        :controls-position="'right'"
                        style="width: 100%">
                    </el-input-number>
                </el-form-item>
                <el-form-item prop="salaryScore" label="期望月薪（元）">
                    <el-input-number 
                        v-model="form.salaryScore"
                        :min="0"
                        :max="100000"
                        :step="1000"
                        :controls-position="'right'"
                        style="width: 100%">
                    </el-input-number>
                </el-form-item>
                <el-form-item prop="workingHoursScore" label="期望每日工作时长（小时）">
                    <el-input-number 
                        v-model="form.workingHoursScore"
                        :min="4"
                        :max="12"
                        :step="0.5"
                        :controls-position="'right'"
                        style="width: 100%">
                    </el-input-number>
                </el-form-item>
                <el-form-item prop="overtimeHoursScore" label="可接受的月加班时长（小时）">
                    <el-input-number 
                        v-model="form.overtimeHoursScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        :controls-position="'right'"
                        style="width: 100%">
                    </el-input-number>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSubmit" :loading="loading">注册</el-button>
                    <el-button @click="$router.push('/login')">已有账号？去登录</el-button>
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
import { User, Message, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
    name: '',
    email: '',
    password: '',
    targetIndustry: '',
    preferredLocation: '',
    workLifeBalanceScore: 50,
    salaryScore: 10000,
    workingHoursScore: 8,
    overtimeHoursScore: 20
})

const rules = {
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    workLifeBalanceScore: [
        { required: true, message: '请输入工作生活平衡评分', trigger: 'blur' },
        { type: 'number', min: 0, max: 100, message: '分值必须在0-100之间', trigger: 'change' }
    ],
    salaryScore: [
        { required: true, message: '请输入期望月薪', trigger: 'blur' },
        { type: 'number', min: 0, message: '薪资不能小于0', trigger: 'change' }
    ],
    workingHoursScore: [
        { required: true, message: '请输入期望工作时长', trigger: 'blur' },
        { type: 'number', min: 4, max: 12, message: '工作时长应在4-12小时之间', trigger: 'change' }
    ],
    overtimeHoursScore: [
        { required: true, message: '请输入可接受的加班时长', trigger: 'blur' },
        { type: 'number', min: 0, message: '加班时长不能小于0', trigger: 'change' }
    ]
}

const handleSubmit = async () => {
    if (!formRef.value) return
    
    try {
        await formRef.value.validate()
        loading.value = true
        const response = await userApi.register(form)
        if (response.data.code === 200) {
            ElMessage.success('注册成功')
            router.push('/login')
        } else {
            ElMessage.error(response.data.message)
        }
    } catch (error) {
        ElMessage.error(error.response?.data?.message || '注册失败')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.register-form {
    max-width: 500px;
    margin: 50px auto;
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
:deep(.el-input-number) {
    width: 100%;
}
</style> 