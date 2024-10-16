// set trạng thái cho các button đã chọn trên sidebar
const allSideMenu = document.querySelectorAll("#sidebar .side-menu.top li a");

allSideMenu.forEach((item) => {
  item.addEventListener("click", function (e) {
    const href = item.getAttribute("href");
    localStorage.setItem("activeMenu", href);

    if (sidebar.classList.contains("hide")) {
      e.stopPropagation();
    }
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


window.addEventListener("resize", function () {
  if (this.innerWidth > 760) {
    sidebar.classList.remove("hide");
  }
});

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
