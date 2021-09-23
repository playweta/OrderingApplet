<template>
  <div>
    <div style="text-align: left; margin: 5px 10px">
      <el-button size="mini" :type="currentOrderStatus === '未付款' ? 'info' : ''"
                 round @click="getOrderInfoAdmins('未付款')">
        未付款
      </el-button>
      <el-button size="mini" :type="currentOrderStatus === '制作中' ? 'warning' : ''"
                 round @click="getOrderInfoAdmins('制作中')">
        制作中
      </el-button>
      <el-button size="mini" :type="currentOrderStatus === '配送中' ? 'primary' : ''"
                 round @click="getOrderInfoAdmins('配送中')">
        配送中
      </el-button>
      <el-button size="mini" :type="currentOrderStatus === '请取餐' ? 'success' : ''"
                 round @click="getOrderInfoAdmins('请取餐')">
        请取餐
      </el-button>
      <el-button size="mini" :type="currentOrderStatus === '已送达' ? 'success' : ''"
                 round @click="getOrderInfoAdmins('已送达')">
        已送达
      </el-button>
      <el-button size="mini" :type="currentOrderStatus === '退款中' ? 'danger' : ''"
                 round @click="getOrderInfoAdmins('退款中')">
        退款中
      </el-button>
    </div>


    <!--增加或更新的表单弹窗-->
    <el-dialog title="退款取消订单" :visible.sync="showCancelReasonModal">
      <textarea cols="50" rows="10" maxlength="30" v-model="cancelReason" placeholder="输入取消订单的原因..."></textarea>
      <br>
      <el-button type="primary" @click="handelCancelOrder" round>确定取消</el-button>
    </el-dialog>

    <!--列表-->
      <div style="margin: 0px 10px;text-align: left;">
        <el-table
                :data="orderInfoAdmins"
                stripe
                :default-sort="{prop: 'createTime', order: 'descending'}">

          <el-table-column type="expand">
            <template slot-scope="props">
              <el-form label-position="left" inline>
                <el-form-item label="订单编号">
                  <span>{{ props.row.orderNo }}</span>
                </el-form-item>
                <br>
                <el-form-item label="取餐方式">
                  <span>{{ props.row.takeType }}</span>
                </el-form-item>
                <br>
                <el-form-item label="商品信息">
                  <span>{{ props.row.goodsPreview }}</span>
                </el-form-item>
                <br>
                <el-form-item label="总价格">
                  <span>￥{{props.row.totalPrice /100 }}</span>
                </el-form-item>
                <br>
                <el-form-item label="支付金额">
                  <span>￥{{ props.row.payPrice / 100}}</span>
                </el-form-item>
                <br>
                <el-form-item label="下单时间">
                  <span>{{ props.row.createTime }}</span>
                </el-form-item>
                <br>
                <el-form-item label="支付时间">
                  <span>{{ props.row.payTime }}</span>
                </el-form-item>
                <br>
                <el-form-item label="微信订单号">
                  <span>{{ props.row.wxPayTransactionId }}</span>
                </el-form-item>
              </el-form>

            </template>
          </el-table-column>

          <el-table-column prop="orderStatus" label="订单状态"></el-table-column>
          <el-table-column prop="addressDetail" label="收货地址" sortable></el-table-column>
          <el-table-column prop="goodsPreview" label="商品信息"></el-table-column>
          <el-table-column prop="goodsTotalNum" label="商品总数" sortable></el-table-column>
          <el-table-column prop="verifyNum" label="取单号"></el-table-column>
          <el-table-column prop="createTime" label="下单时间" sortable></el-table-column>
          <el-table-column prop="payTime" label="支付时间" sortable></el-table-column>
          <el-table-column prop="userPhone" label="用户电话" show-overflow-tooltip></el-table-column>
          <el-table-column prop="receiver" label="取餐人" sortable></el-table-column>
          <el-table-column prop="extraInfo" label="订单备注"></el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
            <span v-if="scope.row.orderStatus == '未付款'">
              <el-button type="text" size="mini" @click="queryWeixinOrder(scope.row.orderNo)"
              >查询支付状态
              </el-button>
              <el-button type="text" size="mini" @click="cancelOrderNotPay(scope.row.orderNo)"
              >取消
              </el-button>
            </span>
              <span v-else-if="scope.row.orderStatus != '退款中'">
              <el-button type="text" size="mini" @click="toNextOrderStatus(scope.row)">下一步</el-button>
            <el-button type="text" size="mini" @click="refundOrder(scope.row)"
                       v-if="scope.row.orderStatus != '未付款' && scope.row.orderStatus != '退款中'">
              退款
            </el-button>
            </span>
            </template>
          </el-table-column>

        </el-table>
        <!--分页组件-->
        <el-row style="float: right">
          <el-pagination
                  @current-change="changePageNo"
                  @size-change="changePageSize"
                  :current-page.sync="searchParams.pageNo"
                  :page-sizes="[5, 10, 20, 30, 50, 100]"
                  :page-size.sync="searchParams.pageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="total">
          </el-pagination>
        </el-row>


      </div>
  </div>
