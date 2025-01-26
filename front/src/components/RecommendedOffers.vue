<template>
    <div class="recommended-offers">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <h2>推荐的 Offers</h2>
                    <div class="similarity-legend">
                        <el-tag type="success">匹配度: 80-100%</el-tag>
                        <el-tag type="warning">匹配度: 60-79%</el-tag>
                        <el-tag type="danger">匹配度: 0-59%</el-tag>
                    </div>
                </div>
            </template>
            
            <el-table :data="sortedOffers" style="width: 100%" v-loading="loading">
                <el-table-column prop="companyName" label="公司名称">
                    <template #default="scope">
                        <span :style="{ color: getMatchColor(scope.row.similarity) }">
                            {{ scope.row.companyName }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="薪资">
                    <template #default="scope">
                        <span :style="{ color: getMatchColor(scope.row.similarity) }">
                            {{ scope.row.salary }} {{ scope.row.salaryCurrency }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="jobTitle" label="职位">
                    <template #default="scope">
                        <span :style="{ color: getMatchColor(scope.row.similarity) }">
                            {{ scope.row.jobTitle }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="location" label="地点">
                    <template #default="scope">
                        <span :style="{ color: getMatchColor(scope.row.similarity) }">
                            {{ scope.row.location }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="industry" label="行业">
                    <template #default="scope">
                        <span :style="{ color: getMatchColor(scope.row.similarity) }">
                            {{ scope.row.industry }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="匹配度" width="180">
                    <template #default="scope">
                        <div class="similarity-cell" :style="{ backgroundColor: getSimilarityBgColor(scope.row.similarity) }">
                            <el-progress 
                                :percentage="getSimilarityPercentage(scope.row.similarity)"
                                :status="getSimilarityStatus(scope.row.similarity)"
                            />
                            <span class="similarity-text">
                                {{ getSimilarityPercentage(scope.row.similarity) }}%
                            </span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="工作评分" width="300">
                    <template #default="scope">
                        <div class="score-bars">
                            <el-tooltip content="工作生活平衡">
                                <el-progress 
                                    :percentage="scope.row.workLifeBalanceScore"
                                    :color="getColorForScore(scope.row.workLifeBalanceScore)"
                                />
                            </el-tooltip>
                            <el-tooltip content="薪资满意度">
                                <el-progress 
                                    :percentage="scope.row.salarySatisfactionScore"
                                    :color="getColorForScore(scope.row.salarySatisfactionScore)"
                                />
                            </el-tooltip>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[5, 10, 20, 50]"
                    :total="total"
                    layout="total, sizes, prev, pager, next"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { offerApi } from '../api/offer'
import { ElMessage } from 'element-plus'

const offers = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadOffers = async () => {
    try {
        loading.value = true
        const response = await offerApi.getRecommendedOffers(currentPage.value, pageSize.value)
        if (response.data.code === 200) {
            console.log('后端返回的完整数据：', response.data)
            const records = response.data.data.records
            console.log('处理前的记录：', records)
            
            // 确保每个记录都有 similarity 值
            offers.value = records.map(record => {
                if (record.similarity === undefined) {
                    console.warn('记录缺少 similarity 值：', record)
                    record.similarity = 0
                }
                return record
            })
            
            console.log('处理后的记录：', offers.value)
            total.value = response.data.data.total
        }
    } catch (error) {
        console.error('加载失败：', error)
        ElMessage.error('加载推荐失败')
    } finally {
        loading.value = false
    }
}

const handleSizeChange = (val) => {
    pageSize.value = val
    loadOffers()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    loadOffers()
}

const getMatchColor = (similarity) => {
    console.log('处理的 similarity 值：', similarity)
    
    // 确保 similarity 是有效数值
    if (similarity === undefined || similarity === null || isNaN(similarity)) {
        console.warn('无效的 similarity 值：', similarity)
        return '#F56C6C'  // 使用 Element Plus 的标准红色
    }
    
    // 将相似度转换为 0-100 的百分比
    const percentage = Math.max(0, Math.min(100, similarity * 100))
    console.log('计算后的百分比：', percentage)
    
    // 根据百分比返回颜色
    if (percentage >= 80) {
        return '#67C23A'  // Element Plus 的标准绿色
    } else if (percentage >= 60) {
        return '#E6A23C'  // Element Plus 的标准黄色
    } else {
        return '#F56C6C'  // Element Plus 的标准红色
    }
}

const getColorForScore = (score) => {
    if (score >= 80) return '#67C23A'
    if (score >= 60) return '#E6A23C'
    return '#F56C6C'
}

// 按匹配度排序的计算属性
const sortedOffers = computed(() => {
    return [...offers.value].sort((a, b) => (b.similarity || 0) - (a.similarity || 0))
})

// 获取匹配度百分比
const getSimilarityPercentage = (similarity) => {
    if (similarity === undefined || similarity === null) return 0
    return Math.round(similarity * 100)
}

// 获取匹配度状态
const getSimilarityStatus = (similarity) => {
    if (similarity >= 0.8) return 'success'
    if (similarity >= 0.6) return 'warning'
    return 'exception'
}

// 获取匹配度背景色
const getSimilarityBgColor = (similarity) => {
    if (similarity === undefined || similarity === null) return '#f5f7fa'
    if (similarity >= 0.8) return '#f0f9eb'
    if (similarity >= 0.6) return '#fdf6ec'
    return '#fef0f0'
}

onMounted(() => {
    loadOffers()
})
</script>

<style scoped>
.recommended-offers {
    margin: 20px 0;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.similarity-legend {
    display: flex;
    gap: 10px;
}
.score-bars {
    display: flex;
    flex-direction: column;
    gap: 5px;
}
.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
.similarity-cell {
    padding: 8px;
    border-radius: 4px;
}
.similarity-text {
    margin-left: 8px;
    font-weight: bold;
}
</style> 