document.addEventListener("DOMContentLoaded", function () {
    fetchEmployeeData(`http://localhost:8080/LeaveManagementApp/login`);
    const container_lsc = document.getElementById('leave-summary-cards');
    fetchMyTeamUpcomingLeaves();
    fetchUpcomingLeaves();
    fetchLeaveSummary();
    loadUpcomingHolidays();


    const dashboardLink = document.getElementById('dashboard');
    dashboardLink.addEventListener('click', function (event) {
        event.preventDefault();
        const display = document.getElementById('employee-details');
        display.innerHTML = '';
        fetchLeaveSummary();
        fetchUpcomingLeaves();
        fetchMyTeamUpcomingLeaves();
        loadUpcomingHolidays();
    });

    function loadUpcomingHolidays() {
        const apiEndpoint = 'http://localhost:8080/LeaveManagementApp/employee/holiday';

        fetch(apiEndpoint)
            .then(response => response.json())
            .then(holidays => {
                const tableContainer = document.getElementById('upcoming-holidays');
                let tableHTML = `
                <h2 class="upcoming-holidays-title">Upcoming Holidays</h2>
                <table class="holiday-table table table-striped">
                    <thead class="holiday-head">
                        <tr class="holiday-row">
                            <th>Holiday Name</th>
                            <th>Date</th>
                            <th>Description</th>
                        </tr>
                    </thead>
                    <tbody>`;

                holidays.forEach(holiday => {
                    tableHTML += `
                    <tr class="holiday-row">
                        <td class="holiday-td">${holiday.holidayName}</td>
                        <td class="holiday-td">${holiday.holidayDate}</td>
                        <td class="holiday-td">${holiday.description}</td>
                    </tr>`;
                });

                tableHTML += `
                        </tbody>
                    </table>`;

                tableContainer.innerHTML = tableHTML;
            })
            .catch(error => {
                console.error('Error fetching holidays:', error);
                document.getElementById('upcoming-holidays').innerHTML = '<p>Error loading holidays.</p>';
            });

    }

    async function fetchUpcomingLeaves() {
        const tableBody = document.getElementById('upcoming-leaves-body');
        tableBody.innerHTML = '';
        try {
            const response = await fetch(`http://localhost:8080/LeaveManagementApp/employee/leave?status=PENDING,APPROVED`);
            const data = await response.json();

            data.forEach(item => {
                const row = document.createElement('tr');

                const statusClass = item.leaveRequestStatus.toLowerCase(); // Class name for status
                row.innerHTML = `
                    <td>${item.leaveType}</td>
                    <td>${item.leaveReason}</td>
                    <td>${item.fromDate}</td>
                    <td>${item.toDate}</td>
                    <td>${item.dateOfApplication}</td>
                    <td>${item.totalDays}</td>
                    <td class="status-cell ${statusClass}">${item.leaveRequestStatus}</td>
                `;

                tableBody.appendChild(row);
            });
        } catch (error) {
            console.error('Error fetching upcoming leaves:', error);
        }
    }
    async function fetchMyTeamUpcomingLeaves() {
        const tableBody = document.getElementById('my-team-upcoming-leaves-body');
        tableBody.innerHTML = '';
        try {
            const response = await fetch('http://localhost:8080/LeaveManagementApp/employee/my-team-leave?status=PENDING,APPROVED');
            const data = await response.json();

            data.forEach(item => {
                const row = document.createElement('tr');
                row.classList.add('team-leaves-table');
                const statusClass = item.leaveRequestStatus.toLowerCase(); // Class name for status

                row.innerHTML = `
                    <td>${item.empName}</td>
                    <td>${item.leaveType}</td>
                    <td>${item.leaveReason}</td>
                    <td>${item.fromDate}</td>
                    <td>${item.toDate}</td>
                    <td>${item.dateOfApplication}</td>
                    <td>${item.totalNoOfDays}</td>
                    <td class="status-cell ${statusClass}">${item.leaveRequestStatus}</td>
                `;

                tableBody.appendChild(row);
            });
        } catch (error) {
            console.error('Error fetching my team upcoming leaves:', error);
        }
    }

    async function fetchLeaveSummary() {
        const summaryContainer = document.getElementById('leave-summary-cards-ml');
        summaryContainer.innerHTML = ''; // Clear existing content

        try {
            const response = await fetch(`http://localhost:8080/LeaveManagementApp/employee/leave/summary`);
            const data = await response.json();

            if (!summaryContainer) {
                console.error('Container element not found');
                return;
            }

            // Create and add the title
            const title = document.createElement('h2');
            title.textContent = 'My Summary';
            title.classList.add('summary-title');
            summaryContainer.appendChild(title);

            // Create the container for the leave cards
            const cardsContainer = document.createElement('div');
            cardsContainer.id = 'leave-summary-cards';
            summaryContainer.appendChild(cardsContainer);

            // Add the leave cards
            data.forEach(item => {
                const card = document.createElement('div');
                card.className = 'leave-card';

                card.innerHTML = `
                <div class="card-title">${item.leaveType}</div>
                <div class="card-info available-leaves">
                    <span>Available:</span> ${item.pendingLeaves}
                </div>
                <div class="card-info used-leaves">
                    <span>Used:</span> ${item.totalLeavesTaken}
                </div>
                `;

                cardsContainer.appendChild(card);
            });
        } catch (error) {
            console.error('Error fetching leave summary:', error);
        }
    }
    async function fetchEmployeeData(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error fetching data:', error);
            window.location.href = 'Login.html';
        }
    }




    // Fetch and display profile name and initial
    fetch("http://localhost:8080/LeaveManagementApp/login")
        .then(response => response.json())
        .then(data => {
            const profileName = document.getElementById("profile-name");
            const profilePic = document.getElementById("profile-pic");
            const employeeName = data.empName;
            profileName.textContent = employeeName;
            profilePic.textContent = employeeName.charAt(0).toUpperCase();
        })
        .catch(error => {
            console.error("Error fetching user data:", error);
        });

    const profilePic = document.getElementById("profile-pic");
    const profileName = document.getElementById("profile-name");
    //    const employeeDetails = document.getElementById("employee-details");

    async function fetchAndDisplayEmployeeData() {
        const tableContainer = document.getElementById('upcoming-holidays');
        tableContainer.innerHTML = '';
        const tableBody = document.getElementById('upcoming-leaves-container');
        tableBody.innerHTML = '';
        const upcomingTeamLeaves = document.getElementById('my-team-upcoming-leaves-container');
        upcomingTeamLeaves.innerHTML = '';
        const employeeDetailsDiv = document.getElementById('employee-details');
        const summaryContainer = document.getElementById('leave-summary-cards-ml');
        summaryContainer.innerHTML = '';
        container_lsc.innerHTML = '';
        employeeDetailsDiv.innerHTML = ''; // Clear previous content
        try {
            const data = await fetchEmployeeData("http://localhost:8080/LeaveManagementApp/login");

            // Construct HTML for employee details
            const detailsHtml = `
               <div class="profile-section-info">
                   <div class="info-profile-pic" style="font-size: 40px;">${data.empName.charAt(0).toUpperCase()}</div>
                   <div class="info-profile-name">${data.empName}</div>
               </div>
               <h3>My Details</h3>
               <table class="table">
                   <tbody>
                       <tr>
                           <td><strong>Date of Birth:</strong></td>
                           <td>${new Date(data.empDateOfBirth).toLocaleDateString()}</td>
                       </tr>
                       <tr>
                           <td><strong>Phone Number:</strong></td>
                           <td>${data.phoneNumber}</td>
                       </tr>
                       <tr>
                           <td><strong>Email:</strong></td>
                           <td>${data.email}</td>
                       </tr>
                   </tbody>
               </table>
               <h3>My Manager Details</h3>
               <table class="table">
                   <tbody>
                       <tr>
                           <td><strong>Manager Name:</strong></td>
                           <td>${data.managerName}</td>
                       </tr>
                       <tr>
                           <td><strong>Manager Phone Number:</strong></td>
                           <td>${data.managerPhoneNumber}</td>
                       </tr>
                       <tr>
                           <td><strong>Manager Email:</strong></td>
                           <td>${data.managerEmailId}</td>
                       </tr>
                   </tbody>
               </table>
           `;

            const employeeDetails = document.getElementById("employee-details");
            employeeDetails.innerHTML = detailsHtml;
        } catch (error) {
            console.error("Error fetching and displaying employee data:", error);
        }
    }


    // Add event listeners to profile pic and profile name
    profilePic.addEventListener("click", fetchAndDisplayEmployeeData);
    profileName.addEventListener("click", fetchAndDisplayEmployeeData);

    // Event listener for "My Team Leaves" button
    document.getElementById('my-team-leaves').addEventListener('click', function () {
        const tableContainer = document.getElementById('upcoming-holidays');
        tableContainer.innerHTML = '';
        const tableBody = document.getElementById('upcoming-leaves-container');
        tableBody.innerHTML = '';
        const upcomingTeamLeaves = document.getElementById('my-team-upcoming-leaves-container');
        upcomingTeamLeaves.innerHTML = '';
        const employeeDetailsDiv = document.getElementById('employee-details');
        const summaryContainer = document.getElementById('leave-summary-cards-ml');
        summaryContainer.innerHTML = '';
        container_lsc.innerHTML = '';
        employeeDetailsDiv.innerHTML = ''; // Clear previous content

        // Create container for tabs and Apply for Leave button
        const headerContainer = document.createElement('div');
        headerContainer.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-3');

        // Create the tabs
        const tabsContainer = document.createElement('ul');
        tabsContainer.classList.add('nav', 'nav-tabs', 'mb-3', 'flex-grow-1'); // Flex-grow to align to left

        const tabs = [
            { id: 'summary-tab', text: 'Summary', callback: () => { displayMyTeamSummary() } },
            {
                id: 'pending-tab', text: 'Pending Approval', callback: () => {
                    fetchTeamLeaveRequests('PENDING');
                }
            },
            {
                id: 'leaves-tab', text: 'Leaves', callback: () => {
                    // Show the leaves tab content with status filter
                    fetchTeamLeaveRequests('APPROVED,REJECTED,CANCELLED');
                }
            }
        ];

        tabs.forEach(tab => {
            const tabElement = document.createElement('li');
            tabElement.classList.add('nav-item');

            const tabLink = document.createElement('a');
            tabLink.classList.add('nav-link');
            tabLink.href = '#';
            tabLink.textContent = tab.text;
            tabLink.addEventListener('click', function (e) {
                e.preventDefault();
                // Remove active class from all tabs
                tabsContainer.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
                // Add active class to the clicked tab
                tabLink.classList.add('active');
                // Call the tab's callback function
                tab.callback();
            });

            if (tab.id === 'summary-tab') {
                tabLink.classList.add('active'); // Set "Leaves" tab as active by default
            }

            tabElement.appendChild(tabLink);
            tabsContainer.appendChild(tabElement);
        });

        headerContainer.appendChild(tabsContainer);


        const headerRightContainer = document.createElement('div'); // Right-aligned container
        headerRightContainer.classList.add('d-flex', 'align-items-center');

        headerContainer.appendChild(headerRightContainer);
        employeeDetailsDiv.appendChild(headerContainer);

        // Function to fetch and display the leave requests
        function fetchTeamLeaveRequests(status) {
            fetch(`http://localhost:8080/LeaveManagementApp/employee/my-team-leave?status=${status}`)
                .then(response => response.json())
                .then(data => {
                    // Clear previous content, but retain header
                    employeeDetailsDiv.innerHTML = '';
                    employeeDetailsDiv.appendChild(headerContainer);

                    // Remove existing status filter dropdown if present in non-"Leaves" tabs
                    const existingFilter = document.querySelector('.get-leaves-dropdown');
                    if (existingFilter && status !== 'APPROVED,REJECTED,PENDING,CANCELLED') {
                        existingFilter.remove();
                    }

                    if (status === 'APPROVED,REJECTED,CANCELLED') {
                        // Create and add the status filter dropdown if in "Leaves" tab
                        const statusFilter = document.createElement('select');
                        statusFilter.classList.add('form-select', 'w-auto', 'mb-3', 'get-leaves-dropdown');
                        const statuses = [
                            { value: 'APPROVED,REJECTED,CANCELLED', text: 'All Requests' },
                            { value: 'APPROVED', text: 'Approved' },
                            { value: 'REJECTED', text: 'Rejected' },
                            { value: 'CANCELLED', text: 'Cancelled' }
                        ];
                        statuses.forEach(statusOption => {
                            const option = document.createElement('option');
                            option.value = statusOption.value;
                            option.textContent = statusOption.text;
                            if (statusOption.value === status) {
                                option.selected = true;
                            }
                            statusFilter.appendChild(option);
                        });

                        statusFilter.addEventListener('change', function () {
                            fetchTeamLeaveRequests(this.value); // Fetch data based on selected status
                        });

                        headerRightContainer.appendChild(statusFilter); // Add status filter to the right container
                    }

                    // Create the table
                    const table = document.createElement('table');
                    table.classList.add('table', 'table-striped', 'table-hover', 'custom-leaves-table');

                    const thead = document.createElement('thead');
                    const headerRow = document.createElement('tr');
                    const headers = ['Employee Name', 'Leave Type', 'FROM', 'TO', 'Total Days', 'Status', 'Actions'];
                    headers.forEach(headerText => {
                        const th = document.createElement('th');
                        th.textContent = headerText;
                        headerRow.appendChild(th);
                    });
                    thead.appendChild(headerRow);
                    table.appendChild(thead);

                    const tbody = document.createElement('tbody');
                    data.forEach(leaveRequest => {
                        const row = document.createElement('tr');

                        const empNameCell = document.createElement('td');
                        empNameCell.textContent = leaveRequest.empName;
                        row.appendChild(empNameCell);

                        const leaveTypeCell = document.createElement('td');
                        leaveTypeCell.textContent = leaveRequest.leaveType;
                        row.appendChild(leaveTypeCell);

                        const fromDateCell = document.createElement('td');
                        fromDateCell.textContent = new Date(leaveRequest.fromDate).toLocaleDateString();
                        row.appendChild(fromDateCell);

                        const toDateCell = document.createElement('td');
                        toDateCell.textContent = new Date(leaveRequest.toDate).toLocaleDateString();
                        row.appendChild(toDateCell);

                        const totalDaysCell = document.createElement('td');
                        totalDaysCell.textContent = leaveRequest.totalNoOfDays;
                        row.appendChild(totalDaysCell);

                        const statusCell = document.createElement('td');
                        statusCell.classList.add('status-cell', leaveRequest.leaveRequestStatus.toLowerCase());
                        statusCell.textContent = leaveRequest.leaveRequestStatus;
                        row.appendChild(statusCell);

                        const actionsCell = document.createElement('td');

                        if (leaveRequest.leaveRequestStatus === 'PENDING') {
                            const approveButton = document.createElement('button');
                            approveButton.textContent = 'Approve';
                            approveButton.classList.add('btn', 'btn-success', 'mr-2', 'approve-btn');

                            approveButton.addEventListener('click', () => showLeaveSummaryModal(leaveRequest, 'APPROVED'));

                            const rejectButton = document.createElement('button');
                            rejectButton.textContent = 'Reject';
                            rejectButton.classList.add('btn', 'btn-danger', 'reject-btn', 'reject-btn');

                            rejectButton.addEventListener('click', () => showLeaveSummaryModal(leaveRequest, 'REJECTED'));

                            actionsCell.appendChild(approveButton);
                            actionsCell.appendChild(rejectButton);
                        }

                        const detailsButton = document.createElement('button');
                        detailsButton.textContent = 'Details';
                        detailsButton.classList.add('btn', 'btn-info', 'details-btn');
                        detailsButton.addEventListener('click', () => {
                            // Show leave request details in a modal
                            const modalTitle = document.getElementById('leaveDetailsModalLabel');
                            const modalBody = document.getElementById('leaveDetailsModalBody');

                            modalTitle.textContent = `Leave Details for ${leaveRequest.empName}`;
                            modalBody.innerHTML = `
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Employee Name:</strong>
                                   ${leaveRequest.empName}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Type:</strong>
                                   ${leaveRequest.leaveType}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Reason:</strong>
                                   ${leaveRequest.leaveReason}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">FROM:</strong>
                                   ${new Date(leaveRequest.fromDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">TO:</strong>
                                   ${new Date(leaveRequest.toDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Date of Application:</strong>
                                   ${new Date(leaveRequest.dateOfApplication).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Total Days:</strong>
                                   ${leaveRequest.totalNoOfDays}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Status:</strong>
                                   ${leaveRequest.leaveRequestStatus}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Date of Approved:</strong>
                                   ${new Date(leaveRequest.dateOfApproved).toLocaleDateString()}
                               </p>
                           `;


                            const leaveDetailsModal = new bootstrap.Modal(document.getElementById('leaveDetailsModal'));
                            leaveDetailsModal.show();
                        });

                        actionsCell.appendChild(detailsButton);
                        row.appendChild(actionsCell);

                        tbody.appendChild(row);
                    });

                    table.appendChild(tbody);
                    employeeDetailsDiv.appendChild(table);
                })
                .catch(error => console.error('Error fetching leave requests:', error));
        }

        function showLeaveSummaryModal(leaveRequest, action) {
            const modalTitle = document.getElementById('leaveSummaryModalLabel');
            const modalEmpName = document.getElementById('modalEmpName');
            const modalLeaveSummaryBody = document.getElementById('modalLeaveSummaryBody');
            const modalLeaveRequestDetails = document.getElementById('modalLeaveRequestDetails');
            const modalApproveButton = document.getElementById('modalApproveButton');
            const modalRejectButton = document.getElementById('modalRejectButton');

            // Reset modal contents and buttons
            modalTitle.textContent = 'Employee Summary';
            modalEmpName.textContent = leaveRequest.empName;
            modalLeaveSummaryBody.innerHTML = '';
            modalLeaveRequestDetails.style.display = 'none';
            modalApproveButton.style.display = 'none';
            modalRejectButton.style.display = 'none';

            // Fetch and display leave summary data
            fetch(`http://localhost:8080/LeaveManagementApp/employee/team/leave/summary?empId=${leaveRequest.empId}`)
                .then(response => response.json())
                .then(leaveSummaryData => {
                    let summaryHtml = '';

                    // Filter and display only the leave type being approved/rejected
                    const summary = leaveSummaryData.find(summary => summary.leaveType === leaveRequest.leaveType);
                    if (summary) {
                        summaryHtml = `<p><strong>${summary.leaveType}:</strong> Available: ${summary.pendingLeaves}, Used: ${summary.totalLeavesTaken}</p>`;
                    } else {
                        summaryHtml = '<p>No data available for this leave type.</p>';
                    }
                    modalLeaveSummaryBody.innerHTML = summaryHtml;

                    // Show request details if action is APPROVE or REJECT
                    if (action === 'APPROVED' || action === 'REJECTED') {
                        modalLeaveRequestDetails.style.display = 'block';
                        modalLeaveRequestDetails.innerHTML = `
                            <p><strong>From:</strong> ${leaveRequest.fromDate}</p>
                            <p><strong>To:</strong> ${leaveRequest.toDate}</p>
                            <p><strong>Total Days:</strong> ${leaveRequest.totalNoOfDays}</p>
                            <p><strong>Applied On:</strong> ${leaveRequest.dateOfApplication}</p>
                        `;

                        // Show the appropriate action button
                        if (action === 'APPROVED') {
                            modalApproveButton.style.display = 'inline-block';
                            modalApproveButton.onclick = () => handleLeaveAction('APPROVED', leaveRequest.leaveRequestId);
                        } else if (action === 'REJECTED') {
                            modalRejectButton.style.display = 'inline-block';
                            modalRejectButton.onclick = () => handleLeaveAction('REJECTED', leaveRequest.leaveRequestId);
                        }
                    }

                    // Show the modal
                    $('#leaveSummaryModal').modal('show');
                })
                .catch(error => {
                    console.error('Error fetching leave summary:', error);
                    showErrorDialog('An unexpected error occurred while fetching the leave summary.');
                });
        }

        function handleLeaveAction(action, leaveRequestId) {
            fetch(`http://localhost:8080/LeaveManagementApp/employee/my-team-leave?approveOrReject=${action}&leaveRequestId=${leaveRequestId}`, {
                method: 'POST'
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(errorData => {
                            let errorMessage = 'An unexpected error occurred.';
                            if (errorData.message.includes('LeaveDaysExceededException')) {
                                errorMessage = 'This employee has already exceeded the leave limit for this leave type. Please review and reject this leave request.';
                            } else {
                                errorMessage = errorData.message;
                            }
                            showErrorDialog(errorMessage);
                            throw new Error(errorMessage); // Prevent further execution
                        });
                    }
                    return response.json();
                })
                .then(result => {
                    if (result) {
                        fetchTeamLeaveRequests('PENDING');
                        $('#leaveSummaryModal').modal('hide');
                    }
                })
                .catch(error => {
                    console.error(`Error ${action.toLowerCase()}ing leave request:`, error);
                    showErrorDialog('An unexpected error occurred while processing the request.');
                });
        }

        function showErrorDialog(errorMessage) {
            const errorDialog = document.getElementById('errorDialog');
            const errorMessageElement = document.getElementById('errorMessage');

            if (errorDialog && errorMessageElement) {
                errorMessageElement.textContent = errorMessage;
                $('#errorDialog').modal('show');
            } else {
                console.error('Error dialog or error message element not found.');
            }
        }




        // Display team summary
        function displayMyTeamSummary() {
            const summaryContainer = document.createElement('div');
            summaryContainer.id = 'summary-container';

            employeeDetailsDiv.innerHTML = ''; // Clear previous content
            employeeDetailsDiv.appendChild(headerContainer);
            employeeDetailsDiv.appendChild(summaryContainer);

            fetch('http://localhost:8080/LeaveManagementApp/employee/team/leave/summary')
                .then(response => response.json())
                .then(data => {
                    summaryContainer.innerHTML = ''; // Clear any previous content

                    for (const [employee, leaveSummaries] of Object.entries(data)) {
                        // Create a card for each employee
                        const employeeCard = document.createElement('div');
                        employeeCard.classList.add('employee-card');

                        // Extract employee name from the key (assuming the name is inside the key)
                        const employeeName = employee.match(/empName='([^']+)'/)[1];

                        // Create employee name element and center it
                        const nameElement = document.createElement('h2');
                        nameElement.classList.add('employee-name');
                        nameElement.innerText = employeeName;
                        employeeCard.appendChild(nameElement);

                        // Create leave summary container
                        const leaveSummaryContainer = document.createElement('div');
                        leaveSummaryContainer.classList.add('leave-summary-container');

                        leaveSummaries.forEach(summary => {
                            const leaveItem = document.createElement('div');
                            leaveItem.classList.add('leave-item');

                            // Create leave type element
                            const leaveTypeElement = document.createElement('strong');
                            leaveTypeElement.classList.add('leave-type');
                            leaveTypeElement.innerText = summary.leaveType;

                            // Create available and used data
                            const leaveDataElement = document.createElement('span');
                            leaveDataElement.classList.add('leave-data');
                            leaveDataElement.innerHTML = `Available: ${summary.pendingLeaves} <br> Used: ${summary.totalLeavesTaken}`;

                            // Add leave type and data to the leave item
                            leaveItem.appendChild(leaveTypeElement);
                            leaveItem.appendChild(leaveDataElement);

                            // Add the leave item to the leave summary container
                            leaveSummaryContainer.appendChild(leaveItem);
                        });

                        // Add the leave summary container to the employee card
                        employeeCard.appendChild(leaveSummaryContainer);

                        // Add the employee card to the summary container
                        summaryContainer.appendChild(employeeCard);
                    }
                })
                .catch(error => {
                    console.error('Error fetching employee leave summary:', error);
                });
        }


        displayMyTeamSummary();
    });



    // Modal structure to be added to your HTML:
    const modalHtml = `
        <div class="modal fade" id="leaveDetailsModal" tabindex="-1" aria-labelledby="leaveDetailsModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="leaveDetailsModalLabel">Leave Details</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body" id="leaveDetailsModalBody">
                        <!-- Leave details will be inserted here -->
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.getElementById('applyLeaveForm').addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent form submission

        const fromDateInput = document.getElementById('fromDate');
        const toDateInput = document.getElementById('toDate');
        const fromDate = new Date(fromDateInput.value);
        const toDate = new Date(toDateInput.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Normalize time to compare dates

        let errorMessage = '';

        // Function to check if a date is a weekend (Saturday or Sunday)
        function isWeekend(date) {
            const day = date.getDay(); // 0 for Sunday, 6 for Saturday
            return day === 0 || day === 6;
        }

        // Check if the dates are in the past
        if (fromDate < today) {
            errorMessage += 'From Date must be today or later.\n';
        }
        if (toDate < today) {
            errorMessage += 'To Date must be today or later.\n';
        }
        // Check if From Date is after To Date
        if (fromDate > toDate) {
            errorMessage += 'From Date cannot be later than To Date.\n';
        }
        // Check if From Date or To Date falls on a weekend
        if (isWeekend(fromDate)) {
            errorMessage += 'From date is not allowed in Weekends\n';
        }
        if (isWeekend(toDate)) {
            errorMessage += ' To date is not allowed in Weekends\n';
        }

        if (errorMessage) {
            alert(errorMessage); // Show error message
            return; // Stop form submission
        } else {

            // Form is valid, proceed with submission
            console.log('Form is valid. Submitting leave application...');


            const leaveTypeId = document.getElementById('leaveTypeSelect').value;
            const leaveReason = document.getElementById('leaveReason').value;
            const fromDate = document.getElementById('fromDate').value;
            const toDate = document.getElementById('toDate').value;
            const dateOfApplication = new Date().toISOString().split('T')[0]; // Current date
            const leaveRequestStatus = 'PENDING';

            const selectedOption = document.querySelector(`#leaveTypeSelect option[value="${leaveTypeId}"]`);
            const leaveTypeName = selectedOption.textContent.split(' (')[0]; // Extract leave type name
            const pendingLeaves = selectedOption.textContent.match(/Available : (\d+) days/)[1];

            const leaveRequest = {
                leaveTypeId,
                leaveReason,
                fromDate,
                toDate,
                dateOfApplication,
                leaveRequestStatus
            };

            fetch('http://localhost:8080/LeaveManagementApp/employee/leave', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(leaveRequest)
            })
                .then(async response => {
                    if (!response.ok) {
                        const leaveModal = bootstrap.Modal.getInstance(document.getElementById('applyLeaveModal'));
                        leaveModal.hide();
                        return response.json().then(errorData => {
                            // Show error modal with specific message and title
                            showErrorModal(
                                `Please select a date range less than or equal to ${pendingLeaves} days for ${leaveTypeName}`,
                                'Leave Limit Exceeded'
                            );
                            throw new Error(errorData.message);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Success:', data);
                    // Close the modal after successful submission
                    const leaveModal = bootstrap.Modal.getInstance(document.getElementById('applyLeaveModal'));
                    leaveModal.hide();
                    openRequestsTab();
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        }
    });

    function openRequestsTab() {
        const requestTab = document.getElementById('request-id');
        if (requestTab) {
            requestTab.click();
        }
    }


    function showErrorModal(message, title = 'Error') {
        const errorMessageElement = document.getElementById('errorMessage');
        const modalTitleElement = document.getElementById('errorModalLabel');

        if (!errorMessageElement || !modalTitleElement) {
            console.error('Error: Required elements not found.');
            return;
        }

        errorMessageElement.textContent = message;
        modalTitleElement.textContent = title;

        const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
        errorModal.show();
    }

    // Append modal to the body
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    // My Leaves Details
    document.getElementById('my-leaves').addEventListener('click', function () {
        const tableContainer = document.getElementById('upcoming-holidays');
        tableContainer.innerHTML = '';
        const tableBody = document.getElementById('upcoming-leaves-container');
        tableBody.innerHTML = '';
        const upcomingTeamLeaves = document.getElementById('my-team-upcoming-leaves-container');
        upcomingTeamLeaves.innerHTML = '';
        const employeeDetailsDiv = document.getElementById('employee-details');
        const container = document.getElementById('leave-summary-cards-ml');
        container.innerHTML = '';
        employeeDetailsDiv.innerHTML = ''; // Clear previous content
        container_lsc.innerHTML = '';
        // Create tabs
        const tabContainer = document.createElement('div');
        tabContainer.classList.add('nav', 'nav-tabs', 'mb-3');

        const leavesTab = document.createElement('button');
        leavesTab.classList.add('nav-link', 'active');
        leavesTab.textContent = 'Leaves';
        leavesTab.addEventListener('click', function () {
            fetchLeaveRequests('APPROVED', 'Leaves'); // Fetch only approved leaves
            setActiveTab(leavesTab);
        });

        const requestTab = document.createElement('button');
        requestTab.classList.add('nav-link');
        requestTab.textContent = 'Requests';
        requestTab.id = 'request-id';
        requestTab.addEventListener('click', function () {
            container.innerHTML = '';
            fetchLeaveRequests('REJECTED,CANCELLED,PENDING', 'Request'); // Fetch leave requests with selected statuses
            setActiveTab(requestTab);
        });

        tabContainer.appendChild(leavesTab);
        tabContainer.appendChild(requestTab);
        container.appendChild(tabContainer);

        // Initial fetch for the "Leaves" tab
        fetchLeaveRequests('APPROVED', 'Leaves');

        function setActiveTab(activeTab) {
            [leavesTab, requestTab].forEach(tab => tab.classList.remove('active'));
            activeTab.classList.add('active');
        }

        function fetchLeaveRequests(status, tabName) {
            fetch(`http://localhost:8080/LeaveManagementApp/employee/leave?status=${status}`)
                .then(response => response.json())
                .then(data => {
                    container.innerHTML = ''; // Clear previous content
                    container.appendChild(tabContainer); // Re-add the tabs

                    // Create header and apply button
                    const headerContainer = document.createElement('div');
                    headerContainer.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-3');

                    const title = document.createElement('h3');
                    title.textContent = tabName;
                    title.classList.add('text-center', 'w-100', 'font-weight-bold');
                    headerContainer.appendChild(title);

                    const applyButton = document.createElement('button');
                    applyButton.textContent = 'Apply for Leave';
                    applyButton.classList.add('btn', 'btn-primary', 'apply-btn');
                    applyButton.addEventListener('click', function () {
                        // Fetch user information
                        fetch('http://localhost:8080/LeaveManagementApp/login')
                            .then(response => response.json())
                            .then(user => {
                                if (user.role === 'ADMIN') {
                                    alert("You're an admin, you don't need any leave.");
                                } else {
                                    fetch(`http://localhost:8080/LeaveManagementApp/employee/leave/summary`)
                                        .then(response => response.json())
                                        .then(leaveSummaries => {
                                            const leaveTypeSelect = document.getElementById('leaveTypeSelect');
                                            leaveTypeSelect.innerHTML = ''; // Clear previous options
                                            leaveSummaries.forEach(summary => {
                                                const option = document.createElement('option');
                                                option.value = summary.leaveTypeId;
                                                option.textContent = `${summary.leaveType} (Available : ${summary.pendingLeaves} days)`;
                                                leaveTypeSelect.appendChild(option);
                                            });
                                            const applyLeaveModal = new bootstrap.Modal(document.getElementById('applyLeaveModal'));
                                            applyLeaveModal.show();
                                        })
                                        .catch(error => console.error('Error fetching leave types:', error));
                                }
                            })
                            .catch(error => console.error('Error fetching user information:', error));
                    });
                    headerContainer.appendChild(applyButton);

                    if (tabName === 'Request') {
                        // Create the status filter dropdown
                        const statusFilter = document.createElement('select');
                        statusFilter.classList.add('form-select', 'w-auto', 'mr-2');
                        const statuses = [
                            { value: 'PENDING,REJECTED,CANCELLED', text: 'All Requests' },
                            { value: 'PENDING', text: 'Pending' },
                            { value: 'REJECTED', text: 'Rejected' },
                            { value: 'CANCELLED', text: 'Cancelled' }
                        ];
                        statuses.forEach(statusOption => {
                            const option = document.createElement('option');
                            option.value = statusOption.value;
                            option.textContent = statusOption.text;
                            statusFilter.appendChild(option);
                        });

                        statusFilter.value = status; // Set the selected value to current status
                        statusFilter.addEventListener('change', function () {
                            fetchLeaveRequests(this.value, 'Request'); // Fetch data based on selected status
                        });

                        headerContainer.appendChild(statusFilter);
                    }

                    container.appendChild(headerContainer);

                    // Create the table
                    const table = document.createElement('table');
                    table.classList.add('table', 'table-striped', 'my-leaves-table');

                    const thead = document.createElement('thead');
                    const headerRow = document.createElement('tr');
                    const headers = ['Leave Type', 'FROM', 'TO', 'Applied On', 'Total Days', 'Status', 'Actions'];
                    headers.forEach(headerText => {
                        const th = document.createElement('th');
                        th.textContent = headerText;
                        headerRow.appendChild(th);
                    });
                    thead.appendChild(headerRow);
                    table.appendChild(thead);

                    const tbody = document.createElement('tbody');
                    data.forEach(leaveRequest => {
                        const row = document.createElement('tr');

                        const leaveTypeCell = document.createElement('td');
                        leaveTypeCell.textContent = leaveRequest.leaveType;
                        row.appendChild(leaveTypeCell);

                        const fromDateCell = document.createElement('td');
                        fromDateCell.textContent = new Date(leaveRequest.fromDate).toLocaleDateString();
                        row.appendChild(fromDateCell);

                        const toDateCell = document.createElement('td');
                        toDateCell.textContent = new Date(leaveRequest.toDate).toLocaleDateString();
                        row.appendChild(toDateCell);

                        const dateOfApplicationCell = document.createElement('td');
                        dateOfApplicationCell.textContent = leaveRequest.dateOfApplication;
                        row.appendChild(dateOfApplicationCell);

                        const totalDaysCell = document.createElement('td');
                        totalDaysCell.textContent = leaveRequest.totalDays;
                        row.appendChild(totalDaysCell);

                        const statusCell = document.createElement('td');
                        statusCell.classList.add('status-cell', leaveRequest.leaveRequestStatus.toLowerCase());
                        statusCell.textContent = leaveRequest.leaveRequestStatus;
                        row.appendChild(statusCell);

                        const actionsCell = document.createElement('td');
                        if (leaveRequest.leaveRequestStatus === 'PENDING') {

                            const cancelButton = document.createElement('button');
                            cancelButton.textContent = 'Cancel';
                            cancelButton.classList.add('btn', 'btn-danger', 'mr-2', 'my-leaves-cancel-btn');
                            cancelButton.addEventListener('click', function () {
                                if (leaveRequest.leaveRequestId) {
                                    fetch(`http://localhost:8080/LeaveManagementApp/employee/leave?leaveRequestId=${leaveRequest.leaveRequestId}`, {
                                        method: 'POST',
                                        headers: {
                                            'Content-Type': 'application/json'
                                        },
                                        body: JSON.stringify({ leaveRequestId: leaveRequest.leaveRequestId })
                                    })
                                        .then(response => response.json())
                                        .then(result => {
                                            if (result) {
                                                alert('Leave request canceled successfully.');
                                                fetchLeaveRequests('PENDING,REJECTED,CANCELLED', 'Request'); // Refresh the list
                                            } else {
                                                alert('Failed to cancel leave request.');
                                            }
                                        })
                                        .catch(error => {
                                            console.error('Error:', error);
                                            alert('An error occurred while canceling the leave request.');
                                        });
                                } else {
                                    console.error('Error: leaveRequestId is undefined.');
                                    alert('Unable to cancel leave request. Leave request ID is missing.');
                                }
                            });
                            actionsCell.appendChild(cancelButton);
                        }

                        const detailsButton = document.createElement('button');
                        detailsButton.textContent = 'Details';
                        detailsButton.classList.add('btn', 'btn-info');
                        detailsButton.addEventListener('click', () => {
                            const modalTitle = document.getElementById('leaveDetailsModalLabel');
                            const modalBody = document.getElementById('leaveDetailsModalBody');

                            modalTitle.textContent = `My Leave Details`;
                            modalBody.innerHTML = `
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Type:</strong>
                                   ${leaveRequest.leaveType}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Reason:</strong>
                                   ${leaveRequest.leaveReason}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">From:</strong>
                                   ${new Date(leaveRequest.fromDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">To:</strong>
                                   ${new Date(leaveRequest.toDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Total Days:</strong>
                                   ${leaveRequest.totalDays}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Date of Application:</strong>
                                   ${leaveRequest.dateOfApplication}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Status:</strong>
                                   ${leaveRequest.leaveRequestStatus}
                               </p>
                           `;

                            const modal = new bootstrap.Modal(document.getElementById('leaveDetailsModal'));
                            modal.show();
                        });
                        actionsCell.appendChild(detailsButton);

                        row.appendChild(actionsCell);

                        tbody.appendChild(row);
                    });

                    table.appendChild(tbody);
                    container.appendChild(table);
                })
                .catch(error => {
                    console.error('Error fetching leave requests:', error);
                });
        }
    });

});
document.addEventListener('DOMContentLoaded', () => {
    const fromDateInput = document.getElementById('fromDate');
    const toDateInput = document.getElementById('toDate');
    const totalDaysInput = document.getElementById('totalDays');

    function calculateTotalDays() {
        const fromDate = new Date(fromDateInput.value);
        const toDate = new Date(toDateInput.value);
        let totalDays = 0;

        if (fromDate && toDate && fromDate <= toDate) {
            for (let date = new Date(fromDate); date <= toDate; date.setDate(date.getDate() + 1)) {
                if (date.getDay() !== 0 && date.getDay() !== 6) { // Exclude Sundays (0) and Saturdays (6)
                    totalDays++;
                }
            }
        }

        totalDaysInput.value = totalDays > 0 ? totalDays : '';
    }

    fromDateInput.addEventListener('change', calculateTotalDays);
    toDateInput.addEventListener('change', calculateTotalDays);
});
