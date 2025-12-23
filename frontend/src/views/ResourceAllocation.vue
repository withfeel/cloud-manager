<template>
  <div class="resource-allocation-container">
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
                  <span>云资源分配管理</span>
                </div>
              </template>
              <div class="search-section">
                <el-select v-model="searchParams.status" placeholder="请选择资源状态" style="width: 150px; margin-right: 10px;">
                  <el-option label="全部" value="" />
                  <el-option label="审批通过" value="1" />
                  <el-option label="已发放" value="3" />
                  <el-option label="已变更" value="4" />
                  <el-option label="已释放" value="5" />
                </el-select>
                <el-input
                  v-model="searchParams.keyword"
                  placeholder="请输入申请单号或项目名称"
                  style="width: 300px; margin-right: 10px;"
                  clearable
                >
                  <template #append>
                    <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
                  </template>
                </el-input>
              </div>
              <el-table :data="resourceList" stripe style="width: 100%" class="mt-20">
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
                <el-table-column label="操作" width="250">
                  <template #default="scope">
                    <el-button type="primary" size="small" @click="handleView(scope.row)">查看详情</el-button>
                    <el-button v-if="scope.row.status === 1" type="success" size="small" @click="handleAllocate(scope.row)">发放资源</el-button>
                    <el-button v-if="scope.row.status === 3" type="warning" size="small" @click="handleUpdate(scope.row)">变更资源</el-button>
                    <el-button v-if="scope.row.status === 3" type="danger" size="small" @click="handleRelease(scope.row)">释放资源</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="pagination-section">
                <el-pagination
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                  :current-page="currentPage"
                  :page-sizes="[10, 20, 50, 100]"
                  :page-size="pageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="total"
                />
              </div>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>
    
    <!-- 变更资源对话框 -->
    <el-dialog
      v-model="updateDialogVisible"
      title="变更资源"
      width="600px"
    >
      <el-form :model="updateForm" :rules="updateRules" ref="updateFormRef" label-width="120px">
        <el-form-item label="CPU核数" prop="totalCpu">
          <el-input-number
            v-model="updateForm.totalCpu"
            :min="1"
            :step="1"
            placeholder="请输入CPU核数"
          />
        </el-form-item>
        <el-form-item label="内存(GB)" prop="totalMemory">
          <el-input-number
            v-model="updateForm.totalMemory"
            :min="1"
            :step="1"
            placeholder="请输入内存大小"
          />
        </el-form-item>
        <el-form-item label="硬盘(GB)" prop="totalDisk">
          <el-input-number
            v-model="updateForm.totalDisk"
            :min="10"
            :step="10"
            placeholder="请输入硬盘大小"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="updateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmUpdate">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, ArrowDown, Home, DocumentAdd, Check, Setting, Money, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const activeMenu = ref('allocation')
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{"username": "admin"}'))

// 搜索参数
const searchParams = reactive({
  keyword: '',
  status: ''
})

// 表格数据
const resourceList = ref([
  {
    id: 1,
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
    id: 2,
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

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(2)

// 变更资源对话框
const updateDialogVisible = ref(false)
const updateFormRef = ref(null)
const selectedResource = ref(null)

const updateForm = reactive({
  totalCpu: 1,
  totalMemory: 2,
  totalDisk: 50
})

const updateRules = {
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
  ]
}

const handleSearch = () => {
  // 模拟搜索功能
  ElMessage.info('搜索功能已触发')
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (current) => {
  currentPage.value = current
}

const handleView = (row) => {
  // 查看详情功能
  ElMessage.info('查看详情功能已触发')
}

const handleAllocate = (row) => {
  // 模拟发放资源
  setTimeout(() => {
    ElMessage.success('资源发放成功')
    // 更新列表中的状态
    const index = resourceList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      resourceList.value[index].status = 3
    }
  }, 1000)
}

const handleUpdate = (row) => {
  selectedResource.value = row
  updateForm.totalCpu = row.totalCpu
  updateForm.totalMemory = row.totalMemory
  updateForm.totalDisk = row.totalDisk
  updateDialogVisible.value = true
}

const confirmUpdate = () => {
  updateFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟变更资源
      setTimeout(() => {
        ElMessage.success('资源变更成功')
        updateDialogVisible.value = false
        // 更新列表中的资源信息
        const index = resourceList.value.findIndex(item => item.id === selectedResource.value.id)
        if (index !== -1) {
          resourceList.value[index].totalCpu = updateForm.totalCpu
          resourceList.value[index].totalMemory = updateForm.totalMemory
          resourceList.value[index].totalDisk = updateForm.totalDisk
          resourceList.value[index].status = 4
        }
      }, 1000)
    }
  })
}

const handleRelease = (row) => {
  // 确认释放资源
  ElMessageBox.confirm('确定要释放该资源吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟释放资源
    setTimeout(() => {
      ElMessage.success('资源释放成功')
      // 更新列表中的状态
      const index = resourceList.value.findIndex(item => item.id === row.id)
      if (index !== -1) {
        resourceList.value[index].status = 5
      }
    }, 1000)
  }).catch(() => {
    ElMessage.info('已取消释放')
  })
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
  // 模拟获取资源列表
})
</script>

<style scoped>
.resource-allocation-container {
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

.search-section {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>