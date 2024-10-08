//set trạng thái cho các button đã chọn trên sidebar
const allSideMenu = document.querySelectorAll("#sidebar .side-menu.top li a");

allSideMenu.forEach((item) => {
  item.addEventListener("click", function () {
    const href = item.getAttribute("href");
    localStorage.setItem("activeMenu", href);
  });
});
window.addEventListener("load", function () {
  const activeMenu = localStorage.getItem("activeMenu");
  if (activeMenu) {
    const activeLink = document.querySelector(`a[href='${activeMenu}']`);
    if (activeLink) {
      activeLink.parentElement.classList.add("active");
    }
  }
});


// TOGGLE SIDEBAR
const menuBar = document.querySelector("#content nav .bx.bx-menu");
const sidebar = document.getElementById("sidebar");

menuBar.addEventListener("click", function () {
  sidebar.classList.toggle("hide");
});

const searchButton = document.querySelector(
  "#content nav form .form-input button"
);
const searchButtonIcon = document.querySelector(
  "#content nav form .form-input button .bx"
);
const searchForm = document.querySelector("#content nav form");

searchButton.addEventListener("click", function (e) {
  if (window.innerWidth < 576) {
    e.preventDefault();
    searchForm.classList.toggle("show");
    if (searchForm.classList.contains("show")) {
      searchButtonIcon.classList.replace("bx-search", "bx-x");
    } else {
      searchButtonIcon.classList.replace("bx-x", "bx-search");
    }
  }
});

if (window.innerWidth < 768) {
  sidebar.classList.add("hide");
} else if (window.innerWidth > 576) {
  searchButtonIcon.classList.replace("bx-x", "bx-search");
  searchForm.classList.remove("show");
}

window.addEventListener("resize", function () {
  if (this.innerWidth > 576) {
    searchButtonIcon.classList.replace("bx-x", "bx-search");
    searchForm.classList.remove("show");
  }
});

const switchMode = document.getElementById("switch-mode");

switchMode.addEventListener("change", function () {
  if (this.checked) {
    document.body.classList.add("dark");
    localStorage.setItem("theme", "dark"); // Lưu trạng thái
    // Toggle moon icon visible and sun icon hidden
    document.querySelector(".bx.bx-moon").style.display = "inline-block";
    document.querySelector(".bx.bx-moon").style.filter =
        "drop-shadow(0 0 1px rgb(255, 255, 255))";
    document.querySelector(".bx.bxs-sun").style.display = "none";
  } else {
    document.body.classList.remove("dark");
    localStorage.setItem("theme", "light"); // Lưu trạng thái
    // Toggle moon icon hidden and sun icon visible
    document.querySelector(".bx.bx-moon").style.display = "none";
    document.querySelector(".bx.bxs-sun").style.display = "inline-block";
  }
});
document.addEventListener("DOMContentLoaded", function () {
  const theme = localStorage.getItem("theme");

  if (theme === "dark") {
    document.body.classList.add("dark");
    switchMode.checked = true; // Đặt checkbox thành checked
    document.querySelector(".bx.bx-moon").style.display = "inline-block";
    document.querySelector(".bx.bxs-sun").style.display = "none";
  } else {
    document.body.classList.remove("dark");
    switchMode.checked = false; // Đặt checkbox thành unchecked
    document.querySelector(".bx.bx-moon").style.display = "none";
    document.querySelector(".bx.bxs-sun").style.display = "inline-block";
  }
});



