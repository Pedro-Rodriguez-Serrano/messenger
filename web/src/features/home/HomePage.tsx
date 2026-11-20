import { ContactsPanel } from '../contacts/ContactsPanel'
import { useAuth } from '../auth/useAuth'

export function HomePage() {
  const { user, logout } = useAuth()
  return (
    <div>
      <header>
        <h1>Hola, {user?.displayName}</h1>
        <p>@{user?.username}</p>
        <button onClick={logout}>Cerrar sesión</button>
      </header>
      <ContactsPanel />
    </div>
  )
}