<template>
  <div>

    <!--列表-->
    <div style="margin: 5px 10px;text-align: left;">
      <el-table
        :data="timingTaskList"
        stripe>

        <el-table-column prop="taskName" label="任务名" sortable></el-table-column>
        <el-table-column prop="description" label="任务描述"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button @click="runTimingTask(scope.row.taskMethodName)" type="text" size="mini">执行</el-button>
          </template>
        </el-table-column>
      </el-table>

    </div>

  </div>
</template>


<script>
  import {doTimingTask} from "../../../api/modules/system/timingTask.js"

  export default {
    name: "timingTaskIndex",
    data() {
      return {
        timingTaskList: [
          {
            taskName: "重置所有商品的默认属性和默认价格",
            taskMethodName: "resetGoodsDefaultProperty",
            description: "商品价格属性设置不对时可以重置"
          },
          {
            taskName: "刷新商品菜单缓存",
            taskMethodName: "updateGoodsMenuListRedisCache",
            description: "在后台更新了商品信息不能马上同步在前台"
          }
        ]
      }
    },
    methods: {
      runTimingTask(taskMethodName) {
        let that = this;
        this.$confirm("是否执行一次该任务", "提示", {}).then(() => {
          doTimingTask(taskMethodName).then(() => {
            that.$notify.success('执行成功')
          })
        }).catch(() => {
        })
      }
    }
  }
</script>
