import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './useAuth'

/** Deja pasar solo si hay sesión; si no, redirige a /login. */
export function ProtectedRoute() {
  const { user, loading } = useAuth()
  if (loading) return <p>Cargando...</p>
  return user ? <Outlet /> : <Navigate to="/login" replace />
}