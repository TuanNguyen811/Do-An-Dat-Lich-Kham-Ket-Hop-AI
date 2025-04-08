-- Bảng Users (Base table for all user types)
CREATE TABLE IF NOT EXISTS Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Doctor', 'Patient') NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    address TEXT,
    avatar_url VARCHAR(255)
);

-- Bảng Admins (Admin-specific attributes)
CREATE TABLE IF NOT EXISTS Admins (
    admin_id INT PRIMARY KEY,
    -- Admin-specific fields can be added here if needed
    FOREIGN KEY (admin_id) REFERENCES Users(user_id)
);

-- Bảng Patients (Patient-specific attributes)
CREATE TABLE IF NOT EXISTS Patients (
    patient_id INT PRIMARY KEY,
    insurance_id VARCHAR(50),
    -- Patient-specific fields can be added here
    FOREIGN KEY (patient_id) REFERENCES Users(user_id)
);

-- Bảng Departments (Thông tin khoa/phòng ban)
CREATE TABLE IF NOT EXISTS Departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    avatar_url VARCHAR(255) -- Đường dẫn ảnh đại diện
);

-- Bảng Doctors (Doctor-specific attributes)
CREATE TABLE IF NOT EXISTS Doctors (
    doctor_id INT PRIMARY KEY,
    department_id INT NOT NULL,
    description TEXT,
    FOREIGN KEY (doctor_id) REFERENCES Users(user_id),
    FOREIGN KEY (department_id) REFERENCES Departments(department_id)
);

-- Bảng DoctorSchedules (Lịch làm việc của bác sĩ)
CREATE TABLE IF NOT EXISTS DoctorSchedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    week INT NOT NULL,
    Monday VARCHAR(20),
    Tuesday VARCHAR(20),
    Wednesday VARCHAR(20),
    Thursday VARCHAR(20),
    Friday VARCHAR(20),
    Saturday VARCHAR(20),
    Sunday VARCHAR(20),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id)
);

-- Bảng Appointments (Lịch khám)
CREATE TABLE IF NOT EXISTS Appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    department_id INT NOT NULL,
    appointment_date DATETIME NOT NULL,
    shift ENUM('Shift 1', 'Shift 2', 'Shift 3', 'Shift 4') NOT NULL,
    description TEXT,
    status ENUM('Scheduled', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id),
    FOREIGN KEY (department_id) REFERENCES Departments(department_id)
);

-- Bảng PatientHealthMetrics (Chỉ số sức khỏe bệnh nhân)
CREATE TABLE IF NOT EXISTS PatientHealthMetrics (
    metric_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    heart_rate INT,
    blood_pressure_systolic INT,
    blood_pressure_diastolic INT,
    blood_sugar_level FLOAT,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id)
);

-- Bảng MedicalHistory (Lịch sử khám bệnh)
CREATE TABLE IF NOT EXISTS MedicalHistory (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    department_id INT NOT NULL,
    visit_date DATETIME NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id),
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id),
    FOREIGN KEY (department_id) REFERENCES Departments(department_id)
);

-- Bảng Notifications (Thông báo nhắc nhở)
CREATE TABLE IF NOT EXISTS Notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type ENUM('Appointment', 'Medication') NOT NULL,
    message TEXT NOT NULL,
    scheduled_time DATETIME NOT NULL,
    status ENUM('Pending', 'Sent', 'Failed') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);