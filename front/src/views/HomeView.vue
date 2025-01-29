<template>
    <div class="home">
        <el-card class="welcome-card">
            <h1>{{ showOfferContent ? 'OfferShow' : '欢迎来到主页' }}</h1>
            <div class="button-group">
                <template v-if="!showOfferContent">
                    <el-button type="primary" @click="handleShowOffer">
                        OfferShow
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
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import RecommendedOffers from '../components/RecommendedOffers.vue'
import { removeToken } from '../utils/auth'

const router = useRouter()
const showOfferContent = ref(false)

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

.home :deep(.el-card),
.home :deep(.recommended-offers) {
    transition: all 0.3s ease;
}
</style> 