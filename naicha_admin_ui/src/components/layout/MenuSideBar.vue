<template>
  <el-menu
          @open="handleOpen"
          background-color="#304156"
          text-color="#fff"
          router
          active-text-color="#1890ff"
          :unique-opened="true"
          :collapse-transition="false"
          class="el-menu-vertical-demo">
    <el-menu-item index="/index" style="background-color: #2b2f3a">
      <template slot="title">
        <img src="./rili.png" style="height: 30px;width: 30px" alt="">
        管理后台
      </template>
    </el-menu-item>

    <div v-for="(firstLevel, i) in menus" :key="i"> <!-- key用不到但是要定义不然报错 -->
      <!-- 有子菜单的一级菜单 -->
      <el-submenu v-if="firstLevel.children && !firstLevel.hidden" :index="firstLevel.title">
        <template slot="title" >
          <i :class=firstLevel.icon></i>
          <span>{{firstLevel.title}}</span>
        </template>
        <div v-for="(secondLevel, i) in firstLevel.children" :key="i">
          <!--有子菜单的二级菜单-->
          <el-submenu v-if="secondLevel.children && !secondLevel.hidden" :index="secondLevel.title">
            <template slot="title">
              <i :class=secondLevel.icon></i>
              <span slot="title">{{secondLevel.title}}</span>
            </template>
            <div v-for="(thirdLevel, i) in secondLevel.children" :key="i">
              <!--三级菜单 还有4级菜单继续类推 一般就3级左右-->
              <el-menu-item v-if="!thirdLevel.hidden" :index="thirdLevel.path" @click="updateRouteTag(thirdLevel)">
                <i :class=thirdLevel.icon></i>
                <span slot="title">{{thirdLevel.title}}</span>
              </el-menu-item>
            </div>
          </el-submenu>

          <!--没有子菜单的二级菜单-->
          <el-menu-item v-else-if="!secondLevel.hidden" :index="secondLevel.path" @click="updateRouteTag(secondLevel)">
            <i :class=secondLevel.icon></i>
            <span slot="title">{{secondLevel.title}}</span>
          </el-menu-item>
        </div>
      </el-submenu>

      <!--没有子菜单的一级菜单-->
      <el-menu-item v-else-if="!firstLevel.hidden" :index="firstLevel.path" @click="updateRouteTag(firstLevel)">
        <i :class=firstLevel.icon></i>
        <span slot="title">{{firstLevel.title}}</span>
      </el-menu-item>
    </div>
  </el-menu>

</template>

<script>

  export default {
    data() {
      return {
        menus: []
      }
    },
    mounted() {
      this.menus = this.$store.getters.systemMenus;
    },
    methods: {
      // 打开非叶子菜单
      handleOpen(key, keyPath) {
        console.log(key, keyPath)
      },
      // 打开叶子菜单
      updateRouteTag(menu) {
        console.log("[点击的具体菜单项]", menu);

        let tag = {
          name: menu.name,
          path: menu.path,
          title: menu.title
        }
        this.$store.commit('updateRouteTags', tag)
      }
    }
  }
</script>

<style>
  .el-menu-vertical-demo:not(.el-menu--collapse) {
    text-align: left;
  }
</style>

