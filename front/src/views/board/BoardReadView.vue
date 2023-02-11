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

//초기화방식개선필요
const board = ref({});
const content = ref("");
const cPage = ref(1);
const comments = ref([]);
const children = ref(new Map());
const childCount = ref(new Map());
const like = ref(0)
const commentCount = ref()
const pageSize=5

const reComments = ref(new Map<Number,Number>());

// api요청방식 개선필요
onMounted(() => {
  axios.get("/api/board/watch/" + props.boardId).then((response) => {
    // console.log(response)
    board.value = response.data;
  });

  axios.get("/api/board/"+props.boardId+"/like")
      .then((response)=>{
        like.value = response.data.count
      })
  getCount()
  getComments()

});
function getChildCount(parentId: any){
  axios.get("/api/board/"+props.boardId+"/comment/"+parentId+"/count")
      .then((response)=>{
        console.log(response.data.count)
        childCount.value.set(parentId, response.data.count)
      })
}
function getCount(){
  axios.get("/api/board/"+props.boardId+"/comment/count")
      .then((response)=>{
        commentCount.value = response.data.count
      })
}
function clickLike(){
  axios.post("/api/board/"+props.boardId+"/like")
      .then((response)=>{
        like.value= response.data.count
      })
}

const getComments = function () {
  axios.get("/api/board/" + props.boardId + "/comments?size=5&page="+cPage.value)
      .then((response) => {
        console.log(cPage.value)
        comments.value = [];
        response.data.forEach((r: any) => {
          comments.value.push(r);
          getChildCount(r.id)
        })
      })
      .catch(error => {
        console.log(error);
      });
}
function getChildren(parentId: any){
  reComments.value.set(parentId,cPage)
  axios.get("/api/comment/"+parentId+"/children?size=5&page="+reComments.value.get(parentId).value)
      .then((response)=>{
        children.value.set(parentId,[]);
        response.data.forEach((r: any) => {
          children.value.get(parentId).push(r);
          getChildCount(parentId)
        })
      })
      .catch(error => {
        console.log(error);
      });
}
function doClick(parentId :any) {
  console.log(parentId)
  console.log(childCount.value.get(parentId))
  if (reComments.value.has(parentId)){
    console.log("닫기")
    let idx = reComments.value.delete(parentId)
  } else{
    console.log("열기")
    reComments.value.set(parentId,1)
    getChildren(parentId)
  }
  getChildCount(parentId)
}


const write = function () {

  axios
      .post("/api/board/" + props.boardId + "/comment", {
        content: content.value,
      })
      .then(() => {
        getComments()
        getCount()
        content.value = "";
      });

};

const writeChild = function (parentId: any) {
  axios
      .post("/api/board/" + props.boardId + "/comment", {
        content: content.value,
        parentId: parentId
      })
      .then(() => {
        getChildren(parentId)
        getChildCount(parentId)
        content.value = "";
      });

};

</script>



<template>
  <div class="border">
    <div class="jumbotron p-4 p-md-5 rounded ">
      <div class="col-md-6 px-0">
        <strong class="d-inline-block mb-2 text-primary">게시글</strong>
        <h1 class="display-4 font-italic"><span>제목 : </span>{{ board.title }}</h1>
        <div class="mb-1 text-muted">
          <span class="p-1">작성자 : {{ board.writer }}</span>
          <span class="p-1">작성일 : {{ board.createdAt }}</span>
          <span class="p-1">수정일 : {{ board.lastModifiedAt }}</span>
        </div>
        <p class="lead mb-1 p-1 bold">{{ board.content }}</p>
      </div>

    </div>


    <div class="p-md-5 row no-gutters rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
      <el-badge :value=like class="item">
        <el-button @click="clickLike()">좋아요</el-button>
      </el-badge>
      <div class="col p-4 d-flex flex-column position-static border">
        <strong class="d-inline-block mb-2 text-primary">댓글</strong><p>{{commentCount}}개</p>
        <div>
          <el-input v-model="content" placeholder="댓글을 입력해주세요"></el-input>
          <el-button type="primary" @click="write()">작성</el-button>
        </div>

        <div>
          <ul v-for="comment in comments" :key="comment.id">
            <div class="dropdown-toggle">
              <div class="sub"><strong>{{ comment.writer }}</strong> {{ comment.createdAt }}</div>
              <div><span>{{ comment.content }}</span></div>
              <el-button  @click="doClick(comment.id)">답글 {{childCount.get(comment.id)}}</el-button>
              <div v-if="reComments.has(comment.id)" class="border"><span>답글내역</span>
                <ul v-for="child in children.get(comment.id)" :key="child.id">
                  <div class="dropdown-toggle">
                    <div class="sub"><strong>{{ child.writer }}</strong> {{ child.createdAt }}</div>
                    <div><span>{{child.content }}</span></div>
                  </div>
                </ul>
                <div>
                  <el-input v-model="content" placeholder="답글을 입력해주세요"></el-input>
                  <el-button type="primary" @click="writeChild(comment.id)">작성</el-button>
                </div>
                <el-pagination v-model:current-page="cPage" @click="getChildren(comment.id)" v-model:page-size="pageSize" layout="prev, pager, next" :total="childCount.get(comment.id)"/>
              </div>
            </div>
          </ul>
        </div>

        <el-pagination v-model:current-page="cPage" @click="getComments()" v-model:page-size="pageSize" layout="prev, pager, next" :total="commentCount"/>
      </div>

    </div>

  </div>


</template>

<style scoped lang="scss">
.title {
  font-size: 1.6rem;
  font-weight: 600;
  color: #383838;
  margin: 0;
}

.content {
  font-size: 0.85rem;
  margin-top: 10px;
  color: #7e7e7e;
}

.sub {
  margin-top: 4px;
  font-size: 0.78rem;

  .regDate {
    margin-left: 2px;
    color: #6b6b6b;
  }
}

</style>