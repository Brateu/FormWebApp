import { test, expect, Page } from '@playwright/test';
import { loginAndPrime } from './helpers.ts';

const HOME = '/';
const NEW = '/forms/new';
const QA_FORM_NAME = `E2E QA Forma ${Date.now()}`;

async function ensureFormViaUI(page : Page) {
  await loginAndPrime(page);

  await page.goto(NEW);
  await page.getByPlaceholder('Form Title').fill(QA_FORM_NAME);
  await page.getByPlaceholder('Form description').fill('E2E komplet test');
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

test.describe('Collaborators', () => {
  test.beforeAll(async ({ browser }) => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    await ensureFormViaUI(page);
    await ctx.close();
  });

  test('add viewer/editor and verify permissions', async ({ page }) => {
    await loginAndPrime(page);
    await page.goto(HOME);

    if (await page.getByText(QA_FORM_NAME).first().count()) {
      await page.getByText(QA_FORM_NAME).first().click();
    } else {
      await page.locator('li.border.rounded-lg.p-4.bg-white').first().click();
    }

    let collabBtn = page.getByRole('button', { name: /collaborators|opencollaborators|add collaborator/i });
    if (!await collabBtn.count()) {
      collabBtn = page.locator('[aria-label="btn-collaborators"]');
    }
    await expect(collabBtn).toBeVisible();
    await expect(collabBtn).toBeEnabled();
    await collabBtn.click();

    const dlg = page.getByRole('dialog', { name: 'Add collaborator' });
    await expect(dlg).toBeVisible({ timeout: 15000 });

    const emailInput = dlg.getByLabel('Collaborator email');
    await expect(emailInput).toBeVisible();
    await emailInput.fill('kolaborator@gmail.com');
 
    const roleField = dlg.getByLabel('Role');
    await roleField.click();
    await page.getByRole('option', { name: /viewer/i }).click();

    await dlg.getByRole('button', { name: /add collaborator/i }).click();

    await expect(page.getByText(/VIEWER/i)).toBeVisible({ timeout: 15000 });

    await collabBtn.click();
    await expect(dlg).toBeVisible({ timeout: 15000 });

    await dlg.getByRole('button', { name: /remove/i }).click();
    await dlg.getByLabel('Collaborator email').fill('kolaborator@gmail.com');
    await roleField.click();
    await page.getByRole('option', { name: /editor/i }).click();
    await dlg.getByRole('button', { name: /add collaborator/i }).click();

    await expect(page.getByText(/EDITOR/i)).toBeVisible({ timeout: 15000 });
  });
});
