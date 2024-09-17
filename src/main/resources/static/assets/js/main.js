/**
 * Main
 */

'use strict';

let menu, animate;

(function () {
  // Initialize menu
  //-----------------

  let layoutMenuEl = document.querySelectorAll('#layout-menu');
  layoutMenuEl.forEach(function (element) {
    menu = new Menu(element, {
      orientation: 'vertical',
      closeChildren: false
    });
    // Change parameter to true if you want scroll animation
    window.Helpers.scrollToActive((animate = false));
    window.Helpers.mainMenu = menu;
  });

  // Initialize menu togglers and bind click on each
  let menuToggler = document.querySelectorAll('.layout-menu-toggle');
  menuToggler.forEach(item => {
    item.addEventListener('click', event => {
      event.preventDefault();
      window.Helpers.toggleCollapsed();
    });
  });

  // Display menu toggle (layout-menu-toggle) on hover with delay
  let delay = function (elem, callback) {
    let timeout = null;
    elem.onmouseenter = function () {
      // Set timeout to be a timer which will invoke callback after 300ms (not for small screen)
      if (!Helpers.isSmallScreen()) {
        timeout = setTimeout(callback, 300);
      } else {
        timeout = setTimeout(callback, 0);
      }
    };

    elem.onmouseleave = function () {
      // Clear any timers set to timeout
      document.querySelector('.layout-menu-toggle').classList.remove('d-block');
      clearTimeout(timeout);
    };
  };
  if (document.getElementById('layout-menu')) {
    delay(document.getElementById('layout-menu'), function () {
      // not for small screen
      if (!Helpers.isSmallScreen()) {
        document.querySelector('.layout-menu-toggle').classList.add('d-block');
      }
    });
  }

  // Display in main menu when menu scrolls
  let menuInnerContainer = document.getElementsByClassName('menu-inner'),
    menuInnerShadow = document.getElementsByClassName('menu-inner-shadow')[0];
  if (menuInnerContainer.length > 0 && menuInnerShadow) {
    menuInnerContainer[0].addEventListener('ps-scroll-y', function () {
      if (this.querySelector('.ps__thumb-y').offsetTop) {
        menuInnerShadow.style.display = 'block';
      } else {
        menuInnerShadow.style.display = 'none';
      }
    });
  }

  // Init helpers & misc
  // --------------------

  // Init BS Tooltip
  const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
  tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl);
  });

  // Accordion active class
  const accordionActiveFunction = function (e) {
    if (e.type == 'show.bs.collapse' || e.type == 'show.bs.collapse') {
      e.target.closest('.accordion-item').classList.add('active');
    } else {
      e.target.closest('.accordion-item').classList.remove('active');
    }
  };

  const accordionTriggerList = [].slice.call(document.querySelectorAll('.accordion'));
  const accordionList = accordionTriggerList.map(function (accordionTriggerEl) {
    accordionTriggerEl.addEventListener('show.bs.collapse', accordionActiveFunction);
    accordionTriggerEl.addEventListener('hide.bs.collapse', accordionActiveFunction);
  });

  // Auto update layout based on screen size
  window.Helpers.setAutoUpdate(true);

  // Toggle Password Visibility
  window.Helpers.initPasswordToggle();

  // Speech To Text
  window.Helpers.initSpeechToText();

  // Manage menu expanded/collapsed with templateCustomizer & local storage
  //------------------------------------------------------------------

  // If current layout is horizontal OR current window screen is small (overlay menu) than return from here
  if (window.Helpers.isSmallScreen()) {
    return;
  }

  // If current layout is vertical and current window screen is > small

  // Auto update menu collapsed/expanded based on the themeConfig
  window.Helpers.setCollapsed(true, false);
})();

// RAM
$(document).ready(function(){
  $('.dropdown-menu #form-check-ram').click(function(event){
    event.stopPropagation(); // Ngăn chặn sự kiện click truyền ra ngoài
  });

  $('.dropdown-menu #form-check-ram').change(function(){
    var selectedItems = [];
    $('.dropdown-menu #form-check-ram input[type="checkbox"]:checked').each(function(){
      selectedItems.push($(this).siblings('label').text());
    });

    if(selectedItems.length > 0){
      $('#ramDropdownMenuButton').text(selectedItems.join(', '));
    } else {
      $('#ramDropdownMenuButton').text('Chọn RAM');
    }
  });
});

// ROM
$(document).ready(function(){
  $('.dropdown-menu #form-check-rom').click(function(event){
    event.stopPropagation(); // Ngăn chặn sự kiện click truyền ra ngoài
  });

  $('.dropdown-menu #form-check-rom').change(function(){
    var selectedItems = [];
    $('.dropdown-menu #form-check-rom input[type="checkbox"]:checked').each(function(){
      selectedItems.push($(this).siblings('label').text());
    });

    if(selectedItems.length > 0){
      $('#romDropdownMenuButton').text(selectedItems.join(', '));
    } else {
      $('#romDropdownMenuButton').text('Chọn ROM');
    }
  });
});

// Màu sắc
function changeButtonBackground(checkbox) {
  var button = checkbox.parentElement;
  if (checkbox.checked) {
    button.classList.add('btn-success');
    addButtonToList(button.innerText.trim());
  } else {
    button.classList.remove('btn-success');
    removeButtonFromList(button.innerText.trim());
  }
}

function addButtonToList(buttonText) {
  var selectedButtons = document.getElementById('selectedButtons');
  var button = document.createElement('button');
  button.setAttribute('type', 'button');
  button.classList.add('btn', 'btn-success', 'me-2', 'mb-2');
  button.style.backgroundColor = buttonText
  button.innerText = buttonText;
  selectedButtons.appendChild(button);
}

function removeButtonFromList(buttonText) {
  var selectedButtons = document.getElementById('selectedButtons');
  var buttons = selectedButtons.getElementsByTagName('button');
  for (var i = 0; i < buttons.length; i++) {
    if (buttons[i].innerText.trim() === buttonText) {
      selectedButtons.removeChild(buttons[i]);
      break;
    }
  }
}







var errorMessage = document.getElementById('errorMessage');
if (errorMessage) {
  errorMessage.classList.add('slideInRight');
  setTimeout(function() {
    errorMessage.classList.remove('slideInRight');
    errorMessage.classList.add('slideOutRight');
    setTimeout(function() {
      errorMessage.style.display = 'none';
    }, 1000);
  }, 4000);
  }

