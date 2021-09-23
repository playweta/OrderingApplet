<template>
  <!--添加或新增角色弹窗-->
  <div style="text-align: left; margin: 5px 10px">
    <el-button size="mini"  type="primary" @click="toAdd" round>添加</el-button>
    <el-dialog :title=formTitle :visible.sync="dialogVisible">
      <el-form ref="elForm" :model="formData" :rules="rules" size="medium" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" :maxlength="20" :style="{width: '100%'}">
          </el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" placeholder="请输入描述" :style="{width: '100%'}"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="handelConfirm">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!--角色授权对话框-->
    <el-dialog title="角色授权" :visible.sync="showSetPermissionDialog">
      <el-form>
        <el-form-item>
          <!--树形控件 配置角色菜单-->
          <el-tree
                  :data="permissionTree"
                  show-checkbox
                  node-key="permission"
                  :props="defaultProps"
                  :default-expanded-keys="['system:app']"
                  :default-checked-keys="permissions"
                  ref="permissionTree">
          </el-tree>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetChecked">清空</el-button>
          <el-button type="primary" @click="setPermissions">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>


    <!--角色列表-->
    <div style="margin: 5px 0px;text-align: left;">
      <el-table :data="roles" stripe
                :default-sort="{prop: 'name', order: 'ascending'}"
                :max-height="700">
        <el-table-column prop="name" label="角色名称" sortable></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
<!--            <el-button @click="toSetPermissions(scope.row)" type="text" size="mini">授权</el-button>-->
<!--            <el-button @click="edit(scope.row)" type="text" size="mini">编辑</el-button>-->
<!--            <el-button type="text" size="mini" @click="delRoles(scope.row.id)">删除</el-button>-->

            <el-button @click="toSetPermissions(scope.row)" type="text" size="small" >授权</el-button>
            <el-button @click="edit(scope.row)" type="text" size="small"  >编辑</el-button>
            <el-button @click="delRoles(scope.row.id)" type="text" size="mini"   >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
  import {deleteRoles, getAllRoles, updateRole, updateRolePermission} from '../../../api/modules/system/role.js'
  import {addRole, getPermissionsOfRole} from "../../../api/modules/system/role";
  import permissionTree from "../../../router/permissionTree.js"

  export default {
    dialogVisible: false,
    data() {
      return {
        formTitle: '添加角色',
        dialogVisible: false,
        showSetPermissionDialog: false, // 角色授权对话框
        roles: [],
        formData: {
          name: "",
          description: "",
        },
        rules: {
          name: [{
            required: true,
            message: '请输入角色名称',
            trigger: 'blur'
          }],
          description: [],
        },
        permissionTree: permissionTree,
        defaultProps: { // 树形控件值映射规则
          id: 'title', // 作为key
          label: 'title',
          children: 'children'
        },
        permissions: []
      }
    },
    mounted() {
      this._getAllRoles()
    },
    methods: {
      _getAllRoles() { //
        getAllRoles().then(result => {
          this.roles = result.data
        })
      },
      toAdd() {
        this.dialogVisible = true;
        this.resetForm()
        this.formTitle = '添加角色';
      },
      edit(selectedRole) {
        // 深拷贝一个对象 不然在表格显示的数据会受到印象
        this.formData = JSON.parse(JSON.stringify(selectedRole));
        this.dialogVisible = true
        this.formTitle = '更新角色'
      },
      // 重置表单
      resetForm() {
        this.formData = {
          name: "",
          description: ""
        };
      },
      // 更新角色信息
      handelConfirm() {
        this.dialogVisible = false;
        if (this.formTitle === "添加角色") {
          addRole(this.formData).then(() => {
            this._getAllRoles();
          })
        } else {
          updateRole(this.formData).then(() => {
            this._getAllRoles();
          })
        }
      },
      // 打开角色授权对话框
      toSetPermissions(selectedRole) {
        let that = this;
        that.resetChecked();
        that.formData = JSON.parse(JSON.stringify(selectedRole));
        that.showSetPermissionDialog = true;
        that.formTitle = '更新角色';
        getPermissionsOfRole(selectedRole.id).then(result => {
          if(result.data.indexOf("*") >= 0){ // 是超级管理员
            let keys = [];
            that.permissionTree.forEach(item => {
              keys.push(item.permission);
            })
            that.permissions = keys;
            console.log(keys)
          }else {
            that.permissions = result.data;
          }
        })
      },
      resetChecked() {
        if(this.$refs['permissionTree'])
          this.$refs['permissionTree'].setCheckedKeys([]);
      },
      setPermissions() {
        this.showSetPermissionDialog = false;
        let nodes = this.$refs.permissionTree.getCheckedNodes(false) // 获取被选中的节点 (是否只是叶子节点)
        let permissions = [];
        nodes.forEach(node => {
          if (node.permission) {
            permissions.push(node.permission);
          }
        })
        let vo = {
          roleId: this.formData.id,
          permissions: permissions
        }
        console.log("[设置角色权限] ", vo);
        updateRolePermission(vo).then(result => {
          this.$notify.success('操作成功')
        })
      },
      // 删除角色
      delRoles(id) {
        let that = this;
        this.$confirm("提示", "删除角色", {}).then(() => {
          deleteRoles([id]).then(() => {
            this.$notify.success('删除成功')
            that._getAllRoles();
          })
        }).catch(() => {
        })
      }
    }
  }


</script>
<style>
</style>
