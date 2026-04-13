import { http } from './http'

export const getTeamList = (): Promise<{
  items: Array<{ id: number; name: string; memberCount: number; eventCount: number }>
}> => {
  return http
    .get('/teams')
    .then(
      (res) =>
        res as unknown as {
          items: Array<{ id: number; name: string; memberCount: number; eventCount: number }>
        }
    )
}

export const createTeam = (payload: { name: string }): Promise<any> => {
  return http.post('/teams', payload).then((res) => res)
}
