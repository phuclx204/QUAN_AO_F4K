const allSideMenu = document.querySelectorAll("#sidebar .side-menu.top li a");

allSideMenu.forEach((item) => {
  item.addEventListener("click", function (e) {
    const href = item.getAttribute("href");
    localStorage.setItem("activeMenu", href);
  });
});

// Handle submenu toggle
const submenuItems = document.querySelectorAll(".has-submenu > a");

// Handle submenu toggle with animation
submenuItems.forEach((item) => {
  item.addEventListener("click", function (e) {
    e.preventDefault();
    const parent = this.parentElement;
    const submenu = parent.querySelector(".submenu");

    // Toggle active class for the parent
    parent.classList.toggle("active");

    // Toggle submenu display with animation
    if (parent.classList.contains("active")) {
      submenu.style.display = 'block'; // Hiển thị submenu
    } else {
      submenu.style.display = 'none'; // Ẩn submenu
    }

    // Lưu trạng thái submenu vào localStorage
    const href = this.getAttribute("href");
    localStorage.setItem("activeMenu", href);
  });
});

// Load trạng thái menu từ localStorage
window.addEventListener("load", function () {
  const activeMenu = localStorage.getItem("activeMenu");
  if (activeMenu) {
    const activeLink = document.querySelector(`a[href='${activeMenu}']`);
    if (activeLink) {
      activeLink.parentElement.classList.add("active");
      // Nếu có menu con, mở nó
      const parent = activeLink.closest('.has-submenu');
      if (parent) {
        parent.classList.add("active");
        const submenu = parent.querySelector(".submenu");
        submenu.style.display = 'block'; // Mở submenu
      }
    }
  }
});

// TOGGLE SIDEBAR
const menuBar = document.querySelector("#content nav .bx.bx-menu");
const sidebar = document.getElementById("sidebar");

menuBar.addEventListener("click", function () {
  const a = sidebar.classList.toggle("hide");
  localStorage.setItem("activeSidebar", a);
});

// window.addEventListener("resize", function () {
//   if (this.innerWidth > 760) {
//     sidebar.classList.remove("hide");
//   }
// });


const switchMode = document.getElementById("switch-mode");

switchMode.addEventListener("change", function () {
  if (this.checked) {
    document.body.classList.add("dark");
    localStorage.setItem("theme", "dark");
    document.querySelector(".bx.bx-moon").style.display = "inline-block";
    document.querySelector(".bx.bx-moon").style.filter =
        "drop-shadow(0 0 1px rgb(255, 255, 255))";
    document.querySelector(".bx.bxs-sun").style.display = "none";
  } else {
    document.body.classList.remove("dark");
    localStorage.setItem("theme", "light");
    document.querySelector(".bx.bx-moon").style.display = "none";
    document.querySelector(".bx.bxs-sun").style.display = "inline-block";
  }
});

document.addEventListener("DOMContentLoaded", function () {
  const theme = localStorage.getItem("theme");
  const sizeBarStatus = localStorage.getItem("activeSidebar");
  if (sizeBarStatus === 'true') {
    sidebar.classList.add("hide");
  } else {
    sidebar.classList.remove("hide")
  }

  if (theme === "dark") {
    document.body.classList.add("dark");
    switchMode.checked = true;
    document.querySelector(".bx.bx-moon").style.display = "inline-block";
    document.querySelector(".bx.bxs-sun").style.display = "none";
  } else {
    document.body.classList.remove("dark");
    switchMode.checked = false;
    document.querySelector(".bx.bx-moon").style.display = "none";
    document.querySelector(".bx.bxs-sun").style.display = "inline-block";
  }
});

function openLoading() {
  $('#loading').addClass('show');
  $('body').addClass('no-interaction');
}

function closeLoading() {
  $('#loading').removeClass('show');
  $('body').removeClass('no-interaction');
}