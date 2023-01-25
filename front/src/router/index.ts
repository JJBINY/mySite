import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginHomeView from "../views/LoginHomeView.vue";
import WriteView from "../views/WriteView.vue";
// @ts-ignore
import ReadView from "../views/ReadView.vue";
import JoinView from "../views/JoinView.vue";
import LoginView from "../views/LoginView.vue";
import MemberInfoView from "../views/MemberInfoView.vue";
import MembersView from "../views/MembersView.vue";
import MemberMailsView from "../views/MemberMailsView.vue";

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
      path: "/write",
      name: "write",
      component: WriteView,
    },
    {
      path: "/read:mailId",
      name: "read",
      component: ReadView,
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
      path: "/memberInfo",
      name: "memberInfo",
      component: MemberInfoView,
      props: true,
    },
    {
      path: "/members",
      name: "members",
      component: MembersView,
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