const API_BASE = 'http://localhost:8080/api/v1/';

$.ajaxSetup({
    beforeSend: function (xhr) {
        const token = localStorage.getItem('jwtToken');
        console.log(token)
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
        success: function (response) {
            if (response.status === 200) {
                allSellers = processSellerData(response.data);
                applyFilters();
            } else {
                showToast(response.message || 'Failed to load sellers', 'danger');
            }
        },
        error: function (xhr) {
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

function renderSellers(sellers) {
    const $tbody = $('#sellersBody');
    $tbody.empty();

    if (sellers.length === 0) {
        $tbody.html(`<tr><td colspan="6" class="text-center py-4">No sellers found</td></tr>`);
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
                        <td>${seller.skills.map(skill =>
            `<span class="seller-badge">${skill}</span>`).join('')}
                        </td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary view-seller"
                                    onclick="viewPortfolio('${seller.id}')">
                                <i class="fas fa-eye me-1"></i>Portfolio
                            </button>
                            <button class="btn btn-sm btn-outline-success chat-seller ms-2"
                                    onclick="openChat('${seller.id}')">
                                <i class="fas fa-comments me-1"></i>Chat
                            </button>
                        </td>
                    </tr>
                `);
    });
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
        await $.ajax({
            url: API_BASE + 'sellers/notify-chat',
            method: 'POST',
            data: {sellerEmail: sellerId},
            timeout: 5000 // 5 second timeout
        });
    } catch (error) {
        console.warn('Notification failed, proceeding with chat anyway');
    }

    try {
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
        const isUser = msg.senderType === 'user';
        $chatMessages.append(`
                    <div class="message ${isUser ? 'user-message' : 'seller-message'}">
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

function viewPortfolio(sellerId) {
    const encodedEmail = encodeURIComponent(sellerId); // Encode special characters
    window.open(`portfolio.html?id=${encodedEmail}`, '_blank');
}

// Initialization
$(document).ready(function () {
    if (!checkAuthStatus()) return;

    fetchSellers();
    setupEventHandlers();
    setupScrollAnimations();
});

function setupEventHandlers() {
    $('#minExp, #maxRate, #minProjects').on('input', applyFilters);

    // Chat events
    $('#sendMessage').click(sendMessage);
    $('#messageInput').keypress(function (e) {
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
    }, {threshold: 0.1});
    document.querySelectorAll('.animate-on-scroll').forEach(el => observer.observe(el));
}
