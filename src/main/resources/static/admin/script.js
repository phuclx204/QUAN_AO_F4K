const allSideMenu = document.querySelectorAll("#sidebar .side-menu.top li a");

allSideMenu.forEach((item) => {
  const li = item.parentElement;

  item.addEventListener("click", function () {
    allSideMenu.forEach((i) => {
      i.parentElement.classList.remove("active");
    });
    li.classList.add("active");
  });
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
    // Toggle moon icon visible and sun icon hidden
    document.querySelector(".bx.bx-moon").style.display = "inline-block";
    document.querySelector(".bx.bx-moon").style.filter =
      "drop-shadow(0 0 1px rgb(255, 255, 255))";
    document.querySelector(".bx.bxs-sun").style.display = "none";
  } else {
    document.body.classList.remove("dark");
    // Toggle moon icon hidden and sun icon visible
    document.querySelector(".bx.bx-moon").style.display = "none";
    document.querySelector(".bx.bxs-sun").style.display = "inline-block";
  }
});



// call api brand
function loadContent(page) {
  console.log("Loading content for:", page);
  const mainContent = document.querySelector('main');

  fetch(page) // Lấy tệp brand.html

      .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.text();
      })
      .then(data => {
        mainContent.innerHTML = data; // Thay thế nội dung của phần main
        if (page === 'brand.html') {
          loadBrandData(); // Gọi hàm để tải dữ liệu thương hiệu
        }
      })
      .catch(error => console.error('Error loading content:', error));
}

function loadBrandData() {
  const tbody = document.querySelector('#brandTable tbody');

  fetch('http://localhost:8080/api/brand') // Đường dẫn API để lấy danh sách thương hiệu
      .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.json(); // Giả sử bạn trả về dữ liệu JSON
      })
      .then(data => {
        tbody.innerHTML = '';
        data.forEach(brand => {
          const row = document.createElement('tr');
          row.innerHTML = `
                    <td>${brand.id}</td>
                    <td>${brand.name}</td>
                `;
          tbody.appendChild(row);
        });
      })
      .catch(error => console.error('Error loading brand data:', error));
}
loadBrandData()
