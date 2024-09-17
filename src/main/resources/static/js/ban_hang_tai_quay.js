document.addEventListener('DOMContentLoaded', function () {
    const cashCheckbox = document.getElementById('btncheck1');
    const bankCheckbox = document.getElementById('btncheck2');
    const codCheckbox = document.getElementById('btncheck4');
    const cashInput = document.getElementById('cash-input');
    const bankInput = document.getElementById('bank-input');
    const createInvoiceButton = document.getElementById('create-invoice-button');
    const thanhToanKhiNhanHang = document.querySelector('.thanhToanKhiNhanHang');
    const changeAmountRow = document.getElementById('change-row');
    const changeAmountElement = document.getElementById('change-amount');
    const cashTextRow = document.querySelector('.cash_text');
    const deliveryCheckbox = document.getElementById('flexSwitchCheckChecked');
    const recipientInfo = document.querySelector('.recipient-info');
    const paymentOnDeliveryCheckbox = document.getElementById('btncheck4');
    const totalPayAmountElement = document.getElementById('total-pay-amount');


    thanhToanKhiNhanHang.classList.add('d-none');

    function updatePaymentInputs() {
        if (cashCheckbox.checked && bankCheckbox.checked) {
            cashInput.classList.remove('d-none');
            cashInput.placeholder = 'Nhập số tiền mặt';
            bankInput.classList.remove('d-none');
            bankInput.placeholder = 'Nhập số tiền chuyển khoản';
            changeAmountRow.classList.remove('d-none');
        } else {
            cashInput.classList.toggle('d-none', !cashCheckbox.checked);
            if (cashCheckbox.checked) {
                cashInput.placeholder = 'Nhập số tiền khách đưa';
                changeAmountRow.classList.remove('d-none');
            }
            bankInput.classList.add('d-none');
        }

        cashTextRow.classList.toggle('d-none', !cashCheckbox.checked);
        updateCreateInvoiceButton();
    }

    const cashErrorElement = document.getElementById('cash-error');
    const bankErrorElement = document.getElementById('bank-error');

    function updateCreateInvoiceButton() {

        const totalPayAmount = parseInt(totalPayAmountElement.textContent.replace(/[^0-9]/g, '')) || 0;
        const isAnyPaymentChecked = document.querySelector('.phuongThucThanhToan input[type="checkbox"]:checked') !== null;

        const cashGiven = parseFloat(cashInput.value.replace(/\D/g, '')) || 0;
        const bankAmount = parseFloat(bankInput.value.replace(/\D/g, '')) || 0;

        let cashError = '';
        let bankError = '';

        // Kiểm tra điều kiện cho phần tiền mặt
        if (cashCheckbox.checked && !bankCheckbox.checked) {
            if (isNaN(cashGiven) || cashGiven === 0) {
                cashError = 'Vui lòng nhập số tiền khách đưa';
            } else if (cashGiven < totalPayAmount) {
                cashError = 'Số tiền khách đưa không đủ để thanh toán';
            }
        }

        // Kiểm tra điều kiện khi cả hai phương thức thanh toán được chọn
        if (cashCheckbox.checked && bankCheckbox.checked) {
            const totalPaymentAmount = cashGiven + bankAmount;
            if ((totalPaymentAmount < totalPayAmount) && bankAmount > 0 && cashGiven > 0) {
                cashError = 'Tổng số tiền thanh toán không đủ';
                bankError = '';
            }
        }

        cashErrorElement.textContent = cashError;
        bankErrorElement.textContent = bankError;

        // Disable hoặc enable nút tạo hóa đơn
        const totalPaymentAmount = cashGiven + bankAmount;
        if ((cashError || bankError || !isAnyPaymentChecked ||
            (totalPaymentAmount < totalPayAmount) || isNaN(cashGiven) || isNaN(bankAmount)) || totalPayAmount === 0) {
            createInvoiceButton.disabled = true;
        } else {
            createInvoiceButton.disabled = false;
        }
        if ((isAnyPaymentChecked && !bankCheckbox.checked && !cashCheckbox.checked) && totalPayAmount > 0) {
            createInvoiceButton.disabled = false;
        }
        if ((bankCheckbox.checked && !cashCheckbox.checked) && totalPayAmount > 0) {
            createInvoiceButton.disabled = false;
        }
        if (paymentOnDeliveryCheckbox.checked && totalPayAmount > 0) {
            createInvoiceButton.disabled = false;
            cashError = '';
        }
    }

     function onCheckboxChange() {
        updatePaymentInputs();
        updateCreateInvoiceButton();
    }

    cashCheckbox.addEventListener('change', onCheckboxChange);
    bankCheckbox.addEventListener('change', onCheckboxChange);

    deliveryCheckbox.addEventListener('change', function () {
        if (this.checked) {
            recipientInfo.classList.remove('d-none');
            thanhToanKhiNhanHang.classList.remove('d-none');
        } else {
            recipientInfo.classList.add('d-none');
            thanhToanKhiNhanHang.classList.add('d-none');
        }
        updateCreateInvoiceButton();
    });

    codCheckbox.addEventListener('change', function () {
        const phuongThucThanhToan = document.querySelector('.phuongThucThanhToan');
        if (this.checked) {
            phuongThucThanhToan.classList.add('d-none');
            cashTextRow.classList.add('d-none');
            cashInput.classList.add('d-none');
            bankInput.classList.add('d-none');
        } else {
            phuongThucThanhToan.classList.remove('d-none');
            cashTextRow.classList.remove('d-none');
            cashInput.classList.remove('d-none');
        }
        updateCreateInvoiceButton();
    });

    cashInput.addEventListener('input', function () {
        const cashGiven = parseFloat(cashInput.value.replace(/\D/g, '')) || 0;
        const bankAmount = parseFloat(bankInput.value.replace(/\D/g, '')) || 0;
        const totalPayAmount = parseFloat(totalPayAmountElement.textContent.replace(/\D/g, '')) || 0;
        let change = 0;

        if (cashGiven >= totalPayAmount && !bankAmount) {
            change = cashGiven - totalPayAmount;
        } else if (cashGiven && bankAmount && (cashGiven + bankAmount) >= totalPayAmount) {
            change = (cashGiven + bankAmount) - totalPayAmount;
        }

        changeAmountElement.textContent = change > 0 ? `${change.toLocaleString('vi-VN', {
            style: 'currency',
            currency: 'VND'
        })}` : '0 ₫';
        updateCreateInvoiceButton();

    });


    updateCreateInvoiceButton();

    const checkboxes = document.querySelectorAll('.checkbox-container input[type="checkbox"]');
    checkboxes.forEach(function (checkbox) {
        checkbox.addEventListener('change', function () {
            var label = this.nextElementSibling;
            if (this.checked) {
                label.classList.add('checked');
            } else {
                label.classList.remove('checked');
            }
        });
    });
});


