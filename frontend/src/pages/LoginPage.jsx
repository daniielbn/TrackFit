import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import ErrorMessage from '../components/ErrorMessage.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { getErrorMessage } from '../utils/errors.js';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      await login(form);
      navigate('/');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <h1>Training Log</h1>
        <h2>Login</h2>
        <ErrorMessage message={error} />
        <form onSubmit={handleSubmit} className="form">
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </label>
          <label>
            Contraseña
            <input name="password" type="password" value={form.password} onChange={handleChange} required />
          </label>
          <button type="submit" disabled={submitting}>
            {submitting ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
        <p className="auth-link">
          ¿No tienes cuenta? <Link to="/register">Crear cuenta</Link>
        </p>
      </section>
    </main>
  );
}
