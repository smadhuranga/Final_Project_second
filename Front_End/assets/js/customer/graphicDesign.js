// Configuration
const API_BASE = 'http://localhost:8080/api/v1/';
let allSellers = [];

// Global AJAX Setup
$.ajaxSetup({
    beforeSend: function (xhr) {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        }
    }
});

// Authentication Functions
function checkAuthStatus() {
    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) {
        window.location.href = '../index.html';
        return false;
    }

    try {
        const tokenPayload = JSON.parse(atob(jwtToken.split('.')[1]));
        if (tokenPayload.exp * 1000 < Date.now()) {
            logout();
            return false;
        }
        return true;
    } catch (error) {
        logout();
        return false;
    }
}

function logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('user');
    window.location.href = '../index.html';
}

// Seller Management
function fetchSellers() {
    if (!checkAuthStatus()) return;

    $.ajax({
        url: API_BASE + 'admin/users?type=SELLER',
        method: 'GET',
        success: function (response) {
            if (response.status === 200) {

                allSellers = processSellerData(response.data.filter(user =>
                    user.type === 'SELLER'
                ));

                applyFilters();
                initializeFilterHandlers();
            } else {
                showToast(response.message || 'Failed to load sellers', 'danger');
            }
        },
        error: function (xhr) {
            if (xhr.status === 401) logout();
            showToast('Failed to load sellers: ' + (xhr.responseJSON?.message || xhr.statusText), 'danger');
        }
    });
}

function processSellerData(apiData) {
    return apiData.map(seller => ({
        id: seller.email,
        name: seller.name,
        experience: seller.yearsExperience || 0,
        rate: seller.hourlyRate || 0,
        projects: seller.completedProjects || 0,
        skills: seller.skills || [],
        portfolio: seller.portfolioUrl || '#'
    }));
}


function renderSellers(sellers) {
    const tbody = $('#sellersBody');
    tbody.empty();

    const safeHTML = sellers.map(seller => {
        const safeName = escapeHtml(seller.name);
        const safeId = escapeHtml(seller.id);
        const safeSkills = seller.skills.map(skill =>
            `<span class="seller-badge">${escapeHtml(skill)}</span>`
        ).join('');

        return `
            <tr class="glow-on-hover">
                <td>
                    <div class="d-flex align-items-center">
                        <i class="fas fa-user-tie fa-2x me-3 text-yellow"></i>
                        <div>
                            <h5 class="mb-0">${safeName}</h5>
                            <small class="text-muted">${seller.experience}+ years experience</small>
                        </div>
                    </div>
                </td>
                <td>${seller.experience} years</td>
                <td>$${seller.rate}/hr</td>
                <td>${seller.projects}+</td>
                <td>${safeSkills}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary view-seller"
                            data-seller-id="${safeId}">
                        <i class="fas fa-eye me-1"></i>Portfolio
                    </button>
                    <button class="btn btn-sm btn-outline-success chat-seller ms-2"
                            data-seller-id="${safeId}"
                            data-seller-name="${safeName}">
                        <i class="fas fa-comments me-1"></i>Chat
                    </button>
                </td>
            </tr>
        `;
    }).join('');

    tbody.html(safeHTML);
    initializeEventHandlers();
}

// Add HTML escaping utility
function escapeHtml(unsafe) {
    return unsafe?.toString()
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;") || '';
}

// Initialize event handlers safely
function initializeEventHandlers() {
    $(document).off('click', '.chat-seller').on('click', '.chat-seller', function () {
        const sellerId = $(this).data('seller-id');
        const sellerName = $(this).data('seller-name');
        console.log('Chat initiated with:', sellerId, sellerName);
        openChat(sellerId, sellerName);
    });

    // Portfolio button handler
    $(document).off('click', '.view-seller').on('click', '.view-seller', function () {
        const sellerId = $(this).data('seller-id');
        console.log('Viewing portfolio for:', sellerId);
        viewPortfolio(sellerId);
    });
}


function viewPortfolio(sellerId) {
    // Validate email format
    const validEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!validEmailRegex.test(sellerId)) {
        showToast('Invalid seller identifier', 'danger');
        return;
    }

    // CORRECT PATH CONSTRUCTION
    const encodedId = encodeURIComponent(sellerId);
    const portfolioPath = `../pages/portfolio.html?id=${encodedId}`;

    // Secure window opening
    window.open(portfolioPath, '_blank', 'noopener,noreferrer');
}

// Filter Handling
function initializeFilterHandlers() {
    $('#minExp, #maxRate, #minProjects').off('input').on('input', applyFilters);
}

function applyFilters() {
    const minExp = parseInt($('#minExp').val()) || 0;
    const maxRate = parseInt($('#maxRate').val()) || Infinity;
    const minProjects = parseInt($('#minProjects').val()) || 0;

    const filtered = allSellers.filter(seller =>
        seller.experience >= minExp &&
        seller.rate <= maxRate &&
        seller.projects >= minProjects
    );

    renderSellers(filtered);
}

// Chat System
let currentChatSeller = null;

async function openChat(sellerId, sellerName) {
    if (!checkAuthStatus()) return;

    try {
        console.log('Sending notification to:', sellerId);
        const response = await $.ajax({
            url: API_BASE + 'sellers/notify-chat',
            method: 'POST',
            data: {sellerEmail: sellerId},
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
            }
        });

        console.log('Notification response:', response);
        showToast('Seller has been notified via email', 'success');
    } catch (error) {
        console.error('Notification failed:', error);
        showToast('Notification failed: ' + (error.responseJSON?.message || 'Server error'), 'danger');
    }

    // Open chat modal
    try {
        $('#sellerName').text(sellerName);
        $('#chatModal').modal('show');
        $('#chatMessages').html(`
            <div class="text-center text-muted p-3">
                <i class="fas fa-envelope fa-2x mb-2"></i>
                <p>Notification sent to ${sellerName}</p>
                <small>You can start chatting once the seller responds</small>
            </div>
        `);
    } catch (modalError) {
        console.error('Error opening modal:', modalError);
        showToast('Error opening chat interface', 'danger');
    }
}


// UI Helpers
function showToast(message, type = 'success') {
    const toast = $(`
            <div class="toast align-items-center border-0 bg-${type} position-fixed bottom-0 end-0 m-3">
                <div class="d-flex">
                    <div class="toast-body text-white">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto"
                            data-bs-dismiss="toast"></button>
                </div>
            </div>
        `);
    $('body').append(toast);
    new bootstrap.Toast(toast[0]).show();
    setTimeout(() => toast.remove(), 3000);
}


// Initialization
$(document).ready(function () {
    if (checkAuthStatus()) {
        fetchSellers();
        initializeScrollAnimations();
        initializeEventHandlers();


    }
});

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

// Error Handling
$(document).ajaxError(function (event, jqXHR) {
    if (jqXHR.status === 401) logout();
});