</template>

<script>
  import {
    queryWeixinOrder,
    getOrderInfoAdmins,
    toNextOrderStatus,
    cancelAndRefund,
    cancelOrderNotPay
  } from "@/api/modules/app/orderInfoAdminApi.js";

  export default {
    name: "orderInfoAdminComponent",
    data() {
      return {
        // 查询参数
        searchParams: {
          searchParam1: null,
          pageNo: 1,
          pageSize: 10,
        },
        total: 0,
        orderInfoAdmins: [],
        currentOrderStatus: '制作中',
        showCancelReasonModal: false,
        selectedOrder: null,
        cancelReason: '', // 取消订单原因
      }
    },
    mounted() {
      this.getOrderInfoAdmins(this.currentOrderStatus)
    },
    methods: {
      // 获取数据
      getOrderInfoAdmins(orderStatus = null) {
        let that = this;
        that.currentOrderStatus = orderStatus;
        getOrderInfoAdmins(that.searchParams.pageNo, that.searchParams.pageSize, orderStatus).then(result => {
          that.orderInfoAdmins = result.data.records;
          that.total = result.data.total;
        })
      },
      // 切换页数
      changePageNo(pageNo) {
        this.searchParams.pageNo = pageNo;
        this.getOrderInfoAdmins()
      },
      // 改变页面大小
      changePageSize(pageSize) {
        this.searchParams.pageSize = pageSize;
        this.getOrderInfoAdmins()
      },
      // 进入订单的下一步
      toNextOrderStatus(selectedOrder) {
        let that = this;
        if (selectedOrder.orderStatus === '未付款') {
          this.$confirm("用户并未付款, 请确保已经线下付款或其他方式支付", "是否继续?", {}).then(() => {
            this.$confirm("订单目前的状态为: " + selectedOrder.orderStatus, "请再次确认是否继续", {}).then(() => {
              toNextOrderStatus(selectedOrder.orderNo, selectedOrder.orderStatus).then(result => {
                this.$notify.success("订单状态已标记为: " + result.data);
                that.getOrderInfoAdmins(selectedOrder.orderStatus);
              })
            }).catch(() => {
            })
          }).catch(() => {
          })
        } else {
          this.$confirm("订单目前的状态为: " + selectedOrder.orderStatus, "是否进入下一步", {}).then(() => {
            toNextOrderStatus(selectedOrder.orderNo, selectedOrder.orderStatus).then(result => {
              this.$notify.success("订单状态已标记为: " + result.data);
              that.getOrderInfoAdmins(selectedOrder.orderStatus);
            })
          }).catch(() => {
          })
        }
      },
      // 查看微信支付状态
      queryWeixinOrder(orderNo) {
        let that = this;
        queryWeixinOrder(orderNo).then(result => {
          this.$notify.success("微信订单的支付状态为: " + result.data);
          that.getOrderInfoAdmins('未付款');
        })
      },
      // 取消没有付款的订单
      cancelOrderNotPay(orderNo){
        let that = this;
        this.$confirm("", "取消订单(请确保该订单未支付)，是否取消？", {}).then(() => {
          cancelOrderNotPay(orderNo).then(result => {
            this.$notify.success( result.data);
            that.getOrderInfoAdmins('未付款');
          })
        }).catch(() => {
        })
      },
      // 退款取消订单
      refundOrder(selectedOrder) {
        let that = this;
        this.selectedOrder = selectedOrder;
        this.$confirm("", "是否退款取消该订单", {}).then(() => {
          that.showCancelReasonModal = true;
        }).catch(() => {
        })
      },
      // 确认取消订单
      handelCancelOrder() {
        let that = this;
        that.showCancelReasonModal = false;
        cancelAndRefund(that.selectedOrder.orderNo, that.cancelReason).then(result => {
          this.$notify.success(result.data);
          that.getOrderInfoAdmins('制作中');
        })
      }
    }
  }
</script>
