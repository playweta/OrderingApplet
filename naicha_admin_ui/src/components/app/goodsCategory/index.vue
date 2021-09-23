<template>
  <div>
    <div style="text-align: left; margin: 5px 10px">
      <el-button size="mini" type="primary" @click="toAdd" round>
        添加
      </el-button>
    </div>

    <!--增加或更新的表单弹窗-->
    <el-dialog :title=formTitle :visible.sync="formDialogVisible">
      <el-form ref="form" :model="formData" :rules="rules" size="large" label-width="100px">
        <el-form-item label="商品类别" prop="name">
          <el-input v-model="formData.name" placeholder="输入商品类别名称"></el-input>
        </el-form-item>
        <el-form-item label="显示顺序" prop="displayOrder">
          <el-input v-model="formData.displayOrder" placeholder="输入显示顺序"></el-input>
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
        :data="goodsCategoryAdmins"
        stripe
        :default-sort="{prop: 'displayOrder', order: 'ascending'}">

        <el-table-column prop="name" label="商品类别名称" sortable></el-table-column>
        <el-table-column prop="displayOrder" label="显示顺序" sortable></el-table-column>

        <el-table-column label="是否显示该类商品">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.showStatus"
              active-color="green"
              inactive-color="red"
              @change="(value) => commitStatusChange(value, scope.row)">
            </el-switch>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="130">
          <template slot-scope="scope">
            <el-button type="text"  size="mini" @click="toEdit(scope.row)">编辑</el-button>
            <el-button type="text"  size="mini"  @click="toDelete(scope.row.name)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
  import {
    addGoodsCategoryAdmin,
    deleteGoodsCategoryAdmins,
    getAllGoodsCategoryAdmins,
    updateGoodsCategoryAdmin,
    updateGoodsCategoryShowStatus
  } from "@/api/modules/app/goodsCategoryAdminApi.js";

  export default {
    name: "goodsCategoryAdminComponent",
    data() {
      return {
        goodsCategoryAdmins: [],
        formDialogVisible: false,
        formTitle: '添加',
        formData: {
          id: null, name: null, displayOrder: null, showStatus: 1
        },
        rules: {
          name: [
            {required: true, message: '不能为空', trigger: 'blur'}
          ], displayOrder: [
            {required: true, message: '显示顺序不能为空', trigger: 'blur'}
          ], showStatus: [
            {required: true, message: '是否显示不能为空', trigger: 'blur'}
          ]
        }
      }
    },
    mounted() {
      this.getAllGoodsCategoryAdmins()
    },
    methods: {
      // 获取数据
      getAllGoodsCategoryAdmins() {
        let that = this;
        getAllGoodsCategoryAdmins().then(result => {
          that.goodsCategoryAdmins = result.data;
        })
      },
      // 重置表单
      resetForm() {
        // 手动重置 不然数据被绑定在toEdit时深克隆出来的对象
        this.formData = {
          name: null, displayOrder: null, showStatus: 1
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
        this.$confirm("提示", "对应的商品也会一并删除，确定删除吗？", {}).then(() => {
          deleteGoodsCategoryAdmins(id).then(() => {
            this.$notify.success("删除成功");
            that.getAllGoodsCategoryAdmins();
          })
        }).catch(() => {
        })
      },
      // 编辑
      toEdit(selectedGoodsCategoryAdmin) {
        // 深拷贝一个对象 不然在表格显示的数据会受到印象
        this.formData = JSON.parse(JSON.stringify(selectedGoodsCategoryAdmin));
        this.formDialogVisible = true
        this.formData.oldName = selectedGoodsCategoryAdmin.name
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
            addGoodsCategoryAdmin(this.formData).then(result => {
              this.$notify.success("添加成功");
              this.getAllGoodsCategoryAdmins()
            })
          } else if (this.formTitle.startsWith('更新')) {
            updateGoodsCategoryAdmin(this.formData.oldName, this.formData).then(result => {
              this.$notify.success("更新成功");
              this.getAllGoodsCategoryAdmins()
            })
          }
        })
      },
      // 显示该类商品或隐藏该类商品
      commitStatusChange(value, goodsCategoryAdmin) {
        this.$confirm(value === false ? '是否隐藏该类商品？' : '是否显示该类商品？').then(() => {
          updateGoodsCategoryShowStatus(goodsCategoryAdmin.name).then(() => {
            this.$notify.success(value === false ? "已隐藏" : "已开启显示")
          }).catch(() => {
            goodsCategoryAdmin.showStatus = !goodsCategoryAdmin.showStatus;
          })
        }).catch(() => {
          goodsCategoryAdmin.showStatus = !goodsCategoryAdmin.showStatus;
        })
      },
    }
  }
</script>
