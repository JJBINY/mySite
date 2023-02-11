<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";


const sender = ref("")
const receiver = ref("")
const title = ref("")
const content = ref("")
let member = ref({})

//TODO 세션 유효성 프론트에서 확인하는 방법 찾아서 개선하기
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
      .post("/api/board/write", {
        title: title.value,
        content: content.value,
      })
      .then(() => {
        router.replace({name: "board"})
      });
};

const cancel = function (){
  router.replace({name: "board"})
}

</script>

<template>
  <div>
    <el-input v-model="title" placeholder="제목을 입력해주세요"/>
  </div>

  <div class="mt-2">
    <el-input v-model="content" type="textarea" rows="15"/>
  </div>

  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="write()">작성완료</el-button>
    <el-button type="warning" @click="cancel()">취소</el-button>
  </div>

</template>

<style>

</style>