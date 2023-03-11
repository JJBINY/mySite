<script setup lang="ts">
import {ref, defineProps} from "vue";
import axios from "axios";
import router from "@/router";

const props = defineProps({
  toLoginId: {
    type: [Number, String],
    require: true,
  },
});

const sender = ref("")
const to = ref("")
const title = ref("")
const content = ref("")
let member = ref({})

axios.get("/api/member")
    .then((response) =>{
      console.log("세션조회")
      member.value = response.data;
    })
    .catch((c) => {
          router.push("login")
    });

const write = function () {
  // console.log(title,content)
  // alert("저장완료")
  axios
      .post("/api/message/create", {
        toLoginId: props.toLoginId,
        content: content.value,
      })
      .then(() => {
        router.replace({name: "home"})
      });

};

const cancel = function (){
  router.replace({name: "home"})
}

</script>

<template>
<!--  //TODO 뷰 구조 변경 필요-->

  <div>
    받는 이
    <el-input v-model="props.toLoginId" />
  </div>

  <div class="mt-2">
    내용
    <el-input v-model="content" type="textarea" rows="15"/>
  </div>

  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="write()">작성완료</el-button>
    <el-button type="warning" @click="cancel()">취소</el-button>
  </div>

</template>

<style>

</style>