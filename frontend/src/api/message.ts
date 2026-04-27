import { http } from './http'

export interface SendMessageParams {
  postId: number
  content: string
  type: 'application' // 可以扩展其他类型
}

export interface Message {
  id: number
  sender: string
  preview: string
  time: string
  content: string
  type: string
  postId?: number
}

export const sendMessage = (params: SendMessageParams): Promise<void> => {
  return http.post('/messages', params).then(() => undefined)
}

export const getMessages = (): Promise<Message[]> => {
  return http.get('/messages').then((res) => res as unknown as Message[])
}
