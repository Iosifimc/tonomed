// historial-medico.js
// Lógica modularizada para historial médico, lista para API
import { apiRequest } from './api.js';
import { modalAlerts } from './modal-alerts.js';
import { FormValidator, validators } from './validation.js';

// Variables globales
let currentUser = null;
let userMedicalHistory = null;
let userConsultations = null;
let userMeasurements = null;

// Inicializar variable global para la sección actual del modal
window.currentSection = 'detalles';

// --- Inicialización principal ---
document.addEventListener('DOMContentLoaded', async () => {
  try {
    // Verificar si el usuario está autenticado
    await checkUserAuthentication();
    
    // Inicializar navegación
    initializeNavigation();
    
    // Cargar datos del usuario
    await loadUserData();
    
    // Inicializar módulos y listeners
    initializeModules();
    
    // Cargar datos iniciales
    await loadInitialData();



    
    
  } catch (error) {
    console.error('Error en inicialización:', error);
    modalAlerts.error('Error al cargar la página. Por favor, inicia sesión nuevamente.');
    //setTimeout(() => {
      //window.location.href = 'iniciar-sesion.html';
    //}, 2000);
  }
});

// --- Verificación de autenticación ---
async function checkUserAuthentication() {
  const userData = localStorage.getItem('user');
  if (!userData) {
    throw new Error('Usuario no autenticado');
  }
  
  currentUser = JSON.parse(userData);
  if (!currentUser.idUsuario) {
    throw new Error('Datos de usuario inválidos');
  }
}

// --- Inicialización de navegación ---
function initializeNavigation() {
  document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', function() {
      document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
      this.classList.add('active');
      const tabId = this.dataset.tab;
      document.getElementById(tabId).classList.add('active');
    });
  });
}

// --- Cargar datos del usuario ---
async function loadUserData() {
  try {
    // Actualizar información del usuario en el header
    updateUserInfo();
    
    // Obtener datos del usuario desde la API
    const userData = await apiRequest(`/users/${currentUser.idUsuario}`);
    currentUser = { ...currentUser, ...userData };
    
    // Actualizar localStorage con datos actualizados
    localStorage.setItem('user', JSON.stringify(currentUser));
    
    // Actualizar información en el header
    updateUserInfo();
    
  } catch (error) {
    console.error('Error al cargar datos del usuario:', error);
    modalAlerts.warning('No se pudieron cargar los datos actualizados del usuario');
  }
}








// --- Actualizar información del usuario en el header ---
function updateUserInfo() {
  const userInfoElement = document.querySelector('.user-info');
  if (userInfoElement && currentUser) {
    const age = calculateAge(currentUser.fechaNacimiento);
    const fullName = `${currentUser.nombre} ${currentUser.apellidoPaterno} ${currentUser.apellidoMaterno}`.trim();
    
    userInfoElement.innerHTML = `
      <div class="user-name">${fullName} | ${age} años</div>
      <div>${currentUser.correo}</div>
      <button class="logout-btn" onclick="logout()">Cerrar sesión</button>
    `;
  }
}

// --- Calcular edad ---
function calculateAge(birthDate) {
  if (!birthDate) return 'N/A';
  
  const birth = new Date(birthDate);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const monthDiff = today.getMonth() - birth.getMonth();
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--;
  }
  
  return age;
}

// --- Inicializar módulos ---
function initializeModules() {
  updateModalNavigation();
  initializeMeasurementListeners();
  addRealTimeValidation();
  preventNegativeNumbers();
}

// --- Cargar datos iniciales ---
async function loadInitialData() {
  try {
    // Cargar historial médico
    await loadMedicalHistory();
    
    // Cargar consultas médicas
    await loadConsultations();
    
    // Cargar medidas personales
    await loadPersonalMeasurements();

    await loadPersonalMeasurementModal();
    
    await loadCharts()

  } catch (error) {
    console.error('Error al cargar datos iniciales:', error);
    modalAlerts.error('Error al cargar los datos médicos');
  }
}

// --- Funciones de modal ---
function openModal() {
  const modal = document.getElementById('newRecordModal');
  if (!modal) {
    modalAlerts.error('Error: No se encontró el modal de consulta médica');
    return;
  }
  
  try {
    if (!window.editingRecord) {
      window.editingRecord = null;
      clearNewRecordForm();
    }
    
    modal.style.display = 'block';
    window.currentSection = 'detalles';
    
    // Establecer fecha actual por defecto
    const dateInput = modal.querySelector('input[type="date"]');
    if (dateInput) {
      dateInput.value = new Date().toISOString().split('T')[0];
    }
    
    // Asegurar que la primera sección esté activa
    const sections = modal.querySelectorAll('.form-section');
    sections.forEach(section => section.classList.remove('active'));
    const firstSection = modal.querySelector('#detalles');
    if (firstSection) {
      firstSection.classList.add('active');
    }
    
    // Actualizar navegación del modal
    const navItems = modal.querySelectorAll('.modal-nav-item');
    navItems.forEach(item => item.classList.remove('active'));
    console.log(navItems);
    const firstNavItem = modal.querySelector('.modal-nav-item[data-section="detalles"]');
    if (firstNavItem) {
      firstNavItem.classList.add('active');
    }
    
    updateModalButtons();
    
    // Verificar y corregir el estado de las secciones
    setTimeout(() => {
      verifyAndFixSections();
      addRealTimeModalValidation();
    }, 100);
    
  } catch (error) {
    console.error('Error al abrir modal:', error);
    modalAlerts.error('Error al abrir el formulario de consulta médica');
  }
}

// Hacer la función openModal disponible globalmente
window.openModal = openModal;

function closeModal() {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  window.editingRecord = null;
  modal.style.display = 'none';
  setTimeout(() => {
    clearNewRecordForm();
  }, 100);
}

// Hacer la función closeModal disponible globalmente
window.closeModal = closeModal;



// --- Funciones de navegación del modal ---

// Función para ir a la siguiente sección
window.nextSection = () => {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const sections = ['detalles', 'medidas', 'tratamiento'];
  
  // Asegurar que currentSection esté inicializada
  if (!window.currentSection) {
    window.currentSection = 'detalles';
  }
  
  const currentSectionIndex = sections.indexOf(window.currentSection);
  
  if (currentSectionIndex < sections.length - 1) {
    const nextSectionIndex = currentSectionIndex + 1;
    const nextSectionName = sections[nextSectionIndex];
    
    // Ocultar sección actual
    const currentSectionElement = modal.querySelector('.form-section.active');
    if (currentSectionElement) {
      currentSectionElement.classList.remove('active');
    }
    
    // Mostrar siguiente sección
    const nextSectionElement = modal.querySelector(`#${nextSectionName}`);
    if (nextSectionElement) {
      nextSectionElement.classList.add('active');
      // Forzar estilos CSS
      nextSectionElement.style.display = 'block';
      nextSectionElement.style.opacity = '1';
      nextSectionElement.style.transform = 'translateX(0)';
    }
    
    // Actualizar navegación
    modal.querySelectorAll('.modal-nav-item').forEach(item => {
      item.classList.remove('active');
    });
    
    const nextNavItem = modal.querySelector(`[data-section="${nextSectionName}"]`);
    if (nextNavItem) {
      nextNavItem.classList.add('active');
    }
    
    // Actualizar variable global
    window.currentSection = nextSectionName;
    
    // Actualizar botones
    updateModalButtons();
  }
};

