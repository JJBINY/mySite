<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

import { reactive } from 'vue'

// do not use same name with ref
const form = reactive({
  loginId: '',
  password: '',
  name: '',
  phone1: '',
  phone2: '',
  phone3: '',
  country: '',
  address: '',
  detail: '',
})

// const validation = ref("");

const onSubmit = () => {
  console.log('submit!')
  console.log(form.name,form.loginId,form.password)


  axios
      .post("/api/join", {
        loginId: form.loginId,
        password: form.password,
        name: form.name,
        phone: form.phone1+form.phone2+form.phone3,
        address: {
          country:form.country,
          address:form.address,
          detail:form.detail,
        },
      })
      .then(() => {
        router.replace({name: "home"})
      })
      .catch((error)=> {
        console.log(error)
        const validation =Object.keys(error.response.data.validation)[0]
        alert(error.response.data.validation[validation])

      })
  ;
}


</script>

<template>

  <el-form :model="form" label-width="120px">
    <span>회원가입</span>
    <el-row>
      <el-col :span="6">
        <div class="grid-content ep-bg-purple"/>
        <el-form-item label="아이디">
          <el-input v-model="form.loginId"/>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <div class="grid-content ep-bg-purple-light"/>
        <el-form-item label="비밀번호">
          <el-input v-model="form.password" type="password"/>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="8">
        <div class="d-flex grid-content ep-bg-purple-dark"/>
        <el-form-item label="이름">
          <el-input v-model="form.name"/>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="4">
        <div class="grid-content ep-bg-purple"/>
        <el-form-item label="휴대폰 번호">
          <el-select v-model="form.phone1" placeholder="번호를 선택해주세요">
            <el-option label="010" value="010"/>
            <el-option label="011" value="011"/>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <div class="grid-content ep-bg-purple-light"/>
        <el-input v-model="form.phone2" maxlength="4" show-word-limit/>
      </el-col>
      <el-col :span="4">
        <div class="grid-content ep-bg-purple"/>
        <el-input v-model="form.phone3" maxlength="4" show-word-limit/>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <div class="d-flex grid-content ep-bg-purple-dark"/>
        <el-form-item label="국가">
          <el-input v-model="form.country"/>
        </el-form-item>
        <el-form-item label="주소">
          <el-input v-model="form.address"/>
        </el-form-item>
        <el-form-item label="상세주소">
          <el-input v-model="form.detail"/>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <div class="mt-2 d-flex justify-content-end">
          <el-button type="primary" @click="onSubmit">회원가입</el-button>
          <el-button type="warning" @click="router.replace({name: 'home'})">취소</el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>



</template>


<style lang="scss">
  .el-row {
    margin-bottom: 1px;
  }

  .el-row:last-child {
    margin-bottom: 0;
  }

  .el-col {
    border-radius: 4px;
  }

  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
</style>