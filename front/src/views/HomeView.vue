<template>
    <div class="home">
        <div class="main-content">
            <el-card class="welcome-card">
                <h1>{{ showOfferContent ? 'OfferShow' : '欢迎来到主页' }}</h1>
                <div class="button-group">
                    <template v-if="!showOfferContent">
                        <el-button type="primary" @click="handleShowOffer">
                            OfferShow
                        </el-button>
                        <el-button type="success" @click="$router.push('/posts')">
                            社区动态
                        </el-button>
                    </template>
                    <template v-else>
                        <el-button type="primary" @click="$router.push('/offers')">
                            查看我的 Offers
                        </el-button>
                        <el-button type="success" @click="$router.push('/offers/create')">
                            创建新的 Offer
                        </el-button>
                        <el-button type="info" @click="handleBack">
                            返回主界面
                        </el-button>
                    </template>
                    <el-button type="warning" @click="handleBackToLogin">
                        返回登录页
                    </el-button>
                </div>
            </el-card>

            <RecommendedOffers v-if="showOfferContent" />
        </div>

        <!-- 修改用户信息侧边栏为可折叠形式 -->
        <div class="user-info-sidebar" :class="{ 'collapsed': !isExpanded }">
            <div class="avatar-trigger" @click="toggleExpand">
                <el-avatar :size="64" :icon="UserFilled" />
                <el-icon :class="{ 'expanded': isExpanded }"><ArrowRight /></el-icon>
            </div>
            
            <el-card class="user-info-card" v-show="isExpanded">
                <template #header>
                    <div class="user-info-header">
                        <h3>{{ userInfo.name || '用户' }}</h3>
                        <el-button type="primary" link @click="handleEdit">
                            <el-icon><Edit /></el-icon>
                            编辑信息
                        </el-button>
                    </div>
                </template>
                <div class="user-info-content">
                    <div class="info-item">
                        <span class="label">目标行业：</span>
                        <el-tag>{{ userInfo.targetIndustry || '未设置' }}</el-tag>
                    </div>
                    <div class="info-item">
                        <span class="label">期望地点：</span>
                        <el-tag type="success">{{ userInfo.preferredLocation || '未设置' }}</el-tag>
                    </div>
                    <div class="info-item">
                        <span class="label">期望薪资：</span>
                        <el-tag type="warning">{{ userInfo.salaryScore || '0' }} 元/月</el-tag>
                    </div>
                    <div class="info-item">
                        <span class="label">工作时长期望：</span>
                        <el-tag type="info">{{ userInfo.workingHoursScore || '0' }} 小时/天</el-tag>
                    </div>
                    <div class="info-item">
                        <span class="label">工作生活平衡期望：</span>
                        <el-progress 
                            :percentage="userInfo.workLifeBalanceScore || 0"
                            :format="format"
                            :status="getProgressStatus(userInfo.workLifeBalanceScore)"
                        />
                    </div>
                    <div class="info-item">
                        <span class="label">加班接受程度：</span>
                        <el-progress 
                            :percentage="userInfo.overtimeHoursScore || 0"
                            :format="format"
                            :status="getProgressStatus(userInfo.overtimeHoursScore)"
                        />
                    </div>
                </div>
            </el-card>
        </div>

        <!-- 添加编辑对话框 -->
        <el-dialog
            v-model="dialogVisible"
            title="编辑个人信息"
            width="500px"
        >
            <el-form
                ref="editFormRef"
                :model="editForm"
                :rules="rules"
                label-width="120px"
            >
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="editForm.name" />
                </el-form-item>
                
                <el-form-item label="目标行业" prop="targetIndustry">
                    <el-select v-model="editForm.targetIndustry" style="width: 100%">
                        <el-option label="技术" value="Tech" />
                        <el-option label="金融" value="Finance" />
                        <el-option label="医疗" value="Medical" />
                        <el-option label="教育" value="Education" />
                    </el-select>
                </el-form-item>

                <el-form-item label="期望地点" prop="preferredLocation">
                    <el-select v-model="editForm.preferredLocation" style="width: 100%">
                        <el-option label="北京" value="Beijing" />
                        <el-option label="上海" value="Shanghai" />
                        <el-option label="深圳" value="Shenzhen" />
                        <el-option label="广州" value="Guangzhou" />
                    </el-select>
                </el-form-item>

                <el-form-item label="期望月薪" prop="salaryScore">
                    <el-input-number 
                        v-model="editForm.salaryScore"
                        :min="0"
                        :max="100000"
                        :step="1000"
                        style="width: 100%"
                    />
                </el-form-item>

                <el-form-item label="期望工作时长" prop="workingHoursScore">
                    <el-input-number 
                        v-model="editForm.workingHoursScore"
                        :min="4"
                        :max="12"
                        :step="0.5"
                        style="width: 100%"
                    />
                </el-form-item>

                <el-form-item label="工作生活平衡" prop="workLifeBalanceScore">
                    <el-slider
                        v-model="editForm.workLifeBalanceScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        show-input
                    />
                </el-form-item>

                <el-form-item label="加班接受程度" prop="overtimeHoursScore">
                    <el-slider
                        v-model="editForm.overtimeHoursScore"
                        :min="0"
                        :max="100"
                        :step="1"
                        show-input
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleUpdate" :loading="updating">
                        确认
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, ArrowRight, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import RecommendedOffers from '../components/RecommendedOffers.vue'
import { removeToken } from '../utils/auth'
import { userApi } from '../api/user'

