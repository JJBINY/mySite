<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";


const router = useRouter();


const logout = function () {

  axios
      .post("/api/logout")
      .then(() => {
        router.push({name: "home"})
      });

};

import { reactive } from 'vue'


const form = reactive({
  loginId: '',
  password: '',
})

</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>환영합니다.</span>
        <el-button class="button" text>정보 수정</el-button>
      </div>
    </template>
    <div v-for="o in 4" :key="o" class="text item">{{ 'List item ' + o }}</div>
    <div class="content">
<!--      {{ message.content.substring(0,100) }}...-->
      회원정보들..
    </div>
  </el-card>
  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="">회원정보 수정</el-button>
    <el-button type="warning" @click="logout()">로그아웃</el-button>
  </div>
</template>


<style>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.box-card {
  width: 480px;
}
</style>