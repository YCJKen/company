<template>
    <div class="offer-form">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <h2>创建 Offer</h2>
                </div>
            </template>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
                <el-form-item prop="companyName" label="公司名称">
                    <el-input v-model="form.companyName" placeholder="请输入公司名称" />
                </el-form-item>

                <el-form-item prop="salary" label="薪资">
                    <el-input-number 
                        v-model="form.salary"
                        :min="0"
                        :controls-position="'right'"
                        style="width: 200px" />
                    <el-select v-model="form.salaryCurrency" style="margin-left: 10px">
                        <el-option label="CNY" value="CNY" />
                        <el-option label="USD" value="USD" />
                    </el-select>
                </el-form-item>

                <el-form-item prop="jobTitle" label="职位名称">
                    <el-input v-model="form.jobTitle" placeholder="请输入职位名称" />
                </el-form-item>

                <el-form-item prop="industry" label="行业">
                    <el-select v-model="form.industry" placeholder="请选择行业">
                        <el-option label="技术" value="Tech" />
                        <el-option label="金融" value="Finance" />
                        <el-option label="医疗" value="Medical" />
                        <el-option label="教育" value="Education" />
                    </el-select>
                </el-form-item>

                <el-form-item prop="location" label="工作地点">
                    <el-select v-model="form.location" placeholder="请选择工作地点">
                        <el-option label="北京" value="Beijing" />
                        <el-option label="上海" value="Shanghai" />
                        <el-option label="深圳" value="Shenzhen" />
                        <el-option label="广州" value="Guangzhou" />
                    </el-select>
                </el-form-item>

                <el-form-item prop="workLifeBalanceScore" label="工作生活平衡">
                    <el-slider
                        v-model="form.workLifeBalanceScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        show-input>
                    </el-slider>
                </el-form-item>

                <el-form-item prop="salarySatisfactionScore" label="薪资满意度">
                    <el-slider
                        v-model="form.salarySatisfactionScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        show-input>
                    </el-slider>
                </el-form-item>

                <el-form-item prop="workingHoursScore" label="工作时长评分">
                    <el-slider
                        v-model="form.workingHoursScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        show-input>
                    </el-slider>
                </el-form-item>

                <el-form-item prop="overtimeHoursScore" label="加班情况评分">
                    <el-slider
                        v-model="form.overtimeHoursScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        show-input>
                    </el-slider>
                </el-form-item>

                <el-form-item prop="offerDate" label="录取日期">
                    <el-date-picker
                        v-model="form.offerDate"
                        type="date"
                        placeholder="选择日期">
                    </el-date-picker>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
                    <el-button @click="resetForm">重置</el-button>
                    <el-button @click="goBack">返回</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { offerApi } from '../api/offer'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
    companyName: '',
    salary: 0,
    salaryCurrency: 'CNY',
    jobTitle: '',
    industry: '',
    location: '',
    workLifeBalanceScore: 50,
    salarySatisfactionScore: 50,
    workingHoursScore: 50,
    overtimeHoursScore: 50,
    offerDate: null
})

const rules = {
    companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
    salary: [{ required: true, message: '请输入薪资', trigger: 'blur' }],
    salaryCurrency: [{ required: true, message: '请选择货币类型', trigger: 'change' }]
}

const handleSubmit = async () => {
    if (!formRef.value) return
    
    try {
        await formRef.value.validate()
        loading.value = true
        const response = await offerApi.createOffer(form)
        if (response.data.code === 200) {
            ElMessage.success('创建成功')
            setTimeout(() => {
                router.push('/home')
            }, 500)
        }
    } catch (error) {
        ElMessage.error(error.response?.data?.message || '创建失败')
    } finally {
        loading.value = false
    }
}

const resetForm = () => {
    if (formRef.value) {
        formRef.value.resetFields()
    }
}

const goBack = () => {
    router.push('/home')
}
</script>

<style scoped>
.offer-form {
    max-width: 800px;
    margin: 20px auto;
}
.card-header {
    display: flex;
    justify-content: center;
    align-items: center;
}
.el-button {
    margin: 0 10px;
}
</style> 