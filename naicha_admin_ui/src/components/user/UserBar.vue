<template>
  <!--用户相关弹框 嵌套到NavMenu里去-->
  <div>
    <div v-if="!userInfo">
      <router-link to="/">
        登录
      </router-link>
    </div>
    <div v-else>
      <div>
        <el-dropdown :show-timeout=10 :hide-timeout=10
                     placement="right"
                     trigger="click"
                     @command="handleCommand">
          <!--用户信息弹窗开关 下拉菜单-->
          <div>
            <img v-if="userInfo.avatar" style="width: 35px; height: 35px; border-radius: 50%"
                 :src="imageBaseUrl + userInfo.avatar" alt="img">
            <img v-else style="width: 35px; height: 35px; border-radius: 50%"
                 src="../../assets/images/avatar.png" alt="img">
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command=1>我的资料</el-dropdown-item>
            <el-dropdown-item command=2>修改密码</el-dropdown-item>
            <el-dropdown-item command=3 divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <span style="color: gray; margin-bottom: 10px;font-size: 10px">
          {{userInfo.college ? userInfo.college.name : ''}} [{{userInfo.roleName}}]
        </span>
      </div>


      <!--个人信息弹窗-->
      <el-dialog
              :visible="userInfoDialogVisible"
              :before-close="handleClose"
              :center="true"
              width="500px"
              top="10vh"
              :show-close="false">
        <div style="text-align: center; padding-bottom: 30px">
          <div>
            <!--上传图片-->
            <el-upload
                    :action="userAvatarUploadUrl + '?token=' + $store.getters.token"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload">
              <img v-if="userInfo.avatar" :src="imageBaseUrl + userInfo.avatar"
                   style="width: 60px; height: 60px;border-radius: 50%"
                   alt="用户头像">
              <div v-else style="border:1px solid lightgray; color: lightgray; height: 60px;">+上传头像</div>
            </el-upload>
          </div>
          <div style="text-align: left; margin: 20px;">
            <div>用户名: {{userInfo.username}}</div>
            <div>性别: {{userInfo.sex == 1 ? '男' : '女'}}</div>
            <div>手机: </div>
            <div>邮箱: </div>
          </div>
        </div>
      </el-dialog>

      <!--修改密码弹窗-->
      <el-dialog :visible.sync="updatePasswordDialogVisible" width="400px">
        <el-form ref="elForm" :model="updatePasswordForm" :rules="updatePasswordFormRules" size="medium"
                 label-width="100px">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="updatePasswordForm.oldPassword" type="password" placeholder="请输入旧密码" :maxlength="18"
                      :style="{width: '100%'}"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="updatePasswordForm.newPassword" type="password" placeholder="请输入新密码" :maxlength="18"
                      :style="{width: '100%'}"></el-input>
          </el-form-item>
        </el-form>
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
      </el-dialog>
    </div>

  </div>
</template>

<script>
  import {getUserInfo, updatePassword} from "../../api/AuthController";

  export default {
    data() {
      return {
        userInfo: {},
        userInfoDialogVisible: false,
        updatePasswordDialogVisible: false,

        //修改密码
        updatePasswordForm: {
          oldPassword: '',
          newPassword: '',
        },
        updatePasswordFormRules: {
          oldPassword: [{required: true, pattern: /^\d{6,18}$/, message: "密码格式不正确", trigger: "blur"}],
          newPassword: [{required: true, pattern: /^\d{6,18}$/, message: "密码格式不正确", trigger: "blur"}]
        },
        // 上传用户头像的url
        userAvatarUploadUrl: process.env.BASE_URL + "/auth/avatar",
        // 商品图片的基础路径
        imageBaseUrl: process.env.BASE_URL + "/static/image/"

      };
    },
    mounted() {
      this.userInfo = this.$store.getters.userInfo;
    },
    methods: {
      handleClose() {
        this.userInfoDialogVisible = false
        this.updatePasswordDialogVisible = false
      },
      /*下拉菜单处理*/
      handleCommand(command) {
        console.log(command)
        if (command == 1)
          this.userInfoDialogVisible = true
        else if (command == 2)
          this.updatePasswordDialogVisible = true
        else if (command == 3)
          this.logout()
      },
      logout() { //退出登录
        this.$store.dispatch("logout");
      },
      //修改密码
      handleUpdatePassword() {
        this.updatePasswordDialogVisible = false
        const oldPassword = this.updatePasswordForm.oldPassword
        const newPassword = this.updatePasswordForm.newPassword
        updatePassword(oldPassword, newPassword).then(result => {
          this.$notify.success("密码修改成功")
        })
      },
      // 上传用户头像
      handleAvatarSuccess() {
        this.$notify.success("上传成功")
        getUserInfo().then(result => {
          this.userInfo = result.data
          this.$store.commit("setUserInfo", this.userInfo); // 重新拉取用户信息
        })
      },
      // 上传用户头像前的处理
      beforeAvatarUpload(file) {
        const isJPG = file.type === 'image/jpeg';
        const isLt1M = file.size / 1024 / 1024 < 1;
        if (!isJPG) {
          this.$message.error('上传头像图片只能是 JPG 格式!');
        }
        if (!isLt1M) {
          this.$message.error('上传头像图片大小不能超过 1MB!');
        }
        return isJPG && isLt1M;
      },
    }
  };
</script>


