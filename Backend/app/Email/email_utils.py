import yagmail
import random
from dotenv import load_dotenv
from Email.query_email import get_appointment_using_email_dict
from Email.template_email import get_subject_otp, get_content_template_otp, get_subject_appointment, \
    get_content_appointment
from sqlalchemy.orm import Session

import os
load_dotenv(os.path.join(os.path.dirname(__file__), "..", "config", "config.env"))

import logging
#Cau hinh log
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)
# Thong tin gmail cua ban
email = os.getenv("GMAIL")
app_password = os.getenv("PASSWORD")

# Khoi tao yagmail
yag = yagmail.SMTP(user=email, password=app_password)


#Tao ma OTP ngau nhien
def generate_otp(length=6):
    otp = ""
    for _ in range(length):
        digit = random.randint(0, 9)
        otp += str(digit)
    return otp


def send_otp_utils(receiver_email, otp_code):
    subject = "Mã OTP xác thực của bạn"
    content = f"""
        Chào bạn,

        Mã OTP của bạn là: {otp_code}

        Vui lòng không chia sẻ mã này với bất kỳ ai.

        Trân trọng,
        Hệ thống xác thực
        """

    yag.send(to=receiver_email, subject=subject, contents=content)
    logger.info(f"Email đã gửi thành công đến: {receiver_email}")

#Chức năng tổng quát
def send_email_utils(receiver_email, content_template_html, subject):
    yag.send(to=receiver_email, subject=subject, contents=[content_template_html])

    logger.info(f"Email đã gửi thành công đến: {receiver_email}")
    logger.info(f"Subject: {subject}")

#Gửi OTP với email
def send_otp_email(receiver_email, otp_code, full_name):
    subject_otp = get_subject_otp()
    content_otp = get_content_template_otp(otp_code=otp_code, full_name=full_name)
    send_email_utils(receiver_email=receiver_email,
                     content_template_html=content_otp,
                     subject=subject_otp)

#Gửi thông tin đặt lịch thành công
def send_appointment_email(db: Session, appointment_id):

    appointment_data = get_appointment_using_email_dict(db=db, appointment_id=appointment_id)


    full_name_patient = appointment_data.get("patient_name")
    receiver_email = appointment_data.get("patient_email")
    appointment_date = appointment_data.get("appointment_date")
    shift = appointment_data.get("shift")
    doctor_name = appointment_data.get("doctor_name")
    department_name = appointment_data.get("department_name")
    status = appointment_data.get("status")
    description = appointment_data.get("description")

    #Chuyển đổi ca làm việc
    shift_map = {
        "Shift 1": "7h - 9h",
        "Shift 2": "9h - 12h",
        "Shift 3": "13h - 15h",
        "Shift 4": "15h - 17h"
    }
    shift = shift_map.get(shift, "Không rõ")

    #Chuyển đổi trạng thái lịch hẹn
    status_map = {
        "Scheduled": "ĐÃ ĐẶT",
        "Completed": "HOÀN THÀNH",
        "Cancelled": "ĐÃ HỦY"
    }
    status = status_map.get(status, "Không rõ")

    description = appointment_data.get("description") or "Không có" #tương đương if

    content_appointment = get_content_appointment(appointment_date= appointment_date,
                                                  doctor_name= doctor_name,
                                                  department_name= department_name,
                                                  description= description,
                                                  status= status,
                                                  shift= shift,
                                                  full_name=full_name_patient)

    subject_appointment = get_subject_appointment(status)

    send_email_utils(receiver_email=receiver_email,
                     subject=subject_appointment,
                     content_template_html=content_appointment)