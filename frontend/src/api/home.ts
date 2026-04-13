import { http } from './http'

export interface PostListParams {
  page: number
  size: number
}
export interface PostItem {
  id: number
  title: string
  preview: string
  meta: string
}
export interface Paginate<T> {
  total: number
  items: T[]
}

export const getPostList = (params: PostListParams): Promise<Paginate<PostItem>> => {
  return http.get('/posts', { params }).then((res) => res as unknown as Paginate<PostItem>)
}

export interface AnnouncementItem {
  id: number
  title: string
  summary: string
}

export const getAnnouncements = (params: {
  page: number
  size: number
}): Promise<Paginate<AnnouncementItem>> => {
  return http
    .get('/announcements', { params })
    .then((res) => res as unknown as Paginate<AnnouncementItem>)
}
