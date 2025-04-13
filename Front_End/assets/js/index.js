


    window.addEventListener('load', () => {
    document.querySelector('.loading-overlay').classList.add('hidden');
});

    // Handle AJAX loading states
    $(document).ajaxStart(function() {
    document.querySelector('.loading-overlay').classList.remove('hidden');
});

    $(document).ajaxStop(function() {
    document.querySelector('.loading-overlay').classList.add('hidden');
});

    // Handle page transitions
    function navigateTo(url) {
    document.querySelector('.loading-overlay').classList.remove('hidden');
    window.location.href = url;
}

    // Initialize on DOM ready
    $(document).ready(function() {
    // Check for existing JWT token
    const jwtToken = localStorage.getItem('jwtToken');
    if(jwtToken) {
    // window.location.href = 'index.html';
}

    // Show login form by default
    showForm('loginForm');
    $('.seller-fields').hide();

    // Configure AJAX to include JWT token in headers
    $.ajaxSetup({
    beforeSend: function(xhr) {
    const token = localStorage.getItem('jwtToken');
    if (token) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
}
}

});

    // Handle global AJAX errors
    $(document).ajaxError(function(event, jqXHR) {
    if (jqXHR.status === 401) { // Unauthorized
    localStorage.removeItem('jwtToken');
    showForm('loginForm');
    alert('Session expired. Please login again.');
}
});
// Login handler - add error clearing
    $('#loginEmail, #loginPassword').on('input', function() {
    $(this).removeClass('error-input');
});


//
//             // Update login handler
    $('#loginForm form').submit(function(e) {
    e.preventDefault();
    const $form = $(this);
    const $btn = $form.find('button').prop('disabled', true);

    // Clear previous errors
    $form.find('.error-text').remove();

    $.ajax({
    type: "POST",
    url: "http://localhost:8080/api/v1/auth/login",
    contentType: "application/json",
    data: JSON.stringify({
    email: $('#loginEmail').val().trim(),
    password: $('#loginPassword').val()
}),
    success: function(response) {
    // Validate response structure
    if (!response?.data?.token) {
    throw new Error('Invalid server response');
}

    // Store authentication data
    localStorage.setItem('jwtToken', response.data.token);
    localStorage.setItem('user', JSON.stringify({
    email: response.data.email,
    type: response.data.userType,
    profileImage: response.data.profileImage
}));


    // Redirect with token verification

    // Verify token before redirect
    validateTokenAfterLogin();
    switch (response.data.userType.toUpperCase()) {
    case 'CUSTOMER':
    window.location.href = './pages/home.html';
    break;
    case 'SELLER':
    window.location.href = './pages/sellerHome.html';
    break;
    case 'ADMIN':
    window.location.href = './pages/adminController.html';
    break;
    case 'COORDINATOR':
    window.location.href = './pages/coordinatorController.html';
    break;
    default:
    logout();
    showErrorToast('Unknown user type - contact support');
}

},
    error: function(xhr) {
    $('#loginPassword').val('');
    let errorMsg = xhr.responseJSON?.message || 'Login failed';
    showErrorToast(errorMsg);
},
    complete: function() {
    $btn.prop('disabled', false);
}
});
});




// Add this function to your login page script
    function validateTokenAfterLogin() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
    logout();
    return;
}

    // Split the token into parts
    const tokenParts = token.split('.');
    if (tokenParts.length !== 3) {
    logout();
    showErrorToast('Invalid token format');
    return;
}

    try {
    // Convert base64url to base64 and handle padding
    const base64Url = tokenParts[1];
    const base64 = base64Url
    .replace(/-/g, '+')
    .replace(/_/g, '/');

    // Add padding
    const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');

    const payload = JSON.parse(atob(padded));

    // Check expiration
    if (Date.now() >= payload.exp * 1000) {
    logout();
    showErrorToast('Session expired');
}
} catch (e) {
    console.error('Token validation failed:', e);
    logout();
    showErrorToast('Invalid authentication token');
}
}





    // Registration Form Submission
    $('#registerForm form').submit(function(e) {
    e.preventDefault();
    const isSeller = $('#registerForm .user-type-btn.active').text().includes('Seller');

    // Manual validation for seller
    if(isSeller) {
    if($('#nic').val().trim() === '') {
    showErrorToast('NIC Number is required for sellers');
    return;
}
}

    if (isSeller) handleSellerRegistration();
    else handleCustomerRegistration();

});







    // Forgot Password Form Submission
    // Update the Forgot Password functions