// Función para ir a la sección anterior
window.previousSection = () => {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const sections = ['detalles', 'medidas', 'tratamiento'];
  
  // Asegurar que currentSection esté inicializada
  if (!window.currentSection) {
    window.currentSection = 'detalles';
  }
  
  const currentSectionIndex = sections.indexOf(window.currentSection);
  
  if (currentSectionIndex > 0) {
    const prevSectionIndex = currentSectionIndex - 1;
    const prevSectionName = sections[prevSectionIndex];
    
    // Ocultar sección actual
    const currentSectionElement = modal.querySelector('.form-section.active');
    if (currentSectionElement) {
      currentSectionElement.classList.remove('active');
    }
    
    // Mostrar sección anterior
    const prevSectionElement = modal.querySelector(`#${prevSectionName}`);
    if (prevSectionElement) {
      prevSectionElement.classList.add('active');
      // Forzar estilos CSS
      prevSectionElement.style.display = 'block';
      prevSectionElement.style.opacity = '1';
      prevSectionElement.style.transform = 'translateX(0)';
    }
    
    // Actualizar navegación
    modal.querySelectorAll('.modal-nav-item').forEach(item => {
      item.classList.remove('active');
    });
    
    const prevNavItem = modal.querySelector(`[data-section="${prevSectionName}"]`);
    if (prevNavItem) {
      prevNavItem.classList.add('active');
    }
    
    // Actualizar variable global
    window.currentSection = prevSectionName;
    
    // Actualizar botones
    updateModalButtons();
  }
};

// Función para actualizar el estado de los botones
function updateModalButtons() {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const sections = ['detalles', 'medidas', 'tratamiento'];
  const currentSectionIndex = sections.indexOf(window.currentSection || 'detalles');
  
  const prevButton = modal.querySelector('.modal-footer .btn-secondary');
  const nextButton = modal.querySelector('.modal-footer .btn-primary');
  
  if (prevButton) {
    prevButton.disabled = currentSectionIndex === 0;
    prevButton.style.opacity = currentSectionIndex === 0 ? '0.5' : '1';
  }
  
  if (nextButton) {
    if (currentSectionIndex === sections.length - 1) {
      nextButton.textContent = 'Guardar';
      nextButton.onclick = saveConsultation;
    } else {
      nextButton.textContent = 'Siguiente';
      nextButton.onclick = nextSection;
    }
  }
}

// Función para verificar y corregir el estado de las secciones
function verifyAndFixSections() {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const sections = ['detalles', 'medidas', 'tratamiento'];
  let activeSectionFound = false;
  
  sections.forEach(sectionId => {
    const section = modal.querySelector(`#${sectionId}`);
    if (section) {
      const isActive = section.classList.contains('active');
      
      if (isActive) {
        activeSectionFound = true;
        // Forzar visualización
        section.style.display = 'block';
        section.style.opacity = '1';
        section.style.transform = 'translateX(0)';
      } else {
        // Ocultar completamente
        section.style.display = 'none';
        section.style.opacity = '0';
        section.style.transform = 'translateX(20px)';
      }
    }
  });
  
  if (!activeSectionFound) {
    const detallesSection = modal.querySelector('#detalles');
    if (detallesSection) {
      detallesSection.classList.add('active');
      detallesSection.style.display = 'block';
      detallesSection.style.opacity = '1';
      detallesSection.style.transform = 'translateX(0)';
      window.currentSection = 'detalles';
    }
  }
}

// --- Funciones completas del formulario de registro médico ---

// Función para guardar la consulta completa
window.saveConsultation = async () => {
  try {
    // Verificar que el modal esté abierto
    const modal = document.getElementById('newRecordModal');
    if (!modal || modal.style.display !== 'block') {
      modalAlerts.error('El formulario no está disponible');
      return;
    }

    // Validar formulario
    const validation = validateConsultationForm();
    if (!validation.isValid) {
      showSectionAlert('tratamiento', validation.message, 'error');
      return;
    }

    // Recopilar datos del formulario
    const consultationData = collectConsultationData();
    
    // Mostrar indicador de carga
    const saveButton = document.querySelector('.modal-footer .btn-primary');
    const originalText = saveButton.textContent;
    saveButton.textContent = 'Guardando...';
    saveButton.disabled = true;

    // Enviar datos a la API

    console.log(consultationData);
    const response = await apiRequest('/consultas', {
      method: 'POST',
      body: JSON.stringify(consultationData)
    });

    // Mostrar éxito y cerrar modal
    modalAlerts.success('Consulta médica registrada correctamente');
    closeModal();
    
    // Recargar datos
    await loadConsultations();

  } catch (error) {
    console.error('Error al guardar consulta:', error);
    modalAlerts.error('Error al guardar la consulta: ' + (error.message || 'Intenta de nuevo'));
    
    // Restaurar botón
    const saveButton = document.querySelector('.modal-footer .btn-primary');
    saveButton.textContent = 'Guardar';
    saveButton.disabled = false;
  }
};

// Función para validar el formulario completo
function validateConsultationForm() {
  // Validar sección detalles
  const diagnosticoInput = document.querySelector('#detalles input[placeholder*="Diagnóstico"]');
  const fechaInput = document.querySelector('#detalles input[type="date"]');
  const centroMedicoInput = document.querySelector('#detalles input[placeholder*="Centro médico"]');
  const doctorInput = document.querySelector('#detalles input[placeholder*="Nombre completo del doctor"]');

  // Verificar que todos los elementos existan
  if (!diagnosticoInput || !fechaInput || !centroMedicoInput || !doctorInput) {
    return { isValid: false, message: 'Error: No se encontraron todos los campos del formulario' };
  }

  const diagnostico = diagnosticoInput.value.trim();
  const fechaConsulta = fechaInput.value;
  const centroMedico = centroMedicoInput.value.trim();
  const doctor = doctorInput.value.trim();

  if (!diagnostico) {
    return { isValid: false, message: 'El diagnóstico es obligatorio' };
  }
  if (!fechaConsulta) {
    return { isValid: false, message: 'La fecha de consulta es obligatoria' };
  }
  if (!centroMedico) {
    return { isValid: false, message: 'El centro médico es obligatorio' };
  }
  if (!doctor) {
    return { isValid: false, message: 'El nombre del doctor es obligatorio' };
  }

  // Validar fecha (no puede ser futura)
  const consultaDate = new Date(fechaConsulta);
  const today = new Date();
  if (consultaDate > today) {
    return { isValid: false, message: 'La fecha de consulta no puede ser futura' };
  }

  return { isValid: true };
}

