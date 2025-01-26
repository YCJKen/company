<template>
    <div class="offer-list">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <h2>我的 Offers</h2>
                    </div>
                    <div class="header-right">
                        <el-button type="primary" @click="$router.push('/offers/create')">创建 Offer</el-button>
                        <el-button @click="$router.push('/home')">返回主页</el-button>
                    </div>
                </div>
            </template>
            
            <el-table :data="offers" style="width: 100%" v-loading="loading">
                <el-table-column prop="companyName" label="公司名称" />
                <el-table-column label="薪资">
                    <template #default="scope">
                        {{ scope.row.salary }} {{ scope.row.salaryCurrency }}
                    </template>
                </el-table-column>
                <el-table-column prop="jobTitle" label="职位" />
                <el-table-column prop="location" label="地点" />
                <el-table-column prop="industry" label="行业" />
                <el-table-column prop="offerDate" label="录取日期" />
                <el-table-column label="操作" width="120">
                    <template #default="scope">
                        <el-button 
                            type="danger" 
                            size="small" 
                            @click="handleDelete(scope.row.offerId)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { offerApi } from '../api/offer'
import { ElMessage, ElMessageBox } from 'element-plus'

const offers = ref([])
const loading = ref(false)

const loadOffers = async () => {
    try {
        loading.value = true
        const response = await offerApi.getUserOffers()
        if (response.data.code === 200) {
            offers.value = response.data.data
        }
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handleDelete = async (offerId) => {
    try {
        await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
            type: 'warning'
        })
        
        const response = await offerApi.deleteOffer(offerId)
        if (response.data.code === 200) {
            ElMessage.success('删除成功')
            loadOffers()
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

onMounted(() => {
    loadOffers()
})
</script>

<style scoped>
.offer-list {
    max-width: 1200px;
    margin: 20px auto;
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
.header-right .el-button {
    margin-left: 10px;
}
</style> 