import { http } from './http'

export interface NotificationVO {
  id: number
  userId: number
  message: string
  type: 'JOIN_REQUEST' | 'SYSTEM' | 'TEAM_UPDATE'
  isRead: boolean
  createdAt: string
}

export const getNotifications = (): Promise<NotificationVO[]> => http.get('/notification/list')
export const markRead = (id: number): Promise<void> => http.post(`/notification/${id}/read`)
export const deleteNotification = (id: number): Promise<void> => http.delete(`/notification/${id}`)
