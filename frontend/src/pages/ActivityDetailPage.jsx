import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Loading from '../components/Loading.jsx';
import { activitiesApi } from '../services/trainingApi.js';
import { getErrorMessage } from '../utils/errors.js';

export default function ActivityDetailPage() {
  const { id } = useParams();
  const [activity, setActivity] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    activitiesApi
      .get(id)
      .then(setActivity)
      .catch((err) => setError(getErrorMessage(err)));
  }, [id]);

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!activity) {
    return <Loading />;
  }

  return (
    <section>
      <div className="page-header">
        <div>
          <h2>{activity.title}</h2>
          <p className="muted">
            {activity.activityDate} · {activity.sportType}
          </p>
        </div>
        <Link className="button-link" to={`/activities/${activity.id}/edit`}>
          Editar
        </Link>
      </div>

      <div className="detail-grid">
        <Detail label="Distancia" value={`${activity.distanceKm} km`} />
        <Detail label="Duración" value={`${activity.durationMinutes} min`} />
        <Detail label="Ritmo medio" value={activity.averagePace ? `${activity.averagePace}` : '-'} />
        <Detail label="Ubicación" value={activity.location || '-'} />
      </div>

      <section className="section-block">
        <h3>Descripción</h3>
        <p>{activity.description || '-'}</p>
      </section>
      <section className="section-block">
        <h3>Notas</h3>
        <p>{activity.notes || '-'}</p>
      </section>
    </section>
  );
}

function Detail({ label, value }) {
  return (
    <article className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}
