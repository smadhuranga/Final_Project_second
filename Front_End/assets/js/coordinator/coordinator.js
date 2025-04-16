
    const API_BASE = 'http://localhost:8080/api/v1/';
    let currentUserType = '';
    let currentUserId = '';

    // Debug setup
    console.debug = function() {
    if (window.console) {
    console.log.apply(console, arguments);
}
};

    // Authentication Check
    function checkAuthStatus() {
    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) redirectToLogin();

    try {
    const tokenPayload = JSON.parse(atob(jwtToken.split('.')[1]));
    if (tokenPayload.exp * 1000 < Date.now()) logout();
} catch (e) {
    logout();
}
}

    function redirectToLogin() {
    window.location.href = '../index.html';
}

    function logout() {
    localStorage.removeItem('jwtToken');
    redirectToLogin();
}

    // AJAX Setup
    $.ajaxSetup({
    headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
},
    error: function(xhr) {
    if (xhr.status === 401) logout();
}
});


    function loadCustomers() {
    $.ajax({
        url: API_BASE + 'admin/users?type=CUSTOMER',
        method: 'GET',
        success: (response) => {
            console.debug('Raw API response:', response);
            if (response.status === 200) {
                // Use email as unique identifier since IDs are missing
                const customersArray = response.data
                    .filter(user => user.type === 'CUSTOMER')
                    .map(user => ({
                        id: user.email, // Use email as temporary ID
                        name: user.name,
                        email: user.email,
                        active: user.isActive || false,
                        type: user.type
                    }));

                console.log("Filtered Customers:", customersArray);
                renderCustomers(customersArray);

                if (customersArray.length === 0) {
                    $('#customersTable').html(
                        `<tr><td colspan="5" class="text-center">
                            No customers found
                        </td></tr>`
                    );
                }
            } else {
                showErrorToast(response.message || 'Failed to load customers');
            }
        },
        error: (xhr) => {
            showErrorToast('Failed to load customers: ' +
                (xhr.responseJSON?.message || xhr.statusText));
        }
    });
}
    function renderCustomers(customers) {
    try {
    const rows = customers.map(customer => `
            <tr class="hover-scale">
                <td>${customer.id}</td>
                <td>${customer.name || 'N/A'}</td>
                <td>${customer.email || 'N/A'}</td>
                <td>${getStatusBadge(customer.active)}</td>
                <td>
                    <button class="btn btn-admin btn-edit me-2"
                       onclick="openChat('${customer.id}', '${customer.name.replace(/'/g, "\\'")}', 'customer')">
                        <i class="fas fa-comment me-2"></i>Chat
                    </button>
                    <button class="btn btn-admin btn-edit me-2"
                        onclick="openCustomerModal('${customer.id}')">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-admin btn-delete"
                        onclick="toggleUserStatus('${customer.id}', 'customer')">
                        <i class="fas fa-ban"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    $('#customersTable').html(rows);
} catch (error) {
    console.error('Rendering error:', error);
    $('#customersTable').html(`
            <tr><td colspan="5" class="text-danger">
                Error loading customer data
            </td></tr>
        `);
}
}
    // Seller Management
    function loadSellers() {
    $.ajax({
        url: API_BASE + 'admin/users?type=SELLER',
        method: 'GET',
        success: (response) => {
            console.debug('Sellers response:', response);
            if (response.status === 200) {
                // Use email as unique key since IDs might be missing
                const sellersArray = response.data
                    .filter(seller => seller.type === 'SELLER')
                    .map(seller => ({
                        id: seller.email,  // Use email as temporary ID
                        name: seller.name,
                        email: seller.email,
                        storeName: seller.storeName || seller.name,
                        active: seller.isActive || false  // Match customer's isActive field
                    }));

                console.log("Processed Sellers:", sellersArray);
                renderSellers(sellersArray);

                if (sellersArray.length === 0) {
                    $('#sellersTable').html(
                        `<tr><td colspan="5" class="text-center">
                            No sellers found
                        </td></tr>`
                    );
                }
            } else {
                showErrorToast(response.message || 'Failed to load sellers');
            }
        },
        error: (xhr) => {
            showErrorToast('Failed to load sellers: ' +
                (xhr.responseJSON?.message || xhr.statusText));
        }
    });
}
    // function loadSellers() {
    //     $.ajax({
    //         url: API_BASE + 'admin/users?type=SELLER',
    //         method: 'GET',
    //         success: (response) => {
    //             console.debug('Sellers response:', response);
    //             if (response.status === 200) {
    //                 // Create a seller map to ensure unique entries
    //                 const sellerMap = response.data.reduce((map, seller) => {
    //                     map[seller.id] = {
    //                         id: seller.id,
    //                         name: seller.name,
    //                         email: seller.email,
    //                         storeName: seller.storeName || seller.name,
    //                         active: seller.active
    //                     };
    //                     return map;
    //                 }, {});
    //
    //                 // Convert map back to array for rendering
    //                 const sellersArray = Object.values(sellerMap);
    //                 renderSellers(sellersArray);
    //             } else {
    //                 showErrorToast(response.message || 'Failed to load sellers');
    //             }
    //         },
    //         error: (xhr) => {
    //             showErrorToast('Failed to load sellers: ' + (xhr.responseJSON?.message || xhr.statusText));
    //         }
    //     });
    // }


    function renderSellers(sellers) {
        try {
            if (!sellers || sellers.length === 0) {
                $('#sellersTable').html(`<tr><td colspan="5" class="text-center">No sellers found</td></tr>`);
                return;
            }

            const rows = sellers.map(seller => `
                <tr class="hover-scale">
                    <td>${seller.id}</td>
                    <td>${(seller.storeName || seller.name).replace(/'/g, "\\'")}</td>
                    <td>${seller.email}</td>
                    <td>${getStatusBadge(seller.active)}</td>
                    <td>
                        <button class="btn btn-admin btn-edit me-2"
                            onclick="openChat('${seller.id}', '${(seller.storeName || seller.name).replace(/'/g, "\\'")}', 'seller')">
                            <i class="fas fa-comment me-2"></i>Chat
                        </button>
                        <button class="btn btn-admin btn-edit me-2"
                            onclick="openSellerModal(${seller.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-admin btn-delete"
                            onclick="toggleUserStatus(${seller.id}, 'seller')">
                            <i class="fas fa-ban"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
            $('#sellersTable').html(rows);
        } catch (error) {
            console.error('Error rendering sellers:', error);
            $('#sellersTable').html(`<tr><td colspan="5" class="text-danger">Error loading data</td></tr>`);
        }
    }
    // Status Management
    function toggleUserStatus(userId, userType) {
    if (!confirm(`Are you sure you want to toggle status for this ${userType}?`)) return;

    $.ajax({
    url: API_BASE + `admin/users/${userId}/status`,
    method: 'PATCH',
    success: () => {
    showSuccessToast('Status updated successfully');
    userType === 'customer' ? loadCustomers() : loadSellers();
},
    error: (xhr) => {
    showErrorToast('Error: ' + xhr.responseJSON?.message);
}
});
}

    // Chat Management
    async function openChat(userId, userName, userType) {
    currentUserId = userId;
    currentUserType = userType;
    $('#chatUserName').text(userName);

    try {
    // Send email notification to the user
    const endpoint = userType === 'seller' ? 'sellers/notify-chat' : 'customers/notify-chat';
    await $.ajax({
    url: API_BASE + endpoint,
    method: 'POST',
    data: { [`${userType}Email`]: userId },
    timeout: 5000
});
    console.debug(`${userType} notification sent successfully`);
} catch (error) {
    console.warn(`${userType} notification failed:`, error);
    showErrorToast(`Could not notify ${userType}, but chat is available`);
}

    loadChatHistory();
    new bootstrap.Modal('#chatModal').show();
}

    function loadChatHistory() {
    $.ajax({
    url: API_BASE + `chats/${currentUserId}?userType=${currentUserType}`,
    method: 'GET',
    success: (response) => {
    if (response.code === 200) renderChatMessages(response.data);
},
    error: (xhr) => {
    showErrorToast('Failed to load chat history');
}
});
}

    function renderChatMessages(messages) {
    const chatHtml = messages.map(msg => `
            <div class="message ${msg.senderType === 'CUSTOMER' ? 'customer-message' : 'seller-message'}">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <div class="message-sender">${msg.senderName}</div>
                    <div class="text-muted small">${new Date(msg.timestamp).toLocaleString()}</div>
                </div>
                <div class="message-text">${msg.content}</div>
            </div>
        `).join('');
    $('#modalChatMessages').html(chatHtml);
    scrollChatBottom();
}

    function scrollChatBottom() {
    const container = document.querySelector('#modalChatMessages');
    container.scrollTop = container.scrollHeight;
}

    // Payment Management
    function loadPayments() {
    $.ajax({
    url: API_BASE + 'payments',
    method: 'GET',
    success: (response) => {
    if (response.code === 200) renderPayments(response.data);
},
    error: (xhr) => {
    showErrorToast('Failed to load payments');
}
});
}

    function renderPayments(payments) {
    const rows = payments.map(payment => `
            <tr class="hover-scale">
                <td>${payment.transactionId}</td>
                <td>${new Date(payment.timestamp).toLocaleDateString()}</td>
                <td>$${payment.amount.toFixed(2)}</td>
                <td>${getPaymentStatusBadge(payment.status)}</td>
                <td>
                    <button class="btn btn-admin btn-edit"
                        onclick="viewReceipt('${payment.id}')">
                        <i class="fas fa-receipt"></i> View
                    </button>
                </td>
            </tr>
        `).join('');
    $('#paymentsTable').html(rows);
}

    // UI Helpers
    function getStatusBadge(active) {
    const status = active ? 'active' : 'blocked';
    return `<span class="status-badge ${status === 'active' ? 'active-status' : 'blocked-status'}">
            ${status}
        </span>`;
}

    function getPaymentStatusBadge(status) {
    const statusMap = {
    'COMPLETED': 'active-status',
    'PENDING': 'text-warning',
    'FAILED': 'blocked-status'
};
    return `<span class="status-badge ${statusMap[status]}">${status.toLowerCase()}</span>`;
}

    function showSuccessToast(message) {
    Toastify({
    text: message,
    duration: 3000,
    style: {
    background: "linear-gradient(to right, #00b09b, #96c93d)",
    borderRadius: "8px"
}
}).showToast();
}

    function showErrorToast(message) {
    Toastify({
    text: message,
    duration: 3000,
    style: {
    background: "linear-gradient(to right, #ff5f6d, #ffc371)",
    borderRadius: "8px"
}
}).showToast();
}

    // Initialization
    $(document).ready(() => {
    checkAuthStatus();
    loadCustomers();
    loadSellers();
    loadPayments();

    // Event Listeners
    $('#logoutBtn').click(logout);

    document.querySelectorAll('.admin-sidebar a').forEach(link => {
    link.addEventListener('click', function(e) {
    e.preventDefault();
    document.querySelectorAll('.admin-section').forEach(section => {
    section.classList.add('d-none');
});
    document.querySelector(this.getAttribute('href')).classList.remove('d-none');
});
});
});

