import { test, expect, Page } from '@playwright/test';
import { loginAndPrime } from './helpers.ts';
import { log } from 'console';

const HOME = '/';
const NEW = '/forms/new';

function newFormName() {
  return `QA Forma ${Date.now()}`;
}

async function waitForFormCreate(page: Page) {
  await Promise.all([
    page.waitForResponse(r =>
      r.url().includes('/api/forms') &&
      r.request().method() === 'POST' &&
      (r.status() === 200 || r.status() === 201)
    ),
    page.waitForLoadState('networkidle'),
  ]);
}

async function waitForFormUpdate(page : Page) {
  await Promise.all([
    page.waitForResponse(r => {
      const url = r.url();
      const isMatch = /\/api\/forms\/\d+/.test(url);
      const method = r.request().method();
      return isMatch && (method === 'PUT' || method === 'PATCH') && (r.status() === 200 || r.status() === 204);
    }),
    page.waitForLoadState('networkidle'),
  ]);
}

test.describe('Forms CRUD', () => {
  test('create form (Home - /forms/new)', async ({ page }) => {
    await loginAndPrime(page);
    
    await page.goto(NEW);
    await page.waitForLoadState('domcontentloaded');

    const name = newFormName();
    await page.getByPlaceholder('Form Title').fill(name);
    await page.getByPlaceholder('Form description').fill('E2E ceo test');

    await Promise.all([
      waitForFormCreate(page),
      page.getByRole('button', { name: /Create Form/i }).click(),
    ]);

    await page.goto(HOME);
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('Recent Forms')).toBeVisible();

    const card = page.locator('li.border.rounded-lg.p-4.bg-white').first();
    await expect(card).toBeVisible({ timeout: 15000 });

    await expect(page.getByText(name).first()).toBeVisible({ timeout: 15000 });
  });

  test('edit form', async ({ page }) => {
    await loginAndPrime(page);

    await page.goto(NEW);
    await page.waitForLoadState('domcontentloaded');

    const name = newFormName();
    await page.getByPlaceholder('Form Title').fill(name);

    await Promise.all([
      waitForFormCreate(page),
      page.getByRole('button', { name: /Create Form|Creating/i }).click(),
    ]);

    await page.goto(HOME);
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('Recent Forms')).toBeVisible();

    const firstCard = page.locator('li.border.rounded-lg.p-4.bg-white').first();
    await expect(firstCard).toBeVisible({ timeout: 15000 });
    await firstCard.click();

    const edited = `Edited ${Date.now()}`;
    await page.getByPlaceholder('Form Title').fill(edited);

    await Promise.all([
      waitForFormUpdate(page),
      page.getByRole('button', { name: /Save changes|Saving/i }).click(),
    ]);

    await page.goto(HOME);
    await page.waitForLoadState('networkidle');
    await expect(page.getByText(edited).first()).toBeVisible({ timeout: 15000 });
  });
});
