<script setup lang="ts">

import axios from "axios";
import {useRouter} from "vue-router";


const router = useRouter();

axios.get("/api/")
    .then((response) => {
      if (response.data == "loginHome") {
        router.push(response.data);
      }
    });
</script>

<template>
  <el-header class="header">

<!--            <RouterLink to="/">Home</RouterLink>-->
<!--            <RouterLink to="/write">메일 작성</RouterLink>-->
    <el-menu mode="horizontal" router>
      <el-menu-item index="/">Home</el-menu-item>
      <el-menu-item index="/board">게시판</el-menu-item>

      <el-menu-item index="/write">메일 작성</el-menu-item>
      <el-menu-item index="/members">회원 조회</el-menu-item>

      <div class="flex-grow"/>
<!---->
      <el-menu-item index="/join">회원가입</el-menu-item>
<!---->
      <el-menu-item index="/login">로그인</el-menu-item>
      <el-menu-item index="/memberInfo">회원정보</el-menu-item>
    </el-menu>


  </el-header>

</template>



<style scoped>
  .header {
    padding: 0;
    height: 60px;
  }
  .flex-grow{
    flex-grow: 1;
  }
</style>