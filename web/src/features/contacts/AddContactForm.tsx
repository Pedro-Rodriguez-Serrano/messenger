import { useState, type SyntheticEvent } from 'react'

interface Props {
  onSend: (username: string) => Promise<string | null>
}

export function AddContactForm({ onSend }: Props) {
  const [username, setUsername] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [isError, setIsError] = useState(false)
  const [busy, setBusy] = useState(false)

  async function onSubmit(e: SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()
    setBusy(true)
    const error = await onSend(username.trim().toLowerCase())
    setBusy(false)
    setIsError(error !== null)
    setMessage(error ?? 'Solicitud enviada')
    if (!error) setUsername('')
  }

  return (
    <form onSubmit={onSubmit}>
      <input placeholder="Añadir contacto por usuario" value={username}
             onChange={(e) => setUsername(e.target.value)} required />
      <button disabled={busy}>Añadir</button>
      {message && <p role={isError ? 'alert' : 'status'}>{message}</p>}
    </form>
  )
}