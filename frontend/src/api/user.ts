// src/api/user.ts
import { http } from './http'

export interface RegisterRequest {
  username: string
  password: string
  nickname?: string
  email?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface UserProfileUpdateRequest {
  nickname?: string
  email?: string
  avatar?: string
}

export interface UserVO {
  id: number
  username: string
  nickname?: string
  email?: string
  avatar?: string
  createdAt: string
  updatedAt: string
}

// 用户注册
export const register = (params: RegisterRequest): Promise<UserVO> => {
  return http.post('/user/register', params)
}

// 用户登录
export const login = (params: LoginRequest): Promise<UserVO> => {
  return http.post('/user/login', params)
}

// 获取用户资料
export const getProfile = (): Promise<UserVO> => {
  return http.get('/user/profile')
}

// 更新用户资料
export const updateProfile = (params: UserProfileUpdateRequest): Promise<UserVO> => {
  return http.put('/user/profile', params)
}
