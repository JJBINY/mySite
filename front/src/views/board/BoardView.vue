<script setup lang="ts">

import axios from "axios";
import {defineProps, onMounted, ref} from "vue";
import {useRouter} from "vue-router";
const props = defineProps({
  boardId: {
    type: [Number, String],
    require: true,
  },
});

const router = useRouter();
const boards = ref([]);
const boardCount = ref();
const pageSize=10;
const cPage = ref(1);
onMounted(()=>{
  getBoards()
  getCount()
})
const getBoards = function () {
  boards.value = []
  axios.get("/api/board/list?size=10&page="+cPage.value)
      .then((response) => {
        response.data.forEach((r: any) => {
          boards.value.push(r);
        })
      })
      .catch(error => {
        console.log(error);
      });
}

function getCount(){
  axios.get("/api/board/count")
      .then((response)=>{
        boardCount.value = response.data.count
        console.log(boardCount.value)
      })
}

const moveToRead = () => {
  router.push({name: "read"})
}


</script>

<template>
  <div class="border">
    <div class="jumbotron p-4 p-md-5 rounded ">
      <div class="col-md-6 px-0">
        <strong class="d-inline-block mb-2 text-primary">게시글 목록 -{{boardCount}}</strong>
        <div class="mt-2 d-flex justify-content-end">
          <el-button type="primary" @click="router.push({name: 'boardWrite'})">게시글 작성</el-button>
        </div>
        <div class="mb-1 text-muted border">

          <ul>
            <li v-for="board in boards" :key="board.id">
              <div class="title">
                <router-link :to="{ name: 'boardRead', params: {boardId: board.id}}">
                  {{ board.title }}
                </router-link>
                <div class="regDate sub">{{board.createdAt}}</div>
              </div>


            </li>
          </ul>


        </div>
      </div>
      <el-pagination class="mt-4" v-model:current-page="cPage" @click="getBoards()" v-model:page-size="pageSize" background layout="prev, pager, next" :total="boardCount"/>
    </div>
  </div>


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

