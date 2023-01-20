<template>
  <el-header class="header">

<!--            <RouterLink to="/">Home</RouterLink>-->
<!--            <RouterLink to="/write">메일 작성</RouterLink>-->
    <el-menu mode="horizontal" router>
      <el-menu-item index="/" >Home</el-menu-item>
      <el-menu-item index="/write">메일 작성</el-menu-item>
      <el-menu-item index="/join">멤버 등록</el-menu-item>
      <el-menu-item index="/members">멤버 조회</el-menu-item>
    </el-menu>

  </el-header>

</template>

<script setup lang="ts">

</script>


<style scoped>
  .header {
    padding: 0;
    height: 60px;
  }
</style>