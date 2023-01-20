<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const title = ref("")
const content = ref("")

const write = function () {
  // console.log(title,content)
  // alert("저장완료")
  axios
      .post("/api/mail/create", {
        title: title.value,
        content: content.value
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