//======================================================================================================================

// Tạo giỏ hàng mới (tab giỏ hàng)
document.getElementById('addCartTab').addEventListener('click', function (event) {
    event.preventDefault();
    fetch('/ban-hang-tai-quay/cartCount')
        .then(response => response.json())
        .then(data => {
            if (data.cartCount >= 5) {
                document.getElementById('addCartTab').classList.add('disabled-icon');
            } else {
                fetch('/ban-hang-tai-quay/createCart')
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            let newTabId = 'navs-top-home-' + data.newCartId;
                            let newTabButton = `<li class="nav-item">
                                <button type="button" class="nav-link" role="tab" data-bs-toggle="tab" data-bs-target="#${newTabId}" aria-controls="${newTabId}" aria-selected="false">
                                    HD${data.newCartId}
                                    <span class="badge rounded-pill badge-center h-px-20 w-px-20 bg-label-danger">0</span>
                                </button>
                            </li>`;
                            let newTabContent = `<div class="tab-pane fade" id="${newTabId}" role="tabpanel">
                                <div class="table-responsive text-nowrap">
                                    <h4 class="d-flex justify-content-between align-items-center">
                                        Giỏ hàng
                                        <div>
                                            <button style="background-color: #ff8548; color: #ffffff;" type="button" class="btn me-2">Quét QR</button>
                                            <button type="button" class="btn btn-primary  " data-bs-toggle="modal"
                                                        data-bs-target="#addProduct">+ Thêm sản phẩm
                                            </button>
                                        </div>
                                    </h4>
                                    <hr>
                                    <div class="d-flex flex-column justify-content-center align-items-center" style="height: 240px;">
                                        <img src="../assets/img/icons/brands/R.png" width="25%" alt="No data"/>
                                        <p style="color: #868080">Hiện chưa có sản phẩm nào trong giỏ hàng!</p>
                                    </div>
                                </div>
                            </div>`;

                            let tabsContainer = document.querySelector('.nav-tabs');
                            let tabContentContainer = document.querySelector('.tab-content');

                            tabsContainer.insertAdjacentHTML('beforeend', newTabButton);
                            tabContentContainer.insertAdjacentHTML('beforeend', newTabContent);

                            tabsContainer.appendChild(document.getElementById('addCartTab').parentElement);

                            let newTab = tabsContainer.querySelector(`button[data-bs-target="#${newTabId}"]`);
                            let newTabInstance = new bootstrap.Tab(newTab);
                            newTabInstance.show();
                        }
                    });
            }
        });
});
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


