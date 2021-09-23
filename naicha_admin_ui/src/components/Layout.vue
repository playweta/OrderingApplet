<template>
  <!--后台页面布局-->
  <el-container style=" min-width: 1000px;">
    <!--侧边菜单栏-->
    <el-aside style="width: 200px; ">
      <SystemMenu style="min-height:100vh; height:  100%" /> <!--固定左侧菜单栏-->
    </el-aside>

    <el-container>
      <el-header style="padding: 0">  <!--固定顶部-->
        <Header></Header>
      </el-header>

      <el-container>
        <el-main style="margin-top: 10px;padding: 0;">
          <!--作为父组件 所有子组件挂载到这里 动态切换组件 导航的组件可以有children嵌套继续类推-->
          <router-view/>
        </el-main>
        <el-footer style="position: fixed; top: 99%; right: 0px">
          <Footer/>
        </el-footer>
      </el-container>
    </el-container>
  </el-container>
</template>

<script>
  import Header from "./layout/Header";
  import SystemMenu from "./layout/MenuSideBar.vue";
  import Footer from "./layout/Footer";

  export default {
    components: {Header, SystemMenu, Footer},
  }
</script>
