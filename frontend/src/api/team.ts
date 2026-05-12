import { http } from './http'

export interface TeamVO {
  id: number
  name: string
  description?: string
  tag?: string
  creatorId: number
  creatorName?: string
  currentMembers: number
  maxMembers: number
  status: string
  createdAt: string
}

export interface UserVO {
  id: number
  username: string
  nickname?: string
  email?: string
  avatar?: string
}

export interface TeamCreateRequest {
  name: string
  description?: string
  tag?: string
  maxMembers?: number
}

export interface TeamUpdateRequest {
  name?: string
  description?: string
  tag?: string
  maxMembers?: number
  status?: string
}

export const getTeamList = (): Promise<TeamVO[]> => http.get('/team/list')
export const getMyTeams = (): Promise<TeamVO[]> => http.get('/team/my')
export const getTeamDetail = (teamId: number): Promise<TeamVO> => http.get(`/team/${teamId}`)
export const createTeam = (payload: TeamCreateRequest): Promise<TeamVO> => http.post('/team/create', payload)
export const updateTeam = (teamId: number, payload: TeamUpdateRequest): Promise<TeamVO> => http.put(`/team/${teamId}`, payload)
export const deleteTeam = (teamId: number): Promise<void> => http.delete(`/team/${teamId}`)
export const getTeamMembers = (teamId: number): Promise<UserVO[]> => http.get(`/team/${teamId}/members`)
export const addTeamMember = (teamId: number, userId: number): Promise<void> => http.post(`/team/${teamId}/members`, { userId })
export const removeTeamMember = (teamId: number, userId: number): Promise<void> => http.delete(`/team/${teamId}/members/${userId}`)
