// src/api/team.ts
import { http } from './http'

export interface TeamCreateRequest {
  name: string
  description?: string
  tags?: string[]
}

export interface TeamUpdateRequest {
  name?: string
  description?: string
  tags?: string[]
}

export interface TeamMemberAddRequest {
  userId: number
  role?: string
}

export interface TeamVO {
  id: number
  name: string
  description?: string
  tags?: string[]
  creatorId: number
  createdAt: string
  updatedAt: string
}

export interface UserVO {
  id: number
  username: string
  nickname?: string
  avatar?: string
}

// 创建团队
export const createTeam = (params: TeamCreateRequest): Promise<TeamVO> => {
  return http.post('/team/create', params)
}

// 查询团队列表
export const getTeamList = (): Promise<TeamVO[]> => {
  return http.get('/team/list')
}

// 查询团队详情
export const getTeamDetail = (id: number): Promise<TeamVO> => {
  return http.get(`/team/${id}`)
}

// 查询我的团队
export const getMyTeams = (): Promise<TeamVO[]> => {
  return http.get('/team/my')
}

// 更新团队
export const updateTeam = (id: number, params: TeamUpdateRequest): Promise<TeamVO> => {
  return http.put(`/team/${id}`, params)
}

// 删除团队
export const deleteTeam = (id: number): Promise<void> => {
  return http.delete(`/team/${id}`)
}

// 查询团队成员
export const getTeamMembers = (id: number): Promise<UserVO[]> => {
  return http.get(`/team/${id}/members`)
}

// 添加团队成员
export const addTeamMember = (id: number, params: TeamMemberAddRequest): Promise<void> => {
  return http.post(`/team/${id}/members`, params)
}

// 移除团队成员
export const removeTeamMember = (teamId: number, userId: number): Promise<void> => {
  return http.delete(`/team/${teamId}/members/${userId}`)
}
