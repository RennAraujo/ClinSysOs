import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

export default function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('RECEPTIONIST');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/auth/register', {
        username,
        password,
        role
      });
      alert('Registration successful! Please login.');
      navigate('/login');
    } catch (err) {
      setError('Registration failed. Try again.');
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="px-8 py-6 mt-4 text-left bg-white shadow-lg rounded-lg w-96">
        <h3 className="text-2xl font-bold text-center text-blue-600">Clínica Vida Plena</h3>
        <h4 className="mt-2 text-center text-gray-500">Cadastro</h4>
        <form onSubmit={handleRegister}>
          <div className="mt-4">
            <div>
              <label className="block" htmlFor="username">Usuário</label>
              <input type="text" placeholder="Usuário"
                className="w-full px-4 py-2 mt-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-600"
                value={username} onChange={(e) => setUsername(e.target.value)} required />
            </div>
            <div className="mt-4">
              <label className="block" htmlFor="password">Senha</label>
              <input type="password" placeholder="Senha"
                className="w-full px-4 py-2 mt-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-600"
                value={password} onChange={(e) => setPassword(e.target.value)} required />
            </div>
            <div className="mt-4">
              <label className="block" htmlFor="role">Perfil</label>
              <select className="w-full px-4 py-2 mt-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-600"
                value={role} onChange={(e) => setRole(e.target.value)}>
                <option value="RECEPTIONIST">Recepcionista</option>
                <option value="DOCTOR">Médico</option>
                <option value="ADMIN">Administrador</option>
              </select>
            </div>
            {error && <p className="mt-2 text-red-500 text-sm">{error}</p>}
            <div className="flex items-baseline justify-between">
              <button className="px-6 py-2 mt-4 text-white bg-blue-600 rounded-lg hover:bg-blue-900">Cadastrar</button>
              <Link to="/login" className="text-sm text-blue-600 hover:underline">Já tenho conta</Link>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
