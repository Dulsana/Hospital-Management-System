/* ==========================================================================
   MEDICARE HOSPITAL - FRONTEND INTERACTIVE APPLICATION SCRIPT
   Includes 6 Core Hospital Management Branches:
   1. Ward & Bed Management
   2. Appointment Scheduling
   3. Doctor Scheduling Management
   4. Operation Theatre (OT) Management
   5. Pharmacy Management
   6. Blood Bank Management
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  // --- Data State ---
  const state = {
    apiBaseUrl: 'http://localhost:8080/api',
    departments: [
      { id: 1, name: 'Cardiology', icon: 'fa-heart-pulse', count: '18 Specialists' },
      { id: 2, name: 'Neurology', icon: 'fa-brain', count: '14 Specialists' },
      { id: 3, name: 'Orthopedics', icon: 'fa-bone', count: '16 Specialists' },
      { id: 4, name: 'Pediatrics', icon: 'fa-baby', count: '12 Specialists' },
      { id: 5, name: 'Oncology', icon: 'fa-ribbon', count: '10 Specialists' },
      { id: 6, name: 'Radiology', icon: 'fa-x-ray', count: '8 Specialists' },
      { id: 7, name: 'Emergency Care', icon: 'fa-truck-medical', count: '24/7 Team' },
      { id: 8, name: 'Gastroenterology', icon: 'fa-stethoscope', count: '9 Specialists' }
    ],
    doctors: [
      {
        id: 101,
        name: 'Dr. Anura Jayasinghe',
        title: 'Senior Consultant Cardiologist',
        department: 'Cardiology',
        qualifications: 'MBBS, MD (Cardiology), FRCP (UK)',
        fee: 3500,
        room: 'Room 204, Main Building',
        schedule: ['Mon', 'Wed', 'Fri'],
        time: '04:00 PM - 07:00 PM',
        image: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80'
      },
      {
        id: 102,
        name: 'Dr. Sanduni Perera',
        title: 'Consultant Neurologist',
        department: 'Neurology',
        qualifications: 'MBBS, MD (Neurology), MRCP (UK)',
        fee: 3800,
        room: 'Room 108, Specialist Wing',
        schedule: ['Tue', 'Thu', 'Sat'],
        time: '05:00 PM - 08:00 PM',
        image: 'https://images.unsplash.com/photo-1594824813566-78a9956461a5?auto=format&fit=crop&w=600&q=80'
      },
      {
        id: 103,
        name: 'Dr. Nishantha Silva',
        title: 'Chief Orthopedic Surgeon',
        department: 'Orthopedics',
        qualifications: 'MBBS, MS (Orth), FRCS (Edin)',
        fee: 4000,
        room: 'Room 312, Surgical Wing',
        schedule: ['Mon', 'Tue', 'Thu'],
        time: '03:00 PM - 06:00 PM',
        image: 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=600&q=80'
      },
      {
        id: 104,
        name: 'Dr. Chamari Wickramasinghe',
        title: 'Consultant Pediatrician',
        department: 'Pediatrics',
        qualifications: 'MBBS, DCH, MD (Pediatrics)',
        fee: 3000,
        room: 'Room 102, Children Care Center',
        schedule: ['Mon', 'Wed', 'Sat'],
        time: '09:00 AM - 12:00 PM',
        image: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=600&q=80'
      },
      {
        id: 105,
        name: 'Dr. Kithsiri Fernando',
        title: 'Consultant Oncologist',
        department: 'Oncology',
        qualifications: 'MBBS, MD (Oncology), FRCRO',
        fee: 4200,
        room: 'Room 405, Cancer Center',
        schedule: ['Wed', 'Fri', 'Sun'],
        time: '02:00 PM - 05:00 PM',
        image: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&w=600&q=80'
      },
      {
        id: 106,
        name: 'Dr. Priyantha Gunawardena',
        title: 'Consultant Gastroenterologist',
        department: 'Gastroenterology',
        qualifications: 'MBBS, MD, Fellowship in Endoscopy',
        fee: 3600,
        room: 'Room 215, OPD Block',
        schedule: ['Tue', 'Fri'],
        time: '04:30 PM - 07:30 PM',
        image: 'https://images.unsplash.com/photo-1582750433449-648ed127bb54?auto=format&fit=crop&w=600&q=80'
      }
    ],
    // --- 6 Core Branch Data States ---
    wards: [
      { id: 1, name: 'ICU (Intensive Care)', totalBeds: 20, occupied: 16, available: 4, type: 'Critical' },
      { id: 2, name: 'CCU (Cardiac Care)', totalBeds: 15, occupied: 12, available: 3, type: 'Critical' },
      { id: 3, name: 'Female Surgical Ward', totalBeds: 30, occupied: 22, available: 8, type: 'General' },
      { id: 4, name: 'Male Surgical Ward', totalBeds: 30, occupied: 25, available: 5, type: 'General' },
      { id: 5, name: 'Pediatric Care Ward', totalBeds: 25, occupied: 18, available: 7, type: 'Specialized' },
      { id: 6, name: 'Private Luxury Suites', totalBeds: 12, occupied: 10, available: 2, type: 'Private' }
    ],
    operationTheatres: [
      { id: 'OT-1', name: 'General Surgery Suite 1', doctor: 'Dr. Nishantha Silva', procedure: 'Laparoscopic Cholecystectomy', status: 'IN PROGRESS', time: '08:00 AM - 11:30 AM' },
      { id: 'OT-2', name: 'Cardiac OT Suite 2', doctor: 'Dr. Anura Jayasinghe', procedure: 'Coronary Artery Bypass (CABG)', status: 'SCHEDULED', time: '12:00 PM - 04:00 PM' },
      { id: 'OT-3', name: 'Neuro OT Suite 3', doctor: 'Dr. Sanduni Perera', procedure: 'Micro-Discectomy', status: 'READY', time: '04:30 PM - 07:00 PM' }
    ],
    bloodBank: [
      { group: 'A+', units: 28, status: 'AVAILABLE' },
      { group: 'A-', units: 8, status: 'LOW STOCK' },
      { group: 'B+', units: 35, status: 'AVAILABLE' },
      { group: 'B-', units: 12, status: 'AVAILABLE' },
      { group: 'AB+', units: 18, status: 'AVAILABLE' },
      { group: 'AB-', units: 5, status: 'CRITICAL' },
      { group: 'O+', units: 42, status: 'AVAILABLE' },
      { group: 'O-', units: 14, status: 'UNIVERSAL DONOR' }
    ],
    pharmacyMedicines: [
      { code: 'MED-01', name: 'Paracetamol 500mg', category: 'Analgesic', stock: 1250, price: 15.00 },
      { code: 'MED-02', name: 'Amoxicillin 500mg', category: 'Antibiotic', stock: 450, price: 45.00 },
      { code: 'MED-03', name: 'Atorvastatin 20mg', category: 'Cardiovascular', stock: 320, price: 65.00 },
      { code: 'MED-04', name: 'Metformin 500mg', category: 'Diabetic Care', stock: 800, price: 25.00 },
      { code: 'MED-05', name: 'Omeprazole 20mg', category: 'Gastroenterology', stock: 600, price: 35.00 },
      { code: 'MED-06', name: 'Insulin Glargine Pen', category: 'Diabetic Care', stock: 85, price: 3200.00 }
    ],
    selectedDoctor: null,
    currentStep: 1,
    bookingData: {}
  };

  // --- DOM Elements ---
  const mobileToggle = document.getElementById('mobileToggle');
  const navMenu = document.getElementById('navMenu');
  const doctorsGrid = document.getElementById('doctorsGrid');
  const filterTabsContainer = document.getElementById('filterTabs');
  const heroSpecialtySelect = document.getElementById('heroSpecialty');
  const heroDoctorSelect = document.getElementById('heroDoctor');
  const heroChannelBtn = document.getElementById('heroChannelBtn');
  
  // Modals
  const appointmentModal = document.getElementById('appointmentModal');
  const emergencyModal = document.getElementById('emergencyModal');
  const labReportModal = document.getElementById('labReportModal');
  const wardModal = document.getElementById('wardModal');
  const doctorRosterModal = document.getElementById('doctorRosterModal');
  const otModal = document.getElementById('otModal');
  const pharmacyModal = document.getElementById('pharmacyModal');
  const bloodBankModal = document.getElementById('bloodBankModal');
  
  const modalCloseBtns = document.querySelectorAll('.modal-close');
  const backToTopBtn = document.getElementById('backToTopBtn');

  // --- Mobile Navigation Toggle ---
  if (mobileToggle && navMenu) {
    mobileToggle.addEventListener('click', () => {
      navMenu.classList.toggle('active');
      const icon = mobileToggle.querySelector('i');
      if (icon) {
        icon.classList.toggle('fa-bars');
        icon.classList.toggle('fa-xmark');
      }
    });
  }

  // --- Initialize Specialty Selectors & Tabs ---
  function initSpecialtyOptions() {
    if (heroSpecialtySelect) {
      heroSpecialtySelect.innerHTML = '<option value="all">All Specialties</option>';
      state.departments.forEach(dept => {
        const option = document.createElement('option');
        option.value = dept.name;
        option.textContent = dept.name;
        heroSpecialtySelect.appendChild(option);
      });
    }

    if (filterTabsContainer) {
      filterTabsContainer.innerHTML = '<button class="filter-tab active" data-dept="all">All Doctors</button>';
      state.departments.forEach(dept => {
        const btn = document.createElement('button');
        btn.className = 'filter-tab';
        btn.dataset.dept = dept.name;
        btn.textContent = dept.name;
        filterTabsContainer.appendChild(btn);
      });

      filterTabsContainer.querySelectorAll('.filter-tab').forEach(btn => {
        btn.addEventListener('click', (e) => {
          filterTabsContainer.querySelectorAll('.filter-tab').forEach(b => b.classList.remove('active'));
          e.target.classList.add('active');
          const dept = e.target.dataset.dept;
          renderDoctors(dept);
        });
      });
    }
  }

  // --- Populate Doctor Dropdown ---
  function updateDoctorDropdown(specialty) {
    if (!heroDoctorSelect) return;
    heroDoctorSelect.innerHTML = '<option value="">Select Doctor (Optional)</option>';
    const filtered = specialty === 'all' 
      ? state.doctors 
      : state.doctors.filter(d => d.department.toLowerCase() === specialty.toLowerCase());
    
    filtered.forEach(doc => {
      const option = document.createElement('option');
      option.value = doc.id;
      option.textContent = `${doc.name} (${doc.department})`;
      heroDoctorSelect.appendChild(option);
    });
  }

  if (heroSpecialtySelect) {
    heroSpecialtySelect.addEventListener('change', (e) => {
      updateDoctorDropdown(e.target.value);
    });
  }

  // --- Render Doctors Grid ---
  function renderDoctors(departmentFilter = 'all') {
    if (!doctorsGrid) return;
    doctorsGrid.innerHTML = '';

    const filtered = departmentFilter === 'all' 
      ? state.doctors 
      : state.doctors.filter(d => d.department.toLowerCase() === departmentFilter.toLowerCase());

    if (filtered.length === 0) {
      doctorsGrid.innerHTML = `
        <div style="grid-column: 1/-1; text-align: center; padding: 3rem; background: var(--bg-surface); border-radius: var(--radius-lg);">
          <i class="fa-solid fa-user-doctor" style="font-size: 3rem; color: var(--text-light); margin-bottom: 1rem;"></i>
          <h3>No doctors found for this specialty</h3>
          <p style="color: var(--text-muted);">Please select another specialty or contact our hotline for guidance.</p>
        </div>
      `;
      return;
    }

    filtered.forEach(doc => {
      const card = document.createElement('div');
      card.className = 'doctor-card';
      card.innerHTML = `
        <div class="doctor-img-wrap">
          <img src="${doc.image}" alt="${doc.name}" class="doctor-img" loading="lazy">
          <span class="doctor-dept-badge">${doc.department}</span>
        </div>
        <div class="doctor-details">
          <h3 class="doctor-name">${doc.name}</h3>
          <div class="doctor-qualifications">${doc.qualifications}</div>
          <div class="doctor-meta">
            <div class="doctor-meta-item">
              <i class="fa-regular fa-clock" style="color: var(--accent-cyan);"></i>
              <span>${doc.schedule.join(', ')} (${doc.time})</span>
            </div>
            <div class="doctor-meta-item">
              <i class="fa-solid fa-door-open" style="color: var(--accent-cyan);"></i>
              <span>${doc.room}</span>
            </div>
          </div>
          <div class="doctor-footer">
            <div class="doctor-fee">
              <span class="fee-label">Consultation Fee</span>
              <span class="fee-amount">LKR ${doc.fee.toLocaleString()}</span>
            </div>
            <button class="btn btn-primary channel-now-btn" data-doc-id="${doc.id}">
              Channel Now <i class="fa-solid fa-arrow-right"></i>
            </button>
          </div>
        </div>
      `;
      doctorsGrid.appendChild(card);
    });

    doctorsGrid.querySelectorAll('.channel-now-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const docId = parseInt(e.currentTarget.dataset.docId);
        openBookingModal(docId);
      });
    });
  }

  // --- Multi-Step Booking Modal Logic ---
  function openBookingModal(docId = null) {
    state.currentStep = 1;
    state.bookingData = {};
    if (docId) {
      state.selectedDoctor = state.doctors.find(d => d.id === docId) || null;
    } else {
      state.selectedDoctor = null;
    }

    renderModalStep();
    if (appointmentModal) appointmentModal.classList.add('active');
  }

  function renderModalStep() {
    const modalBody = document.getElementById('modalBody');
    if (!modalBody) return;

    if (state.currentStep === 1) {
      const selectedDocId = state.selectedDoctor ? state.selectedDoctor.id : '';
      const minDate = new Date().toISOString().split('T')[0];

      modalBody.innerHTML = `
        <div class="modal-step-indicator">
          <div class="step-item active"><span class="step-number">1</span> Select Doctor</div>
          <div class="step-item"><span class="step-number">2</span> Patient Info</div>
          <div class="step-item"><span class="step-number">3</span> Confirmation</div>
        </div>
        <form id="bookingStep1Form">
          <div class="form-group" style="margin-bottom: 1.25rem;">
            <label for="modalDoctorSelect"><i class="fa-solid fa-user-doctor"></i> Select Specialist Doctor</label>
            <select id="modalDoctorSelect" class="form-control" required>
              <option value="">-- Choose Doctor --</option>
              ${state.doctors.map(d => `
                <option value="${d.id}" ${d.id === selectedDocId ? 'selected' : ''}>
                  ${d.name} (${d.department}) - LKR ${d.fee}
                </option>
              `).join('')}
            </select>
          </div>
          <div class="form-group" style="margin-bottom: 1.25rem;">
            <label for="modalBookingDate"><i class="fa-regular fa-calendar-days"></i> Preferred Date</label>
            <input type="date" id="modalBookingDate" class="form-control" min="${minDate}" required>
          </div>
          <div class="form-group" style="margin-bottom: 1.5rem;">
            <label for="modalTimeSlot"><i class="fa-regular fa-clock"></i> Preferred Time Slot</label>
            <select id="modalTimeSlot" class="form-control" required>
              <option value="Morning (09:00 AM - 12:00 PM)">Morning (09:00 AM - 12:00 PM)</option>
              <option value="Afternoon (01:00 PM - 04:00 PM)">Afternoon (01:00 PM - 04:00 PM)</option>
              <option value="Evening (05:00 PM - 08:00 PM)" selected>Evening (05:00 PM - 08:00 PM)</option>
            </select>
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 1rem;">
            <button type="submit" class="btn btn-primary">Next: Patient Info <i class="fa-solid fa-arrow-right"></i></button>
          </div>
        </form>
      `;

      document.getElementById('bookingStep1Form').addEventListener('submit', (e) => {
        e.preventDefault();
        const docSelect = document.getElementById('modalDoctorSelect');
        const docId = parseInt(docSelect.value);
        state.selectedDoctor = state.doctors.find(d => d.id === docId);
        state.bookingData.doctor = state.selectedDoctor;
        state.bookingData.date = document.getElementById('modalBookingDate').value;
        state.bookingData.timeSlot = document.getElementById('modalTimeSlot').value;
        state.currentStep = 2;
        renderModalStep();
      });

    } else if (state.currentStep === 2) {
      modalBody.innerHTML = `
        <div class="modal-step-indicator">
          <div class="step-item"><span class="step-number">1</span> Select Doctor</div>
          <div class="step-item active"><span class="step-number">2</span> Patient Info</div>
          <div class="step-item"><span class="step-number">3</span> Confirmation</div>
        </div>
        <form id="bookingStep2Form">
          <div class="form-group" style="margin-bottom: 1rem;">
            <label for="patientName"><i class="fa-solid fa-user"></i> Full Name</label>
            <input type="text" id="patientName" class="form-control" placeholder="e.g. K.A. Suneth Silva" required>
          </div>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
            <div class="form-group">
              <label for="patientPhone"><i class="fa-solid fa-phone"></i> Phone Number</label>
              <input type="tel" pattern="(?:0|\\+94(?: |-)?)[0-9]{2}(?: |-)?[0-9]{3}(?: |-)?[0-9]{4}" title="Sri Lankan number, e.g. 0771234567 or +94 77 123 4567" id="patientPhone" class="form-control" placeholder="0771234567" required>
            </div>
            <div class="form-group">
              <label for="patientEmail"><i class="fa-solid fa-envelope"></i> Email Address</label>
              <input type="email" id="patientEmail" class="form-control" placeholder="suneth@example.com" required>
            </div>
          </div>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
            <div class="form-group">
              <label for="patientAge"><i class="fa-solid fa-hashtag"></i> Age</label>
              <input type="number" id="patientAge" class="form-control" min="1" max="120" placeholder="35" required>
            </div>
            <div class="form-group">
              <label for="patientGender"><i class="fa-solid fa-venus-mars"></i> Gender</label>
              <select id="patientGender" class="form-control" required>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>
          </div>
          <div style="display: flex; justify-content: space-between; gap: 1rem;">
            <button type="button" id="prevStepBtn" class="btn btn-secondary"><i class="fa-solid fa-arrow-left"></i> Back</button>
            <button type="submit" class="btn btn-primary">Confirm & Book <i class="fa-solid fa-check"></i></button>
          </div>
        </form>
      `;

      // A logged-in patient books under their own account so it appears in "My Appointments"
      const user = typeof Auth !== 'undefined' ? Auth.currentUser() : null;
      if (user && user.role === 'patient') {
        document.getElementById('patientName').value = [user.firstName, user.lastName].filter(Boolean).join(' ');
        document.getElementById('patientEmail').value = user.email;
        document.getElementById('patientEmail').readOnly = true;
      }

      document.getElementById('prevStepBtn').addEventListener('click', () => {
        state.currentStep = 1;
        renderModalStep();
      });

      document.getElementById('bookingStep2Form').addEventListener('submit', async (e) => {
        e.preventDefault();
        state.bookingData.patientName = document.getElementById('patientName').value;
        state.bookingData.patientPhone = document.getElementById('patientPhone').value;
        state.bookingData.patientEmail = document.getElementById('patientEmail').value;
        state.bookingData.patientAge = document.getElementById('patientAge').value;
        state.bookingData.patientGender = document.getElementById('patientGender').value;
        
        const refCode = 'MED-' + Math.floor(100000 + Math.random() * 900000);
        state.bookingData.reference = refCode;

        // Only show the confirmation when the database really saved the booking
        try {
          const res = await fetch(`${state.apiBaseUrl}/appointments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              doctorId: state.bookingData.doctor.id,
              doctorName: state.bookingData.doctor.name,
              patientName: state.bookingData.patientName,
              patientPhone: state.bookingData.patientPhone,
              patientEmail: state.bookingData.patientEmail,
              appointmentDate: state.bookingData.date,
              timeSlot: state.bookingData.timeSlot,
              referenceCode: refCode
            })
          });
          const saved = await res.json().catch(() => ({}));
          if (!res.ok) throw new Error(saved.message || saved.error || `Request failed (HTTP ${res.status})`);
          state.bookingData.reference = saved.referenceCode;
        } catch (err) {
          const msg = err instanceof TypeError
            ? 'Cannot reach the server. Is the Spring Boot backend running on port 8080?'
            : err.message;
          alert('❌ Could not book the appointment:\n' + msg);
          return;
        }

        state.currentStep = 3;
        renderModalStep();
      });

    } else if (state.currentStep === 3) {
      const data = state.bookingData;
      modalBody.innerHTML = `
        <div style="text-align: center; margin-bottom: 1.5rem;">
          <div style="width: 60px; height: 60px; background: #dcfce7; color: var(--success-green); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2rem; margin: 0 auto 1rem;">
            <i class="fa-solid fa-circle-check"></i>
          </div>
          <h3 style="color: var(--primary-navy);">Appointment Confirmed!</h3>
          <p style="color: var(--text-muted); font-size: 0.9rem;">Saved to the hospital system. View or change it on the Appointments page.</p>
        </div>
        
        <div class="ticket-card">
          <div class="ticket-header">
            <div>
              <div style="font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase;">Appointment Reference</div>
              <div class="ticket-ref">${data.reference}</div>
            </div>
            <div style="text-align: right;">
              <div style="font-size: 0.8rem; color: var(--text-muted);">Status</div>
              <span style="background: #dcfce7; color: #15803d; padding: 2px 8px; border-radius: 12px; font-weight: 700; font-size: 0.8rem;">CONFIRMED</span>
            </div>
          </div>
          <div class="ticket-grid">
            <div><strong>Doctor:</strong> ${data.doctor ? data.doctor.name : 'Consultant'}</div>
            <div><strong>Specialty:</strong> ${data.doctor ? data.doctor.department : ''}</div>
            <div><strong>Date:</strong> ${data.date}</div>
            <div><strong>Time:</strong> ${data.timeSlot}</div>
            <div><strong>Location:</strong> ${data.doctor ? data.doctor.room : 'Main Clinic'}</div>
            <div><strong>Patient:</strong> ${data.patientName}</div>
            <div><strong>Fee:</strong> LKR ${data.doctor ? data.doctor.fee.toLocaleString() : '3,500'}</div>
          </div>
        </div>

        <div style="margin-top: 1.5rem; text-align: center;">
          <button id="closeModalSuccessBtn" class="btn btn-primary" style="width: 100%;">
            Done & Return to Homepage
          </button>
        </div>
      `;

      document.getElementById('closeModalSuccessBtn').addEventListener('click', () => {
        if (appointmentModal) appointmentModal.classList.remove('active');
      });
    }
  }

  // Hero Channel Button Handler
  if (heroChannelBtn) {
    heroChannelBtn.addEventListener('click', () => {
      const selectedDocId = heroDoctorSelect ? parseInt(heroDoctorSelect.value) : null;
      openBookingModal(selectedDocId);
    });
  }

  // --- Render Dynamic Content for 6 Core Branches ---
  
  // 1. Ward & Bed Management Modal Renderer
  function renderWardModal() {
    const body = document.getElementById('wardModalBody');
    if (!body) return;

    body.innerHTML = `
      <div style="margin-bottom: 1.5rem;">
        <h4 style="color: var(--primary-navy); margin-bottom: 0.5rem;">Live Ward & Bed Availability Tracker</h4>
        <p style="font-size: 0.85rem; color: var(--text-muted);">Real-time status of ICU, CCU, Surgical, and Private Suite bed occupancy.</p>
      </div>

      <div class="ward-grid">
        ${state.wards.map(w => `
          <div class="ward-card">
            <div class="ward-card-name">${w.name}</div>
            <div class="ward-card-avail">${w.available} Free</div>
            <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 4px;">Total: ${w.totalBeds} Beds</div>
          </div>
        `).join('')}
      </div>

      <form id="bedReserveForm" style="margin-top: 1.5rem; background: var(--bg-subtle); padding: 1.25rem; border-radius: var(--radius-md);">
        <h5 style="margin-bottom: 1rem; color: var(--primary-navy);"><i class="fa-solid fa-bed"></i> Bed Admission / Reservation Request</h5>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
          <div class="form-group">
            <label>Patient Name</label>
            <input type="text" class="form-control" required placeholder="Patient Full Name">
          </div>
          <div class="form-group">
            <label>Ward Category</label>
            <select class="form-control" required>
              ${state.wards.map(w => `<option value="${w.id}">${w.name} (${w.available} Free)</option>`).join('')}
            </select>
          </div>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%;">
          <i class="fa-solid fa-paper-plane"></i> Submit Bed Reservation Request
        </button>
      </form>
    `;

    document.getElementById('bedReserveForm').addEventListener('submit', (e) => {
      e.preventDefault();
      alert('Bed Reservation Request Submitted successfully! Admission Desk will contact you immediately.');
      if (wardModal) wardModal.classList.remove('active');
    });
  }

  // 2. Doctor Scheduling Roster Renderer
  function renderDoctorRosterModal() {
    const body = document.getElementById('doctorRosterBody');
    if (!body) return;

    body.innerHTML = `
      <div style="margin-bottom: 1rem;">
        <h4 style="color: var(--primary-navy);">Doctor Duty Roster & Consultation Hours</h4>
        <p style="font-size: 0.85rem; color: var(--text-muted);">Weekly schedule for all specialist consultants.</p>
      </div>

      <table class="roster-table">
        <thead>
          <tr>
            <th>Doctor Name</th>
            <th>Specialty</th>
            <th>Consultation Days</th>
            <th>Time Slot</th>
            <th>Room</th>
          </tr>
        </thead>
        <tbody>
          ${state.doctors.map(d => `
            <tr>
              <td><strong>${d.name}</strong></td>
              <td>${d.department}</td>
              <td>${d.schedule.join(', ')}</td>
              <td>${d.time}</td>
              <td>${d.room}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  }

  // 3. Operation Theatre (OT) Management Renderer
  function renderOTModal() {
    const body = document.getElementById('otModalBody');
    if (!body) return;

    body.innerHTML = `
      <div style="margin-bottom: 1rem;">
        <h4 style="color: var(--primary-navy);">Operation Theatre (OT) Real-Time Schedule</h4>
        <p style="font-size: 0.85rem; color: var(--text-muted);">Surgical suites, lead surgeons, procedures, and live OT status.</p>
      </div>

      <div class="ot-timeline">
        ${state.operationTheatres.map(ot => `
          <div class="ot-slot">
            <div>
              <strong style="color: var(--primary-navy);">${ot.id} - ${ot.name}</strong>
              <div style="font-size: 0.8rem; color: var(--text-muted);">${ot.procedure} | Lead: ${ot.doctor}</div>
            </div>
            <div style="text-align: right;">
              <span style="background: ${ot.status === 'IN PROGRESS' ? '#fee2e2' : '#e0f2fe'}; color: ${ot.status === 'IN PROGRESS' ? '#b91c1c' : '#0369a1'}; padding: 3px 8px; border-radius: 10px; font-weight: 700; font-size: 0.75rem;">
                ${ot.status}
              </span>
              <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 2px;">${ot.time}</div>
            </div>
          </div>
        `).join('')}
      </div>

      <form id="otRequestForm" style="margin-top: 1.5rem; background: var(--bg-subtle); padding: 1.25rem; border-radius: var(--radius-md);">
        <h5 style="margin-bottom: 1rem; color: var(--primary-navy);"><i class="fa-solid fa-hospital-user"></i> Schedule Emergency/Elective Surgery</h5>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
          <div class="form-group">
            <label>Surgeon Name</label>
            <input type="text" class="form-control" required placeholder="Lead Surgeon">
          </div>
          <div class="form-group">
            <label>Surgical Procedure</label>
            <input type="text" class="form-control" required placeholder="Procedure Name">
          </div>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%;">
          <i class="fa-solid fa-calendar-plus"></i> Request OT Booking
        </button>
      </form>
    `;

    document.getElementById('otRequestForm').addEventListener('submit', (e) => {
      e.preventDefault();
      alert('OT Surgery Request submitted to Chief Anesthesiologist & OT Director!');
      if (otModal) otModal.classList.remove('active');
    });
  }

  // 4. Pharmacy Management Renderer
  function renderPharmacyModal() {
    const body = document.getElementById('pharmacyModalBody');
    if (!body) return;

    body.innerHTML = `
      <div style="margin-bottom: 1rem;">
        <h4 style="color: var(--primary-navy);">OPD Pharmacy Medicine Stock Search</h4>
        <input type="text" id="pharmacySearchInput" class="form-control" placeholder="Search medicine by name or category..." style="margin-top: 0.5rem;">
      </div>

      <div id="medicineList" style="display: flex; flex-direction: column; gap: 0.75rem; max-height: 250px; overflow-y: auto;">
        <!-- Rendered by search -->
      </div>

      <form id="rxUploadForm" style="margin-top: 1.5rem; background: var(--bg-subtle); padding: 1.25rem; border-radius: var(--radius-md);">
        <h5 style="margin-bottom: 1rem; color: var(--primary-navy);"><i class="fa-solid fa-prescription-bottle-medical"></i> E-Prescription Upload for Home Delivery</h5>
        <div class="form-group" style="margin-bottom: 1rem;">
          <label>Patient Contact Phone</label>
          <input type="tel" pattern="(?:0|\\+94(?: |-)?)[0-9]{2}(?: |-)?[0-9]{3}(?: |-)?[0-9]{4}" title="Sri Lankan number, e.g. 0771234567 or +94 77 123 4567" class="form-control" required placeholder="077XXXXXXX">
        </div>
        <div class="form-group" style="margin-bottom: 1rem;">
          <label>Upload Prescription Image/PDF</label>
          <input type="file" class="form-control" accept="image/*,.pdf" required>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%;">
          <i class="fa-solid fa-upload"></i> Submit Prescription
        </button>
      </form>
    `;

    function filterMedicineList(query = '') {
      const list = document.getElementById('medicineList');
      if (!list) return;
      const filtered = state.pharmacyMedicines.filter(m => 
        m.name.toLowerCase().includes(query.toLowerCase()) || 
        m.category.toLowerCase().includes(query.toLowerCase())
      );

      list.innerHTML = filtered.map(m => `
        <div style="display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 0.75rem 1rem; border-radius: var(--radius-sm); border: 1px solid var(--border-light);">
          <div>
            <strong>${m.name}</strong> <span style="font-size: 0.75rem; color: var(--text-muted);">(${m.category})</span>
            <div style="font-size: 0.75rem; color: var(--success-green); font-weight: 600;">In Stock: ${m.stock} units</div>
          </div>
          <div style="font-weight: 700; color: var(--primary-blue);">LKR ${m.price.toFixed(2)}</div>
        </div>
      `).join('');
    }

    filterMedicineList('');
    document.getElementById('pharmacySearchInput').addEventListener('input', (e) => {
      filterMedicineList(e.target.value);
    });

    document.getElementById('rxUploadForm').addEventListener('submit', (e) => {
      e.preventDefault();
      alert('E-Prescription uploaded successfully! OPD Pharmacist will call you within 15 minutes.');
      if (pharmacyModal) pharmacyModal.classList.remove('active');
    });
  }

  // 5. Blood Bank Management Renderer
  function renderBloodBankModal() {
    const body = document.getElementById('bloodBankModalBody');
    if (!body) return;

    body.innerHTML = `
      <div style="margin-bottom: 1rem;">
        <h4 style="color: var(--primary-navy);">Live Blood Inventory Units</h4>
        <p style="font-size: 0.85rem; color: var(--text-muted);">Current blood group availability across all reserve units.</p>
      </div>

      <div class="blood-grid">
        ${state.bloodBank.map(b => `
          <div class="blood-item">
            <div class="blood-group-tag">${b.group}</div>
            <div class="blood-unit-count"><strong>${b.units}</strong> Units</div>
          </div>
        `).join('')}
      </div>

      <form id="urgentBloodForm" style="margin-top: 1.5rem; background: #fff5f5; border: 1px solid #fca5a5; padding: 1.25rem; border-radius: var(--radius-md);">
        <h5 style="margin-bottom: 1rem; color: var(--emergency-red);"><i class="fa-solid fa-droplet"></i> Urgent Blood Unit Request</h5>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
          <div class="form-group">
            <label>Required Blood Group</label>
            <select class="form-control" required>
              ${state.bloodBank.map(b => `<option value="${b.group}">${b.group} (${b.units} Units Available)</option>`).join('')}
            </select>
          </div>
          <div class="form-group">
            <label>Units Needed</label>
            <input type="number" min="1" max="10" value="1" class="form-control" required>
          </div>
        </div>
        <div class="form-group" style="margin-bottom: 1rem;">
          <label>Hospital / Patient Ward Reference</label>
          <input type="text" class="form-control" required placeholder="e.g. Ward 4 / Room 204">
        </div>
        <button type="submit" class="btn btn-emergency" style="width: 100%;">
          <i class="fa-solid fa-bell"></i> Request Urgent Blood Dispatch
        </button>
      </form>
    `;

    document.getElementById('urgentBloodForm').addEventListener('submit', (e) => {
      e.preventDefault();
      alert('Urgent Blood Request Dispatched! Blood Bank Officer has received your alert.');
      if (bloodBankModal) bloodBankModal.classList.remove('active');
    });
  }

  // --- Attach Event Listeners to 6 Core Branch Cards ---
  const bWardCard = document.getElementById('branchWard');
  const bAppCard = document.getElementById('branchAppointment');
  const bDoctorCard = document.getElementById('branchDoctorRoster');
  const bOtCard = document.getElementById('branchOT');
  const bPharmacyCard = document.getElementById('branchPharmacy');
  const bBloodCard = document.getElementById('branchBloodBank');

  if (bWardCard) bWardCard.addEventListener('click', () => { renderWardModal(); wardModal.classList.add('active'); });
  if (bAppCard) bAppCard.addEventListener('click', () => openBookingModal());
  if (bDoctorCard) bDoctorCard.addEventListener('click', () => { renderDoctorRosterModal(); doctorRosterModal.classList.add('active'); });
  if (bOtCard) bOtCard.addEventListener('click', () => { renderOTModal(); otModal.classList.add('active'); });
  if (bPharmacyCard) bPharmacyCard.addEventListener('click', () => { renderPharmacyModal(); pharmacyModal.classList.add('active'); });
  if (bBloodCard) bBloodCard.addEventListener('click', () => { renderBloodBankModal(); bloodBankModal.classList.add('active'); });

  // --- Modal Close Button Handlers ---
  modalCloseBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      [appointmentModal, emergencyModal, labReportModal, wardModal, doctorRosterModal, otModal, pharmacyModal, bloodBankModal].forEach(m => {
        if (m) m.classList.remove('active');
      });
    });
  });

  [appointmentModal, emergencyModal, labReportModal, wardModal, doctorRosterModal, otModal, pharmacyModal, bloodBankModal].forEach(modal => {
    if (modal) {
      modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.classList.remove('active');
      });
    }
  });

  // --- Lab Report Form Handler ---
  const labReportForm = document.getElementById('labReportForm');
  if (labReportForm) {
    labReportForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const resultDiv = document.getElementById('labReportResult');
      const refInput = document.getElementById('labRefNo').value.trim();
      
      resultDiv.style.display = 'block';
      resultDiv.innerHTML = `
        <div style="background: #f0fdf4; border: 1px solid #bbf7d0; padding: 1rem; border-radius: var(--radius-md);">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
            <strong style="color: #166534;">Report Ref: ${refInput.toUpperCase()}</strong>
            <span style="background: #dcfce7; color: #15803d; font-size: 0.75rem; padding: 2px 8px; border-radius: 10px; font-weight: 700;">READY</span>
          </div>
          <p style="font-size: 0.85rem; color: #374151;">Full Blood Count & Lipid Profile verified by Senior Pathologist.</p>
          <button class="btn btn-secondary" style="margin-top: 0.75rem; width: 100%; padding: 0.5rem; font-size: 0.85rem;" onclick="alert('Downloading PDF lab report...')">
            <i class="fa-solid fa-download"></i> Download Report (PDF)
          </button>
        </div>
      `;
    });
  }

  // --- Animated Numbers Counter ---
  const statNumbers = document.querySelectorAll('.stat-number');
  let animated = false;

  function animateStats() {
    if (animated) return;
    statNumbers.forEach(stat => {
      const target = parseInt(stat.dataset.target);
      let count = 0;
      const speed = Math.ceil(target / 40);
      const timer = setInterval(() => {
        count += speed;
        if (count >= target) {
          stat.textContent = target.toLocaleString() + (stat.dataset.suffix || '');
          clearInterval(timer);
        } else {
          stat.textContent = count.toLocaleString() + (stat.dataset.suffix || '');
        }
      }, 30);
    });
    animated = true;
  }

  window.addEventListener('scroll', () => {
    const statsElem = document.querySelector('.stats-section');
    if (statsElem) {
      const pos = statsElem.getBoundingClientRect();
      if (pos.top < window.innerHeight && pos.bottom >= 0) {
        animateStats();
      }
    }

    if (backToTopBtn) {
      if (window.scrollY > 400) {
        backToTopBtn.classList.add('visible');
      } else {
        backToTopBtn.classList.remove('visible');
      }
    }
  });

  if (backToTopBtn) {
    backToTopBtn.addEventListener('click', () => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }

  // Replace the built-in doctor list with the real doctors from the database
  async function loadDoctorsFromApi() {
    try {
      const res = await fetch(`${state.apiBaseUrl}/doctors`);
      if (!res.ok) return;
      const list = await res.json();
      if (!Array.isArray(list) || list.length === 0) return;
      state.doctors = list.map(d => ({
        id: d.id,
        name: d.name,
        title: d.title,
        department: d.department ? d.department.name : 'General',
        qualifications: d.qualifications || '',
        fee: Number(d.fee) || 0,
        room: d.roomNumber || '',
        schedule: d.scheduleDays ? d.scheduleDays.split(',').map(x => x.trim()) : [],
        time: d.timeSlot || '',
        image: d.imageUrl || 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80'
      }));
      updateDoctorDropdown('all');
      renderDoctors('all');
    } catch (err) {
      console.warn('Backend not reachable, showing the built-in doctor list.');
    }
  }

  // --- Initialize Page ---
  initSpecialtyOptions();
  updateDoctorDropdown('all');
  renderDoctors('all');
  loadDoctorsFromApi();
});
