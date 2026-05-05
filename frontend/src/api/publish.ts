import { http } from './http'

export interface PostVO {
  id: number
  title: string
  content: string
  topic?: string
  authorId: number
  createdAt: string
  updatedAt: string
}

export const createPost = (payload: {
  title: string
  content: string
  topic?: string
}): Promise<PostVO> => {
  return http.post('/posts', payload).then((res) => res as unknown as PostVO)
}