// Función para recopilar todos los datos del formulario
function collectConsultationData() {
  // Datos básicos de la consulta
  const diagnosticoInput = document.querySelector('#detalles input[placeholder*="Diagnóstico"]');
  const fechaInput = document.querySelector('#detalles input[type="date"]');
  const centroMedicoInput = document.querySelector('#detalles input[placeholder*="Centro médico"]');
  const doctorInput = document.querySelector('#detalles input[placeholder*="Nombre completo del doctor"]');

  // Verificar que todos los elementos existan
  if (!diagnosticoInput || !fechaInput || !centroMedicoInput || !doctorInput) {
    throw new Error('No se encontraron todos los campos del formulario');
  }

  const diagnostico = diagnosticoInput.value.trim();
  const fechaConsulta = fechaInput.value;
  const centroMedico = centroMedicoInput.value.trim();
  const doctor = doctorInput.value.trim();

  // Recopilar medidas
  const medidas = [];
  document.querySelectorAll('#measurementsList .measurement-row').forEach(row => {
    const tipoSelect = row.querySelector('select');
    const valorInput = row.querySelector('input[type="number"]');
    
    if (tipoSelect.value && valorInput.value) {
      const tipoMedida = getMedidaIdFromName(tipoSelect.value);
      medidas.push({
        idMedida: tipoMedida,
        fechaRegistro: fechaConsulta,
        valorMedida: parseFloat(valorInput.value),
        notaAdicional: `Registrado durante consulta médica`
      });
    }
  });

  // Recopilar medicamentos
  const prescripciones = [];
  document.querySelectorAll('#medicationsList .medication-row').forEach(row => {
    const nombreInput = row.querySelector('input[placeholder*="Paracetamol"]');
    const presentacionInput = row.querySelector('input[placeholder*="Tabletas 500 mg"]');
    const dosisInput = row.querySelector('input[placeholder*="1 tableta"]');
    const frecuenciaInput = row.querySelector('input[placeholder*="En horas"]');
    const duracionInput = row.querySelector('input[placeholder*="En días"]');
    
    if (nombreInput.value.trim() && dosisInput.value.trim()) {
      prescripciones.push({
        nombreMedicamento: nombreInput.value.trim(),
        presentacionMedicamento: presentacionInput.value.trim() || 'No especificado',
        dosis: dosisInput.value.trim(),
        frecuencia: frecuenciaInput.value ? `Cada ${frecuenciaInput.value} horas` : 'No especificado',
        duracion: duracionInput.value ? `${duracionInput.value} días` : 'No especificado'
      });
    }
  });

  // Recopilar procedimientos
  const procedimientos = [];
  document.querySelectorAll('#proceduresList .procedure-row').forEach(row => {
    const procedimientoSelect = row.querySelector('select');
    const notasInput = row.querySelector('input[placeholder*="Detalles importantes"]');
    
    if (procedimientoSelect.value) {
      const procedimientoId = getProcedimientoIdFromName(procedimientoSelect.value);
      procedimientos.push({
        idProcedimiento: procedimientoId,
        notaAdicional: notasInput.value.trim() || 'Procedimiento realizado durante consulta'
      });
    }
  });

  // Estructura final para la API
  return {
    consultaMedica: {
      idUsuario: currentUser.idUsuario,
      diagnostico: diagnostico,
      doctor: doctor,
      clinica: centroMedico,
      fechaConsulta: fechaConsulta
    },
    prescripciones: prescripciones,
    medidasConsulta: medidas,
    procedimientos: procedimientos
  };
}

// Función para obtener ID de medida desde el nombre
function getMedidaIdFromName(nombre) {
  const medidas = {
    'peso': 1,
    'altura': 2,
    'presion': 3,
    'temperatura': 4,
    'glucosa': 5
  };
  return medidas[nombre] || 1;
}

// Función para obtener ID de procedimiento desde el nombre
function getProcedimientoIdFromName(nombre) {
  const procedimientos = {
    'aplicacion-yesos': 1,
    'vendaje': 2,
    'curacion-heridas': 3,
    'sutura': 4,
    'retiro-puntos': 5,
    'drenaje-abcesos': 6,
    'extraccion-cuerpos': 7,
    'aplicacion-vacunas': 8,
    'inyeccion-intramuscular': 9,
    'nebulizacion': 10,
    'prueba-antigenos': 11
  };
  return procedimientos[nombre] || 1;
}

// Función para mostrar alerta en una sección específica
function showSectionAlert(section, message, type = 'error') {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const alertElement = modal.querySelector(`#alert-${section}`);
  if (alertElement) {
    const messageElement = alertElement.querySelector('.alert-message');
    const iconElement = alertElement.querySelector('.alert-icon');
    
    messageElement.textContent = message;
    iconElement.textContent = type === 'error' ? '⚠️' : '✅';
    alertElement.className = `section-alert show ${type}`;
    
    // Auto-ocultar después de 5 segundos
    setTimeout(() => {
      alertElement.classList.remove('show');
    }, 5000);
  }
}

// Función para mostrar alerta en el modal de mediciones
function showMeasurementAlert(message, type = 'error') {
  const modal = document.getElementById('newMeasurementModal');
  if (!modal) return;
  
  const alertElement = modal.querySelector('#alert-measurement');
  if (alertElement) {
    const messageElement = alertElement.querySelector('.alert-message');
    const iconElement = alertElement.querySelector('.alert-icon');
    
    messageElement.textContent = message;
    iconElement.textContent = type === 'error' ? '⚠️' : '✅';
    alertElement.className = `section-alert show ${type}`;
    
    // Auto-ocultar después de 5 segundos
    setTimeout(() => {
      alertElement.classList.remove('show');
    }, 5000);
  }
}

// Función para agregar medicamento
window.addMedication = () => {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const medicationsList = modal.querySelector('#medicationsList');
  if (!medicationsList) return;
  
  const newMedication = document.createElement('div');
  newMedication.className = 'medication-row';
  newMedication.innerHTML = `
    <div class="medication-header">
      <div class="form-group">
        <label class="form-label">Nombre del medicamento</label>
        <input type="text" class="form-input" placeholder="Ej: Paracetamol">
      </div>
      <div class="form-group">
        <label class="form-label">Presentación del medicamento</label>
        <input type="text" class="form-input" placeholder="Ej: Tabletas 500 mg">
      </div>
      <button type="button" class="delete-btn" onclick="removeMedication(this)">🗑</button>
    </div>
    <div class="medication-details">
      <div class="form-group">
        <label class="form-label">Dosis</label>
        <input type="text" class="form-input" placeholder="Ej: 1 tableta, 10 ml">
      </div>
      <div class="form-group">
        <label class="form-label">Tiempo entre dosis</label>
        <input type="number" class="form-input no-negative" placeholder="En horas" min="1" max="72" step="1">
      </div>
      <div class="form-group">
        <label class="form-label">Duración</label>
        <input type="number" class="form-input no-negative" placeholder="En días" min="1" max="365" step="1">
      </div>
    </div>
  `;
  medicationsList.appendChild(newMedication);
};

// Función para remover medicamento
window.removeMedication = (element) => {
  if (element && element.closest('.medication-row')) {
    element.closest('.medication-row').remove();
  }
};

