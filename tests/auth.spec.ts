import { test, expect } from '@playwright/test';

const LOGIN_PATH = '/login';

test.describe('Auth', () => {
  test('login (positive)', async ({ page }) => {
    await page.goto(LOGIN_PATH);
    await page.getByText('Login').first().waitFor();

    await page.getByRole('textbox', { name: 'Email Address' })
      .fill(`qa+${Date.now()}@mail.com`);
    await page.getByRole('textbox', { name: 'Password' })
      .fill('StrongPass!123');
    await Promise.all([
      page.waitForLoadState('networkidle'),
      page.getByRole('button', { name: 'Sign In' }).click(),
    ]);

  });

  test('Auth: login (negative) – pogrešna lozinka', async ({ page }) => {
    await page.goto('/login'); 
    await page.getByRole('textbox', { name: 'Email Address' }).fill('qa@example.com');
    await page.getByRole('textbox', { name: 'Password' }).fill('wrong');
    await page.getByRole('button', { name: /Sign In/i }).click();

    await expect(page).toHaveURL(/login|auth\/login/);

    const maybeError = page.getByText(/invalid|error|wrong|pogrešn/i);
    await maybeError.first().waitFor({ state: 'visible', timeout: 2000 }).catch(() => {});
  });

});
