import { http } from './http'

type ScheduleSlot = {
  time: '上午' | '下午' | '晚上'
  events: Array<{ id: number; title: string }>
}

export interface ScheduleResponse {
  days: string[]
  slots: ScheduleSlot[]
}

export const getSchedule = (params: { week: string }): Promise<ScheduleResponse> => {
  return http.get('/schedule', { params }).then((res) => res as unknown as ScheduleResponse)
}
