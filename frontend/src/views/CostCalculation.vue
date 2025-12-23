<template>
  <div class="cost-calculation-container">
    <el-container>
      <el-header>
        <div class="header-left">
          <h2>政务云资源管理系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userInfo.username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
            @select="handleMenuSelect"
          >
            <el-menu-item index="dashboard">
              <el-icon><home /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="application">
              <el-icon><document-add /></el-icon>
              <span>资源申请</span>
            </el-menu-item>
            <el-menu-item index="approval">
              <el-icon><check /></el-icon>
              <span>审批管理</span>
            </el-menu-item>
            <el-menu-item index="allocation">
              <el-icon><setting /></el-icon>
              <span>资源分配</span>
            </el-menu-item>
            <el-menu-item index="cost">
              <el-icon><money /></el-icon>
              <span>费用核算</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <div class="main-content">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>云资源费用核算</span>
                </div>
              </template>
              
              <!-- 核算查询 -->
              <div class="calculation-section">
                <el-form :model="calculationForm" :rules="calculationRules" ref="calculationFormRef" label-width="120px" inline>
                  <el-form-item label="申请单号" prop="applicationId">
                    <el-select v-model="calculationForm.applicationId" placeholder="请选择申请单号" style="width: 200px;">
                      <el-option
                        v-for="item in applicationOptions"
                        :key="item.id"
                        :label="item.applicationNumber"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="核算年份" prop="year">
                    <el-input-number
                      v-model="calculationForm.year"
                      :min="2020"
                      :max="2030"
                      :step="1"
                      placeholder="请输入核算年份"
                      style="width: 150px;"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleCalculate" :loading="calculating">开始核算</el-button>
                  </el-form-item>
                </el-form>
              </div>
              
              <!-- 核算结果 -->
              <div v-if="calculationResult" class="result-section mt-20">
                <el-card shadow="hover" class="result-card">
                  <template #header>
                    <div class="result-header">
                      <span>核算结果</span>
                    </div>
                  </template>
                  <div class="result-content">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="result-item">
                          <label>申请单号：</label>
                          <span>{{ calculationResult.applicationNumber }}</span>
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="result-item">
                          <label>项目名称：</label>
                          <span>{{ calculationResult.projectName }}</span>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="result-item">
                          <label>核算年份：</label>
                          <span>{{ calculationResult.year }}年</span>
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="result-item">
                          <label>本年自然天数：</label>
                          <span>{{ calculationResult.totalDays }}天</span>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="result-item">
                          <label>每日单价：</label>
                          <span class="amount">¥{{ calculationResult.dailyUnitPrice }}</span>
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="result-item">
                          <label>年总费用：</label>
                          <span class="amount total">¥{{ calculationResult.totalCost }}</span>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </el-card>
              </div>
              
              <!-- 历史核算记录 -->
              <div class="history-section mt-20">
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>历史核算记录</span>
                    </div>
                  </template>
                  <el-table :data="historyRecords" stripe style="width: 100%">
                    <el-table-column prop="applicationNumber" label="申请单号" width="180" />
                    <el-table-column prop="projectName" label="项目名称" />
                    <el-table-column prop="year" label="核算年份" width="100" />
                    <el-table-column prop="totalDays" label="自然天数" width="100" />
                    <el-table-column prop="dailyUnitPrice" label="每日单价" width="120" formatter="formatPrice" />
                    <el-table-column prop="totalCost" label="年总费用" width="120" formatter="formatPrice" />
                    <el-table-column prop="calculationTime" label="核算时间" width="180" />
                  </el-table>
                </el-card>
              </div>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, ArrowDown, Home, DocumentAdd, Check, Setting, Money } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const activeMenu = ref('cost')
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{"username": "admin"}'))
const calculationFormRef = ref(null)
const calculating = ref(false)

// 核算表单
const calculationForm = reactive({
  applicationId: '',
  year: new Date().getFullYear()
})

const calculationRules = {
  applicationId: [
    { required: true, message: '请选择申请单号', trigger: 'blur' }
  ],
  year: [
    { required: true, message: '请输入核算年份', trigger: 'blur' },
    { type: 'number', min: 2020, max: 2030, message: '核算年份必须在2020-2030之间', trigger: 'blur' }
  ]
}

// 申请单号选项
const applicationOptions = ref([
  { id: 1, applicationNumber: 'APP123456789', projectName: '电子政务系统' },
  { id: 2, applicationNumber: 'APP987654321', projectName: '大数据分析平台' }
])

// 核算结果
const calculationResult = ref(null)

// 历史核算记录
const historyRecords = ref([
  {
    id: 1,
    applicationNumber: 'APP123456789',
    projectName: '电子政务系统',
    year: 2023,
    totalDays: 365,
    dailyUnitPrice: 123.45,
    totalCost: 45069.25,
    calculationTime: '2023-12-31 14:30:00'
  }
])

const handleCalculate = () => {
  calculationFormRef.value.validate((valid) => {
    if (valid) {
      calculating.value = true
      // 模拟费用核算
      setTimeout(() => {
        // 获取选中的申请信息
        const selectedApp = applicationOptions.value.find(item => item.id === calculationForm.applicationId)
        if (selectedApp) {
          // 模拟核算结果
          calculationResult.value = {
            applicationNumber: selectedApp.applicationNumber,
            projectName: selectedApp.projectName,
            year: calculationForm.year,
            totalDays: calculationForm.year % 4 === 0 ? 366 : 365,
            dailyUnitPrice: 156.78,
            totalCost: (calculationForm.year % 4 === 0 ? 366 : 365) * 156.78,
            calculationTime: new Date().toLocaleString()
          }
          // 添加到历史记录
          historyRecords.value.unshift({
            id: Date.now(),
            ...calculationResult.value
          })
        }
        ElMessage.success('费用核算完成')
        calculating.value = false
      }, 1500)
    }
  })
}

const formatPrice = (row, column, cellValue) => {
  return '¥' + cellValue.toFixed(2)
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
  switch(index) {
    case 'dashboard':
      router.push('/')
      break
    case 'application':
      router.push('/application')
      break
    case 'approval':
      router.push('/approval')
      break
    case 'allocation':
      router.push('/allocation')
      break
    case 'cost':
      router.push('/cost')
      break
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('退出登录成功')
  router.push('/login')
}

onMounted(() => {
  // 模拟获取申请列表和历史核算记录
})
</script>

<style scoped>
.cost-calculation-container {
  height: 100vh;
}

.el-header {
  background-color: #303133;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
}

.user-info {
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.el-aside {
  background-color: #545c64;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.calculation-section {
  margin-top: 20px;
}

.result-section {
  margin-top: 20px;
}

.result-card {
  border-left: 4px solid #409eff;
}

.result-header {
  font-weight: bold;
  font-size: 16px;
}

.result-content {
  padding: 20px 0;
}

.result-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.result-item label {
  width: 100px;
  font-weight: bold;
  color: #303133;
}

.result-item span {
  color: #606266;
}

.amount {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.amount.total {
  color: #f56c6c;
  font-size: 24px;
}

.mt-20 {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>