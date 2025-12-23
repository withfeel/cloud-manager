<template>
  <div class="resource-application-container">
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
                  <span>云资源申请</span>
                </div>
              </template>
              <el-form
                :model="applicationForm"
                :rules="applicationRules"
                ref="applicationFormRef"
                label-width="120px"
                class="application-form"
              >
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="申请单位" prop="organization">
                      <el-input v-model="applicationForm.organization" placeholder="请输入申请单位" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="项目名称" prop="projectName">
                      <el-input v-model="applicationForm.projectName" placeholder="请输入项目名称" />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="20">
                  <el-col :span="8">
                    <el-form-item label="CPU核数" prop="totalCpu">
                      <el-input-number
                        v-model="applicationForm.totalCpu"
                        :min="1"
                        :step="1"
                        placeholder="请输入CPU核数"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="内存(GB)" prop="totalMemory">
                      <el-input-number
                        v-model="applicationForm.totalMemory"
                        :min="1"
                        :step="1"
                        placeholder="请输入内存大小"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="硬盘(GB)" prop="totalDisk">
                      <el-input-number
                        v-model="applicationForm.totalDisk"
                        :min="1"
                        :step="10"
                        placeholder="请输入硬盘大小"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item label="申请理由" prop="applicationReason">
                  <el-input
                    v-model="applicationForm.applicationReason"
                    type="textarea"
                    :rows="4"
                    placeholder="请详细说明申请理由"
                  />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="handleSubmit" :loading="submitting">提交申请</el-button>
                  <el-button @click="handleReset">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
            
            <el-card class="mt-20">
              <template #header>
                <div class="card-header">
                  <span>我的申请记录</span>
                </div>
              </template>
              <el-table :data="applicationRecords" stripe style="width: 100%">
                <el-table-column prop="applicationNumber" label="申请单号" width="180" />
                <el-table-column prop="projectName" label="项目名称" />
                <el-table-column prop="totalCpu" label="CPU(核)" width="80" />
                <el-table-column prop="totalMemory" label="内存(GB)" width="100" />
                <el-table-column prop="totalDisk" label="硬盘(GB)" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">
                      {{ getStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="applyTime" label="申请时间" width="180" />
                <el-table-column label="操作" width="150">
                  <template #default="scope">
                    <el-button type="primary" size="small">查看详情</el-button>
                  </template>
                </el-table-column>
              </el-table>
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
const activeMenu = ref('application')
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{"username": "admin"}'))
const applicationFormRef = ref(null)
const submitting = ref(false)

const applicationForm = reactive({
  organization: '',
  projectName: '',
  totalCpu: 1,
  totalMemory: 2,
  totalDisk: 50,
  applicationReason: ''
})

const applicationRules = {
  organization: [
    { required: true, message: '请输入申请单位', trigger: 'blur' }
  ],
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  totalCpu: [
    { required: true, message: '请输入CPU核数', trigger: 'blur' },
    { type: 'number', min: 1, message: 'CPU核数不能小于1', trigger: 'blur' }
  ],
  totalMemory: [
    { required: true, message: '请输入内存大小', trigger: 'blur' },
    { type: 'number', min: 1, message: '内存大小不能小于1GB', trigger: 'blur' }
  ],
  totalDisk: [
    { required: true, message: '请输入硬盘大小', trigger: 'blur' },
    { type: 'number', min: 10, message: '硬盘大小不能小于10GB', trigger: 'blur' }
  ],
  applicationReason: [
    { required: true, message: '请输入申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由不能少于10个字符', trigger: 'blur' }
  ]
}

const applicationRecords = ref([
  {
    id: 1,
    applicationNumber: 'APP123456789',
    projectName: '电子政务系统',
    totalCpu: 16,
    totalMemory: 32,
    totalDisk: 500,
    status: 1,
    applyTime: '2024-01-15 14:30:00'
  },
  {
    id: 2,
    applicationNumber: 'APP987654321',
    projectName: '大数据分析平台',
    totalCpu: 32,
    totalMemory: 128,
    totalDisk: 2000,
    status: 3,
    applyTime: '2024-01-14 10:15:00'
  }
])

const handleSubmit = () => {
  applicationFormRef.value.validate((valid) => {
    if (valid) {
      submitting.value = true
      // 模拟提交申请
      setTimeout(() => {
        ElMessage.success('申请提交成功')
        handleReset()
        submitting.value = false
        // 模拟更新申请记录
        applicationRecords.value.push({
          id: Date.now(),
          applicationNumber: 'APP' + Date.now(),
          projectName: applicationForm.projectName,
          totalCpu: applicationForm.totalCpu,
          totalMemory: applicationForm.totalMemory,
          totalDisk: applicationForm.totalDisk,
          status: 0,
          applyTime: new Date().toLocaleString()
        })
      }, 1000)
    }
  })
}

const handleReset = () => {
  applicationFormRef.value.resetFields()
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

const getStatusType = (status) => {
  switch(status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'primary'
    case 4: return 'info'
    case 5: return 'info'
    default: return ''
  }
}

const getStatusText = (status) => {
  switch(status) {
    case 0: return '待审批'
    case 1: return '审批通过'
    case 2: return '审批拒绝'
    case 3: return '已发放'
    case 4: return '已变更'
    case 5: return '已释放'
    default: return ''
  }
}

onMounted(() => {
  // 模拟获取申请记录
})
</script>

<style scoped>
.resource-application-container {
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

.application-form {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-20 {
  margin-top: 20px;
}
</style>