<script setup lang="ts">
import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import router from "@/router";


const props = defineProps({
  mailId: {
    type: [Number, String],
    require: true,
  },
});

//초기화방식개선필요
const message = ref({
  id: 0,
  title: "",
  content: "",
});



// api요청방식 개선필요
onMounted(() => {
  axios.get("/api/message/"+props.mailId).then((response) => {
    // console.log(response)
    message.value = response.data;
    });

});

</script>
<template>
  <h2 class="title">{{message.title}}</h2>
  <div class="sub d-flex">
    <div class="regDate">생성일 2023-01-20</div>
  </div>
  <div class="content">{{message.content}}</div>

</template>

<style scoped lang="scss">
.title{
  font-size: 1.6rem;
  font-weight: 600;
  color: #383838;
  margin: 0;
}
.content{
  font-size: 0.85rem;
  margin-top: 10px;
  color: #7e7e7e;
}
.sub{
  margin-top: 4px;
  font-size: 0.78rem;

  .regDate{
    margin-left: 2px;
    color: #6b6b6b;
  }
}
</style>