import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      // Assuming backend is at localhost:8080
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        username,
        password
      });
      
      localStorage.setItem('token', response.data.token);
      try {
        const token = response.data.token;
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
          atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
        );
        const payload = JSON.parse(jsonPayload);
        const role =
          payload.role ||
          (Array.isArray(payload.roles) ? payload.roles[0] : undefined) ||
          (Array.isArray(payload.authorities) ? (payload.authorities[0] || '').replace('ROLE_', '') : undefined) ||
          '';
        if (role) localStorage.setItem('role', role);
      } catch (_) {
        // ignore decode errors
      }
      navigate('/dashboard');
    } catch (err) {
      setError('Falha no login. Verifique suas credenciais.');
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="px-8 py-6 mt-4 text-left bg-white shadow-lg rounded-lg w-96">
        <h3 className="text-2xl font-bold text-center text-blue-600">Clínica Vida Plena</h3>
        <h4 className="mt-2 text-center text-gray-500">Login</h4>
        <form onSubmit={handleLogin}>
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
            {error && <p className="mt-2 text-red-500 text-sm">{error}</p>}
            <div className="flex items-baseline justify-between">
              <button className="px-6 py-2 mt-4 text-white bg-blue-600 rounded-lg hover:bg-blue-900">Entrar</button>
              <Link to="/register" className="text-sm text-blue-600 hover:underline">Criar conta</Link>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
