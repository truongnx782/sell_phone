// Ẩn/hiện form thanh toán bổ sung
window.onload = function () {
    if (document.getElementById('totalAmount')) {
        const totalAmountElement = document.getElementById('totalAmount').textContent;
        const totalAmount = parseFloat(totalAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const payAmountElement = document.getElementById('pay-amount').textContent;
        const payAmount = parseFloat(payAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const paymentInfoDiv = document.getElementById('payment-info');

        if (totalAmount - payAmount !== 0) {
            paymentInfoDiv.style.display = 'block';
        } else {
            paymentInfoDiv.style.display = 'none';
        }
    }

};

// Cập nhật trạng thái thanh toán
function updatePaymentStatus(totalAmount, discountAmount, payAmount) {
    const trangThaiTT1 = document.getElementById('trangThaiThanhToan1');
    const trangThaiTT2 = document.getElementById('trangThaiThanhToan2');

    if ((totalAmount - discountAmount - payAmount) > 0) {
        trangThaiTT1.innerText = 'Trạng thái: Khách hàng nợ cửa hàng';
        trangThaiTT2.innerText = '* Note: Yêu cầu shipper phụ thu thêm bên phía khách hàng [ ' +
            formatCurrency(totalAmount - discountAmount - payAmount) + ' ] trước khi hoàn thành đơn hàng';
    } else if ((totalAmount - discountAmount - payAmount) < 0) {
        trangThaiTT1.innerText = 'Trạng thái: Cửa hàng nợ khách hàng';
        trangThaiTT2.innerText = '* Note: Nhân viên cần hoàn trả tiền thừa lại cho khách hàng';
    }
}

// Định dạng số tiền thành tiền tệ Việt Nam
function formatCurrency(amount) {
    return `${amount.toLocaleString('vi-VN')} đ`;
}

// Tính tổng số tiền
function tinhTongTien() {
    let totalAmount = 0;
    document.querySelectorAll('.product-price').forEach(priceElement => {
        const price = parseInt(priceElement.textContent.replace(/,/g, '').replace(' đ', ''));
        totalAmount += isNaN(price) ? 0 : price;
    });
    return totalAmount;
}

// Cập nhật tổng tiền và voucher
function updateTotalAndVouchers() {
    if (document.getElementById('apply-voucher-button')) {
        const khachHangId = document.getElementById('khach-hang-id').value;
        const totalAmount = tinhTongTien();
        const discountAmount = getDiscountAmount();

        updateDisplayAmounts(totalAmount, discountAmount);
        updatePaymentInfo(totalAmount, discountAmount);

        fetchBestDiscountVouchers(totalAmount, khachHangId)
            .then(vouchers => {
                populateVoucherSelect(vouchers, totalAmount);
                applyCurrentVoucher(vouchers, totalAmount);
            });
    }
}

// Lấy số tiền giảm giá
function getDiscountAmount() {
    const discountAmountElement = document.getElementById('discount-amount');
    return parseInt(discountAmountElement.textContent.replace(/,/g, '').replace(/[^0-9.-]+/g, '')) || 0;
}

// Cập nhật hiển thị số tiền
function updateDisplayAmounts(totalAmount, discountAmount) {
    const formattedTotalAmount = formatCurrency(totalAmount);
    const formattedPayAmount = formatCurrency(totalAmount - discountAmount);

    document.getElementById('total-amount').textContent = formattedTotalAmount;
    document.getElementById('total-pay-amount').textContent = formattedPayAmount;

    const totalAmountElement = document.getElementById('totalAmount');
    if (totalAmountElement) {
        totalAmountElement.textContent = formattedPayAmount;
        const payAmount = parseFloat(document.getElementById('pay-amount').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const totalPayAmount = parseFloat(document.getElementById('total-pay-amount').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));

        document.getElementById('price_difference').textContent = formatCurrency(totalPayAmount - payAmount);
    }
    document.getElementById('discount-amount').innerText = formatCurrency(discountAmount);
}

// Cập nhật thông tin thanh toán
function updatePaymentInfo(totalAmount, discountAmount) {
    if (document.getElementById('pay-amount')) {
        const payAmount = parseFloat(document.getElementById('pay-amount').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const paymentInfoDiv = document.getElementById('payment-info');
        if (totalAmount - discountAmount - payAmount !== 0) {
            paymentInfoDiv.style.display = 'block';
        } else {
            paymentInfoDiv.style.display = 'none';
        }
        updatePaymentStatus(totalAmount, discountAmount, payAmount);
    }
}

// Lấy danh sách voucher giảm giá tốt nhất
function fetchBestDiscountVouchers(totalAmount,khachHangId) {

    return fetch('/ban-hang-tai-quay/getBestDiscountVoucher', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({totalAmount,khachHangId }),
    })
        .then(response => response.json())
        .then(vouchers => {
            console.log('Vouchers:', vouchers);
            return vouchers;
        });
}

// Hiển thị danh sách voucher lên select option
function populateVoucherSelect(vouchers, totalAmount) {
    const discountCodeSelect = document.getElementById('discount-code-select');
    discountCodeSelect.innerHTML = '<option value="">Không áp dụng voucher</option>';

    if (vouchers.length > 0) {
        vouchers.sort((a, b) => {
            const discountA = getVoucherDiscount(a, totalAmount);
            const discountB = getVoucherDiscount(b, totalAmount);
            return discountB - discountA;
        });

        vouchers.forEach(voucher => {
            const option = document.createElement('option');
            option.value = voucher.id;
            option.textContent = `${voucher.tenPhieuGiamGia} -
             Giảm ${(voucher.phanTramGiam > 0 && voucher.phanTramGiam != null) ? voucher.phanTramGiam + '%' : 
                voucher.soTienDuocGiam.toLocaleString() + ' đ'}`;
            discountCodeSelect.appendChild(option);
        });

        discountCodeSelect.value = vouchers[0].id;
    }
}

// Lấy số tiền giảm giá của voucher
function getVoucherDiscount(voucher, totalAmount) {
    return (voucher.phanTramGiam > 0 && voucher.phanTramGiam != null) ?
        totalAmount * voucher.phanTramGiam / 100 : voucher.soTienDuocGiam;
}

// Áp dụng voucher hiện tại
function applyCurrentVoucher(vouchers, totalAmount) {
    const currentVoucherId = document.getElementById('current-voucher-id').value;
    const currentVoucher = vouchers.find(voucher => voucher.id.toString() === currentVoucherId.toString());
    applyVoucher(currentVoucher, totalAmount);
}

// Áp dụng voucher
function applyVoucher(voucher, totalAmount) {
    let discountAmount = 0;
    if (voucher) {
        discountAmount = getVoucherDiscount(voucher, totalAmount);

        if ((voucher.soTienDuocGiamToiDa != null && voucher.soTienDuocGiamToiDa > 0) &&
            discountAmount > voucher.soTienDuocGiamToiDa) {
            discountAmount = voucher.soTienDuocGiamToiDa;
        }

        discountAmount = Math.round(discountAmount * 100) / 100;
    }

    updateDisplayAmounts(totalAmount, discountAmount);
    updatePaymentInfo(totalAmount, discountAmount);
}

// Cập nhật voucher của đơn hàng
function updateOrderVoucher(orderId, voucherId) {
    if (document.getElementById('apply-voucher-button')) {
        const totalAmount = parseFloat(document.getElementById('total-amount').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const totalPayAmount = parseFloat(document.getElementById('total-pay-amount').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));

        return fetch(`/ban-hang-tai-quay/updateOrderVoucher/${orderId}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                voucherId,
                tongTien: totalAmount,
                tongTienSG: totalPayAmount,
            }),
        })
            .then(response => {
                if (response.ok) {
                    console.log('Voucher updated successfully');
                } else {
                    console.error('Failed to update voucher');
                }
            })
            .catch(error => console.error('Error:', error));
    }
}


document.addEventListener('DOMContentLoaded', function () {
    const voucherApplied = localStorage.getItem('voucherApplied') === 'true';
    if (voucherApplied) {
        enableConfirmPaymentButton();
    }
});

// Xử lý sự kiện click nút "Áp dụng" voucher
if (document.getElementById('apply-voucher-button')) {
    document.getElementById('apply-voucher-button').addEventListener('click', function () {
        const discountCodeSelect = document.getElementById('discount-code-select');
        const selectedVoucherId = discountCodeSelect.value;
        const orderId = discountCodeSelect.getAttribute('data-order-id');
        const voucherId = selectedVoucherId ? selectedVoucherId : null;

        updateOrderVoucher(orderId, voucherId)
            .then(() => {
                localStorage.setItem('voucherApplied', 'true');
                location.reload();
            })
            .catch(error => {
                console.error('Lỗi áp dụng voucher:', error);
            });
    });

}

// Hàm enable nút "Xác nhận thanh toán"
function enableConfirmPaymentButton() {
    if (document.getElementById('confirmPaymentButton')) {
        const confirmPaymentButton = document.getElementById('confirmPaymentButton');
        confirmPaymentButton.removeAttribute('disabled');
    }
    localStorage.removeItem('voucherApplied')
}

updateTotalAndVouchers();

//======================================================================================================================
function completeOrder() {
    var note = document.getElementById('completeNote').value;
    var hoaDonId = document.getElementById('completeButton').getAttribute('data-hoa-don-id');
    // Lấy tổng tiền và tổng tiền sau giảm
    const totalAmountElement = document.getElementById('total-amount1').textContent;
    const totalAmount = parseFloat(totalAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
    const tongTienSauGiamElement = document.getElementById('total-pay-amount1').textContent;
    const tongTienSauGiam = parseFloat(tongTienSauGiamElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));

    var url = '/quan-ly-don-hang/complete-order/' + hoaDonId +
        '?note=' + encodeURIComponent(note) +
        '&totalAmount=' + totalAmount +
        '&tongTienSauGiam=' + tongTienSauGiam;
    window.location.href = url;
}


// Complete đơn hàng
if (document.getElementById('completeButton')) {
    document.getElementById('completeButton').addEventListener('click', function () {
        if (document.getElementById("totalAmount")) {
            const price_difference = parseFloat(document.getElementById('price_difference').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
            if (price_difference <= 0) {
                $('#completeModal').modal('show');
            } else {
                $('#confirmTTPhu').modal('show');
                document.getElementById('confirm-payment-modal-btn2').addEventListener('click', function () {
                    const orderId = document.getElementById('id-hoa-don').getAttribute('data-order-id');
                    const paymentDetails = {
                        idHoaDon: {id: orderId},
                        idHinhThuc: {id: 1},
                        tongTien: Math.abs(price_difference),
                        trangThai: 3,// Trạng thái 3 là khi khách hàng nợ cửa hàng
                        ghiChu: 'Tiền khách hàng thanh toán bổ sung cho cửa hàng'
                    };

                    fetch('/quan-ly-don-hang/payment-details', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(paymentDetails)
                    })
                        .then(response => response.text())
                        .then(data => {
                            if (data === "Thanh toán thành công") {
                                $('#confirmPayment').modal('hide');

                                completeOrder();

                            } else {
                                console.log('Thanh toán thất bại')
                            }
                        })
                        .catch(error => console.error('Error:', error));
                    window.location.reload();
                });
            }
        } else {
            $('#completeModal').modal('show');
        }
    });
}


//======================================================================================================================
if (document.getElementById('confirmOrderButton')) {
    document.getElementById('confirmOrderButton').addEventListener('click', function () {
        if (document.getElementById('totalAmount')) {
            const price_difference = parseFloat(document.getElementById('price_difference').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
            if (price_difference < 0) {
                $('#confirmTTTien').modal('show');

            } else {
                $('#confirmModal').modal('show');
            }
        } else {
            $('#confirmModal').modal('show');
        }

    });
}
if (document.getElementById('confirmPaymentButton')) {
    document.getElementById('confirmPaymentButton').addEventListener('click', function () {
        const totalPayment = parseFloat(document.getElementById('pay-amount').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const totalAmount = parseFloat(document.getElementById('totalAmount').textContent.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const priceDifference = totalAmount - totalPayment;

        if (priceDifference > 0) {
            // Modal khi khách hàng nợ cửa hàng
            $('#confirmPayment').modal('show');

            document.getElementById('confirm-payment-modal-btn1').addEventListener('click', function () {
                const orderId = document.getElementById('discount-code-select').getAttribute('data-order-id');
                const paymentDetails = {
                    idHoaDon: {id: orderId},
                    idHinhThuc: {id: 2},
                    tongTien: Math.abs(priceDifference),
                    trangThai: 3,// Trạng thái 3 là khi khách hàng nợ cửa hàng
                    ghiChu: 'Tiền khách hàng thanh toán bổ sung cho cửa hàng'
                };

                fetch('/quan-ly-don-hang/payment-details', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(paymentDetails)
                })
                    .then(response => response.text())
                    .then(data => {
                        if (data === "Thanh toán thành công") {
                            $('#confirmPayment').modal('hide');
                            console.log('Thanh toán bổ sung đã được xác nhận.')
                            location.reload();
                        } else {
                            console.log('Thanh toán thất bại')
                        }
                    })
                    .catch(error => console.error('Error:', error));
            });
        } else {
            // Modal khi cửa hàng nợ khách hàng
            $('#confirmPaymentModal').modal('show');

            document.getElementById('confirm-payment-modal-btn').addEventListener('click', function () {
                const orderId = document.getElementById('discount-code-select').getAttribute('data-order-id');
                const paymentDetails = {
                    idHoaDon: {id: orderId},
                    idHinhThuc: {id: 2}, // Trạng thái 2 là khi cửa hàng nợ khách hàng
                    tongTien: Math.abs(priceDifference),
                    trangThai: 2,
                    ghiChu: 'Tiền cửa hàng hoàn trả khách hàng'
                };

                fetch('/quan-ly-don-hang/payment-details', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(paymentDetails)
                })
                    .then(response => response.text())
                    .then(data => {
                        if (data == "Thanh toán thành công") {
                            $('#confirmPaymentModal').modal('hide');

                            console.log('Thanh toán bổ sung đã được xác nhận.')

                            location.reload();
                        } else {
                            console.log('Thanh toán thất bại')
                        }
                    })
                    .catch(error => console.error('Error:', error));
            });
        }
    });
}


//======================================================================================================================


function confirmOrder() {
    var note = document.getElementById('confirmationNote').value;
    var confirmButton = document.getElementById('confirmOrderButton');
    var hoaDonId = confirmButton.getAttribute('data-hoa-don-id');

    // Lấy tổng tiền và tổng tiền sau giảm
    const totalAmountElement = document.getElementById('total-amount').textContent;
    const totalAmount = parseFloat(totalAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
    const tongTienSauGiamElement = document.getElementById('total-pay-amount').textContent;
    const tongTienSauGiam = parseFloat(tongTienSauGiamElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));

    var url = '/quan-ly-don-hang/confirm-order/' + hoaDonId +
        '?note=' + encodeURIComponent(note) +
        '&tongTien=' + encodeURIComponent(totalAmount) +
        '&tongTienSG=' + encodeURIComponent(tongTienSauGiam);

    sessionStorage.setItem('printBill', 'true');

    window.location.href = url;
}

//=====================================================================================================================
// Tự động in hóa đơn sau khi xác nhận đơn hàng
window.onload = function () {
    if (sessionStorage.getItem('printBill') === 'true') {
        document.getElementById('printableBill').style.display = 'block';
        window.print();
        sessionStorage.removeItem('printBill');
    }
}
$('#inHoaDon').click(function () {
    var printableBill = document.getElementById('printableBill');
    printableBill.style.display = 'block';

    window.print();

    printableBill.style.display = 'none';
});

//=====================================================================================================================
function shipOrder() {
    var note = document.getElementById('shipNote').value;
    var hoaDonId = document.querySelector('[data-bs-target="#shipModal"]').getAttribute('data-hoa-don-id');

    // Lấy tổng tiền và tổng tiền sau giảm
    const totalAmountElement = document.getElementById('total-amount').textContent;
    const totalAmount = parseFloat(totalAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
    const tongTienSauGiamElement = document.getElementById('total-pay-amount').textContent;
    const tongTienSauGiam = parseFloat(tongTienSauGiamElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));


    var url = '/quan-ly-don-hang/ship-order/' + hoaDonId +
        '?note=' + encodeURIComponent(note) +
        '&totalAmount=' + totalAmount +
        '&tongTienSauGiam=' + tongTienSauGiam;
    window.location.href = url;
}


//======================================================================================================================
function checkInputUndo() {
    const minLength = 30;
    const textarea = document.getElementById("confirmGhiChu");
    const confirmButton = document.getElementById("confirmButton");
    const errorMessage = document.getElementById("errorMss");

    if (textarea.value.trim().length < minLength) {
        confirmButton.disabled = true;
        errorMessage.style.display = "block";
    } else {
        confirmButton.disabled = false;
        errorMessage.style.display = "none";
    }
}

function checkInputCancel() {
    const minLength = 30;
    const textarea = document.getElementById("confirmDescription");
    const confirmButton = document.getElementById("confirmCancel");
    const errorMessage = document.getElementById("errorMssCancel");

    if (textarea.value.trim().length < minLength) {
        confirmButton.disabled = true;
        errorMessage.style.display = "block";
    } else {
        confirmButton.disabled = false;
        errorMessage.style.display = "none";
    }
}

//======================================================================================================================


document.getElementById('imeiProduct').addEventListener('change', function (event) {
    if (event.target.classList.contains('form-check-input')) {
        const checkbox = event.target;
        const row = checkbox.closest('tr');
        const imeiCell = row.querySelector('td:first-child');
        const imei = imeiCell.innerText;
        const isChecked = checkbox.checked;

        if (isChecked) {
            addImei(imei);
        } else {
            removeImei(imei);
        }
    }
});

// Function thêm imei vào danh sách imei đã chọn
function addImei(imei) {
    const selectedImeiList = document.getElementById('selectedImeiList');
    const imeiItem = document.createElement('div');
    imeiItem.classList.add('selected-imei');
    imeiItem.innerText = imei;
    selectedImeiList.appendChild(imeiItem);
    console.log(imei)
}

// Function xóa imei ra khỏi danh sách imei đã chọn
function removeImei(imei) {
    const selectedImeiList = document.getElementById('selectedImeiList');
    const selectedImeiItems = selectedImeiList.querySelectorAll('.selected-imei');
    selectedImeiItems.forEach(item => {
        if (item.innerText === imei) {
            item.remove();
        }
    });
}

document.getElementById('imeiProduct').addEventListener('hidden.bs.modal', function () {
    const selectedImeiList = document.getElementById('selectedImeiList');
    selectedImeiList.innerHTML = '';
});


//======================================================================================================================


// Hiển thị danh dách imei theo idChiTietSP
let currentProductId;
let currentPage = 0;
let totalPages = 0;

// Hiển thị imei lên modal
function loadImeis(button, page = 0) {
    currentProductId = button.getAttribute('data-product-id');
    fetchImeis(page);
}

// Lấy ra danh sách imei
function fetchImeis(page) {
    const url = new URL('/ban-hang-tai-quay/getImeis', window.location.origin);
    url.searchParams.append('productId', currentProductId);
    url.searchParams.append('page', page);
    url.searchParams.append('size', 5);
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log('Data:', data);
            const imeiTableBody = document.getElementById('imeiTableBody');
            imeiTableBody.innerHTML = '';
            if (data.content) {
                data.content.forEach(imei => {
                    const row = document.createElement('tr');
                    row.innerHTML = `<td>${imei.maImei}</td><td><input class="form-check-input" type="checkbox" value="${imei.maImei}"></td>`;
                    imeiTableBody.appendChild(row);
                });
                totalPages = data.totalPages;
                currentPage = page;
                document.getElementById('pageInfo').innerText = ` ${currentPage + 1} / ${totalPages}`;
                document.getElementById('prevPage').disabled = currentPage === 0;
                document.getElementById('nextPage').disabled = currentPage === totalPages - 1;
            } else {
                console.error('data:', data);
            }
        })
        .catch(error => console.error('Lỗi get imei:', error));
}

function prevPage() {
    if (currentPage > 0) {
        fetchImeis(currentPage - 1);
    }
}

function nextPage() {
    if (currentPage < totalPages - 1) {
        fetchImeis(currentPage + 1);
    }
}

//======================================================================================================================
function addProductsToCart() {
    const totalAmountElement = document.getElementById('total-amount').textContent;
    const totalAmount = parseFloat(totalAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
    const tongTienSauGiamElement = document.getElementById('total-pay-amount').textContent;
    const tongTienSauGiam = parseFloat(tongTienSauGiamElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));

    const selectedImeiList = [];
    const checkboxes = document.querySelectorAll('.form-check-input:checked');
    checkboxes.forEach(checkbox => {
        selectedImeiList.push(checkbox.value);
    });

    var hoaDonId = document.querySelector('[data-bs-target="#addProduct"]').getAttribute('data-hoa-don-id');

    fetch(`/quan-ly-don-hang/addProductsToCart`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            imeiList: selectedImeiList,
            gioHangId: hoaDonId,
            tongTien: totalAmount,
            tongTienSG: tongTienSauGiam
        })
    })
        .then(response => response.text())
        .then(data => {
            if (data === "Thêm thành công sản phẩm vào giỏ hàng.") {
                checkboxes.forEach(checkbox => {
                    checkbox.checked = false;
                });
                $('#imeiProduct').modal('hide');
                window.location.reload();
            } else {
                console.error('Failed to add products to cart:', data);
            }
        })
        .catch(error => console.error('Error adding products to cart:', error));
}


//======================================================================================================================

let imeiToDelete = '';
let hoaDonIdToDelete = '';

function confirmDelete(element) {
    imeiToDelete = element.getAttribute("data-imei");
    hoaDonIdToDelete = element.getAttribute("data-hoa-don-id");
    $('#confirmDeleteModal').modal('show');
}


document.getElementById('confirm-delete-btn').addEventListener('click', function () {
    deleteProduct();
});

function deleteProduct() {
    const totalAmountElement = document.getElementById('total-amount').textContent;
    const totalAmount = parseFloat(totalAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
    const tongTienSauGiamElement = document.getElementById('total-pay-amount').textContent;
    const tongTienSauGiam = parseFloat(tongTienSauGiamElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));

    fetch('/quan-ly-don-hang/deleteProductFromCart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            imei: imeiToDelete,
            gioHangId: hoaDonIdToDelete,
            tongTien: totalAmount,
            tongTienSG: tongTienSauGiam
        })
    })
        .then(response => response.text())
        .then(data => {
            $('#confirmDeleteModal').modal('hide');
            if (data.includes("Xóa thành công")) {
                updateTotalAndVouchers();
                location.reload();


            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

//======================================================================================================================

// Hiển thị thông tin người nhận lên form
document.addEventListener('DOMContentLoaded', function () {
    const citis = document.getElementById("city");
    const district = document.getElementById("district");
    const ward = document.getElementById("ward");

    const cityValue = "${hoaDon.city}";
    const districtValue = "${hoaDon.district}";
    const wardValue = "${hoaDon.ward}";

    axios.get("https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json")
        .then(function (response) {
            data = response.data;
            renderCity(data, cityValue, districtValue, wardValue);
        });

    function renderCity(data, selectedCity, selectedDistrict, selectedWard) {
        data.forEach(city => {
            const opt = document.createElement('option');
            opt.value = city.Name;
            opt.text = city.Name;
            opt.setAttribute('data-id', city.Id);
            if (city.Name === selectedCity) {
                opt.selected = true;
            }
            citis.appendChild(opt);
        });

        if (selectedCity) {
            const cityId = citis.querySelector('option:checked').dataset.id;
            if (cityId) {
                const selectedCity = data.find(city => city.Id === cityId);
                renderDistricts(selectedCity.Districts, selectedDistrict, selectedWard);
            }
        }

        citis.onchange = function () {
            district.length = 1;
            ward.length = 1;
            const cityId = this.options[this.selectedIndex].dataset.id;
            if (cityId) {
                const selectedCity = data.find(city => city.Id === cityId);
                renderDistricts(selectedCity.Districts, '', '');
            }
        };
    }

    function renderDistricts(districts, selectedDistrict, selectedWard) {
        districts.forEach(districtItem => {
            const opt = document.createElement('option');
            opt.value = districtItem.Name;
            opt.text = districtItem.Name;
            opt.setAttribute('data-id', districtItem.Id);
            if (districtItem.Name === selectedDistrict) {
                opt.selected = true;
            }
            district.appendChild(opt);
        });

        if (selectedDistrict) {
            const districtId = district.querySelector('option:checked').dataset.id;
            if (districtId) {
                const selectedDistrict = districts.find(districtItem => districtItem.Id == districtId);
                if (selectedDistrict && selectedDistrict.Wards) {
                    renderWards(selectedDistrict.Wards, selectedWard);
                }
            }
        }

        district.onchange = function () {
            ward.length = 1;
            const cityId = citis.options[citis.selectedIndex].dataset.id;
            const districtId = this.options[this.selectedIndex].dataset.id;
            if (cityId && districtId) {
                const selectedCity = data.find(city => city.Id === cityId);
                const selectedDistrict = selectedCity.Districts.find(districtItem => districtItem.Id == districtId);
                if (selectedDistrict && selectedDistrict.Wards) {
                    renderWards(selectedDistrict.Wards, '');
                }
            }
        };
    }

    function renderWards(wards, selectedWard) {
        wards.forEach(wardItem => {
            const opt = document.createElement('option');
            opt.value = wardItem.Name;
            opt.text = wardItem.Name;
            if (wardItem.Name === selectedWard) {
                opt.selected = true;
            }
            ward.appendChild(opt);
        });
    }

});

// Cập nhật thông tin giao hàng (thông tin người nhận)
document.getElementById('confirm-update').addEventListener('click', function () {
    const recipientName = document.getElementById('recipient-name').value;
    const recipientPhone = document.getElementById('recipient-phone').value;
    const city = document.getElementById('city').value;
    const district = document.getElementById('district').value;
    const ward = document.getElementById('ward').value;
    const address = document.getElementById('address').value;

    const addressFull = `${city}, ${district}, ${ward}, ${address}`;

    var confirmButton = document.getElementById('confirm-update');
    var hoaDonId = confirmButton.getAttribute('data-hoa-don-id');

    const updateData = {
        hoaDonId: hoaDonId,
        tenNguoiNhan: recipientName,
        sdtNguoiNhan: recipientPhone,
        diaChi: addressFull
    };

    fetch('/quan-ly-don-hang/update-thong-tin-nguoi-nhan', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updateData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
               // alert('Cập nhật thông tin thành công.');
                $('#modalShipping').modal('hide');
                window.location.reload();
            } else {
                alert('Cập nhật thông tin thất bại.');
            }
        })
        .catch(error => console.error('Lỗi update thông tin người nhận:', error));
});


//Quét mã QR thêm sản phẩm vào giỏ hàng
$(document).ready(function () {
    let html5QrCode = new Html5Qrcode("qr-reader");

    function onScanSuccess(decodedText, decodedResult) {
        html5QrCode.stop().then(() => {
            addProductToCartByImei(decodedText);
        }).catch(err => {
            console.log(err);
        });
    }

    $('#qrModal').on('shown.bs.modal', function () {
        html5QrCode.start(
            {facingMode: "environment"},
            {
                fps: 10,
                qrbox: {width: 250, height: 250}
            },
            onScanSuccess)
            .catch(err => {
                console.log(err);
            });
    });

    $('#qrModal').on('hidden.bs.modal', function () {
        html5QrCode.stop().catch(err => {
            console.log(err);
        });
    });

    // Thêm imei vừa quét được vào giỏ hàng
    function addProductToCartByImei(imei) {
        const qrButton = document.getElementById('qrButton');
        const gioHangId = qrButton.getAttribute('data-hoa-don-id');

        fetch(`/ban-hang-tai-quay/addProductsToCart`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({imeiList: [imei], gioHangId: gioHangId})
        })
            .then(response => response.text())
            .then(data => {
                if (data === "Thêm thành công sản phẩm vào giỏ hàng.") {
                    $('#qrModal').modal('hide');
                    window.location.reload();
                } else {
                    alert(data);
                    $('#qrModal').modal('hide');
                }
            })
            .catch(error => console.error('Lỗi thêm imei vào giỏ hàng:', error));
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const currentTab = localStorage.getItem('currentTab');
    if (currentTab) {
        const tabElement = document.getElementById(currentTab);
        if (tabElement) {
            tabElement.click();
        }
    }

    // Lưu trạng thái tab hiện tại vào localStorage khi tab được chọn
    const tabLinks = document.querySelectorAll('[data-bs-toggle="tab"]');
    tabLinks.forEach(function (tabLink) {
        tabLink.addEventListener('shown.bs.tab', function (event) {
            localStorage.setItem('currentTab', event.target.id);
            document.getElementById('tabInput').value = event.target.id;
        });
    });
});
