import { http } from './http'

export interface TeamVO {
  id: number
  name: string
  description: string
  tags: string[]
  creatorId: number
  createdAt: string
  updatedAt: string
}

export interface UserVO {
  id: number
  username: string
  nickname: string
  avatar: string
}

export const getTeamList = (): Promise<{
  items: Array<{ id: number; name: string; memberCount: number; eventCount: number }>
}> => {
  return http.get('/teams').then(
    (res) =>
      res as unknown as {
        items: Array<{ id: number; name: string; memberCount: number; eventCount: number }>
      }
  )
}

// 为了保持向后兼容，添加 getMyTeams 作为 getTeamList 的别名
export const getMyTeams = (): Promise<TeamVO[]> => {
  return http.get('/team/my').then((res) => res as unknown as TeamVO[])
}

export const createTeam = (payload: { name: string }): Promise<TeamVO> => {
  return http.post('/teams', payload).then((res) => res as unknown as TeamVO)
}

export const getTeamDetail = (teamId: number): Promise<TeamVO> => {
  return http.get(`/teams/${teamId}`).then((res) => res as unknown as TeamVO)
}

export const updateTeam = (
  teamId: number,
  payload: { name: string; description: string; tags: string[] }
): Promise<TeamVO> => {
  return http.put(`/teams/${teamId}`, payload).then((res) => res as unknown as TeamVO)
}

export const deleteTeam = (teamId: number): Promise<void> => {
  return http.delete(`/teams/${teamId}`).then(() => {})
}

export const getTeamMembers = (teamId: number): Promise<UserVO[]> => {
  return http.get(`/teams/${teamId}/members`).then((res) => res as unknown as UserVO[])
}

export const addTeamMember = (teamId: number, payload: { userId: number }): Promise<void> => {
  return http.post(`/teams/${teamId}/members`, payload).then(() => {})
}

export const removeTeamMember = (teamId: number, userId: number): Promise<void> => {
  return http.delete(`/teams/${teamId}/members/${userId}`).then(() => {})
}