const router = useRouter()
const showOfferContent = ref(false)
const userInfo = ref({})
const isExpanded = ref(false)
const dialogVisible = ref(false)
const editFormRef = ref(null)
const updating = ref(false)
const editForm = reactive({
    name: '',
    targetIndustry: '',
    preferredLocation: '',
    salaryScore: 0,
    workingHoursScore: 8,
    workLifeBalanceScore: 50,
    overtimeHoursScore: 20
})

const rules = {
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    targetIndustry: [{ required: true, message: '请选择目标行业', trigger: 'change' }],
    preferredLocation: [{ required: true, message: '请选择期望地点', trigger: 'change' }],
    salaryScore: [{ required: true, message: '请输入期望薪资', trigger: 'blur' }],
    workingHoursScore: [{ required: true, message: '请输入期望工作时长', trigger: 'blur' }]
}

// 从localStorage获取用户信息
onMounted(() => {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo) {
        userInfo.value = JSON.parse(storedUserInfo)
    }
})

const toggleExpand = () => {
    isExpanded.value = !isExpanded.value
}

const handleBackToLogin = async () => {
    removeToken()
    await router.push('/login')
}

const handleShowOffer = () => {
    showOfferContent.value = true
}

const handleBack = () => {
    showOfferContent.value = false
}

const format = (percentage) => {
    return percentage + '分'
}

const getProgressStatus = (score) => {
    if (score >= 80) return 'success'
    if (score >= 60) return 'warning'
    return 'exception'
}

const handleEdit = () => {
    Object.assign(editForm, userInfo.value)
    dialogVisible.value = true
}

const handleUpdate = async () => {
    if (!editFormRef.value) return
    
    try {
        await editFormRef.value.validate()
        updating.value = true
        
        const response = await userApi.updateUserInfo(editForm)
        if (response.data.code === 200) {
            ElMessage.success('更新成功')
            // 更新本地存储的用户信息
            userInfo.value = response.data.data
            localStorage.setItem('userInfo', JSON.stringify(response.data.data))
            dialogVisible.value = false
        }
    } catch (error) {
        ElMessage.error(error.response?.data?.message || '更新失败')
    } finally {
        updating.value = false
    }
}
</script>

<style scoped>
.home {
    max-width: 1400px;
    margin: 20px auto;
    padding: 0 20px;
    display: flex;
    gap: 20px;
}

.main-content {
    flex: 1;
}

.welcome-card {
    text-align: center;
    margin-bottom: 20px;
}

.button-group {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    gap: 15px;
}

.button-group .el-button {
    margin: 0 10px;
}

.user-info-sidebar {
    position: relative;
    transition: all 0.3s ease;
}

.avatar-trigger {
    position: relative;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    border-radius: 50%;
    transition: all 0.3s ease;
}

.avatar-trigger:hover {
    background-color: #f5f7fa;
}

.avatar-trigger .el-icon {
    font-size: 20px;
    color: #909399;
    transition: transform 0.3s ease;
}

.avatar-trigger .el-icon.expanded {
    transform: rotate(180deg);
}

.user-info-card {
    width: 300px;
    margin-top: 10px;
    transition: all 0.3s ease;
}

.user-info-sidebar.collapsed .user-info-card {
    display: none;
}

.user-info-content {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.info-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.info-item .label {
    font-size: 14px;
    color: #606266;
}

.home :deep(.el-card),
.home :deep(.recommended-offers) {
    transition: all 0.3s ease;
}

.el-progress {
    margin-top: 5px;
}

/* 添加展开/收起动画 */
.user-info-card {
    animation: slideIn 0.3s ease;
}

@keyframes slideIn {
    from {
        opacity: 0;
        transform: translateX(30px);
    }
    to {
        opacity: 1;
        transform: translateX(0);
    }
}

.user-info-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

:deep(.el-slider) {
    width: 100%;
}
</style> 