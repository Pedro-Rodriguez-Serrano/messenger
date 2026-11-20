import { request } from './http'

/** Datos públicos de un usuario (sin email). Igual que UserSummary del contrato. */
export interface UserSummary {
  id: string
  username: string
  displayName: string
  personalMessage?: string | null
  avatarUrl?: string | null
}

export interface Contact {
  user: UserSummary
  since: string
}

export interface ContactRequest {
  id: string
  user: UserSummary
  createdAt: string
}

export const contactsApi = {
  listFriends: (token: string) => request<Contact[]>('/api/v1/contacts', { token }),

  listRequests: (token: string, direction: 'incoming' | 'outgoing') =>
    request<ContactRequest[]>(`/api/v1/contacts/requests?direction=${direction}`, { token }),

  sendRequest: (token: string, username: string) =>
    request<ContactRequest>('/api/v1/contacts/requests', {
      method: 'POST',
      body: { username },
      token,
    }),

  accept: (token: string, requestId: string) =>
    request<Contact>(`/api/v1/contacts/requests/${requestId}/accept`, { method: 'POST', token }),

  deleteRequest: (token: string, requestId: string) =>
    request<void>(`/api/v1/contacts/requests/${requestId}`, { method: 'DELETE', token }),

  removeFriend: (token: string, userId: string) =>
    request<void>(`/api/v1/contacts/${userId}`, { method: 'DELETE', token }),
}