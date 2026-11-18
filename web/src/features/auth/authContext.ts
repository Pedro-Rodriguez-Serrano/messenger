import { createContext } from 'react'
import type { RegisterInput, User } from '../../api/auth'

export interface AuthState {
  user: User | null
  /** true mientras se comprueba si el token guardado sigue siendo válido */
  loading: boolean
  login: (identifier: string, password: string) => Promise<void>
  register: (input: RegisterInput) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthState | null>(null)