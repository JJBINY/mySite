<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const login = function () {
  // console.log(title,content)
  // alert("저장완료")
  axios
      .post("/api/login", {
        loginId: form.loginId,
        password: form.password,
      })
      .then((response) => {
        // console.log(response)
        //TODO 로그인 성공 or 실패 분기별 로직작성
        router.replace({name: "home"})
      })
      .catch((error)=> {
        console.log(error)
        alert(error.response.data.message)
      })
};

import { reactive } from 'vue'


const form = reactive({
  loginId: '',
  password: '',
})


</script>

<template>
  <el-form :inline="true" :model="form" class="demo-form-inline">
    <el-form-item label="아이디">
      <el-input v-model="form.loginId" placeholder="아이디" />
    </el-form-item>
    <el-form-item label="비밀번호">
      <el-input v-model="form.password" placeholder="비밀번호" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="login()">로그인</el-button>
      <el-button type="warning" @click="router.replace({name: 'home'})">취소</el-button>
    </el-form-item>

  </el-form>
</template>

<style>

</style>