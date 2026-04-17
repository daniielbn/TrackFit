import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import ErrorMessage from '../components/ErrorMessage.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { getErrorMessage } from '../utils/errors.js';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '' });
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
      await register(form);
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
        <h2>Registro</h2>
        <ErrorMessage message={error} />
        <form onSubmit={handleSubmit} className="form">
          <label>
            Nombre
            <input name="name" value={form.name} onChange={handleChange} required />
          </label>
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </label>
          <label>
            Contraseña
            <input name="password" type="password" minLength="6" value={form.password} onChange={handleChange} required />
          </label>
          <button type="submit" disabled={submitting}>
            {submitting ? 'Creando...' : 'Crear cuenta'}
          </button>
        </form>
        <p className="auth-link">
          ¿Ya tienes cuenta? <Link to="/login">Entrar</Link>
        </p>
      </section>
    </main>
  );
}
