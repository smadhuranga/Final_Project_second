// Configuration
const API_BASE = 'http://localhost:8080/api/v1/';
let allSellers = [];
let currentChat = null;
let chatInterval = null;

// Global AJAX Setup
$.ajaxSetup({
    beforeSend: function (xhr) {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        }
    }
});

// Initialization
$(document).ready(function () {
    checkAuthStatus();
    initializeScrollAnimations();
    loadSellers();
    setupEventListeners();
});

// Authentication Functions
function checkAuthStatus() {
    const token = localStorage.getItem('jwtToken');
    if (!token) redirectToLogin();

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.exp * 1000 < Date.now()) logout();
    } catch (error) {
        redirectToLogin();
    }
}

function redirectToLogin() {
    window.location.href = '../index.html';
}

function logout() {
    localStorage.removeItem('jwtToken');
    redirectToLogin();
}

// Seller Data Functions
async function loadSellers() {
    try {
        const response = await $.ajax({
            url: API_BASE + 'admin/users?type=SELLER',
            method: 'GET'
        });

        // Handle API response structure
        const sellersData = response.data || [];
        allSellers = processSellerData(sellersData);
        renderSellers(allSellers);
    } catch (error) {
        handleAjaxError(error, 'Failed to load sellers');
    }
}

function processSellerData(apiData) {
    return apiData.map(seller => ({
        id: seller.id || 'N/A',
        name: seller.name || 'Anonymous Seller',
        email: seller.email || 'no-email', // Add this line
        avatar: seller.profileImage || 'https://source.unsplash.com/random/200x200/?person',
        experience: seller.yearsExperience || 0,
        hourlyRate: seller.hourlyRate || 0,
        completedProjects: seller.completedProjects || 0,
        skills: (seller.skills && seller.skills.split(', ')) || ['No skills listed'],
        bio: seller.bio || 'No bio available',
        portfolio: tryParsePortfolio(seller.portfolio),
        type: seller.type || 'CUSTOMER'
    })).filter(seller => seller.type === 'SELLER');
}

function filterSellers() {
    const filters = {
        minExp: parseInt($('#minExp').val()) || 0,
        maxRate: parseInt($('#maxRate').val()) || Infinity,
        minProjects: parseInt($('#minProjects').val()) || 0
    };

    const filtered = allSellers.filter(seller =>
        seller.experience >= filters.minExp &&
        seller.hourlyRate <= filters.maxRate &&
        seller.completedProjects >= filters.minProjects
    );

    renderSellers(filtered);
}

function tryParsePortfolio(portfolio) {
    try {
        return portfolio ? JSON.parse(portfolio) : [];
    } catch (e) {
        return [];
    }
}

function renderSellers(sellers) {
    const tbody = $('#sellersBody');
    tbody.empty();

    if (sellers.length === 0) {
        tbody.html(`
            <tr>
                <td colspan="6" class="text-center py-4">
                    No sellers found matching your criteria
                </td>
            </tr>
        `);
        return;
    }

    sellers.forEach(seller => {
        tbody.append(`
            <tr>
                <td>
                    <div class="d-flex align-items-center">
                        <img src="${seller.avatar}"
                            class="rounded-circle me-3"
                            width="40"
                            height="40"
                            alt="${seller.name}">
                        <div>
                            <h6 class="mb-0">${seller.name}</h6>
                            <small class="text-muted">Email: ${seller.email}</small>
                        </div>
                    </div>
                </td>
                <td>${seller.experience} yrs</td>
                <td>$${seller.hourlyRate}/hr</td>
                <td>${seller.completedProjects}</td>
                <td>${seller.skills.slice(0, 3).join(', ')}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary view-portfolio"
                            data-seller-email="${seller.email}">
                        <i class="fas fa-eye me-1"></i>Portfolio
                    </button>
                    <button class="btn btn-sm btn-outline-success chat-seller ms-2"
                            data-seller-email="${seller.email}">
                        <i class="fas fa-comments me-1"></i>Chat
                    </button>
                </td>
            </tr>
        `);
    });
}

$(document).on('click', '.view-portfolio', function () {
    const sellerEmail = $(this).data('seller-email');
    viewPortfolio(sellerEmail);
});
$(document).on('click', '.view-seller', showSellerDetails);

function viewPortfolio(sellerEmail) {
    if (!sellerEmail || sellerEmail === 'no-email') {
        showAlert('Seller email not available', 'danger');
        return;
    }
    const encodedEmail = encodeURIComponent(sellerEmail);
    window.location.href = `portfolio.html?id=${encodedEmail}`;
}


