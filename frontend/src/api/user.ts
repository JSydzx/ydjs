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
  major?: string
  grade?: string
  skills?: string
  bio?: string
}

export interface UserVO {
  id: number
  username: string
  nickname?: string
  email?: string
  avatar?: string
  major?: string
  grade?: string
  skills?: string
  bio?: string
  createdAt?: string
  updatedAt?: string
}

export interface LoginResponse {
  token: string
  user: UserVO
}

export const register = (params: RegisterRequest): Promise<UserVO> => http.post('/user/register', params)
export const login = (params: LoginRequest): Promise<LoginResponse> => http.post('/user/login', params)
export const getProfile = (): Promise<UserVO> => http.get('/user/profile')
export const updateProfile = (params: UserProfileUpdateRequest): Promise<UserVO> => http.put('/user/profile', params)