// Función para agregar medida
window.addMeasurement = () => {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const measurementsList = modal.querySelector('#measurementsList');
  if (!measurementsList) return;
  
  const newMeasurement = document.createElement('div');
  newMeasurement.className = 'measurement-row';
  newMeasurement.innerHTML = `
    <div class="form-group">
      <label class="form-label">Medida corporal a registrar</label>
      <select class="form-input" placeholder="Ej: Peso, Altura, Presión arterial">
        <option value="">Seleccione una medida</option>
        <option value="peso">Peso (kg)</option>
        <option value="altura">Altura (cm)</option>
        <option value="presion">Presión arterial (mm Hg)</option>
        <option value="temperatura">Temperatura (°C)</option>
        <option value="glucosa">Glucosa (mg/dL)</option>
      </select>
    </div>
    <div class="form-group">
      <label class="form-label">Valor registrado</label>
      <input type="number" class="form-input no-negative" placeholder="Ej: 70 (kg), 175 (cm)" min="0" step="0.1">
    </div>
    <button type="button" class="delete-btn" onclick="removeMeasurement(this)">🗑</button>
  `;
  measurementsList.appendChild(newMeasurement);
};

// Función para remover medida
window.removeMeasurement = (element) => {
  if (element && element.closest('.measurement-row')) {
    element.closest('.measurement-row').remove();
  }
};

// Función para agregar procedimiento
window.addProcedure = () => {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const proceduresList = modal.querySelector('#proceduresList');
  if (!proceduresList) return;
  
  const newProcedure = document.createElement('div');
  newProcedure.className = 'procedure-row';
  newProcedure.innerHTML = `
    <div class="form-group">
      <label class="form-label">Procedimiento médico</label>
      <select class="form-input">
        <option value="">Seleccione un procedimiento</option>
        <option value="aplicacion-yesos">Aplicación/retiración de yesos</option>
        <option value="vendaje">Vendaje de una herida/lesión</option>
        <option value="curacion-heridas">Curación de heridas</option>
        <option value="sutura">Sutura</option>
        <option value="retiro-puntos">Retiro de puntos</option>
        <option value="drenaje-abcesos">Drenaje de abcesos</option>
        <option value="extraccion-cuerpos">Extracción de cuerpos extraños</option>
        <option value="aplicacion-vacunas">Aplicación de vacunas</option>
        <option value="inyeccion-intramuscular">Inyección intramuscular</option>
        <option value="nebulizacion">Nebulización</option>
        <option value="prueba-antigenos">Prueba de antígenos</option>
      </select>
    </div>
    <div class="form-group">
      <label class="form-label">Notas</label>
      <input type="text" class="form-input" placeholder="Detalles importantes (opcional)">
    </div>
    <button type="button" class="delete-btn" onclick="removeProcedure(this)">🗑</button>
  `;
  proceduresList.appendChild(newProcedure);
};

// Función para remover procedimiento
window.removeProcedure = (element) => {
  if (element && element.closest('.procedure-row')) {
    element.closest('.procedure-row').remove();
  }
};

// Función para ocultar alerta
window.hideAlert = (type) => {
  // Buscar en el modal principal
  const mainModal = document.getElementById('newRecordModal');
  if (mainModal) {
    const alertElement = mainModal.querySelector(`#alert-${type}`);
    if (alertElement) {
      alertElement.classList.remove('show');
    }
  }
  
  // Buscar en el modal de mediciones
  const measurementModal = document.getElementById('newMeasurementModal');
  if (measurementModal) {
    const alertElement = measurementModal.querySelector(`#alert-${type}`);
    if (alertElement) {
      alertElement.classList.remove('show');
    }
  }
};

// --- Funciones del modal de medición ---

// Función para abrir modal de medición
window.openMeasurementModal = () => {
  const modal = document.getElementById('newMeasurementModal');
  if (modal) {
    modal.style.display = 'block';
    // Establecer fecha actual por defecto
    const dateInput = modal.querySelector('input[type="date"]');
    if (dateInput) {
      dateInput.value = new Date().toISOString().split('T')[0];
    }
    
    // Agregar event listeners para validación en tiempo real
    const tipoSelect = modal.querySelector('.measurement-form-select');
    const valorInput = modal.querySelector('.measurement-form-input');
    const fechaInput = modal.querySelector('input[type="date"]');
    
    if (tipoSelect) {
      tipoSelect.addEventListener('change', addMeasurementModalValidation);
    }
    if (valorInput) {
      valorInput.addEventListener('input', addMeasurementModalValidation);
    }
    if (fechaInput) {
      fechaInput.addEventListener('change', addMeasurementModalValidation);
    }
  }
};

// Función para cerrar modal de medición
window.closeMeasurementModal = () => {
  const modal = document.getElementById('newMeasurementModal');
  if (modal) {
    modal.style.display = 'none';
    
    // Limpiar formulario
    const tipoSelect = modal.querySelector('.measurement-form-select');
    const valorInput = modal.querySelector('.measurement-form-input');
    const fechaInput = modal.querySelector('input[type="date"]');
    
    if (tipoSelect) {
      tipoSelect.selectedIndex = 0;
      tipoSelect.classList.remove('valid', 'invalid');
    }
    if (valorInput) {
      valorInput.value = '';
      valorInput.classList.remove('valid', 'invalid');
    }
    if (fechaInput) {
      fechaInput.value = '';
      fechaInput.classList.remove('valid', 'invalid');
    }
    
    // Ocultar alertas
    const alertElement = modal.querySelector('#alert-measurement');
    if (alertElement) {
      alertElement.classList.remove('show');
    }
    
    // Restaurar botón de guardar
    const saveButton = modal.querySelector('.btn-save');
    if (saveButton) {
      saveButton.textContent = 'Guardar medición';
      saveButton.disabled = false;
    }
  }
};

// Función para guardar medición individual
window.saveMeasurement = async () => {
  try {
    const modal = document.getElementById('newMeasurementModal');
    if (!modal) {
      modalAlerts.error('Modal de medición no disponible');
      return;
    }
    
    // Usar el nuevo sistema de validación
    if (!measurementValidator || !measurementValidator.isFormValid()) {
      showMeasurementAlert('Por favor completa todos los campos correctamente', 'error');
      return;
    }
    
    const tipoSelect = modal.querySelector('.measurement-form-select');
    const valorInput = modal.querySelector('.measurement-form-input');
    const fechaInput = modal.querySelector('input[type="date"]');
  
    console.log(valorInput.value);
    // Preparar datos
    const medidaData = {
      idMedida: getMedidaIdFromName(tipoSelect.value),
      idUsuario: currentUser.idUsuario,
      fechaRegistro: fechaInput.value,
      valorMedida: valorInput.value,
      notaAdicional: `Medida registrada manualmente`
    };

    // Mostrar indicador de carga
    const saveButton = modal.querySelector('.btn-save');
    const originalText = saveButton.textContent;
    saveButton.textContent = 'Guardando...';
    saveButton.disabled = true;

    // Enviar a la API
    await apiRequest('/medidas-personales', {
      method: 'POST',
      body: JSON.stringify(medidaData)
    });

    // Mostrar éxito y cerrar
    showMeasurementAlert('Medida registrada correctamente', 'success');
    setTimeout(() => {
      closeMeasurementModal();
    }, 1500);
    
    // Recargar datos
    await loadPersonalMeasurements();

  } catch (error) {
    console.error('Error al guardar medida:', error);
    showMeasurementAlert('Error al guardar la medida: ' + (error.message || 'Intenta de nuevo'), 'error');
    
    // Restaurar botón
    const saveButton = modal.querySelector('.btn-save');
    if (saveButton) {
      saveButton.textContent = 'Guardar medición';
      saveButton.disabled = false;
    }
  }
};

