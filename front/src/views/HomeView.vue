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


</script>

<template>
  <h2>홈화면입니다.</h2>



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