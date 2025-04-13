
    // Configuration
    const API_BASE = 'http://localhost:8080/api/v1/';

    // Global AJAX Setup
    $.ajaxSetup({
    beforeSend: function(xhr) {
    const token = localStorage.getItem('jwtToken');
    if (token) {
    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
}
}
});

    // Session Management
    let currentSeller = null;
    let allSellers = [];

    // Authentication Functions
    function checkAuthStatus() {
    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) {
    showToast('Please login to access this feature', 'warning');
    window.location.href = '/login';
    return false;
}

    try {
    const tokenPayload = JSON.parse(atob(jwtToken.split('.')[1]));
    if (tokenPayload.exp * 1000 < Date.now()) {
    showToast('Session expired. Please login again.', 'danger');
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
    return false;
}
    return true;
} catch (error) {
    showToast('Invalid authentication token', 'danger');
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
    return false;
}
}

    // Seller Management
    function fetchSellers() {
    if (!checkAuthStatus()) return;

    $.ajax({
    url: API_BASE + 'admin/users?type=SELLER',
    method: 'GET',
    success: function(response) {
    if (response.status === 200) {
    allSellers = processSellerData(response.data);
    renderSellers(allSellers); // Render all sellers initially
    applyFilters(); // Apply filters if any
} else {
    showToast(response.message || 'Failed to load sellers', 'danger');
}
},
    error: function(xhr) {
    if (xhr.status === 401) {
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
} else {
    showToast('Failed to load sellers: ' +
    (xhr.responseJSON?.message || xhr.statusText), 'danger');
}
}
});
}

    function processSellerData(apiData) {
    return apiData
    .filter(seller => seller.type === 'SELLER')
    .map(seller => ({
    id: seller.email,
    name: seller.name,
    email: seller.email,
    experience: seller.yearsExperience || 0,
    rate: seller.hourlyRate || 0,
    projects: seller.completedProjects || 0,
    skills: seller.skills || [],
    portfolio: seller.portfolioUrl || '#'
}));
}

    function applyFilters() {
    const minExp = parseInt($('#filterExperience').val()) || 0;
    const maxRate = parseInt($('#filterPrice').val()) || Infinity;
    const minProjects = parseInt($('#filterProjects').val()) || 0;

    const filtered = allSellers.filter(seller =>
    seller.experience >= minExp &&
    seller.rate <= maxRate &&
    seller.projects >= minProjects
    );

    renderSellers(filtered);
}

    function renderSellers(sellers) {
    const $tbody = $('#editorsTable');
    $tbody.empty();

    if (sellers.length === 0) {
    $tbody.html(`<tr><td colspan="5" class="text-center py-4">No sellers found</td></tr>`);
    return;
}

    sellers.forEach(seller => {
    $tbody.append(`
        <tr>
            <td>
                <div class="d-flex align-items-center">
                    <i class="fas fa-user-edit fa-lg me-3 text-yellow"></i>
                    <div>
                        <h5 class="mb-0">${seller.name}</h5>
                        <small class="text-muted">${seller.skills[0] || 'Content'} Specialist</small>
                    </div>
                </div>
            </td>
            <td>${seller.experience}+ years</td>
            <td>$${seller.rate}/hr</td>
            <td>${seller.projects}+</td>
            <td>
                <button class="btn btn-sm btn-outline-primary view-seller" onclick="viewPortfolio('${seller.email}')">
                    <i class="fas fa-eye me-1"></i>Portfolio
                </button>
                <button class="btn btn-sm btn-outline-success chat-seller ms-2"
                        onclick="openChat('${seller.id}')"
                        data-seller-email="${seller.id}">
                    <i class="fas fa-comments me-1"></i>Chat
                </button>
            </td>
        </tr>
    `);
});
}
    function viewPortfolio(email) {
    // Redirect to the portfolio page using the seller's email
    window.location.href = `portfolio.html?id=${encodeURIComponent(email)}`;
}

    // Chat System
    async function openChat(sellerId) {
    if (!checkAuthStatus()) return;

    currentSeller = allSellers.find(s => s.id === sellerId);
    if (!currentSeller) {
    showToast('Seller not found', 'danger');
    return;
}

    try {
    // Send email notification to seller
    await $.ajax({
    url: API_BASE + 'sellers/notify-chat',
    method: 'POST',
    contentType: 'application/x-www-form-urlencoded',
    data: { sellerEmail: currentSeller.id },
    timeout: 5000
});
} catch (error) {
    console.warn('Notification failed:', error);
    showToast('Seller notification failed, but you can still chat', 'warning');
}

    try {
    $('#sellerName').text(currentSeller.name);
    await loadChatHistory(sellerId);
    $('#chatModal').modal('show');
} catch (error) {
    showToast('Failed to start chat', 'danger');
}
}

    async function loadChatHistory(sellerId) {
    try {
    const response = await $.ajax({
    url: API_BASE + `chat/${sellerId}`,
    method: 'GET'
});
    renderChatMessages(response.data || []);
} catch (error) {
    if (error.status === 401) {
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
} else {
    showToast('Failed to load chat history', 'danger');
}
}
}

    function renderChatMessages(messages) {
    const $chatMessages = $('#chatMessages');
    $chatMessages.empty();

    messages.forEach(msg => {
    const isUser  = msg.senderType === 'user';
    $chatMessages.append(`
                <div class="message ${isUser  ? 'user-message' : 'seller-message'}">
                    <div class="message-content">${msg.content}</div>
                    <small class="message-time">
                        ${new Date(msg.timestamp).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit'
})}
                    </small>
                </div>
            `);
});

    $chatMessages.scrollTop($chatMessages[0].scrollHeight);
}

    async function sendMessage() {
    const message = $('#messageInput').val().trim();
    if (!message || !currentSeller) return;

    try {
    await $.ajax({
    url: API_BASE + 'chat/send',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
    sellerId: currentSeller.id,
    message: message
})
});
    $('#messageInput').val('');
    await loadChatHistory(currentSeller.id);
} catch (error) {
    if (error.status === 401) {
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
} else {
    showToast('Failed to send message', 'danger');
}
}
}

    // UI Helpers
    function showToast(message, type = 'success') {
    const toast = $(`
            <div class="toast align-items-center border-0 bg-${type} position-fixed bottom-0 end-0 m-3">
                <div class="d-flex">
                    <div class="toast-body text-white">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `);
    $('body').append(toast);
    new bootstrap.Toast(toast[0]).show();
    setTimeout(() => toast.remove(), 3000);
}



    // Initialization
    $(document).ready(function() {
    if (!checkAuthStatus()) return;

    fetchSellers();
    setupEventHandlers();
    setupScrollAnimations();
});

    function setupEventHandlers() {
    $('#filterExperience, #filterPrice, #filterProjects').on('change', applyFilters);
    $('#sendMessageBtn').click(sendMessage);
    $('#messageInput').keypress(function(e) {
    if (e.which === 13 && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
}
});
}

    function setupScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
    if (entry.isIntersecting) {
    entry.target.classList.add('animated');
}
});
}, { threshold: 0.1 });
    document.querySelectorAll('.animate-on-scroll').forEach(el => observer.observe(el));
}
