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
            </div>
        </el-card>
    </div>
</template>

<script setup>
</script>

<style scoped>
.home {
    max-width: 800px;
    margin: 50px auto;
}
.welcome-card {
    text-align: center;
}
.button-group {
    margin-top: 20px;
}
.button-group .el-button {
    margin: 0 10px;
}
</style> 