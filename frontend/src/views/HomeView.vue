<template>
  <div class="page home-page">
    <header class="top-bar">
      <h1>公告</h1>
    </header>

    <section class="category-area">
      <button v-for="cat in categories" :key="cat" :class="{ active: selectedCategory === cat }"
        @click="selectedCategory = cat">
        {{ cat }}
      </button>
    </section>

    <section class="post-area">
      <ul>
        <li v-for="post in filteredPosts" :key="post.id" class="post-card" @click="openPost(post.id)">
          <div class="post-top">
            <img class="avatar" :src="post.avatar" alt="头像" @click.stop="openUser(post.userId || 0)" />
            <div class="user-meta">
              <div class="user-meta-top">
                <div class="nickname">{{ post.nickname }}</div>
                <div class="post-time">{{ post.time }}</div>
              </div>
              <div class="title">{{ post.title }}</div>
            </div>
          </div>
          <div class="content">{{ post.preview }}</div>
          <div class="thumb" v-if="post.imgUrl">
            <img :src="post.imgUrl" alt="配图" />
          </div>
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '../api/home'

type PostItem = {
  id: number
  title: string
  preview: string
  meta: string
}

type PostCard = PostItem & {
  avatar: string
  nickname: string
  imgUrl: string
  time: string
  views: number
  likes: number
  shares: number
  category: string
  userId: number
}

const categories = ['全部', '篮球', '足球', '排球', '羽毛球', '乒乓球']
const selectedCategory = ref('全部')

const router = useRouter()
const posts = ref<PostCard[]>([])

const filteredPosts = computed(() => {
  if (selectedCategory.value === '全部') return posts.value
  return posts.value.filter((item) => item.category === selectedCategory.value)
})

const loading = ref(false)

const loadPosts = async () => {
  loading.value = true
  console.log('开始调用 getPostList API...')
  try {
    const res = await getPostList({ page: 1, size: 20 })
    console.log('API 调用成功！返回数据:', res)

    const items = (res as any)?.items || []
    if (!Array.isArray(items)) {
      console.warn('接口返回数据格式不合预期，将使用空列表替代', items)
      posts.value = []
      return
    }

    posts.value = items.map((item: PostItem) => {
      const source = item as unknown as Partial<PostCard>
      return {
        id: item.id,
        title: item.title || '我是标题，XXXX队伍招人',
        preview: item.preview || '我是正文预览，内容只展示两行。',
        meta: item.meta || '',
        avatar: source.avatar || 'https://via.placeholder.com/40',
        nickname: source.nickname || 'XXXX',
        imgUrl: source.imgUrl || 'https://via.placeholder.com/320x180',
        views: source.views ?? 2410,
        likes: source.likes ?? 160,
        shares: source.shares ?? 2,
        category: source.category || '全部',
        time: source.time || '刚刚',
        userId: source.userId ?? 0,
      }
    })
    console.log('数据处理完成，posts:', posts.value)
  } catch (error) {
    console.error('API 调用失败！错误:', error)
    posts.value = []
  } finally {
    loading.value = false
  }
}

const openUser = (userId: number) => {
  window.alert(`跳转用户 ${userId} 页面（暂未设计）`)
}

const openPost = (postId: number) => {
  router.push({ name: 'postDetail', params: { id: postId } })
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.home-page {
  padding: 12px;
}

:root {
  --primary: #4a90e2;
  --secondary: #999;
  --accent: #ff7a45;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.top-bar h1 {
  margin: 0;
  color: #4a90e2;
  font-size: 28px;
  line-height: 34px;
  font-weight: 700;
}

.category-area {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.category-area button {
  border: 1px solid var(--secondary);
  color: #333333;
  padding: 6px 12px;
  border-radius: 16px;
  background: white;
  font-size: 14px;
  cursor: pointer;
}

.category-area button.active {
  color: #4a90e2;
}

.post-area ul {
  padding: 0;
  margin: 0;
  list-style: none;
}

.post-card {
  background: #fff;
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.05);
}

.post-top {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  align-items: center;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.user-meta .user-meta-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.user-meta .nickname {
  font-weight: 700;
  font-size: 16px;
  color: #333333;
}

.user-meta .post-time {
  color: #333333;
  font-size: 12px;
}

.user-meta .title {
  font-size: 18px;
  line-height: 25px;
  font-weight: 700;
}

.content {
  margin: 6px 0;
  color: #333333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.thumb img {
  width: 100%;
  max-height: 180px;
  border-radius: 8px;
  object-fit: cover;
  margin-top: 6px;
}

.post-footer {
  margin-top: 8px;
  font-size: 13px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  color: #333333;
}
</style>