import { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const [appointments, setAppointments] = useState([]);
  const [newPatient, setNewPatient] = useState('');
  const [newDoctor, setNewDoctor] = useState('');
  const [newSpecialty, setNewSpecialty] = useState('');
  const [newDate, setNewDate] = useState('');
  const [userRole, setUserRole] = useState('');
  const formRef = useRef(null);
  const firstInputRef = useRef(null);
  const navigate = useNavigate();
  const formatId = (id) => (id ? String(id) : '');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    const parseJwt = (tkn) => {
      try {
        const base64Url = tkn.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
          atob(base64)
            .split('')
            .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
            .join('')
        );
        return JSON.parse(jsonPayload);
      } catch (err) {
        return {};
      }
    };
    const payload = parseJwt(token);
    const roleFromPayload =
      payload.role ||
      (Array.isArray(payload.roles) ? payload.roles[0] : undefined) ||
      (Array.isArray(payload.authorities) ? (payload.authorities[0] || '').replace('ROLE_', '') : undefined) ||
      '';
    setUserRole(roleFromPayload);

    fetchAppointments();
  }, [navigate]);

  const fetchAppointments = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8080/api/appointments', {
        headers: { Authorization: `Bearer ${token}` }
      });
      const sorted = Array.isArray(response.data)
        ? response.data.slice().sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime))
        : [];
      setAppointments(sorted);
    } catch (err) {
      console.error(err);
    }
  };

  const nextStatus = (current) => {
    if (current === 'SCHEDULED') return 'IN_PROGRESS';
    if (current === 'IN_PROGRESS') return 'COMPLETED';
    return current;
  };

  const handleChangeStatus = async (app) => {
    try {
      const token = localStorage.getItem('token');
      const status = nextStatus(app.status);
      if (status === app.status) return;
      await axios.put(`http://localhost:8080/api/appointments/${app.id}`, {
        patientName: app.patientName,
        doctorName: app.doctorName,
        specialty: app.specialty,
        dateTime: app.dateTime,
        status
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAppointments();
    } catch (err) {
      const msg = err?.response?.data?.message || 'Erro ao alterar status.';
      alert(msg);
    }
  };

  const handleDelete = async (id) => {
    try {
      const token = localStorage.getItem('token');
      await axios.delete(`http://localhost:8080/api/appointments/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAppointments();
    } catch (err) {
      const msg = err?.response?.data?.message || 'Erro ao excluir agendamento.';
      alert(msg);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      await axios.post('http://localhost:8080/api/appointments', {
        patientName: newPatient,
        doctorName: newDoctor,
        specialty: newSpecialty,
        dateTime: `${newDate}:00`
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setNewPatient('');
      setNewDoctor('');
      setNewSpecialty('');
      setNewDate('');
      fetchAppointments();
    } catch (err) {
      const msg = err?.response?.data?.message || 'Erro ao criar agendamento.';
      alert(msg);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    navigate('/login');
  };
  const handleAgendarClick = () => {
    if (formRef.current) {
      formRef.current.scrollIntoView({ behavior: 'smooth' });
    }
    if (firstInputRef.current) {
      firstInputRef.current.focus();
    }
  };

  const canCreate = userRole === 'ADMIN' || userRole === 'RECEPTIONIST';
  const canDelete = userRole === 'ADMIN';
  const canUpdateStatus = (status) => {
    if (userRole === 'ADMIN') return true;
    if (userRole === 'RECEPTIONIST') return status !== 'COMPLETED';
    if (userRole === 'DOCTOR') return status !== 'COMPLETED';
    return false;
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-blue-600">Clínica Vida Plena - Painel</h1>
          <div className="flex items-center gap-3">
            {canCreate && (
              <button onClick={handleAgendarClick} className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">
                Agendar
              </button>
            )}
            <button onClick={handleLogout} className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">Sair</button>
          </div>
        </div>

        {canCreate && (
          <div ref={formRef} className="bg-white p-6 rounded-lg shadow mb-8">
            <h2 className="text-xl font-semibold mb-4">Novo Agendamento</h2>
            <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <input ref={firstInputRef} type="text" placeholder="Nome do Paciente" value={newPatient} onChange={(e) => setNewPatient(e.target.value)} required
                className="border p-2 rounded" />
              <input type="text" placeholder="Nome do Médico" value={newDoctor} onChange={(e) => setNewDoctor(e.target.value)} required
                className="border p-2 rounded" />
              <input type="text" placeholder="Especialidade" value={newSpecialty} onChange={(e) => setNewSpecialty(e.target.value)} required
                className="border p-2 rounded" />
              <input type="datetime-local" value={newDate} onChange={(e) => setNewDate(e.target.value)} required
                className="border p-2 rounded" />
              <button type="submit" className="bg-green-500 text-white p-2 rounded hover:bg-green-600">Agendar</button>
            </form>
          </div>
        )}

        <div className="bg-white p-6 rounded-lg shadow">
          <h2 className="text-xl font-semibold mb-4">Agendamentos</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr>
                  <th className="border-b p-2">ID</th>
                  <th className="border-b p-2">Paciente</th>
                  <th className="border-b p-2">Médico</th>
                  <th className="border-b p-2">Data/Hora</th>
                  <th className="border-b p-2">Status</th>
                  <th className="border-b p-2">Ações</th>
                </tr>
              </thead>
              <tbody>
                {appointments.map((app, idx) => (
                  <tr key={app.id} className="hover:bg-gray-50">
                    <td className="border-b p-2">{idx + 1}</td>
                    <td className="border-b p-2">{app.patientName}</td>
                    <td className="border-b p-2">{app.doctorName}</td>
                    <td className="border-b p-2">{new Date(app.dateTime).toLocaleString()}</td>
                    <td className="border-b p-2">
                      <span className={`px-2 py-1 rounded text-sm ${
                        app.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                        app.status === 'CANCELED' ? 'bg-red-100 text-red-800' :
                        'bg-yellow-100 text-yellow-800'
                      }`}>
                        {app.status}
                      </span>
                    </td>
                    <td className="border-b p-2 space-x-2">
                      <button
                        onClick={() => handleChangeStatus(app)}
                        className="px-2 py-1 bg-blue-500 text-white rounded text-sm hover:bg-blue-600 disabled:opacity-50"
                        disabled={!canUpdateStatus(app.status) || app.status === 'COMPLETED' || app.status === 'CANCELED'}
                      >
                        Alterar status
                      </button>
                      <button
                        onClick={() => handleDelete(app.id)}
                        className="px-2 py-1 bg-red-500 text-white rounded text-sm hover:bg-red-600 disabled:opacity-50"
                        disabled={!canDelete}
                      >
                        Excluir
                      </button>
                    </td>
                  </tr>
                ))}
                {appointments.length === 0 && (
                  <tr>
                    <td colSpan="6" className="p-4 text-center text-gray-500">Nenhum agendamento encontrado.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
