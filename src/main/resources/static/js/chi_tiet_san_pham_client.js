// Sự kiện hiển thị màu sắc khi click vào Rom
// document.addEventListener("DOMContentLoaded", function() {
//     let buttons = document.querySelectorAll('a[data-colors]');
//     let selectedButton = null;
//     const idKhachHang = document.getElementById('idKhachHang').value;
//     function formatCurrency(number) {
//         if (isNaN(number)) {
//             return '0đ';
//         }
//         let formattedNumber = new Intl.NumberFormat('vi-VN').format(Number(number));
//         return formattedNumber + 'đ';
//     }
//
//     function selectButton(button) {
//         if (selectedButton) {
//             selectedButton.classList.remove('selected');
//         }
//         button.classList.add('selected');
//         selectedButton = button;
//
//         let colors = button.getAttribute('data-colors').replace(/[\[\]]/g, '').split(',');
//         let colorCodes = button.getAttribute('data-color-codes').replace(/[\[\]]/g, '').split(',');
//         let ids = button.getAttribute('data-ids').replace(/[\[\]]/g, '').split(',');
//         let prices = button.getAttribute('data-prices').replace(/[\[\]]/g, '').split(',');
//         let colorDisplay = document.getElementById('colorDisplay');
//         let hiddenColorInput = document.getElementById('selectedColorId');
//         let hiddenRomInput = document.getElementById('selectedRomId');
//         let priceDisplay = document.getElementById('priceDisplay');
//
//         colorDisplay.innerHTML = '';
//         priceDisplay.innerHTML = '';
//
//         hiddenRomInput.value = button.getAttribute('data-rom-id');
//
//         colors.forEach(function(color, index) {
//             let colorCode = colorCodes[index].trim();
//             let id = ids[index].trim();
//             let price = prices[index];
//
//             let colorBlock = document.createElement('div');
//             colorBlock.style.backgroundColor = colorCode;
//             colorBlock.style.width = '30px';
//             colorBlock.style.height = '30px';
//             colorBlock.style.borderRadius = '5px';
//             colorBlock.style.borderColor = '#ffffff';
//             colorBlock.style.display = 'inline-block';
//             colorBlock.style.marginRight = '10px';
//             colorBlock.setAttribute('data-id', id);
//             colorBlock.setAttribute('data-price', price);
//             colorBlock.addEventListener('click', function() {
//                 hiddenColorInput.value = this.getAttribute('data-id');
//                 let priceValue = this.getAttribute('data-price');
//                 priceDisplay.innerText = formatCurrency(priceValue);
//             });
//             colorDisplay.appendChild(colorBlock);
//
//             let colorText = document.createElement('p');
//             colorText.innerText = color.trim();
//             colorText.style.display = 'inline-block';
//             colorText.style.marginRight = '10px';
//             colorDisplay.appendChild(colorText);
//         });
//
//         if (colors.length > 0 && prices.length > 0) {
//             hiddenColorInput.value = ids[0].trim();
//             priceDisplay.innerText = formatCurrency(prices[0]);
//         }
//     }
//
//     buttons.forEach(function(button) {
//         button.addEventListener('click', function(event) {
//             event.preventDefault();
//             selectButton(this);
//         });
//     });
//
//     if (buttons.length > 0) {
//         selectButton(buttons[0]);
//     }
//
//     document.getElementById("buyNowButton").addEventListener("click", function(event) {
//         event.preventDefault();
//         const selectedColorId = document.getElementById('selectedColorId').value;
//         const selectedRomId = document.getElementById('selectedRomId').value;
//         const productId = document.getElementById('productId').value;
//
//         if (!selectedColorId || !selectedRomId) {
//             alert("Vui lòng chọn phiên bản và màu sắc.");
//             return;
//         }
//
//         fetch('/add-to-cart', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify({
//                 romId: selectedRomId,
//                 colorId: selectedColorId,
//                 productId: productId
//             })
//         }).then(response => {
//             if (response.ok) {
//                 return response.text();
//             }
//             throw new Error('Something went wrong');
//         }).then(data => {
//             console.log(data);
//             if (data.includes("Thêm thành công sản phẩm vào giỏ hàng")) {
//                 window.location.href = "/gio-hang/" + idKhachHang;
//             } else {
//                 alert(data);
//             }
//         }).catch(error => {
//             console.error(error);
//             alert("Đã xảy ra lỗi, vui lòng thử lại.");
//         });
//     });
// });
document.addEventListener("DOMContentLoaded", function() {
    let buttons = document.querySelectorAll('a[data-colors]');
    let selectedButton = null;
    const idKhachHang = document.getElementById('idKhachHang').value;

    function formatCurrency(number) {
        if (isNaN(number)) {
            return '0đ';
        }
        let formattedNumber = new Intl.NumberFormat('vi-VN').format(Number(number));
        return formattedNumber + 'đ';
    }

    function selectButton(button) {
        if (selectedButton) {
            selectedButton.classList.remove('selected');
        }
        button.classList.add('selected');
        selectedButton = button;

        let colors = button.getAttribute('data-colors').replace(/[\[\]]/g, '').split(',');
        let colorCodes = button.getAttribute('data-color-codes').replace(/[\[\]]/g, '').split(',');
        let ids = button.getAttribute('data-ids').replace(/[\[\]]/g, '').split(',');
        let prices = button.getAttribute('data-prices').replace(/[\[\]]/g, '').split(',');
        let colorDisplay = document.getElementById('colorDisplay');
        let hiddenColorInput = document.getElementById('selectedColorId');
        let hiddenRomInput = document.getElementById('selectedRomId');
        let priceDisplay = document.getElementById('priceDisplay');

        colorDisplay.innerHTML = '';
        priceDisplay.innerHTML = '';

        hiddenRomInput.value = button.getAttribute('data-rom-id');

        colors.forEach(function(color, index) {
            let colorCode = colorCodes[index].trim();
            let id = ids[index].trim();
            let price = prices[index];

            // Tạo thẻ chứa cho cả khối màu, tên màu và giá
            let colorContainer = document.createElement('div');
            colorContainer.style.display = 'inline-block';
            colorContainer.style.padding = '10px';
            colorContainer.style.border = '2px solid #d6727a'; // Thêm thuộc tính border
            colorContainer.style.marginRight = '10px';
            colorContainer.style.marginLeft = '20px';
            colorContainer.style.borderRadius = '5px';
            colorContainer.style.textAlign = 'center'; // Căn giữa nội dung

            let colorBlock = document.createElement('div');
            colorBlock.style.backgroundColor = colorCode;
            colorBlock.style.width = '30px';
            colorBlock.style.height = '30px';
            colorBlock.style.borderRadius = '5px';
            colorBlock.style.border = '1px solid #fff';
            colorBlock.style.display = 'inline-block';
            colorBlock.style.marginBottom = '5px';
            colorBlock.setAttribute('data-id', id);
            colorBlock.setAttribute('data-price', price);
            colorBlock.addEventListener('click', function() {
                hiddenColorInput.value = this.getAttribute('data-id');
                let priceValue = this.getAttribute('data-price');
                priceDisplay.innerText = formatCurrency(priceValue);
            });

            let colorText = document.createElement('p');
            colorText.innerText = color.trim();
            colorText.style.display = 'block';
            colorText.style.margin = '5px 0';

            let priceText = document.createElement('p');
            priceText.innerText = formatCurrency(price);
            priceText.style.display = 'block';
            priceText.style.margin = '5px 0';

            colorContainer.appendChild(colorBlock);
            colorContainer.appendChild(colorText);
            colorContainer.appendChild(priceText);
            colorDisplay.appendChild(colorContainer);
        });

        if (colors.length > 0 && prices.length > 0) {
            hiddenColorInput.value = ids[0].trim();
            priceDisplay.innerText = formatCurrency(prices[0]);
        }
    }

    buttons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            selectButton(this);
        });
    });

    if (buttons.length > 0) {
        selectButton(buttons[0]);
    }

    document.getElementById("buyNowButton").addEventListener("click", function(event) {
        event.preventDefault();
        const selectedColorId = document.getElementById('selectedColorId').value;
        const selectedRomId = document.getElementById('selectedRomId').value;
        const productId = document.getElementById('productId').value;

        if (!selectedColorId || !selectedRomId) {
            alert("Vui lòng chọn phiên bản và màu sắc.");
            return;
        }
        fetch('/add-to-cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                romId: selectedRomId,
                colorId: selectedColorId,
                productId: productId
            })
        }).then(response => {
            return response.text().then(text => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}, message: ${text}`);
                }
                return text;
            });
        }).then(data => {
            console.log(data);
                window.location.href = "/gio-hang";
        }).catch(error => {
            console.error('Error details:', error); // In ra chi tiết lỗi
            alert("Đã xảy ra lỗi, vui lòng thử lại.");
        });

    });
});



//
// Lắng nghe sự kiện change của các checkbox
document.querySelectorAll('.product-checkbox').forEach(checkbox => {
    checkbox.addEventListener('change', function() {
        calculateTotalPrice(); // Gọi hàm tính toán tổng tiền khi có thay đổi
    });
});

function calculateTotalPrice() {
    let totalPrice = 0;

    // Lặp qua các checkbox đã chọn và tính tổng giá trị
    document.querySelectorAll('.product-checkbox:checked').forEach(checkbox => {
        let price = parseFloat(checkbox.value); // Lấy giá trị của checkbox và chuyển đổi thành số
        totalPrice += price; // Cộng vào tổng tiền tạm tính
    });

    // Hiển thị tổng tiền tạm tính ở dạng định dạng tiền tệ
    let formattedPrice = formatCurrency(totalPrice);
    document.getElementById('temporaryTotalPrice').innerText = formattedPrice;
}

// Hàm định dạng tiền tệ
function formatCurrency(number) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(number).replace('₫', 'đ');
}

// Xóa khỏi giỏ hàng
document.addEventListener('DOMContentLoaded', function() {
    // Lắng nghe sự kiện click vào thẻ a có class là 'delete-from-cart'
    document.querySelectorAll('.delete-from-cart').forEach(anchor => {
        anchor.addEventListener('click', function(event) {
            event.preventDefault();

            // Lấy giá trị id từ thuộc tính data-id
            let gioHangChiTietId = this.getAttribute('data-id');
            const idKhachHang = document.getElementById('idKhachHang').value;

            // Gọi API để xóa sản phẩm khỏi giỏ hàng
            fetch('/delete-to-cart/' + gioHangChiTietId, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Something went wrong');
            }).then(data => {
                console.log('Xóa thành công');
                window.location.href = "/gio-hang";
            }).catch(error => {
                console.error('Xóa lỗi!', error);
                // Xử lý lỗi nếu có
            });
        });
    });
});
// Sự kiện gửi list sản phẩm để thanh toán
document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.btn-mua-ngay').addEventListener('click', function(event) {
        event.preventDefault(); // Ngăn chặn hành động mặc định của liên kết
        const idKhachHang = document.getElementById('idKhachHang').value;

        let selectedProductIds = [];

        // Lặp qua các checkbox đã chọn và thu thập ID sản phẩm
        document.querySelectorAll('.product-checkbox:checked').forEach(checkbox => {
            console.log('Checkbox:', checkbox); // Kiểm tra checkbox

            // Tìm phần tử cha gần nhất chứa input ẩn với id="idCTSP"
            let trElement = checkbox.closest('tr');
            console.log('trElement:', trElement); // Kiểm tra phần tử tr

            if (trElement) {
                let hiddenInput = trElement.querySelector('#idCTSP');
                console.log('hiddenInput:', hiddenInput); // Kiểm tra input ẩn

                if (hiddenInput) {
                    let productId = hiddenInput.value; // Lấy giá trị từ input ẩn
                    selectedProductIds.push(productId);
                }
            }
        });
        alert("Khách hàng: "+idKhachHang+", Sản phẩm: "+selectedProductIds);
        console.log('Selected Product IDs:', selectedProductIds); // Kiểm tra danh sách ID sản phẩm đã chọn

        sendSelectedProductsToController(idKhachHang, selectedProductIds);
    });

    function sendSelectedProductsToController(idKhachHang, productIds) {
        let url = `/thanh-toan?productIds=${productIds.join(',')}`;

        // Thêm idKhachHang vào URL nếu nó không null và không phải là chuỗi 'null'
        if (idKhachHang && idKhachHang !== 'null') {
            url += `&idKhachHang=${idKhachHang}`;
        }
        // Body của request
        const body = {
            idKhachHang: idKhachHang ? parseInt(idKhachHang) : null, // Chuyển đổi idKhachHang thành số nguyên nếu có, nếu không thì để null
            productIds: productIds.map(id => parseInt(id)) // Chuyển đổi mỗi phần tử trong productIds thành số nguyên
        };


        fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body) // Chuyển đổi body thành dạng JSON
            })
            .then(response => {
                return response.json(); // Giả định server luôn trả về JSON
            })
            .then(data => {
                if (data.success) {
                    window.location.href = data.redirectUrl;
                } else {
                    alert("Error: " + data.message);
                }
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }




    // function sendSelectedProductsToController(idKhachHang, productIds) {
    //     // Tạo URL endpoint với idKhachHang
    //     let url = `/thanh-toan`;
    //
    //     // Body của request
    //     const body = {
    //         idKhachHang: parseInt(idKhachHang), // Chuyển đổi idKhachHang thành số nguyên
    //         productIds: productIds.map(id => parseInt(id)) // Chuyển đổi mỗi phần tử trong productIds thành số nguyên
    //     };
    //
    //     fetch(url, {
    //         method: 'POST',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         },
    //         body: JSON.stringify(body) // Chuyển đổi body thành dạng JSON
    //     })
    //         .then(response => {
    //             if (!response.ok) {
    //                 throw new Error('Network response was not ok');
    //             }
    //             return response.json();
    //         })
    //         .then(data => {
    //             // Chuyển hướng sang trang /thanh-toan/{idKhachHang}
    //             window.location.href = `/thanh-toan-gio-hang/${idKhachHang}`;
    //         })
    //         .catch((error) => {
    //             console.error('Error:', error);
    //         });
    // }

});
// Chọn phương thức thanh toán
function selectPaymentMethod(methodId) {
// Reset tất cả các phần tử đang được chọn
    $('#thanhToanNhanHang').removeClass('selected');
    $('#vnPay').removeClass('selected');

// Đánh dấu phần tử được click là đã chọn
    $('#' + methodId).addClass('selected');

// Thực hiện các thao tác cần thiết khi chọn phương thức thanh toán
// Ví dụ: hiển thị hoặc ẩn các phần tử khác, cập nhật biến trạng thái, vv.
}
