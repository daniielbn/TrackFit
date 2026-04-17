import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import LoginPage from './LoginPage.jsx';

const { mockLogin } = vi.hoisted(() => ({
  mockLogin: vi.fn()
}));

vi.mock('../context/AuthContext.jsx', () => ({
  useAuth: () => ({
    login: mockLogin
  })
}));

describe('LoginPage', () => {
  beforeEach(() => {
    mockLogin.mockReset();
  });

  it('submits credentials and redirects after a successful login', async () => {
    mockLogin.mockResolvedValueOnce();
    const user = userEvent.setup();

    renderLoginPage();

    await user.type(screen.getByLabelText(/email/i), 'daniel@gmail.com');
    await user.type(screen.getByLabelText(/contrase/i), '123456');
    await user.click(screen.getByRole('button', { name: /entrar/i }));

    expect(mockLogin).toHaveBeenCalledWith({
      email: 'daniel@gmail.com',
      password: '123456'
    });
    expect(await screen.findByText('Dashboard mock')).toBeInTheDocument();
  });

  it('shows a visible error when login fails', async () => {
    mockLogin.mockRejectedValueOnce({
      response: {
        data: {
          message: 'Invalid email or password'
        }
      }
    });
    const user = userEvent.setup();

    renderLoginPage();

    await user.type(screen.getByLabelText(/email/i), 'wrong@gmail.com');
    await user.type(screen.getByLabelText(/contrase/i), '123456');
    await user.click(screen.getByRole('button', { name: /entrar/i }));

    expect(await screen.findByText('Invalid email or password')).toBeInTheDocument();
  });
});

function renderLoginPage() {
  render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<h1>Dashboard mock</h1>} />
      </Routes>
    </MemoryRouter>
  );
}
