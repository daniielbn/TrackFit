import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Loading from '../components/Loading.jsx';
import { activitiesApi } from '../services/trainingApi.js';
import { getErrorMessage } from '../utils/errors.js';
import { SPORT_TYPES } from '../utils/options.js';

const emptyForm = {
  activityDate: '',
  sportType: 'RUNNING',
  title: '',
  description: '',
  durationMinutes: '',
  distanceKm: '',
  averagePace: '',
  location: '',
  notes: ''
};

export default function ActivityFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = Boolean(id);
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(isEditing);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!isEditing) {
      return;
    }

    activitiesApi
      .get(id)
      .then((activity) => {
        setForm({
          activityDate: activity.activityDate || '',
          sportType: activity.sportType || 'RUNNING',
          title: activity.title || '',
          description: activity.description || '',
          durationMinutes: activity.durationMinutes || '',
          distanceKm: activity.distanceKm || '',
          averagePace: activity.averagePace || '',
          location: activity.location || '',
          notes: activity.notes || ''
        });
      })
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, [id, isEditing]);

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');

    const payload = {
      ...form,
      durationMinutes: Number(form.durationMinutes),
      distanceKm: Number(form.distanceKm),
      averagePace: form.averagePace ? Number(form.averagePace) : null
    };

    try {
      if (isEditing) {
        await activitiesApi.update(id, payload);
      } else {
        await activitiesApi.create(payload);
      }
      navigate('/activities');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <section>
      <div className="page-header">
        <div>
          <h2>{isEditing ? 'Editar actividad' : 'Nueva actividad'}</h2>
          <p className="muted">Completa los datos básicos del entrenamiento.</p>
        </div>
      </div>

      <ErrorMessage message={error} />
      <form className="form wide-form" onSubmit={handleSubmit}>
        <div className="form-grid">
          <label>
            Fecha
            <input name="activityDate" type="date" value={form.activityDate} onChange={handleChange} required />
          </label>
          <label>
            Deporte
            <select name="sportType" value={form.sportType} onChange={handleChange} required>
              {SPORT_TYPES.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </label>
          <label>
            Título
            <input name="title" value={form.title} onChange={handleChange} required />
          </label>
          <label>
            Duración (min)
            <input
              name="durationMinutes"
              type="number"
              min="1"
              value={form.durationMinutes}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Distancia (km)
            <input
              name="distanceKm"
              type="number"
              min="0"
              step="0.01"
              value={form.distanceKm}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Ritmo medio
            <input name="averagePace" type="number" min="0.01" step="0.01" value={form.averagePace} onChange={handleChange} />
          </label>
          <label>
            Ubicación
            <input name="location" value={form.location} onChange={handleChange} />
          </label>
        </div>

        <label>
          Descripción
          <textarea name="description" value={form.description} onChange={handleChange} rows="3" />
        </label>
        <label>
          Notas
          <textarea name="notes" value={form.notes} onChange={handleChange} rows="3" />
        </label>

        <div className="form-actions">
          <button type="submit" disabled={submitting}>
            {submitting ? 'Guardando...' : 'Guardar'}
          </button>
          <Link className="secondary-link" to="/activities">
            Cancelar
          </Link>
        </div>
      </form>
    </section>
  );
}
