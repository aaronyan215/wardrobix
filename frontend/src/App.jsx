import React, { useState, useEffect } from 'react';

// Login form component
function Login({ onLogin, switchToSignup }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = e => {
    e.preventDefault();
    onLogin(username, password);
  };

  return (
    <form 
      onSubmit={handleSubmit} 
      style={{ 
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '12px',
        marginBottom: 20, 
        color: '#ddd' 
      }}
    >
      <input
        placeholder="Username"
        value={username}
        onChange={e => setUsername(e.target.value)}
        required
        className="cred-input"
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={e => setPassword(e.target.value)}
        required
        className="cred-input"
      />
      <button
        type="submit"
        className="auth-button-main"
        onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#a79eff')}
        onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#7c7aff')}
      >
        Login
      </button>
      <button
        type="button"
        onClick={switchToSignup}
        className="auth-button-secondary"
        onMouseEnter={e => {
          e.currentTarget.style.textDecoration = 'underline';
          e.currentTarget.style.color = 'white';
        }}
        onMouseLeave={e => {
          e.currentTarget.style.textDecoration = 'none';
          e.currentTarget.style.color = '#a79eff';
        }}
      >
        Sign Up
      </button>
    </form>
  );
}

// Signup form component
function Signup({ onSignup, switchToLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    try {
      const result = await onSignup(username, password);
      if (result === true) {
        setSuccess('Account created! You can now return to log in.');
        setUsername('');
        setPassword('');
      } else {
        setError('Sorry! That username is already taken.');
      }
    } catch (err) {
      setError('Signup failed due to network error.');
    }
  };

  return (
    <form 
      onSubmit={handleSubmit} 
      style={{ 
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '12px',
        marginBottom: 20, 
        color: '#ddd' 
      }}
    >
      <input
        placeholder="Your Username"
        value={username}
        onChange={e => setUsername(e.target.value)}
        required
        className="cred-input"
      />
      <input
        type="password"
        placeholder="Create a Password"
        value={password}
        onChange={e => setPassword(e.target.value)}
        required
        className="cred-input"
      />
      <button
        type="submit"
        className="auth-button-main"
        onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#a79eff')}
        onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#7c7aff')}
      >
        Sign Up
      </button>
      <button
        type="button"
        onClick={switchToLogin}
        className="auth-button-secondary"
        onMouseEnter={e => {
          e.currentTarget.style.textDecoration = 'underline';
          e.currentTarget.style.color = 'white';
        }}
        onMouseLeave={e => {
          e.currentTarget.style.textDecoration = 'none';
          e.currentTarget.style.color = '#a79eff';
        }}
      >
        Back to Login
      </button>
      {error && <div style={{ marginTop: 10, color: 'red' }}>{error}</div>}
      {success && <div style={{ marginTop: 10, color: 'lightgreen' }}>{success}</div>}
    </form>
  );
}

