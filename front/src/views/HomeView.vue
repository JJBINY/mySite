<script setup lang="ts">

import axios from "axios";
import {reactive} from "vue";
import {useRouter} from "vue-router";


const router = useRouter();

axios.get("/api/")
    .then((response) => {
      if (response.data == "loginHome") {
        router.push(response.data);
      }
    });


const login = function () {
  // console.log(title,content)
  // alert("저장완료")
  axios
      .post("/api/login", {
        loginId: form.loginId,
        password: form.password,
      })
      .then((response) => {
        // console.log(response.status);
        console.log("postok")

        //TODO 로그인 성공 or 실패 분기별 로직작성
        router.replace({name: "loginHome"});
      })
      .catch((r) => {
        console.log(r.status)
      })
  ;

};


const form = reactive({
  loginId: '',
  password: '',
})

</script>

<template>
  <h2>홈화면입니다.</h2>
  <el-form :inline="true" :model="form" class="demo-form-inline">
    <el-form-item label="아이디">
      <el-input v-model="form.loginId" placeholder="아이디" />
    </el-form-item>
    <el-form-item label="비밀번호">
      <el-input v-model="form.password" placeholder="비밀번호" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="login()">로그인</el-button>
    </el-form-item>
    </el-form>


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