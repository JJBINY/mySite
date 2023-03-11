import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginHomeView from "../views/LoginHomeView.vue";
import MessageWriteView from "../views/MessageWriteView.vue";
import MessageReadView from "../views/MessageReadView.vue";
import JoinView from "../views/member/JoinView.vue";
import LoginView from "../views/member/LoginView.vue";
import MemberEditView from "../views/member/MemberEditView.vue";
import MembersView from "../views/member/MembersView.vue";
import MemberMailsView from "../views/member/MemberMailsView.vue";
import BoardView from "../views/board/BoardView.vue";
import BoardWriteView from "../views/board/BoardWriteView.vue";
import BoardEditView from "../views/board/BoardEditView.vue";
import BoardReadView from "../views/board/BoardReadView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/loginHome",
      name: "loginHome",
      component: LoginHomeView,
    },
    {
      path: "/messageWrite:toLoginId",
      name: "messageWrite",
      component: MessageWriteView,
      props: true,
    },
    {
      path: "/messageRead:messageId",
      name: "messageRead",
      component: MessageReadView,
      props: true,
    },
    {
      path: "/mails:memberId",
      name: "memberMails",
      component: MemberMailsView,
      props: true,
    },
    {
      path: "/join",
      name: "join",
      component: JoinView,
      props: true,
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      props: true,
    },
    {
      path: "/memberEdit",
      name: "memberEdit",
      component: MemberEditView,
      props: true,
    },
    {
      path: "/members",
      name: "members",
      component: MembersView,
      props: true,
    },
    {
      path: "/board",
      name: "board",
      component: BoardView,
      props: true,
    },
    {
      path: "/boardWrite",
      name: "boardWrite",
      component: BoardWriteView,
      props: true,
    },
    {
      path: "/boardEdit:boardId",
      name: "boardEdit",
      component: BoardEditView,
      props: true,
    },
    {
      path: "/boardRead:boardId",
      name: "boardRead",
      component: BoardReadView,
      props: true,
    },
    // {
    //   path: "/about",
    //   name: "about",
    //   // route level code-splitting
    //   // this generates a separate chunk (About.[hash].js) for this route
    //   // which is lazy-loaded when the route is visited.
    //   component: () => import("../views/AboutView.vue"),
    // },
  ],
});

export default router;
