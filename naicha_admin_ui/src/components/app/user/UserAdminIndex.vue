<template>
  <div>
    <div style="text-align: left; margin: 5px 10px">
    </div>

    <!--增加或更新的表单弹窗-->
    <el-dialog :title=formTitle :visible.sync="formDialogVisible">
      <el-form ref="form" :model="formData" :rules="rules" size="large" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="手机号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="handelConfirm">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!--列表-->
    <div style="margin: 0px 10px;text-align: left;">
      <el-table
        :data="userAdmins"
        stripe
        :default-sort="{prop: 'name', order: 'ascending'}">
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="phone" label="手机号"></el-table-column>
        <el-table-column prop="wxAvatar" label="微信头像">
          <template slot-scope="scope">
            <img :src="scope.row.wxAvatar" alt="" style="width: 50px; height: 50px; border-radius: 50%;">
          </template>
        </el-table-column>
        <!-- 账号状态-->
        <el-table-column label="账号状态">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-color="green"
              inactive-color="red"
              @change="(value) => commitStatusChange(value, scope.row)">
            </el-switch>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" size="mini"  @click="toEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="mini"  @click="toDelete(scope.row.wxOpenid)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <el-row style="float: right">
        <el-pagination
          @current-change="changePageNo"
          @size-change="changePageSize"
          :current-page.sync="searchParams.pageNo"
          :page-sizes="[5, 10, 20, 30, 50, 100]"
          :page-size.sync="searchParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </el-row>

    </div>
  </div>
</template>

<script>
  import {addUserAdmin, deleteUserAdmins, getUserAdmins, updateUserAdmin} from "@/api/modules/app/userAdminApi.js";

  export default {
    name: "userAdminComponent",
    data() {
      return {
        // 查询参数
        searchParams: {
          searchParam1: null,
          pageNo: 1,
          pageSize: 10,
        },
        total: 0,
        userAdmins: [],
        formDialogVisible: false,
        formTitle: '添加',
        formData: {
          wxOpenid: null, name: null, phone: null, sex: null, wxAvatar: null, status: null
        },
        rules: {
          name: [
            {required: true, message: '姓名不能为空', trigger: 'blur'}
          ], phone: [
            {required: true, message: '手机号不能为空', trigger: 'blur'}
          ]
        }
      }
    },
    mounted() {
      this.getUserAdmins()
    },
    methods: {
      // 获取数据
      getUserAdmins() {
        let that = this;
        getUserAdmins(that.searchParams.pageNo, that.searchParams.pageSize).then(result => {
          that.userAdmins = result.data.records;
          that.total = result.data.total;
        })
      },
      // 切换页数
      changePageNo(pageNo) {
        this.searchParams.pageNo = pageNo;
        this.getUserAdmins()
      },
      // 改变页面大小
      changePageSize(pageSize) {
        this.searchParams.pageSize = pageSize;
        this.getUserAdmins()
      },
      // 清除查询参数
      clearSearchParams() {
        this.searchParams.searchParam1 = null;
      },
      // 重置表单
      resetForm() {
        // 手动重置 不然数据被绑定在toEdit时深克隆出来的对象
        this.formData = {
          wxOpenid: null, name: null, phone: null, sex: null, wxAvatar: null, status: null
        }
      },
      // 增加
      toAdd() {
        this.resetForm()
        this.formDialogVisible = true
        this.formTitle = '添加'
      },
      // 删除
      toDelete(id) {
        let that = this;
        this.$confirm("提示", "是否删除", {}).then(() => {
          deleteUserAdmins([id]).then(() => {
            this.$notify.success("删除成功");
            that.getUserAdmins();
          })
        }).catch(() => {
        })
      },
      // 编辑
      toEdit(selectedUserAdmin) {
        // 深拷贝一个对象 不然在表格显示的数据会受到印象
        this.formData = JSON.parse(JSON.stringify(selectedUserAdmin));
        this.formDialogVisible = true
        this.formTitle = '更新'
      },
      // 提交表单
      handelConfirm() {
        this.$refs['form'].validate(valid => {
          console.log(this.formTitle)
          if (!valid)
            return

          this.formDialogVisible = false;
          if (this.formTitle.startsWith('添加')) {
            addUserAdmin(this.formData).then(result => {
              this.$notify.success("添加成功");
              this.getUserAdmins()
            })
          } else if (this.formTitle.startsWith('更新')) {
            updateUserAdmin(this.formData).then(result => {
              this.$notify.success("更新成功");
              this.getUserAdmins()
            })
          }
        })
      },
      // 改变状态
      commitStatusChange(value, userAdmin) {
        this.$confirm(value === false ? '冻结？' : '激活？').then(() => {
          let obj = {
            wxOpenid: userAdmin.wxOpenid,
            status: userAdmin.status
          }
          updateUserAdmin(obj).then(() => {
            this.$notify.success(value === false ? "已冻结" : "已激活")
          }).catch(() => {
            userAdmin.status = !userAdmin.status;
          })
        }).catch(() => {
          userAdmin.status = !userAdmin.status;
        })
      },
    }
  }
</script>
