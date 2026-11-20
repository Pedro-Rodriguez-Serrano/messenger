import { useCallback, useEffect, useState } from 'react'
import { contactsApi, type Contact, type ContactRequest } from '../../api/contacts'
import { ApiError } from '../../api/http'
import { useAuth } from '../auth/useAuth'

const REFRESH_MS = 10_000

export function useContacts() {
  const { token, logout } = useAuth()
  const [friends, setFriends] = useState<Contact[]>([])
  const [incoming, setIncoming] = useState<ContactRequest[]>([])
  const [outgoing, setOutgoing] = useState<ContactRequest[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    if (!token) return
    try {
      const [f, i, o] = await Promise.all([
        contactsApi.listFriends(token),
        contactsApi.listRequests(token, 'incoming'),
        contactsApi.listRequests(token, 'outgoing'),
      ])
      setFriends(f)
      setIncoming(i)
      setOutgoing(o)
      setError(null)
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) logout()
      else setError('No se pudieron cargar los contactos')
    } finally {
      setLoading(false)
    }
  }, [token, logout])

  // Carga inicial y sondeo periódico (se sustituirá por WebSocket más adelante)
  useEffect(() => {
    void refresh()
    const id = setInterval(() => void refresh(), REFRESH_MS)
    return () => clearInterval(id)
  }, [refresh])

  /**
   * Ejecuta una acción y refresca las listas.
   * Devuelve null si fue bien, o el mensaje de error para mostrarlo.
   */
  async function run(action: (token: string) => Promise<unknown>): Promise<string | null> {
    if (!token) return 'No hay sesión'
    try {
      await action(token)
      await refresh()
      return null
    } catch (e) {
      if (e instanceof ApiError) {
        if (e.status === 401) logout()
        return e.message
      }
      return 'No se pudo conectar con el servidor'
    }
  }

  return {
    friends, incoming, outgoing, loading, error,
    sendRequest: (username: string) => run((t) => contactsApi.sendRequest(t, username)),
    accept: (requestId: string) => run((t) => contactsApi.accept(t, requestId)),
    rejectOrCancel: (requestId: string) => run((t) => contactsApi.deleteRequest(t, requestId)),
    removeFriend: (userId: string) => run((t) => contactsApi.removeFriend(t, userId)),
  }
}