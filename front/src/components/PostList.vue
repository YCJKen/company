<template>
    <div class="post-list">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <h2>社区动态</h2>
                    </div>
                    <div class="header-right">
                        <el-button type="primary" @click="showCreateDialog = true">
                            发布新帖子
                        </el-button>
                        <el-button @click="$router.push('/home')">返回主页</el-button>
                    </div>
                </div>
            </template>

            <div class="posts" v-loading="loading">
                <el-card v-for="post in posts" :key="post.postId" class="post-card">
                    <template #header>
                        <div class="post-header">
                            <div class="user-info">
                                <el-avatar 
                                    :size="40" 
                                    :icon="UserFilled"
                                    @click="showUserInfo(post.user)"
                                    class="clickable-avatar"
                                />
                                <span class="username">{{ post.user?.name || '用户' }}</span>
                            </div>
                            <el-dropdown v-if="post.userId === currentUserId">
                                <el-button link>
                                    <el-icon><More /></el-icon>
                                </el-button>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="handleDelete(post.postId)">
                                            删除
                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>

                    <div class="post-content">
                        <div class="text">{{ post.content }}</div>
                        <div v-if="post.imageUrls && post.imageUrls.length > 0" class="images">
                            <el-image
                                v-for="(url, index) in post.imageUrls"
                                :key="index"
                                :src="getFullImageUrl(url)"
                                :preview-src-list="getFullImageUrlList(post.imageUrls)"
                                fit="cover"
                            />
                        </div>
                    </div>

                    <div class="post-footer">
                        <div class="actions">
                            <el-button 
                                :type="post.isLiked ? 'primary' : 'default'"
                                link
                                @click="handleLike(post)"
                            >
                                <el-icon><Star /></el-icon>
                                {{ post.likesCount || 0 }}
                            </el-button>
                            <el-button link @click="showComments(post)">
                                <el-icon><ChatDotRound /></el-icon>
                                {{ post.commentsCount || 0 }}
                            </el-button>
                        </div>
                        <span class="time">{{ formatTime(post.createdAt) }}</span>
                    </div>
                </el-card>

                <el-pagination
                    v-if="total > 0"
                    class="pagination"
                    :current-page="currentPage"
                    :page-size="pageSize"
                    :total="total"
                    @current-change="handlePageChange"
                />
            </div>
        </el-card>

        <!-- 发帖对话框 -->
        <el-dialog
            v-model="showCreateDialog"
            title="发布帖子"
            width="500px"
        >
            <el-form
                ref="createFormRef"
                :model="createForm"
                :rules="rules"
            >
                <el-form-item prop="content">
                    <el-input
                        v-model="createForm.content"
                        type="textarea"
                        :rows="4"
                        placeholder="写点什么..."
                    />
                </el-form-item>

                <el-form-item>
                    <el-upload
                        action="#"
                        list-type="picture-card"
                        :auto-upload="false"
                        :on-change="handleImageChange"
                        :on-remove="handleImageRemove"
                        :limit="9"
                        ref="uploadRef"
                    >
                        <el-icon><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="showCreateDialog = false">取消</el-button>
                    <el-button type="primary" @click="handleCreatePost" :loading="creating">
                        发布
                    </el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 添加用户信息对话框 -->
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
                <div class="actions" v-if="selectedUser.userId !== currentUserId">
                    <el-button 
                        type="primary" 
                        @click="sendFriendRequest(selectedUser.userId)"
                    >
                        添加好友
                    </el-button>
                    <el-button 
                        type="success" 
                        @click="openChat(selectedUser)"
                    >
                        发送消息
                    </el-button>
                </div>
            </div>
        </el-dialog>

        <!-- 添加评论对话框 -->
        <el-dialog
            v-model="showCommentDialog"
            :title="'评论'"
            width="500px"
        >
            <div class="comments-container" v-loading="loadingComments">
                <!-- 评论列表 -->
                <div class="comments-list">
                    <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
                        <div class="comment-header">
                            <div class="user-info">
                                <el-avatar :size="32" :icon="UserFilled" />
                                <span class="username">{{ comment.user?.name || '用户' }}</span>
                            </div>
                            <el-button 
                                v-if="comment.userId === currentUserId"
                                type="danger" 
                                link
                                @click="handleDeleteComment(comment.commentId)"
                            >
                                删除
                            </el-button>
                        </div>
                        <div class="comment-content">{{ comment.content }}</div>
                        <div class="comment-footer">
                            <span class="time">{{ formatTime(comment.createdAt) }}</span>
                        </div>
                    </div>
                </div>

                <!-- 评论输入框 -->
                <div class="comment-input">
                    <el-input
                        v-model="commentForm.content"
                        type="textarea"
                        :rows="2"
                        placeholder="写下你的评论..."
                    />
                    <el-button 
                        type="primary" 
                        @click="handleComment"
                        :loading="commenting"
                    >
                        发表评论
                    </el-button>
                </div>
            </div>
        </el-dialog>

        <!-- 聊天对话框 -->
        <ChatDialog
            v-model:visible="showChatDialog"
            :friend="selectedUser"
        />
    </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Star, ChatDotRound, More, Plus } from '@element-plus/icons-vue'