// Thêm imei vào danh sách imei đã chọn
function addImei(imei) {
    const selectedImeiList = document.getElementById('selectedImeiList');
    const imeiItem = document.createElement('div');
    imeiItem.classList.add('selected-imei');
    imeiItem.innerText = imei;
    selectedImeiList.appendChild(imeiItem);
    console.log(imei)
}

// Xóa imei ra khỏi danh sách imei đã chọn
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

function loadImeis(button, page = 0) {
    currentProductId = button.getAttribute('data-product-id');
    fetchImeis(page);
}

function fetchImeis(page) {
    const url = new URL('/ban-hang-tai-quay/getImeis', window.location.origin);
    url.searchParams.append('productId', currentProductId);
    url.searchParams.append('page', page);
    url.searchParams.append('size', 5);
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error:' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
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

// ======================================================================================================================

// Load thông tin giỏ hàng khi tải trang
document.addEventListener('DOMContentLoaded', function () {
    const firstTab = document.querySelector('.nav-link.active');
    if (firstTab) {
        const gioHangId = firstTab.getAttribute('data-giohang-id');
        updateCartAndVoucher(gioHangId, firstTab);
    }
});


//======================================================================================================================
function addProductsToCart() {
    const selectedImeiList = [];
    const checkboxes = document.querySelectorAll('.form-check-input:checked');
    checkboxes.forEach(checkbox => {
        selectedImeiList.push(checkbox.value);
    });

    const activeTab = document.querySelector('.nav-link.active');
    const gioHangId = activeTab.getAttribute('data-giohang-id');

    fetch(`/ban-hang-tai-quay/addProductsToCart`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({imeiList: selectedImeiList, gioHangId: gioHangId})
    })
        .then(response => response.text())
        .then(data => {
            if (data === "Thêm thành công sản phẩm vào giỏ hàng.") {
                checkboxes.forEach(checkbox => {
                    checkbox.checked = false;
                });
                $('#imeiProduct').modal('hide');
                updateCartAndVoucher(gioHangId, activeTab);
                sendTabChangeMessage();
            } else {
                console.error('Thêm sản phẩm vào giỏ hàng thất bại:', data);
            }
        })
        .catch(error => console.error('Error:', error));
}


//======================================================================================================================


// Sự kiện ấn enter để search khách hàng
document.getElementById('search-customer-input').addEventListener('keypress', function (event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        searchCustomer();
    }
});

// Tìm kiếm khách hàng theo số điện thoại
function searchCustomer() {
    const searchValue = document.getElementById('search-customer-input').value;

    fetch('/ban-hang-tai-quay/search-customer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({'search-customer': searchValue})
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                console.log('Không tìm thấy khách hàng với số điện thoại đã nhập!');
            }
        })
        .then(customer => {
            // Hiển thị thông tin khách hàng lên form
            document.getElementById('customer-name').value = customer.tenKhachHang;
            document.getElementById('customer-phone').value = customer.sdt;

            // Lưu ID khách hàng vào thẻ ẩn
            document.getElementById('khach-hang-id').value = customer.id;

            // Thêm khách hàng vào giỏ hàng hiện tại
            const activeTab = document.querySelector('.nav-link.active');
            const gioHangId = activeTab.getAttribute('data-giohang-id');

            fetch('/ban-hang-tai-quay/addCustomerToCart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({gioHangId: gioHangId, khachHangId: customer.id})
            })
                .then(response => response.text())
                .then(data => {
                    console.log('Khách hàng đã được thêm vào giỏ hàng:', data);
                    updateCartAndVoucher(gioHangId, activeTab);
                })
                .catch(error => console.error('Lỗi thêm khách hàng vào giỏ hàng:', error));
        })
        .catch(error => {
            console.log('không tìm thấy khách hàng với sdt đã nhập');
        });
}


