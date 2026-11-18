import { useState, type SyntheticEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ApiError } from '../../api/http'
import { useAuth } from './useAuth'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  async function onSubmit(e: SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()
    setError(null)
    setBusy(true)
    try {
      await login(identifier, password)
      navigate('/', { replace: true })
    } catch (err) {
      setError(err instanceof ApiError && err.status === 401
        ? 'Usuario o contraseña incorrectos'
        : 'No se pudo iniciar sesión. ¿Está el servidor encendido?')
    } finally {
      setBusy(false)
    }
  }

  return (
    <form onSubmit={onSubmit}>
      <h1>Iniciar sesión</h1>
      <input placeholder="Usuario o email" value={identifier}
             onChange={(e) => setIdentifier(e.target.value)} required />
      <input type="password" placeholder="Contraseña" value={password}
             onChange={(e) => setPassword(e.target.value)} required />
      {error && <p role="alert">{error}</p>}
      <button disabled={busy}>{busy ? 'Entrando...' : 'Entrar'}</button>
      <p>¿No tienes cuenta? <Link to="/register">Regístrate</Link></p>
    </form>
  )
}