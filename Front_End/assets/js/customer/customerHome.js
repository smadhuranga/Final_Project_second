
    // auth.js - Consolidated Security & Functionality

    // Global JWT Configuration
    $.ajaxSetup({
    beforeSend: function(xhr) {
    const token = localStorage.getItem('jwtToken');
    console.log(token)
    if (token) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
}
}
});

    // Session Management
    function checkAuthStatus() {
    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) {
    window.location.href = '../index.html';
    return false;
}

    // Verify token is not expired
    const tokenPayload = JSON.parse(atob(jwtToken.split('.')[1]));
    if (tokenPayload.exp * 1000 < Date.now()) {
    logout();
    return false;
}

    return true;
}


    // Token Validation
    function validateToken() {
    if (!checkAuthStatus()) return;

    $.ajax({
    url: 'http://localhost:8080/api/v1/auth/validate-token',
    method: 'GET',
    success: function(response) {
    const storedUser = JSON.parse(localStorage.getItem('user'));
    console.log('Stored user:', storedUser);
    console.log('Response:', response);

    // Check if response has user data in different possible locations
    const userData = response.user || response.data || response;

    if (!storedUser || (userData.email && storedUser.email !== userData.email)) {
    localStorage.setItem('user', JSON.stringify(userData));
}
},
    error: function(xhr) {
    if (xhr.status === 401){
    logout();
}

}
});
}

    // Error Handling
    $(document).ajaxError(function(event, jqXHR) {
    if (jqXHR.status === 401) logout();
});

    // Logout Function
    function logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('user');
    window.location.href = '../index.html';
}

    // Profile Management
    function loadProfileData() {
    if (!checkAuthStatus()) {
    console.log('Not authenticated');
    return;
}

    $.ajax({
    url: 'http://localhost:8080/api/v1/customers/me',
    method: 'GET',
    headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
},
    success: function(response) {
    console.log('Profile data received:', response);
    if (!response) {
    showAlert('Received empty profile data', 'warning');
    return;
}
    updateProfileUI(response);
    populateEditForm(response);
},
    error: function(xhr) {
    console.error('Profile load error:', xhr);
    const message = xhr.responseJSON?.message ||
    xhr.statusText ||
    'Error loading profile';
    showAlert(message, 'danger');

    // If unauthorized, force logout
    if (xhr.status === 401) {
    logout();
}
}
});
}

    function updateProfileUI(user) {
    // Safe data access with fallbacks
    console.log('Received user data:', user);
    $('#profileName').text(user?.data.name || 'Not provided');
    $('#profileEmail').text(user?.data.email || 'Not provided');
    $('#phoneNumber').text(user?.data.phone || 'Not provided');
    $('#profileAddress').text(user?.data.address || 'Not provided');
    $('#profileAvatar').attr('src', user?.data.profileImage || 'default-avatar.png');
    console.log(user.data.profileImage)
    $('#registrationDate').text(
    user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'Unknown'
    );
    $('#accountType').text(user?.type || 'Customer');
    $('#emailVerified').text(user?.emailVerified ? 'Verified' : 'Not Verified');
    $('#completedOrders').text(user?.orderIds?.length || 0);
}

    function populateEditForm(user) {
    $('#editName').val(user.data.name);
    $('#editEmail').val(user.data.email);
    $('#editPhone').val(user.data.phone);
    $('#editAddress').val(user.data.address);
}

    // Update the file input change handler
    document.getElementById('profileImageInput').addEventListener('change', function(e) {
    if (this.files && this.files[0]) {
    // Immediately upload when file is selected
    uploadProfileImage();
}
});

    // Keep your existing uploadProfileImage function but add visual feedback
    function uploadProfileImage() {
    const fileInput = document.getElementById('profileImageInput');
    const file = fileInput.files[0];

    if (!file) {
    showAlert('Please select an image file', 'warning');
    return;
}

    // Add loading state
    const cameraBtn = document.querySelector('.fa-camera').parentElement;
    cameraBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

    const formData = new FormData();
    formData.append('file', file);

    $.ajax({
    url: 'http://localhost:8080/api/v1/customers/upload-profile-image',
    method: 'POST',
    data: formData,
    contentType: false,
    processData: false,
    headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
},
    success: function(response) {
    if (response.data) {
    $('#profileAvatar').attr('src', response.data);
    showAlert('Profile image updated successfully', 'success');
    // Update the user object in localStorage if needed
    const user = JSON.parse(localStorage.getItem('user'));
    if (user) {
    user.profileImage = response.data;
    localStorage.setItem('user', JSON.stringify(user));
}
}
    cameraBtn.innerHTML = '<i class="fas fa-camera"></i>';
},
    error: function(xhr) {
    showAlert(xhr.responseJSON?.message || 'Error uploading image', 'danger');
    cameraBtn.innerHTML = '<i class="fas fa-camera"></i>';
}
});
}
    // Form Handlers
    function handleProfileUpdate(formData) {
    $.ajax({
        url: 'http://localhost:8080/api/v1/customers/update',
        method: 'PUT',
        contentType: 'application/json',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        data: JSON.stringify(formData),
        success: function(response) {
            console.log('Update success:', response);
            loadProfileData();
            $('#editProfileModal').modal('hide');
            showAlert('Profile updated successfully!', 'success');
        },
        error: function(xhr) {
            console.error('Update error:', xhr);
            const errorMsg = xhr.responseJSON?.message || 'Error updating profile';
            showAlert(errorMsg, 'danger');


        }
    });
}

    function handlePasswordChange(passwordData) {
    if (passwordData.newPassword !== passwordData.confirmPassword) {
    showAlert('Passwords do not match!', 'danger');
    return;
}

    $.ajax({
    url: 'http://localhost:8080/api/v1/customers/change-password',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
    currentPassword: passwordData.currentPassword,
    newPassword: passwordData.newPassword
}),
    success: function() {
    $('#changePasswordModal').modal('hide');
    $('#changePasswordForm')[0].reset();
    showAlert('Password changed successfully!', 'success');
},
    error: (xhr) => showAlert(xhr.responseJSON?.message || 'Error changing password', 'danger')
});
}
    // UI Helpers
    function showAlert(message, type) {
    const alert = $(`
        <div class="alert alert-${type} alert-dismissible fade show position-fixed top-0 end-0 m-3">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `);
    $('body').append(alert);
    setTimeout(() => alert.alert('close'), 3000);
}

    function calculatePasswordStrength(password) {
    let strength = 0;
    if (password.length >= 8) strength += 25;
    if (/[A-Z]/.test(password)) strength += 25;
    if (/[0-9]/.test(password)) strength += 25;
    if (/[^A-Za-z0-9]/.test(password)) strength += 25;
    return Math.min(strength, 100);
}

    // Navigation & UI
    function showSection(sectionId) {
    if (!checkAuthStatus()) return;

    document.querySelectorAll('.section').forEach(section => {
    section.classList.remove('active');
});

    setTimeout(() => {
    const section = document.getElementById(sectionId);
    if (section) {
    section.classList.add('active');
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
}, 50);

    const navbarCollapse = document.getElementById('navbarNav');
    if (navbarCollapse?.classList.contains('show')) {
    new bootstrap.Collapse(navbarCollapse).hide();
}
}

    function initializeScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
    if (entry.isIntersecting) {
    entry.target.classList.add('animated');
}
});
}, { threshold: 0.1 });

    document.querySelectorAll('.animate-on-scroll').forEach(el => observer.observe(el));
}

    // Document Ready
    $(document).ready(function() {
    validateToken();
    initializeScrollAnimations();

    // Password Strength Indicator
    $('#newPassword').on('input', function() {
    const strength = calculatePasswordStrength($(this).val());
    $('.progress-bar').css('width', strength + '%');
});

    // Form Submissions
    $('#editProfileForm').submit(function(e) {
    e.preventDefault();
    handleProfileUpdate({
    name: $('#editName').val(),
    email: $('#editEmail').val(),
    phone: $('#editPhone').val(),
    address: $('#editAddress').val()
});
});


    $('#changePasswordForm').submit(function(e) {
    e.preventDefault();
    handlePasswordChange({
    currentPassword: $('#currentPassword').val(),
    newPassword: $('#newPassword').val(),
    confirmPassword: $('#confirmPassword').val()
});
});

    // Search Functionality
    $('#searchInput').on('input', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    document.querySelectorAll('.service-card').forEach(card => {
    card.style.display = card.textContent.toLowerCase().includes(searchTerm) ? 'block' : 'none';
});
});

    // Contact Form
    $('#contactForm').submit(function(e) {
    e.preventDefault();
    showAlert('Thank you for your message! We will respond shortly.', 'success');
    this.reset();
});

    // Navbar Scroll Effect
    window.addEventListener('scroll', () => {
    document.querySelector('.navbar').classList.toggle('navbar-scrolled', window.scrollY > 50);
});

    loadProfileData();
});
