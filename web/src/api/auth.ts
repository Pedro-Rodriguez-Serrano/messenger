/*tipos y llamadas de identidad, calcados del contrato*/

import { request } from './http'

export interface User {
  id: string
  username: string
  email?: string
  displayName: string
  personalMessage?: string | null
  avatarUrl?: string | null
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: User
}

export interface RegisterInput {
  username: string
  email: string
  password: string
  displayName: string
}

export const authApi = {
  register: (input: RegisterInput) =>
    request<AuthResponse>('/api/v1/auth/register', { method: 'POST', body: input }),

  login: (identifier: string, password: string) =>
    request<AuthResponse>('/api/v1/auth/login', {
      method: 'POST',
      body: { identifier, password },
    }),

  me: (token: string) => request<User>('/api/v1/users/me', { token }),
}