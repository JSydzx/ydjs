import { http } from './http'

export const createPost = (payload: {
  title: string
  content: string
  topic?: string
}): Promise<any> => {
  return http.post('/posts', payload).then((res) => res)
}
