import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import PrivateRoute from './components/PrivateRoute.jsx';
import ActivitiesPage from './pages/ActivitiesPage.jsx';
import ActivityDetailPage from './pages/ActivityDetailPage.jsx';
import ActivityFormPage from './pages/ActivityFormPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import PlannedWorkoutsPage from './pages/PlannedWorkoutsPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import StatsPage from './pages/StatsPage.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/"
        element={
          <PrivateRoute>
            <Layout />
          </PrivateRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="activities" element={<ActivitiesPage />} />
        <Route path="activities/new" element={<ActivityFormPage />} />
        <Route path="activities/:id" element={<ActivityDetailPage />} />
        <Route path="activities/:id/edit" element={<ActivityFormPage />} />
        <Route path="planned-workouts" element={<PlannedWorkoutsPage />} />
        <Route path="stats" element={<StatsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
