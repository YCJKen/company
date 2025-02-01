<template>
    <div class="friend-list">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <el-button 
                            type="primary" 
                            plain
                            icon="ArrowLeft"
                            @click="$router.push('/home')"
                        >
                            返回主页
                        </el-button>
                        <h3>我的好友</h3>
                    </div>
                    <div class="header-right">
                        <el-button type="primary" @click="showSearchDialog = true">添加好友</el-button>
                    </div>
                </div>
            </template>

            <div class="friend-requests" v-if="friendRequests.length > 0">
                <h4>好友请求</h4>
                <el-card v-for="request in friendRequests" :key="request.id" class="request-card">
                    <div class="request-item">
                        <div class="user-info">
                            <el-avatar :size="32" :icon="UserFilled" />
                            <span class="username">{{ request.friend?.name }}</span>
                        </div>
                        <div class="actions">
                            <el-button type="success" size="small" @click="handleRequest(request.userId, true)">
                                接受
                            </el-button>
                            <el-button type="danger" size="small" @click="handleRequest(request.userId, false)">
                                拒绝
                            </el-button>
                        </div>
                    </div>
                </el-card>
            </div>

            <div class="friends">
                <el-empty v-if="friends.length === 0" description="暂无好友" />
                <el-card v-for="friend in friends" :key="friend.userId" class="friend-card">
                    <div class="friend-item">
                        <div class="user-info">
                            <el-avatar :size="40" :icon="UserFilled" @click="showUserInfo(friend)" />
                            <span class="username">{{ friend.name }}</span>
                        </div>
                        <div class="actions">
                            <el-button type="primary" size="small" @click="openChat(friend)">
                                发送消息
                            </el-button>
                        </div>
                    </div>
                </el-card>
            </div>
        </el-card>

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
        <el-dialog
            v-model="showUserDialog"
            :title="selectedUser?.name + ' 的个人信息'"
            width="400px"
        >
            <div class="user-info-content" v-if="selectedUser">
                <div class="info-item">
                    <span class="label">目标行业：</span>
                    <el-tag>{{ selectedUser.targetIndustry || '未设置' }}</el-tag>
                </div>
                <div class="info-item">
                    <span class="label">期望地点：</span>
                    <el-tag type="success">{{ selectedUser.preferredLocation || '未设置' }}</el-tag>
                </div>
                <div class="info-item">
                    <span class="label">期望薪资：</span>
                    <el-tag type="warning">{{ selectedUser.salaryScore || '0' }} 元/月</el-tag>
                </div>
                <div class="info-item">
                    <span class="label">工作时长期望：</span>
                    <el-tag type="info">{{ selectedUser.workingHoursScore || '0' }} 小时/天</el-tag>
                </div>
                <div class="info-item">
                    <span class="label">工作生活平衡期望：</span>
                    <el-progress 
                        :percentage="selectedUser.workLifeBalanceScore || 0"
                        :format="format"
                        :status="getProgressStatus(selectedUser.workLifeBalanceScore)"
                    />
                </div>
                <div class="info-item">
                    <span class="label">加班接受程度：</span>
                    <el-progress 
                        :percentage="selectedUser.overtimeHoursScore || 0"
                        :format="format"
                        :status="getProgressStatus(selectedUser.overtimeHoursScore)"
                    />
                </div>
            </div>
        </el-dialog>

        <!-- 聊天对话框组件 -->
        <ChatDialog
            v-model:visible="showChatDialog"
            :friend="selectedFriend"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, ArrowLeft } from '@element-plus/icons-vue'
import { userApi } from '../api/user'
import ChatDialog from './ChatDialog.vue'

const currentUserId = ref(JSON.parse(localStorage.getItem('userInfo'))?.userId)
const friends = ref([])
const friendRequests = ref([])
const showSearchDialog = ref(false)
const showUserDialog = ref(false)
const searchKeyword = ref('')
const searchResults = ref([])
const searching = ref(false)
const selectedUser = ref(null)
const showChatDialog = ref(false)
const selectedFriend = ref(null)

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

const sendRequest = async (friendId) => {
    try {
        await userApi.sendFriendRequest(friendId)
        ElMessage.success('好友请求已发送')
        showSearchDialog.value = false
    } catch (error) {
        ElMessage.error('发送请求失败')
    }
}

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

const showUserInfo = (user) => {
    selectedUser.value = user
    showUserDialog.value = true
}

const format = (percentage) => {
    return percentage + '分'
}

const getProgressStatus = (score) => {
    if (score >= 80) return 'success'
    if (score >= 60) return 'warning'
    return 'exception'
}

const openChat = (friend) => {
    selectedFriend.value = friend
    showChatDialog.value = true
}

onMounted(() => {
    loadFriends()
    loadFriendRequests()
})
</script>

<style scoped>
.friend-list {
    max-width: 800px;
    margin: 0 auto;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 20px;
}

.header-left h3 {
    margin: 0;
}

.header-right {
    display: flex;
    gap: 10px;
}

.friend-requests {
    margin-bottom: 20px;
}

.request-card,
.friend-card {
    margin-bottom: 10px;
}

.request-item,
.friend-item,
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

.actions {
    display: flex;
    gap: 10px;
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

.info-item {
    margin-bottom: 15px;
}

.info-item .label {
    display: block;
    margin-bottom: 5px;
    color: #606266;
    font-size: 14px;
}

.info-item .el-tag {
    margin-right: 8px;
}
</style> 