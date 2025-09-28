import { request, expect, Page } from '@playwright/test';

function safeDecodeBase64(str: string) {
  try {
    const b64 = str.replace(/-/g, '+').replace(/_/g, '/');
    const pad = b64.length % 4;
    const b64p = pad ? b64 + '='.repeat(4 - pad) : b64;
    return Buffer.from(b64p, 'base64').toString('utf8');
  } catch {
    return null;
  }
}

export async function apiLoginAndGetToken(baseURL: string, email: string, password: string) {
  const ctx = await request.newContext({ baseURL });
  const res = await ctx.post('/api/user/login', {
    data: { email, password },
    headers: { 'Content-Type': 'application/json' },
  });
  expect(res.ok(), `Login failed: ${res.status()} ${await res.text()}`).toBeTruthy();
  const body = await res.json();
  await ctx.dispose();

  const token = body.token || body.accessToken || body.jwt;
  expect(token, 'Missing token in login response').toBeTruthy();
  return token as string;
}

export function getUserIdFromJwt(jwt: string | null | undefined): string | null {
  if (!jwt) return null;
  const parts = jwt.split('.');
  if (parts.length < 2) return null;
  const json = safeDecodeBase64(parts[1]);
  if (!json) return null;
  try {
    const payload = JSON.parse(json);
    const val = payload.userId ?? payload.id ?? payload.sub ?? '';
    return val ? String(val) : null;
  } catch {
    return null;
  }
}

export async function primeFrontendAuth(page: Page, jwt: string) {
  await page.addInitScript(([k, v]) => {
    localStorage.setItem(k, v);
  }, ['jwtToken', jwt]);
}

export function buildFakeJwt(userId: number) {
  const header = Buffer.from(JSON.stringify({ alg: 'none', typ: 'JWT' })).toString('base64url');
  const payload = Buffer.from(JSON.stringify({ userId })).toString('base64url');
  return `${header}.${payload}.x`;
}

export async function loginAndPrime(page: Page) {
  const baseURL = 'http://localhost:8080';
  const email = 'testemail@gmail.com';
  const password = 'TestTest1!';

  const api = await request.newContext({ baseURL });
  const res = await api.post('/api/user/login', {
    data: { email, password },
  });
  if (!res.ok()) throw new Error(`Login failed: ${res.status()}`);

  const { token } = await res.json();

  await page.addInitScript(({ t }) => {
    localStorage.setItem('jwtToken', t);
  }, {t: token});
}

export async function ensureFormExistsViaApi(opts: {
  gatewayBase: string;   
  token: string;         
  name: string;          
}) {
  const userId = getUserIdFromJwt(opts.token);
  if (!userId) {
    throw new Error('Cannot extract userId from JWT – backend expects X-User-ID for forms.');
  }

  const ctx = await request.newContext({
    baseURL: opts.gatewayBase,
    extraHTTPHeaders: {
      Authorization: `Bearer ${opts.token}`,
      'X-User-ID': userId,               
      'Content-Type': 'application/json',
    },
  });


  const listRes = await ctx.get('/api/forms/user');
  expect(listRes.ok(), `List forms failed: ${listRes.status()} ${await listRes.text()}`).toBeTruthy();
  const list = (await listRes.json()) as any[];
  const found = Array.isArray(list) ? list.find(f => (f.name || f.title) === opts.name) : null;
  if (found) {
    await ctx.dispose();
    return found;
  }

  const payload = {
    name: opts.name,
    description: 'E2E ceo test',
    allowAnonymous: true,
    responseLimit: 0,
    locked: false,
    status: 'ACTIVE',
    visibility: 'PUBLIC',
    questions: [
      { text: 'Vaše puno ime', type: 'SHORT_TEXT', required: true, orderIndex: 0, options: [] },
      {
        text: 'Koji paket koristite?',
        type: 'SINGLE_CHOICE',
        required: true,
        orderIndex: 1,
        options: [{ text: 'Basic' }, { text: 'Pro' }],
      },
      { text: 'Ocenite uslugu (1–5)', type: 'NUMBER', required: true, orderIndex: 2, options: [] },
    ],
  };

  const createRes = await ctx.post('/api/forms', { data: payload });
  expect(createRes.ok(), `Create form failed: ${createRes.status()} ${await createRes.text()}`).toBeTruthy();
  const form = await createRes.json();
  await ctx.dispose();
  return form; 
}
