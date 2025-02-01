<template>
    <el-dialog
        :modelValue="visible"
        @update:modelValue="$emit('update:visible', $event)"
        :title="'与 ' + friend?.name + ' 的对话'"
        width="600px"
        destroy-on-close
    >
        <div class="chat-container" v-loading="loading">
            <div class="messages" ref="messagesRef">
                <template v-for="(group, date) in groupedMessages" :key="date">
                    <!-- 日期分割线 -->
                    <div class="message-date">
                        <span>{{ formatDate(date) }}</span>
                    </div>
                    <!-- 该日期下的消息 -->
                    <div 
                        v-for="message in group" 
                        :key="message.messageId" 
                        class="message"
                        :class="{ 'message-self': message.senderId === currentUserId }"
                    >
                        <div class="message-content">
                            <div class="message-text">{{ message.content }}</div>
                            <div class="message-time">{{ formatMessageTime(message.createdAt) }}</div>
                        </div>
                    </div>
                </template>
            </div>

            <div class="message-input">
                <el-input
                    v-model="messageText"
                    type="textarea"
                    :rows="3"
                    placeholder="输入消息..."
                    @keyup.enter.exact="handleSend"
                />
                <el-button 
                    type="primary" 
                    @click="handleSend"
                    :loading="sending"
                >
                    发送
                </el-button>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { messageApi } from '../api/message'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'  // 导入中文语言包

dayjs.locale('zh-cn')  // 使用中文

const props = defineProps({
    visible: Boolean,
    friend: Object
})

const emit = defineEmits(['update:visible'])

const currentUserId = ref(JSON.parse(localStorage.getItem('userInfo'))?.userId)
const messages = ref([])
const messageText = ref('')
const loading = ref(false)
const sending = ref(false)
const messagesRef = ref(null)

const loadMessages = async () => {
    if (!props.friend) return
    
    try {
        loading.value = true
        const response = await messageApi.getConversation(props.friend.userId)
        if (response.data.code === 200) {
            messages.value = response.data.data
            await nextTick()
            scrollToBottom()
        }
    } catch (error) {
        ElMessage.error('加载消息失败')
    } finally {
        loading.value = false
    }
}

const handleSend = async () => {
    if (!messageText.value.trim()) {
        ElMessage.warning('请输入消息内容')
        return
    }
    
    try {
        sending.value = true
        const response = await messageApi.sendMessage({
            receiverId: props.friend.userId,
            content: messageText.value
        })
        if (response.data.code === 200) {
            messages.value.push(response.data.data)
            messageText.value = ''
            await nextTick()
            scrollToBottom()
        }
    } catch (error) {
        ElMessage.error('发送失败')
    } finally {
        sending.value = false
    }
}

const scrollToBottom = () => {
    if (messagesRef.value) {
        messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
}

const formatTime = (time) => {
    return dayjs(time).format('MM-DD HH:mm')
}

// 格式化消息时间显示
const formatMessageTime = (time) => {
    if (!time) return '';
    // 处理后端返回的 LocalDateTime 格式
    // 如果时间字符串中没有 T，添加 T
    const formattedTime = time.includes('T') ? time : time.replace(' ', 'T');
    return dayjs(formattedTime).format('HH:mm');
}

// 格式化日期显示
const formatDate = (date) => {
    if (!date) return '';
    // 处理后端返回的 LocalDateTime 格式
    const formattedDate = date.includes('T') ? date : date.replace(' ', 'T');
    const messageDate = dayjs(formattedDate);
    const today = dayjs();
    const yesterday = dayjs().subtract(1, 'day');
    
    if (messageDate.isSame(today, 'day')) {
        return '今天';
    } else if (messageDate.isSame(yesterday, 'day')) {
        return '昨天';
    } else if (messageDate.isSame(today, 'year')) {
        return messageDate.format('M月D日');
    } else {
        return messageDate.format('YYYY年M月D日');
    }
}

// 修改消息分组方法
const groupedMessages = computed(() => {
    const groups = {};
    messages.value.forEach(message => {
        if (!message.createdAt) return;
        // 处理后端返回的 LocalDateTime 格式
        const formattedTime = message.createdAt.includes('T') 
            ? message.createdAt 
            : message.createdAt.replace(' ', 'T');
        const date = dayjs(formattedTime).format('YYYY-MM-DD');
        if (!groups[date]) {
            groups[date] = [];
        }
        groups[date].push(message);
    });
    return groups;
});

watch(() => props.visible, (newVal) => {
    if (newVal) {
        loadMessages()
    }
})

// 监听对话框关闭
watch(() => props.visible, (newVal) => {
    if (!newVal) {
        emit('update:visible', false)
    }
})
</script>

<style scoped>
.chat-container {
    height: 500px;
    display: flex;
    flex-direction: column;
}

.messages {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.message {
    display: flex;
    margin-bottom: 10px;
}

.message-self {
    flex-direction: row-reverse;
}

.message-date {
    text-align: center;
    margin: 20px 0;
    position: relative;
}

.message-date span {
    background-color: #f2f2f2;
    padding: 2px 10px;
    border-radius: 10px;
    font-size: 12px;
    color: #999;
}

.message-content {
    position: relative;
    max-width: 70%;
    padding: 10px;
    border-radius: 8px;
    background-color: #f4f4f5;
}

.message-self .message-content {
    background-color: #95ec69;  /* 仿微信绿色 */
}

.message-time {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
    text-align: right;
}

.message-text {
    word-break: break-word;
    white-space: pre-wrap;
    line-height: 1.4;
}

/* 添加消息气泡的小尾巴 */
.message-content::before {
    content: '';
    position: absolute;
    top: 15px;
    border: 6px solid transparent;
}

.message .message-content::before {
    left: -12px;
    border-right-color: #f4f4f5;
}

.message-self .message-content::before {
    right: -12px;
    left: auto;
    border-left-color: #95ec69;
    border-right-color: transparent;
}

/* 优化滚动条样式 */
.messages::-webkit-scrollbar {
    width: 6px;
}

.messages::-webkit-scrollbar-thumb {
    background-color: #ddd;
    border-radius: 3px;
}

.messages::-webkit-scrollbar-track {
    background-color: transparent;
}

.message-input {
    padding: 20px;
    border-top: 1px solid #dcdfe6;
    display: flex;
    gap: 10px;
}

.message-input .el-input {
    flex: 1;
}
</style> 