//============================================================================
// Cập nhật thông tin khách hàng của từng giỏ hàng khi chuyển tab/khi tải lại trang
document.addEventListener('DOMContentLoaded', function () {
    const activeTab = document.querySelector('.nav-link.active');
    const gioHangId = activeTab.getAttribute('data-giohang-id');

    checkCustomerInfo(gioHangId);
    updateCartAndVoucher(gioHangId, activeTab);

    const tabLinks = document.querySelectorAll('.nav-link');
    tabLinks.forEach(tabLink => {
        tabLink.addEventListener('click', function () {
            const gioHangId = this.getAttribute('data-giohang-id');
            checkCustomerInfo(gioHangId);
            updateCartAndVoucher(gioHangId, this);
        });
    });
});

// Hàm kiểm tra thông tin khách hàng
function checkCustomerInfo(gioHangId) {
    fetch('/ban-hang-tai-quay/getCustomerInfo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({gioHangId: gioHangId})
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                document.getElementById('customer-name').value = '';
                document.getElementById('customer-phone').value = '';
                document.getElementById('khach-hang-id').value = '';
            }
        })
        .then(customerInfo => {
            // Hiển thị thông tin khách hàng lên form
            if (customerInfo) {
                document.getElementById('customer-name').value = customerInfo.tenKhachHang;
                document.getElementById('customer-phone').value = customerInfo.sdt;
                // Lưu ID khách hàng vào thẻ ẩn
                document.getElementById('khach-hang-id').value = customerInfo.id;
            } else {
                document.getElementById('customer-name').value = '';
                document.getElementById('customer-phone').value = '';
                document.getElementById('khach-hang-id').value = '';
            }
        })
        .catch(error => {
            console.error('Lỗi kiểm tra thông tin khách hàng:', error);
        });
}

//======================================================================================================================
// Tự động fill thông tin khách hàng vào form thông tin người nhận khi chọn "Bán giao hàng"
    document.getElementById('flexSwitchCheckChecked').addEventListener('change', async (event) => {
        if (event.target.checked) {
            const gioHangId = document.querySelector('.nav-tabs .nav-link.active').getAttribute('data-giohang-id');

            try {
                const response = await fetch('/ban-hang-tai-quay/getCustomerInfo1', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({gioHangId: gioHangId})
                });

                if (!response.ok) {
                    throw new Error("Không thể lấy thông tin khách hàng.");
                }

                const customerInfo = await response.json();

                // Điền thông tin vào các trường thông tin người nhận
                document.querySelector('.recipient-info input.form-control.ten').value = customerInfo.tenKhachHang || '';
                document.querySelector('.recipient-info input.form-control.sdt').value = customerInfo.sdt || '';

                document.getElementById('city').value = customerInfo.tinh || '';
                document.getElementById('district').value = customerInfo.quan || '';
                document.getElementById('ward').value = customerInfo.phuong || '';
                document.getElementById('address').value = customerInfo.diaChiCuThe || '';

                const citySelect = document.getElementById('city');
                const existingCityOption = Array.from(citySelect.options).find(option => option.value === customerInfo.tinh);

                if (existingCityOption) {
                    existingCityOption.selected = true;
                } else if (customerInfo.tinh) {
                    const option = document.createElement('option');
                    option.value = customerInfo.tinh;
                    option.textContent = customerInfo.tinh;
                    option.selected = true;
                    citySelect.appendChild(option);
                }

                const districtSelect = document.getElementById('district');
                const existingDistrictOption = Array.from(districtSelect.options).find(option => option.value === customerInfo.quan);

                if (existingDistrictOption) {
                    existingDistrictOption.selected = true;
                } else if (customerInfo.quan) {
                    const option = document.createElement('option');
                    option.value = customerInfo.quan;
                    option.textContent = customerInfo.quan;
                    option.selected = true;
                    districtSelect.appendChild(option);
                }

                const wardSelect = document.getElementById('ward');
                const existingWardOption = Array.from(wardSelect.options).find(option => option.value === customerInfo.phuong);

                if (existingWardOption) {
                    existingWardOption.selected = true;
                } else if (customerInfo.phuong) {
                    const option = document.createElement('option');
                    option.value = customerInfo.phuong;
                    option.textContent = customerInfo.phuong;
                    option.selected = true;
                    wardSelect.appendChild(option);
                }

            } catch (error) {
                console.error('Error:', error);
            }
        } else {
            document.querySelector('.recipient-info input.form-control.ten').value = '';
            document.querySelector('.recipient-info input.form-control.sdt').value = '';
            document.getElementById('city').value = '';
            document.getElementById('district').value = '';
            document.getElementById('ward').value = '';
            document.getElementById('address').value = '';
        }
    });
// ======================================================================================================================

document.querySelectorAll('.nav-link').forEach(tab => {
    tab.addEventListener('click', function () {
        const gioHangId = this.getAttribute('data-giohang-id');
        updateCartAndVoucher(gioHangId, this);
    });
});

