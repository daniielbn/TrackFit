import { useEffect, useState } from 'react';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Loading from '../components/Loading.jsx';
import { dashboardApi } from '../services/trainingApi.js';
import { getErrorMessage } from '../utils/errors.js';

export default function DashboardPage() {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    dashboardApi
      .getSummary()
      .then(setSummary)
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!summary) {
    return <Loading />;
  }

  return (
    <section>
      <div className="page-header">
        <div>
          <h2>Dashboard</h2>
          <p className="muted">Resumen general y próximos entrenamientos.</p>
        </div>
      </div>

      <div className="metric-grid">
        <Metric label="Actividades" value={summary.totalActivities} />
        <Metric label="Distancia total" value={`${summary.totalDistanceKm} km`} />
        <Metric label="Tiempo total" value={`${summary.totalDurationMinutes} min`} />
        <Metric label="Actividades este mes" value={summary.activitiesThisMonth} />
        <Metric label="Distancia este mes" value={`${summary.distanceThisMonth} km`} />
        <Metric label="Tiempo este mes" value={`${summary.durationThisMonth} min`} />
      </div>

      <section className="section-block">
        <h3>Próximos entrenamientos</h3>
        {summary.upcomingWorkouts.length === 0 ? (
          <p className="muted">No hay entrenamientos pendientes.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Título</th>
                  <th>Deporte</th>
                  <th>Objetivo</th>
                </tr>
              </thead>
              <tbody>
                {summary.upcomingWorkouts.map((workout) => (
                  <tr key={workout.id}>
                    <td>{workout.plannedDate}</td>
                    <td>{workout.title}</td>
                    <td>{workout.sportType}</td>
                    <td>{formatWorkoutGoal(workout)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </section>
  );
}

function Metric({ label, value }) {
  return (
    <article className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function formatWorkoutGoal(workout) {
  const parts = [];
  if (workout.targetDistanceKm) {
    parts.push(`${workout.targetDistanceKm} km`);
  }
  if (workout.targetDurationMinutes) {
    parts.push(`${workout.targetDurationMinutes} min`);
  }
  return parts.length > 0 ? parts.join(' · ') : '-';
}
