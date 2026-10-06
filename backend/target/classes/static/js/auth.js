// Shared login helpers for the Medicare HMS pages.
// Loaded in the <head> of every page so the page guard runs before anything is shown.
const AUTH_API = (location.port === '8080' ? '' : 'http://localhost:8080') + '/api/auth';
const AUTH_KEY = 'medicareUser';

// Which roles may open each page. Pages not listed here are public.
const PAGE_ROLES = {
  'appointments.html': ['patient', 'doctor', 'staff', 'admin'],
  'pharmacy.html': ['doctor', 'staff', 'admin'],
  'bloodbank.html': ['doctor', 'staff', 'admin'],
  'wards.html': ['doctor', 'staff', 'admin'],
  'ot.html': ['doctor', 'staff', 'admin'],
  'doctor-scheduling.html': ['doctor', 'admin']
};

// Page each role lands on after logging in
const HOME_PAGE = {
  patient: 'appointments.html',
  doctor: 'doctor-scheduling.html',
  staff: 'pharmacy.html',
  admin: 'index.html'
};

// Sri Lankan phone numbers: 0 + 9 digits or +94 + 9 digits; spaces and dashes are ignored.
// The same rule is checked again on the server (PhoneValidator.java).
function isValidPhone(phone) {
  return /^(0\d{9}|\+94\d{9})$/.test(String(phone || '').replace(/[\s-]/g, ''));
}

const Auth = {
  async post(path, body) {
    let res;
    try {
      res = await fetch(`${AUTH_API}/${path}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    } catch (err) {
      throw new Error('Cannot reach the server. Is the Spring Boot backend running on port 8080?');
    }
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || data.message || `Request failed (${res.status})`);
    return data;
  },

  login(email, password, role) {
    return this.post('login', { email, password, role });
  },

  register(details) {
    return this.post('register', details);
  },

  saveUser(user, remember) {
    const store = remember ? localStorage : sessionStorage;
    store.setItem(AUTH_KEY, JSON.stringify(user));
  },

  currentUser() {
    try {
      const raw = sessionStorage.getItem(AUTH_KEY) || localStorage.getItem(AUTH_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch (e) {
      return null;
    }
  },

  role() {
    const user = this.currentUser();
    return user ? user.role : 'guest';
  },

  hasRole(...roles) {
    return roles.includes(this.role());
  },

  canOpen(page) {
    const allowed = PAGE_ROLES[page];
    return !allowed || allowed.includes(this.role());
  },

  logout() {
    sessionStorage.removeItem(AUTH_KEY);
    localStorage.removeItem(AUTH_KEY);
    window.location.href = 'login.html';
  },

  // Send the visitor to the login page if this page is not allowed for them
  guardPage() {
    const page = location.pathname.split('/').pop() || 'index.html';
    if (this.canOpen(page)) return;
    const reason = this.currentUser() ? 'denied' : 'login';
    location.replace(`login.html?next=${encodeURIComponent(page)}&reason=${reason}`);
  },

  // Header: replace the Login button with "name (role) + Logout", hide menu links the role cannot open
  decorateHeader() {
    const user = this.currentUser();
    if (user) {
      document.querySelectorAll('#navMenu a.nav-link').forEach(link => {
        const page = (link.getAttribute('href') || '').split('#')[0];
        if (!this.canOpen(page)) link.style.display = 'none';
      });
    }
    const loginBtn = document.querySelector('.nav-cta a[href="login.html"]');
    if (!user || !loginBtn) return;
    const box = document.createElement('div');
    box.className = 'user-chip';
    box.innerHTML = `
      <span class="user-chip-name"><i class="fa-solid fa-circle-user"></i> <span></span></span>
      <span class="user-chip-role"></span>
      <button type="button" class="user-chip-logout" title="Log out"><i class="fa-solid fa-right-from-bracket"></i> Logout</button>`;
    box.querySelector('.user-chip-name span').textContent = user.firstName;
    box.querySelector('.user-chip-role').textContent = user.role;
    box.querySelector('.user-chip-logout').addEventListener('click', () => this.logout());
    loginBtn.replaceWith(box);
  }
};

// The role is put on <html> straight away. CSS in style.css then hides every element
// whose data-roles list does not include it, e.g. <button data-roles="staff admin">.
document.documentElement.dataset.role = Auth.role();
Auth.guardPage();
document.addEventListener('DOMContentLoaded', () => Auth.decorateHeader());
