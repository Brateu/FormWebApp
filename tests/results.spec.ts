import { test, expect, Page } from '@playwright/test';
import { loginAndPrime } from './helpers.ts';

const HOME = '/';
const NEW = '/forms/new';
const QA_FORM_NAME = `QA Forma ${Date.now()}`;

async function ensureFormViaUI(page : Page) {
  await loginAndPrime(page);

  await page.goto(NEW);
  await page.getByPlaceholder('Form Title').fill(QA_FORM_NAME);
  await page.getByPlaceholder('Form description').fill('E2E ceo test');
  await page.getByRole('button', { name: /Create Form|Creating/i }).click();

  await page.goto(HOME);
  await expect(page.getByText('Recent Forms')).toBeVisible();

  const nameNode = page.getByText(QA_FORM_NAME).first();
  if (!(await nameNode.count())) {
    await page.reload();
  }
  if (await nameNode.count()) {
    await expect(nameNode).toBeVisible({ timeout: 15000 });
  }
}

async function openResultsTab(page : Page) {
  const byTestId = page.getByTestId('responses');
  if (await byTestId.count()) {
    await byTestId.click();
    return;
  }
 
  const byLink = page.getByRole('link', { name: /responses|results|rezultati/i });
  if (await byLink.count()) {
    await byLink.first().click();
    return;
  }
  
  const byText = page.getByText(/responses/i);
  if (await byText.count()) {
    await byText.first().click();
    return;
  }

  const m = page.url().match(/\/forms\/(\d+)\//);
  if (m) {
    await page.goto(`/forms/${m[1]}/responses`);
  } else {
    throw new Error('Cannot locate results tab/link and no form id in URL');
  }
}

test.beforeAll(async ({ browser }) => {
  const ctx = await browser.newContext();
  const page = await ctx.newPage();
  await ensureFormViaUI(page);
  await ctx.close();
});

test.describe('Results and export', () => {
  test('view list and details', async ({ page }) => {
    await loginAndPrime(page);
    await page.goto(HOME);

    if (await page.getByText(QA_FORM_NAME).first().count()) {
      await page.getByText(QA_FORM_NAME).first().click();
    } else {
      await page.locator('li.border.rounded-lg.p-4.bg-white').first().click();
    }

    await page.waitForURL(/\/forms\/\d+\/(edit|responses)/, { timeout: 10000 });

    await openResultsTab(page);

    await expect(page.getByRole('button', { name: /export xlsx/i })).toBeVisible({ timeout: 15000 });
  });

  test('export XLSX', async ({ page }) => {
    await loginAndPrime(page);
    await page.goto(HOME);

    if (await page.getByText(QA_FORM_NAME).first().count()) {
      await page.getByText(QA_FORM_NAME).first().click();
    } else {
      await page.locator('li.border.rounded-lg.p-4.bg-white').first().click();
    }

    await page.waitForURL(/\/forms\/\d+\/(edit|responses)/, { timeout: 10000 });

    await openResultsTab(page);

    const [download] = await Promise.all([
      page.waitForEvent('download'),
      page.getByRole('button', { name: /export xlsx/i }).click(),
    ]);
    const p = await download.path();
    expect(p).toBeTruthy();
  });
});
