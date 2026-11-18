import { useState, type SyntheticEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ApiError } from '../../api/http'
import { useAuth } from './useAuth'

export function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', email: '', password: '', displayName: '' })
  const [errors, setErrors] = useState<string[]>([])
  const [busy, setBusy] = useState(false)

  function set(field: keyof typeof form) {
    return (e: React.ChangeEvent<HTMLInputElement>) =>
      setForm({ ...form, [field]: e.target.value })
  }

  async function onSubmit(e: SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()
    setErrors([])
    setBusy(true)
    try {
      await register(form)
      navigate('/', { replace: true })
    } catch (err) {
      if (err instanceof ApiError && err.problem?.errors?.length) {
        setErrors(err.problem.errors.map((x) => `${x.field}: ${x.message}`))
      } else if (err instanceof ApiError) {
        setErrors([err.message])
      } else {
        setErrors(['No se pudo registrar. ¿Está el servidor encendido?'])
      }
    } finally {
      setBusy(false)
    }
  }

  return (
    <form onSubmit={onSubmit}>
      <h1>Crear cuenta</h1>
      <input placeholder="Usuario (minúsculas, números, . y _)" value={form.username}
             onChange={set('username')} required />
      <input type="email" placeholder="Email" value={form.email}
             onChange={set('email')} required />
      <input type="password" placeholder="Contraseña (8-72 caracteres)" value={form.password}
             onChange={set('password')} required />
      <input placeholder="Nombre visible" value={form.displayName}
             onChange={set('displayName')} required />
      {errors.length > 0 && (
        <ul role="alert">{errors.map((m) => <li key={m}>{m}</li>)}</ul>
      )}
      <button disabled={busy}>{busy ? 'Creando...' : 'Registrarme'}</button>
      <p>¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link></p>
    </form>
  )
}