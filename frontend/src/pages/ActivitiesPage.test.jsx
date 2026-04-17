import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import ActivitiesPage from './ActivitiesPage.jsx';

const { mockActivitiesApi } = vi.hoisted(() => ({
  mockActivitiesApi: {
    list: vi.fn(),
    remove: vi.fn()
  }
}));

vi.mock('../services/trainingApi.js', () => ({
  activitiesApi: mockActivitiesApi
}));

describe('ActivitiesPage', () => {
  beforeEach(() => {
    mockActivitiesApi.list.mockReset();
    mockActivitiesApi.remove.mockReset();
    vi.spyOn(window, 'confirm').mockReset();
  });

  it('loads and renders the authenticated user activities', async () => {
    mockActivitiesApi.list.mockResolvedValueOnce([
      {
        id: 1,
        activityDate: '2026-04-17',
        title: 'Rodaje suave',
        sportType: 'RUNNING',
        distanceKm: 8.5,
        durationMinutes: 45
      }
    ]);

    renderActivitiesPage();

    expect(await screen.findByText('Rodaje suave')).toBeInTheDocument();
    expect(screen.getByText('8.5 km')).toBeInTheDocument();
    expect(screen.getByText('45 min')).toBeInTheDocument();
  });

  it('deletes an activity after user confirmation and reloads the list', async () => {
    mockActivitiesApi.list
      .mockResolvedValueOnce([
        {
          id: 1,
          activityDate: '2026-04-17',
          title: 'Rodaje suave',
          sportType: 'RUNNING',
          distanceKm: 8.5,
          durationMinutes: 45
        }
      ])
      .mockResolvedValueOnce([]);
    mockActivitiesApi.remove.mockResolvedValueOnce();
    vi.spyOn(window, 'confirm').mockReturnValueOnce(true);
    const user = userEvent.setup();

    renderActivitiesPage();

    await screen.findByText('Rodaje suave');
    await user.click(screen.getByRole('button', { name: /eliminar/i }));

    expect(window.confirm).toHaveBeenCalled();
    expect(mockActivitiesApi.remove).toHaveBeenCalledWith(1);
    await waitFor(() => expect(mockActivitiesApi.list).toHaveBeenCalledTimes(2));
    expect(await screen.findByText(/no hay actividades/i)).toBeInTheDocument();
  });
});

function renderActivitiesPage() {
  render(
    <MemoryRouter>
      <ActivitiesPage />
    </MemoryRouter>
  );
}