// Áp dụng voucher
function applyVoucher(voucher, totalAmount) {
    const discountCodeSelect = document.getElementById('discount-code-select');
    const discountAmountElement = document.getElementById('discount-amount');
    const totalPayAmountElement = document.getElementById('total-pay-amount');

    if (!discountCodeSelect || !discountAmountElement  || !totalPayAmountElement) {
        return;
    }

    let discountAmount = 0;

    if (voucher) {
        if (voucher.phanTramGiam != null && voucher.phanTramGiam > 0) {
            discountAmount = totalAmount * voucher.phanTramGiam / 100;
        } else if (voucher.soTienDuocGiam != null) {
            discountAmount = voucher.soTienDuocGiam;
        }

        if (voucher.soTienDuocGiamToiDa != null && voucher.soTienDuocGiamToiDa > 0 && discountAmount > voucher.soTienDuocGiamToiDa) {
            discountAmount = voucher.soTienDuocGiamToiDa;
        }

        discountAmount = Math.round(discountAmount * 100) / 100;
        discountAmountElement.textContent = '- ' + `${discountAmount.toLocaleString('vi-VN', {
            style: 'currency',
            currency: 'VND'
        })}`;

    } else {
        discountCodeSelect.value = '';
        discountAmountElement.textContent = '0 đ';
    }
    sendTabChangeMessage();



    const totalPayAmount = totalAmount - discountAmount;
    totalPayAmountElement.textContent = `${totalPayAmount.toLocaleString('vi-VN', {
        style: 'currency',
        currency: 'VND'
    })}`;
}

//=================================================
// Click vào nút áp dụng voucher
document.getElementById('apply-voucher-button').addEventListener('click', () => {
    const selectedVoucherId = document.getElementById('discount-code-select').value;
    const totalAmountText = document.getElementById('total-amount').textContent;

    const totalAmount = parseFloat(totalAmountText.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));

    if (isNaN(totalAmount)) {
        return;
    }

    if (selectedVoucherId) {
        fetch(`/ban-hang-tai-quay/getVoucherById/${selectedVoucherId}`)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Error: ' + response.statusText);
                }
            })
            .then(voucher => {
                applyVoucher(voucher, totalAmount);
            })
            .catch(error => console.error('Lỗi get voucher:', error));
    } else {
        applyVoucher(null, totalAmount);
    }
});


