// api.js
// Utilidades para peticiones a la API

export const API_BASE_URL = 'http://54.197.161.202:7000';

/**
 * Realiza una petición a la API
 * @param {string} endpoint - Endpoint relativo (ej: '/login')
 * @param {object} options - Opciones fetch (method, headers, body, etc)
 * @returns {Promise<any>} Respuesta de la API
 */
export const apiRequest = async (endpoint, options = {}) => {
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    });
    if (response.status === 409) {
      throw new Error('409');
    }
    if (!response.ok) throw new Error('Error en la petición');
    //verificar si la respuesta es un json
    if (response.headers.get('content-type') === 'application/json') {
      return await response.json();
    } else {
      return await response.text();
    }
    //  return await response.json();
  } catch (error) {
    if (error.message === '409') {
      throw new Error('409');
    }
    console.error('API error:', error);
    throw error;
  }
};