// Keep existing code below unchanged


});




    // Move these OUTSIDE the $(document).ready() block
    let currentResetEmail = null;

    function sendPasswordResetOtp() {
    const email = $('#forgotEmail').val().trim();

    if (!email || !validateEmail(email)) {
    showErrorToast('Please enter a valid email address');
    return;
}

    $.ajax({
    type: "POST",
    url: "http://localhost:8080/api/v1/auth/forgot-password",
    contentType: "application/json",
    data: JSON.stringify({ email: email }),
    success: function(response) {
    // Change from response.code to response.status
    if(response.status === 200) {
    currentResetEmail = email;
    $('#step1').hide();
    $('#step2').show();
}
    showErrorToast(response.message);
},
    error: function(xhr) {
    const errorMsg = xhr.responseJSON?.message || 'Error sending OTP';
    showErrorToast(errorMsg);
    $('#step1').show();
    $('#step2').hide();
}
});
}

    function handlePasswordReset() {
    const otp = $('#resetOtp').val().trim();
    const newPassword = $('#resetPassword').val();

    if (!otp || otp.length !== 4) {
    showErrorToast('Please enter a valid 4-digit OTP');
    return;
}

    if (!newPassword || newPassword.length < 6) {
    showErrorToast('Password must be at least 6 characters');
    return;
}

    const requestData = {
    email: currentResetEmail,
    otp: otp,
    newPassword: newPassword
};

    $.ajax({
    type: "POST",
    url: "http://localhost:8080/api/v1/auth/reset-password",
    contentType: "application/json",
    data: JSON.stringify(requestData),
    success: function(response) {
    if(response.code === 200) {
    // Clear form fields
    $('#forgotEmail, #resetOtp, #resetPassword').val('');
    // Show success message
    alert('Password reset successfully! Please login with your new password');
    // Return to login
    showForm('loginForm');
} else {
    showErrorToast(response.message || 'Password reset failed');
}
},
    error: function(xhr) {
    const error = xhr.responseJSON || {};
    let errorMsg = error.message || 'Password reset failed';

    if(xhr.status === 401) {
    errorMsg = 'Invalid or expired OTP';
} else if(xhr.status === 404) {
    errorMsg = 'User not found';
}

    showErrorToast(errorMsg);
    // Keep step2 visible for corrections
    $('#step2').show();
}
});
}

    function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
}


    // Form visibility control
    function showForm(formId) {
    $('.container').addClass('hidden');
    $('#' + formId).removeClass('hidden');
}







    // User type toggle
    function toggleUserType(element) {
    $('.user-type-btn').removeClass('active');
    $(element).addClass('active');
    const isSeller = $(element).text().includes('Seller');

    // Toggle seller fields and required attributes
    $('.seller-fields').toggle(isSeller);
    $('#nic').prop('required', isSeller); // Dynamic required attribute

    // Update name field label
    const nameInput = $('#regName');
    nameInput.attr('placeholder', isSeller ? 'Business Name*' : 'Full Name*');

    // Clear NIC field when switching to customer
    if(!isSeller) $('#nic').val('');
}

    // Seller registration handler
    function handleSellerRegistration() {
    const formData = new FormData();
    const sellerData = {
    name: $('#regName').val().trim(),
    email: $('#regEmail').val().trim(),
    password: $('#regPassword').val(),
    nic: $('#nic').val().trim(),
    phone: $('#regPhone').val().trim(),
    address: $('#regAddress').val().trim(),
    bio: $('#bio').val().trim(),
    qualifications: $('#qualifications').val().split('\n').filter(q => q.trim() !== ''),
    type: 'SELLER' // Add this line to include the user type
};

    // Validate required fields
    if (!sellerData.nic) {
    showErrorToast('NIC Number is required for sellers');
    return;
}

    formData.append('data', new Blob([JSON.stringify(sellerData)], { type: "application/json" }));

    const profileImage = $('#profileImage')[0].files[0];
    if (profileImage) {
    formData.append('profileImage', profileImage);
}

    $.ajax({
    type: "POST",
    url: "http://localhost:8080/api/v1/sellers/register",
    data: formData,
    processData: false,
    contentType: false,
    success: function(response) {
    localStorage.setItem('jwtToken', response.accessToken);
    localStorage.setItem('user', JSON.stringify(response.user));
    window.location.href = 'index.html';
},
    error: function(xhr) {
    const error = xhr.responseJSON || {};
    showErrorToast(error.message || `Registration failed: ${xhr.statusText}`);
}
});
}

    // Customer registration handler
    function handleCustomerRegistration() {
    const formData = new FormData();
    const customerData = {
    name: $('#regName').val(),
    email: $('#regEmail').val(),
    password: $('#regPassword').val(),
    phone: $('#regPhone').val(),
    address: $('#regAddress').val(),
    type: 'CUSTOMER'
};

    formData.append('data', new Blob([JSON.stringify(customerData)], {
    type: "application/json"
}));

    const profileImage = $('#profileImage')[0].files[0];
    if (profileImage) {
    formData.append('profileImage', profileImage);
}

    $.ajax({
    type: "POST",
    url: "http://localhost:8080/api/v1/customers/register",
    data: formData,
    processData: false,
    contentType: false,
    success: function(response) {
    localStorage.setItem('jwtToken', response.accessToken);
    localStorage.setItem('user', JSON.stringify(response.user));
    window.location.href = 'index.html';
},
    error: function(xhr) {
    const error = xhr.responseJSON || {};
    showErrorToast(error.message || `Registration failed: ${xhr.statusText}`);
}
});
}

    // Add this utility function
    function showErrorToast(message) {
    const toast = $(`<div class="error-toast">${message}</div>`);
    $('body').append(toast);
    setTimeout(() => toast.addClass('show'), 10);
    setTimeout(() => toast.remove(), 5000);
}
    function logout() {
    // Clear all authentication-related items
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('user');
    // Redirect to login page
    window.location.href = '/index.html';
}
