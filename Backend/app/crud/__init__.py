# Import user crud operations
from .crud_user import (
    get_user_by_email,
    get_user,
    get_users,
    create_user,
    update_user,
    delete_user
)

# Import doctor crud operations
from .crud_doctor import (
    get_doctor,
    get_doctors,
    create_doctor,
    update_doctor,
    delete_doctor
)

# Import patient crud operations
from .crud_patient import (
    get_patient,
    get_patients, 
    create_patient,
    update_patient,
    delete_patient
)

# Import admin crud operations
from .crud_admin import (
    get_admin,
    get_admins,
    create_admin,
    update_admin,
    delete_admin,
)

from .crud_DoctorSchedule import (
    delete_doctor_schedule,
    get_doctor_schedule,
    get_doctor_schedules,
    create_doctor_schedule,
    update_doctor_schedule
)
# Import department crud operations
from .crud_department import (
    get_department_by_name,
    get_department,
    get_departments,
    create_department,
    update_department,
    delete_department
)

# Import appointment crud operations
from .crud_appointment import (
    get_appointment,
    get_appointments,
    create_appointment,
    update_appointment,
    delete_appointment
)

# Import medical history crud operations
from .crud_MedicalHistory import (
    get_medical_history,
    get_medical_histories,
    create_medical_history,
    update_medical_history,
    delete_medical_history
)

# Import notification crud operations
from .crud_Notification import (
    get_notification,
    get_notifications,
    create_notification,
    update_notification,
    delete_notification
)

# Import patient health metrics crud operations
from .crud_health_metrics import (
    create_or_update_health_metric,
    get_health_metric_by_patient
)

# Authentication functions
from .crud_user import authenticate_user