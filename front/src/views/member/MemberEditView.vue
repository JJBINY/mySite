<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";


const router = useRouter();
const members = ref([]);
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
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>회원정보 수정</span>
      </div>
    </template>
    회원정보 수정 추후 구현예정

  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="">수정완료</el-button><!--TODO-->
    <el-button type="warning" @click="router.push({name:'loginHome'})">취소</el-button>
  </div>
  </el-card>

</template>

<style scoped lang="scss">
ul {
  list-style: none;
  padding: 0;

  li {
    margin-bottom: 1.6rem;

    .title {
      a {
        font-size: 1.2rem;
        color: #303030;
        text-decoration: none;
      }

      :hover{
        text-decoration: underline;
      }
    }

    .content {
      font-size: 0.95rem;
      margin-top: 8px;
      color: #5d5d5d;
    }

    :last-child {
      margin-bottom: 0;
    }

    .sub{
      margin-top: 4px;
      font-size: 0.78rem;

      .regDate{
        margin-left: 2px;
        color: #6b6b6b;
      }
    }
  }
}
</style>