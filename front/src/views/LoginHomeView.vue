<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";


const router = useRouter();



const member = ref({});
axios.get("/api/member")
    .then((response) =>{
      console.log("세션조회성공")
      member.value = response.data;
    })
    .catch((c) => {
      router.push("login")
    });


const logout = function () {
  axios
      .post("/api/logout")
      .then(() => {
        router.push({name: "home"})
      });
};

const update = function () {
        router.push({name: "memberEdit"})
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
        <span>환영합니다 {{member.name}}님.</span>
      </div>
    </template>
    <div class="content">

      <div class="text item">이름 : {{member.name}}</div>
      <div class="text item">아이디 : {{member.loginId}}</div>
      <div class="text item">연락처 : {{member.phone}}</div>
      <div class="text item">주소 : {{member.address}}</div>
    </div>
  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="update()">회원정보 수정</el-button>
    <el-button type="warning" @click="logout()">로그아웃</el-button>
  </div>
  </el-card>


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