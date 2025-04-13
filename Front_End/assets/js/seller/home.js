
    $(document).ready(function() {
    // JWT Management
    let jwtToken = localStorage.getItem('jwtToken');
    let currentUser = JSON.parse(localStorage.getItem('currentUser')) || {};

    // Set up AJAX defaults with JWT
    $.ajaxSetup({
    beforeSend: function(xhr) {
    if (jwtToken) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + jwtToken);
}
}
});

    // Initial Load
    if (jwtToken) {
    loadDashboardData();
    loadServices();
    loadOrders();
    loadProfileData();
} else {
    window.location.href = '../index.html';
}

    // Dashboard Data Load
    function loadDashboardData() {
    $.ajax({
    url: 'http://localhost:8080/api/v1/sellers/dashboard',
    method: 'GET',
    success: function(response) {
    if (response.status === 200) { // Changed from response.code
    const data = response.data; // Changed from response.data.data
    $('#totalEarnings').text(`$${data.totalEarnings.toFixed(2)}`);
    $('#activeOrders').text(data.activeOrders);
    $('#avgRating').text(data.avgRating.toFixed(1));
}
},
    error: handleError
});
}

    // Services Management
    function loadServices() {
    $.ajax({
    url: 'http://localhost:8080/api/v1/services',
    method: 'GET',
    success: function(response) {
    if (response.status === 200) { // Changed from response.code
    renderServices(response.data); // response.data is the services array
}
},
    error: handleError
});
}

    function renderServices(services) {
    let html = '';
    services.forEach(service => {
    html += `
          <div class="col-md-4">
            <div class="dashboard-card">
              <h5>${service.title}</h5>
              <p class="text-muted">${service.category}</p>
              <div class="d-flex justify-content-between">
                <span>$${service.price}</span>
                <div>
                  <button class="btn btn-sm btn-outline-primary edit-service" data-id="${service.id}">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger delete-service" data-id="${service.id}">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>`;
});
    $('#servicesContainer').html(html);
}

    // Update in the modal shown event
    $('#editProfileModal').on('show.bs.modal', function() {
    $.ajax({
    url: 'http://localhost:8080/api/v1/sellers/me',
    method: 'GET',
    success: function(response) {
    if (response.status === 200) { // Changed from response.code
    const profile = response.data; // Changed from response.data
    $('#editName').val(profile.name);
    $('#editEmail').val(profile.email);
    $('#editPhone').val(profile.phone || '');
    $('#editAddress').val(profile.address || '');
    $('#editBio').val(profile.bio || '');
    $('#editSkillIds').val(profile.skillIds?.join(', ') || '');
}
},
    error: handleError
});
});

