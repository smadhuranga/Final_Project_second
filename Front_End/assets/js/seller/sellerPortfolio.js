
    const API_BASE = 'http://localhost:8080/api/v1/';

    // Get seller ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const sellerId = decodeURIComponent(urlParams.get('id'));

    $(document).ready(function() {
    if (!sellerId) {
    showError('No designer specified');
    return;
}

    fetchSellerProfile();
});

    function fetchSellerProfile() {
    $.ajax({
        url: API_BASE + `users/${encodeURIComponent(sellerId)}`,
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        success: function(response) {
            if (response.status === 200) {
                console.log('Portfolio data:', response.data);
                $('#loading').addClass('d-none');
                $('#portfolioData').removeClass('d-none');
                populatePortfolioData(response.data);
            } else {
                showError(response.message || 'Failed to load portfolio');
            }
        },
        error: function(xhr) {
            showError(xhr.responseJSON?.message || 'Failed to load designer information');
        }
    });
}

    function populatePortfolioData(seller) {
    // Show loading avatar and hide real image
    $('#sellerAvatar').css({ opacity: 0, visibility: 'hidden' });

    $('.avatar-loading').show();
    $('#sellerAvatar').hide();

    // Basic Information
    $('#sellerName').text(seller.name);
    $('#sellerBio').text(seller.bio || 'Professional graphic designer with a passion for creating impactful visual identities.');

    // Profile Picture Handling
    const avatarUrl = seller.profileImage
    ? seller.profileImage
    : 'https://source.unsplash.com/random/400x400/?portrait';
    const img = new Image();
    img.src = avatarUrl;

    img.onload = () => {
    $('#sellerAvatar')
    .attr('src', avatarUrl)
    .css({ opacity: 1, visibility: 'visible' });
    $('.avatar-loading').hide();
};
    img.onerror = () => {
    console.error('Failed to load avatar:', avatarUrl);
    $('#sellerAvatar')
    .attr('src', 'https://source.unsplash.com/random/400x400/?portrait')
    .css({ opacity: 1, visibility: 'visible' });
    $('.avatar-loading').hide();
};


    $('#sellerAvatar')
    .attr('src', avatarUrl)
    .on('load', function() {
    $('.avatar-loading').hide();
    $(this).show();
})
    .on('error', function() {
    console.error('Failed to load avatar:', avatarUrl);
    $('.avatar-loading').hide();
    $(this).attr('src', 'https://source.unsplash.com/random/400x400/?portrait').show();
});

    // Remove duplicate avatar setting
    // Stats
    const statsHtml = `
      <div class="stats-card">
          <h4 class="text-yellow">${seller.yearsExperience || 0}+</h4>
          <p>Years Experience</p>
      </div>
      <div class="stats-card">
          <h4 class="text-yellow">${seller.completedProjects || 0}+</h4>
          <p>Projects Completed</p>
      </div>
      <div class="stats-card">
          <h4 class="text-yellow">${seller.hourlyRate || 0}/hr</h4>
          <p>Hourly Rate</p>
      </div>`;
    $('#sellerStats').html(statsHtml);

    // Skills
    const skillsHtml = (seller.skills || []).map(skill =>
    `<div class="skill-badge">${skill}</div>`
    ).join('');
    $('#sellerSkills').html(skillsHtml);

    // Portfolio Items
    const portfolioHtml = (seller.portfolioItems || []).map((item, index) => `
      <div class="col-md-4">
          <div class="portfolio-item">
              <img src="${item.imageUrl || 'https://source.unsplash.com/random/600x400/?design'}"
                   class="img-fluid"
                   alt="${item.title}">
              <div class="portfolio-overlay">
                  <div class="text-center">
                      <h4>${item.title}</h4>
                      <p class="mb-0">${item.description}</p>
                  </div>
              </div>
          </div>
      </div>`).join('');
    $('#portfolioGrid').html(portfolioHtml);
}

    function showError(message) {
    $('#loading').addClass('d-none');
    $('#errorState').removeClass('d-none');
    $('#errorMessage').text(message);
}
