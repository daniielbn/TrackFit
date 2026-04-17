import { createContext, useContext, useMemo, useState } from 'react';
import { authApi } from '../services/trainingApi.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('training_token'));
  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem('training_user');
    return storedUser ? JSON.parse(storedUser) : null;
  });

  const saveSession = (authResponse) => {
    localStorage.setItem('training_token', authResponse.token);
    localStorage.setItem('training_user', JSON.stringify(authResponse.user));
    setToken(authResponse.token);
    setUser(authResponse.user);
  };

  const register = async (payload) => {
    const authResponse = await authApi.register(payload);
    saveSession(authResponse);
  };

  const login = async (payload) => {
    const authResponse = await authApi.login(payload);
    saveSession(authResponse);
  };

  const logout = () => {
    localStorage.removeItem('training_token');
    localStorage.removeItem('training_user');
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({
      token,
      user,
      isAuthenticated: Boolean(token),
      register,
      login,
      logout
    }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