// ======================================================================================================================
function updateCartAndVoucher(gioHangId, tabElement) {
    const url = new URL('/ban-hang-tai-quay/getCartDetails', window.location.origin);
    url.searchParams.append('gioHangId', gioHangId);

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log('Cart details:', data);
            const tabPaneId = tabElement.getAttribute('data-bs-target').substring(1);
            const cartTable = document.querySelector(`#${tabPaneId} table`);
            const cartTableBody = document.querySelector(`#${tabPaneId} #cartTableBody`);
            const noDataDiv = document.querySelector(`#${tabPaneId} .no-data`);
            const totalAmountElement = document.querySelector(`#${tabPaneId} .total-amount`);
            const cartTableFooter = document.querySelector(`#${tabPaneId} #cartTableFooter`);
            const badge = tabElement.querySelector('.badge'); // Hiển thị số lượng sản phẩm trong giỏ hàng

            if (!cartTableBody || !cartTable || !noDataDiv || !totalAmountElement || !cartTableFooter) {
                return;
            }

            cartTableBody.innerHTML = '';
            let totalAmount = 0;
            if (Array.isArray(data) && data.length > 0) {
                let totalProductCount = 0; // Đếm tổng số sản phẩm

                data.forEach((detail, index) => {
                    const imeiDaBanList = detail.imeiList;
                    imeiDaBanList.forEach((imei, imeiIndex) => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                                        <td>${imeiIndex + 1}</td>
                                        <td>${detail.tenSanPham}</td>
                                        <td>${imei}</td>
                                        <td>${detail.giaBan.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}</td>
                                        <td><a href="#" data-imei="${imei}" data-giohang-id="${gioHangId}" class="delete-product"><i class="bi bi-trash text-danger" style="font-size: 23px"></i></a></td>`;
                                                cartTableBody.appendChild(row);
                                            });

                    // Cập nhật tổng số sản phẩm đã bán
                    totalProductCount += imeiDaBanList.length;
                    totalAmount += detail.giaBan * imeiDaBanList.length;
                });

                noDataDiv.style.display = 'none';
                cartTable.style.display = 'table';
                cartTableFooter.style.display = 'block';
                // Hiển thị số lượng sản phẩm trong giỏ hàng
                badge.textContent = totalProductCount;
            } else {
                noDataDiv.style.display = 'flex';
                cartTable.style.display = 'none';
                cartTableFooter.style.display = 'none';
                badge.textContent = 0;
            }
            sendTabChangeMessage();
            // Hiển thị tổng tiền bên dưới table giỏ hàng
            totalAmountElement.textContent = `${totalAmount.toLocaleString('vi-VN', {
                style: 'currency',
                currency: 'VND'
            })}`;

            // click vào icon delete
            document.querySelectorAll('.delete-product').forEach(button => {
                button.addEventListener('click', function (event) {
                    event.preventDefault();
                    const imei = this.getAttribute('data-imei');
                    const gioHangId = this.getAttribute('data-giohang-id');
                    deleteProductFromCart(gioHangId, imei, tabElement);
                });
            });
            // Hiển thị tổng tiền hàng
            const tongTienHang = document.getElementById('total-amount');
            tongTienHang.textContent = `${totalAmount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}`;


            // Lấy phần tử khach-hang-id
            // Lấy phần tử khach-hang-id
            const khachHangElement = document.getElementById('khach-hang-id');
            let khachHangId = null;

            if (khachHangElement) {
                khachHangId = khachHangElement.value;
            }

            const requestData = {
                totalAmount: totalAmount,
                khachHangId: khachHangId

            };



            // Gửi yêu cầu đến server để lấy danh sách voucher hợp lệ
            fetch('/ban-hang-tai-quay/getBestDiscountVoucher', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            })
                .then(response => response.text())
                .then(text => {
                    try {
                        const vouchers = JSON.parse(text);
                        const discountCodeSelect = document.getElementById('discount-code-select');
                        discountCodeSelect.innerHTML = '<option value="">Không áp dụng voucher</option>';

                        if (vouchers.length > 0) {
                            vouchers.sort((a, b) => {
                                const discountA = (a.phanTramGiam != null && a.phanTramGiam > 0) ?
                                    totalAmount * a.phanTramGiam / 100 :
                                    a.soTienDuocGiam;
                                const discountB = (b.phanTramGiam != null && b.phanTramGiam > 0) ?
                                    totalAmount * b.phanTramGiam / 100 :
                                    b.soTienDuocGiam;
                                return discountB - discountA;
                            });

                            vouchers.forEach(voucher => {
                                const option = document.createElement('option');
                                option.value = voucher.id;
                                option.textContent = voucher.tenPhieuGiamGia;
                                discountCodeSelect.appendChild(option);
                            });

                            discountCodeSelect.value = vouchers[0].id;
                            applyVoucher(vouchers[0], totalAmount);
                        } else {
                            applyVoucher(null, totalAmount);
                        }
                    } catch (error) {
                        console.error('Error parsing JSON:', error);
                        console.error('Received text:', text);
                        const discountCodeSelect = document.getElementById('discount-code-select');
                        const discountAmountElement = document.getElementById('discount-amount');
                        const totalPayAmountElement = document.getElementById('total-pay-amount');
                        discountCodeSelect.innerHTML = '';
                        discountAmountElement.textContent = '0 đ';
                        totalPayAmountElement.textContent = `${totalAmount.toLocaleString('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        })}`;
                    }
                })
                .catch(error => {
                    console.error('Error fetching best discount voucher:', error);
                    const discountCodeSelect = document.getElementById('discount-code-select');
                    const discountAmountElement = document.getElementById('discount-amount');
                    const totalPayAmountElement = document.getElementById('total-pay-amount');
                    discountCodeSelect.innerHTML = '';
                    discountAmountElement.textContent = '0 đ';
                    totalPayAmountElement.textContent = `${totalAmount.toLocaleString('vi-VN', {
                        style: 'currency',
                        currency: 'VND'
                    })}`;
                });

        })
        .catch(error => console.error('Error:', error));


}


// ======================================================================================================================

// Xóa sản phẩm khỏi giỏ hàng theo imei
function deleteProductFromCart(gioHangId, imei, tabElement) {
    fetch('/ban-hang-tai-quay/deleteProductFromCart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({gioHangId: gioHangId, imei: imei})
    })
        .then(response => response.text())
        .then(data => {
            if (data === "Xóa thành công sản phẩm khỏi giỏ hàng.") {
                updateCartAndVoucher(gioHangId, tabElement);
                sendTabChangeMessage();
            } else {
                console.error('Xóa thất bại sản phẩm khỏi giỏ hàng', data);
            }
        })
        .catch(error => console.error('Lỗi xóa sản phẩm khỏi giỏ hàng:', error));
}


