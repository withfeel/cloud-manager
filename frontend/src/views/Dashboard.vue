<template>
  <div class="dashboard-container">
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
            <div class="stats-cards">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-number">128</div>
                  <div class="stat-label">总申请数</div>
                </div>
                <div class="stat-icon">
                  <el-icon><document /></el-icon>
                </div>
              </el-card>
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-number">86</div>
                  <div class="stat-label">已审批</div>
                </div>
                <div class="stat-icon">
                  <el-icon><check-circle /></el-icon>
                </div>
              </el-card>
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-number">64</div>
                  <div class="stat-label">已发放</div>
                </div>
                <div class="stat-icon">
                  <el-icon><success /></el-icon>
                </div>
              </el-card>
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-number">¥128,000</div>
                  <div class="stat-label">本年费用</div>
                </div>
                <div class="stat-icon">
                  <el-icon><money /></el-icon>
                </div>
              </el-card>
            </div>
            
            <div class="recent-applications">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>最近申请</span>
                    <el-button type="primary" size="small">查看全部</el-button>
                  </div>
                </template>
                <el-table :data="recentApplications" stripe style="width: 100%">
                  <el-table-column prop="applicationNumber" label="申请单号" width="180" />
                  <el-table-column prop="organization" label="申请单位" />
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
                </el-table>
              </el-card>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, ArrowDown, Home, DocumentAdd, Check, Setting, Money, Document, CheckCircle, Success } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = ref('dashboard')
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{"username": "admin"}'))

const recentApplications = ref([
  {
    applicationNumber: 'APP123456789',
    organization: '某政府部门',
    projectName: '电子政务系统',
    totalCpu: 16,
    totalMemory: 32,
    totalDisk: 500,
    status: 1,
    applyTime: '2024-01-15 14:30:00'
  },
  {
    applicationNumber: 'APP987654321',
    organization: '某事业单位',
    projectName: '大数据分析平台',
    totalCpu: 32,
    totalMemory: 128,
    totalDisk: 2000,
    status: 3,
    applyTime: '2024-01-14 10:15:00'
  }
])

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
  // 模拟获取最近申请数据
})
</script>

<style scoped>
.dashboard-container {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-icon {
  font-size: 48px;
  color: #409eff;
  opacity: 0.6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recent-applications {
  margin-top: 20px;
}
</style>