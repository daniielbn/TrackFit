import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Loading from '../components/Loading.jsx';
import { activitiesApi } from '../services/trainingApi.js';
import { getErrorMessage } from '../utils/errors.js';

export default function ActivitiesPage() {
  const [activities, setActivities] = useState(null);
  const [error, setError] = useState('');

  const loadActivities = () => {
    activitiesApi
      .list()
      .then(setActivities)
      .catch((err) => setError(getErrorMessage(err)));
  };

  useEffect(() => {
    loadActivities();
  }, []);

  const handleDelete = async (id) => {
    const confirmed = window.confirm('¿Eliminar esta actividad?');
    if (!confirmed) {
      return;
    }

    try {
      await activitiesApi.remove(id);
      loadActivities();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  return (
    <section>
      <div className="page-header">
        <div>
          <h2>Actividades</h2>
          <p className="muted">Registro manual de entrenamientos realizados.</p>
        </div>
        <Link className="button-link" to="/activities/new">
          Nueva actividad
        </Link>
      </div>

      <ErrorMessage message={error} />
      {!activities ? (
        <Loading />
      ) : activities.length === 0 ? (
        <p className="muted">Todavía no hay actividades registradas.</p>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Título</th>
                <th>Deporte</th>
                <th>Distancia</th>
                <th>Duración</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {activities.map((activity) => (
                <tr key={activity.id}>
                  <td>{activity.activityDate}</td>
                  <td>{activity.title}</td>
                  <td>{activity.sportType}</td>
                  <td>{activity.distanceKm} km</td>
                  <td>{activity.durationMinutes} min</td>
                  <td className="actions">
                    <Link to={`/activities/${activity.id}`}>Ver</Link>
                    <Link to={`/activities/${activity.id}/edit`}>Editar</Link>
                    <button type="button" className="link-button danger" onClick={() => handleDelete(activity.id)}>
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
