<template>
  <div>
    <!--达到要求弹出div-->
    <el-card class="box-card" v-if="messageList && messageList.length">
      <div style="font-size: 25px">
        <span>您有新的订单来了,请注意查看</span>
      </div>
      <div v-for="message in messageList" :key="message.orderNo" style="font-size: 15px;">
        订单号[{{message.orderNo}}] [{{message.address}}]
        <el-button style="" size="default" type="text" @click="confirmReceiveMessage(message.orderNo)">收到</el-button>
        <br>
      </div>
    </el-card>
  </div>
</template>

<!--最新订单消息弹窗-->
<script>
  import {resentOrderMessage, confirmReceiveOrderMessage} from "../../api/modules/app/messageApi.js"

  export default {
    name: "OrderMessage",
    data() {
      return {
        messageList: []
      }
    },
    mounted() {
      this.getMessage();
    },
    methods: {
      // 语音提醒来了新订单
      voiceTips(address) {
        let txt = '您有新的订单来了,' + address + ',请注意查看'
        new Audio('http://tts.baidu.com/text2audio?cuid=baiduid&lan=zh&ctp=1&pdt=12&tex=' + txt).play();
      },
      getMessage() {
        let that = this;
        setInterval(() => {
          that.getRecentMessage();
        }, 15000);
      },
      getRecentMessage() {
        resentOrderMessage().then(result => {
          if (result.data.length > this.messageList.length)
            this.voiceTips(result.data[result.data.length - 1].address)
          this.messageList = result.data;
        }).catch(result => {
          console.log(result.data)
        })
      },
      // 确认收到消息
      confirmReceiveMessage(orderNo) {
        let that = this;
        confirmReceiveOrderMessage(orderNo).then(result => {
          that.$notify.success(result.data);
          that.getRecentMessage();
        }).catch(result => {
          console.log(result.data)
        })
      }

      //
    }
  }

</script>

<style>

  .box-card {
    position: fixed;
    top: 70vh;
    right: 1vw;
    width: 600px;
    height: 29vh;
    z-index: 100;
    background: #ff9966 !important;
  }
</style>
