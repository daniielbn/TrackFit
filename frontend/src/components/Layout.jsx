import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <h1>Training Log</h1>
          <p>{user?.name}</p>
        </div>
        <nav>
          <NavLink to="/">Dashboard</NavLink>
          <NavLink to="/activities">Actividades</NavLink>
          <NavLink to="/planned-workouts">Planificados</NavLink>
          <NavLink to="/stats">Estadísticas</NavLink>
        </nav>
        <button className="secondary-button" type="button" onClick={handleLogout}>
          Logout
        </button>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
