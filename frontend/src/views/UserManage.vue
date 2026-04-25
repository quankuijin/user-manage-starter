<template>
  <div>
    <header class="header-container">
      <h1 class="header-title">用户管理系统</h1>
      <div class="header-user-info">
        <span class="header-username">欢迎，{{ username }}</span>
        <el-button type="danger" text @click="handleLogout" style="color: #fff;">
          退出登录
        </el-button>
      </div>
    </header>

    <div class="content-container">
      <div class="toolbar-container">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索姓名/编码/电话"
            style="width: 300px;"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </div>

      <div class="table-container">
        <el-table :data="tableData" border stripe style="width: 100%;">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="code" label="编码" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="gender" label="性别" width="80" />
          <el-table-column prop="birthDate" label="出生日期" width="120">
            <template #default="scope">
              {{ formatDate(scope.row.birthDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="电话" width="130" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入用户编码" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="选择出生日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="2"
            placeholder="请输入地址"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="400px"
    >
      <p>确定要删除用户「{{ deleteUser?.name }}」吗？</p>
      <p style="color: #999; margin-top: 10px; font-size: 12px;">此操作不可恢复，请谨慎操作。</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" :loading="deleteLoading" @click="confirmDelete">
            确定删除
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '管理员')
const searchKeyword = ref('')
const tableData = ref([])
const allUsers = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const deleteDialogVisible = ref(false)
const deleteUser = ref(null)
const deleteLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  code: '',
  name: '',
  gender: '男',
  birthDate: '',
  phone: '',
  email: '',
  address: '',
  remark: ''
})

const rules = {
  code: [
    { required: true, message: '请输入用户编码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const filteredData = computed(() => {
  if (!searchKeyword.value) {
    return allUsers.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return allUsers.value.filter(user =>
    user.name?.toLowerCase().includes(keyword) ||
    user.code?.toLowerCase().includes(keyword) ||
    user.phone?.includes(keyword)
  )
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  try {
    const res = await request.get('/users')
    allUsers.value = res.data || []
    total.value = filteredData.value.length
    tableData.value = paginatedData.value
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  total.value = filteredData.value.length
  tableData.value = paginatedData.value
}

const handleSizeChange = (val) => {
  pageSize.value = val
  tableData.value = paginatedData.value
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  tableData.value = paginatedData.value
}

const resetForm = () => {
  form.id = null
  form.code = ''
  form.name = ''
  form.gender = '男'
  form.birthDate = ''
  form.phone = ''
  form.email = ''
  form.address = ''
  form.remark = ''
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.code = row.code
  form.name = row.name
  form.gender = row.gender || '男'
  form.birthDate = row.birthDate || ''
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.address = row.address || ''
  form.remark = row.remark || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      const userData = { ...form }
      if (isEdit.value) {
        await request.put(`/users/${form.id}`, userData)
        ElMessage.success('更新成功')
      } else {
        await request.post('/users', userData)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadUsers()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  deleteUser.value = row
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  if (!deleteUser.value) return
  
  deleteLoading.value = true
  try {
    await request.delete(`/users/${deleteUser.value.id}`)
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error('删除失败:', error)
  } finally {
    deleteLoading.value = false
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
  ElMessage.success('已退出登录')
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}
</script>

<style scoped>
</style>
