import router from '../../router/index'
import {getUserInfo, login, loginByToken} from "../../api/AuthController";
import {Loading} from "element-ui";

const user = {
  state: {
    userInfo: null,
    permissions: [],
    token: null
  },
  // 能保证获取到最新的值 eg: ...mapGetters(['userInfo']) this.$store.getters.userInfo
  getters: {
    userInfo: state => state.userInfo,
    token: state => state.token,
    permissions: state => state.permissions
  },
  // 调用方法 this.$store.commit("mutationName", data);
  mutations: {
    setToken(state, token){
      state.token = token;
    },
  },
  // 调用方法 this.$store.dispatch("actionName", data);
  actions: {
    loginByToken({commit, state, dispatch}, token) {
      console.log("[通过本地缓存的令牌登录]", token);
      commit("setToken", token);
      loginByToken(token).then(result => {
        state.userInfo = result.data;
        commit("setToken", result.data.token);
        window.localStorage.setItem('token', result.data.token);

        dispatch("generateSystemMenus", result.data.permissions); // 生成菜单路由表
        router.push({redirect: true, path: '/index'});
      }).catch(() => {
        window.localStorage.removeItem("token");
      })
    },
    // 用户登录
    login({commit, state, dispatch}, requestUser) {
      login(requestUser).then(result => {
        state.userInfo = result.data;
        commit("setToken", result.data.token);
        window.localStorage.setItem('token', result.data.token);
        window.localStorage.setItem("loginUser", JSON.stringify(requestUser));

        dispatch("generateSystemMenus", result.data.permissions); // 生成菜单路由表
        router.push({redirect: true, path: '/index'});
      })
    },
    // 退出登录
    logout: ({commit, state}) => {
      commit("setToken", null);
      state.userInfo = null;
      window.localStorage.removeItem('token')
      location.reload();
    }
  }
}

export default user
