import { useEffect, useState } from 'react';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Loading from '../components/Loading.jsx';
import { plannedWorkoutsApi } from '../services/trainingApi.js';
import { getErrorMessage } from '../utils/errors.js';
import { SPORT_TYPES, WORKOUT_STATUSES } from '../utils/options.js';

const emptyForm = {
  plannedDate: '',
  title: '',
  description: '',
  sportType: 'RUNNING',
  targetDurationMinutes: '',
  targetDistanceKm: '',
  status: 'PENDING'
};

export default function PlannedWorkoutsPage() {
  const [workouts, setWorkouts] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const loadWorkouts = () => {
    plannedWorkoutsApi
      .list()
      .then(setWorkouts)
      .catch((err) => setError(getErrorMessage(err)));
  };

  useEffect(() => {
    loadWorkouts();
  }, []);

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const startEdit = (workout) => {
    setEditingId(workout.id);
    setForm({
      plannedDate: workout.plannedDate || '',
      title: workout.title || '',
      description: workout.description || '',
      sportType: workout.sportType || 'RUNNING',
      targetDurationMinutes: workout.targetDurationMinutes || '',
      targetDistanceKm: workout.targetDistanceKm || '',
      status: workout.status || 'PENDING'
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');

    const payload = {
      ...form,
      targetDurationMinutes: form.targetDurationMinutes ? Number(form.targetDurationMinutes) : null,
      targetDistanceKm: form.targetDistanceKm ? Number(form.targetDistanceKm) : null
    };

    try {
      if (editingId) {
        await plannedWorkoutsApi.update(editingId, payload);
      } else {
        await plannedWorkoutsApi.create(payload);
      }
      resetForm();
      loadWorkouts();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const updateStatus = async (id, status) => {
    try {
      await plannedWorkoutsApi.updateStatus(id, status);
      loadWorkouts();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm('¿Eliminar este entrenamiento planificado?');
    if (!confirmed) {
      return;
    }

    try {
      await plannedWorkoutsApi.remove(id);
      loadWorkouts();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  return (
    <section>
      <div className="page-header">
        <div>
          <h2>Entrenamientos planificados</h2>
          <p className="muted">Planifica próximos entrenamientos y actualiza su estado.</p>
        </div>
      </div>

      <ErrorMessage message={error} />

      <section className="section-block">
        <h3>{editingId ? 'Editar entrenamiento' : 'Nuevo entrenamiento'}</h3>
        <form className="form wide-form" onSubmit={handleSubmit}>
          <div className="form-grid">
            <label>
              Fecha
              <input name="plannedDate" type="date" value={form.plannedDate} onChange={handleChange} required />
            </label>
            <label>
              Título
              <input name="title" value={form.title} onChange={handleChange} required />
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
              Duración objetivo (min)
              <input
                name="targetDurationMinutes"
                type="number"
                min="1"
                value={form.targetDurationMinutes}
                onChange={handleChange}
              />
            </label>
            <label>
              Distancia objetivo (km)
              <input
                name="targetDistanceKm"
                type="number"
                min="0.01"
                step="0.01"
                value={form.targetDistanceKm}
                onChange={handleChange}
              />
            </label>
            <label>
              Estado
              <select name="status" value={form.status} onChange={handleChange}>
                {WORKOUT_STATUSES.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </label>
          </div>
          <label>
            Descripción
            <textarea name="description" value={form.description} onChange={handleChange} rows="3" />
          </label>
          <div className="form-actions">
            <button type="submit" disabled={submitting}>
              {submitting ? 'Guardando...' : 'Guardar'}
            </button>
            {editingId && (
              <button type="button" className="secondary-button" onClick={resetForm}>
                Cancelar edición
              </button>
            )}
          </div>
        </form>
      </section>

      {!workouts ? (
        <Loading />
      ) : workouts.length === 0 ? (
        <p className="muted">No hay entrenamientos planificados.</p>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Título</th>
                <th>Deporte</th>
                <th>Estado</th>
                <th>Objetivo</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {workouts.map((workout) => (
                <tr key={workout.id}>
                  <td>{workout.plannedDate}</td>
                  <td>{workout.title}</td>
                  <td>{workout.sportType}</td>
                  <td>{workout.status}</td>
                  <td>{formatGoal(workout)}</td>
                  <td className="actions">
                    <button type="button" className="link-button" onClick={() => startEdit(workout)}>
                      Editar
                    </button>
                    <button type="button" className="link-button" onClick={() => updateStatus(workout.id, 'DONE')}>
                      Realizado
                    </button>
                    <button type="button" className="link-button" onClick={() => updateStatus(workout.id, 'CANCELLED')}>
                      Cancelar
                    </button>
                    <button type="button" className="link-button danger" onClick={() => handleDelete(workout.id)}>
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

function formatGoal(workout) {
  const parts = [];
  if (workout.targetDistanceKm) {
    parts.push(`${workout.targetDistanceKm} km`);
  }
  if (workout.targetDurationMinutes) {
    parts.push(`${workout.targetDurationMinutes} min`);
  }
  return parts.length > 0 ? parts.join(' · ') : '-';
}
