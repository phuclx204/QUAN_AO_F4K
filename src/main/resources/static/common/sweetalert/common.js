function $alterTop(type = 'success' | 'warning' | 'error' | 'info' | 'question', title, position = 'top') {
    const Toast = Swal.mixin({
        toast: true,
        width: 'auto',
        position: position,
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
        }
    });

    Toast.fire({
        icon: type,
        title: title
    });
}

function $alter(type, title, text) {
    Swal.fire({
        title: title,
        text: text,
        icon: type
    });
}

function $confirm(type, text, title = 'Nhắc nhở') {
    return Swal.fire({
        title: title,
        text: text,
        icon: type,
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes",
        cancelButtonText: "No",
    })
}

