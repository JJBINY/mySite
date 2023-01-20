<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const name = ref("")
const phone = ref("")
const address = ref("")

const join = function () {
  // console.log(title,content)
  // alert("저장완료")
  axios
      .post("/api/mail/join", {
        name: name.value,
        phone: phone.value,
        address: address.value,
      })
      .then(() => {
        router.replace({name: "home"})
      });

};

import { reactive } from 'vue'

// do not use same name with ref
const form = reactive({
  name: '',
  phone1: '',
  phone2: '',
  phone3: '',
  city: '',
  street: '',
  zipcode: '',
})

const onSubmit = () => {
  console.log('submit!')
  console.log(form.name)
  console.log(form.phone1 + form.phone2 + form.phone3)
  console.log()

  axios
      .post("/api/member/join", {
        name: form.name,
        phone: form.phone1+form.phone2+form.phone3,
        address: {
          city:form.city,
          street:form.street,
          zipcode:form.zipcode,
        },
      })
      .then(() => {
        router.replace({name: "home"})
      });
}


</script>

<template>
  <el-form :model="form" label-width="120px">
    <el-form-item label="Name">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item label="Phone">
      <el-select v-model="form.phone1" placeholder="번호를 선택해주세요">
        <el-option label="010" value="010"/>
        <el-option label="011" value="011" />
      </el-select>
      <el-input v-model="form.phone2" maxlength="4" show-word-limit/>
      <el-input v-model="form.phone3" maxlength="4" show-word-limit/>
    </el-form-item>

    <el-form-item label="City">
      <el-input v-model="form.city" />
    </el-form-item>
    <el-form-item label="Street">
      <el-input v-model="form.street" />
    </el-form-item>
    <el-form-item label="Zipcode">
      <el-input v-model="form.zipcode" />
    </el-form-item>

  </el-form>
  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="onSubmit">Join</el-button>
    <el-button type="warning" @click="router.replace({name: 'home'})">Cancel</el-button>
  </div>
</template>

<style>

</style>