// var p = o(121), h = o.n(p);
// document.addEventListener("DOMContentLoaded", () => {
//     var e = document.querySelectorAll(".filter-price") || [];
//     const t = e => {
//         const t = e.closest(".widget-filter-price");
//         h.a.create(e, {
//             start: [60, 900],
//             connect: !0,
//             tooltips: [!0, !0],
//             range: {min: 0, max: 2e3},
//             pips: {mode: "values", values: [0, 250, 500, 750, 1e3], density: 100}
//         });
//         var o = !!t && t.querySelector(".filter-min"), n = !!t && t.querySelector(".filter-max");
//         const s = [o, n];
//         e.noUiSlider.on("update", (function (e, t) {
//             s[t].value = e[t]
//         })), o.addEventListener("change", (function () {
//             e.noUiSlider.set([this.value, null])
//         })), n.addEventListener("change", (function () {
//             e.noUiSlider.set([null, this.value])
//         }))
//     };
//     e.forEach(e => {
//         t(e)
//     })
// });