<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";


const router = useRouter();
const members = ref([]);

axios.get("/api/members").then((response) => {
  response.data.forEach((r: any) => {
    members.value.push(r);
  })
});

</script>

<template>
  <ul>
    <li v-for="member in members" :key="member.id">

      <div class="title">
<!--        <router-link :to="{ name: 'readMember', params: {memberId: member.id}}">-->
          {{ member.name }}
<!--        </router-link>-->
      </div>
      <div class="title">
          {{ member.phone }}
      </div>
      <div class="title">
        {{ member.address }}
      </div>

<!--      <div class="sub d-flex">-->
<!--        <div class="regDate">2023-01-20{{member.date}}</div>-->
<!--      </div>-->

    </li>
  </ul>
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