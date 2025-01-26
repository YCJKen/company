<template>
    <div class="home">
        <el-card class="welcome-card">
            <h1>欢迎来到主页</h1>
            <div class="button-group">
                <el-button type="primary" @click="$router.push('/offers')">
                    查看我的 Offers
                </el-button>
                <el-button type="success" @click="$router.push('/offers/create')">
                    创建新的 Offer
                </el-button>
                <el-button type="warning" @click="handleBackToLogin">
                    返回登录页
                </el-button>
            </div>
        </el-card>

        <!-- 添加推荐 Offers 组件 -->
        <RecommendedOffers />
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import RecommendedOffers from '../components/RecommendedOffers.vue'
import { removeToken } from '../utils/auth'

const router = useRouter()

const handleBackToLogin = async () => {
    removeToken()
    await router.push('/login')
}
</script>

<style scoped>
.home {
    max-width: 1200px;
    margin: 20px auto;
    padding: 0 20px;
}
.welcome-card {
    text-align: center;
    margin-bottom: 20px;
}
.button-group {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    gap: 15px;  /* 按钮之间的间距 */
}
.button-group .el-button {
    margin: 0 10px;
}
</style> 