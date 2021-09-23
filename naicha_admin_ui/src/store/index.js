import Vue from 'vue'
import Vuex from 'vuex'
import systemMenu from './modules/systemMenu.js'
import user from './modules/user'
import menuTag from "./modules/menuTag";

Vue.use(Vuex)

// 缓存数据的地方
export default new Vuex.Store({
  modules: {
    systemMenu,
    user,
    menuTag,
  },

  state: {
  },
  getters: {
  },
  mutations: {
  }
})
