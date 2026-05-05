<template>
  <div class="page schedule-page">
    <h1>我的日程</h1>

    <div
      v-if="!hasTeam"
      class="no-team"
    >
      <p>暂未加入队伍</p>
      <button @click="goToTeams">
        前往加入队伍
      </button>
    </div>

    <div
      v-else
      class="schedule-table"
    >
      <table>
        <thead>
          <tr>
            <th />
            <th>上午</th>
            <th>下午</th>
            <th>晚上</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="day-header">
              星期一
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[0].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[0].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[1].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[1].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[2].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[2].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
          </tr>
          <tr>
            <td class="day-header">
              星期二
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[0].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[0].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[1].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[1].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[2].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[2].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
          </tr>
          <tr>
            <td class="day-header">
              星期三
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[0].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[0].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[1].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[1].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[2].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[2].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
          </tr>
          <tr>
            <td class="day-header">
              星期四
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[0].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[0].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[1].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[1].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[2].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[2].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
          </tr>
          <tr>
            <td class="day-header">
              星期五
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[0].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[0].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[1].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[1].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[2].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[2].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
          </tr>
          <tr>
            <td class="day-header">
              星期六
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[0].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[0].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[1].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[1].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[2].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[2].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
          </tr>
          <tr>
            <td class="day-header">
              星期日
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[0].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[0].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[1].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[1].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
            <td class="time-slot">
              <div
                v-for="event in schedule.slots[2].events"
                :key="event.id"
                class="event-item"
              >
                {{ event.title }}
              </div>
              <div
                v-if="schedule.slots[2].events.length === 0"
                class="empty-slot"
              >
                -
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getSchedule, type ScheduleResponse } from '../api/hall.ts'

const router = useRouter()

const hasTeam = ref(false) // TODO: 检查用户是否加入了队伍
const schedule = ref<ScheduleResponse>({
  days: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'],
  slots: [
    { time: '上午', events: [] },
    { time: '下午', events: [] },
    { time: '晚上', events: [] }
  ]
})

const loadSchedule = async () => {
  try {
    // TODO: 检查用户是否加入队伍
    hasTeam.value = true // 暂时设为true以显示表格

    const data = await getSchedule({ week: 'current' })
    schedule.value = data
  } catch (error) {
    console.error('加载日程失败:', error)
    // 如果API失败，使用示例数据
    hasTeam.value = true
    schedule.value = {
      days: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'],
      slots: [
        {
          time: '上午',
          events: [
            { id: 1, title: '篮球训练' },
            { id: 2, title: '羽毛球比赛' }
          ]
        },
        {
          time: '下午',
          events: [
            { id: 3, title: '足球友谊赛' }
          ]
        },
        {
          time: '晚上',
          events: [
            { id: 4, title: '乒乓球练习' }
          ]
        }
      ]
    }
  }
}

const goToTeams = () => {
  router.push('/profile')
}

onMounted(() => {
  loadSchedule()
})
</script>

<style scoped>
.schedule-page {
  padding: 15px;
}

:root {
  --primary: #4a90e2;
}

h1 {
  color: #4a90e2;
  font-size: 28px;
  margin-bottom: 20px;
}

.no-team {
  text-align: center;
  padding: 40px 20px;
}

.no-team p {
  font-size: 18px;
  color: #333333;
}

.no-team button {
  background: #0066ff;
  color: #333333;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.schedule-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

th,
td {
  padding: 12px;
  text-align: center;
  border: 1px solid #eee;
}

th {
  background: #ffffff;
  font-weight: 600;
  color: #333333;
  font-weight: 600;
  background: #ffffff;
  text-align: left;
}

.day-header {
  background: #ffffff;
  font-weight: 600;
  color: #333333;
}

.time-slot {
  min-width: 120px;
}

.event-item {
  background: #e3f2fd;
  color: #333333;
  margin: 2px 0;
  font-size: 14px;
}

.empty-slot {
  color: #333333;
  font-style: italic;
}
</style>