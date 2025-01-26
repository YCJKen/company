<template>
    <div class="nav-bar">
        <el-menu
            mode="horizontal"
            :ellipsis="false"
            class="menu"
        >
            <el-menu-item index="1" @click="$router.push('/home')">
                <el-icon><House /></el-icon>
                首页
            </el-menu-item>
            
            <div class="flex-grow" />
            
            <el-sub-menu index="2">
                <template #title>
                    <el-icon><User /></el-icon>
                    {{ userName }}
                </template>
                <el-menu-item index="2-1" @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                </el-menu-item>
            </el-sub-menu>
        </el-menu>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { House, User, SwitchButton } from '@element-plus/icons-vue'
import { logout } from '../utils/auth'

// 这里可以从 Vuex 或其他状态管理中获取用户名
const userName = ref(localStorage.getItem('userName') || '当前用户')

const handleLogout = () => {
    logout()
}
</script>

<style scoped>
.nav-bar {
    border-bottom: solid 1px var(--el-menu-border-color);
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    background-color: white;
}
.menu {
    padding: 0 20px;
}
.flex-grow {
    flex-grow: 1;
}
</style> 