// Update in the form submission
    $('#editProfileForm').submit(function(e) {
    e.preventDefault();
    const updatedData = {
    name: $('#editName').val(),
    email: $('#editEmail').val(),
    phone: $('#editPhone').val() || '', // Handle empty phone
    address: $('#editAddress').val() || '', // Handle empty address
    bio: $('#editBio').val() || '',
    skillIds: $('#editSkillIds').val()
    .split(',')
    .map(id => parseInt(id.trim()))
    .filter(id => !isNaN(id))
};

    // Add validation for skill IDs
    if (updatedData.skillIds.length === 0) {
    alert('Please enter valid skill IDs separated by commas');
    return;
}

    $.ajax({
    url: 'http://localhost:8080/api/v1/sellers/me',
    method: 'PUT',
    contentType: 'application/json',
    data: JSON.stringify(updatedData),
    success: function(response) {
    if (response.code === 200) {
    loadProfileData();
    $('#editProfileModal').modal('hide');
}
},
    error: handleError
});
});
    // Service Form Handling
    $('#serviceForm').submit(function(e) {
    e.preventDefault();
    const serviceData = {
    title: $('#serviceTitle').val(),
    category: $('#serviceCategory').val(),
    price: parseFloat($('#servicePrice').val()),
    description: $('#serviceDescription').val()
};

    $.ajax({
    url: 'http://localhost:8080/api/v1/services',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(serviceData),
    success: function(response) {
    if (response.code === 201) {
    loadServices();
    $('#addServiceModal').modal('hide');
    this.reset();
}
},
    error: handleError
});
});

    // Order Management
    function loadOrders() {
    $.ajax({
    url: 'http://localhost:8080/api/v1/orders',
    method: 'GET',
    success: function(response) {
    if (response.status === 200) {
    renderOrders(response.data);
}
},
    error: handleError
});
}

    function renderOrders(orders) {
    let html = '';
    orders.forEach(order => {
    html += `
          <tr>
            <td>#${order.id}</td>
            <td>${order.service}</td>
            <td>$${order.price}</td>
            <td>
              <span class="status-badge ${order.statusClass}">
                ${order.status}
              </span>
            </td>
            <td>
              <button class="btn btn-sm btn-outline-success deliver-order" data-id="${order.id}">
                Deliver
              </button>
            </td>
          </tr>`;
});
    $('#ordersTable').html(html);
}

    // Profile Management
    // Update loadProfileData function
    function loadProfileData() {
    $.ajax({
    url: 'http://localhost:8080/api/v1/sellers/me',
    method: 'GET',
    success: function(response) {
    console.log('API Response:', response);
    if (response.status === 200) { // Changed from response.code
    const profile = response.data; // Changed from response.data.data
    console.log('Profile Data:', profile);

    // Update profile fields

    $('#sellerName').text(profile.name || 'Not provided');
    $('#profileEmail').text(profile.email || 'Not provided');
    $('#profilePhone').text(profile.phone || 'Not provided');
    $('#profileNic').text(profile.nic || 'Not provided');

    // Member Since date formatting
    const memberSince = profile.createdAt
    ? new Date(profile.createdAt).toLocaleDateString()
    : 'N/A';
    $('#memberSince').text(memberSince);

    // Skills display
    let skillsHtml = '';
    if (profile.skillIds && profile.skillIds.length > 0) {
    profile.skillIds.forEach(skillId => {
    skillsHtml += `<span class="badge bg-primary me-1">#${skillId}</span>`;
});
} else {
    skillsHtml = '<span class="text-muted">No skills listed</span>';
}
    $('#profileSkills').html(skillsHtml);

    // Bio
    $('#profileBio').text(profile.bio || 'No bio provided');
}
},
    error: function(xhr) {
    console.error('Profile load error:', xhr);
    handleError(xhr);
}
});
}
    // Chat Functionality
    function sendMessage() {
    const message = $('#chatInput').val().trim();
    if (message) {
    $.ajax({
    url: 'http://localhost:8080/api/v1/chat',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ message: message }),
    success: function() {
    $('.chat-messages').append(`
              <div class="message-bubble message-right">${message}</div>
            `);
    $('#chatInput').val('');
    $('.chat-messages').scrollTop($('.chat-messages')[0].scrollHeight);
},
    error: handleError
});
}
}

    // Error Handling
    function handleError(xhr) {
    const error = xhr.responseJSON;
    switch (xhr.status) {
    case 401:
    localStorage.clear();
    window.location.href = '../index.html';
    break;
    case 403:
    alert('You dont have permission for this action');
    break;
    default:
    alert(error?.message || 'An error occurred');
}
}

    // Event Listeners
    $('.chat-toggle').click(() => $('.chat-box').toggleClass('active'));
    $('#sendMessage').click(sendMessage);
    $('#chatInput').keypress(e => e.which === 13 && sendMessage());

    $(document).on('click', '.deliver-order', function() {
    const orderId = $(this).data('id');
    $.ajax({
    url: `http://localhost:8080/api/v1/orders/${orderId}/deliver`,
    method: 'PUT',
    success: function(response) {
    if (response.code === 200) loadOrders();
},
    error: handleError
});
});

    $(document).on('click', '.delete-service', function() {
    const serviceId = $(this).data('id');
    if (confirm('Are you sure you want to delete this service?')) {
    $.ajax({
    url: `http://localhost:8080/api/v1/services/${serviceId}`,
    method: 'DELETE',
    success: function(response) {
    if (response.code === 200) loadServices();
},
    error: handleError
});
}
});

    // Logout Functionality
    $('#logoutButton').click(() => {
    localStorage.clear();
    window.location.href = '../index.html';
});
});

    // Logout Functionality
    $('#logoutButton').click(function() {
    // Clear local storage
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('currentUser');

    // Redirect to login page
    window.location.href = '../index.html';
});