// Seller Details Functions
async function showSellerDetails() {
    const sellerId = $(this).data('seller-id');
    try {
        const response = await $.ajax({
            url: API_BASE + `sellers/${sellerId}`
        });
        populateSellerModal(response.data);
        $('#sellerModal').modal('show');
    } catch (error) {
        handleAjaxError(error, 'Failed to load seller details');
    }
}

function populateSellerModal(seller) {
    $('.seller-name').text(seller.name);
    $('.seller-avatar').attr('src', seller.avatar);
    $('.seller-bio').text(seller.bio);
    $('.seller-rating').html(generateStars(seller.rating));
    $('.seller-skills').html(seller.skills.map(s => `
            <span class="badge bg-secondary me-1">${s}</span>
        `));

    const portfolioContainer = $('.seller-portfolio');
    portfolioContainer.empty();
    seller.portfolio.forEach(item => {
        portfolioContainer.append(`
                <div class="col-md-4 mb-3">
                    <div class="portfolio-item">
                        <img src="${item.image}"
                            class="img-fluid"
                            alt="${item.title}">
                    </div>
                </div>
            `);
    });
}


// Modified startChatSession function
async function startChatSession() {
    const sellerEmail = $(this).data('seller-email');
    currentChat = sellerEmail;

    try {

        await $.ajax({
            url: API_BASE + 'sellers/notify-chat',
            method: 'POST',
            data: {sellerEmail: sellerEmail},
            timeout: 5000
        });
    } catch (error) {
        console.warn('Email notification failed:', error);
        showAlert('Could not notify seller, but you can still send messages', 'warning');
    }

    try {
        const encodedEmail = encodeURIComponent(sellerEmail);
        const response = await $.ajax({
            url: API_BASE + `sellers/me/chat/${encodedEmail}`,
            method: 'GET'
        });
        openChatModal(response);
        startChatPolling();
    } catch (error) {
        console.error('Chat error:', error);
        showAlert('Failed to load chat history. Starting new conversation.', 'info');
        openChatModal([]);
    }
}

function openChatModal(messages) {
    const chatMessages = $('.chat-messages');
    chatMessages.empty();

    messages.forEach(msg => {
        chatMessages.append(`
                <div class="message ${msg.sender === 'buyer' ? 'sent' : 'received'}">
                    <div class="message-content">${msg.content}</div>
                    <div class="message-time">
                        ${new Date(msg.timestamp).toLocaleTimeString()}
                    </div>
                </div>
            `);
    });

    $('#chatModal').modal('show');
    scrollToBottom();
}

async function sendChatMessage() {
    const message = $('#chatModal textarea').val().trim();
    if (!message) return;

    try {
        await $.ajax({
            url: API_BASE + `chat/${currentChat}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({message})
        });

        $('#chatModal textarea').val('');
        appendMessage(message, 'sent');
    } catch (error) {
        handleAjaxError(error, 'Failed to send message');
    }
}

// Helper Functions
function generateStars(rating) {
    return [...Array(5)].map((_, i) => `
            <i class="fas fa-star ${i < rating ? 'text-warning' : 'text-secondary'}"></i>
        `).join('');
}

function handleAjaxError(error, defaultMsg) {
    console.error(error);
    if (error.status === 401) logout();
    showAlert(error.responseJSON?.message || defaultMsg, 'danger');
}

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

function initializeScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animated');
            }
        });
    }, {threshold: 0.1});

    document.querySelectorAll('.animate-on-scroll').forEach(el => observer.observe(el));
}

function setupEventListeners() {

    $('#minExp, #maxRate, #minProjects').on('input', filterSellers);

    $(document).on('click', '.view-portfolio', function () {
        const sellerEmail = $(this).data('seller-email');
        viewPortfolio(sellerEmail);
    });

    $(document).on('click', '.view-seller', showSellerDetails);

    $(document).on('click', '.chat-seller', startChatSession);

    $(document).on('click', '.send-message', sendChatMessage);
    $('#chatModal').on('hidden.bs.modal', stopChatPolling);
}

function stopChatPolling() {
    if (chatInterval) clearInterval(chatInterval);
    currentChat = null;
}

function scrollToBottom() {
    const container = $('.chat-messages');
    container.scrollTop(container[0].scrollHeight);
}

function appendMessage(content, direction) {
    const msgElement = `
            <div class="message ${direction}">
                <div class="message-content">${content}</div>
                <div class="message-time">
                    ${new Date().toLocaleTimeString()}
                </div>
            </div>
        `;
    $('.chat-messages').append(msgElement);
    scrollToBottom();
}