// --- Funciones de utilidad y eventos globales ---
window.onclick = function(event) {
  const modal = document.getElementById('newRecordModal');
  if (modal && event.target === modal) {
    closeModal();
  }
  
  const measurementModal = document.getElementById('newMeasurementModal');
  if (measurementModal && event.target === measurementModal) {
    closeMeasurementModal();
  }
};

// --- Funciones para cargar datos médicos ---

// Cargar historial médico (antecedentes)
async function loadMedicalHistory() {
  try {
    userMedicalHistory = await apiRequest(`/antecedentes-medicos/usuario/${currentUser.idUsuario}`);
    displayMedicalHistory(userMedicalHistory);
  } catch (error) {
    console.error('Error al cargar historial médico:', error);
    // Si no hay datos, mostrar mensaje
    displayMedicalHistory(null);
  }
}

// Cargar consultas médicas
async function loadConsultations() {
  try {
    userConsultations = await apiRequest(`/consultas/usuario/${currentUser.idUsuario}`);
    displayConsultations(userConsultations);
  } catch (error) {
    console.error('Error al cargar consultas:', error);
    // Si no hay datos, mostrar mensaje
    displayConsultations([]);
  }
}

// Cargar medidas personales
async function loadPersonalMeasurements() {
  try {
    userMeasurements = await apiRequest(`/users/${currentUser.idUsuario}`);
    console.log(userMeasurements);
    const userData = document.querySelector('#informacion');

    const fechaNacimiento = new Date(userMeasurements.fechaNacimiento);
    const fechaNacimientoFormateada = fechaNacimiento.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' });




    userData.innerHTML = `
     <div class="profile-container">
                <div class="profile-card">
                    <div class="profile-form">
                        <div class="profile-row">
                            <div class="profile-group">
                                <label class="profile-label">Nombre</label>
                                <input type="text" id="nombre" class="profile-input readonly" value="${userMeasurements.nombre}" readonly>
                            </div>
                            <div class="profile-group">
                                <label class="profile-label" id="apellidos" >Apellidos</label>
                                <input type="text" class="profile-input readonly" value="${userMeasurements.apellidoPaterno} ${userMeasurements.apellidoMaterno}" readonly>
                            </div>
                        </div>
                        
                        <div class="profile-row">
                            <div class="profile-group">
                                <label class="profile-label">Fecha de nacimiento</label>
                                <input type="text" id="fechaNacimiento" class="profile-input readonly" value="${fechaNacimientoFormateada}" readonly>
                            </div>
                            <div class="profile-group">
                                <label class="profile-label">Sexo</label>
                                <input type="text" id="sexo" class="profile-input readonly" value="${userMeasurements.sexo}" readonly>
                            </div>
                        </div>
                        
                        <div class="profile-row">
                            <div class="profile-group">
                                <label class="profile-label">Correo electrónico</label>
                                <input type="email" class="profile-input readonly" id="email-input" value="${userMeasurements.correo}" readonly>
                            </div>
                            <div class="profile-group">
                                <label class="profile-label">Contraseña</label>
                                <div class="password-field">
                                    <input type="password" class="profile-input readonly" id="password-input" value="************" readonly>
                                    <button type="button" class="toggle-password" id="toggle-password"  title="Mostrar contraseña">👁️</button>
                                </div>
                            </div>
                        </div>
                        
                        <div class="profile-actions">
                            <button type="button" class="btn-edit" id="edit-btn" >Editar perfil</button>
                            <button type="button" class="btn-save-changes" id="save-btn" disabled >Guardar cambios</button>
                        </div>
                    </div>
                </div>
            </div>
    `;


    const saveButton = userData.querySelector("#save-btn")

    saveButton.addEventListener('click',async  (e) => {
      if(!saveButton.disabled) {
        

        const nombre= userData.querySelector('#nombre').value
        const apellidos = userData.querySelector('#apellidos').value
        const fecha = userData.querySelector('#fechaNacimiento').value
        const partes = fecha.split("/"); // ["09", "10", "2002"]
        const fechaNacimiento = `${partes[2]}-${partes[1].padStart(2, "0")}-${partes[0].padStart(2, "0")}`;
        
       

        const sexo = userData.querySelectorAll('#sexo').value
        const correo = userData.querySelector('#email-input').value
        //const contraseña = userData.querySelector('#password-input').value;  CUANDO LA USEN METANLO EN LA API GG
      


        const data = {
          nombre,
          apellidos,
          fechaNacimiento,
          sexo,
          correo
        }

        const res = await apiRequest(`/users/${currentUser.idUsuario}`,  {
          method: 'PUT',
          body: JSON.stringify(data)
        }
        )

      }
    })


    const editButton = userData.querySelector("#edit-btn")
    editButton.addEventListener('click', e => {
      const inputs = userData.querySelectorAll("input")


      inputs.forEach(el => {
        if (el.classList.contains("readonly")) {
          el.classList.remove("readonly")
          saveButton.disabled = false
          

        }else{
          el.classList.add('readonly')
          saveButton.disabled = true

        }


        el.readOnly = !el.readOnly
      });
      

    })

    
    const togglePasswordVisibility = userData.querySelector("#toggle-password")
    togglePasswordVisibility.addEventListener('click', e => {
      
      const passwordInput = userData.querySelector("#password-input")
      
      passwordInput.type =  passwordInput.type == "password" ? "text" : "password"

    })





    


          } catch (error) {
    console.error('Error al cargar medidas:', error);
    // Si no hay datos, mostrar mensaje
    
  }
}