function App() {
  // State for auth
  const [credentials, setCredentials] = useState(null);

  // State to toggle between login and signup
  const [showSignup, setShowSignup] = useState(false);

  // Clothing and outfit states
  const [clothes, setClothes] = useState([]);
  const [formData, setFormData] = useState({ name: '', formality: '', color: '', type: '', subtype: '' });
  const [generateParams, setGenerateParams] = useState({ formality: '', city: '' });
  const [generatedOutfit, setGeneratedOutfit] = useState(null);
  const [editingId, setEditingId] = useState(null);

  // Helper: create headers with Basic Auth
  const getAuthHeaders = () => {
    if (!credentials) return {};
    const encoded = btoa(`${credentials.username}:${credentials.password}`);
    return {
      'Authorization': `Basic ${encoded}`,
      'Content-Type': 'application/json',
    };
  };

  // Fetch clothes when credentials change or on demand
  useEffect(() => {
    if (credentials) {
      fetchClothes();
    } else {
      setClothes([]);
      setGeneratedOutfit(null);
    }
  }, [credentials]);

  // Fetch clothes for logged-in user
  const fetchClothes = () => {
    if (!credentials) return;

    fetch('http://localhost:8080/clothes', {
      method: 'GET',
      headers: getAuthHeaders(),
    })
      .then(res => {
        if (res.status === 401) {
          alert('Session expired, please login again.');
          setCredentials(null);
          throw new Error('Unauthorized');
        }
        return res.json();
      })
      .then(data => setClothes(data))
      .catch(console.error);
  };

  // Add or update clothing item
  const handleSubmit = () => {
    if (!credentials) return;

    const method = editingId ? 'PUT' : 'POST';
    const url = editingId ? `http://localhost:8080/clothes/${editingId}` : 'http://localhost:8080/clothes';

    fetch(url, {
      method,
      headers: getAuthHeaders(),
      body: JSON.stringify(formData),
    })
      .then(res => {
        if (!res.ok) throw new Error('Request failed');
        return res.json();
      })
      .then(() => {
        fetchClothes();
        setFormData({ name: '', formality: '', color: '', type: '', subtype: '' });
        if (editingId) setEditingId(null);
      })
      .catch(console.error);
  };

  // Edit clothing item
  const handleEdit = item => {
    setFormData({
      id: item.id,
      name: item.name,
      formality: item.formality,
      color: item.color,
      type: item.type,
      subtype: item.subtype,
    });
    setEditingId(item.id);
  };

  // Delete clothing item
  const deleteClothingItem = id => {
    if (!credentials) return;

    fetch(`http://localhost:8080/clothes/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    })
      .then(res => {
        if (!res.ok) throw new Error('Delete failed');
        fetchClothes();
      })
      .catch(console.error);
  };

  // Generate outfit
  const generateOutfit = () => {
    if (!credentials) return;

    const { formality, city } = generateParams;
    if (!formality || !city) {
      alert('Please enter both formality and city');
      return;
    }

    fetch(`http://localhost:8080/recommend/${formality}/${city}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to generate outfit');
        return res.json();
      })
      .then(data => setGeneratedOutfit(data))
      .catch(err => {
        console.error(err);
        alert('Failed to generate outfit');
      });
  };

  // Handle login from Login component
  const handleLogin = async (username, password) => {
    try {
      const res = await fetch('http://localhost:8080/clothes', {
        method: 'GET',
        headers: {
          'Authorization': 'Basic ' + btoa(username + ':' + password),
        },
      });

      if (res.ok) {
        setCredentials({username, password});
      } else {
        alert('Invalid username or password');
      }
    } catch (err) {
      console.error(err);
      alert('Login failed. Please try again.');
    }
  };

  // Handle signup - returns a Promise so Signup component can show success/error
  const handleSignup = async (username, password) => {
    const res = await fetch('http://localhost:8080/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    
    return res.ok;
  };

  // Handle logout
  const handleLogout = () => {
    setCredentials(null);
    setEditingId(null);
    setFormData({ name: '', formality: '', color: '', type: '', subtype: '' });
    setGenerateParams({ formality: '', city: '' });
  };

  // Show login or signup form if not logged in
  if (!credentials) {
    return (
      <div 
        style={{ 
          position: 'fixed',
          top: 0,
          left: 0,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          backgroundColor: '#211f3b',
          width: '100vw',
          height: '100vh',
          alignItems: 'center',
        }}
      >
        <h3 style={{ color: '#bab1ff' }}>SMARTER OUTFIT GENERATION</h3>
        <h1 className='login-title'>WardrobiX</h1>
        <p className='login-text'>
          Create an account or login for free, then upload your clothes to generate smart outfits based on real-time data
        </p>
        {showSignup ? (
          <Signup onSignup={handleSignup} switchToLogin={() => setShowSignup(false)} />
        ) : (
          <Login onLogin={handleLogin} switchToSignup={() => setShowSignup(true)} />
        )}
      </div>
    );
  }

  // Logged-in UI (same styling and layout)
  return (
    <div 
      style={{ 
        padding: 20, 
        width: '97vw', 
        backgroundColor: '#211f3b', 
        minHeight: '120vh',
        overflowX: 'hidden',
        overflowY: 'auto'
      }}
    >
      <div
        style={{
          marginBottom: 20,
          color: '#ddd',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <div>
          <h3 style={{ color: '#bab1ff' }}>WELCOME, {credentials.username.toUpperCase()} !</h3>
          <h1 style={{ marginTop: '4px' }}>WardrobiX</h1>
        </div>
        <button
          onClick={handleLogout}
          style={{
            marginTop: -120,
            backgroundColor: '#7c7aff',
            color: 'white',
            fontWeight: 'bold',
            fontSize: 14,
            padding: '8px 14px',
            borderRadius: 6,
            border: 'none',
            cursor: 'pointer',
          }}
          onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#a79eff')}
          onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#7c7aff')}
        >
          Logout
        </button>
      </div>

      {/* Double Columns for Add and Generate Cards */}

      <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: '300px' }}>

            {/* Add Clothing Item Form */}

            <h3 style={{ marginLeft: '16px' }}>Add Clothing Item</h3>
            <div
              style={{
                backgroundColor: '#37344e',
                border: '1px solid #37344e',
                borderRadius: 20,
                padding: '20px 24px',
                marginBottom: 30,
                boxShadow: '0 4px 10px rgba(0,0,0,0.25)',
                display: 'flex',
                flexWrap: 'wrap',
                gap: '16px',
                color: '#ddd',
              }}
            >
              {['name', 'formality', 'color', 'type', 'subtype'].map(field => (
                <input
                  key={field}
                  placeholder={field.charAt(0).toUpperCase() + field.slice(1)}
                  value={formData[field]}
                  onChange={e => setFormData({ ...formData, [field]: e.target.value })}
                  className='form-data-input'
                />
              ))}

              <div style={{ flexBasis: '100%', marginTop: 10 }}>
                <button
                  onClick={handleSubmit}
                  style={{
                    backgroundColor: '#7c7aff',
                    color: 'white',
                    fontWeight: 'bold',
                    fontSize: 16,
                    padding: '12px 17px',
                    borderRadius: 10,
                    border: 'none',
                    cursor: 'pointer',
                    transition: 'background-color 0.3s ease',
                  }}
                  onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#928cff')}
                  onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#7c7aff')}
                >
                  {editingId ? 'Save Changes' : ' + Add Item'}
                </button>

                {editingId && (
                  <button
                    onClick={() => {
                      setEditingId(null);
                      setFormData({ name: '', formality: '', color: '', type: '', subtype: '' });
                    }}
                    style={{
                      marginLeft: 12,
                      backgroundColor: '#333',
                      color: '#bbb',
                      fontSize: 14,
                      padding: '10px 18px',
                      borderRadius: 8,
                      border: 'none',
                      cursor: 'pointer',
                      transition: 'background-color 0.3s ease',
                    }}
                    onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#555')}
                    onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#333')}
                  >
                    Cancel Edit
                  </button>
                )}
              </div>
            </div>
          </div>

          <div style={{ flex: 1, minWidth: '300px' }}>

            {/* Generate Outfit Section */}

            <h3 style={{ marginLeft: '16px' }}>Generate Outfit</h3>
            <div
              style={{
                backgroundColor: '#37344e',
                border: '1px solid #37344e',
                borderRadius: 20,
                padding: '20px 24px',
                marginBottom: 30,
                boxShadow: '0 4px 10px rgba(0,0,0,0.25)',
                display: 'flex',
                flexDirection: 'column',
                gap: '16px',
                color: '#ddd',
              }}
            >
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px' }}>
                <input
                  placeholder='formality'
                  value={generateParams.formality}
                  onChange={e => setGenerateParams({ ...generateParams, formality: e.target.value })}
                  className='form-data-input'
                />
                <input
                  placeholder='city name'
                  value={generateParams.city}
                  onChange={e => setGenerateParams({ ...generateParams, city: e.target.value })}
                  className='form-data-input'
                />
              </div>

              <button
                onClick={generateOutfit}
                style={{
                  backgroundColor: '#7c7aff',
                  color: 'white',
                  fontWeight: 'bold',
                  fontSize: 16,
                  padding: '12px 17px',
                  borderRadius: 10,
                  border: 'none',
                  cursor: 'pointer',
                  transition: 'background-color 0.3s ease',
                  alignSelf: 'stretch',
                }}
                onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#928cff')}
                onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#7c7aff')}
              >
                Generate Outfit
              </button>

              {generatedOutfit && (
                <div>
                  <h3 style={{
                    marginLeft: '4px', 
                    marginTop: '1rem',
                    fontSize: 15 
                    }}
                  >
                    Recommended Outfit:
                  </h3>
                  <ul style={{ listStyle: 'none', padding: 0 }}>
                    {generatedOutfit.map(
                      item =>
                        item && (
                          <li
                            key={item.id}
                            style={{
                              backgroundColor: '#6169ff85',
                              border: 'transparent',
                              borderRadius: 14,
                              padding: '12px 16px',
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center',
                              marginBottom: '10px',
                            }}
                          >
                            <div style={{ flex: 2, minWidth: '120px', color: 'white' }}>
                              <strong>{item.name}</strong>
                            </div>
                            <div style={{ flex: 1, color: '#bbb' }}>{item.type}</div>
                            <div style={{ flex: 1, color: '#bbb' }}>{item.subtype}</div>
                          </li>
                        )
                    )}
                  </ul>
                </div>
              )}
            </div>
          </div>
      </div>
      
      {/* Closet List */}

      <h2 style={{ marginLeft: '16px', marginTop: '2.5rem' }}>Your Closet</h2>
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '9px', // spacing between cards
        }}
      >
        {clothes.map(item => (
          <div
            key={item.id}
            style={{
              backgroundColor: '#37344e',
              border: '1px solid #37344e',
              borderRadius: 7,
              padding: '12px 16px',
              display: 'flex',
              flexWrap: 'wrap',
              justifyContent: 'space-between',
              alignItems: 'center',
              boxShadow: '0 2px 6px rgba(0,0,0,0.15)',
            }}
          >
            <div style={{ flex: 2, minWidth: '120px', color: 'white' }}>
              <strong>{item.name}</strong>
            </div>
            <div style={{ flex: 1, color: '#bbb' }}>{item.color}</div>
            <div style={{ flex: 1, color: '#bbb' }}>{item.type}</div>
            <div style={{ flex: 1, color: '#bbb' }}>{item.subtype}</div>
            <div style={{ flex: 1, color: '#bbb' }}>{item.formality}</div>

            <div style={{ display: 'flex', gap: '6px' }}>
              <button
                onClick={() => handleEdit(item)}
                style={{
                  fontSize: 12,
                  padding: '4px 6px',
                  background: 'transparent',
                  color: 'white',
                  border: 'none',
                  borderRadius: 4,
                  cursor: 'pointer',
                }}
              >
                ✏️
              </button>
              <button
                onClick={() => deleteClothingItem(item.id)}
                style={{
                  fontSize: 12,
                  padding: '4px 6px',
                  background: 'transparent',
                  color: 'white',
                  border: 'none',
                  borderRadius: 4,
                  cursor: 'pointer',
                }}
              >
                🗑️
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
