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
                        <el-button type="info" @click="$router.push('/friends')">
                            好友列表
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

        <!-- 修改用户信息侧边栏 -->
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

                <!-- 用户基本信息 -->
                <div class="user-info-section">
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

                <!-- 添加好友列表部分 -->
                <div class="friends-section">
                    <div class="section-header">
                        <h4>我的好友</h4>
                        <el-button type="primary" link @click="showSearchDialog = true">
                            <el-icon><Plus /></el-icon>
                            添加好友
                        </el-button>
                    </div>

                    <!-- 好友请求 -->
                    <div class="friend-requests" v-if="friendRequests.length > 0">
                        <div class="section-subheader">
                            <span>好友请求</span>
                            <el-badge :value="friendRequests.length" type="warning" />
                        </div>
                        <div class="request-list">
                            <div v-for="request in friendRequests" 
                                :key="request.id" 
                                class="request-item"
                            >
                                <div class="user-brief">
                                    <el-avatar :size="32" :icon="UserFilled" />
                                    <span>{{ request.friend?.name }}</span>
                                </div>
                                <div class="request-actions">
                                    <el-button type="success" size="small" @click="handleRequest(request.userId, true)">
                                        接受
                                    </el-button>
                                    <el-button type="danger" size="small" @click="handleRequest(request.userId, false)">
                                        拒绝
                                    </el-button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 好友列表 -->
                    <div class="friends-list">
                        <el-empty v-if="friends.length === 0" description="暂无好友" />
                        <div v-for="friend in friends" 
                            :key="friend.userId" 
                            class="friend-item"
                        >
                            <div class="user-brief" @click="showUserInfo(friend)">
                                <el-avatar :size="40" :icon="UserFilled" />
                                <span>{{ friend.name }}</span>
                            </div>
                            <el-button 
                                type="primary" 
                                size="small" 
                                @click="openChat(friend)"
                            >
                                聊天
                            </el-button>
                        </div>
                    </div>
                </div>
            </el-card>
        </div>

        <!-- 搜索用户对话框 -->
        <el-dialog v-model="showSearchDialog" title="添加好友" width="500px">
            <div class="search-container">
                <el-input
                    v-model="searchKeyword"
                    placeholder="输入用户名或邮箱搜索"
                    @keyup.enter="searchUsers"
                >
                    <template #append>
                        <el-button @click="searchUsers">搜索</el-button>
                    </template>
                </el-input>

                <div class="search-results" v-loading="searching">
                    <el-card v-for="user in searchResults" :key="user.userId" class="user-card">
                        <div class="user-item">
                            <div class="user-info">
                                <el-avatar :size="32" :icon="UserFilled" />
                                <span class="username">{{ user.name }}</span>
                            </div>
                            <el-button 
                                type="primary" 
                                size="small"
                                @click="sendRequest(user.userId)"
                                :disabled="user.userId === currentUserId"
                            >
                                添加好友
                            </el-button>
                        </div>
                    </el-card>
                </div>
            </div>
        </el-dialog>

        <!-- 用户信息对话框 -->
        <el-dialog v-model="showUserDialog" :title="selectedUser?.name + ' 的个人信息'" width="400px">
            <!-- ... 用户信息对话框内容保持不变 ... -->
        </el-dialog>

        <!-- 聊天对话框 -->
        <ChatDialog v-model:visible="showChatDialog" :friend="selectedFriend" />
    </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, ArrowRight, Edit, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import RecommendedOffers from '../components/RecommendedOffers.vue'
import { removeToken } from '../utils/auth'
import { userApi } from '../api/user'
import ChatDialog from '../components/ChatDialog.vue'

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

// 添加好友相关的数据
const friends = ref([])
const friendRequests = ref([])
const showSearchDialog = ref(false)
const showUserDialog = ref(false)
const showChatDialog = ref(false)
const selectedUser = ref(null)
const selectedFriend = ref(null)
const searchKeyword = ref('')
const searchResults = ref([])
const searching = ref(false)
const currentUserId = ref(JSON.parse(localStorage.getItem('userInfo'))?.userId)

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
    loadFriends()
    loadFriendRequests()
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

