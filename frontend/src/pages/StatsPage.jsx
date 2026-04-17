import { useEffect, useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Loading from '../components/Loading.jsx';
import { statsApi } from '../services/trainingApi.js';
import { getErrorMessage } from '../utils/errors.js';

export default function StatsPage() {
  const [monthly, setMonthly] = useState(null);
  const [sports, setSports] = useState(null);
  const [paceSummary, setPaceSummary] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([statsApi.monthly(), statsApi.sports(), statsApi.paceSummary()])
      .then(([monthlyData, sportsData, paceData]) => {
        setMonthly(monthlyData);
        setSports(sportsData);
        setPaceSummary(paceData);
      })
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!monthly || !sports || !paceSummary) {
    return <Loading />;
  }

  return (
    <section>
      <div className="page-header">
        <div>
          <h2>Estadísticas</h2>
          <p className="muted">Agregaciones básicas de tus actividades.</p>
        </div>
      </div>

      <div className="metric-grid compact">
        <article className="metric-card">
          <span>Ritmo medio general</span>
          <strong>{paceSummary.averagePace ? paceSummary.averagePace : '-'}</strong>
        </article>
      </div>

      <section className="section-block">
        <h3>Distancia por mes</h3>
        {monthly.length === 0 ? (
          <p className="muted">No hay datos suficientes.</p>
        ) : (
          <div className="chart-box">
            <ResponsiveContainer width="100%" height={280}>
              <LineChart data={monthly}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="distanceKm" stroke="#2563eb" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        )}
      </section>

      <section className="section-block">
        <h3>Tiempo por mes</h3>
        {monthly.length === 0 ? (
          <p className="muted">No hay datos suficientes.</p>
        ) : (
          <div className="chart-box">
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={monthly}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="durationMinutes" fill="#16a34a" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </section>

      <section className="section-block">
        <h3>Actividades por deporte</h3>
        {sports.length === 0 ? (
          <p className="muted">No hay datos suficientes.</p>
        ) : (
          <div className="chart-box">
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={sports}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="sportType" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="activities" fill="#f97316" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </section>
    </section>
  );
}