import { postApi } from '../api/post'
import { userApi } from '../api/user'
import { uploadApi } from '../api/upload'
import dayjs from 'dayjs'
import ChatDialog from './ChatDialog.vue'

const loading = ref(false)
const creating = ref(false)
const showCreateDialog = ref(false)
const showUserDialog = ref(false)
const showCommentDialog = ref(false)
const loadingComments = ref(false)
const commenting = ref(false)
const posts = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const currentUserId = ref(JSON.parse(localStorage.getItem('userInfo'))?.userId)
const selectedUser = ref(null)
const comments = ref([])
const currentPostId = ref(null)
const showChatDialog = ref(false)

const uploadRef = ref(null)
const createForm = ref({
    content: '',
    images: []
})

const rules = {
    content: [
        { required: true, message: '请输入内容', trigger: 'blur' },
        { max: 1000, message: '内容长度不能超过1000字', trigger: 'blur' }
    ]
}

const commentForm = reactive({
    content: '',
    postId: null,
    parentId: null
})

const loadPosts = async () => {
    try {
        loading.value = true
        const response = await postApi.getPosts(currentPage.value, pageSize.value)
        if (response.data.code === 200) {
            posts.value = response.data.data.records
            total.value = response.data.data.total
        }
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handleCreatePost = async () => {
    if (!createForm.value.content.trim()) {
        ElMessage.warning('请输入内容')
        return
    }
    
    try {
        creating.value = true
        const imageUrls = []
        
        if (createForm.value.images && createForm.value.images.length > 0) {
            for (const file of createForm.value.images) {
                try {
                    const formData = new FormData()
                    formData.append('file', file.raw)
                    
                    const response = await uploadApi.uploadFile(formData)
                    if (response.data.code === 200 && response.data.data) {
                        const imageUrl = response.data.data.startsWith('http') 
                            ? response.data.data 
                            : `http://localhost:8080${response.data.data}`
                        imageUrls.push(imageUrl)
                    }
                } catch {
                    ElMessage.error('部分图片上传失败')
                }
            }
        }
        
        const response = await postApi.createPost({
            content: createForm.value.content,
            imageUrls: imageUrls
        })
        
        if (response.data.code === 200) {
            ElMessage.success('发布成功')
            createForm.value.content = ''
            createForm.value.images = []
            if (uploadRef.value) {
                uploadRef.value.clearFiles()
            }
            showCreateDialog.value = false
            await loadPosts()
        }
    } catch {
        ElMessage.error('发布失败')
    } finally {
        creating.value = false
    }
}

const handleLike = async (post) => {
    try {
        if (post.isLiked) {
            await postApi.unlikePost(post.postId)
            post.likesCount--
        } else {
            await postApi.likePost(post.postId)
            post.likesCount++
        }
        post.isLiked = !post.isLiked
    } catch (error) {
        ElMessage.error('操作失败')
    }
}

const handleDelete = async (postId) => {
    try {
        await ElMessageBox.confirm('确定要删除这条帖子吗？', '提示', {
            type: 'warning'
        })
        await postApi.deletePost(postId)
        ElMessage.success('删除成功')
        loadPosts()
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

const handlePageChange = (page) => {
    currentPage.value = page
    loadPosts()
}

const handleImageChange = (uploadFile) => {
    if (uploadFile && uploadFile.raw) {
        if (!createForm.value.images) {
            createForm.value.images = []
        }
        createForm.value.images.push(uploadFile)
    }
}

const handleImageRemove = (uploadFile) => {
    const index = createForm.value.images.findIndex(f => f.uid === uploadFile.uid)
    if (index !== -1) {
        createForm.value.images.splice(index, 1)
    }
}

const formatTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const showUserInfo = (user) => {
    if (user) {
        selectedUser.value = user
        showUserDialog.value = true
    }
}

const format = (percentage) => {
    return percentage + '分'
}

const getProgressStatus = (score) => {
    if (score >= 80) return 'success'
    if (score >= 60) return 'warning'
    return 'exception'
}

const showComments = async (post) => {
    currentPostId.value = post.postId
    commentForm.postId = post.postId
    showCommentDialog.value = true
    await loadComments()
}

const loadComments = async () => {
    if (!currentPostId.value) return
    
    try {
        loadingComments.value = true
        const response = await postApi.getComments(currentPostId.value)
        if (response.data.code === 200) {
            comments.value = response.data.data
        }
    } catch (error) {
        ElMessage.error('加载评论失败')
    } finally {
        loadingComments.value = false
    }
}

const handleComment = async () => {
    if (!commentForm.content.trim()) {
        ElMessage.warning('请输入评论内容')
        return
    }
    
    try {
        commenting.value = true
        const response = await postApi.createComment(commentForm)
        if (response.data.code === 200) {
            ElMessage.success('评论成功')
            commentForm.content = ''
            await loadComments()
            // 更新帖子评论数
            const post = posts.value.find(p => p.postId === currentPostId.value)
            if (post) {
                post.commentsCount++
            }
        }
    } catch (error) {
        ElMessage.error('评论失败')
    } finally {
        commenting.value = false
    }
}

const handleDeleteComment = async (commentId) => {
    try {
        await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
            type: 'warning'
        })
        await postApi.deleteComment(commentId)
        ElMessage.success('删除成功')
        await loadComments()
        // 更新帖子评论数
        const post = posts.value.find(p => p.postId === currentPostId.value)
        if (post) {
            post.commentsCount--
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

const sendFriendRequest = async (friendId) => {
    try {
        await userApi.sendFriendRequest(friendId)
        ElMessage.success('好友请求已发送')
        showUserDialog.value = false
    } catch (error) {
        ElMessage.error('发送请求失败')
    }
}

const openChat = (user) => {
    showUserDialog.value = false
    showChatDialog.value = true
}

// 添加处理图片URL的方法
const getFullImageUrl = (url) => {
    if (!url) return '';
    // 如果已经是完整URL，直接返回
    if (url.startsWith('http')) return url;
    // 否则添加域名
    return `http://localhost:8080${url}`;
}

const getFullImageUrlList = (urls) => {
    if (!urls || !Array.isArray(urls)) return [];
    return urls.map(url => getFullImageUrl(url));
}

onMounted(() => {
    loadPosts()
})
</script>

<style scoped>
.post-list {
    max-width: 800px;
    margin: 0 auto;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-right {
    display: flex;
    gap: 10px;  /* 按钮之间的间距 */
}

.header-left {
    display: flex;
    align-items: center;
}

.post-card {
    margin-bottom: 20px;
}

.post-header {
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

.post-content {
    padding: 10px 0;
}

.text {
    white-space: pre-wrap;
    margin-bottom: 10px;
}

.images {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
}

.images .el-image {
    width: 100%;
    height: 150px;
    border-radius: 4px;
}

.post-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}

.actions {
    display: flex;
    gap: 20px;
}

.time {
    color: #999;
    font-size: 14px;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.clickable-avatar {
    cursor: pointer;
    transition: transform 0.2s;
}

.clickable-avatar:hover {
    transform: scale(1.1);
}

.user-info-content {
    padding: 20px;
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

.comments-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.comments-list {
    max-height: 400px;
    overflow-y: auto;
}

.comment-item {
    padding: 10px;
    border-bottom: 1px solid #eee;
}

.comment-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.comment-content {
    margin: 8px 0;
    white-space: pre-wrap;
}

.comment-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #999;
}

.comment-input {
    display: flex;
    gap: 10px;
    align-items: flex-start;
}

.comment-input .el-input {
    flex: 1;
}

.actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.el-upload--picture-card {
    width: 100px;
    height: 100px;
    line-height: 100px;
}

.el-upload-list--picture-card .el-upload-list__item {
    width: 100px;
    height: 100px;
}
</style> 