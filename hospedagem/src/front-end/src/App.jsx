import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import AppLayout from './components/AppLayout';
import ToastHost from './components/ToastHost';
import AlugueisPage from './pages/alugueis/AlugueisPage';
import ClientesPage from './pages/clientes/ClientesPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import LoginPage from './pages/login/LoginPage';
import QuartosPage from './pages/quartos/QuartosPage';
import ResidenciasPage from './pages/residencias/ResidenciasPage';

function App() {
  return (
    <BrowserRouter>
      <ToastHost />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<AppLayout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/residencias" element={<ResidenciasPage />} />
          <Route path="/quartos" element={<QuartosPage />} />
          <Route path="/clientes" element={<ClientesPage />} />
          <Route path="/alugueis" element={<AlugueisPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
