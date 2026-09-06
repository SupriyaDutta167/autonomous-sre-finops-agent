import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { DataProvider } from './contexts/DataContext';
import { PreferencesProvider } from './contexts/PreferencesContext';
import AppLayout from './layouts/AppLayout';

import Welcome from './pages/Welcome';
import Overview from './pages/Overview';
import Incidents from './pages/Incidents';
import IncidentDetails from './pages/IncidentDetails';
import Infrastructure from './pages/Infrastructure';
import InfrastructureDetails from './pages/InfrastructureDetails';
import Agent from './pages/Agent';
import Safety from './pages/Safety';
import FinOps from './pages/FinOps';
import Activity from './pages/Activity';
import Settings from './pages/Settings';

export default function App() {
  return (
    <PreferencesProvider>
      <DataProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Welcome />} />
            
            <Route element={<AppLayout />}>
              <Route path="/overview" element={<Overview />} />
              <Route path="/incidents" element={<Incidents />} />
              <Route path="/incidents/:id" element={<IncidentDetails />} />
              <Route path="/infrastructure" element={<Infrastructure />} />
              <Route path="/infrastructure/:instanceName" element={<InfrastructureDetails />} />
              <Route path="/agent" element={<Agent />} />
              <Route path="/safety" element={<Safety />} />
              <Route path="/finops" element={<FinOps />} />
              <Route path="/activity" element={<Activity />} />
              <Route path="/settings" element={<Settings />} />
            </Route>
            
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </DataProvider>
    </PreferencesProvider>
  );
}
