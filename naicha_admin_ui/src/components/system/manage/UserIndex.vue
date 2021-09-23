<template>
  <div>
    <div style="text-align: left; margin: 5px 10px">
      <el-button size="mini" type="primary" @click="toAddUser" round >
        添加
      </el-button>
    </div>

    <!--增加或更新用户的表单弹窗-->
    <el-dialog :title=formTitle :visible.sync="userFormDialogVisible">
      <el-form ref="userForm" :model="userFormData" :rules="rules" size="medium" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userFormData.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="userFormData.sex" size="mini" style="float: left">
            <el-radio-button v-for="(item, index) in sexOptions"
                             :key="index" :label="item.value" :disabled="item.disabled">{{item.label}}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="handelConfirm">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!--选择角色弹窗-->
    <el-dialog title="设置角色" :visible.sync="showSetRoleDialog">
      <el-select  @change="setRole" value="选择角色" placeholder="选择角色">
        <el-option
          v-for="item in roles"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        >
        </el-option>
      </el-select>
    </el-dialog>

    <!--用户列表-->
    <div style="margin: 0px 10px;text-align: left;">
      <el-table
        :data="users.slice((currentPage-1)*pageSize, currentPage * pageSize)"
        stripe
        :default-sort="{prop: 'username', order: 'ascending'}">

        <el-table-column prop="username" label="用户名" sortable></el-table-column>
        <el-table-column  label="角色" prop="roleName">
        </el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-color="green"
              inactive-color="red"
              @change="(value) => commitStatusChange(value, scope.row)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作"  width="220">
          <template slot-scope="scope">
            <el-button @click="toSetRole(scope.row.id)" type="text" size="small">授权</el-button>
            <el-button @click="resetPassword(scope.row.id)" type="text" size="small">重置密码</el-button>
            <el-button @click="toEditUser(scope.row)" type="text" size="small">编辑</el-button>
            <el-button type="text" size="small" @click="deleteUser(scope.row.id)" >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <el-row style="float: right">
        <el-pagination
          @current-change="currentPage => this.currentPage = currentPage"
          @size-change="pageSize => this.pageSize = pageSize"
          :current-page.sync="currentPage"
          :page-sizes="[5, 10, 20, 30, 50, 100]"
          :page-size.sync="pageSize"
          layout=" total, sizes, prev, pager, next, jumper"
          :total="users.length">
        </el-pagination>
      </el-row>
    </div>
  </div>
</template>

<script>
  import {
    addSysUser,
    deleteSysUsers,
    getAllSysUsers,
    updateStatus,
    updateSysUser,
    resetPassword,
    setRole
  } from "../../../api/modules/system/user.js";
  import {getAllRoles} from "../../../api/modules/system/role";

  export default {
    data() {
      return {
        currentPage: 1,
        pageSize: 10,
        users: [],
        userFormDialogVisible: false,
        formTitle: '增加',
        userFormData: {
          username: ''
        },
        rules: {
          username: [{
            required: true,
            message: '请输入用户名',
            trigger: 'blur'
          }]
        },
        sexOptions: [{
          "label": "男",
          "value": 1
        }, {
          "label": "女",
          "value": 0
        }],
        currentSelectedUserId: null,
        roles: [],
        showSetRoleDialog: false, // 角色授权对话框
        currentSelectedRoleId: null
      }
    },
    mounted() {
      this.getAllUsers();
    },
    methods: {
      //获取所有用户
      getAllUsers() {
        getAllSysUsers().then(result => {
          this.users = result.data
        })
      },
      // 重置表单
      resetForm() {
        this.userFormData = { // 手动重置 不然数据被绑定在toEditUser时深克隆出来的对象
          username: ''
        }
      },
      // 增加用户
      toAddUser() {
        this.resetForm()
        this.userFormDialogVisible = true
        this.formTitle = '添加用户'
      },
      // 删除用户
      deleteUser(id) {
        let that = this;
        this.$confirm("提示", "删除角色", {}).then(() =>{
          deleteSysUsers(id).then(() => {
            this.$notify.success({
              title: '删除成功',
              duration: 500
            })
            that.getAllUsers();
          })
        }).catch(() => {})
      },
      // 编辑用户
      toEditUser(selectedUser) {
        // 深拷贝一个对象 不然在表格显示的数据会受到印象
        this.userFormData = JSON.parse(JSON.stringify(selectedUser));
        this.userFormDialogVisible = true
        this.formTitle = '更新用户'
      },
      // 提交表单
      handelConfirm() {
        this.$refs['userForm'].validate(valid => {
          console.log(this.formTitle)
          if (!valid)
            return

          this.userFormDialogVisible = false;
          if (this.formTitle.startsWith('添加')) {
            addSysUser(this.userFormData).then(result => {
              this.$notify({
                title: "添加成功",
                type: "success",
                duration: 1000
              });
              this.getAllUsers()
            })
          } else if (this.formTitle.startsWith('更新')) {
            updateSysUser(this.userFormData).then(result => {
              this.$notify({
                title: "更新成功",
                type: "success",
                duration: 1000
              });
              this.getAllUsers()
            })
          }
        })
      },
      // 冻结账号
      commitStatusChange(value, user) {
        this.$confirm(value === false ? '冻结用户?' : '激活用户').then(() => {
          updateStatus(user.id, user.status).then(() => {
            this.$notify.success(value === false ? "已冻结" : "已激活")
          }).catch(() => {
            user.status = !user.status;
          })
        }).catch(() => {
          user.status = !user.status;
        })
      },
      // 重置密码
      resetPassword(userId) {
        this.$confirm("重置该用户的密码?").then(() => {
          resetPassword(userId).then(() => {
            this.$notify.success('密码已重置')
          });
        }).catch(() => {})
      },
      toSetRole(userId){
        let that = this;
        getAllRoles().then(result => {
          that.roles = result.data;
          that.currentSelectedUserId = userId;
          that.showSetRoleDialog = true;
        })
      },
      // 角色授权
      setRole(roleId){
        this.showSetRoleDialog = false;
        let that = this;
        setRole(that.currentSelectedUserId, roleId).then(result => {
          that.$notify.success("设置成功");
          that.getAllUsers();
        })
      }
    }
  }
</script>
