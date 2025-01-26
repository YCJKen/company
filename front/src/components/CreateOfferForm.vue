<template>
  <el-form :model="offerForm" :rules="rules" ref="offerFormRef" label-width="120px">
    <el-form-item label="公司名称" prop="companyName">
      <el-input v-model="offerForm.companyName" placeholder="请输入公司名称"></el-input>
    </el-form-item>
    
    <el-form-item label="薪资" prop="salary">
      <el-input-number v-model="offerForm.salary" :min="0" :precision="2"></el-input-number>
      <el-select v-model="offerForm.salaryCurrency" style="margin-left: 10px">
        <el-option label="CNY" value="CNY"></el-option>
        <el-option label="USD" value="USD"></el-option>
      </el-select>
    </el-form-item>
    
    <el-form-item label="职位名称" prop="jobTitle">
      <el-input v-model="offerForm.jobTitle" placeholder="请输入职位名称"></el-input>
    </el-form-item>
    
    <el-form-item label="行业" prop="industry">
      <el-select v-model="offerForm.industry" placeholder="请选择行业">
        <el-option label="科技" value="Tech"></el-option>
        <el-option label="金融" value="Finance"></el-option>
        <el-option label="医疗" value="Healthcare"></el-option>
        <el-option label="教育" value="Education"></el-option>
      </el-select>
    </el-form-item>
    
    <el-form-item label="工作地点" prop="location">
      <el-input v-model="offerForm.location" placeholder="请输入工作地点"></el-input>
    </el-form-item>
    
    <el-form-item label="工作生活平衡" prop="workLifeBalanceScore">
      <el-rate
        v-model="offerForm.workLifeBalanceScore"
        :max="10"
        :texts="['极差', '很差', '差', '一般', '还行', '良好', '不错', '很好', '极好', '完美']"
        show-text
      ></el-rate>
    </el-form-item>
    
    <el-form-item label="薪资满意度" prop="salarySatisfactionScore">
      <el-rate
        v-model="offerForm.salarySatisfactionScore"
        :max="10"
        :texts="['极差', '很差', '差', '一般', '还行', '良好', '不错', '很好', '极好', '完美']"
        show-text
      ></el-rate>
    </el-form-item>
    
    <el-form-item label="每日工作时长" prop="workingHoursScore">
      <el-input-number 
        v-model="offerForm.workingHoursScore" 
        :min="4" 
        :max="24"
        :step="0.5"
        :precision="1"
      >
        <template #suffix>小时/天</template>
      </el-input-number>
    </el-form-item>
    
    <el-form-item label="每周加班时长" prop="overtimeHoursScore">
      <el-input-number 
        v-model="offerForm.overtimeHoursScore" 
        :min="0" 
        :max="60"
        :step="0.5"
        :precision="1"
      >
        <template #suffix>小时/周</template>
      </el-input-number>
    </el-form-item>
    
    <el-form-item label="Offer日期" prop="offerDate">
      <el-date-picker
        v-model="offerForm.offerDate"
        type="date"
        placeholder="选择日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
      ></el-date-picker>
    </el-form-item>
    
    <el-form-item>
      <el-button type="primary" @click="submitForm">创建 Offer</el-button>
      <el-button @click="resetForm">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { offerApi } from '../api/offer'

const offerFormRef = ref(null)

const offerForm = reactive({
  companyName: '',
  salary: 0,
  salaryCurrency: 'CNY',
  jobTitle: '',
  industry: '',
  location: '',
  workLifeBalanceScore: 5,
  salarySatisfactionScore: 5,
  workingHoursScore: 8,  // 默认8小时工作制
  overtimeHoursScore: 0, // 默认无加班
  offerDate: ''
})

const rules = {
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  salary: [{ required: true, message: '请输入薪资', trigger: 'blur' }],
  jobTitle: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业', trigger: 'change' }],
  location: [{ required: true, message: '请输入工作地点', trigger: 'blur' }],
  workingHoursScore: [
    { required: true, message: '请输入工作时长', trigger: 'blur' },
    { type: 'number', min: 4, max: 24, message: '工作时长必须在4-24小时之间', trigger: 'blur' }
  ],
  overtimeHoursScore: [
    { required: true, message: '请输入加班时长', trigger: 'blur' },
    { type: 'number', min: 0, max: 60, message: '加班时长必须在0-60小时之间', trigger: 'blur' }
  ],
  offerDate: [{ required: true, message: '请选择Offer日期', trigger: 'change' }]
}

const submitForm = async () => {
  if (!offerFormRef.value) return
  
  await offerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await offerApi.createOffer(offerForm)
        ElMessage.success('Offer创建成功')
        resetForm()
      } catch (error) {
        ElMessage.error(error.message || 'Offer创建失败')
      }
    }
  })
}

const resetForm = () => {
  if (offerFormRef.value) {
    offerFormRef.value.resetFields()
  }
}
</script>

<style scoped>
.el-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.el-input-number {
  width: 180px;
}

.el-select {
  width: 100%;
}

.el-rate {
  margin-top: 8px;
}
</style> 