<script setup lang="ts">
import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import router from "@/router";

const props = defineProps({
  boardId: {
    type: [Number, String],
    require: true,
  },
});


const title = ref("")
const content = ref("")
let member = ref({})

onMounted(() => {
  axios.get("/api/board/watch/" + props.boardId).then((response) => {

    const board = response.data;
    title.value = board.title;
    content.value = board.content;
  });
});
//TODO 세션 유효성 프론트에서 확인하는 방법 찾아서 개선하기
axios.get("/api/member")
    .then((response) =>{
      console.log("세션조회")
      console.log(props.boardId)
      member.value = response.data;
    })
    .catch((c) => {
          router.push("login")
    });



const update = function () {

  axios
      .patch("/api/board/"+props.boardId, {
        title: title.value,
        content: content.value,
      })
      .then(() => {
        router.push({name: "boardRead", params: {boardId:props.boardId}})
      });
};

const cancel = function (){
  router.replace({name: "board"})
}

</script>

<template>
  <div>
    <el-input v-model="title" placeholder="제목을 입력해주세요"/>
  </div>

  <div class="mt-2">
    <el-input v-model="content" type="textarea" rows="15"/>
  </div>

  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="update()">수정완료</el-button>
    <el-button type="warning" @click="cancel()">취소</el-button>
  </div>

</template>

<style>

</style>