// Proxy route to forward requests from the Next.js frontend to the API Gateway
// This avoids browser CORS issues in development when the gateway is on a different port.
export async function GET(request) {
  try {
    const gatewayUrl = process.env.NEXT_PUBLIC_GATEWAY_URL || 'http://localhost:8080'
    const target = `${gatewayUrl}/api/v1/merchandise/products`

    const res = await fetch(target, {
      method: 'GET',
      headers: {
        accept: 'application/json',
      },
      // server-side fetch does not include browser credentials
    })

    const body = await res.arrayBuffer()
    const headers = new Headers()
    headers.set('content-type', res.headers.get('content-type') || 'application/json')

    return new Response(body, { status: res.status, headers })
  } catch (err) {
    const message = err && err.message ? err.message : String(err)
    return new Response(JSON.stringify({ error: 'proxy_error', message }), { status: 502, headers: { 'content-type': 'application/json' } })
  }
}

export async function OPTIONS() {
  // simple options response for browsers that might preflight to the same-origin proxy
  return new Response(null, { status: 204, headers: { 'allow': 'GET,OPTIONS' } })
}
