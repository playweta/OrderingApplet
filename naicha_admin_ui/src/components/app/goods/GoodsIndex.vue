<template>
  <div>
    <div style="text-align: left; margin: 5px 10px">
      <el-button size="mini" type="primary" @click="toAdd" round>
        添加
      </el-button>
    </div>

    <!--增加或更新商品的表单弹窗-->
    <el-dialog :title=formTitle :visible.sync="formDialogVisible">
      <el-form ref="form" :model="formData" :rules="rules" size="large" label-width="150px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder=""></el-input>
        </el-form-item>
        <el-form-item label="选择商品种类">
          <el-select v-model="formData.goodsCategoryName"
                     placeholder="选择商品种类"
                     size="mini" style="float: left">
            <el-option
              v-for="item in goodsCategoryList"
              :key="item.name"
              :label="item.name"
              :value="item.name">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="显示次序" prop="displayOrder">
          <el-input v-model="formData.displayOrder" placeholder="显示次序" type="number"></el-input>
        </el-form-item>
        <el-form-item label="默认价格(单位: 分)" prop="defaultPrice">
          <el-input v-model="formData.defaultPrice" placeholder="默认价格" type="number"></el-input>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="formData.description" placeholder="商品描述, 最多100字"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="handelConfirm">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>


    <!--增加或更新商品属性的表单弹窗-->
    <el-dialog :title=goodsPropertyFormTitle :visible.sync="goodsPropertyFormDialogVisible">
      <el-form ref="goodsPropertyForm" :model="goodsPropertyFormData" :rules="goodsPropertyRules" size="large"
               label-width="180px">

        <el-form-item label="属性类型" prop="category">
          <el-select v-model="goodsPropertyFormData.category"
                     placeholder="选择商品种类"
                     size="mini" style="float: left">
            <el-option
              v-for="item in goodsPropertyCategories"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="属性值" prop="propertyOption">
          <el-input v-model="goodsPropertyFormData.propertyOption" placeholder="比如: 大杯，加冰，常温..."></el-input>
        </el-form-item>
        <el-form-item label="商品默认价格(单位: 分)" prop="realPrice">
          <el-input v-model="goodsPropertyFormData.rebasePrice" placeholder="属性为'大小'时才要填，其他情况不填或填0"
                    type="number"></el-input>
        </el-form-item>
        <el-form-item label="额外价格(单位: 分)" prop="realPrice">
          <el-input v-model="goodsPropertyFormData.extraPrice" placeholder="没有就不填，一般在'加料'的时候填" type="number"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetGoodsPropertyForm">重置</el-button>
          <el-button type="primary" @click="handelGoodsPropertyFormConfirm">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>


    <!-- 商品列表-->
    <div style="margin: 0px 10px;text-align: left;">
      <el-table
        :data="goodsAdmins"
        stripe
        :row-key="row =>{ return row.id; }"
        :expand-row-keys="expands"
        @expand-change="expandSelect"
        :default-sort="{prop: 'goodsCategoryName', order: 'ascending'}">

        <!--商品的属性列表-->
        <el-table-column type="expand">
          <template slot-scope="goodsScope">
            <el-button size="mini" type="primary" @click="toAddGoodsProperty(goodsScope.row.id)" round>
              添加商品属性
            </el-button>
            <el-table
              :data="goodsScope.row.goodsPropertyList"
              stripe>
              <el-table-column prop="category" label="属性类型" sortable></el-table-column>
              <el-table-column prop="propertyOption" label="属性值"></el-table-column>
              <el-table-column prop="isDefault" label="是否为默认属性" sortable>
                <template slot-scope="goodsPropertyScope">
                  <el-switch
                    v-model="goodsPropertyScope.row.isDefault"
                    active-color="green"
                    inactive-color="red"
                    :disabled="goodsPropertyScope.row.isDefault || goodsPropertyScope.row.category === '加料'"
                    @change="(value) => setDefaultPropertyOfGoods(value, goodsPropertyScope.row)">
                  </el-switch>
                </template>
              </el-table-column>
              <el-table-column prop="rebasePrice" label="商品默认价格" sortable>
                <template slot-scope="goodsPropertyScope">
                  {{goodsPropertyScope.row.rebasePrice ? '￥' + goodsPropertyScope.row.rebasePrice / 100 : '无'}}
                </template>
              </el-table-column>
              <el-table-column prop="extraPrice" label="额外价格" sortable>
                <template slot-scope="goodsPropertyScope">
                  {{goodsPropertyScope.row.extraPrice ? '￥' + goodsPropertyScope.row.extraPrice / 100 : '无'}}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template slot-scope="goodsPropertyScope">
                  <el-button @click="toEditGoodsProperty(goodsPropertyScope.row)" type="primary" size="mini"
                             icon="el-icon-edit" circle></el-button>
                  <el-button @click="deleteGoodsProperty(goodsPropertyScope.row)" type="danger" size="mini"
                             icon="el-icon-delete" circle></el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="商品名称" sortable></el-table-column>
        <el-table-column prop="goodsCategoryName" label="商品种类" sortable>
        </el-table-column>
        <el-table-column prop="displayOrder" label="显示顺序" sortable></el-table-column>
        <el-table-column prop="defaultPrice" label="默认价格" sortable>
          <template slot-scope="scope">
            {{'￥' + scope.row.defaultPrice / 100}}
          </template>
        </el-table-column>

        <!--上传商品图片-->
        <el-table-column prop="image" label="商品图片">
          <template slot-scope="scope">
            <el-upload
              :action="fileUploadUrl + '?goodsId=' + scope.row.id + '&token=' + $store.getters.token"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload">
              <img v-if="scope.row.image" :src="goodsImageBaseUrl + scope.row.image" style="width: 60px; height: 60px"
                   alt="商品图片">
              <div v-else style="border:1px solid lightgray; color: lightgray">+上传图片</div>
            </el-upload>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="商品描述"></el-table-column>

        <el-table-column label="是否上架">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.isSell"
              active-color="green"
              inactive-color="red"
              @change="(value) => commitStatusChange(value, scope.row)">
            </el-switch>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="130">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="toEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" @click="toDelete(scope.row.id)">删除</el-button>
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
    addGoodsAdmin,
    addGoodsProperty,
    deleteGoodsAdmins,
    deleteGoodsProperty,
    getGoodsAdminById,
    getGoodsAdmins,
    setDefaultPropertyOfGoods,
    updateGoodsAdmin,
    updateGoodsProperty,
    updateSellStatus
  } from "@/api/modules/app/goodsAdminApi.js";
  import {getAllGoodsCategoryAdmins} from "../../../api/modules/app/goodsCategoryAdminApi"

  export default {
    name: "goodsAdminComponent",
    data() {

      return {
        // 查询参数
        searchParams: {
          searchParam1: null,
          pageNo: 1,
          pageSize: 10,
        },
        goodsCategoryList: [], // 商品类别列表
        total: 0,
        goodsAdmins: [],
        formDialogVisible: false,
        formTitle: '添加',
        formData: {
          id: null,
          goodsCategoryName: '',
          name: '',
          displayOrder: 0,
          defaultPrice: 0.0,
          isSell: 1,
          image: null,
          description: ''
        },
        rules: {
          name: [
            {required: true, message: '商品种类不能为空', trigger: 'blur'}
          ], goodsCategoryName: [
            {required: true, message: '商品种类不能为空', trigger: 'blur'}
          ], displayOrder: [
            {required: true, message: '显示次序不能为空', trigger: 'blur'}
          ], defaultPrice: [
            {required: true, message: '默认价格不能为空', trigger: 'blur'}
          ], isSell: [
            {required: true, message: '是否在卖不能为空', trigger: 'blur'}
          ], image: [
            {required: true, message: '商品图片不能为空', trigger: 'blur'}
          ]
        },
        // 图片上传的url
        fileUploadUrl: process.env.BASE_URL + '/goodsAdmin/image',
        // 商品图片的基础路径
        goodsImageBaseUrl: process.env.BASE_URL + '/static/image/',

        // ********** 商品属性 ************ //
        expands: [], // 展开的行
        goodsPropertyFormDialogVisible: false,
        goodsPropertyFormTitle: "添加商品属性",
        currentGoodsId: null, // 操作当前的商品商品属性所需要的goodsId
        goodsPropertyCategories: ['大小', '温度', '甜度', '口味', '加料'], // 商品属性的类型，在数据库就定死了
        goodsPropertyFormData: {
          id: null,
          goodsId: null,
          category: '', // 商品属性类型: 温度','甜度','大小','口味'
          propertyOption: '',
          isDefault: 0,
          rebasePrice: null,
          extraPrice: null
        },
        goodsPropertyRules: {
          category: [
            {required: true, message: '商品属性类型不能为空', trigger: 'blur'}
          ], propertyOption: [
            {required: true, message: '商品的属性值不能为空', trigger: 'blur'}
          ]
        },
      }

    },
    mounted() {
      this.getGoodsAdmins();
      this.getAllGoodsCategoryAdmins();
    },
    methods: {
      // 获取商品类别
      getAllGoodsCategoryAdmins() {
        let that = this;
        getAllGoodsCategoryAdmins().then(result => {
          that.goodsCategoryList = result.data;
        })
      },
      // 修改商品的类别
      changeCategoryOfGoods(e) {
        console.log("修改商品的种类: ", e)
      },
      // 获取数据
      getGoodsAdmins() {
        let that = this;
        getGoodsAdmins(that.searchParams.pageNo, that.searchParams.pageSize).then(result => {
          // TODO 将获取的商品的图片加个随机值，不然上传了图片后不能马上刷新
          result.data.records.forEach(goods => {
            if (goods.image)
              goods.image = goods.image + "?random=" + Math.floor(Math.random() * 10000);
          })
          that.goodsAdmins = result.data.records;
          that.total = result.data.total;
        })
      },
      // 切换页数
      changePageNo(pageNo) {
        this.searchParams.pageNo = pageNo;
        this.getGoodsAdmins()
      },
      // 改变页面大小
      changePageSize(pageSize) {
        this.searchParams.pageSize = pageSize;
        this.getGoodsAdmins()
      },
      // 重置商品数据表单
      resetForm() {
        // 手动重置 不然数据被绑定在toEdit时深克隆出来的对象
        this.formData = {
          id: null,
          goodsCategoryName: '',
          name: '',
          displayOrder: 0,
          defaultPrice: 0.0,
          isSell: 1,
          image: null,
          description: ''
        }
      },
      // 增加商品
      toAdd() {
        this.resetForm()
        this.formDialogVisible = true
        this.formTitle = '添加'
      },
      // 删除商品
      toDelete(id) {
        let that = this;
        this.$confirm("提示", "是否删除", {}).then(() => {
          deleteGoodsAdmins([id]).then(() => {
            this.$notify.success("删除成功");
            that.getGoodsAdmins();
          })
        }).catch(() => {
        })
      },
      // 编辑商品
      toEdit(selectedGoodsAdmin) {
        // 深拷贝一个对象 不然在表格显示的数据会受到印象
        this.formData = JSON.parse(JSON.stringify(selectedGoodsAdmin));
        this.formDialogVisible = true
        this.formTitle = '更新'
      },
      // 提交更新商品的表单
      handelConfirm() {
        this.$refs['form'].validate(valid => {
          console.log(this.formTitle)
          if (!valid)
            return

          this.formDialogVisible = false;
          if (this.formTitle.startsWith('添加')) {
            addGoodsAdmin(this.formData).then(result => {
              this.$notify.success("添加成功");
              this.getGoodsAdmins()
            })
          } else if (this.formTitle.startsWith('更新')) {
            updateGoodsAdmin(this.formData).then(result => {
              this.$notify.success("更新成功");
              this.getGoodsAdmins()
            })
          }
        })
      },
      // 上架或下架商品
      commitStatusChange(value, goodsAdmin) {
        this.$confirm(value === false ? '是否下架商品？' : '是否上架商品？').then(() => {
          updateSellStatus(goodsAdmin.id).then(() => {
            this.$notify.success(value === true ? "已上架" : "已下架")
          }).catch(() => {
            goodsAdmin.isSell = !goodsAdmin.isSell;
          })
        }).catch(() => {
          goodsAdmin.isSell = !goodsAdmin.isSell;
        })
      },
      // 上传商品图片
      handleAvatarSuccess() {
        this.getGoodsAdmins();
      },
      // 上传图片前的处理操作
      beforeAvatarUpload(file) {
        const isJPG = file.type === 'image/jpeg';
        const isLt2M = file.size / 1024 / 30 < 1;
        if (!isJPG) {
          this.$message.error('上传头像图片只能是 JPG 格式!');
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 30KB!');
        }
        return isJPG && isLt2M;
      },


      // ***********  商品属性操作 ************* //
      // 刷新单个商品， 在每次设置完商品属性后局部刷新
      refreshGoods() {
        let that = this;
        getGoodsAdminById(that.currentGoodsId).then(result => {
          let goods = result.data;
          let goodsList = that.goodsAdmins;
          for (let i = 0; i < goodsList.length; i++) {
            if (goodsList[i].id === goods.id) {
              that.$set(that.goodsAdmins, i, goods);
              break;
            }
          }
        })
      },
      // 重置商品属性表单
      resetGoodsPropertyForm() {
        // 手动重置 不然数据被绑定在toEdit时深克隆出来的对象
        this.goodsPropertyFormData = {
          id: null,
          goodsId: null,
          category: '', // 商品属性类型: 温度','甜度','大小','口味'
          propertyOption: '',
          isDefault: 0,
          rebasePrice: null,
          extraPrice: null
        }
      },
      // 添加商品属性
      toAddGoodsProperty(goodsId) {
        this.currentGoodsId = goodsId
        this.resetGoodsPropertyForm()
        this.goodsPropertyFormDialogVisible = true
        this.goodsPropertyFormTitle = '添加商品属性'
      },
      // 编辑商品属性
      toEditGoodsProperty(selectedGoodsProperty) {
        this.currentGoodsId = selectedGoodsProperty.goodsId
        // 深拷贝一个对象 不然在表格显示的数据会受到印象
        this.goodsPropertyFormData = JSON.parse(JSON.stringify(selectedGoodsProperty));
        this.goodsPropertyFormDialogVisible = true
        this.goodsPropertyFormTitle = '更新商品属性'
      },
      // 控制只有一行被展开
      expandSelect: function (row, expandedRows) { // 当行展开时
        var that = this
        if (expandedRows.length) {
          that.expands = []
          if (row) {
            that.expands.push(row.id)
          }
        } else {
          that.expands = []
        }
      },
      // 处理商品属性表单提交
      handelGoodsPropertyFormConfirm() {
        let that = this;
        this.$refs['goodsPropertyForm'].validate(valid => {
          console.log(this.goodsPropertyFormTitle)
          if (!valid)
            return

          that.goodsPropertyFormDialogVisible = false;
          if (that.goodsPropertyFormTitle.startsWith('添加')) {
            that.goodsPropertyFormData.goodsId = that.currentGoodsId;
            addGoodsProperty(that.goodsPropertyFormData).then(result => {
              that.$notify.success("添加成功");
              that.refreshGoods()
            })
          } else if (this.goodsPropertyFormTitle.startsWith('更新')) {
            updateGoodsProperty(this.goodsPropertyFormData).then(result => {
              this.$notify.success("更新成功");
              that.refreshGoods()
            })
          }
        })
      },
      // 设置默认商品属性
      setDefaultPropertyOfGoods(value, goodsProperty) {
        this.currentGoodsId = goodsProperty.goodsId;
        if (!value) { // 如果是先取消默认属性则直接返回，通过设置其他为默认属性就可以间接取消了
          goodsProperty.isDefault = true;
          return;
        }
        let that = this;
        this.$confirm('是否设置为默认属性？').then(() => {
          setDefaultPropertyOfGoods(goodsProperty.id).then(() => {
            that.$notify.success("设置成功")
            that.refreshGoods()
          }).catch(() => {
            goodsProperty.isDefault = !goodsProperty.isDefault;
          })
        }).catch(() => {
          goodsProperty.isDefault = !goodsProperty.isDefault;
        })
      },
      // 删除商品的属性
      deleteGoodsProperty(goodsProperty) {
        this.currentGoodsId = goodsProperty.goodsId
        let that = this;
        this.$confirm("提示", "是否删除该商品属性", {}).then(() => {
          deleteGoodsProperty(goodsProperty.id).then(() => {
            this.$notify.success("删除成功");
            that.refreshGoods()
          })
        }).catch(() => {
        })
      }

    }
  }
</script>
