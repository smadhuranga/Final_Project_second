
    $(document).ready(function () {
    // JWT Security Configuration
    $.ajaxSetup({
        beforeSend: function(xhr) {
            const token = localStorage.getItem('jwtToken');
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        }
    });

    // Session Management
    function checkAuthStatus() {
    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) {
    logout();
    return false;
}

    // Existing token expiration check...
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


    // Initialize DataTables
    let userTables = {
    customers: null,
    sellers: null,
    coordinators: null
};



    function getColumnsConfig(tableType) {
    const baseColumns = [
{ data: 'name' },
{ data: 'email' },
{ data: 'phone' }
    ];

    if (tableType === 'sellers' || tableType === 'coordinators') {
    baseColumns.push({ data: 'role' });
}

    baseColumns.push({
    data: null,
    render: function(data, type, row) {
    return `
                        <button class="btn btn-admin btn-delete me-2" data-id="${row.id}">
                            <i class="fas fa-trash"></i>
                        </button>
                        ${tableType === 'sellers' ? `
                            <button class="btn btn-admin btn-edit me-2" onclick="showSellerHistory(${row.id})">
                                <i class="fas fa-history"></i>
                            </button>
                        ` : ''}
                        <button class="btn btn-admin btn-edit" onclick="openChat('${tableType}', ${row.id})">
                            <i class="fas fa-comment"></i>
                        </button>
                    `;
}
});

    return baseColumns;
}

    // Handle user type selection
    $('#userType').change(function() {
    const selectedType = $(this).val();
    $('.user-table').addClass('d-none');
    $(`#${selectedType}Table`).removeClass('d-none');

    // Reload table data if needed
    if (userTables[selectedType]) {
    userTables[selectedType].ajax.reload();
}
});

    // Section switching
    $('.nav-link').click(function (e) {
    e.preventDefault();
    $('.content-section').addClass('d-none');
    const target = $(this).attr('href');
    $(target).removeClass('d-none');
    $('.nav-link').removeClass('active');
    $(this).addClass('active');

    if (target === '#dashboard') {
    initializeCharts();
}
});

    // Chart initialization
    let earningsChart, projectStatsChart;

    function initializeCharts() {
    if (earningsChart) earningsChart.destroy();
    if (projectStatsChart) projectStatsChart.destroy();

    // Earnings Chart
    earningsChart = new Chart(document.getElementById('earningsChart'), {
    type: 'line',
    data: {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [{
    label: 'Monthly Earnings',
    data: [6500, 5900, 8000, 8100, 5600, 5500],
    borderColor: '#FFD700',
    tension: 0.4
}]
}
});

    // Project Stats Chart
    projectStatsChart = new Chart(document.getElementById('projectStats'), {
    type: 'bar',
    data: {
    labels: ['Ongoing', 'Completed', 'Pending'],
    datasets: [{
    label: 'Project Status',
    data: [45, 32, 12],
    backgroundColor: ['#FFD700', '#00FF00', '#ff4444']
}]
}
});
}

    // Delete handler
    // Update the delete button click handler
    // Update the delete handler
    $(document).on('click', '.btn-delete', function() {
    const $row = $(this).closest('tr');
    const userEmail = $row.find('td:eq(2)').text().trim(); // Email is in 3rd column (index 2)

    if (!userEmail) {
    showAlert('Invalid user email', 'danger');
    return;
}

    if (confirm("Are you sure you want to delete this user?")) {
    $.ajax({
    url: `http://localhost:8080/api/v1/admin/users/by-email/${encodeURIComponent(userEmail)}`,
    method: 'DELETE',
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization',
    'Bearer ' + localStorage.getItem('jwtToken'));
},
    success: function() {
    $('#usersTable').DataTable().ajax.reload();
    showAlert('User deleted successfully', 'success');
},
    error: function(xhr) {
    const errorMsg = xhr.responseJSON?.message || 'Error deleting user';
    showAlert(errorMsg, 'danger');
}
});
}
});

    // Save user
    $('#saveUser').click(function () {
    const formData = {
    name: $('#userName').val(),
    email: $('#userEmail').val(),
    status: $('#userStatus').val(),
    type: $('#userType').val()
};

    $.ajax({
    url: '/api/users',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(formData),
    success: function (response) {
    $('#userModal').modal('hide');
    $('#userForm')[0].reset();
    showAlert('User saved successfully!', 'success');
    userTables[formData.type].ajax.reload();
},
    error: function (xhr) {
    showAlert(xhr.responseJSON?.message || 'Error saving user', 'danger');
}
});
});

    // Initialize the dashboard
    initializeUserTables();
    initializeCharts();

    // Utility Functions

    window.showAlert = function(message, type = 'success') {
    const alert = $(`
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `);
    $('.admin-main').prepend(alert);
    setTimeout(() => alert.alert('close'), 3000);
};

    window.showSellerHistory = function(sellerId) {
    $.ajax({
    url: `/api/sellers/${sellerId}/history`,
    method: 'GET',
    success: (data) => {
    $('#historyContent').html(
    data.map(history => `
                            <tr>
                                <td>${new Date(history.date).toLocaleDateString()}</td>
                                <td>$${history.amount.toFixed(2)}</td>
                                <td>${history.project}</td>
                            </tr>
                        `).join('')
    );
    $('#historyModal').modal('show');
}
});
}

    window.openChat = function(userType, userId) {
    // Implement chat functionality
    showAlert(`Chat with ${userType} #${userId} initialized`, 'info');
}
});

    // Chat functionality
    let currentChatUser = null;

    function loadChatHistory(userType, userId) {
    $.ajax({
        url: `/api/chats/${userId}`,
        method: 'GET',
        success: (messages) => {
            const chatContainer = $('#chatMessages');
            chatContainer.empty();

            messages.forEach(msg => {
                const messageClass = msg.sender === 'admin' ? 'sent' : 'received';
                chatContainer.append(`
                        <div class="message ${messageClass}">
                            <div class="message-text">${msg.text}</div>
                            <div class="message-time">
                                ${new Date(msg.timestamp).toLocaleString()}
                            </div>
                        </div>
                    `);
            });
            chatContainer.scrollTop(chatContainer[0].scrollHeight);
        }
    });
}

    window.openChat = function(userType, userId) {
    currentChatUser = { type: userType, id: userId };
    $('#chatTitle').text(`Chat with ${userType} #${userId}`);
    loadChatHistory(userType, userId);
    $('#chatModal').modal('show');
}

    $('#sendMessage').click(function() {
    const message = $('#messageInput').val();
    if (!message) return;

    $.ajax({
    url: '/api/chats/send',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
    recipientType: currentChatUser.type,
    recipientId: currentChatUser.id,
    message: message
}),
    success: () => {
    $('#messageInput').val('');
    loadChatHistory(currentChatUser.type, currentChatUser.id);
}
});
});

    // Enhanced User Management
    // User Management Section
    let usersTable = null;
    let currentUserType = 'CUSTOMER';
    let allUsers = []; // Store all users

    // Fetch all users once
    function fetchAllUsers() {
    $.ajax({
        url: 'http://localhost:8080/api/v1/admin/users',
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization',
                'Bearer ' + localStorage.getItem('jwtToken'));
        },
        success: function(response) {
            allUsers = response.data;
            initializeUserTables(currentUserType);
        }
    });
}

    function initializeUserTables(userType) {
    // Cleanup existing table
    if ($.fn.DataTable.isDataTable('#usersTable')) {
    usersTable.destroy();
    $('#usersTable').empty();
}

    // Filter users by type
    const filteredUsers = allUsers.filter(user => user.type === userType);

    // Initialize new table
    usersTable = $('#usersTable').DataTable({
    data: filteredUsers,
    columns: [
{
    data: 'avatar',
    render: (data) => `<img src="${data || '../images/default-avatar.jpg'}"
                                     class="user-avatar">`
},
{ data: 'name' },
{ data: 'email' },
{ data: 'phone' },
{
    data: 'status',
    render: (data) => `
                    <label class="switch status-toggle">
                        <input type="checkbox" ${data === 'ACTIVE' ? 'checked' : ''}>
                        <span class="slider"></span>
                    </label>`
},
{
    data: 'createdAt',
    render: (data) => new Date(data).toLocaleDateString()
},
{
    data: 'lastLogin',
    render: (data) => data ? new Date(data).toLocaleString() : 'Never'
},
{
    data: null,
    render: (data, type, row) => `
                    <button class="btn btn-admin btn-delete me-2"
                            data-email="${row.email}">
                        <i class="fas fa-trash"></i>
                    </button>
                    <button class="btn btn-admin btn-edit"
                            onclick="openChat('${row.type}', '${row.email}')">
                        <i class="fas fa-comment"></i>
                    </button>`
}
    ],
    destroy: true // Important for reinitialization
});
}

    // Handle type switching
    $('[data-type]').click(function() {
    currentUserType = $(this).data('type');
    $('[data-type]').removeClass('active');
    $(this).addClass('active');
    initializeUserTables(currentUserType);
});

    // Initial load
    $(document).ready(function() {
    fetchAllUsers(); // Fetch all users first
});

    // Search functionality
    $('#userSearch').on('input', function() {
    usersTable.search(this.value).draw();
});
    // Handle status toggle
    $(document).on('change', '.status-toggle input', function() {
    const userId = $(this).data('id');
    const newStatus = $(this).prop('checked') ? 'active' : 'inactive';

    $.ajax({
    url: `/api/users/${userId}/status`,
    method: 'PUT',
    contentType: 'application/json',
    data: JSON.stringify({ status: newStatus }),
    success: () => showAlert('Status updated successfully', 'success')
});
});

    // Search functionality
    $('#userSearch').on('input', function() {
    const table = $('#usersTable').DataTable();
    table.search(this.value).draw();
});



    // Add to the existing JavaScript

    let servicesTable, categoriesTable;
    let categoryMap = {};
    let sellerMap = {};

    function initializeServiceTables() {
    // Fetch categories and sellers first
    Promise.all([
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/categories',
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
            }
        }),
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/users?type=SELLER',
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
            }
        })
    ]).then(([categoriesResponse, sellersResponse]) => {
        // Create category mapping
        categoryMap = {};
        categoriesResponse.data.forEach(category => {
            categoryMap[category.id] = category.name;
        });

        // Create seller mapping
        sellerMap = {};
        sellersResponse.data.forEach(seller => {
            sellerMap[seller.id] = seller.name;
        });

        // Initialize Services Table
        servicesTable = $('#servicesTable').DataTable({
            ajax: {
                url: 'http://localhost:8080/api/v1/admin/services',
                dataSrc: 'data',
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
                }
            },
            columns: [
                { data: 'title' },
                {
                    data: 'categoryId',
                    render: function(data) {
                        return data ? (categoryMap[data] || 'N/A') : 'N/A';
                    }
                },
                {
                    data: 'price',
                    render: function(data) {
                        return data ? '$' + data.toFixed(2) : 'N/A';
                    }
                },
                { data: 'deliveryTime' },
                {
                    data: 'sellerId',
                    render: function(data) {
                        return data ? (sellerMap[data] || 'N/A') : 'N/A';
                    }
                },
                {
                    data: null,
                    render: function(data, type, row) {
                        return `
                        <button class="btn btn-admin btn-edit me-2" onclick="editService(${row.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-admin btn-delete" onclick="deleteService(${row.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    `;
                    }
                }
            ]
        });

        // Initialize Categories Table
        categoriesTable = $('#categoriesTable').DataTable({
            ajax: {
                url: 'http://localhost:8080/api/v1/admin/categories',
                dataSrc: 'data',
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
                }
            },
            columns: [
                { data: 'name' },
                { data: 'description' },
                {
                    data: null,
                    render: function(data, type, row) {
                        return `
                        <button class="btn btn-admin btn-edit me-2" onclick="editCategory(${row.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-admin btn-delete" onclick="deleteCategory(${row.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    `;
                    }
                }
            ]
        });

    }).catch(error => {
        console.error('Error initializing service tables:', error);
        showAlert('Failed to load service data', 'danger');
    });
}
    function logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('user');
    window.location.href = '../index.html'; // Adjust path as needed
}

    // Add click handler for logout button
    $(document).on('click', '#logoutButton', function(e) {
    e.preventDefault();
    logout();
});


    async function getSellerName(sellerId) {
    try {
    const response = await $.ajax({
    url: `http://localhost:8080/api/v1/admin/users/${sellerId}`,
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
}
});
    return response.data.name;
} catch (error) {
    return 'N/A';
}
}

    // Service CRUD operations
    function editService(id) {
    $.ajax({
        url: `http://localhost:8080/api/v1/admin/services/${id}`,
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
        },
        success: function(service) {
            // Load dropdown data first
            loadServiceModalData().then(() => {
                // Set form values after dropdowns are populated
                $('#serviceId').val(service.id);
                $('#serviceTitle').val(service.title);
                $('#serviceDescription').val(service.description);
                $('#servicePrice').val(service.price);
                $('#serviceDeliveryTime').val(service.deliveryTime);
                $('#serviceCategory').val(service.categoryId);
                $('#serviceSeller').val(service.sellerId);

                $('#serviceModal').modal('show');
            });
        }
    });
}

    function deleteService(id) {
    if (confirm('Are you sure you want to delete this service?')) {
    $.ajax({
    url: `http://localhost:8080/api/v1/admin/services/${id}`,
    method: 'DELETE',
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
},
    success: function() {
    servicesTable.ajax.reload();
    showAlert('Service deleted successfully', 'success');
}
});
}
}

    // Modify the saveService click handler
    $('#saveService').click(function() {
    // Clear previous errors
    $('.is-invalid').removeClass('is-invalid');

    // Validate inputs
    const serviceData = {
    id: $('#serviceId').val(),
    title: $('#serviceTitle').val().trim(),
    description: $('#serviceDescription').val().trim(),
    price: $('#servicePrice').val(),
    deliveryTime: $('#serviceDeliveryTime').val().trim(),
    categoryId: $('#serviceCategory').val(),
    sellerId: $('#serviceSeller').val()
};

    // Enhanced validation
    let isValid = true;

    if (!serviceData.title) {
    $('#serviceTitle').addClass('is-invalid');
    isValid = false;
}

    if (!serviceData.price || isNaN(serviceData.price) || serviceData.price <= 0) {
    $('#servicePrice').addClass('is-invalid');
    isValid = false;
}

    if (!serviceData.deliveryTime) {
    $('#serviceDeliveryTime').addClass('is-invalid');
    isValid = false;
}

    if (!serviceData.categoryId) {
    $('#serviceCategory').addClass('is-invalid');
    isValid = false;
}

    if (!serviceData.sellerId) {
    $('#serviceSeller').addClass('is-invalid');
    isValid = false;
}

    if (!isValid) {
    showAlert('Please fill all required fields correctly', 'danger');
    return;
}

    // Convert numeric fields
    serviceData.price = parseFloat(serviceData.price);
    serviceData.categoryId = parseInt(serviceData.categoryId);
    serviceData.sellerId = parseInt(serviceData.sellerId);

    const method = serviceData.id ? 'PUT' : 'POST';
    const url = serviceData.id
    ? `http://localhost:8080/api/v1/admin/services/${serviceData.id}`
    : 'http://localhost:8080/api/v1/admin/services';

    $.ajax({
    url: url,
    method: method,
    contentType: 'application/json',
    data: JSON.stringify(serviceData),
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
},
    success: function() {
    $('#serviceModal').modal('hide');
    servicesTable.ajax.reload();
    showAlert('Service saved successfully', 'success');
},
    error: function(xhr) {
    const response = xhr.responseJSON;
    let errorMessage = 'Failed to save service';

    if (xhr.status === 400) {
    if (response && response.message) {
    errorMessage = response.message;
    // Handle duplicate title error
    if (errorMessage.toLowerCase().includes('title')) {
    $('#serviceTitle').addClass('is-invalid');
}
}
    else if (response && response.errors) {
    errorMessage = response.errors.join(', ');
}
}
    showAlert(errorMessage, 'danger');
}
});
});
    // Category CRUD operations
    function editCategory(id) {
    $.ajax({
        url: `http://localhost:8080/api/v1/admin/categories/${id}`,
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
        },
        success: function(category) {
            $('#categoryId').val(category.id);
            $('#categoryName').val(category.name);
            $('#categoryDescription').val(category.description);
            $('#categoryModal').modal('show');
        }
    });
}

    function deleteCategory(id) {
    if (confirm('Are you sure you want to delete this category?')) {
    $.ajax({
    url: `http://localhost:8080/api/v1/admin/categories/${id}`,
    method: 'DELETE',
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
},
    success: function() {
    categoriesTable.ajax.reload();
    showAlert('Category deleted successfully', 'success');
}
});
}
}

    $('#saveCategory').click(function() {
    const categoryData = {
    id: $('#categoryId').val(),
    name: $('#categoryName').val(),
    description: $('#categoryDescription').val()
};

    const method = categoryData.id ? 'PUT' : 'POST';
    const url = categoryData.id
    ? `http://localhost:8080/api/v1/admin/categories/${categoryData.id}`
    : 'http://localhost:8080/api/v1/admin/categories';

    $.ajax({
    url: url,
    method: method,
    contentType: 'application/json',
    data: JSON.stringify(categoryData),
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
},
    success: function() {
    $('#categoryModal').modal('hide');
    categoriesTable.ajax.reload();
    showAlert('Category saved successfully', 'success');
}
});
});
    // Add this function to load dropdown data

    // Add this event listener for modal show
    $('#serviceModal').on('show.bs.modal', function() {
    // Load dropdown data first
    loadServiceModalData().then(() => {
        if (!$('#serviceId').val()) {
            $('#serviceForm')[0].reset();
            $('#serviceCategory, #serviceSeller').val('');
        }
    }).catch(error => {
        console.error('Error loading modal data:', error);
        showAlert('Failed to load required data', 'danger');
        $('#serviceModal').modal('hide');
    });
});

    // Modified loadServiceModalData with Promise
    // Modify loadServiceModalData function
    function loadServiceModalData() {
    return new Promise((resolve, reject) => {
    Promise.all([
    $.ajax({
    url: 'http://localhost:8080/api/v1/admin/categories',
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
}
}),
    $.ajax({
    url: 'http://localhost:8080/api/v1/admin/users?type=SELLER',
    beforeSend: function(xhr) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
}
})
    ]).then(([categories, sellers]) => {
    // Populate categories
    $('#serviceCategory').empty().append(
    '<option value="">Select Category</option>' +
    categories.data.map(c => `<option value="${c.id}">${c.name}</option>`).join('')
    );

    // Populate sellers
    $('#serviceSeller').empty().append(
    '<option value="">Select Seller</option>' +
    sellers.data.filter(u => u.type === 'SELLER')
    .map(s => `<option value="${s.id}">${s.name}</option>`).join('')
    );
    resolve();
}).catch(error => {
    showAlert('Failed to load required data', 'danger');
    reject(error);
});
});
}

    // ================== EDIT SERVICE ==================
    window.editService = function(id) {
    $.ajax({
        url: `http://localhost:8080/api/v1/admin/services/${id}`,
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwtToken'));
        },
        success: function(service) {
            loadServiceModalData().then(() => {
                $('#serviceId').val(service.id);
                $('#serviceTitle').val(service.title);
                $('#serviceDescription').val(service.description);
                $('#servicePrice').val(service.price);
                $('#serviceDeliveryTime').val(service.deliveryTime);
                $('#serviceCategory').val(service.categoryId);
                $('#serviceSeller').val(service.sellerId);
                $('#serviceModal').modal('show');
            });
        }
    });
}



    // Initialize service tables when the section is shown
    $('a[href="#services"]').click(function() {
    initializeServiceTables();
});
