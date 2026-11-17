import { useState } from 'react'
import { authApi } from './api/auth'
import { ApiError } from './api/http'

export default function App() {
  const [output, setOutput] = useState('Pulsa el botón')

  async function probar() {
    try {
      const res = await authApi.login('ana_92', 'supersecreta1')
      const me = await authApi.me(res.accessToken)
      setOutput(JSON.stringify(me, null, 2))
    } catch (e) {
      setOutput(e instanceof ApiError ? `Error ${e.status}: ${e.message}` : String(e))
    }
  }

  return (
    <div>
      <button onClick={probar}>Probar login</button>
      <pre>{output}</pre>
    </div>
  )
}