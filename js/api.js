// Shared helpers for talking to the Spring Boot REST API.
// Every page uses api() so that a failed save shows the real error instead of a fake "success".
const API_ROOT = (location.port === '8080' ? '' : 'http://localhost:8080') + '/api';

async function api(path, method = 'GET', body) {
  const options = { method, headers: {} };
  if (body !== undefined) {
    options.headers['Content-Type'] = 'application/json';
    options.body = JSON.stringify(body);
  }

  let res;
  try {
    res = await fetch(API_ROOT + path, options);
  } catch (err) {
    throw new Error('Cannot reach the server. Is the Spring Boot backend running on port 8080?');
  }

  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch (e) { data = text; }

  if (!res.ok) {
    let message = (data && (data.message || data.error)) || `Request failed (HTTP ${res.status})`;
    if (data && Array.isArray(data.details) && data.details.length) {
      message += '\n- ' + data.details.join('\n- ');
    }
    throw new Error(message);
  }
  return data;
}

// Escape values from the database before putting them into innerHTML
function esc(value) {
  if (value === null || value === undefined) return '';
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// Convert a Doctor from the API into the shape the doctor cards use
function toUiDoctor(d) {
  return {
    id: d.id,
    name: d.name,
    title: d.title || '',
    dept: d.department ? d.department.name : 'General',
    deptId: d.department ? d.department.id : null,
    qual: d.qualifications || '',
    fee: Number(d.fee) || 0,
    room: d.roomNumber || '',
    schedule: d.scheduleDays ? d.scheduleDays.split(',').map(s => s.trim()).filter(Boolean) : [],
    time: d.timeSlot || '',
    gender: d.gender || '',
    email: d.email || '',
    phone: d.phone || '',
    img: d.imageUrl || 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80'
  };
}

// Fill a <select> with options; keeps the current value if it still exists
function fillSelect(select, items, valueOf, labelOf, placeholder) {
  const current = select.value;
  select.innerHTML = (placeholder ? `<option value="">${esc(placeholder)}</option>` : '') +
    items.map(item => `<option value="${esc(valueOf(item))}">${esc(labelOf(item))}</option>`).join('');
  if (current && [...select.options].some(o => o.value === current)) select.value = current;
}