// 加载好友列表
const loadFriends = async () => {
    try {
        const response = await userApi.getFriends()
        if (response.data.code === 200) {
            friends.value = response.data.data
        }
    } catch (error) {
        ElMessage.error('加载好友列表失败')
    }
}

// 加载好友请求
const loadFriendRequests = async () => {
    try {
        const response = await userApi.getFriendRequests()
        if (response.data.code === 200) {
            friendRequests.value = response.data.data
        }
    } catch (error) {
        ElMessage.error('加载好友请求失败')
    }
}

// 处理好友请求
const handleRequest = async (friendId, accept) => {
    try {
        await userApi.handleFriendRequest(friendId, accept)
        ElMessage.success(accept ? '已接受好友请求' : '已拒绝好友请求')
        await loadFriendRequests()
        if (accept) {
            await loadFriends()
        }
    } catch (error) {
        ElMessage.error('操作失败')
    }
}

// 打开聊天窗口
const openChat = (friend) => {
    selectedFriend.value = friend
    showChatDialog.value = true
}

// 显示用户信息
const showUserInfo = (user) => {
    selectedUser.value = user
    showUserDialog.value = true
}

// 搜索用户
const searchUsers = async () => {
    if (!searchKeyword.value.trim()) {
        ElMessage.warning('请输入搜索关键词')
        return
    }
    
    try {
        searching.value = true
        const response = await userApi.searchUsers(searchKeyword.value)
        if (response.data.code === 200) {
            searchResults.value = response.data.data
        }
    } catch (error) {
        ElMessage.error('搜索失败')
    } finally {
        searching.value = false
    }
}

// 发送好友请求
const sendRequest = async (friendId) => {
    try {
        await userApi.sendFriendRequest(friendId)
        ElMessage.success('好友请求已发送')
        showSearchDialog.value = false
    } catch (error) {
        ElMessage.error('发送请求失败')
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

.friends-section {
    margin-top: 20px;
    border-top: 1px solid #eee;
    padding-top: 20px;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.section-header h4 {
    margin: 0;
    color: #303133;
}

.section-subheader {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
    color: #606266;
    font-size: 14px;
}

.request-list {
    max-height: 200px;
    overflow-y: auto;
}

.request-item {
    padding: 8px;
    border-radius: 4px;
    background-color: #f5f7fa;
    margin-bottom: 8px;
}

.request-actions {
    display: flex;
    gap: 8px;
    margin-top: 8px;
}

.friends-list {
    margin-top: 15px;
    max-height: 400px;
    overflow-y: auto;
}

.friend-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border-radius: 4px;
    transition: background-color 0.3s;
    cursor: pointer;
}

.friend-item:hover {
    background-color: #f5f7fa;
}

.user-brief {
    display: flex;
    align-items: center;
    gap: 10px;
}

.user-brief span {
    font-weight: 500;
}

/* 优化滚动条样式 */
.friends-list::-webkit-scrollbar,
.request-list::-webkit-scrollbar {
    width: 6px;
}

.friends-list::-webkit-scrollbar-thumb,
.request-list::-webkit-scrollbar-thumb {
    background-color: #ddd;
    border-radius: 3px;
}

.friends-list::-webkit-scrollbar-track,
.request-list::-webkit-scrollbar-track {
    background-color: transparent;
}

.search-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.search-results {
    max-height: 400px;
    overflow-y: auto;
}

.user-card {
    margin-bottom: 10px;
}

.user-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
}

.username {
    font-weight: bold;
}

/* 优化搜索结果滚动条 */
.search-results::-webkit-scrollbar {
    width: 6px;
}

.search-results::-webkit-scrollbar-thumb {
    background-color: #ddd;
    border-radius: 3px;
}

.search-results::-webkit-scrollbar-track {
    background-color: transparent;
}
</style> 