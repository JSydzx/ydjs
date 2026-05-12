import { http } from './http'

export interface JoinRequestVO {
  id: number
  teamId: number
  userId: number
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  reason?: string
  requestedAt: string
  processedAt?: string
}

export const createJoinRequest = (teamId: number, reason: string): Promise<JoinRequestVO> => http.post('/join/request', { teamId, reason })
export const getMyJoinRequests = (): Promise<JoinRequestVO[]> => http.get('/join/requests')
export const getTeamJoinRequests = (teamId: number): Promise<JoinRequestVO[]> => http.get(`/team/${teamId}/join-requests`)
export const approveJoinRequest = (requestId: number): Promise<void> => http.post(`/join/${requestId}/approve`)
export const rejectJoinRequest = (requestId: number): Promise<void> => http.post(`/join/${requestId}/reject`)
