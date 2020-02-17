<template>
  <div>
    <!-- 筛选 -->
    <div class="comentFrom" :style="{'height':height+'rem'}">
      <label>价格区间</label>
      <div class="lable">
        <span class="spn1"><yd-input  v-model="screenFrom.startMoeny" required :show-success-icon="false" :show-clear-icon="false" :show-error-icon="false" regex="number" placeholder="起始价格"></yd-input></span>
        <span class="icon">——</span>
        <span class="spn2"><yd-input  v-model="screenFrom.endMoeny" required :show-success-icon="false" :show-clear-icon="false" :show-error-icon="false" regex="number" placeholder="结束价格"></yd-input></span>
      </div>
      <!-- 过滤数组 -->
        <div  v-for="(items,keys) in  Object.keys(screenFrom).filter(val=> val!=='startMoeny').filter(val=> val!=='endMoeny')" :key="keys" >
          <div  v-if="screenFrom[items].length > 0" >
            <div class="title">
                <!-- specification:规格 -->
              {{
                  items == 'brand' ? '品牌':
                  items == 'specification' ? '':
                  ''
              }}
            </div>

            <div  v-if="dom">
                <div v-for="(item,key) in screenFrom[items]" :key="key" >

                  <!-- 二维数组 -->
                  <div v-if="Array.isArray(item.value)" class="box">
                    <h3 >{{item.name}}</h3>
                    <div class="coment">
                      <div class="lable2" v-for="(i,k) in item.value" :key="k">
                        <span :class="[item.select.includes( i.id ) ? 'select_btn' : '','spn']"  @click="sceenFun(i,key,keys)">
                          {{i.content}}
                        </span>
                      </div>
                    </div>
                  </div>

                  <!-- 单一数组 -->
                  <div v-else class="lable2" >
                    <span  :class="[item.select.includes( item.id ) ? 'select_btn' : '','spn']"  @click="sceenFun(item,key,keys)">
                      {{item.content ? item.content : item.name}}
                    </span>
                  </div>

                </div>
            </div>

           </div>

        </div>

    </div>

    <!-- 底部 -->
    <div class="foot">
      <div @click="reset">重置</div>
      <div @click="submit">确定</div>
    </div>

  </div>
</template>

<script>
import commJs from '../comm';
export default {
  props: ['screenFrom'],
  data () {
    return {
      current:false,
      height:5,
      dom:true,
      // 选择结果
      selectResult:{},
      // 选择的规格
      selectScreen:{}
    }
  },
  methods: {
    /**
     * joker
     * 选择规格/价格
     * ps：
     */
    sceenFun(item,key,keys){
      // console.log(121);
      // 过滤
      let keysArr = Object.keys(this.screenFrom).filter(val=> val!=='startMoeny').filter(val=> val!=='endMoeny');
      let selectId = {};

      // 增强
      if(!Array.isArray(this.screenFrom[keysArr[keys]][key].select)){
        this.screenFrom[keysArr[keys]][key].select = [];
      }

      // 数据判断
      if(this.screenFrom[keysArr[keys]][key].select.includes(item.id)){
       // 存在，过滤
        let selectArr = this.screenFrom[keysArr[keys]][key].select.filter(val=> val!== item.id );
        this.screenFrom[keysArr[keys]][key].select = selectArr;
      }else{
        // 不存在，添加
        this.screenFrom[keysArr[keys]][key].select.push(item.id);
      }
      // 数据处理
      keysArr.forEach((item,key,arr)=>{
        let Arr = [];
        this.screenFrom[item].map((ite)=>{
          Arr.push(ite.select);
        });
        selectId[item] = ( [].concat(...Arr) ).join(',');
      })
      selectId.startMoeny =  this.screenFrom.startMoeny;
      selectId.endMoeny =  this.screenFrom.endMoeny;
      this.selectScreen = selectId;
      // 增强
      this.dom = false;
      this.dom =true;
    },
    // 提交
    submit(){
        Object.keys(this.screenFrom).forEach((item,key,arr)=>{
          this.selectResult[item] = '';
        })
        this.selectResult.startMoeny = this.screenFrom.startMoeny;
        this.selectResult.endMoeny = this.screenFrom.endMoeny;
        // 合并
        this.selectResult =Object.assign(this.selectResult,this.selectScreen);
        this.$emit('selectFun',this.selectResult);
    },
    // 重置
    reset(){
      this.screenFrom.startMoeny ='';
      this.screenFrom.endMoeny = '';
      this.selectScreen ={};
      Object.keys(this.screenFrom).filter(val=> val!=='startMoeny').filter(val=> val!=='endMoeny').forEach((item,key,arr)=>{
        // console.log(this.screenFrom[item]);
        if(Array.isArray(this.screenFrom[item])){
          this.screenFrom[item].forEach((ite,k)=>{
            // console.log(this.screenFrom[item][k].select);
            this.screenFrom[item][k].select = [];
          })
        }
      })
      // 增强
      this.dom = false;
      this.dom =true;
    }
  },
  mounted(){
    // console.log(commJs.equipment().height_rem);
    this.height = commJs.equipment().height_rem - 3
  },

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 筛选弹窗
  .comentFrom{
     padding: .9375rem .5rem;
    //  padding-bottom: 3rem;
     overflow-x: hidden;
     label{
       font-size: .9375rem;
       color: #333333;
     }
    .title{
      color: #333333;
      font-size: 1rem;
      margin: .8rem 0;
    }
    .lable{
      display:flex;
      align-items:center;
      margin: .625rem 0 1.2rem 0;
      .icon{
        margin: 0 1rem;
      }
      .spn1,.spn2{
        text-align: center;
        background: #f5f5f5;
        padding: .4rem .5rem;
        font-size: .8rem;
        border-radius: .625rem;
        input{
          width: 100%;
        }
      }
    }
  }
  /deep/ .yd-input input{
    text-align: center;
  }
  .spn{
    border-radius: 1.25rem;
    padding: .2rem .8rem;
    margin: .5rem .5rem 0 0;
    color:#333333;
    background: #f5f5f5;
    font-size: .875rem;
    border: 1px solid #f5f5f5;
  }
  .select_btn{
    border: 1px solid #D7000F;
    color:#D7000F;
    background: #f7cccf;
  }
  .foot{
    background: #ffffff;
    width: 100%;
    display: flex;
    font-size:1rem;
    border-top: 1px solid #e6e6ee;
    height: 2.8125rem;
    position: fixed;
    bottom: 0;
    div{
      height: 2.8125rem;
      display: flex;
      align-items:center;
      justify-content:center;
      width: 50%;
    }
    div:nth-child(2){
      background: #D7000F;
      color: #ffffff;
    }
  }
.coment{
  display: flex;
  flex-wrap:wrap;
}
h3{
  clear: both;
  margin: 1rem 0;
}
.lable2{
  span{
    float: left;
    margin: .2rem .4rem .5rem 0;
  }

}

.box{
  margin-bottom: 1rem;
  clear: both;
}
</style>

