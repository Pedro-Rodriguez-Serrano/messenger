import { useCallback, useEffect, useState, type ReactNode } from 'react'
import { authApi, type AuthResponse, type RegisterInput, type User } from '../../api/auth'
import { AuthContext } from './authContext'

const TOKEN_KEY = 'messenger.token'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(TOKEN_KEY))
  const [loading, setLoading] = useState(() => localStorage.getItem(TOKEN_KEY) !== null)

  // Al arrancar: si hay token guardado, comprobar que sigue siendo válido
  useEffect(() => {
    const saved = localStorage.getItem(TOKEN_KEY)
    if (!saved) return
    authApi
      .me(saved)
      .then(setUser)
      .catch(() => {
        localStorage.removeItem(TOKEN_KEY)
        setToken(null)
      })
      .finally(() => setLoading(false))
  }, [])

  function startSession(res: AuthResponse) {
    localStorage.setItem(TOKEN_KEY, res.accessToken)
    setToken(res.accessToken)
    setUser(res.user)
  }

  async function login(identifier: string, password: string) {
    startSession(await authApi.login(identifier, password))
  }

  async function register(input: RegisterInput) {
    startSession(await authApi.register(input))
  }

  // useCallback: la función mantiene la misma identidad entre renders,
  // así los hooks que dependen de ella no se reejecutan sin parar
  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    setToken(null)
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}