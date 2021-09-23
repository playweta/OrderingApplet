<template>
  <div style="height: 70px; overflow: hidden;">
    <!--后台页面顶部条-->
    <div style="height: 40px;display: flex;align-items: center; justify-content: space-between;">
      <div style="margin-left: 10px">
        {{currentTag}}
      </div>
      <!--用户信息-->
      <userBar></userBar>
      <screenfull style="margin: 0 30px;" :height="20" :width="20"></screenfull>
    </div>
    <!--标签导航条-->
    <el-card body-style="padding: 0;">
      <el-tag
              size="small"
              :key="index"
              v-for="(tag, index) in routeTags"
              closable
              :disable-transitions="false"
              @close="handleClose(index)"
              class="routeTag">
        <router-link :to="tag.path">{{tag.title}}</router-link>
      </el-tag>
    </el-card>
  </div>

</template>

<!--顶部的路由导航标签-->
<script>
  import screenfull from "../components/Screenfull/index"
  import userBar from "../user/UserBar";

  export default {
    components: {userBar, screenfull},
    data() {
      return {
        //顶部的路由标签条
        routeTags: [],
        currentTag: [] // 单数组
      }
    },
    mounted() {
      this.currentTag = this.$store.getters.currentTag;
      this.routeTags = this.$store.getters.routeTags;
    },
    methods: {
      handleClose(index) { // 删除标签
        this.$store.commit("deleteTag", index);
        this.routeTags = this.$store.getters.routeTags;
      }
    }
  }
</script>

<style>
  .routeTag {
    margin: 2px;
    float: left;
    border-radius: 0;
    background-color: #ffffff;
  }

  .routeTag:hover {
    background-color: #36a3f7;
  }
</style>