// ======================================================================================================================

// Hiển thị thông báo khi thêm đơn hàng thành công
document.addEventListener('DOMContentLoaded', () => {
    var successMessage = localStorage.getItem('successMessage');
    if (successMessage && successMessage !== "") {
        var successMessageElement = document.getElementById('successMessage');
        if (successMessageElement) {
            successMessageElement.querySelector('.success__title').textContent = successMessage;
            successMessageElement.classList.add('slideInRight');
            successMessageElement.classList.add('show');
            setTimeout(function () {
                successMessageElement.classList.remove('slideInRight');
                successMessageElement.classList.add('slideOutRight');
                setTimeout(function () {
                    successMessageElement.style.display = 'none';
                    localStorage.removeItem('successMessage');
                }, 1000);
            }, 3000);
        }
        localStorage.removeItem('successMessage');

    }

    document.querySelector('.btnTaoHoaDon').addEventListener('click', async () => {
        const gioHangId = document.querySelector('.nav-tabs .nav-link.active').getAttribute('data-giohang-id');
        const idPhieuGiamGia = document.getElementById('discount-code-select').value;
        const banGiaoHang = document.getElementById('flexSwitchCheckChecked').checked;
        const thanhToanKhiNhanHangElement = document.getElementById('btncheck4');
        const thanhToanKhiNhanHang = thanhToanKhiNhanHangElement ? thanhToanKhiNhanHangElement.checked : false;
        const phuongThucThanhToan = [];
        if (document.getElementById('btncheck1').checked) phuongThucThanhToan.push(1);
        if (document.getElementById('btncheck2').checked) phuongThucThanhToan.push(2);
        const totalAmountElement = document.getElementById('total-amount').textContent;
        const totalAmount = parseFloat(totalAmountElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const tongTienSauGiamElement = document.getElementById('total-pay-amount').textContent;
        const tongTienSauGiam = parseFloat(tongTienSauGiamElement.replace(/\./g, '').replace(/[^0-9.-]+/g, ''));
        const cashInput = document.getElementById('cash-input');
        const bankInput = document.getElementById('bank-input');
        const cashGiven = parseFloat(cashInput.value.replace(/\D/g, '')) || 0;
        const bankAmount = parseFloat(bankInput.value.replace(/\D/g, '')) || 0;

        const payload = {
            gioHangId: gioHangId,
            banGiaoHang: banGiaoHang,
            phuongThucThanhToan: phuongThucThanhToan,
            thanhToanKhiNhanHang: thanhToanKhiNhanHang,
            tongTien: totalAmount,
            tongTienSG: tongTienSauGiam,
            tienMat: cashGiven,
            chuyenKhoan: bankAmount
        };

        if (idPhieuGiamGia) {
            payload.idPhieuGiamGia = idPhieuGiamGia;
        }

        if (banGiaoHang) {
            const tenNguoiNhan = document.querySelector('.recipient-info input.form-control.ten').value.trim();
            const sdtNguoiNhan = document.querySelector('.recipient-info input.form-control.sdt').value.trim();
            const ghiChu = document.querySelector('.recipient-info textarea').value.trim();
            const customerName = document.getElementById('customer-name').value;
            const province = document.getElementById('city');
            const district = document.getElementById('district');
            const ward = document.getElementById('ward');
            const address = document.getElementById('address');

            const diaChi = `${province.options[province.selectedIndex].text}, ${district.options[district.selectedIndex].text}, ${ward.options[ward.selectedIndex].text}, ${address.value}`.trim();

            if (customerName) {
                if (!tenNguoiNhan && !sdtNguoiNhan && !diaChi) {
                    try {
                        const response = await fetch('/ban-hang-tai-quay/getCustomerInfo', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({gioHangId: gioHangId})
                        });

                        if (!response.ok) {
                            throw new Error("Không thể lấy thông tin khách hàng.");
                        }

                        const customerInfo = await response.json();
                        payload.tenNguoiNhan = customerInfo.tenKhachHang;
                        payload.sdtNguoiNhan = customerInfo.sdt;
                        payload.diaChi = customerInfo.diaChiMacDinh;
                        payload.ghiChu = ghiChu;

                    } catch (error) {
                        console.error('Error:', error);
                        return;
                    }
                } else {
                    // Nhập thông tin người nhận
                    payload.tenNguoiNhan = tenNguoiNhan;
                    payload.sdtNguoiNhan = sdtNguoiNhan;
                    payload.diaChi = diaChi;
                    payload.ghiChu = ghiChu;
                }
            } else {
                if (!tenNguoiNhan || !sdtNguoiNhan || !diaChi) {
                  //  alert("Vui lòng nhập đầy đủ thông tin người nhận.");
                    return;
                }
                payload.tenNguoiNhan = tenNguoiNhan;
                payload.sdtNguoiNhan = sdtNguoiNhan;
                payload.diaChi = diaChi;
                payload.ghiChu = ghiChu;
            }
        }

        try {
            const response = await fetch('/ban-hang-tai-quay/addInvoice', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const textResponse = await response.text();
            if (!banGiaoHang) {
                localStorage.setItem('invoiceCreated', 'true');
            }
            // Lưu trạng thái thành công vào Local Storage
            localStorage.setItem('successMessage', textResponse);

            updateCartAndVoucher(gioHangId, document.querySelector('.nav-tabs .nav-link.active'));
            window.location.reload();
           // window.location.href = `/quan-ly-don-hang/view-detail/${invoiceId}`;
        } catch (error) {
            console.error(error);
        }
    });
});


// //===================================================================================
// Validate khi thêm nhanh khách hàng
function validateInput(id, errorId, message, regex = null) {
    const input = document.getElementById(id);
    const errorElement = document.getElementById(errorId);

    if (input.value.trim() === "") {
        errorElement.textContent = message;
        return false;
    } else if (regex && !regex.test(input.value.trim())) {
        errorElement.textContent = 'Định dạng không hợp lệ.';
        return false;
    } else {
        errorElement.textContent = "";
        return true;
    }
}

// Thêm nhanh khách hàng
function addCustomer() {
    const isNameValid = validateInput('customerName', 'customerNameError', 'Họ và tên không được để trống.');
    const isPhoneValid = validateInput('customerPhone', 'customerPhoneError', 'Số điện thoại không được để trống.', /^[0-9]{10,11}$/);
    const isEmailValid = validateInput('customerEmail', 'customerEmailError', 'Email không được để trống.', /^[^\s@]+@[^\s@]+\.[^\s@]+$/);
    const isCityValid = validateInput('city1', 'cityError', 'Tỉnh/ Thành phố không được để trống.');
    const isDistrictValid = validateInput('district1', 'districtError', 'Quận/ Huyện không được để trống.');
    const isWardValid = validateInput('ward1', 'wardError', 'Phường/ xã không được để trống.');
    const isAddressValid = validateInput('address1', 'addressError', 'Địa chỉ cụ thể không được để trống.');

    if (isNameValid && isPhoneValid && isEmailValid && isCityValid && isDistrictValid && isWardValid && isAddressValid) {
        const customerName = document.getElementById('customerName').value;
        const customerPhone = document.getElementById('customerPhone').value;
        const customerEmail = document.getElementById('customerEmail').value;
        const city = document.getElementById('city1').value;
        const district = document.getElementById('district1').value;
        const ward = document.getElementById('ward1').value;
        const address = document.getElementById('address1').value;

        // Thêm khách hàng vào giỏ hàng hiện tại
        const activeTab = document.querySelector('.nav-link.active');
        const gioHangId = activeTab.getAttribute('data-giohang-id');
        fetch('/ban-hang-tai-quay/add-customer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                customerName: customerName,
                customerPhone: customerPhone,
                customerEmail: customerEmail,
                city: city,
                district: district,
                ward: ward,
                address: address,
                gioHangId: gioHangId
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    $('#addCustomer').modal('hide');
                    checkCustomerInfo(gioHangId);
                    updateCartAndVoucher(gioHangId, activeTab);
                } else {
                    if (data.errors) {
                        for (const key in data.errors) {
                            const errorElement = document.getElementById(key + 'Error');
                            if (errorElement) {
                                errorElement.textContent = data.errors[key];
                            }
                        }
                    } else {
                        console.log('Thêm khách hàng thất bại: ' + data.message);
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });

    }
}
// ======================================================================================================================

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
                qrbox: {width: 300, height: 300}
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
        const activeTab = document.querySelector('.nav-link.active');
        const gioHangId = activeTab.getAttribute('data-giohang-id');

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
                    updateCartAndVoucher(gioHangId, activeTab);
                    sendTabChangeMessage();
                } else {
                    $('#qrModal').modal('hide');
                }
            })
            .catch(error => console.error('Lỗi thêm vào giỏ hàng:', error));
    }
});
