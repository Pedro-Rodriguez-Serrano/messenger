import { useEffect, useState, type ReactNode } from 'react'
import { authApi, type AuthResponse, type RegisterInput, type User } from '../../api/auth'
import { AuthContext } from './authContext'

const TOKEN_KEY = 'messenger.token'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(() => localStorage.getItem(TOKEN_KEY) !== null)

  // Al arrancar: si hay token guardado, comprobar que sigue siendo válido
  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (!token) return
    authApi
      .me(token)
      .then(setUser)
      .catch(() => localStorage.removeItem(TOKEN_KEY))
      .finally(() => setLoading(false))
  }, [])

  function startSession(res: AuthResponse) {
    localStorage.setItem(TOKEN_KEY, res.accessToken)
    setUser(res.user)
  }

  async function login(identifier: string, password: string) {
    startSession(await authApi.login(identifier, password))
  }

  async function register(input: RegisterInput) {
    startSession(await authApi.register(input))
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}