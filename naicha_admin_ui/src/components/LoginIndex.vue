<template>
  <div class="container1">

    <div class="login" v-if="!loading">
      <h2>用户登录</h2>
      <el-form ref="form" :model="loginForm" :rules="rules" size="mini" label-width="70px">
        <el-form-item class="login_box"  label="用户名" prop="username">
          <el-input v-model="loginForm.username"  placeholder="请输入用户名" style="margin-left: 10px;"></el-input>
        </el-form-item>
        <el-form-item class="login_box" label="密码" prop="password">
          <el-input v-model="loginForm.password" placeholder="请输入密码" type="password" style="margin-left: 10px;"></el-input>
        </el-form-item>
        <el-form-item class="login_box" prop="verifyCode">
          <el-input v-model="loginForm.verifyCode" placeholder="验证码" style="margin-left: 10px; width: 40%; float: left"></el-input>
          <div><img :src="codeUrl" @click="getValidCode" alt=" "></div>
        </el-form-item>
        <el-button id="loginButton" type="primary" @click="handleLogin" :loading="loading">登录</el-button>
      </el-form>
    </div>
  </div>
</template>
<script>
  import {getCode} from "../api/AuthController";
  import {Loading} from "element-ui";

  export default {
    data() {
      return {
        loading: false,
        registerDialogVisible: false,
        codeUrl: "",
        //登录用户
        loginForm: {
          username: '',
          password: '',
          uuid: "",
          verifyCode: ''
        },
        rules: {
          username: [{required: true, trigger: 'blur', message: '用户名不能为空'}],
          password: [{required: true, trigger: 'blur', message: '密码不能为空'}]
        },

        //注册用户
        registerUserForm: {
          username: '',
          password: ''
        },
        registerRules: {
          username: [{required: true, trigger: 'blur', message: '用户名不能为空'},
            {max: 18}],
          password: [{required: true, trigger: 'blur', message: '密码不能为空'},
            {max: 18}],
        },
      }
    },
    created() {
      let loading = Loading.service({  // 加载遮罩
        lock: true,
        text: '加载中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 1)'
      })
      setTimeout(() => {
        loading.close()
      }, 500);

      // 获取验证码
      this.getValidCode()
      this.handleCookie() // 查看本地是否缓存了会话
    },
    methods: {
      getValidCode() {
        let that = this;
        getCode().then(result => {
          that.loginForm.uuid = result.data.uuid;
          that.codeUrl = result.data.image;
        });
      },
      handleCookie() {
        this.loading = true;
        const cacheToken = window.localStorage.getItem('token');
        if (cacheToken != null){
          this.$store.dispatch("loginByToken", cacheToken);
        }else {
          const cacheLoginUser = window.localStorage.getItem("loginUser");
          if (cacheLoginUser) {
            this.loginForm = JSON.parse(cacheLoginUser);
          }
          this.loading = false;
        }
      },
      handleLogin() {
        let that = this;
        this.$refs['form'].validate(valid => {
          if (valid) {
            this.loading = true;
            setTimeout(() => {
              that.loading = false;
            }, 2000)
            this.$store.dispatch("login", this.loginForm);
          }
        })
      }
    }
  }
</script>
<style>

  .container1{
    background:linear-gradient(#b3c2db, #a5bedb); display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(#547abf, #4c79b3);
  }

  .login{
    width: 400px;
    height: 300px;
    padding: 20px;
    background-color: rgba(0,0,0,0.2);
    box-shadow: 0 15px 25px rgba(0,0,0,0.4);
  }
  .login h2{
    color: #fff;
    margin-bottom: 30px;
  }
  .login .login_box{
    position: relative;
    height: 50px;
    width: 100%;
  }
  .login .login_box input{
    /*清处input框自带的边框和轮廓*/
    outline: none;
    border: none;
    width: 100%;
    padding: 10px 0;
    margin-bottom: 30px;
    color: #fff;
    font-size: 16px;
    border-bottom: 1px solid #fff;
    background-color: transparent;

  }
  .login .login_box label{
    position: absolute;
    top: 0;
    left: 0;
    padding: 10px 0;
    color: #fff;
    pointer-events: none;
    transition: all 0.5s;
  }
  #loginButton{
    background-color: rgba(0,0,0,0.2);
    width: 80px;
    height: 40px;
    overflow: hidden;
    padding: 10px 15px;
    border-radius: 0;
    color: #03e9f4;
    text-decoration: none;
    font-size: 20px;
    transition: all 0.5s;
  }
  #loginButton:hover{
    color: #fff;
    border-radius: 10px;
    background-color: #03e9f4;
    box-shadow: 0 0 5px #03e9f4,
    0 0 25px #03e9f4,
    0 0 50px #03e9f4,
    0 0 100px #03e9f4;
  }

</style>


