import { useState } from 'react'
import { AddContactForm } from './AddContactForm'
import { useContacts } from './useContacts'

export function ContactsPanel() {
  const {
    friends, incoming, outgoing, loading, error,
    sendRequest, accept, rejectOrCancel, removeFriend,
  } = useContacts()
  const [actionError, setActionError] = useState<string | null>(null)

  async function handle(action: Promise<string | null>) {
    setActionError(await action)
  }

  if (loading) return <p>Cargando contactos...</p>

  return (
    <section>
      <AddContactForm onSend={sendRequest} />
      {error && <p role="alert">{error}</p>}
      {actionError && <p role="alert">{actionError}</p>}

      <h2>Solicitudes recibidas ({incoming.length})</h2>
      {incoming.length === 0 && <p>No tienes solicitudes pendientes.</p>}
      <ul>
        {incoming.map((r) => (
          <li key={r.id}>
            {r.user.displayName} (@{r.user.username}){' '}
            <button onClick={() => handle(accept(r.id))}>Aceptar</button>{' '}
            <button onClick={() => handle(rejectOrCancel(r.id))}>Rechazar</button>
          </li>
        ))}
      </ul>

      <h2>Solicitudes enviadas ({outgoing.length})</h2>
      <ul>
        {outgoing.map((r) => (
          <li key={r.id}>
            {r.user.displayName} (@{r.user.username}){' '}
            <button onClick={() => handle(rejectOrCancel(r.id))}>Cancelar</button>
          </li>
        ))}
      </ul>

      <h2>Mis amigos ({friends.length})</h2>
      {friends.length === 0 && <p>Todavía no tienes amigos. Añade a alguien por su usuario.</p>}
      <ul>
        {friends.map((c) => (
          <li key={c.user.id}>
            <strong>{c.user.displayName}</strong> (@{c.user.username})
            {c.user.personalMessage && <em> — {c.user.personalMessage}</em>}{' '}
            <button
              onClick={() => {
                if (window.confirm(`¿Eliminar a ${c.user.displayName}?`)) {
                  void handle(removeFriend(c.user.id))
                }
              }}
            >
              Eliminar
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}