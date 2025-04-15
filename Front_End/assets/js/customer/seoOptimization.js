// Configuration
const API_BASE = 'http://localhost:8080/api/v1/';
let allSellers = [];
let currentSeller = null;
let chatInterval = null;

// Configure global AJAX settings
$.ajaxSetup({
    beforeSend: function (xhr) {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        }
    },
    error: function (xhr) {
        if (xhr.status === 401) {
            localStorage.removeItem('jwtToken');
            window.location.href = '/login';
        }
    }
});

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
async function initializePage() {
    if (!checkAuthStatus()) return;

    try {
        const response = await $.ajax({
            url: API_BASE + 'admin/users?type=SELLER',
            method: 'GET'
        });

        allSellers = processSellerData(response.data);
        console.log('All Sellers:', allSellers);
        renderSpecialists(allSellers);
        setupFilters();
        setupSearch();
        initScrollAnimations();
    } catch (error) {
        if (error.status === 401) {
            showToast('Session expired. Please login again.', 'danger');
            localStorage.removeItem('jwtToken');
            window.location.href = '/login';
        } else {
            showToast('Failed to load specialists. Please try again later.', 'danger');
            $('#specialistsTable').html(`<tr><td colspan="5" class="text-center">No specialists available</td></tr>`);
        }
    }
}



// Render Functions
function renderSpecialists(data) {
    const tbody = $('#specialistsTable');
    tbody.empty().html(data.map(specialist => `
    <tr>
        <td>
            <div class="d-flex align-items-center">
                <i class="fas fa-user-tie fa-2x me-3"></i>
                <div>
                    <h5 class="mb-0">${escapeHTML(specialist.name)}</h5>
                    <small>${escapeHTML(specialist.skills.join(', '))}</small>
                </div>
            </div>
        </td>
        <td><span class="exp-badge">${specialist.experience}+ years</span></td>
        <td><span class="price-badge">$${specialist.rate}/hr</span></td>
        <td>${specialist.projects}+</td>
        <td>
             <button class="btn btn-sm btn-outline-primary view-seller"
                data-email="${escapeHTML(specialist.email)}">
            <i class="fas fa-eye me-1"></i>Portfolio
        </button>
            <button class="btn btn-sm btn-outline-success chat-seller ms-2"
        data-id="${specialist.id}"
        data-name="${escapeHTML(specialist.name)}"
        data-email="${specialist.email}">
    <i class="fas fa-comment me-1"></i>Chat
</button>
        </td>
    </tr>
`));

    // Update the view-seller click handler
    $(document).on('click', '.view-seller', async function () {
        try {
            const sellerEmail = $(this).data('email');

            // Redirect to the portfolio page using the seller's email
            window.location.href = `portfolio.html?id=${encodeURIComponent(sellerEmail)}`;
        } catch (error) {
            showToast('Failed to load portfolio. Please try again later.', 'danger');
            console.error('Error navigating to portfolio:', error);
        }
    });
}

function processSellerData(apiData) {
    return apiData
        .filter(seller => seller.type === 'SELLER')
        .map(seller => ({
            id: seller.id,
            email: seller.email, // Add email field
            name: seller.name,
            experience: seller.experienceYears || 0,
            rate: seller.hourlyRate || 0,
            projects: seller.completedProjects || 0,
            skills: seller.skills || [],
            portfolio: seller.portfolioUrl || '#',
            rating: seller.rating || '4.5'
        }));
}


// Filter System
function setupFilters() {
    $('.filter-select').off('change').on('change', applyFilters);
}

function applyFilters() {
    const price = parseInt($('#filterPrice').val());
    const experience = parseInt($('#filterExperience').val());
    const projects = parseInt($('#filterProjects').val());

    const filtered = allSellers.filter(seller => {
        const priceMatch = !price || checkPriceRange(seller.rate, price);
        const expMatch = !experience || checkExperience(seller.experience, experience);
        const projectsMatch = !projects || seller.projects >= projects;

        return priceMatch && expMatch && projectsMatch;
    });

    renderSpecialists(filtered);
}

function checkPriceRange(rate, priceFilter) {
    const ranges = {
        50: rate < 50,
        100: rate >= 50 && rate <= 100,
        150: rate > 100 && rate <= 150,
        200: rate > 150
    };
    return ranges[priceFilter];
}

function checkExperience(exp, expFilter) {
    const ranges = {
        2: exp <= 2,
        5: exp > 2 && exp <= 5,
        10: exp > 5
    };
    return ranges[expFilter];
}

// Search System
function setupSearch() {
    $('#searchInput').off('input').on('input', function () {
        const term = $(this).val().toLowerCase();
        const filtered = allSellers.filter(seller =>
            seller.name.toLowerCase().includes(term) ||
            seller.skills.some(skill => skill.toLowerCase().includes(term))
        );
        renderSpecialists(filtered);
    });
}

// Chat System
$(document).on('click', '.chat-seller', async function () {
    if (!checkAuthStatus()) return;

    currentSeller = {
        id: $(this).data('id'),
        name: $(this).data('name'),
        email: $(this).data('email')
    };

    try {
        await $.ajax({
            url: API_BASE + 'sellers/notify-chat',
            method: 'POST',
            data: {sellerEmail: currentSeller.email},
            timeout: 5000
        });
    } catch (error) {
        console.warn('Notification failed:', error);
        showToast("Couldn't notify seller, but chat is available", 'warning');
    }

    // Open chat interface
    $('.chat-modal').addClass('active');
    $('#sellerName').text(currentSeller.name);
    loadChatHistory();
    chatInterval = setInterval(loadChatHistory, 3000);
});

async function loadChatHistory() {
    try {
        const encodedEmail = encodeURIComponent(currentSeller.email);
        const response = await $.ajax({
            url: API_BASE + `sellers/me/chat/${encodedEmail}`,
            method: 'GET'
        });

        $('#chatMessages').html(response.data.map(msg => `
            <div class="message ${msg.senderType === 'user' ? 'sent' : ''}">
                <div class="msg-text">${escapeHTML(msg.content)}</div>
                <div class="msg-time">
                    ${new Date(msg.timestamp).toLocaleTimeString()}
                </div>
            </div>
        `).join(''));
        scrollChatToBottom();
    } catch (error) {
        showToast('Error loading chat history', 'danger');
    }
}

async function sendMessage() {
    const message = $('#messageInput').val().trim();
    if (!message) return;

    try {
        await $.ajax({
            url: API_BASE + 'chat/send',
            method: 'POST',
            data: {
                sellerEmail: currentSeller.email,
                message: message
            }
        });

        $('#messageInput').val('');
        loadChatHistory();
    } catch (error) {
        showToast('Error sending message', 'danger');
    }
}

function handleMessageKey(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
}

// Utility Functions
function escapeHTML(str) {
    return str.replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
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

function scrollChatToBottom() {
    const chatBody = document.getElementById('chatMessages');
    chatBody.scrollTop = chatBody.scrollHeight;
}

function initScrollAnimations() {
    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animated');
            }
        });
    }, {threshold: 0.1});

    document.querySelectorAll('.animate-on-scroll').forEach(el => observer.observe(el));
}

// Initialization
$(document).ready(() => {
    initializePage();

    // Chat Input Handling
    $('#messageInput').keypress(e => {
        if (e.which === 13 && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    // Chat Modal Cleanup
    $('.chat-modal .btn-close').click(() => {
        clearInterval(chatInterval);
        $('.chat-modal').removeClass('active');
        currentSeller = null;
    });
});
