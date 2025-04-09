# Import base user schemas
from .User import (
    UserBase,
    UserCreate,
    UserUpdate,
    UserInDB,
    UserResponse
)

# Import patient schemas
from .Patient import (
    PatientBase,
    PatientCreate,
    PatientUpdate,
    PatientInDB,
    PatientResponse
)

# Import doctor schemas
from .Doctor import (
    DoctorBase,
    DoctorCreate,
    DoctorUpdate,
    DoctorInDB,
    DoctorResponse
)

# Import admin schemas
from schemas.DoctorSchedule import (
    DoctorScheduleBase,
    DoctorScheduleCreate,
    DoctorScheduleUpdate,
    DoctorSchedule
)
from .Admin import (
    AdminBase,
    AdminCreate,
    AdminUpdate,
    AdminInDB,
    AdminResponse
)

# Import appointment schemas
from .Appointment import (
    AppointmentBase,
    AppointmentCreate,
    AppointmentUpdate,
    AppointmentInDB,
    AppointmentResponse
)

# Import department schemas
from .Department import (
    DepartmentBase,
    DepartmentCreate,
    DepartmentUpdate,
    DepartmentInDB,
    DepartmentResponse
)

# Import medical history schemas
from .MedicalHistory import (
    MedicalHistoryBase,
    MedicalHistoryCreate,
    MedicalHistoryUpdate,
    MedicalHistoryInDB,
    MedicalHistoryResponse
)

# Import notification schemas
from .Notification import (
    NotificationBase,
    NotificationCreate,
    NotificationUpdate,
    NotificationInDB,
    NotificationResponse
)

# Import patient health metrics schemas
from .PatientHealthMetrics import (
    PatientHealthMetricsCreate,
    PatientHealthMetricsResponse
)

# Import token schemas
from .Token import Token, TokenData

# Import login schema
from .Login import Login