async function loadPersonalMeasurementModal() {
  try {
    userMeasurements = await apiRequest(`/medidas/usuario/${currentUser.idUsuario}`);
    displayMeasurements(userMeasurements);
  } catch (error) {
    console.error('Error al cargar medidas:', error);
  }
}
const loadCharts = async () => {
  const data = await apiRequest(`/medidas/usuario/${currentUser.idUsuario}`, {
    method: 'GET'
  });

  

  const graficasContainer = document.querySelector("#graficas");
  const graficar = graficasContainer.querySelector("#graficar-btn");
  const nextMonthButton = graficasContainer.querySelector("#next-chart");
  const previousMonthButton = graficasContainer.querySelector("#prev-chart");
  const textMonth = graficasContainer.querySelector("#currentMonth");
  const textYear = graficasContainer.querySelector("#currentYear");
  const tabs = graficasContainer.querySelectorAll(".measurement-tab");

  // Elementos para estadísticas
  const totalMeasurementsEl = graficasContainer.querySelector("#totalMeasurements");
  const firstMeasurementEl = graficasContainer.querySelector("#firstMeasurement");
  const lastMeasurementEl = graficasContainer.querySelector("#lastMeasurement");

  const nombresMes = [
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
  ];

  let fecha = new Date();
  let mes = fecha.getMonth();
  let año = fecha.getFullYear();
  let medida = graficasContainer.querySelector(".measurement-tab.active")?.dataset.type || "peso";

  const actualizarTexto = () => {
    textMonth.textContent = nombresMes[mes];
    textYear.textContent = año;
  };

  actualizarTexto();

  const filtrarDatos = () => {
    return data
      .filter(item => item.tipoMedida.toLowerCase() === medida.toLowerCase())
      .filter(item => {
        const fecha = new Date(item.fechaRegistro);
        return fecha.getMonth() === mes && fecha.getFullYear() === año;
      })
      .map(item => ({
        fecha: new Date(item.fechaRegistro),
        fechaStr: new Date(item.fechaRegistro).toLocaleDateString('es-MX'),
        valor: item.valorMedida
      }));
  };

  let chartInstance;
  const renderChart = () => {
    const datosFiltrados = filtrarDatos();

    // Actualizar estadísticas
    totalMeasurementsEl.textContent = datosFiltrados.length;

    if (datosFiltrados.length > 0) {
      // Ordenar fechas para primer y último registro
      const fechasOrdenadas = datosFiltrados
        .map(d => d.fecha)
        .sort((a, b) => a - b);
      firstMeasurementEl.textContent = fechasOrdenadas[0].toLocaleDateString('es-MX');
      lastMeasurementEl.textContent = fechasOrdenadas[fechasOrdenadas.length - 1].toLocaleDateString('es-MX');
    } else {
      firstMeasurementEl.textContent = "-";
      lastMeasurementEl.textContent = "-";
    }

    const ctx = document.getElementById('measurementChart').getContext('2d');

    if (chartInstance) {
      chartInstance.destroy();
    }

    chartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: datosFiltrados.map(d => d.fechaStr),
        datasets: [{
          label: `Medidas de ${medida}`,
          data: datosFiltrados.map(d => d.valor),
          borderColor: 'rgba(75, 192, 192, 1)',
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          tension: 0.3,
          fill: true
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  };

  renderChart();

  nextMonthButton.addEventListener('click', () => {
    mes++;
    if (mes > 11) {
      mes = 0;
      año++;
    }
    actualizarTexto();
    renderChart();
  });

  previousMonthButton.addEventListener('click', () => {
    mes--;
    if (mes < 0) {
      mes = 11;
      año--;
    }
    actualizarTexto();
    renderChart();
  });

  tabs.forEach(btn => {
    btn.addEventListener("click", () => {
      tabs.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      medida = btn.dataset.type;
      renderChart();
    });
  });
};




// --- Funciones para mostrar datos en la UI ---

// Mostrar historial médico
function displayMedicalHistory(data) {
  const historialTab = document.getElementById('historial');
  if (!historialTab) return;
  
  if (!data || (Array.isArray(data.alergias) && data.alergias.length === 0 && 
      Array.isArray(data.medicamentosCronicos) && data.medicamentosCronicos.length === 0 &&
      Array.isArray(data.cirugias) && data.cirugias.length === 0 &&
      Array.isArray(data.enfermedadesCronicas) && data.enfermedadesCronicas.length === 0)) {
    
    historialTab.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">📋</div>
        <h3>No hay historial médico registrado</h3>
        <p>Comienza agregando tus antecedentes médicos para tener un registro completo.</p>
      </div>
    `;
    return;
  }
  
  // Mostrar datos del historial médico
  let historialHTML = '<div class="medical-history-content">';
  
  // Alergias
  if (data.alergias && data.alergias.length > 0) {
    historialHTML += `
      <div class="history-section">
        <h3>🔄 Alergias</h3>
        <div class="history-items">
          ${data.alergias.map(alergia => `
            <div class="history-item">
              <span class="item-name">${alergia.nombreAlergia}</span>
              <button class="btn-edit" onclick="editAlergia(${alergia.idAlergia})">✏️</button>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  }
  
  // Medicamentos crónicos
  if (data.medicamentosCronicos && data.medicamentosCronicos.length > 0) {
    historialHTML += `
      <div class="history-section">
        <h3>💊 Medicamentos Crónicos</h3>
        <div class="history-items">
          ${data.medicamentosCronicos.map(med => `
            <div class="history-item">
              <span class="item-name">${med.nombreMedicamento}</span>
              <button class="btn-edit" onclick="editMedicamento(${med.idMedicamentoCronico})">✏️</button>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  }
  
  // Cirugías
  if (data.cirugias && data.cirugias.length > 0) {
    historialHTML += `
      <div class="history-section">
        <h3>🏥 Cirugías</h3>
        <div class="history-items">
          ${data.cirugias.map(cirugia => `
            <div class="history-item">
              <span class="item-name">${cirugia.nombreCirugia || 'Cirugía'}</span>
              <span class="item-date">${formatDate(cirugia.fechaCirugia)}</span>
              <button class="btn-edit" onclick="editCirugia(${cirugia.idCirugia})">✏️</button>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  }
  
  // Enfermedades crónicas
  if (data.enfermedadesCronicas && data.enfermedadesCronicas.length > 0) {
    historialHTML += `
      <div class="history-section">
        <h3>🏥 Enfermedades Crónicas</h3>
        <div class="history-items">
          ${data.enfermedadesCronicas.map(enfermedad => `
            <div class="history-item">
              <span class="item-name">${enfermedad.nombreEnfermedadCronica || 'Enfermedad'}</span>
              <span class="item-date">Diagnosticada: ${formatDate(enfermedad.fechaDiagnostico)}</span>
              <button class="btn-edit" onclick="editEnfermedad(${enfermedad.idEnfermedadCronica})">✏️</button>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  }
  
  historialHTML += '</div>';
  historialTab.innerHTML = historialHTML;
}

// Mostrar consultas médicas
function displayConsultations(consultas) {
  const consultasTab = document.querySelector('#consultas .records-list');
  if (!consultasTab) return;
  
  if (!consultas || consultas.length === 0) {
    consultasTab.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">📋</div>
        <h3>No hay consultas médicas registradas</h3>
        <p>Comienza agregando tu primera consulta médica usando el botón "Crear un nuevo registro".</p>
      </div>
    `;
    return;
  }
  
  // Ordenar consultas por fecha (más recientes primero)
  const consultasOrdenadas = consultas.sort((a, b) => 
    new Date(b.fechaConsulta) - new Date(a.fechaConsulta)
  );
  
  let consultasHTML = '';
  consultasOrdenadas.forEach(consulta => {
    consultasHTML += `
      <div class="record-card" data-id="${consulta.idConsulta}">
        <div class="record-header">
          <div class="record-date">${formatDate(consulta.fechaConsulta)}</div>
          <div class="record-actions">
            <button class="btn-edit" onclick="editConsulta(${consulta.idConsulta})">✏️</button>
            <button class="btn-delete" onclick="deleteConsulta(${consulta.idConsulta})">🗑️</button>
          </div>
        </div>
        <div class="record-content">
          <div class="record-field">
            <strong>Diagnóstico:</strong> ${consulta.diagnostico || 'No especificado'}
          </div>
          <div class="record-field">
            <strong>Doctor:</strong> ${consulta.doctor || 'No especificado'}
          </div>
          <div class="record-field">
            <strong>Clínica:</strong> ${consulta.clinica || 'No especificado'}
          </div>
          ${consulta.prescripciones && consulta.prescripciones.length > 0 ? `
            <div class="record-field">
              <strong>Medicamentos:</strong> ${consulta.prescripciones.length} prescripción(es)
            </div>
          ` : ''}
        </div>
      </div>
    `;
  });
  
  consultasTab.innerHTML = consultasHTML;
}

// Mostrar medidas personales
function displayMeasurements(medidas) {
  const medidasTab = document.querySelector('#medidas .measurements-grid');
  if (!medidasTab) return;
  
  if (!medidas || medidas.length === 0) {
    medidasTab.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">📊</div>
        <h3>No hay medidas registradas</h3>
        <p>Comienza registrando tus medidas corporales para hacer seguimiento usando el botón "Registrar nueva medición".</p>
      </div>
    `;
    return;
  }
  
  // Ordenar medidas por fecha (más recientes primero)
  const medidasOrdenadas = medidas.sort((a, b) => 
    new Date(b.fechaRegistro) - new Date(a.fechaRegistro)
  );
  
  let medidasHTML = '';
  medidasOrdenadas.forEach(medida => {
    const medidaType = getMedidaTypeName(medida.idMedida);
    const medidaIcon = getMedidaIcon(medida.idMedida);
    
    medidasHTML += `
      <div class="measurement-card" data-id="${medida.idRegistroMedida}">
        <div class="measurement-header">
          <div class="measurement-icon">${medidaIcon}</div>
          <div class="measurement-type">${medidaType}</div>
          <div class="measurement-actions">
            <button class="btn-edit" onclick="editMedida(${medida.idRegistroMedida})">✏️</button>
            <button class="btn-delete" onclick="deleteMedida(${medida.idRegistroMedida})">🗑️</button>
          </div>
        </div>
        <div class="measurement-content">
          <div class="measurement-value">${medida.valorMedida} ${getMedidaUnit(medida.idMedida)}</div>
          <div class="measurement-date">${formatDate(medida.fechaRegistro)}</div>
          ${medida.notaAdicional ? `
            <div class="measurement-note">${medida.notaAdicional}</div>
          ` : ''}
        </div>
      </div>
    `;
  });
  
  medidasTab.innerHTML = medidasHTML;
}

// --- Funciones de utilidad ---

// Formatear fecha
function formatDate(dateString) {
  if (!dateString) return 'N/A';
  
  const date = new Date(dateString);
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

// Obtener nombre del tipo de medida
function getMedidaTypeName(idMedida) {
  const tipos = {
    1: 'Peso',
    2: 'Altura',
    3: 'Presión Arterial',
    4: 'Temperatura',
    5: 'Glucosa',
    6: 'Frecuencia Cardíaca'
  };
  return tipos[idMedida] || 'Medida';
}

// Obtener icono del tipo de medida
function getMedidaIcon(idMedida) {
  const iconos = {
    1: '⚖️',
    2: '📏',
    3: '❤️',
    4: '🌡️',
    5: '🩸',
    6: '💓'
  };
  return iconos[idMedida] || '📊';
}

// Obtener unidad del tipo de medida
function getMedidaUnit(idMedida) {
  const unidades = {
    1: 'kg',
    2: 'cm',
    3: 'mmHg',
    4: '°C',
    5: 'mg/dL',
    6: 'bpm'
  };
  return unidades[idMedida] || '';
}

// --- Funciones para acciones CRUD ---

// Funciones para editar (placeholder)
window.editConsulta = (id) => {
  modalAlerts.info('Función de edición de consulta en desarrollo');
};

window.deleteConsulta = (id) => {
  modalAlerts.confirm(
    '¿Estás seguro de que quieres eliminar esta consulta?',
    async () => {
      try {
        await apiRequest(`/consultas/${id}`, { method: 'DELETE' });
        modalAlerts.success('Consulta eliminada correctamente');
        await loadConsultations(); // Recargar datos
      } catch (error) {
        modalAlerts.error('Error al eliminar la consulta');
      }
    }
  );
};

window.editMedida = (id) => {
  modalAlerts.info('Función de edición de medida en desarrollo');
};

window.deleteMedida = (id) => {
  modalAlerts.confirm(
    '¿Estás seguro de que quieres eliminar esta medida?',
    async () => {
      try {
        await apiRequest(`/registro-medidas/${id}`, { method: 'DELETE' });
        modalAlerts.success('Medida eliminada correctamente');
        await loadPersonalMeasurements(); // Recargar datos
      } catch (error) {
        modalAlerts.error('Error al eliminar la medida');
      }
    }
  );
};

// Funciones para historial médico (placeholder)
window.editAlergia = (id) => {
  modalAlerts.info('Función de edición de alergia en desarrollo');
};

window.editMedicamento = (id) => {
  modalAlerts.info('Función de edición de medicamento en desarrollo');
};

window.editCirugia = (id) => {
  modalAlerts.info('Función de edición de cirugía en desarrollo');
};

window.editEnfermedad = (id) => {
  modalAlerts.info('Función de edición de enfermedad en desarrollo');
};

// Funciones para abrir modales (placeholder)
window.openMedicalHistoryModal = () => {
  modalAlerts.info('Modal de historial médico en desarrollo');
};



// --- Función de logout ---
window.logout = () => {
  modalAlerts.confirm(
    '¿Estás seguro de que quieres cerrar sesión?',
    () => {
      // Limpiar localStorage
      localStorage.removeItem('user');
      
      // Mostrar mensaje de éxito
      modalAlerts.success('Sesión cerrada correctamente');
      
      // Redirigir al login
      setTimeout(() => {
        window.location.href = 'iniciar-sesion.html';
      }, 1500);
    }
  );
};

// --- Funciones de utilidad que faltaban ---

// Función para actualizar navegación del modal
function updateModalNavigation() {
  // Inicializar la navegación del modal
  window.currentSection = 'detalles';
  
  // Configurar listeners para los elementos de navegación
  document.querySelectorAll('.modal-nav-item').forEach(item => {
    item.addEventListener('click', function() {
      const sectionName = this.dataset.section;
      if (sectionName) {
        // Ocultar todas las secciones
        document.querySelectorAll('.form-section').forEach(section => {
          section.classList.remove('active');
        });
        
        // Mostrar la sección seleccionada
        const targetSection = document.getElementById(sectionName);
        if (targetSection) {
          targetSection.classList.add('active');
        }
        
        // Actualizar navegación
        document.querySelectorAll('.modal-nav-item').forEach(navItem => {
          navItem.classList.remove('active');
        });
        this.classList.add('active');
        
        // Actualizar variable global
        window.currentSection = sectionName;
        
        // Actualizar botones
        updateModalButtons();
      }
    });
  });
  
  // Inicializar botones
  updateModalButtons();
}

// Función para inicializar listeners de medidas
function initializeMeasurementListeners() {
  // Esta función se usará para los modales de medidas
  // Por ahora es un placeholder que no hace nada
}

// Función para agregar validación en tiempo real
function addRealTimeValidation() {
  // Validar campos obligatorios en tiempo real
  document.addEventListener('input', function(e) {
    const input = e.target;
    
    // Verificar que el input existe y tiene las clases necesarias
    if (!input || !input.classList || !input.classList.contains('form-input')) {
      return;
    }
    
    const formGroup = input.closest('.form-group');
    
    if (formGroup) {
      // Remover clases de validación previas
      input.classList.remove('valid', 'invalid');
      
      // Validar según el tipo de campo
      if (input.hasAttribute('required') || (input.placeholder && input.placeholder.includes('obligatorio'))) {
        if (input.value.trim() === '') {
          input.classList.add('invalid');
        } else {
          input.classList.add('valid');
        }
      }
      
      // Validar email
      if (input.type === 'email' && input.value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(input.value)) {
          input.classList.add('invalid');
        } else {
          input.classList.add('valid');
        }
      }
      
      // Validar números positivos
      if (input.type === 'number' && input.value) {
        if (parseFloat(input.value) < 0) {
          input.classList.add('invalid');
        } else {
          input.classList.add('valid');
        }
      }
    }
  });
}

// Función para prevenir números negativos
function preventNegativeNumbers() {
  // Prevenir números negativos en inputs numéricos
  document.addEventListener('input', function(e) {
    const input = e.target;
    
    if (input.type === 'number' && input.classList.contains('no-negative')) {
      if (parseFloat(input.value) < 0) {
        input.value = Math.abs(parseFloat(input.value));
      }
    }
  });
  
  // Prevenir entrada de números negativos
  document.addEventListener('keydown', function(e) {
    const input = e.target;
    
    if (input.type === 'number' && input.classList.contains('no-negative')) {
      if (e.key === '-') {
        e.preventDefault();
      }
    }
  });
}



// Función para limpiar formulario de nuevo registro
function clearNewRecordForm() {
  // Limpiar campos de texto
  document.querySelectorAll('#newRecordModal .form-input').forEach(input => {
    if (input.type === 'text' || input.type === 'number') {
      input.value = '';
    } else if (input.type === 'date') {
      input.value = '';
    } else if (input.tagName === 'SELECT') {
      input.selectedIndex = 0;
    }
    
    // Remover clases de validación
    input.classList.remove('valid', 'invalid');
  });
  
  // Limpiar listas dinámicas (mantener solo el primer elemento)
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  const measurementsList = modal.querySelector('#measurementsList');
  if (measurementsList) {
    const measurementRows = measurementsList.querySelectorAll('.measurement-row');
    for (let i = 1; i < measurementRows.length; i++) {
      measurementRows[i].remove();
    }
    // Limpiar el primer elemento
    if (measurementRows[0]) {
      measurementRows[0].querySelectorAll('input, select').forEach(input => {
        input.value = '';
      });
    }
  }
  
  const medicationsList = modal.querySelector('#medicationsList');
  if (medicationsList) {
    const medicationRows = medicationsList.querySelectorAll('.medication-row');
    for (let i = 1; i < medicationRows.length; i++) {
      medicationRows[i].remove();
    }
    // Limpiar el primer elemento
    if (medicationRows[0]) {
      medicationRows[0].querySelectorAll('input').forEach(input => {
        input.value = '';
      });
    }
  }
  
  const proceduresList = modal.querySelector('#proceduresList');
  if (proceduresList) {
    const procedureRows = proceduresList.querySelectorAll('.procedure-row');
    for (let i = 1; i < procedureRows.length; i++) {
      procedureRows[i].remove();
    }
    // Limpiar el primer elemento
    if (procedureRows[0]) {
      procedureRows[0].querySelectorAll('input, select').forEach(input => {
        if (input.tagName === 'SELECT') {
          input.selectedIndex = 0;
        } else {
          input.value = '';
        }
      });
    }
  }
  
  // Ocultar alertas
  document.querySelectorAll('.section-alert').forEach(alert => {
    alert.classList.remove('show');
  });
  
  // Resetear navegación a la primera sección
  window.currentSection = 'detalles';
  updateModalButtons();
}

// Validadores para modales
let modalValidator;
let measurementValidator;

// Función para agregar validación en tiempo real al modal
function addRealTimeModalValidation() {
  const modal = document.getElementById('newRecordModal');
  if (!modal) return;
  
  // Inicializar validador del modal si no existe
  if (!modalValidator) {
    modalValidator = new FormValidator('#newRecordModal', {
      validateOnInput: true,
      validateOnBlur: true
    });
    
    // Agregar validadores específicos para campos médicos
    modalValidator.addValidator('diagnostico', (value) => value.length >= 10, 'El diagnóstico debe tener al menos 10 caracteres');
    modalValidator.addValidator('centroMedico', (value) => value.length >= 3, 'Ingresa el nombre del centro médico');
    modalValidator.addValidator('nombreDoctor', validators.fullName, 'Ingresa el nombre completo del doctor');
  }
  
  // Validar que al menos un medicamento tenga nombre y dosis
  const medications = document.querySelectorAll('#medicationsList .medication-row');
  let hasValidMedication = false;
  
  medications.forEach(medication => {
    const nombreInput = medication.querySelector('input[placeholder*="Paracetamol"]');
    const dosisInput = medication.querySelector('input[placeholder*="1 tableta"]');
    
    if (nombreInput && dosisInput && nombreInput.value.trim() && dosisInput.value.trim()) {
      hasValidMedication = true;
    }
  });
  
  // Si hay medicamentos pero ninguno es válido, mostrar alerta
  if (medications.length > 0 && !hasValidMedication) {
    showSectionAlert('tratamiento', 'Por favor completa al menos un medicamento con nombre y dosis', 'error');
  }
}

// Función para agregar validación en tiempo real al modal de mediciones
function addMeasurementModalValidation() {
  const modal = document.getElementById('newMeasurementModal');
  if (!modal) return;
  
  // Inicializar validador del modal de mediciones si no existe
  if (!measurementValidator) {
    measurementValidator = new FormValidator('#newMeasurementModal', {
      validateOnInput: true,
      validateOnBlur: true
    });
    
    // Validadores específicos para mediciones
    measurementValidator.addValidator('valor', (value) => {
      const num = parseFloat(value);
      return num > 0 && num < 1000; // Rango razonable para medidas corporales
    }, 'El valor debe estar entre 0 y 1000');
    
    measurementValidator.addValidator('fecha', validators.notFutureDate, 'La fecha no puede ser futura');
  }
} 



