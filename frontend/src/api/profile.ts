import { http } from './http'

export const getUserInfo = (): Promise<{
  name: string
  school: string
  postCount: number
  followCount: number
  favoriteCount: number
}> => {
  return http.get('/user/profile').then(
    (res) =>
      res as unknown as {
        name: string
        school: string
        postCount: number
        followCount: number
        favoriteCount: number
      }
  )
}

export const updateUserInfo = (payload: {
  name?: string
  school?: string
  signature?: string
}): Promise<any> => {
  return http.put('/user/profile', payload).then((res) => res)
}
