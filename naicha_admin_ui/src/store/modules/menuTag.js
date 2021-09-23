const menuTag = {
  state: {
    // 顶部的路由标签条
    routeTags: [],
    currentTag: [] // 单数组
  },
  getters: {
    routeTags: state => state.routeTags,
    currentTag: state => state.tag
  },
  /**
   * 组件内存值 this.$store.commit('mutations里的方法名', 要存的数据)
   * 组件内取值 this.$store.getters.getterMethod;
   */
  mutations: {
    // 更新标签条 {path, title, name}
    updateRouteTags(state, tag) {
      let flag = false;
      state.routeTags.forEach(item => {
        if(item.name === tag.name){
          flag = true;
          return;
        }
      })
      if(flag) // 存在了
        return;
      state.routeTags.push(tag);
        if(state.routeTags.length > 7) // 限制标签
          state.routeTags.splice(0, 1);
    },
    // 删除标签
    deleteTag(state, index){
      state.routeTags.splice(index, 1);
    }
  },
  actions: {}
}
export default menuTag
