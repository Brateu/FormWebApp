import { test, expect, Page } from '@playwright/test';
import { loginAndPrime } from './helpers.ts';

const HOME = '/';
const NEW = '/forms/new';

function uniqName() {
  return `QA Forma ${Date.now()}`;
}

async function waitForFormCreate(page : Page) {
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

test('Forms CRUD - create + add question + save + edit title', async ({ page }) => {
  await loginAndPrime(page);

  await page.goto(NEW);
  await page.waitForLoadState('domcontentloaded');

  const name = uniqName();
  await page.getByPlaceholder('Form Title').fill(name);
  await page.getByPlaceholder('Form description').fill('E2E ceo test');

  const addBtn = page.getByText(/Add Question/i);
  if (await addBtn.count()) {
    await addBtn.click();
  }

  await Promise.all([
    waitForFormCreate(page),
    page.getByRole('button', { name: /Create Form|Creating/i }).click(),
  ]);

  await page.goto(HOME);
  await page.waitForLoadState('networkidle');
  await expect(page.getByText('Recent Forms')).toBeVisible({ timeout: 15000 });
  await expect(page.getByText(name).first()).toBeVisible({ timeout: 15000 });

  if (await page.getByText(name).first().count()) {
    await page.getByText(name).first().click();
  } else {
    await page.locator('li.border.rounded-lg.p-4.bg-white').first().click();
  }

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
