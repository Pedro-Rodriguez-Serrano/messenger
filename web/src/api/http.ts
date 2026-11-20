/* cliente base */

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

/** Error en formato Problem Details (RFC 9457), como define el contrato. */
export interface Problem {
  type?: string
  title: string
  status: number
  detail?: string
  errors?: { field: string; message: string }[]
}

export class ApiError extends Error {
  status: number
  problem: Problem | null

  constructor(status: number, problem: Problem | null) {
    super(problem?.detail ?? problem?.title ?? `Error ${status}`)
    this.status = status
    this.problem = problem
  }
}

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'
  body?: unknown
  token?: string
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {}
  if (options.body !== undefined) headers['Content-Type'] = 'application/json'
  if (options.token) headers['Authorization'] = `Bearer ${options.token}`

  const response = await fetch(`${BASE_URL}${path}`, {
    method: options.method ?? 'GET',
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  })

  if (!response.ok) {
    const problem = await response.json().catch(() => null)
    throw new ApiError(response.status, problem)
  }

  if (response.status === 204) return undefined as T
  
  return response.json() as Promise<T>
}